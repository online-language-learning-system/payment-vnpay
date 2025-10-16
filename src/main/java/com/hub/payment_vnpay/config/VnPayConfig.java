package com.hub.payment_vnpay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VnPayConfig {

    @Value("${vnp.tmnCode}") private String tmnCode;
    @Value("${vnp.hashSecret}") private String hashSecret;
    @Value("${vnp.apiUrl}") private String apiUrl;
    @Value("${vnp.returnUrl}") private String returnUrl;
    @Value("${vnp.ipnUrl}") private String ipnUrl;
    @Value("${vnp.version}") private String version;
    @Value("${vnp.command}") private String command;
    @Value("${vnp.locale}") private String locale;
    @Value("${vnp.currency}") private String currency;
    @Value("${vnp.bankCode}") private String bankCode;

    public String getTmnCode() { return tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public String getApiUrl() { return apiUrl; }
    public String getReturnUrl() { return returnUrl; }
    public String getIpnUrl() { return ipnUrl; }
    public String getVersion() { return version; }
    public String getCommand() { return command; }
    public String getLocale() { return locale; }
    public String getCurrency() { return currency; }
    public String getBankCode() { return bankCode; }
}

