package com.tech.padawan.financialmanager;

import com.tech.padawan.financialmanager.global.config.SaveMoneySlotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SaveMoneySlotConfiguration.class)
public class FinancialmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialmanagerApplication.class, args);
	}

}
