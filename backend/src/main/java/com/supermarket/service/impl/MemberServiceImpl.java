package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Member;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {


    @Override
    public List<Member> getAllMembers() {
        return List.of();
    }

    @Override
    public Member getMemberById(Long memberId) {
        return null;
    }

    @Override
    public boolean addMember(Member member) {
        return false;
    }

    @Override
    public boolean updateMember(Member member) {
        return false;
    }

    @Override
    public boolean deleteMember(Long memberId) {
        return false;
    }

    @Override
    public List<Member> getMembersByLevel(String memberLevel) {
        return List.of();
    }

    @Override
    public boolean updateMemberPoints(Long memberId, Integer points) {
        Member member = new Member();
        member.setMemberId(memberId);
        member.setPoints(points);
        return this.updateById(member);
    }

    @Override
    public boolean addMemberPoints(Long memberId, Integer pointsToAdd) {
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
            
            // 根据消费金额自动升级会员等级
            String newLevel = calculateMemberLevel(newConsumption);
            updateMember.setMemberLevel(newLevel);
            
            return this.updateById(updateMember);
        }
        return false;
    }

    @Override
    public Map<String, Object> getMemberStatistics() {
        return Map.of();
    }

    @Override
    public List<Member> getTopSpendingMembers(int limit) {
        return List.of();
    }

    @Override
    public List<Member> getRecentRegisteredMembers(int limit) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getRecentRegistrations(int limit) {
        return List.of();
    }

    @Override
    public boolean validateMemberExists(String phoneNumber) {
        return false;
    }

    /**
     * 根据消费金额计算会员等级
     */
    private String calculateMemberLevel(Double totalConsumption) {
        if (totalConsumption >= 10000) {
            return "钻石会员";
        } else if (totalConsumption >= 5000) {
            return "金卡会员";
        } else if (totalConsumption >= 2000) {
            return "银卡会员";
        } else {
            return "普通会员";
        }
    }
}