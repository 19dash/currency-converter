package main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import main.model.CurrentRate;

@Repository
public interface CurrentRateRepository 
extends JpaRepository<CurrentRate, Long> {
	public CurrentRate findCurrentRateById(Long id);
	/*public CurrentRate 
	findCurrentRateByName(String currency);*/
}
