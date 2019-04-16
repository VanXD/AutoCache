package com.vanxd.autocache.example.entity;

import com.vanxd.autocache.core.entity.BaseEntity;

public class TestDemo extends BaseEntity {
    private String a;
    private Integer b;
    private Boolean c;
    private Long testaId;
    private String testaName;

    public Long getTestaId() {
        return testaId;
    }

    public void setTestaId(Long testaId) {
        this.testaId = testaId;
    }

    public String getTestaName() {
        return testaName;
    }

    public void setTestaName(String testaName) {
        this.testaName = testaName;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Boolean getC() {
        return c;
    }

    public void setC(Boolean c) {
        this.c = c;
    }
}
