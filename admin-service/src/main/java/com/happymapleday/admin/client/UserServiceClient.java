package com.happymapleday.admin.client;

import com.happymapleday.admin.dto.external.UserServiceUserMetricsDto;
import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "user-service", url = "${services.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/user/admin/metrics/normal-users")
    ApiResponse<UserServiceUserMetricsDto> getNormalUsersMetrics(
        @RequestParam(required = false) String period,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );
}

