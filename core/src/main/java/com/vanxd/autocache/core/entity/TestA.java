package com.vanxd.autocache.core.entity;

public class TestA extends BaseEntity {
    private String testaName;
    private Integer testaInteger;
    private Boolean testaBoolean;

    public String getTestaName() {
        return testaName;
    }

    public void setTestaName(String testaName) {
        this.testaName = testaName;
    }

    public Integer getTestaInteger() {
        return testaInteger;
    }

    public void setTestaInteger(Integer testaInteger) {
        this.testaInteger = testaInteger;
    }

    public Boolean getTestaBoolean() {
        return testaBoolean;
    }

    public void setTestaBoolean(Boolean testaBoolean) {
        this.testaBoolean = testaBoolean;
    }
}
