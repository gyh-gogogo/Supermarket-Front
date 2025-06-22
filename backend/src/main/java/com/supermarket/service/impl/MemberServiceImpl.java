package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Member;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public List<Member> getAllMembers() {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public Member getMemberById(Long memberId) {
        return this.getById(memberId);
    }

    @Override
    public boolean addMember(Member member) {
        try {
            System.out.println("💾 MemberServiceImpl.addMember 开始执行");
            System.out.println("📋 会员信息: " + member);
            
            // 设置默认值
            member.setMemberCode("M" + System.currentTimeMillis());
            member.setStatus("active");
            if (member.getPoints() == null) {
                member.setPoints(0);
            }
            if (member.getTotalConsumption() == null) {
                member.setTotalConsumption(0.0);
            }
            if (member.getMemberLevel() == null || member.getMemberLevel().trim().isEmpty()) {
                member.setMemberLevel("bronze");
            }
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
            
            System.out.println("💾 准备保存会员数据: " + member);
            boolean result = this.save(member);
            System.out.println("✅ 会员保存结果: " + result);
            
            return result;
        } catch (Exception e) {
            System.err.println("❌ MemberServiceImpl.addMember 失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateMember(Member member) {
        member.setUpdatedAt(LocalDateTime.now());
        return this.updateById(member);
    }

    @Override
    public boolean deleteMember(Long memberId) {
        return this.removeById(memberId);
    }

    @Override
    public List<Member> getMembersByLevel(String memberLevel) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_level", memberLevel);
        queryWrapper.eq("status", "active");
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateMemberPoints(Long memberId, Integer points) {
        try {
            Member member = new Member();
            member.setMemberId(memberId);
            member.setPoints(points);
            member.setUpdatedAt(LocalDateTime.now());
            return this.updateById(member);
        } catch (Exception e) {
            System.err.println("❌ 更新积分失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addMemberPoints(Long memberId, Integer pointsToAdd) {
        Member existingMember = this.getById(memberId);
        if (existingMember != null) {
            Integer currentPoints = existingMember.getPoints() != null ? existingMember.getPoints() : 0;
            Integer newPoints = currentPoints + pointsToAdd;
            
            Member updateMember = new Member();
            updateMember.setMemberId(memberId);
            updateMember.setPoints(newPoints);
            updateMember.setUpdatedAt(LocalDateTime.now());
            
            return this.updateById(updateMember);
        }
        return false;
    }

    @Override
    public boolean updateMemberConsumption(Long memberId, Double amount) {
        Member existingMember = this.getById(memberId);
        if (existingMember != null) {
            Double currentConsumption = existingMember.getTotalConsumption() != null ? existingMember.getTotalConsumption() : 0.0;
            Double newConsumption = currentConsumption + amount;
            
            Member updateMember = new Member();
            updateMember.setMemberId(memberId);
            updateMember.setTotalConsumption(newConsumption);
            updateMember.setUpdatedAt(LocalDateTime.now());
            
            // 根据消费金额自动升级会员等级
            String newLevel = calculateMemberLevel(newConsumption);
            updateMember.setMemberLevel(newLevel);
            
            return this.updateById(updateMember);
        }
        return false;
    }

    @Override
    public Map<String, Object> getMemberStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总会员数
        QueryWrapper<Member> totalQuery = new QueryWrapper<>();
        totalQuery.eq("status", "active");
        int totalMembers = Math.toIntExact(this.count(totalQuery));
        
        // 各等级会员数
        int bronzeCount = getMemberCountByLevel("bronze");
        int silverCount = getMemberCountByLevel("silver");
        int goldCount = getMemberCountByLevel("gold");
        int diamondCount = getMemberCountByLevel("diamond");
        
        // 总积分
        List<Member> allMembers = this.getAllMembers();
        int totalPoints = allMembers.stream()
            .mapToInt(member -> member.getPoints() != null ? member.getPoints() : 0)
            .sum();
        
        // 总消费额
        double totalConsumption = allMembers.stream()
            .mapToDouble(member -> member.getTotalConsumption() != null ? member.getTotalConsumption() : 0.0)
            .sum();
        
        stats.put("totalMembers", totalMembers);
        stats.put("bronzeMembers", bronzeCount);
        stats.put("silverMembers", silverCount);
        stats.put("goldMembers", goldCount);
        stats.put("diamondMembers", diamondCount);
        stats.put("totalPoints", totalPoints);
        stats.put("totalConsumption", totalConsumption);
        stats.put("activeMembers", totalMembers);
        
        return stats;
    }

    @Override
    public List<Member> getTopSpendingMembers(int limit) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        queryWrapper.gt("total_consumption", 0);
        queryWrapper.orderByDesc("total_consumption");
        queryWrapper.last("LIMIT " + limit);
        return this.list(queryWrapper);
    }

    @Override
    public List<Member> getRecentRegisteredMembers(int limit) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "active");
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT " + limit);
        return this.list(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getRecentRegistrations(int limit) {
        return memberMapper.getRecentRegistrations(limit);
    }

    @Override
    public boolean validateMemberExists(String phoneNumber) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phoneNumber);
        queryWrapper.eq("status", "active");
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Member getMemberByPhone(String phone) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        queryWrapper.eq("status", "active");
        return this.getOne(queryWrapper);
    }

    /**
     * 根据消费金额计算会员等级
     */
    private String calculateMemberLevel(Double totalConsumption) {
        if (totalConsumption >= 10000) {
            return "diamond";
        } else if (totalConsumption >= 5000) {
            return "gold";
        } else if (totalConsumption >= 2000) {
            return "silver";
        } else {
            return "bronze";
        }
    }

    /**
     * 获取指定等级的会员数量
     */
    private int getMemberCountByLevel(String level) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_level", level);
        queryWrapper.eq("status", "active");
        return Math.toIntExact(this.count(queryWrapper));
    }
}