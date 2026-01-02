package com.sanjay.dto;

public class CancelBooking {
	private Integer customerId;
    private String vehicleNo;
	public CancelBooking(Integer customerId, String vehicleNo) {
		super();
		this.customerId = customerId;
		this.vehicleNo = vehicleNo;
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
    
    
}
