package com.parking.domain.service;

import com.parking.domain.valueObject.HourlyRate;
import com.parking.domain.valueObject.Money;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingPricingService {

    private static final HourlyRate STANDARD_RATE = new HourlyRate(2.0);

    public Money calculateCost(LocalDateTime entryTime, LocalDateTime exitTime) {
        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
        if (hours < 1) {
            hours = 1;
        }
        return STANDARD_RATE.calculateCost(hours);
    }

    public HourlyRate getStandardRate() {
        return STANDARD_RATE;
    }

}
