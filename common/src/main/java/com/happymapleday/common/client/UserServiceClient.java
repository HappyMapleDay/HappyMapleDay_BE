package com.happymapleday.common.client;

import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${services.user-service.url:http://localhost:8082}")
public interface UserServiceClient {
    
    @PostMapping("/api/auth/login")
    ApiResponse<Object> login(@RequestBody Object loginRequest);
    
    @PostMapping("/api/auth/register")
    ApiResponse<Object> register(@RequestBody Object registerRequest);
    
    @GetMapping("/api/user/{userId}")
    ApiResponse<Object> getUserInfo(@PathVariable Long userId);
    
    @GetMapping("/api/user/auto-settlement-enabled")
    ApiResponse<Object> getAutoSettlementEnabledUsers();
} 