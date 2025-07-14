package com.sanjay.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanjay.dto.BookingStatus;
import com.sanjay.dto.CustomerDTO;
import com.sanjay.dto.RentalHistoryDTO;
import com.sanjay.dto.RentalRequestDTO;
import com.sanjay.dto.ReturnRequestDTO;
import com.sanjay.dto.VehicleDTO;
import com.sanjay.entity.Customer;
import com.sanjay.entity.RentalHistory;
import com.sanjay.entity.Vehicle;
import com.sanjay.exception.QuickRentalException;
import com.sanjay.repository.CustomerRepository;
import com.sanjay.repository.RentalHistoryRepository;
import com.sanjay.repository.VehicleRepository;

import jakarta.transaction.Transactional;

@Service(value="quickVehicleService")
@Transactional
public class QuickVehicleServiceImpl implements QuickVehicleService{
	
	@Autowired
	private CustomerRepository customerRepository;
	
	 @Autowired
	 private VehicleRepository vehicleRepository;
	
	 @Autowired
	 private RentalHistoryRepository rentalHistoryRepository;
	@Override
	public CustomerDTO viewRentalHistory(Integer customerId) throws QuickRentalException{
		Optional<Customer>cust=customerRepository.findById(customerId);
		if(cust.isEmpty()) {
			throw new QuickRentalException("QuickVehicleService.CUSTOMER_NOT_FOUND");
		}
		Customer c=cust.get();
		CustomerDTO cdto=new CustomerDTO();
		cdto.setCustomerId(customerId);
		cdto.setCustomerName(c.getCustomerName());
		cdto.setDrivingLicenceNo(c.getDrivingLicenceNo());
		cdto.setAge(c.getAge());
		
		List<RentalHistory>r=new ArrayList<>();
		r=c.getRentalHistory();
		
		List<RentalHistoryDTO>res=new ArrayList<>();
		int i=0;
		for(RentalHistory rr:r) {
			RentalHistoryDTO ren=new RentalHistoryDTO();
			ren.setId(rr.getId());
			ren.setPickUpDate(rr.getPickUpDate());
			ren.setDropOffDate(rr.getDropOffDate());
			
			VehicleDTO v=new VehicleDTO();
			v.setVehicleNo(c.getRentalHistory().get(i).getVehicle().getVehicleNo());
			v.setPrice(c.getRentalHistory().get(i).getVehicle().getPrice());
			v.setVehicleName(c.getRentalHistory().get(i).getVehicle().getVehicleName());
			v.setBookingStatus(c.getRentalHistory().get(i).getVehicle().getBookingStatus().name());
			v.setVehicleType(c.getRentalHistory().get(i).getVehicle().getVehicleType());
			
			ren.setVehicleDTO(v);
			res.add(ren);
		}
		
		cdto.setRentalHistoryDTOs(res);
		List<RentalHistoryDTO>ans=res.stream().sorted((r1,r2)->r1.getDropOffDate().compareTo(r2.getDropOffDate())).collect(Collectors.toList());
		
		
		return cdto;
	}
	
		@Override
		public String deleteCustomer(CustomerDTO customerDTO) throws QuickRentalException{
			Optional<Customer>c=customerRepository.findByDrivingLicenceNo(customerDTO.getDrivingLicenceNo());
			System.out.println("Received Driving Licence No: " + customerDTO.getDrivingLicenceNo());

			if(c.isEmpty()) {
				throw new QuickRentalException("QuickVehicleService.CUSTOMER_NOT_FOUND");
			}
			Customer cust=c.get();
			String no=cust.getDrivingLicenceNo();
			customerRepository.delete(cust);
			
			return no;
		}
		
		@Override
		public String registerCustomer(CustomerDTO customerDTO) throws QuickRentalException {
		    Optional<Customer> existing = customerRepository.findByDrivingLicenceNo(customerDTO.getDrivingLicenceNo());
		    if (existing.isPresent()) {
		        throw new QuickRentalException("QuickVehicleService.customer.alreadyExists");
		    }

		    Customer c = new Customer();
		    c.setCustomerName(customerDTO.getCustomerName());
		    c.setDrivingLicenceNo(customerDTO.getDrivingLicenceNo());
		    c.setAge(customerDTO.getAge());

		    Customer saved = customerRepository.save(c);
		    return saved.getCustomerName();
		}
		
