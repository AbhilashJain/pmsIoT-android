package com.hcl.pmsiot.data;

public class DashboardResponse<T> {

	private T data;
	
	private String status;
	
	private String message;

	public T getData() {
		return data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public void setData(T data) {
		this.data = data;
	}
}
