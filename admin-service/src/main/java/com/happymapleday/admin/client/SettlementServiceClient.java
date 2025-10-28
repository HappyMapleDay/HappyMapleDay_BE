package com.happymapleday.admin.client;

import com.happymapleday.admin.dto.external.SettlementBossKillDto;
import com.happymapleday.admin.dto.external.SettlementCombatPowerDto;
import com.happymapleday.admin.dto.external.SettlementItemDropDto;
import com.happymapleday.admin.dto.external.SettlementItemPriceDto;
import com.happymapleday.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "settlement-service", url = "${services.settlement-service.url}")
public interface SettlementServiceClient {

    @GetMapping("/api/settlement/admin/metrics/boss/kills/time-series")
    ApiResponse<List<SettlementBossKillDto>> getBossKillsTimeSeries(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) String range
    );

    @GetMapping("/api/settlement/admin/metrics/boss/hardness/avg-combat-power")
    ApiResponse<List<SettlementCombatPowerDto>> getBossAvgCombatPower(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    );

    @GetMapping("/api/settlement/admin/metrics/item/drops/summary")
    ApiResponse<List<SettlementItemDropDto>> getItemDropsSummary(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    );

    @GetMapping("/api/settlement/admin/metrics/item/average-price/summary")
    ApiResponse<List<SettlementItemPriceDto>> getItemAveragePriceSummary(
        @RequestParam(required = false) Long bossId,
        @RequestParam(required = false) Long itemId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    );
}

