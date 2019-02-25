package com.liyang.jpa.mysql.exception;


/**
 * 使用SmartJpaQuery的的各种异常
 * 号码5000-10000
 * @author liyang
 *
 */
public class GetFormatException extends RuntimeException{
	private int error=1;
	private int code;
	private String message;
	private Object because;
	public GetFormatException(int code, String message, Object because) {
		super();
		this.code = code;
		this.message = message;
		this.because = because;
	}

	public int getError() {
		return error;
	}
	public void setError(int error) {
		this.error = error;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getBecause() {
		return because;
	}
	public void setBecause(Object because) {
		this.because = because;
	}

}
