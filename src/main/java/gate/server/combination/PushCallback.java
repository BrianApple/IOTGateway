package gate.server.combination;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import gate.base.chachequeue.CacheQueue;
import gate.base.config.Config;
import gate.base.domain.ChannelData;
import gate.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * 发布/订阅消息的回调类 
 * @author yangcheng
 */
public class PushCallback implements MqttCallback{

	@Override
	public void connectionLost(Throwable cause) {
		String threadName = Thread.currentThread().getName();
		if(threadName.contains("msgTransWorker_UP")){
			while(true){
				try {
					rePublish();
					System.out.println(threadName+"重连");
					break;
				} catch (MqttException | InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(threadName.contains("msgTransWorker_DN")){
			while(true){
				try {
					new Subscriber(Config.MQTT_SUBSCRIBER_TOPIC,Thread.currentThread().getName());
					System.out.println(threadName+"重连");
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
//        System.out.println("接收消息主题 : " + topic);  
//        System.out.println(Thread.currentThread().getName()+"接收消息Qos : " + message.getQos());  
//        System.out.println("接收消息内容 : " + new String(message.getPayload()));
        CacheQueue.down2TmnlQueue.put(message.getPayload());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		//do nothing
	}
	
	/**
	 * 重连时  线程名称本身是加了gateNum的
	 * @throws MqttSecurityException
	 * @throws MqttException
	 * @throws InterruptedException
	 */
	public void rePublish() throws MqttSecurityException, MqttException, InterruptedException{
		Publisher publisher;
		publisher = new Publisher(Config.MQTT_PUBLISHER_TOPIC,Thread.currentThread().getName());
			
		ChannelData channelData = null;
		MqttMessage message = null;
		StringBuilder sb = null;
		while(true){
			channelData = CacheQueue.up2MasterQueue.take();
			if(channelData == null){
				continue;
			}
			String ipAddress=channelData.getIpAddress();
	    	ByteBuf bufData = channelData.getSocketData().getByteBuf();
	    	sb = new StringBuilder();
	    	sb.append(ipAddress).append(CommonUtil.ipDataSplit).append(ByteBufUtil.hexDump(bufData));
	    	CommonUtil.releaseByteBuf(bufData);
			message = new MqttMessage();  
	    	message.setQos(1); 
	    	message.setRetained(true);  
	    	message.setPayload(sb.toString().getBytes());
	    	publisher.publish( publisher.getMqttTopic(), message);
				
		}
	}

}
