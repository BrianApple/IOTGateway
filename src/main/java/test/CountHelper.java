package test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/**
 * 测试使用的一些计数的变量--要合理考虑并发
 * 
 * @author BriansPC
 *
 */
public class CountHelper {
	public static int ThreadNum = 1 ;
	
	public static AtomicLong startTimeLong = new AtomicLong();
	public static AtomicLong endTimeLong = new AtomicLong();
	public static AtomicInteger clientRecieveCount ;
	public static AtomicInteger masterRecieveCount ;
	
	public static Long masterRecieveStartTime;
	
	static{
		clientRecieveCount = new AtomicInteger(0);
		masterRecieveCount = new AtomicInteger(0);
	}

	
	
}
