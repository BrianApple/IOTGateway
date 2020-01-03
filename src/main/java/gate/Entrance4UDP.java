package gate;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import gate.base.cache.ClientChannelCache;
import gate.base.cache.ProtocalStrategyCache;
import gate.base.chachequeue.CacheQueue;
import gate.base.config.Config;
import gate.kafka.KK2Tmnl;
import gate.server.Server4Terminal;
import gate.threadWorkers.InnerQueue2Tmnl;
import gate.threadWorkers.Tmnl2MQTT;
import gate.util.CommonUtil;
/**
 * 网关启动入口
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月12日
 */
public class Entrance4UDP {
	
	public static CommandLine commandLine = null;
	private static String[] protocolType;
	public static void main(String[] args) {
		
		suitCommonLine(args);
		System.setProperty("org.jboss.netty.epollBugWorkaround", "true");
		initEnvriment();
		for(int i = 0 ; i < protocolType.length ; i++){
			String pts =  protocolType[i];
			String pid = pts.split("\\,")[0];//pId
			
			new Thread(new Runnable() {
				public void run() {
					String[] pt = pts.split("\\,");
					boolean isBigEndian = "0".equals(pt[1]) ? false : true;
					boolean isDataLenthIncludeLenthFieldLenth = "0".equals(pt[5]) ? false : true;
					System.out.println(String.format("！！！网关开始提供规约类型为%s的终端连接服务，开启端口号为：%s", Integer.parseInt(pt[0]),Integer.parseInt(pt[7])));
					Server4Terminal server4Terminal = new Server4Terminal(pt[0],pt[7]);
					server4Terminal.bindAddress(server4Terminal.config(Integer.parseInt(pt[0]),isBigEndian,Integer.parseInt(pt[2]),
							Integer.parseInt(pt[3]),Integer.parseInt(pt[4]),isDataLenthIncludeLenthFieldLenth,Integer.parseInt(pt[6])));//1, false, -1, 1, 2, true, 1
					
				}
			},"gate2tmnlThread_pid_"+pid).start();
			ProtocalStrategyCache.protocalStrategyCache.put(pid, pts);
		}
		
		addHook();
		
	}
	
	
	/**
	 * 命令行
	 */
	public static boolean suitCommonLine(String[] args){
		
		commandLine =
				 CommonUtil.parseCmdLine("iotGateServer", args, CommonUtil.buildCommandlineOptions(new Options()),
                    new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
        }
		boolean isCluster = false;
        if (!commandLine.hasOption("f") || !commandLine.hasOption("n")) {
        	System.err.println("启动参数有误，请重新启动");
        	System.exit(-1);
        }
        String confFile = commandLine.getOptionValue("f");
        protocolType = getProtocolType(confFile);
        
        CommonUtil.gateNum = Integer.parseInt(commandLine.getOptionValue("n"));
        System.out.println(String.format("网关编号为：%s", CommonUtil.gateNum));
        return isCluster;
	}
	/**
	 * 环境初始化
	 */
	public static  void initEnvriment(){
		try {
			//下行内部队列使用byte[]数组
			new InnerQueue2Tmnl(CacheQueue.down2TmnlQueue, 2).start();
			//上行数据目的地
			if(Config.ENABLE_MQTT){
				new Tmnl2MQTT(CacheQueue.up2MasterQueue,6).start();
			}else{
				//启动kk消费者
				KK2Tmnl.init();
			}
			
		} catch (Exception e) {
			System.err.println("数据中转线程启动失败");
			e.printStackTrace();
			System.exit(-1);
		};
		
	}
	/**
	 * JVM的关闭钩子--JVM正常关闭才会执行
	 */
	public static void addHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				//清空缓存信息
				ClientChannelCache.clearAll();
				CacheQueue.clearIpCountRelationCache();
				CacheQueue.clearMasterChannelCache();
			}
		}));
	}

	
	@SuppressWarnings("resource")
	public static String[] getProtocolType(String filePath){
		File conf=  new File(filePath);
		System.setProperty("BasicDir",conf.getParent() );
		BufferedReader bufferedReader =null;
        try {
        	bufferedReader = new BufferedReader(new FileReader(conf));
        	String str;
        	while((str = bufferedReader.readLine()) != null){
        		if(str.startsWith("protocolType")){
        			return str.split("\\=")[1].split(";");
        		}
            }
        	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("配置文件加载失败");
        	System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return null;
        
	}
}
