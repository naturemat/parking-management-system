package com.parking.infrastructure.config;

import com.parking.domain.service.ParkingPricingService;
import com.parking.domain.service.RevenueService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ParkingPricingService parkingPricingService() {
        return new ParkingPricingService();
    }

    @Bean
    public RevenueService revenueService() {
        return new RevenueService();
    }

}
