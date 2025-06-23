package com.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.entity.Member;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.service.MemberService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员管理控制器
 */
@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 分页查询会员
     */
    @GetMapping("/page")
    public Result<IPage<Member>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String memberLevel) {
        try {
            System.out.println("👥 分页查询会员 - 页码:" + current + ", 大小:" + size);
            System.out.println("🔍 查询条件 - 姓名:" + memberName + ", 手机:" + phone + ", 等级:" + memberLevel);
            
            Page<Member> page = new Page<>(current, size);
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            
            if (memberName != null && !memberName.trim().isEmpty()) {
                queryWrapper.like("member_name", memberName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                queryWrapper.like("phone", phone.trim());
            }
            if (memberLevel != null && !memberLevel.trim().isEmpty()) {
                queryWrapper.eq("member_level", memberLevel.trim());
            }
            
            queryWrapper.eq("status", "active");
            queryWrapper.orderByDesc("created_at");
            
            IPage<Member> memberPage = memberService.page(page, queryWrapper);
            
            System.out.println("✅ 查询成功，共" + memberPage.getTotal() + "条记录");
            return Result.success(memberPage);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取会员
     */
    @GetMapping("/{id}")
    public Result<Member> getMemberById(@PathVariable Long id) {
        try {
            Member member = memberService.getMemberById(id);
            if (member != null) {
                return Result.success(member);
            } else {
                return Result.error("会员不存在");
            }
        } catch (Exception e) {
            return Result.error("获取会员信息失败: " + e.getMessage());
        }
    }

    /**
     * 添加会员 - 移除所有权限检查
     */
    @PostMapping()
    public Result<Member> create(@RequestBody Member member) {
        try {
            System.out.println("🎯 MemberController.create 被调用");
            System.out.println("📋 接收到的会员数据: " + member);
            // 基础数据验证
            if (member.getMemberName() == null || member.getMemberName().trim().isEmpty()) {
                System.out.println("❌ 会员姓名为空");
                return Result.error("会员姓名不能为空");
            }
            if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
                System.out.println("❌ 手机号为空");
                return Result.error("手机号不能为空");
            }
            // 检查手机号是否已存在
            QueryWrapper<Member> checkQuery = new QueryWrapper<>();
            checkQuery.eq("phone", member.getPhone());
            long phoneCount = memberService.count(checkQuery);
            if (phoneCount > 0) {
                System.out.println("❌ 手机号已存在: " + member.getPhone());
                return Result.error("手机号已存在");
            }
            
            System.out.println("✅ 数据验证通过，开始保存会员");
            member.setMemberCode("M" + System.currentTimeMillis());
            boolean success = memberMapper.insertMember(member);
            
            if (success) {
                System.out.println("✅ 会员创建成功，ID: " + member.getMemberId());
                return Result.success("创建成功", member);
            } else {
                System.out.println("❌ 会员创建失败");
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            System.err.println("❌ MemberController.create 异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新会员信息
     */
    @PutMapping("/{id}")
    public Result<Member> update(@PathVariable Long id, @RequestBody Member member) {
        try {
            member.setMemberId(id);
            member.setUpdatedAt(LocalDateTime.now());
            
            boolean success = memberService.updateById(member);
            if (success) {
                return Result.success("更新成功", member);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = memberService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据手机号查询会员 - 收银台专用
     */
    @GetMapping("/phone/{phone}")
    public Result<Member> getMemberByPhone(@PathVariable String phone) {
        try {
            System.out.println("📱 根据手机号查询会员: " + phone);
            
            // 手机号格式验证
            if (phone == null || phone.trim().isEmpty()) {
                return Result.error("手机号不能为空");
            }
            
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return Result.error("手机号格式不正确");
            }
            
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone.trim());
            queryWrapper.eq("status", "active");
            
            Member member = memberService.getOne(queryWrapper);
            
            if (member != null) {
                System.out.println("✅ 找到会员: " + member.getMemberName() + 
                    ", 等级: " + member.getMemberLevel() + 
                    ", 积分: " + member.getPoints());
                return Result.success("查询成功", member);
            } else {
                System.out.println("❌ 未找到手机号对应的会员: " + phone);
                return Result.error("未找到该手机号对应的会员");
            }
        } catch (Exception e) {
            System.err.println("❌ 查询会员失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 计算会员折扣
     */
    @PostMapping("/calculate-discount")
    public Result<Map<String, Object>> calculateDiscount(@RequestBody Map<String, Object> request) {
        try {
            Long memberId = Long.valueOf(request.get("memberId").toString());
            Double totalAmount = Double.valueOf(request.get("totalAmount").toString());
            
            System.out.println("💰 计算会员折扣 - 会员ID: " + memberId + ", 金额: " + totalAmount);
            
            Member member = memberService.getById(memberId);
            if (member == null) {
                return Result.error("会员不存在");
            }
            
            // 计算折扣率
            double discountRate = 0.0;
            String memberLevelName = "";
            
            switch (member.getMemberLevel()) {
                case "diamond":
                    discountRate = 0.15;
                    memberLevelName = "钻石会员";
                    break;
                case "gold":
                    discountRate = 0.10;
                    memberLevelName = "金卡会员";
                    break;
                case "silver":
                    discountRate = 0.05;
                    memberLevelName = "银卡会员";
                    break;
                case "bronze":
                default:
                    discountRate = 0.0;
                    memberLevelName = "普通会员";
                    break;
            }
            
            // 计算折扣
            double discountAmount = totalAmount * discountRate;
            double finalAmount = totalAmount - discountAmount;
            
            Map<String, Object> discountInfo = new HashMap<>();
            discountInfo.put("memberId", memberId);
            discountInfo.put("memberName", member.getMemberName());
            discountInfo.put("memberLevel", member.getMemberLevel());
            discountInfo.put("memberLevelName", memberLevelName);
            discountInfo.put("discountRate", discountRate);
            discountInfo.put("discountPercentage", discountRate * 100);
            discountInfo.put("totalAmount", totalAmount);
            discountInfo.put("discountAmount", discountAmount);
            discountInfo.put("finalAmount", finalAmount);
            discountInfo.put("savedAmount", discountAmount);
            
            System.out.println("✅ 折扣计算完成 - 原价: " + totalAmount + 
                ", 折扣: " + (discountRate * 100) + "%" +
                ", 优惠: " + discountAmount + 
                ", 实付: " + finalAmount);
            
            return Result.success("计算成功", discountInfo);
        } catch (Exception e) {
            System.err.println("❌ 计算折扣失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("计算失败: " + e.getMessage());
        }
    }

    /**
     * 获取会员等级列表
     */
    @GetMapping("/levels")
    public Result<List<Map<String, String>>> getMemberLevels() {
        try {
            System.out.println("📊 获取会员等级列表");
            
            List<Map<String, String>> levels = new ArrayList<>();
            levels.add(Map.of("value", "bronze", "label", "普通会员"));
            levels.add(Map.of("value", "silver", "label", "银卡会员"));
            levels.add(Map.of("value", "gold", "label", "金卡会员"));
            levels.add(Map.of("value", "diamond", "label", "钻石会员"));
            
            return Result.success("获取成功", levels);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取会员统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getMemberStatistics() {
        try {
            System.out.println("📊 获取会员统计信息");
            
            Map<String, Object> statistics = memberService.getMemberStatistics();
            return Result.success("获取成功", statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取消费排行榜
     */
    @GetMapping("/top-spending")
    public Result<List<Member>> getTopSpendingMembers(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("🏆 获取消费排行榜，限制数量: " + limit);
            
            List<Member> topMembers = memberService.getTopSpendingMembers(limit);
            return Result.success("查询成功", topMembers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近注册的会员
     */
    @GetMapping("/recent")
    public Result<List<Member>> getRecentRegisteredMembers(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("🕒 获取最近注册会员，限制数量: " + limit);
            
            List<Member> recentMembers = memberService.getRecentRegisteredMembers(limit);
            return Result.success("查询成功", recentMembers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }    }
    /**
     * 测试会员创建接口
     */
    @PostMapping("/test-create")
    public Result<String> testCreate(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("🧪 测试创建会员接口被调用");
            System.out.println("📋 接收数据: " + data);
            return Result.success("测试接口正常工作");
        } catch (Exception e) {
            System.err.println("❌ 测试接口异常: " + e.getMessage());
            return Result.error("测试接口异常: " + e.getMessage());
        }
    }
}