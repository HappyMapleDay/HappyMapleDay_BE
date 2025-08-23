package com.happymapleday.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomBoxItemResponse {
    private Long randomBoxItemId;
    private String dropItemName;
    private String dropItemNameEn;
    private Integer dropItemLevel;
    private String fullDropItemName;
    private Boolean hasDropLevel;
}


