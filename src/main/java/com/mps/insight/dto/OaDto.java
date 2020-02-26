package com.mps.insight.dto;

public class OaDto {
	String titleId;
    String titleName;
    String dataType;
    int totalUses;
    int oaUses;
    int suscribOa;

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }
 
    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getTotalUses() {
        return totalUses;
    }

    public void setTotalUses(int totalUses) {
        this.totalUses = totalUses;
    }

    public int getOaUses() {
        return oaUses;
    }

    public void setOaUses(int oaUses) {
        this.oaUses = oaUses;
    }

    public int getSuscribOa() {
        return suscribOa;
    }

    public void setSuscribOa(int suscribOa) {
        this.suscribOa = suscribOa;
    }

}
