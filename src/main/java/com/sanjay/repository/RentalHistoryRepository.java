package com.sanjay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanjay.entity.Customer;
import com.sanjay.entity.RentalHistory;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
    List<RentalHistory> findByCustomer(Customer customer);
    
    Optional<RentalHistory> findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(Integer customerId, String vehicleNo);

}