		@Override
		public String addVehicle(VehicleDTO dto) throws QuickRentalException {
	        Vehicle v = new Vehicle();
	        v.setVehicleNo(dto.getVehicleNo());
	        v.setVehicleName(dto.getVehicleName());
	        v.setPrice(dto.getPrice());

	        try {
	            v.setBookingStatus(BookingStatus.valueOf(dto.getBookingStatus().toUpperCase()));
	        } catch (IllegalArgumentException e) {
	            throw new QuickRentalException("Invalid booking status");
	        }

	        v.setVehicleType(dto.getVehicleType());

	        vehicleRepository.save(v);
	        return v.getVehicleNo();
	    }
		
		@Override
		public String rentVehicle(RentalRequestDTO rentalRequestDTO) throws QuickRentalException {
		    Optional<Customer> customerOpt = customerRepository.findById(rentalRequestDTO.getCustomerId());
		    if (!customerOpt.isPresent()) {
		        throw new QuickRentalException("Customer not found.");
		    }

		    Optional<Vehicle> vehicleOpt = vehicleRepository.findById(rentalRequestDTO.getVehicleNo());
		    if (!vehicleOpt.isPresent()) {
		        throw new QuickRentalException("Vehicle not found.");
		    }

		    Vehicle vehicle = vehicleOpt.get();
		    if (vehicle.getBookingStatus() == BookingStatus.BOOKED) {
		        throw new QuickRentalException("Vehicle already booked.");
		    }

		    // 1. Update vehicle status
		    vehicle.setBookingStatus(BookingStatus.BOOKED);
		    vehicleRepository.save(vehicle);

		    // 2. Create rental history
		    RentalHistory rental = new RentalHistory();
		    rental.setCustomer(customerOpt.get());
		    rental.setVehicle(vehicle);
		    rental.setPickUpDate(rentalRequestDTO.getPickUpDate() != null ? rentalRequestDTO.getPickUpDate() : LocalDate.now());
		    rental.setDropOffDate(rentalRequestDTO.getDropOffDate());
		    rental.setVehicle(vehicle);
		    rentalHistoryRepository.save(rental);

		    return vehicle.getVehicleNo();
		}
		
		@Override
		public String returnVehicle(ReturnRequestDTO dto) throws QuickRentalException {
		    Optional<RentalHistory> rentalOpt = rentalHistoryRepository
		        .findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(dto.getCustomerId(), dto.getVehicleNo());

		    if (!rentalOpt.isPresent()) {
		        throw new QuickRentalException("Rental record not found.");
		    }

		    RentalHistory rental = rentalOpt.get();
		    LocalDate dbDropOff = rental.getDropOffDate();
		    LocalDate actual = dto.getActualDropOffDate();

		    if (actual == null) {
		        throw new QuickRentalException("Drop-off date is required.");
		    }

		    if (!actual.isAfter(dbDropOff)) {
		        // Returned on time or early → mark available
		        Vehicle vehicle = rental.getVehicle();
		        vehicle.setBookingStatus(BookingStatus.AVAILABLE);
		        vehicleRepository.save(vehicle);
		    } else {
		        // Late return → only extend drop-off date
		        rental.setDropOffDate(actual);
		    }

		    rentalHistoryRepository.save(rental);
		    return "Vehicle " + dto.getVehicleNo()+" returned successfully";
		}
		
		@Override
		public List<VehicleDTO> viewAvailableVehicles() throws QuickRentalException {
		    List<Vehicle> vehicles = vehicleRepository.findByBookingStatus(BookingStatus.AVAILABLE);
		    
		    if (vehicles.isEmpty()) {
		        throw new QuickRentalException("No available vehicles at the moment.");
		    }
		    
		    return vehicles.stream()
		                   .map(this::convertToDTO)
		                   .collect(Collectors.toList());
		}

		// Helper method to convert entity to DTO
		private VehicleDTO convertToDTO(Vehicle vehicle) {
		    VehicleDTO dto = new VehicleDTO();
		    dto.setVehicleNo(vehicle.getVehicleNo());
		    dto.setVehicleName(vehicle.getVehicleName());
		    dto.setPrice(vehicle.getPrice());
		    dto.setVehicleType(vehicle.getVehicleType());
		    dto.setBookingStatus(vehicle.getBookingStatus().toString());
		    return dto;
		}



}
