package com.using.common.notify;

import org.springframework.beans.factory.annotation.Autowired;

import com.using.common.notify.interfaces.INotifyPersistence;
import com.using.common.notify.model.BaseRequest;

public class BaseService {

	@Autowired(required = false)
	protected INotifyPersistence notifyPersistence;
	
	public void save(BaseRequest req) {
		notifyPersistence.save(req);
	}
}
