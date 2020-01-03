package gate.server.combination;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import gate.base.config.Config;
import test.CountHelper;

/**
 * 消息发布  
 * @author yangcheng
 */
public class Publisher {
    private MqttClient client;
    private MqttTopic mqttTopic;
    
    private MqttMessage message;

    /**
     * 
     * @param topic 订阅消息的主题
     * @throws MqttSecurityException
     * @throws MqttException
     */
	public Publisher(String topic,String MQTT_PUBLISHER_CLIENTID) throws MqttSecurityException, MqttException {
		super();
		client = new MqttClient(Config.MQTT_HOST, MQTT_PUBLISHER_CLIENTID, new MemoryPersistence());
		connect(topic);
	}
	
	/**
	 * 连接远程MQTT服务
	 * @throws MqttException 
	 * @throws MqttSecurityException 
	 */
	private void connect(String topic) throws MqttSecurityException, MqttException{
		MqttConnectOptions  options = new MqttConnectOptions();
		
		options.setMaxInflight(50000);
		options.setCleanSession(true);
        options.setUserName(Config.MQTT_USERNAME);
        options.setPassword(Config.MQTT_PASSWORD.toCharArray());  
        options.setConnectionTimeout(10);  
        options.setKeepAliveInterval(30);  
        client.setCallback(new PushCallback());
        client.connect(options);  

        mqttTopic = client.getTopic(topic);
	}
    
	/**
	 * 发布消息到MQTT服务器
	 * @param topic
	 * @param message
	 * @throws MqttException 
	 * @throws MqttPersistenceException 
	 */
	public void publish(MqttTopic topic , MqttMessage message) throws MqttPersistenceException, MqttException{
		MqttDeliveryToken token = topic.publish(message);  
		if(CountHelper.startTimeLong.get() == 0){
			synchronized (CountHelper.class) {
				if(CountHelper.startTimeLong.get() == 0){
					CountHelper.startTimeLong.set(System.currentTimeMillis());
				}
			}
		}

	}
	
    public MqttTopic getMqttTopic() {
		return mqttTopic;
	}

	/** 
     *  启动入口 
     * @param args 
     * @throws MqttException 
     */  
    public static void main(String[] args) throws MqttException {  
    	Publisher publisher = new Publisher("TEST/TOPIC","clientId-001");  

    	publisher.message = new MqttMessage();
    	publisher.message.setQos(1);  
    	publisher.message.setRetained(true);
    	publisher.message.setPayload("这是推送消息的内容".getBytes());
    	publisher.publish(publisher.mqttTopic , publisher.message);
    	
    	
        System.out.println(publisher.message.isRetained() + "------ratained状态");  
    }
    

}
