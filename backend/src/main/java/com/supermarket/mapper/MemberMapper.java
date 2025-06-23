package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.Member;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 会员数据访问层
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 查询所有会员
     */
    @Select("SELECT * FROM members ORDER BY registered_at DESC")
    List<Member> selectAll();

    /**
     * 根据ID查询会员
     */
    @Select("SELECT * FROM members WHERE member_id = #{memberId}")
    Member selectById(Long memberId);



    /**
     * 根据等级查询会员
     */
    @Select("SELECT * FROM members WHERE member_level = #{memberLevel} ORDER BY registered_at DESC")
    List<Member> selectByLevel(String memberLevel);

    /**
     * 插入会员
     */
    @Insert("INSERT INTO members (member_name, phone,  member_level, " +
            "points, total_consumption,created_at, updated_at) VALUES " +
            "(#{memberName}, #{phone},   #{memberLevel}, " +
            "#{points}, #{totalConsumption}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "memberId")
    int insert(Member member);

    /**
     * 更新会员信息
     */
    @Update("UPDATE members SET member_name = #{memberName}, phone = #{phone}, " +
            "member_level = #{memberLevel}, points = #{points},total_consumption = #{totalConsumption}, " +
            "last_visit = #{lastVisit}, remarks = #{remarks}, updated_at = #{updatedAt} " +
            "WHERE member_id = #{memberId}")
    int update(Member member);

    /**
     * 删除会员
     */
    @Delete("DELETE FROM members WHERE member_id = #{memberId}")
    int deleteById(Long memberId);

    /**
     * 关键词搜索会员
     */
    @Select("SELECT * FROM members WHERE member_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR phone_number LIKE CONCAT('%', #{keyword}, '%') ORDER BY registered_at DESC")
    List<Member> searchByKeyword(String keyword);

    /**
     * 统计所有会员数量
     */
    @Select("SELECT COUNT(*) FROM members")
    int countAll();

    /**
     * 按等级统计会员数量
     */
    @Select("SELECT COUNT(*) FROM members WHERE member_level = #{memberLevel}")
    int countByLevel(String memberLevel);

    /**
     * 统计本月新增会员
     */
    @Select("SELECT COUNT(*) FROM members WHERE DATE_FORMAT(registered_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    int countNewMembersThisMonth();

    /**
     * 统计活跃会员（指定天数内有消费）
     */
    @Select("SELECT COUNT(*) FROM members WHERE last_visit >= DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int countActiveMembersInDays(int days);

    /**
     * 获取平均消费金额
     */
    @Select("SELECT AVG(total_spent) FROM members WHERE total_spent > 0")
    BigDecimal getAverageConsumption();

    /**
     * 获取消费排行榜
     */
    @Select("SELECT * FROM members WHERE total_consumption > 0 AND status = 'active' " +
            "ORDER BY total_consumption DESC LIMIT #{limit}")
    List<Member> selectTopSpendingMembers(@Param("limit") Integer limit);

    /**
     * 获取最近注册的会员
     */
    @Select("SELECT * FROM members WHERE status = 'active' " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<Member> selectRecentRegisteredMembers(@Param("limit") Integer limit);

    /**
     * 获取最近注册记录（用于活动展示）
     */
    @Select("SELECT member_id, member_name, phone, created_at FROM members " +
            "WHERE status = 'active' ORDER BY created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> getRecentRegistrations(@Param("limit") Integer limit);

    /**
     * 创建会员（返回自增ID）
     */
    @Insert("INSERT INTO members (member_code, member_name, phone, points, total_consumption, " +
            "member_level, status, email,created_at, updated_at) VALUES " +
            "(#{memberCode}, #{memberName}, #{phone}, #{points}, #{totalConsumption}, " +
            "#{memberLevel}, #{status}, #{email},#{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "memberId")
    boolean insertMember(Member member);

    /**
     * 更新会员信息
     */
    @Update("UPDATE members SET member_name = #{memberName}, phone = #{phone}, " +
            "points = #{points}, total_consumption = #{totalConsumption}, " +
            "member_level = #{memberLevel}, status = #{status}, updated_at = #{updatedAt} " +
            "WHERE member_id = #{memberId}")
    int updateMember(Member member);

    /**
     * 删除会员（物理删除）
     */
    @Delete("DELETE FROM members WHERE member_id = #{memberId}")
    int deleteMember(@Param("memberId") Long memberId);

    /**
     * 根据会员卡号查询会员
     */
    @Select("SELECT * FROM members WHERE member_code = #{memberCode} AND status = 'active'")
    Member selectByMemberCode(@Param("memberCode") String memberCode);

    /**
     * 统计会员总数
     */
    @Select("SELECT COUNT(*) FROM members WHERE status = 'active'")
    int countActiveMembers();

    /**
     * 统计今日新增会员
     */
    @Select("SELECT COUNT(*) FROM members WHERE DATE(created_at) = CURDATE() AND status = 'active'")
    int countTodayNewMembers();

    /**
     * 获取会员等级分布
     */
    @Select("SELECT member_level, COUNT(*) as count FROM members " +
            "WHERE status = 'active' GROUP BY member_level")
    List<Map<String, Object>> getMemberLevelDistribution();

    /**
     * 获取高消费会员（消费超过指定金额）
     */
    @Select("SELECT * FROM members WHERE total_consumption >= #{amount} AND status = 'active' " +
            "ORDER BY total_consumption DESC")
    List<Member> selectHighSpendingMembers(@Param("amount") Double amount);

    /**
     * 更新会员积分
     */
    @Update("UPDATE members SET points = #{points}, updated_at = NOW() WHERE member_id = #{memberId}")
    int updateMemberPoints(@Param("memberId") Long memberId, @Param("points") Integer points);

    /**
     * 更新会员消费总额
     */
    @Update("UPDATE members SET total_consumption = total_consumption + #{amount}, updated_at = NOW() " +
            "WHERE member_id = #{memberId}")
    int updateMemberConsumption(@Param("memberId") Long memberId, @Param("amount") Double amount);

    /**
     * 批量更新会员等级
     */
    @Update("UPDATE members SET member_level = #{newLevel}, updated_at = NOW() " +
            "WHERE total_consumption >= #{minAmount} AND total_consumption < #{maxAmount} AND status = 'active'")
    int batchUpdateMemberLevel(@Param("newLevel") String newLevel,
                              @Param("minAmount") Double minAmount,
                              @Param("maxAmount") Double maxAmount);

    /**
     * 搜索会员（模糊查询）
     */
    @Select("SELECT * FROM members WHERE " +
            "(member_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "phone LIKE CONCAT('%', #{keyword}, '%') OR " +
            "member_code LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 'active' ORDER BY created_at DESC")
    List<Member> searchMembers(@Param("keyword") String keyword);

    /**
     * 获取生日在指定月份的会员
     */
    @Select("SELECT * FROM members WHERE MONTH(birthday) = #{month} AND status = 'active' " +
            "ORDER BY DAY(birthday)")
    List<Member> getMembersByBirthMonth(@Param("month") Integer month);

    /**
     * 获取最近注册统计
     */
    @Select("SELECT DATE(created_at) as date, COUNT(*) as count " +
            "FROM members " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{limit} DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<Map<String, Object>> getRecentRegistrations(@Param("limit") int limit);

    /**
     * 根据手机号查询会员
     */
    @Select("SELECT * FROM members WHERE phone = #{phone} AND status = 'active' LIMIT 1")
    Member selectByPhone(@Param("phone") String phone);

    /**
     * 获取会员等级统计
     */
    @Select("SELECT member_level, COUNT(*) as count " +
            "FROM members " +
            "WHERE status = 'active' " +
            "GROUP BY member_level")
    List<Map<String, Object>> getMemberLevelStatistics();
}