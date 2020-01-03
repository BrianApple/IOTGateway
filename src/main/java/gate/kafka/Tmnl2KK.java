package gate.kafka;


import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import gate.base.config.Config;
import test.CountHelper;
/**
 * 上行报文
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月15日
 */
public class Tmnl2KK{

    private static KafkaProducer<String,String> producer = null;

    /**
     * 初始化生产者
     */
    static {
        Properties configs = initConfig();
        producer = new KafkaProducer<String, String>(configs);
    }

    /**
     * 初始化配置
     * @return
     */
    private static Properties initConfig(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,Config.KAFKA_BROKER_LIST);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        return properties;
    }
    
	public static void sendMsgAsynch(String msg) {
		ProducerRecord<String , String> record = null;
        record = new ProducerRecord<String, String>(Config.KAFKA_PUBLISHER_TOPIC, msg);
        //异步发送消息
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e){
                	e.printStackTrace();
                }else {
//                    System.out.println(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));
                }
            }
        });
        /**
         * 测试统计
         */
        if(CountHelper.startTimeLong.get() == 0){
			synchronized (CountHelper.class) {
				if(CountHelper.startTimeLong.get() == 0){
					CountHelper.startTimeLong.set(System.currentTimeMillis());
				}
			}
		}
	}
	
	public static void closeKafkaProductor(){
		producer.close();
	}

    public static void main(String[] args) throws InterruptedException {
        //消息实体
        ProducerRecord<String , String> record = null;
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            record = new ProducerRecord<String, String>(Config.KAFKA_PUBLISHER_TOPIC, "value"+(int)(10*(Math.random())));
            //异步发送消息
//            producer.send(record, new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//                    if (null != e){
//                    	e.printStackTrace();
//                    }else {
//                        System.out.println(String.format("offset:%s,partition:%s",recordMetadata.offset(),recordMetadata.partition()));
//                    }
//                }
//            });
            producer.send(record);
        }
        System.out.println("耗时："+(System.currentTimeMillis()-time));
        producer.close();
    }


}
