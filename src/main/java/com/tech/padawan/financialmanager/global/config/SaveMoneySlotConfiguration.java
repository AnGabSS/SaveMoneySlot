package com.tech.padawan.financialmanager.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("save-money-slot")
public record SaveMoneySlotConfiguration(String secret_key, String jwt_issuer, String front_url) {
}
