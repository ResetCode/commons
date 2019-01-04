package com.using.common.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;

import com.using.common.core.cache.redis.Executor;
import com.using.common.core.cache.redis.Pair;
import com.using.common.core.cache.redis.RedisLock;
import com.using.common.util.SerializeUtils;

/**
 *  liumohan 
 */

//@Service
public class RedisCache {

	/** 3秒 */
	public final static int EXPIRES_LEVEL_THREE_SECOND = 3;
	/** 5分钟 **/
	public final static int EXPIRES_LEVEL_FIVE_MIN =60*5;
	/** 1小时 **/
	public final static int EXPIRES_LEVEL_HOUR =60*60;	
	/** 1天 **/
	public final static int EXPIRES_LEVEL_DAY = 60*60*24;
	/** 7天 **/
	public final static int EXPIRES_LEVEL_WEEK = 60*60*24*7;
	/** 30天 **/
	public final static int EXPIRES_LEVEL_MONTH = 60*60*24*30;
	
	/** redis 开关 */
	@Autowired(required = false)
	@Value(value = "${redis.open}")
	private String open = "true";

	/** host */
	@Autowired(required = false)
	@Value(value = "${redis.host}")
	private String host = "127.0.0.1";

	/** 端口 */
	@Autowired(required = false)
	@Value(value = "${redis.port}")
	private Integer port = 6379; 

	/** 密码 */
	@Autowired(required = false)
	@Value(value = "${redis.password}")
	private String password = "myredis";

	 
	/** 超时时间 */
	@Autowired(required = false)
	@Value(value = "${redis.timeout}")
	private Integer timeout = 2000;

	/** 最大空闲连接数数，默认8个 */
	@Autowired(required = false)
	@Value(value = "${redis.maxIdle}")
	private Integer maxIdle = 8; // 

	/** 最大连接数，默认8个 */
	@Autowired(required = false)
	@Value(value = "${redis.maxTotal}")
	private Integer maxTotal = 8; // 

	@Autowired(required = false)
	@Value(value = "${redis.maxWaitMillis}")
	private Integer maxWaitMillis = 1000;// 最大等待毫秒数

	@Autowired(required = false)
	@Value(value = "${redis.testOnBorrow}")
	private boolean testOnBorrow = true; // 获取连接的时候检查有效性，默认false

	@Autowired(required = false)
	@Value(value = "${redis.testOnReturn}")
	private boolean testOnReturn = false; // 返回连接的时候检查有效性，默认false

	private ShardedJedisPool jedisPool;

	private boolean on = true; // 是否打开redis开关,默认为true方便测试

	private JedisPoolConfig poolConfig;

	private JedisShardInfo jedisShardInfo;

	private List<JedisShardInfo> jedisShardInfos;
	
	@Autowired(required = false)
	@Value(value = "${redis.dbIndex}")
	private String dbIndex;

	@PostConstruct
	public void initMethod() {
		poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setTestOnBorrow(testOnBorrow);
		poolConfig.setTestOnReturn(testOnReturn);

		jedisShardInfo = new JedisShardInfo(host, port,timeout);
		jedisShardInfo.setPassword(password);
		
		jedisShardInfos = new ArrayList<JedisShardInfo>();
		jedisShardInfos.add(jedisShardInfo);

		jedisPool = new ShardedJedisPool(poolConfig, jedisShardInfos);
		on = "true".equals(open);
	}

	
	/**
	 * 为指定的key设置对象值
	 * @param key
	 * 			键
	 * @param o
	 * 			存储对象
	 * @return
	 * @throws Exception
	 */
	public Boolean setObject(final String key,final Object o){
		return new Executor<Boolean>(jedisPool,on,dbIndex) {
			@Override
			public Boolean execute()  {
				try 
				{
					byte[] objBytes = SerializeUtils.serialize(o);
					String result = this.jedis.set(key.getBytes(), objBytes);
					if(!"OK".equals(result))
						return false;
				} 
				catch (Exception e) 
				{
					throw new RuntimeException("Redis set object exception",e);
				}
				return true;
			}
		}.getResult();
	}
	
