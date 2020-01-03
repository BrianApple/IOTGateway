package gate.threadWorkers;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gate.base.cache.ClientChannelCache;
import gate.base.config.Config;
import gate.concurrent.ThreadFactoryImpl;
import gate.server.combination.Subscriber;
import gate.util.CommonUtil;
import gate.util.StringUtils;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

/**
 * 下行报文中转
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月30日
 */
public class InnerQueue2Tmnl implements DataTransfer{

	private BlockingQueue<Object> down2TmnlQueue;
	private final int poolSize;
	private ExecutorService exService;
	public InnerQueue2Tmnl(BlockingQueue<Object> down2TmnlQueue ,int poolSize) {
		super();
		this.down2TmnlQueue = down2TmnlQueue;
		this.poolSize = poolSize;
		exService = Executors.newFixedThreadPool(poolSize,new ThreadFactoryImpl("msgTransWorker_DNMSG_", false));
	}
	



	public void run() {
		
			
			for (int i=0 ; i < poolSize ; i++ ){
				exService.execute(new Runnable() {
					
					@Override
					public void run() {
//						new Subscriber(Config.MQTT_SUBSCRIBER_TOPIC,Thread.currentThread().getName()+"_"+CommonUtil.gateNum);
						while(true){
							byte[] subscribleData = null;
							try {
								subscribleData = (byte[]) down2TmnlQueue.take();//获取从Server4Terminal发送过来的上行报文对象
								if(subscribleData == null){
									continue;
								}
								String[] subscribleDataStr = new String(subscribleData).split("\\$");
								Channel channel = ClientChannelCache.get(subscribleDataStr[0]);
								if(channel != null){
									if(channel.isWritable()){
										String[] ipAddress = subscribleDataStr[0].split("\\|");
										DatagramPacket datagramPacket= new DatagramPacket(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(subscribleDataStr[1])),
												new InetSocketAddress(ipAddress[0],Integer.parseInt(ipAddress[1])));
										channel.writeAndFlush(datagramPacket);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		
	}



	@Override
	public void start() throws Exception {
		new Thread(this).start();
	}

}
