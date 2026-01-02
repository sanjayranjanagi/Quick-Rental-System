package com.sanjay.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sanjay.dto.BookingStatus;
import com.sanjay.dto.CancelBooking;
import com.sanjay.dto.CustomerDTO;
import com.sanjay.dto.ExtendBooking;
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

@Service(value = "quickVehicleService")
@Transactional
public class QuickVehicleServiceImpl implements QuickVehicleService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;

    // ---------------------------------------------------------
    // VIEW RENTAL HISTORY
    // ---------------------------------------------------------
    @Override
    public CustomerDTO viewRentalHistory(Integer customerId) throws QuickRentalException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new QuickRentalException("QuickVehicleService.CUSTOMER_NOT_FOUND"));

        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setDrivingLicenceNo(customer.getDrivingLicenceNo());
        dto.setAge(customer.getAge());

        List<RentalHistoryDTO> historyDTOs = customer.getRentalHistory()
                .stream()
                .map(rh -> {
                    RentalHistoryDTO rdto = new RentalHistoryDTO();
                    rdto.setId(rh.getId());
                    rdto.setPickUpDate(rh.getPickUpDate());
                    rdto.setDropOffDate(rh.getDropOffDate());

                    Vehicle v = rh.getVehicle();
                    VehicleDTO vdto = new VehicleDTO();
                    vdto.setVehicleNo(v.getVehicleNo());
                    vdto.setVehicleName(v.getVehicleName());
                    vdto.setPrice(v.getPrice());
                    vdto.setVehicleType(v.getVehicleType());
                    vdto.setBookingStatus(v.getBookingStatus().name());

                    rdto.setVehicleDTO(vdto);
                    return rdto;
                })
                .sorted((a, b) -> a.getDropOffDate().compareTo(b.getDropOffDate()))
                .collect(Collectors.toList());

        dto.setRentalHistoryDTOs(historyDTOs);
        return dto;
    }

    // ---------------------------------------------------------
    // DELETE CUSTOMER (ADMIN USE ONLY)
    // ---------------------------------------------------------
    @Override
    public String deleteCustomer(CustomerDTO customerDTO) throws QuickRentalException {

        Customer customer = customerRepository.findByDrivingLicenceNo(customerDTO.getDrivingLicenceNo())
                .orElseThrow(() -> new QuickRentalException("QuickVehicleService.CUSTOMER_NOT_FOUND"));

        String lic = customer.getDrivingLicenceNo();
        customerRepository.delete(customer);

        return lic;
    }

    // ---------------------------------------------------------
    // REGISTER CUSTOMER (optional in service, but keeping as required)
    // ---------------------------------------------------------
    @Override
    public String registerCustomer(CustomerDTO customerDTO) throws QuickRentalException {

        if (customerRepository.findByDrivingLicenceNo(customerDTO.getDrivingLicenceNo()).isPresent()) {
            throw new QuickRentalException("QuickVehicleService.customer.alreadyExists");
        }

        Customer c = new Customer();
        c.setCustomerName(customerDTO.getCustomerName());
        c.setDrivingLicenceNo(customerDTO.getDrivingLicenceNo());
        c.setAge(customerDTO.getAge());

        customerRepository.save(c);

        return c.getCustomerName();
    }

    // ---------------------------------------------------------
    // ADD VEHICLE
    // ---------------------------------------------------------
    @Override
    public String addVehicle(VehicleDTO dto) throws QuickRentalException {

        Vehicle v = new Vehicle();
        v.setVehicleNo(dto.getVehicleNo());
        v.setVehicleName(dto.getVehicleName());
        v.setPrice(dto.getPrice());

        try {
            v.setBookingStatus(BookingStatus.valueOf(dto.getBookingStatus().toUpperCase()));
        } catch (Exception e) {
            throw new QuickRentalException("Invalid booking status");
        }

        v.setVehicleType(dto.getVehicleType());

        vehicleRepository.save(v);
        return v.getVehicleNo();
    }
    
 // ---------------------------------------------------------
    // RENT VEHICLE
    // ---------------------------------------------------------
    @Override
    public String rentVehicle(RentalRequestDTO rentalRequestDTO) throws QuickRentalException {

        Customer customer = customerRepository.findById(rentalRequestDTO.getCustomerId())
                .orElseThrow(() -> new QuickRentalException("Customer not found."));

        Vehicle vehicle = vehicleRepository.findById(rentalRequestDTO.getVehicleNo())
                .orElseThrow(() -> new QuickRentalException("Vehicle not found."));

        LocalDate pickup = rentalRequestDTO.getPickUpDate() != null
                ? rentalRequestDTO.getPickUpDate()
                : LocalDate.now();

        LocalDate dropoff = rentalRequestDTO.getDropOffDate();

        if (dropoff == null) {
            throw new QuickRentalException("Drop-off date is required.");
        }

        // ---------------------------------------------
        // 1. Validate pickup < dropoff
        // ---------------------------------------------
        if (!dropoff.isAfter(pickup)) {
            throw new QuickRentalException("Drop-off date must be after pick-up date.");
        }

        // ---------------------------------------------
        // 2. Check overlapping bookings for this vehicle
        // ---------------------------------------------
        List<RentalHistory> overlapsForVehicle =
                rentalHistoryRepository.findOverlappingBookingsForVehicle(
                        vehicle.getVehicleNo(), pickup, dropoff);

        if (!overlapsForVehicle.isEmpty()) {
            throw new QuickRentalException("Vehicle is not available for selected dates.");
        }

        // ---------------------------------------------
        // 3. Check customer overlapping bookings
        //    (prevents customer renting 2 vehicles at same time)
        // ---------------------------------------------
        List<RentalHistory> overlapsForCustomer =
                rentalHistoryRepository.findOverlappingBookingsForCustomer(
                        customer.getCustomerId(), pickup, dropoff);

        if (!overlapsForCustomer.isEmpty()) {
            throw new QuickRentalException("Customer already has a booking during this period.");
        }

        // ---------------------------------------------
        // 4. Mark vehicle booked + save rental history
        // ---------------------------------------------
        vehicle.setBookingStatus(BookingStatus.BOOKED);
        vehicleRepository.save(vehicle);

        RentalHistory rental = new RentalHistory();
        rental.setCustomer(customer);
        rental.setVehicle(vehicle);
        rental.setPickUpDate(pickup);
        rental.setDropOffDate(dropoff);

        rentalHistoryRepository.save(rental);

        return "Vehicle " + vehicle.getVehicleNo() + " booked successfully.";
    }


    // ---------------------------------------------------------
    // RETURN VEHICLE
    // ---------------------------------------------------------
    @Override
    public String returnVehicle(ReturnRequestDTO dto) throws QuickRentalException {

        RentalHistory rental = rentalHistoryRepository
                .findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(
                        dto.getCustomerId(), dto.getVehicleNo())
                .orElseThrow(() -> new QuickRentalException("Rental record not found."));

        LocalDate actual = dto.getActualDropOffDate();
        if (actual == null) {
            throw new QuickRentalException("Drop-off date is required.");
        }

        if (actual.isBefore(rental.getPickUpDate())) {
            throw new QuickRentalException("Invalid return date.");
        }

        Vehicle v = rental.getVehicle();

        // If actual return is same or earlier → available
        if (!actual.isAfter(rental.getDropOffDate())) {
            v.setBookingStatus(BookingStatus.AVAILABLE);
            vehicleRepository.save(v);
        }

        rental.setDropOffDate(actual);
        rentalHistoryRepository.save(rental);

        return "Vehicle " + dto.getVehicleNo() + " returned successfully.";
    }


    // ---------------------------------------------------------
    // VIEW AVAILABLE VEHICLES
    // ---------------------------------------------------------
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

    @Override
    public List<VehicleDTO>searchAvailableVehicles(LocalDate startDate, LocalDate endDate) throws QuickRentalException{
    	 if (startDate == null || endDate == null) {
    	        throw new QuickRentalException("Start date and end date are required.");
    	    }

    	    if (!endDate.isAfter(startDate)) {
    	        throw new QuickRentalException("End date must be after start date.");
    	    }
    	    
    	    // 1. Auto free expired rentals
    	    List<RentalHistory> rentals = rentalHistoryRepository.findAll();

    	    for (RentalHistory r : rentals) {
    	        if (r.getDropOffDate().isBefore(LocalDate.now())) {
    	            Vehicle v = r.getVehicle();
    	            if (v.getBookingStatus() == BookingStatus.BOOKED) {
    	                v.setBookingStatus(BookingStatus.AVAILABLE);
    	                vehicleRepository.save(v);
    	            }
    	        }
    	    }

    	    // 2. Now perform your existing search logic
    	    List<Vehicle> vehicles = vehicleRepository.findByBookingStatus(BookingStatus.AVAILABLE);

    	    List<Vehicle> filtered = vehicles.stream()
    	        .filter(v -> {
    	            List<RentalHistory> history = rentalHistoryRepository.findByVehicle(v);
    	            return history.stream().noneMatch(h ->
    	                !(h.getDropOffDate().isBefore(startDate) || h.getPickUpDate().isAfter(endDate))
    	            );
    	        })
    	        .collect(Collectors.toList());

    	    return filtered.stream()
    	            .map(this::convertToDTO)
    	            .collect(Collectors.toList());
    }
    
    @Override
    public String extendBooking(ExtendBooking dto) throws QuickRentalException {
        if (dto.getCustomerId() == null || dto.getVehicleNo() == null || dto.getNewDropOffDate() == null) {
            throw new QuickRentalException("Invalid input for extension.");
        }

        // find latest rental for this customer & vehicle
        RentalHistory rental = rentalHistoryRepository
            .findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(dto.getCustomerId(), dto.getVehicleNo())
            .orElseThrow(() -> new QuickRentalException("Rental record not found for extension."));

        LocalDate currentDrop = rental.getDropOffDate();
        LocalDate newDrop = dto.getNewDropOffDate();

        if (!newDrop.isAfter(currentDrop)) {
            throw new QuickRentalException("New drop-off date must be after existing drop-off date.");
        }

        // Ensure newDrop is after pickUp
        if (!newDrop.isAfter(rental.getPickUpDate())) {
            throw new QuickRentalException("New drop-off date must be after pick-up date.");
        }

        // Check overlapping bookings for the vehicle in the extension window:
        // We want to check any booking that overlaps (currentDrop+1 .. newDrop)
        LocalDate checkFrom = currentDrop.plusDays(1);
        LocalDate checkTo = newDrop;

        List<RentalHistory> overlapsVehicle = rentalHistoryRepository.findOverlappingBookingsForVehicle(
                rental.getVehicle().getVehicleNo(), checkFrom, checkTo);

        // There might be the same rental (if repository query includes it) — filter current rental id out
        boolean otherOverlap = overlapsVehicle.stream()
                .anyMatch(r -> !r.getId().equals(rental.getId()));

        if (otherOverlap) {
            throw new QuickRentalException("Cannot extend: vehicle has another booking in requested extension period.");
        }

        // Check customer overlapping bookings in extension window
        List<RentalHistory> overlapsCustomer = rentalHistoryRepository.findOverlappingBookingsForCustomer(
                dto.getCustomerId(), checkFrom, checkTo);
        boolean customerOtherOverlap = overlapsCustomer.stream()
                .anyMatch(r -> !r.getId().equals(rental.getId()));

        if (customerOtherOverlap) {
            throw new QuickRentalException("Cannot extend: customer has another booking in requested extension period.");
        }

        // All good — extend
        rental.setDropOffDate(newDrop);
        rentalHistoryRepository.save(rental);

        // Ensure vehicle stays BOOKED
        Vehicle v = rental.getVehicle();
        v.setBookingStatus(BookingStatus.BOOKED);
        vehicleRepository.save(v);

        return "Booking extended successfully to " + newDrop.toString();
    }
    
    @Override
    public String cancelBooking(CancelBooking dto) throws QuickRentalException {
        if (dto.getCustomerId() == null || dto.getVehicleNo() == null) {
            throw new QuickRentalException("Invalid input for cancellation.");
        }

        RentalHistory rental = rentalHistoryRepository
            .findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(dto.getCustomerId(), dto.getVehicleNo())
            .orElseThrow(() -> new QuickRentalException("Rental record not found."));

        LocalDate today = LocalDate.now();

        // Allow cancel only if booking hasn't started yet (pickup in future)
        if (!today.isBefore(rental.getPickUpDate())) {
            throw new QuickRentalException("Cannot cancel booking that has already started or passed.");
        }

        // Delete the rental
        rentalHistoryRepository.delete(rental);

        // After deletion, check if vehicle has any other active or future booking.
        // If no bookings exist that overlap today or future, set vehicle AVAILABLE.
        Vehicle vehicle = rental.getVehicle();

        // find any rentals for this vehicle that overlap today or are in the future
        List<RentalHistory> futureOrActive = rentalHistoryRepository.findByVehicle(vehicle)
                .stream()
                .filter(r -> !r.getDropOffDate().isBefore(today)) // dropOff >= today => active or future
                .collect(Collectors.toList());

        if (futureOrActive.isEmpty()) {
            vehicle.setBookingStatus(BookingStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        return "Booking cancelled successfully.";
    }
    
    
    // Helper
    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleNo(vehicle.getVehicleNo());
        dto.setVehicleName(vehicle.getVehicleName());
        dto.setPrice(vehicle.getPrice());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setBookingStatus(vehicle.getBookingStatus().name());
        return dto;
    }

}
