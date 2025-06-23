package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员实体类
 */
@Data
@TableName("members")
public class Member {
    @TableId(type = IdType.AUTO)
    private Long memberId;
    
    private String memberCode;
    
    private String memberName;
    
    private String phone;
    
    private String email;
    
    private Integer points;
    
    private Double totalConsumption;
    
    private String memberLevel;
    
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 根据会员等级获取折扣率
    public double getDiscountRate() {
        switch (this.memberLevel) {
            case "diamond":
                return 0.15; // 钻石会员15%折扣
            case "gold":
                return 0.10; // 金卡会员10%折扣
            case "silver":
                return 0.05; // 银卡会员5%折扣
            case "bronze":
            default:
                return 0.0; // 普通会员无折扣
        }
    }
    
    // 获取会员等级显示名称
    public String getMemberLevelName() {
        switch (this.memberLevel) {
            case "diamond":
                return "钻石会员";
            case "gold":
                return "金卡会员";
            case "silver":
                return "银卡会员";
            case "bronze":
            default:
                return "普通会员";
        }
    }
    
    // 构造方法
    public Member() {
        this.points = 0;
        this.totalConsumption = 0.0;
        this.memberLevel = "bronze";
        this.status = "active";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Member(String memberName, String phone) {
        this();
        this.memberName = memberName;
        this.phone = phone;
        this.memberCode = "M" + System.currentTimeMillis();
    }
}