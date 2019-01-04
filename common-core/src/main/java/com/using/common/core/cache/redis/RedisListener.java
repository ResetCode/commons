package com.using.common.core.cache.redis;

import redis.clients.jedis.JedisPubSub;

/**
 * jedis 自定义 listener，重新定义jedisPubSub中回调方法
 */
public abstract class RedisListener extends JedisPubSub{

	
	//获取所有订阅渠道
	public abstract int getSubscribedChannels();

	//获取是否被订阅
	public abstract boolean isSubscribed();

	//接收到消息后进行分发执行
	public abstract void onMessage(String channel, String message);

	//接收到消息后按照表达式进行分发执行
	public abstract void onPMessage(String pattern, String channel, String message);
	
	// 按表达式的方式订阅时候的执行，并返回订阅数量
	public abstract void onPSubscribe(String pattern, int subscribedChannels);

	// 按表达式的方式取消订阅时候的执行，并返回订阅数量
	public abstract void onPUnsubscribe(String pattern, int subscribedChannels);

	//订阅后执行
	public abstract void onSubscribe(String channel, int subscribedChannels);

	//取消订阅后执行
	public abstract void onUnsubscribe(String channel, int subscribedChannels);

}
