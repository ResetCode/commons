package com.using.common.core.bean;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.using.common.core.validation.group.Query;

public class BaseCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotNull(message = "startIndex不可为空", groups = { Query.List.class })
	@Min(value = 0, message = "startIndex最小值为0", groups = { Query.List.class })
	protected Integer startIndex;
	protected Integer pageIndex;
	@NotNull(message = "pageSize不可为空", groups = { Query.List.class })
	@Min(value = 1, message = "pageSize最小值为1", groups = { Query.List.class })
	@Max(value = 100, message = "pageSize最大值为100", groups = { Query.List.class })
	protected Integer pageSize;

	public BaseCondition() {

	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