	/**
	 * @param key
	 * 		     键
	 * @return
	 */
	public <T> T getObject(final String key){
		return new Executor<T>(jedisPool,on,dbIndex){
			@Override
			public T execute() {
				byte[] objBytes = this.jedis.get(key.getBytes());
				try 
				{
					Object o = SerializeUtils.unserialize(objBytes);
					
					return (T) o;
				} 
				catch (Exception e) {
					throw new RuntimeException("Redis get object exception",e);
				} 
			}
		}.getResult();
	}
	/**
	 * 追加字符串，并返回追加后字符串结果
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public String append(final String key, final String value) {
		return new Executor<String>(jedisPool, on,dbIndex) {

			@Override
			public String execute() {
				this.jedis.append(key, value);
				return jedis.get(key);
			}

		}.getResult();
	}

	/**
	 * 删除模糊匹配的key
	 * 
	 * @param likeKey
	 *            模糊匹配的key
	 * @return 删除成功的条数
	 */
	public Long delLikeKeys(final String likeKey) {
		return new Executor<Long>(jedisPool, on,dbIndex) {

			@Override
			public Long execute() {
				Collection<Jedis> jedisC = jedis.getAllShards();
				Iterator<Jedis> iter = jedisC.iterator();
				long count = 0;
				while (iter.hasNext()) {
					Jedis _jedis = iter.next();
					Set<String> keys = _jedis.keys(likeKey + "*");
					count += _jedis.del(keys.toArray(new String[keys.size()]));
				}
				return count;
			}
		}.getResult();
	}

	/**
	 * 删除指定key
	 * 
	 * @param key
	 *            匹配key
	 * @return 删除成功的条数
	 */
	public Long delKey(final String key) {

		return new Executor<Long>(jedisPool, on,dbIndex) {

			@Override
			public Long execute() {

				return this.jedis.del(key);
			}

		}.getResult();
	}

	/**
	 * 删除key的集合
	 * 
	 * @param keys
	 *            匹配的key的集合
	 * @return 删除成功的条数
	 */
	public Long delKeys(final String[] keys) {
		return new Executor<Long>(jedisPool,on,dbIndex) {

			@Override
			public Long execute() {
				Collection<Jedis> jedisC = jedis.getAllShards();
				Iterator<Jedis> iter = jedisC.iterator();
				long count = 0;
				while (iter.hasNext()) {
					Jedis _jedis = iter.next();
					count += _jedis.del(keys);
				}
				return count;
			}
		}.getResult();
	}

	/**
	 * 为指定key设置生存时间，当key过期时(生存时间为0)，它会被自动删除
	 * 在Redis中，带有生存时间的key被称为[可挥发](valatile)的
	 * 
	 * @param key
	 *            键
	 * @param expire
	 *            声明周期，单位为秒
	 * @return 设置成功：1， 已超时或者key不存在：0
	 */
	public Long expire(final String key, final int expire) {
		return new Executor<Long>(jedisPool, on,dbIndex) {

			@Override
			public Long execute() {
				return this.jedis.expire(key, expire);
			}
		}.getResult();

	}

	/**
	 * 设置key的值加1
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public Long incr(final String key) {
		return new Executor<Long>(jedisPool, on,dbIndex) {

			@Override
			public Long execute() {
				return this.jedis.incr(key);
			}
		}.getResult();
	}

	/**
	 * 设置键的新值，并返回旧值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            旧值
	 * @return
	 */
	public String getSet(final String key, final String value) {
		return new Executor<String>(jedisPool, on,dbIndex) {

			@Override
			public String execute() {
				return this.jedis.getSet(key, value);
			}

		}.getResult();
	}

