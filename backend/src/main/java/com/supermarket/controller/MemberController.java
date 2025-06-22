package com.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.entity.Member;
import com.supermarket.service.MemberService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // 分页查询会员
    @GetMapping("/page")
    public Result<IPage<Member>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String phone) {
        try {
            System.out.println("👥 分页查询会员 - 页码:" + current + ", 大小:" + size);
            
            Page<Member> page = new Page<>(current, size);
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            
            if (memberName != null && !memberName.trim().isEmpty()) {
                queryWrapper.like("member_name", memberName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                queryWrapper.like("phone", phone.trim());
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
     * 获取所有会员
     */
    @GetMapping
    public Result<List<Member>> getAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();
            return Result.success(members);
        } catch (Exception e) {
            return Result.error("获取会员列表失败: " + e.getMessage());
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
     * 添加会员
     */
    @PostMapping
    public Result<Member> create(@RequestBody Member member) {
        try {
            System.out.println("创建会员: " + member.getMemberName());
            
            // 检查手机号是否已存在
            QueryWrapper<Member> checkQuery = new QueryWrapper<>();
            checkQuery.eq("phone", member.getPhone());
            long phoneCount = memberService.count(checkQuery);
            if (phoneCount > 0) {
                return Result.error("手机号已存在");
            }
            
            // 生成会员卡号
            String memberCode = "M" + System.currentTimeMillis();
            member.setMemberCode(memberCode);
            member.setStatus("active");
            member.setPoints(0);
            member.setTotalConsumption(0.0);
            member.setMemberLevel("普通会员");
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
            
            boolean success = memberService.save(member);
            if (success) {
                System.out.println("✅ 会员创建成功，卡号: " + memberCode);
                return Result.success("创建成功", member);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
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

    // 获取所有会员列表
    @GetMapping("/list")
    public Result<List<Member>> getList() {
        try {
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "active");
            queryWrapper.orderByDesc("created_at");
            
            List<Member> members = memberService.list(queryWrapper);
            return Result.success(members);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }



    // 更新会员积分
    @PutMapping("/{id}/points")
    public Result<Member> updatePoints(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer points = request.get("points");
            System.out.println("💎 更新会员积分:" + id + ", 积分:" + points);
            
            boolean success = memberService.updateMemberPoints(id, points);
            if (success) {
                Member updatedMember = memberService.getById(id);
                return Result.success("积分更新成功", updatedMember);
            } else {
                return Result.error("积分更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("积分更新失败: " + e.getMessage());
        }
    }

    // 会员消费记录
    @GetMapping("/{id}/consumption")
    public Result<List<Map<String, Object>>> getConsumptionHistory(@PathVariable Long id) {
        try {
            System.out.println("📊 查询会员消费记录:" + id);
            
            // 这里可以实现具体的消费记录查询逻辑
            // 暂时返回空列表
            List<Map<String, Object>> consumptionHistory = new ArrayList<>();
            
            return Result.success(consumptionHistory);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取消费排行榜
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

    // 获取最近注册的会员
    @GetMapping("/recent")
    public Result<List<Member>> getRecentRegisteredMembers(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            System.out.println("🕒 获取最近注册会员，限制数量: " + limit);
            
            List<Member> recentMembers = memberService.getRecentRegisteredMembers(limit);
            return Result.success("查询成功", recentMembers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}