package com.example.slj.gradeadministration.domain;

public class Teachinfo {
    private Integer id;

    private String tid;

    private Integer cid;

    private String classno;

    private Integer addgrade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getClassno() {
        return classno;
    }

    public void setClassno(String classno) {
        this.classno = classno;
    }

    public Integer getAddgrade() {
        return addgrade;
    }

    public void setAddgrade(Integer addgrade) {
        this.addgrade = addgrade;
    }
}