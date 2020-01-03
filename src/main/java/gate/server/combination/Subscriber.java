package gate.server.combination;

import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import gate.base.config.Config;

/**
 * 消息订阅
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月24日
 */
public class Subscriber {
    private MqttClient client;  
    private MqttConnectOptions options;
    @SuppressWarnings("unused")
    private ScheduledExecutorService scheduler; 
    public Subscriber(String topic,String subscriber_clientid){
    	subscriber(topic,subscriber_clientid );
    }
    
    private void subscriber(String topic,String subscriber_clientid) {
        try {
            client = new MqttClient(Config.MQTT_HOST, subscriber_clientid, new MemoryPersistence());  
            options = new MqttConnectOptions();  
            options.setCleanSession(true);  
            options.setUserName(Config.MQTT_USERNAME);  
            options.setPassword(Config.MQTT_PASSWORD.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);
            client.setCallback(new PushCallback());  
            client.connect(options);  
            int[] Qos  = {1};  
            String[] topics = {topic};  
            client.subscribe(topics, Qos);  
        } catch (Exception e) {
        	System.out.println("---------------------");
            e.printStackTrace();  
        }  
    }  
    
	
	
    public static void main(String[] args) throws MqttException {  
    	Subscriber subscriber = new Subscriber(Config.MQTT_SUBSCRIBER_TOPIC,"testId");
    	
    }  
    

}
