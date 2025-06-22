package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

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
    private Integer points;
    private Double totalConsumption;
    private String memberLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}