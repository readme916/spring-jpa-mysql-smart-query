package com.liyang.jpa.smart.query.response;

public class HTTPListResponse {
	private Object items;
	private long total;
	private long pageNumber;
	private long pageSize;
	
	public HTTPListResponse() {
		
	}
	public HTTPListResponse(Object items, long total, long pageNumber, long pageSize) {
		super();
		this.items = items;
		this.total = total;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
	public Object getItems() {
		return items;
	}
	public void setItems(Object items) {
		this.items = items;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getPageSize() {
		return pageSize;
	}
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	
}
