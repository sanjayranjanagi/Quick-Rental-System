package com.sanjay.service;

import java.util.List;

import com.sanjay.dto.CustomerDTO;
import com.sanjay.dto.RentalRequestDTO;
import com.sanjay.dto.ReturnRequestDTO;
import com.sanjay.dto.VehicleDTO;
import com.sanjay.exception.QuickRentalException;

public interface QuickVehicleService {
	
	public CustomerDTO viewRentalHistory(Integer customerId) throws QuickRentalException;
	
	public String deleteCustomer(CustomerDTO customerDTo) throws QuickRentalException;
	
	String registerCustomer(CustomerDTO customerDTO) throws QuickRentalException;
	
	public String addVehicle(VehicleDTO dto) throws QuickRentalException;
	
	String rentVehicle(RentalRequestDTO rentalRequestDTO) throws QuickRentalException;
	
	String returnVehicle(ReturnRequestDTO dto) throws QuickRentalException;
	
	public List<VehicleDTO> viewAvailableVehicles() throws QuickRentalException;


}
	