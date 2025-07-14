package com.sanjay.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class RentalHistoryDTO {
	
	private Integer id;
	@NotNull(message="{rental.pickupdate.absent}")
	@FutureOrPresent(message="{rental.date.invalid}")
	private LocalDate pickUpDate;
	@NotNull(message="{rentla.dropoffdate.absent}")
	@FutureOrPresent(message="{rental.date.invalid}")
	private LocalDate dropOffDate;
	private VehicleDTO vehicleDTO;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public VehicleDTO getVehicleDTO() {
		return vehicleDTO;
	}
	public void setVehicleDTO(VehicleDTO vehicleDTO) {
		this.vehicleDTO = vehicleDTO;
	}
	
	
	
}
