package com.happymapleday.settlement.dto.response;

import java.math.BigInteger;

public class BossRecordDetailResponse {
    
    private String characterName;
    private String bossName;
    private String difficulty;
    private Integer partySize;
    private BigInteger crystalIncome;
    private BigInteger desireItemIncome;
    private BigInteger totalIncome;
    
    // 기본 생성자
    public BossRecordDetailResponse() {}
    
    // 생성자
    public BossRecordDetailResponse(String characterName, String bossName, String difficulty,
                                  Integer partySize, BigInteger crystalIncome, 
                                  BigInteger desireItemIncome, BigInteger totalIncome) {
        this.characterName = characterName;
        this.bossName = bossName;
        this.difficulty = difficulty;
        this.partySize = partySize;
        this.crystalIncome = crystalIncome;
        this.desireItemIncome = desireItemIncome;
        this.totalIncome = totalIncome;
    }
    
    // Getter/Setter
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
    
    public String getBossName() {
        return bossName;
    }
    
    public void setBossName(String bossName) {
        this.bossName = bossName;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public Integer getPartySize() {
        return partySize;
    }
    
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    
    public BigInteger getCrystalIncome() {
        return crystalIncome;
    }
    
    public void setCrystalIncome(BigInteger crystalIncome) {
        this.crystalIncome = crystalIncome;
    }
    
    public BigInteger getDesireItemIncome() {
        return desireItemIncome;
    }
    
    public void setDesireItemIncome(BigInteger desireItemIncome) {
        this.desireItemIncome = desireItemIncome;
    }
    
    public BigInteger getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigInteger totalIncome) {
        this.totalIncome = totalIncome;
    }
} 