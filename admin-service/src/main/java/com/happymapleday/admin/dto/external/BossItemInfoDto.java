package com.happymapleday.admin.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BossItemInfoDto {
    private Long adminItemId;
    private String itemName;
    private String itemNameEn;
    private Boolean isRandomBox;
}

