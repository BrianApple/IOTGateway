package gate.threadWorkers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import gate.base.config.Config;
import gate.base.domain.ChannelData;
import gate.concurrent.ThreadFactoryImpl;
import gate.server.combination.Publisher;
import gate.server.combination.Subscriber;
import gate.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * 上行报文中转
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月13日
 */
public class Tmnl2MQTT implements DataTransfer{

	private BlockingQueue<ChannelData> up2MasterQueue;
	private final int poolSize;
	private ExecutorService exService;
	private Publisher publisher;
	public Tmnl2MQTT(BlockingQueue<ChannelData> up2MasterQueue ,int poolSize) {
		super();
		this.up2MasterQueue = up2MasterQueue;
		this.poolSize = poolSize;
		exService = Executors.newFixedThreadPool(poolSize,new ThreadFactoryImpl("msgTransWorker_UP_", false));
		new Subscriber(Config.MQTT_SUBSCRIBER_TOPIC,"msgTransWorker_DN_"+CommonUtil.gateNum);
	}



	public void run() {
			for (int i=0 ; i < poolSize ; i++ ){
				exService.execute(new Runnable() {
					@Override
					public void run() {
						try {
							publisher = new Publisher(Config.MQTT_PUBLISHER_TOPIC,Thread.currentThread().getName()+"_"+CommonUtil.gateNum);
						} catch (MqttSecurityException e1) {
							System.err.println("认证失败");
							e1.printStackTrace();
							System.exit(0);
						} catch (MqttException e1) {
							System.err.println("MQTT服务连接异常");
							e1.printStackTrace();
							System.exit(0);
						}
						ChannelData channelData = null;
						MqttMessage message = null;
						StringBuilder sb = null;
						while(true){
							
							try {
								channelData = up2MasterQueue.take();//获取从Server4Terminal发送过来的上行报文对象
								if(channelData == null){
									continue;
								}
								String ipAddress=channelData.getIpAddress();
						    	ByteBuf bufData = channelData.getSocketData().getByteBuf();
						    	sb = new StringBuilder();
						    	sb.append(ipAddress).append(CommonUtil.ipDataSplit).append(ByteBufUtil.hexDump(bufData));
//						    	byte[] destData = new byte[bufData.readableBytes()];
//						    	bufData.readBytes(destData);
						    	CommonUtil.releaseByteBuf(bufData);
								message = new MqttMessage();  
						    	message.setQos(1);  
						    	message.setRetained(true);  
						    	message.setPayload(sb.toString().getBytes());
						    	publisher.publish( publisher.getMqttTopic(), message);
								
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
