package com.sanjay.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanjay.entity.Customer;
import com.sanjay.entity.RentalHistory;
import com.sanjay.entity.Vehicle;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
    List<RentalHistory> findByCustomer(Customer customer);
    
    List<RentalHistory> findByVehicle(Vehicle vehicle);

    List<RentalHistory> findByDropOffDateBefore(LocalDate date);
    
    Optional<RentalHistory> findTopByCustomer_CustomerIdAndVehicle_VehicleNoOrderByIdDesc(Integer customerId, String vehicleNo);
    
    @Query("""
    	       SELECT r FROM RentalHistory r 
    	       WHERE r.vehicle.vehicleNo = :vehicleNo
    	       AND r.dropOffDate >= :pickup
    	       AND r.pickUpDate <= :dropoff
    	       """)
    List<RentalHistory> findOverlappingBookingsForVehicle(
            @Param("vehicleNo") String vehicleNo,
            @Param("pickup") LocalDate pickup,
            @Param("dropoff") LocalDate dropoff
    );
    
    @Query("""
    	       SELECT r FROM RentalHistory r 
    	       WHERE r.customer.customerId = :customerId
    	       AND r.dropOffDate >= :pickup
    	       AND r.pickUpDate <= :dropoff
    	       """)
    	List<RentalHistory> findOverlappingBookingsForCustomer(
    	        @Param("customerId") Integer customerId,
    	        @Param("pickup") LocalDate pickup,
    	        @Param("dropoff") LocalDate dropoff
    	);
    
    
    @Query("SELECT DISTINCT r.vehicle.vehicleNo FROM RentalHistory r " +
    	       "WHERE (:startDate <= r.dropOffDate AND :endDate >= r.pickUpDate)")
    	List<String> findBookedVehicleNosInRange(
    	        LocalDate startDate, LocalDate endDate);

}

