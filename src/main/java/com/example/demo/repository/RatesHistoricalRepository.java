package com.example.demo.repository;

import com.example.demo.dbmodel.RateHistorical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;

@Repository
public interface RatesHistoricalRepository extends JpaRepository<RateHistorical, Long> {
    @Query(value = "SELECT DISTINCT timestamp FROM gateway.rate WHERE timestamp >= NOW() - make_interval(hours => :period)", nativeQuery = true)
    ArrayList<Instant> getLatestRateTimestamps(@Param("period") int period);
}
