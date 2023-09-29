package main.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import main.model.RateDynamic;

@Repository
public interface RateDynamicRepository extends 
JpaRepository<RateDynamic, Long> {
	@Query(value="select * from "
			+ "rate_dynamic where date >= :d1 "
			+ "and date <= :d2 and current_rate_id"
			+ " = :currentRateId", 
			nativeQuery=true)
	List<RateDynamic> 
	findPeriod(LocalDate d1,
			LocalDate d2, Long currentRateId);
}
