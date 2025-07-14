package com.sanjay.dto;

import java.time.LocalDate;

public class ReturnRequestDTO {
    private Integer customerId;
    private String vehicleNo;
    private LocalDate actualDropOffDate;

    // Getters and Setters
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

    public LocalDate getActualDropOffDate() {
        return actualDropOffDate;
    }

    public void setActualDropOffDate(LocalDate actualDropOffDate) {
        this.actualDropOffDate = actualDropOffDate;
    }
}
