package com.sanjay.api;

import java.util.List;

import org.hibernate.annotations.AnyDiscriminator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanjay.dto.CustomerDTO;
import com.sanjay.dto.RentalRequestDTO;
import com.sanjay.dto.ReturnRequestDTO;
import com.sanjay.dto.VehicleDTO;
import com.sanjay.exception.QuickRentalException;
import com.sanjay.service.QuickVehicleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value="/quick-rent")
@Validated
public class QuickRentalApi {
	
	@Autowired
	private QuickVehicleService quickVehicleService;
	
	@Autowired
	private Environment environment;
	
	@GetMapping(value="/rent-history/{customerId}")
	public ResponseEntity<CustomerDTO>getCustomer(@Valid @PathVariable
			@Min(value=1000,message= "{customer.id.invalid}")Integer customerId) throws QuickRentalException{
		CustomerDTO cdto=quickVehicleService.viewRentalHistory(customerId);
		
		return new ResponseEntity<>(cdto,HttpStatus.OK);
	}
	
	@PostMapping(value="/register-customer")
	public ResponseEntity<String> registerCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws QuickRentalException {
	    String name = quickVehicleService.registerCustomer(customerDTO);
	    String successMessage = environment.getProperty("QuickRentalApi.customer.registered") + ": " + name;
	    return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
	}

	
	@DeleteMapping(value="/delete")
	public ResponseEntity<String>deleteCustomer(@RequestBody CustomerDTO customerDTO) throws QuickRentalException{
		String no=quickVehicleService.deleteCustomer(customerDTO);
		String successMessage=environment.getProperty("QuickRentalApi.customer.deleted")+":"+no;
		
		return new ResponseEntity<>(successMessage,HttpStatus.OK);
	}
	
	@PostMapping(value="/add-vehicle")
	public ResponseEntity<String> addVehicle(@RequestBody @Valid VehicleDTO vehicleDTO) throws QuickRentalException {
	    String vehicleNo = quickVehicleService.addVehicle(vehicleDTO);
	    String successMessage = environment.getProperty("QuickRentalApi.VEHICLE.ADDED") + ": " + vehicleNo;
	    return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
	}
	
	@PostMapping("/rent-vehicle")
	public ResponseEntity<String> rentVehicle(@RequestBody @Valid RentalRequestDTO rentalRequestDTO) throws QuickRentalException {
	    String vehicleNo = quickVehicleService.rentVehicle(rentalRequestDTO);
	    String successMessage = "Vehicle rented successfully until " + rentalRequestDTO.getDropOffDate() + " for vehicle: " + vehicleNo;
	    return new ResponseEntity<>(successMessage, HttpStatus.OK);
	}
	
	@PostMapping(value="/return-vehicle")
	public ResponseEntity<String> returnVehicle(@RequestBody @Valid ReturnRequestDTO dto) throws QuickRentalException {
	    String result = quickVehicleService.returnVehicle(dto);
	    return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/available-vehicles")
	public ResponseEntity<List<VehicleDTO>> getAvailableVehicles() throws QuickRentalException {
	    List<VehicleDTO> availableVehicles = quickVehicleService.viewAvailableVehicles();
	    return new ResponseEntity<>(availableVehicles, HttpStatus.OK);
	}





}
