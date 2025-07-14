package com.sanjay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.sanjay.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

@Query("select c from Customer c where c.drivingLicenceNo = :drivingLicenceNo")
Optional<Customer> findByDrivingLicenceNo(@Param("drivingLicenceNo") String drivingLicenceNo);
}

