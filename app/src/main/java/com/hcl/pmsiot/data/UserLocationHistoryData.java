package com.hcl.pmsiot.data;

import java.util.Date;

public class UserLocationHistoryData {

	
	public UserLocationHistoryData(BoundaryData coordinate, Date timestamp) {
		super();
		this.coordinate = coordinate;
		this.timestamp = timestamp;
	}

	private BoundaryData coordinate;
	
	private Date timestamp;

	public BoundaryData getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(BoundaryData coordinate) {
		this.coordinate = coordinate;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
