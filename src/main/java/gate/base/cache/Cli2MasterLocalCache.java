package gate.base.cache;

import gate.base.domain.LocalCache;
/**
 * 缓存网关与前置连接的会话
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月24日
 */
public class Cli2MasterLocalCache implements LocalCache{
	
	
	private Cli2MasterLocalCache(){
		if(inner.cli2MasterLocalCache != null){
			throw new IllegalStateException("禁止创建gate.base.cache.Cli2MasterLocalCache对象！");
		}
	}
	
	
	static class inner{
		static Cli2MasterLocalCache cli2MasterLocalCache = new Cli2MasterLocalCache();
		
	}
	

	
	public static Cli2MasterLocalCache getInstance(){
		return Cli2MasterLocalCache.inner.cli2MasterLocalCache;
	}



	@Override
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void set(Object key, Object value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean del(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	
	

}
