package com.decathlon.gateway.Link_referencing_api.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "" })
public class Status {
	
	private String healthy = "false";
	private String database_healthy = "false";
	private String message = "Unknow message.";

	public Status() {
		
	}
	
	public Status(boolean healthy, boolean database_healthy, String message) {
		this.setHealthy(healthy + "");
		this.database_healthy = database_healthy + "";
		this.setMessage(message);
	}
	
	public void setDataBaseHealthy(boolean b, String msg) {
		database_healthy = b + "";
		setMessage(msg);
	}
	public String getDatabaseHealthy() {
		return this.database_healthy;
	}
	
	public void param() {
		if (database_healthy.equals("true"))
			setHealthy("true");
	}

	public String getHealthy() {
		return healthy;
	}

	public void setHealthy(String healthy) {
		this.healthy = healthy;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
