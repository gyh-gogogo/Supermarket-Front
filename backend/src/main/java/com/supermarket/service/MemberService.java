package com.supermarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.entity.Member;

import java.util.List;
import java.util.Map;

/**
 * 会员服务接口
 */
public interface MemberService extends IService<Member> {

    /**
     * 获取所有会员
     * @return 会员列表
     */
    List<Member> getAllMembers();

    /**
     * 根据ID获取会员
     * @param memberId 会员ID
     * @return 会员信息
     */
    Member getMemberById(Long memberId);

    /**
     * 添加会员
     * @param member 会员信息
     * @return 是否成功
     */
    boolean addMember(Member member);

    /**
     * 更新会员信息
     * @param member 会员信息
     * @return 是否成功
     */
    boolean updateMember(Member member);

    /**
     * 删除会员
     * @param memberId 会员ID
     * @return 是否成功
     */
    boolean deleteMember(Long memberId);

    /**
     * 根据等级获取会员
     * @param memberLevel 会员等级
     * @return 会员列表
     */
    List<Member> getMembersByLevel(String memberLevel);

    /**
     * 更新会员积分
     * @param memberId 会员ID
     * @param points 新积分
     * @return 是否成功
     */
    boolean updateMemberPoints(Long memberId, Integer points);

    /**
     * 增加会员积分
     * @param memberId 会员ID
     * @param pointsToAdd 增加的积分
     * @return 是否成功
     */
    boolean addMemberPoints(Long memberId, Integer pointsToAdd);

    /**
     * 更新会员消费信息
     * @param memberId 会员ID
     * @param amount 消费金额
     * @return 是否成功
     */
    boolean updateMemberConsumption(Long memberId, Double amount);

    /**
     * 获取会员统计信息
     * @return 统计数据
     */
    Map<String, Object> getMemberStatistics();

    /**
     * 获取消费排行榜
     * @param limit 返回数量
     * @return 会员列表
     */
    List<Member> getTopSpendingMembers(int limit);

    /**
     * 获取最近注册的会员
     * @param limit 返回数量
     * @return 会员列表
     */
    List<Member> getRecentRegisteredMembers(int limit);

    /**
     * 获取最近注册记录（用于活动展示）
     * @param limit 返回数量
     * @return 会员列表
     */
    List<Map<String, Object>> getRecentRegistrations(int limit);

    /**
     * 验证会员是否存在
     * @param phoneNumber 手机号
     * @return 是否存在
     */
    boolean validateMemberExists(String phoneNumber);

}