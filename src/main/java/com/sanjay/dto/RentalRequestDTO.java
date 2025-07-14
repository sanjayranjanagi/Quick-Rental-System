package com.sanjay.dto;

import java.time.LocalDate;

public class RentalRequestDTO {

	 private Integer customerId;
	 private String vehicleNo;
	 private LocalDate pickUpDate;
	 private LocalDate dropOffDate;
	 
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
	public LocalDate getPickUpDate() {
		return pickUpDate;
	}
	public void setPickUpDate(LocalDate pickUpDate) {
		this.pickUpDate = pickUpDate;
	}
	public LocalDate getDropOffDate() {
		return dropOffDate;
	}
	public void setDropOffDate(LocalDate dropOffDate) {
		this.dropOffDate = dropOffDate;
	}
	
	 
	 
}