	/**
	 * 将字符串value关联到key 如果key已经持有其他值，就覆盖旧值，无视类型
	 * 对于某个原本带有生存时间(TTL)的键来说，当set成功在这个键上执行时，这个键原有的TTL将被清除 时间复杂度: O(1)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(final String key, final String value) {
		return new Executor<String>(jedisPool, on,dbIndex) {

			@Override
			public String execute() {
				return this.jedis.set(key, value);
			}

		}.getResult();
	}

	/**
	 * 讲值关联到key并将key的生存时间设置为expire(以秒为单位) 如果key已经存在，则覆盖旧值 等同于以下两个命令 SET key
	 * value EXPIRE key expire #设置TTL
	 * 不同之处是这个方法是一个原子性(atomic)操作，关联值和设置生存时间俩个动作会在同一时间内完成，在Redis用做缓存时，非常实用
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expire
	 *            生命周期
	 * @return
	 * 		  成功返回OK
	 */
	public String set(final String key, final String value, final int expire) {
		return new Executor<String>(jedisPool, on,dbIndex) {

			@Override
			public String execute() {
				return this.jedis.setex(key, expire, value);
			}

		}.getResult();
	}

	/**
	 * 将key的值设为value，当且仅当key不存在。 若给定的key已经存在则setnx不做任何动作 时间复杂度:O(1)
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 设置成功返回1，设置失败返回0
	 */
	public Long setIfKeyNotExists(final String key, final String value) {
		return new Executor<Long>(jedisPool, on, dbIndex) {

			@Override
			public Long execute() {
				return this.jedis.setnx(key, value);
			}

		}.getResult();
	}

	/**
	 * 返回key所关联的字符串值，如果key不存在那么返回特殊值nil 假如key储存的值不是字符串类型，返回一个错误，因为get只能用于处理字符串的值
	 * 时间复杂度O(1)
	 * 
	 * @param key
	 *            键
	 * @return 当key不存在时，返回nil，否则，返回key的值，如果不是字符串类型，那么返回一个错误
	 */
	public String get(final String key) {
		return new Executor<String>(jedisPool, on, dbIndex) {

			@Override
			public String execute() {
				return this.jedis.get(key);
			}

		}.getResult();
	}

	/**
	 * 批量的{@link #set(String, String)}
	 * 
	 * @param pairs
	 *            键值对数组{数组第一个元素为key，第二个元素为value}
	 * @return 操作状态的集合
	 */
	public List<Object> batchSet(final List<Pair<String, String>> pairs) {
		return new Executor<List<Object>>(jedisPool, on, dbIndex) {

			@Override
			public List<Object> execute() {
				ShardedJedisPipeline pipeline = jedis.pipelined();
				for (Pair<String, String> pair : pairs) {
					pipeline.set(pair.getKey(), pair.getValue());
				}
				return pipeline.syncAndReturnAll();
			}

		}.getResult();
	}

	/**
	 * 批量的{@link #get(String)}
	 * 
	 * @param keys
	 *            key数组
	 * @return value集合
	 */
	public List<String> batchGet(final String[] keys) {
		return new Executor<List<String>>(jedisPool, on, dbIndex) {

			@Override
			public List<String> execute() {
				ShardedJedisPipeline pipeline = this.jedis.pipelined();
				List<String> result = new ArrayList<String>(keys.length);
				List<Response<String>> responses = new ArrayList<Response<String>>(
						keys.length);
				for (String key : keys) {
					responses.add(pipeline.get(key));
				}
				pipeline.sync();
				for (Response<String> response : responses) {
					result.add(response.get());
				}
				return result;
			}

		}.getResult();
	}

	/**
	 * 返回哈希表 key 中给定域 field 的值。 时间复杂度:O(1)
	 * 
	 * @param key
	 *            key
	 * @param field
	 *            域
	 * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 null 。
	 */
	public String hashGet(final String key, final String field) {
		return new Executor<String>(jedisPool, on, dbIndex) {

			@Override
			public String execute() {
				return jedis.hget(key, field);
			}
		}.getResult();
	}

	/**
	 * 返回哈希表 key 中给定域 field 的值。 时间复杂度:O(1)
	 * @param key
	 * @param field
	 * @param cacheCaller
	 * @return
	 */
	public Object hashGet(final String key,final String field,final ICacheCaller cacheCaller) {
		return new Executor<Object>(jedisPool, on, dbIndex) {

			@Override
			public Object execute() {
				String result = jedis.hget(key, field);
				if(result == null){
					return cacheCaller.Call();
				} 
				return result;
			}
			
		}.getResult();
	}
	   /* ======================================Hashes====================================== */  
	  
