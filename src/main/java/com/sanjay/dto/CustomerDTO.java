package com.sanjay.dto;

import java.util.List;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CustomerDTO {
	
	private Integer customerId;
	private String customerName;
	@NotNull(message="{customer.drivinglicenceno.absent}")
	@Pattern(regexp = "([A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{7})",
    message = "Invalid driving licence format")
	private String drivingLicenceNo;
	private Integer age;
	private List<RentalHistoryDTO>rentalHistoryDTOs;
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDrivingLicenceNo() {
		return drivingLicenceNo;
	}
	public void setDrivingLicenceNo(String drivingLicenceNo) {
		this.drivingLicenceNo = drivingLicenceNo;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public List<RentalHistoryDTO> getRentalHistoryDTOs() {
		return rentalHistoryDTOs;
	}
	public void setRentalHistoryDTOs(List<RentalHistoryDTO> rentalHistoryDTOs) {
		this.rentalHistoryDTOs = rentalHistoryDTOs;
	}
	
	
}
