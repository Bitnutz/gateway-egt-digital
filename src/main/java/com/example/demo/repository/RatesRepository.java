package com.example.demo.repository;

import com.example.demo.dbmodel.Rate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface RatesRepository extends JpaRepository<Rate, String> {
    @Query(value = "SELECT * FROM gateway.rate WHERE currency_name = :currency AND delete_flag = false", nativeQuery = true)
    ArrayList<Rate> getLatestRates(@Param("currency") String currency);

    @Modifying
    @Transactional
    @Query(value = "UPDATE gateway.rate SET delete_flag = true", nativeQuery = true)
    int deleteOldRates();

    @Query(value = "SELECT * FROM gateway.rate WHERE currency_name = :currency AND timestamp in :timestamps", nativeQuery = true)
    ArrayList<Rate> getRatesHistoryByTimestamp(@Param("currency") String currency, @Param("timestamps") List<Instant> timestamps);

}
