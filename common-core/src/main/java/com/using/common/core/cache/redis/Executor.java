package com.using.common.core.cache.redis;

import java.util.Collection;
import java.util.Iterator;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 执行器
 * 它保证在执行操作之后释放数据源
 * @author fengjc
 * @param <T>
 */
public abstract class Executor<T> {

	private ShardedJedisPool jedisPool;
	
	public ShardedJedis jedis;
	
	public Executor(ShardedJedisPool jedisPool,boolean on,String db) {
		this.jedisPool = jedisPool;
		this.jedis = (on ? this.jedisPool.getResource() : null);
		
		Integer dbIndex = Integer.valueOf(db);
		//设置jedis 可选db , 需要设置indexDb时 放开代码，indexDb从properties文件中提取
		if(on && dbIndex > -1) {
			Collection<Jedis> c = jedis.getAllShards();
			Iterator<Jedis> jds = c.iterator();
			while (jds.hasNext()) {
				jds.next().select(dbIndex);
			}
		}
	}
	
	
	/**
	 * 回调
	 * @return
	 * 		执行结果
	 */
	public abstract T execute();
	
	/**
	 * 获取执行结果
	 * @return
	 */
	public T getResult(){
		T result = null;
		try
		{
			result = execute();
		}
		catch (Throwable e) 
		{
			throw new RuntimeException("Redis execute exception",e);
		}
		finally
		{
			if(jedis != null)
			{
				jedis.close();
			}
		}
		return result;
	}
}
