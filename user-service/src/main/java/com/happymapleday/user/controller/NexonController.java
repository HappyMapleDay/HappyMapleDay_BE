package com.happymapleday.user.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.user.dto.NexonCharacterSummaryDto;
import com.happymapleday.user.service.NexonProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/user/nexon")
@RequiredArgsConstructor
public class NexonController {

    private final NexonProxyService nexonProxyService;

    // 캐릭터 목록 - 캐시 우선
    @GetMapping("/characters")
    public Mono<ResponseEntity<ApiResponse<List<NexonCharacterSummaryDto>>>> getCharacters() {
        return Mono.fromCallable(() -> nexonProxyService.getUserCharacters(false))
                .map(list -> ResponseEntity.ok(ApiResponse.success(list)));
    }

    // 캐릭터 목록 리프레시 - 쿨타임 60초
    @PostMapping("/characters/refresh")
    public Mono<ResponseEntity<ApiResponse<List<NexonCharacterSummaryDto>>>> refreshCharacters() {
        return Mono.fromCallable(() -> nexonProxyService.getUserCharacters(true))
                .map(list -> ResponseEntity.ok(ApiResponse.success(list)));
    }

    // 개별 캐릭터 엔드포인트는 요구 사항에 따라 제공하지 않음
}


