package com.sanjay.dto;

import java.time.LocalDate;

public class ExtendBooking {
	 private Integer customerId;
	 private String vehicleNo;
	 private LocalDate newDropOffDate;
	public ExtendBooking(Integer customerId, String vehicleNo, LocalDate newDropOffDate) {
		super();
		this.customerId = customerId;
		this.vehicleNo = vehicleNo;
		this.newDropOffDate = newDropOffDate;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public LocalDate getNewDropOffDate() {
		return newDropOffDate;
	}
	public void setNewDropOffDate(LocalDate newDropOffDate) {
		this.newDropOffDate = newDropOffDate;
	}
	 
	 
}
