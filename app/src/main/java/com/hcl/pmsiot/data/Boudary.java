package com.hcl.pmsiot.data;

public class Boudary {
	
	private String latitude;
	private String longitude;
	
	public Boudary() {
		// TODO Auto-generated constructor stub
	}

	public Boudary(String latitude, String longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	

}
