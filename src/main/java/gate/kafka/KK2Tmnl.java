package gate.kafka;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import gate.base.cache.ClientChannelCache;
import gate.base.config.Config;
import gate.concurrent.ThreadFactoryImpl;
import gate.util.CommonUtil;
import gate.util.StringUtils;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

/**
 * 下行报文
 * @Description: 
 * @author  yangcheng
 * @date:   2019年8月15日
 */
public class KK2Tmnl {
	private static  ExecutorService exService = Executors.newFixedThreadPool(2,new ThreadFactoryImpl("kafka_sub_thread_", false));
	public static void  init(){
		exService.execute(new Runnable() {
			
			@Override
			public void run() {
				Properties props = new Properties();
		        props.put("bootstrap.servers",Config.KAFKA_BROKER_LIST);
		        props.put("group.id", "msgTransWorker_DN_"+CommonUtil.gateNum);
		        props.put("enable.auto.commit", "true");
		        props.put("auto.commit.interval.ms", "1000");
		        props.put("session.timeout.ms", "30000");
		        props.put("key.deserializer", StringDeserializer.class.getName());
		        props.put("value.deserializer", StringDeserializer.class.getName());
		        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		        consumer.subscribe(Arrays.asList(Config.KAFKA_SUBSCRIBER_TOPIC));
		        while(true){
		            ConsumerRecords<String,String> consumerRecords = consumer.poll(100);
		            for(ConsumerRecord<String,String> consumerRecord : consumerRecords){
//		                    System.out.println("在test-partition-1中读到：" + consumerRecord.value());
	                    String[] subscribleDataStr = new String(consumerRecord.value()).split("\\$");
						Channel channel = ClientChannelCache.get(subscribleDataStr[0]);
						if(channel != null){
							if(channel.isWritable()){
								String[] ipAddress = subscribleDataStr[0].split("\\|");
								DatagramPacket datagramPacket= new DatagramPacket(Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(subscribleDataStr[1])),
										new InetSocketAddress(ipAddress[0],Integer.parseInt(ipAddress[1])));
								channel.writeAndFlush(datagramPacket);
							}
						}
		            }
		        }
			}
		});
	}
	
    public static void main(String[] args) {
    	init();
    }
}
