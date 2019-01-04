package com.using.common.queue.bean;

import java.io.Serializable;

public class QueueResponse<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected T data;
	protected boolean hasException = false;
	protected Throwable throwable;

	public QueueResponse() {

	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean getHasException() {
		return hasException;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.hasException = true;
		this.throwable = throwable;
	}
}
