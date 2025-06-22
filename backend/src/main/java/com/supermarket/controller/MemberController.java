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
            
            Member member = memberService.getMemberByPhone(phone);
            
            if (member != null) {
                System.out.println("✅ 找到会员: " + member.getMemberName());
                return Result.success("查询成功", member);
            } else {
                return Result.error("未找到该手机号对应的会员");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 会员积分操作（增加或扣减）
     */
    @PostMapping("/{id}/points/operation")
    public Result<Member> pointsOperation(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request) {
        try {
            String operation = (String) request.get("operation");
            Integer points = null;
            
            // 处理points参数的类型转换
            Object pointsObj = request.get("points");
            if (pointsObj instanceof Integer) {
                points = (Integer) pointsObj;
            } else if (pointsObj instanceof Double) {
                points = ((Double) pointsObj).intValue();
            } else if (pointsObj instanceof String) {
                points = Integer.parseInt((String) pointsObj);
            }
            
            String remark = (String) request.get("remark");
            
            System.out.println("💎 会员积分操作: " + id + ", 操作: " + operation + ", 积分: " + points);
            
            if (points == null || points <= 0) {
                return Result.error("积分数量必须大于0");
            }
            
            Member member = memberService.getById(id);
            if (member == null) {
                return Result.error("会员不存在");
            }
            
            Integer currentPoints = member.getPoints() != null ? member.getPoints() : 0;
            Integer newPoints;
            
            if ("add".equals(operation)) {
                newPoints = currentPoints + points;
            } else if ("subtract".equals(operation)) {
                newPoints = Math.max(0, currentPoints - points); // 积分不能为负
            } else {
                return Result.error("无效的操作类型，只支持add或subtract");
            }
            
            boolean success = memberService.updateMemberPoints(id, newPoints);
            if (success) {
                Member updatedMember = memberService.getById(id);
                System.out.println("✅ 积分操作成功，当前积分: " + newPoints);
                return Result.success("积分操作成功", updatedMember);
            } else {
                return Result.error("积分操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("积分操作失败: " + e.getMessage());
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