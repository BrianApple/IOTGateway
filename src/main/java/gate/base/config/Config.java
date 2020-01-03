package gate.base.config;
/**
 * 基本配置文件
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月24日
 */
public class Config {
	
	/**
	 * true  数据往mqtt存放;false  数据往kafka存放
	 */
	public static final boolean ENABLE_MQTT = false;
	/**
	 * MQTT相关配置信息
	 */
	public static final String MQTT_PUBLISHER_TOPIC = "TOPIC/all";
	public static final String MQTT_SUBSCRIBER_TOPIC = "TOPIC/all";
	public static final String MQTT_HOST = "tcp://XX.XX.XX.XX:1883";
	public static final String MQTT_USERNAME = "XXXX";
	public static final String MQTT_PASSWORD = "XXXX";
	
	/**
	 * 本地测试环境
	 */
//	public static final String MQTT_HOST = "tcp://127.0.0.1:8885";
//	public static final String MQTT_USERNAME = "testOne";
//	public static final String MQTT_PASSWORD = "6156ADE136B3BF385B595CF3A69BFAFF2D6AFE87FF3EE1B49224F51D0259B00B014BA7771064E9F46CBF67F27780AABCCC1C252142397FEE8316A91CB0C52176";
	
	/**
	 * kafka相关配置信息
	 */
	public final static String KAFKA_PUBLISHER_TOPIC = "testTopic";
	public final static String KAFKA_SUBSCRIBER_TOPIC = "testTopic";
	public final static String KAFKA_BROKER_LIST = "192.168.18.27:9092";//测试环境
	
}
