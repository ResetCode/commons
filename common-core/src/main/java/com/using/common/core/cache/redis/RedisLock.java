package com.using.common.core.cache.redis;

import java.util.Random;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis实现的跨jvm的lock
 * 
 * @author fengjc
 */
public class RedisLock {

	/** 加锁标志 **/
	public static final String LOCKED = "TRUE";

	/** 毫秒与毫微秒的换算单位 1毫秒 = 1000000 毫微秒 **/
	public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;

	/** 默认超过时间(毫秒) **/
//	public static final long DEFAULT_TIME_OUT = 1000;
	public static final long DEFAULT_TIME_OUT = 600000;

	public static final Random RANDOM = new Random();

	/** 锁的超时时间(秒)，过期删除 **/
	public static final int EXPIRE = 3 * 60;

	private ShardedJedisPool jedisPool;
	private ShardedJedis jedis;
	private String key;

	// 锁状态标识
	private boolean locked = false;

	public RedisLock(String key, ShardedJedisPool jedisPool) {
		this.key = key + "_lock";
		this.jedisPool = jedisPool;
		this.jedis = this.jedisPool.getResource();
	}

	/**
	 * 加锁 lock(); try {doSomething();} finally {unlock();}
	 * 
	 * @param timeout 超时时间
	 * @return 成功或失败标志
	 */
	public boolean lock(long timeout) {
		// 获取当前时间的毫微秒
		long nano = System.nanoTime();
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ((System.nanoTime() - nano) < timeout) {
				// 判断如果该key不存在，则存储,如果之前有一个对应值则返回0
				if (this.jedis.setnx(this.key, LOCKED) == 1) {
					// 指定该key的过期时间
					this.jedis.expire(this.key, EXPIRE);
					this.locked = true;
					return this.locked;
				}

				// 短暂休眠，避免出现活锁
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			throw new RuntimeException("Locking error", e);
		}
		return false;
	}

	/**
	 * 加锁 lock(); try {doSomething();} finally {unlock();}
	 * 
	 * @param timeout 超时时间
	 * @return 成功或失败标志
	 */
	public boolean lock() {
		return lock(DEFAULT_TIME_OUT);
	}

	/**
	 * 解锁 无论是否加锁成功，都需要调用unlock lock(); try {doSomething();} finally {unlock();}
	 */
	public void unlock() {
		try {
			if (this.locked) {
				this.jedis.del(this.key);
			}
		} finally {
			if (this.jedis != null) {
				this.jedis.disconnect();
			}
//				  this.jedis.close();
//				  this.jedis.disconnect();
		}
	}
}
