package com.happymapleday.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NexonCharacterSummaryDto {

    private final String ocid;
    private final String character_name;
    private final String character_class;
    private final String world_name;
    private final Integer character_level;
    private final String character_image;

    public static NexonCharacterSummaryDto from(JsonNode basicInfo, String ocid) {
        String nickname = getTextSafely(basicInfo, "character_name");
        String job = getTextSafely(basicInfo, "character_class");
        String server = getTextSafely(basicInfo, "world_name");
        Integer level = getIntSafely(basicInfo, "character_level");
        String imageUrl = getTextSafely(basicInfo, "character_image");

        return NexonCharacterSummaryDto.builder()
                .ocid(ocid)
                .character_name(nickname)
                .character_class(job)
                .world_name(server)
                .character_level(level)
                .character_image(imageUrl)
                .build();
    }

    private static String getTextSafely(JsonNode node, String field) {
        JsonNode value = node != null ? node.get(field) : null;
        return value != null && !value.isNull() ? value.asText() : null;
    }

    private static Integer getIntSafely(JsonNode node, String field) {
        JsonNode value = node != null ? node.get(field) : null;
        return value != null && value.canConvertToInt() ? value.asInt() : null;
    }
}


