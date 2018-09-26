package com.edmi.entity.korea_comments;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 2018年9月18日
 * korea_comments_target_brands 模型
 */
@Entity
@Table(name = "korea_comments_target_brands")
public class Korea_comments_target_brands {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk_id;

    @Column(nullable = false, name = "brand_index")
    private String brand_index;

    @Column(nullable = false, name = "brand_status")
    private String brand_status;

    @Column(nullable = false, name = "brand_name")
    private String brand_name;

    @Column(nullable = false, name = "insert_Time")
    private Timestamp insertTime;

    @Column(nullable = false, name = "update_Time")
    private Timestamp updateTime;

    public Long getPk_id() {
        return pk_id;
    }

    public void setPk_id(Long pk_id) {
        this.pk_id = pk_id;
    }

    public String getBrand_index() {
        return brand_index;
    }

    public void setBrand_index(String brand_index) {
        this.brand_index = brand_index;
    }

    public String getBrand_status() {
        return brand_status;
    }

    public void setBrand_status(String brand_status) {
        this.brand_status = brand_status;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