    /** 
     * 将哈希表 key 中的域 field 的值设为 value 。 
     * 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。 
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。 
     * 时间复杂度: O(1) 
     * @param key key 
     * @param field 域 
     * @param value string value 
     * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。 
     */  
    public Long hashSet(final String key, final String field, final String value) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Long execute() {  
                return jedis.hset(key, field, value);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 将哈希表 key 中的域 field 的值设为 value 。 
     * 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。 
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。 
     * @param key key 
     * @param field 域 
     * @param value string value 
     * @param expire 生命周期，单位为秒 
     * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。 
     */  
    public Long hashSet(final String key, final String field, final String value, final int expire) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Long execute() {  
                Pipeline pipeline = jedis.getShard(key).pipelined();  
                Response<Long> result = pipeline.hset(key, field, value);  
                pipeline.expire(key, expire);  
                pipeline.sync();  
                return result.get();  
            }  
        }.getResult();  
    }  
    
	/**
	 * 返回哈希表 key 中给定域 field 的值。 如果哈希表 key 存在，同时设置这个 key 的生存时间
	 * 
	 * @param key
	 *            key
	 * @param field
	 *            域
	 * @param expire
	 *            生命周期，单位为秒
	 * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
	 */
	public String hashGet(final String key, final String field, final int expire) {
		return new Executor<String>(jedisPool, on, dbIndex) {

			@Override
			public String execute() {
				Pipeline pipeline = jedis.getShard(key).pipelined();
				Response<String> result = pipeline.hget(key, field);
				pipeline.expire(key, expire);
				pipeline.sync();
				return result.get();
			}
		}.getResult();
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 时间复杂度: O(N) (N为fields的数量)
	 * 
	 * @param key
	 *            key
	 * @param hash
	 *            field-value的map
	 * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
	 */
	public String hashMultipileSet(final String key,
			final Map<String, String> hash) {
		return new Executor<String>(jedisPool, on, dbIndex) {

			@Override
			public String execute() {
				return jedis.hmset(key, hash);
			}
		}.getResult();
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。同时设置这个 key 的生存时间
	 * 
	 * @param key
	 *            key
	 * @param hash
	 *            field-value的map
	 * @param expire
	 *            生命周期，单位为秒
	 * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
	 */
	public String hashMultipleSet(final String key, final Map<String, String> hash, final int expire) {
		return new Executor<String>(jedisPool, on, dbIndex) {

			@Override
			public String execute() {
				Pipeline pipeline = jedis.getShard(key).pipelined(); 
				Response<String> result = pipeline.hmset(key, hash);
				pipeline.expire(key, expire);
				pipeline.sync();
				return result.get();
			}
		}.getResult();
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 时间复杂度: O(N)
	 * (N为fields的数量)
	 * 
	 * @param key
	 *            key
	 * @param fields
	 *            field的数组
	 * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 */
	public List<String> hashMultipleGet(final String key,
			final String... fields) {
		return new Executor<List<String>>(jedisPool, on, dbIndex) {

			@Override
			public List<String> execute() {
				return jedis.hmget(key, fields);
			}
		}.getResult();
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 同时设置这个 key 的生存时间
	 * 
	 * @param key
	 *            key
	 * @param fields
	 *            field的数组
	 * @param expire
	 *            生命周期，单位为秒
	 * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
	 */
	public List<String> hashMultipleGet(final String key, final int expire,
			final String... fields) {
		return new Executor<List<String>>(jedisPool, on, dbIndex) {

			@Override
			public List<String> execute() {
				Pipeline pipeline = jedis.getShard(key).pipelined();
				Response<List<String>> result = pipeline.hmget(key, fields);
				pipeline.expire(key, expire);
				pipeline.sync();
				return result.get();
			}
		}.getResult();
	}

	/**
	 * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
	 * 
	 * @param pairs
	 *            多个hash的多个field
	 * @return 操作状态的集合
	 */
	public List<Object> batchHashMultipleSet(
			final List<Pair<String, Map<String, String>>> pairs) {
		return new Executor<List<Object>>(jedisPool, on, dbIndex) {

			@Override
			public List<Object> execute() {
				ShardedJedisPipeline pipeline = jedis.pipelined();
				for (Pair<String, Map<String, String>> pair : pairs) {
					pipeline.hmset(pair.getKey(), pair.getValue());
				}
				return pipeline.syncAndReturnAll();
			}
		}.getResult();
	}

	/**
	 * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
	 * 
	 * @param data
	 *            Map<String, Map<String, String>>格式的数据
	 * @return 操作状态的集合
	 */
	public List<Object> batchHashMultipleSet(
			final Map<String, Map<String, String>> data) {
		return new Executor<List<Object>>(jedisPool, on, dbIndex) {

			@Override
			public List<Object> execute() {
				ShardedJedisPipeline pipeline = jedis.pipelined();
				for (Map.Entry<String, Map<String, String>> iter : data.entrySet()) {
					pipeline.hmset(iter.getKey(), iter.getValue());
				}
				return pipeline.syncAndReturnAll();
			}
		}.getResult();
	}

	  /** 
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 
     * 时间复杂度: O(N) 
     * @param key key 
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。 
     */  
    public Map<String, String> hashGetAll(final String key) {  
        return new Executor<Map<String, String>>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Map<String, String> execute() {  
                return jedis.hgetAll(key);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 
     * 同时设置这个 key 的生存时间 
     * @param key key 
     * @param expire 生命周期，单位为秒 
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。 
     */  
    public Map<String, String> hashGetAll(final String key, final int expire) {  
        return new Executor<Map<String, String>>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Map<String, String> execute() {  
                Pipeline pipeline = jedis.getShard(key).pipelined();  
                Response<Map<String, String>> result = pipeline.hgetAll(key);  
                pipeline.expire(key, expire);  
                pipeline.sync();  
                return result.get();  
            }  
        }.getResult();  
    }  
    
    /** 
     * 批量的{@link #hashGetAll(String)} 
     * @param keys key的数组 
     * @return 执行结果的集合 
     */  
    public List<Map<String, String>> batchHashGetAll(final String... keys) {  
        return new Executor<List<Map<String, String>>>(jedisPool, on, dbIndex) {  
  
            @Override  
           public List<Map<String, String>> execute() {  
                ShardedJedisPipeline pipeline = jedis.pipelined();  
                List<Map<String, String>> result = new ArrayList<Map<String, String>>(keys.length);  
                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(keys.length);  
                for (String key : keys) {  
                    responses.add(pipeline.hgetAll(key));  
                }  
                pipeline.sync();  
                for (Response<Map<String, String>> resp : responses) {  
                    result.add(resp.get());  
                }  
                return result;  
            }  
        }.getResult();  
    }  
  
    /** 
     * 批量的{@link #hashMultipleGet(String, String...)}，与{@link #batchHashGetAll(String...)}不同的是，返回值为Map类型 
     * @param keys key的数组 
     * @return 多个hash的所有filed和value 
     */  
    public Map<String, Map<String, String>> batchHashGetAllForMap(final String... keys) {  
        return new Executor<Map<String, Map<String, String>>>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Map<String, Map<String, String>> execute() {  
                ShardedJedisPipeline pipeline = jedis.pipelined();  
  
                // 设置map容量防止rehash  
                int capacity = 1;  
                while ((int) (capacity * 0.75) <= keys.length) {  
                    capacity <<= 1;  
                }  
                Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>(capacity);  
                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(keys.length);  
                for (String key : keys) {  
                    responses.add(pipeline.hgetAll(key));  
                }  
                pipeline.sync();  
                for (int i = 0; i < keys.length; ++i) {  
                    result.put(keys[i], responses.get(i).get());  
                }  
                return result;  
            }  
        }.getResult();  
    }  
    
    /* ======================================List====================================== */  
    
    /** 
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。 
     * @param key key 
     * @param values value的数组 
     * @return 执行 listPushTail 操作后，表的长度 
     */  
    public Long listPushTail(final String key, final String... values) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Long execute() {  
                return jedis.rpush(key, values);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 将一个或多个值 value 插入到列表 key 的表头 
     * @param key key 
     * @param value string value 
     * @return 执行 listPushHead 命令后，列表的长度。 
     */  
    public Long listPushHead(final String key, final String value) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Long execute() {  
                return jedis.lpush(key, value);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 将一个或多个值 value 插入到列表 key 的表头, 当列表大于指定长度是就对列表进行修剪(trim) 
     * @param key key 
     * @param value string value 
     * @param size 链表超过这个长度就修剪元素 
     * @return 执行 listPushHeadAndTrim 命令后，列表的长度。 
     */  
    public Long listPushHeadAndTrim(final String key, final String value, final long size) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Long execute() {  
                Pipeline pipeline = jedis.getShard(key).pipelined();  
                Response<Long> result = pipeline.lpush(key, value);  
                // 修剪列表元素, 如果 size - 1 比 end 下标还要大，Redis将 size 的值设置为 end 。  
                pipeline.ltrim(key, 0, size - 1);  
                pipeline.sync();  
                return result.get();  
            }  
        }.getResult();  
    }  
  
    /** 
     * 批量的{@link #listPushTail(String, String...)}，以锁的方式实现 
     * @param key key 
     * @param values value的数组 
     * @param delOld 如果key存在，是否删除它。true 删除；false: 不删除，只是在行尾追加 
     */  
    public void batchListPushTail(final String key, final String[] values, final boolean delOld) {  
        new Executor<Object>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Object execute() {  
                if (delOld) {  
                    RedisLock lock = new RedisLock(key, jedisPool);  
                    lock.lock();  
                    try {  
                        Pipeline pipeline = jedis.getShard(key).pipelined();  
                        pipeline.del(key);  
                        for (String value : values) {  
                            pipeline.rpush(key, value);  
                        }  
                        pipeline.sync();  
                    } finally {  
                        lock.unlock();  
                    }  
                } else {  
                    jedis.rpush(key, values);  
                }  
                return null;  
            }  
        }.getResult();  
    }  
  
    /** 
     * 同{@link #batchListPushTail(String, String[], boolean)},不同的是利用redis的事务特性来实现 
     * @param key key 
     * @param values value的数组 
     * @return null 
     */  
    public Object updateListInTransaction(final String key, final List<String> values) {  
        return new Executor<Object>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Object execute() {  
                Transaction transaction = jedis.getShard(key).multi();  
                transaction.del(key);  
                for (String value : values) {  
                    transaction.rpush(key, value);  
                }  
                transaction.exec();  
                return null;  
            }  
        }.getResult();  
    }  
  
    /** 
     * 在key对应list的尾部部添加字符串元素,如果key存在，什么也不做 
     * @param key key 
     * @param values value的数组 
     * @return 执行insertListIfNotExists后，表的长度 
     */  
    public Long insertListIfNotExists(final String key, final String[] values) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
           public Long execute() {  
                RedisLock lock = new RedisLock(key, jedisPool);  
                lock.lock();  
                try {  
                    if (!jedis.exists(key)) {  
                        return jedis.rpush(key, values);  
                    }  
                } finally {  
                    lock.unlock();  
                }  
                return 0L;  
            }  
        }.getResult();  
    }  
  
    /** 
     * 返回list所有元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素，key不存在返回空列表 
     * @param key key 
     * @return list所有元素 
     */  
    public List<String> listGetAll(final String key) {  
        return new Executor<List<String>>(jedisPool, on, dbIndex) {  
  
            @Override  
            public List<String> execute() {  
                return jedis.lrange(key, 0, -1);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素，key不存在返回空列表 
     * @param key key 
     * @param beginIndex 下标开始索引（包含） 
     * @param endIndex 下标结束索引（不包含） 
     * @return 指定区间内的元素 
     */  
    public List<String> listRange(final String key, final long beginIndex, final long endIndex) {  
        return new Executor<List<String>>(jedisPool, on, dbIndex) {  
  
            @Override  
            public List<String> execute() {  
                return jedis.lrange(key, beginIndex, endIndex - 1);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 一次获得多个链表的数据 
     * @param keys key的数组 
     * @return 执行结果 
     */  
    public Map<String, List<String>> batchGetAllList(final List<String> keys) {  
        return new Executor<Map<String, List<String>>>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Map<String, List<String>> execute() {  
                ShardedJedisPipeline pipeline = jedis.pipelined();  
                Map<String, List<String>> result = new HashMap<String, List<String>>();  
                List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>(keys.size());  
                for (String key : keys) {  
                    responses.add(pipeline.lrange(key, 0, -1));  
                }  
                pipeline.sync();  
                for (int i = 0; i < keys.size(); ++i) {  
                    result.put(keys.get(i), responses.get(i).get());  
                }  
                return result;  
            }  
        }.getResult();  
    }  
  
    /* ======================================Pub/Sub====================================== */  
  
    /** 
     * 将信息 message 发送到指定的频道 channel。 
     * 时间复杂度：O(N+M)，其中 N 是频道 channel 的订阅者数量，而 M 则是使用模式订阅(subscribed patterns)的客户端的数量。 
     * @param channel 频道 
     * @param message 信息 
     * @return 接收到信息 message 的订阅者数量。 
     */  
    public Long publish(final String channel, final String message) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Long execute() {  
                Jedis _jedis = jedis.getShard(channel);  
                return _jedis.publish(channel, message);  
            }  
              
        }.getResult();  
    }  
  
    /** 
     * 订阅给定的一个频道的信息。 
     * @param jedisPubSub 监听器 
     * @param channel 频道 
     * 目前先不建议使用订阅方法，后面将修改此方法为异步方法，另起线程去监听订阅消息
     */
    @Deprecated
    public void subscribe(final JedisPubSub jedisPubSub, final String channel) {  
        new Executor<Object>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Object execute() {  
                Jedis _jedis = jedis.getShard(channel);  
                // 注意subscribe是一个阻塞操作，因为当前线程要轮询Redis的响应然后调用subscribe  
                _jedis.subscribe(jedisPubSub, channel);  
                return null;  
            }  
        }.getResult();  
    }  
  
    /** 
     * 取消订阅 
     * @param jedisPubSub 监听器 
     */  
    public void unSubscribe(final JedisPubSub jedisPubSub) {  
        jedisPubSub.unsubscribe();  
    }  
  
    /* ======================================Sorted set================================= */  
  
    /** 
     * 将一个 member 元素及其 score 值加入到有序集 key 当中。 
     * @param key key 
     * @param score score 值可以是整数值或双精度浮点数。 
     * @param member 有序集的成员 
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。 
     */  
    public Long addWithSortedSet(final String key, final double score, final String member) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Long execute() {  
                return jedis.zadd(key, score, member);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 将多个 member 元素及其 score 值加入到有序集 key 当中。 
     * @param key key 
     * @param scoreMembers score、member的pair 
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。 
     */  
    public Long addWithSortedSet(final String key, final Map<String, Double> scoreMembers) {  
        return new Executor<Long>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Long execute() {  
                return jedis.zadd(key, scoreMembers);  
            }  
        }.getResult();  
    }  
  
    /** 
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。 
     * 有序集成员按 score 值递减(从大到小)的次序排列。 
     * @param key key 
     * @param max score最大值 
     * @param min score最小值 
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表 
     */  
    public Set<String> revrangeByScoreWithSortedSet(final String key, final double max, final double min) {  
        return new Executor<Set<String>>(jedisPool, on, dbIndex) {  
  
            @Override  
            public Set<String> execute() {  
                return jedis.zrevrangeByScore(key, max, min);  
            }  
        }.getResult();  
    }  
  
    /* ======================================Other====================================== */  
  
    /** 
     * 设置数据源 
     * @param jedisPool, on 数据源 
     */  
    public void setShardedJedisPool(ShardedJedisPool jedisPool) {  
        this.jedisPool = jedisPool;  
    }  
  
    /** 
     * 构造Pair键值对 
     * @param key key 
     * @param value value 
     * @return 键值对 
     */  
    public <K, V> Pair<K, V> makePair(K key, V value) {  
        return new Pair<K, V>(key, value);  
    }  

}
