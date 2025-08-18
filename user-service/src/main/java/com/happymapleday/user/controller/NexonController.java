package com.happymapleday.user.controller;

import com.happymapleday.common.dto.ApiResponse;
import com.happymapleday.user.dto.NexonCharacterSummaryDto;
import com.happymapleday.user.service.NexonProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nexon")
@RequiredArgsConstructor
public class NexonController {

    private final NexonProxyService nexonProxyService;

    // 캐릭터 목록 - 캐시 우선
    @GetMapping("/characters")
    public ResponseEntity<ApiResponse<List<NexonCharacterSummaryDto>>> getCharacters() {
        List<NexonCharacterSummaryDto> result = nexonProxyService.getUserCharacters(false);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 캐릭터 목록 리프레시 - 쿨타임 60초
    @PostMapping("/characters/refresh")
    public ResponseEntity<ApiResponse<List<NexonCharacterSummaryDto>>> refreshCharacters() {
        List<NexonCharacterSummaryDto> result = nexonProxyService.getUserCharacters(true);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 개별 캐릭터 엔드포인트는 요구 사항에 따라 제공하지 않음
}


