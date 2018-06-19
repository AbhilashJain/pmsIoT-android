package com.hcl.pmsiot.data;

import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class UserDetailData {

	public UserDetailData() {
		// TODO Auto-generated constructor stub
	}

	public UserDetailData(String userId, double latitude, double longitude, Date lastKnown, boolean online,
			List<UserLocationHistoryData> locationHistory) {
		super();
		this.userId = userId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastKnown = lastKnown;
		this.online = online;
		this.locationHistory = locationHistory;
	}

	private String userId;

	private double latitude;

	private double longitude;

	private Date lastKnown;

	private boolean online;
	
	private List<UserLocationHistoryData> locationHistory;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getLastKnown() {
		return lastKnown;
	}

	public void setLastKnown(Date lastKnown) {
		this.lastKnown = lastKnown;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public List<UserLocationHistoryData> getLocationHistory() {
		return locationHistory;
	}

	public void setLocationHistory(List<UserLocationHistoryData> locationHistory) {
		this.locationHistory = locationHistory;
	}
	
	public static UserDetailBuilder newBuilder() {
		return new UserDetailBuilder();
	}

	public static class UserDetailBuilder {

		private String userId;

		private double latitude;

		private double longitude;

		private Date lastKnown;

		private boolean online;
		
		private List<UserLocationHistoryData> locationHistory;
		
		public UserDetailData getUserDetailData() {
			return new UserDetailData(this.userId, this.latitude, this.longitude, this.lastKnown, this.online, this.locationHistory);
		}

		public UserDetailBuilder setUserId(String userId) {
			this.userId = userId;
			return this;
		}

		public UserDetailBuilder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public UserDetailBuilder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public UserDetailBuilder setLastKnown(Date lastKnown) {
			this.lastKnown = lastKnown;
			return this;
		}

		public UserDetailBuilder setOnline(boolean online) {
			this.online = online;
			return this;
		}

		public UserDetailBuilder setLocationHistory(List<UserLocationHistoryData> locationHistory) {
			this.locationHistory = locationHistory;
			return this;
		}

	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
