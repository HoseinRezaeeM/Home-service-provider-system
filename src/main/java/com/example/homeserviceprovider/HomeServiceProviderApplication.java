package com.example.homeserviceprovider;

import com.example.homeserviceprovider.service.Impl.CustomerServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class HomeServiceProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeServiceProviderApplication.class, args);

	}

}
