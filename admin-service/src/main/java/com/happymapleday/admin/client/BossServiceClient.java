package com.happymapleday.admin.client;

import com.happymapleday.admin.dto.external.BossInfoDto;
import com.happymapleday.admin.dto.external.BossItemInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "boss-service", url = "${services.boss-service.url}")
public interface BossServiceClient {

    @GetMapping("/api/boss/admin/{id}")
    BossInfoDto getBossInfo(@PathVariable("id") Long id);

    @GetMapping("/api/boss/admin/items/{id}")
    BossItemInfoDto getItemInfo(@PathVariable("id") Long id);
}

