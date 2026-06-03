package com.parking;

import com.parking.presentation.cli.ParkingCli;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParkingApplication implements CommandLineRunner {

    private final ParkingCli cli;

    public ParkingApplication(ParkingCli cli) {
        this.cli = cli;
    }

    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        cli.start();
    }

}
