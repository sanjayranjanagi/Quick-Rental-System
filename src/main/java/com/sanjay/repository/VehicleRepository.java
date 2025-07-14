package com.sanjay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanjay.dto.BookingStatus;
import com.sanjay.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByBookingStatus(BookingStatus bookingStatus);

}

