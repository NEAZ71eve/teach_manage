package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.KnowledgePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知识点数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class KnowledgePointRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有知识点
     */
    public List<KnowledgePoint> findAll() {
        String sql = "SELECT kp_id, kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time FROM knowledge_point";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KnowledgePoint point = new KnowledgePoint();
            point.setPointId(rs.getInt("kp_id"));
            point.setPointName(rs.getString("kp_name"));
            point.setCourseId(rs.getInt("course_id"));
            point.setParentId(rs.getObject("parent_kp_id", Integer.class));
            point.setDescription(rs.getString("kp_desc"));
            point.setDifficulty(rs.getString("kp_difficulty"));
            point.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            point.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return point;
        });
    }

    /**
     * 根据ID查询知识点
     */
    public Optional<KnowledgePoint> findById(Integer id) {
        String sql = "SELECT kp_id, kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time FROM knowledge_point WHERE kp_id = ?";
        List<KnowledgePoint> points = jdbcTemplate.query(sql, (rs, rowNum) -> {
            KnowledgePoint point = new KnowledgePoint();
            point.setPointId(rs.getInt("kp_id"));
            point.setPointName(rs.getString("kp_name"));
            point.setCourseId(rs.getInt("course_id"));
            point.setParentId(rs.getObject("parent_kp_id", Integer.class));
            point.setDescription(rs.getString("kp_desc"));
            point.setDifficulty(rs.getString("kp_difficulty"));
            point.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            point.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return point;
        }, id);
        return points.isEmpty() ? Optional.empty() : Optional.of(points.get(0));
    }

    /**
     * 根据课程ID查询知识点
     */
    public List<KnowledgePoint> findByCourseId(Integer courseId) {
        String sql = "SELECT kp_id, kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time FROM knowledge_point WHERE course_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KnowledgePoint point = new KnowledgePoint();
            point.setPointId(rs.getInt("kp_id"));
            point.setPointName(rs.getString("kp_name"));
            point.setCourseId(rs.getInt("course_id"));
            point.setParentId(rs.getObject("parent_kp_id", Integer.class));
            point.setDescription(rs.getString("kp_desc"));
            point.setDifficulty(rs.getString("kp_difficulty"));
            point.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            point.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return point;
        }, courseId);
    }

    /**
     * 根据父ID查询子知识点
     */
    public List<KnowledgePoint> findByParentId(Integer parentId) {
        String sql = "SELECT kp_id, kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time FROM knowledge_point WHERE parent_kp_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KnowledgePoint point = new KnowledgePoint();
            point.setPointId(rs.getInt("kp_id"));
            point.setPointName(rs.getString("kp_name"));
            point.setCourseId(rs.getInt("course_id"));
            point.setParentId(rs.getObject("parent_kp_id", Integer.class));
            point.setDescription(rs.getString("kp_desc"));
            point.setDifficulty(rs.getString("kp_difficulty"));
            point.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            point.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return point;
        }, parentId);
    }

    /**
     * 新增知识点
     */
    public int save(KnowledgePoint point) {
        String sql = "INSERT INTO knowledge_point (kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                point.getPointName(), 
                point.getCourseId(), 
                point.getParentId(), 
                point.getDescription(),
                point.getDifficulty());
    }

    /**
     * 更新知识点
     */
    public int update(KnowledgePoint point) {
        String sql = "UPDATE knowledge_point SET kp_name = ?, course_id = ?, parent_kp_id = ?, kp_desc = ?, kp_difficulty = ?, update_time = NOW() WHERE kp_id = ?";
        return jdbcTemplate.update(sql, 
                point.getPointName(), 
                point.getCourseId(), 
                point.getParentId(), 
                point.getDescription(),
                point.getDifficulty(),
                point.getPointId());
    }

    /**
     * 删除知识点
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM knowledge_point WHERE kp_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除知识点
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM knowledge_point WHERE kp_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 分页查询知识点
     */
    public List<KnowledgePoint> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT kp_id, kp_name, course_id, parent_kp_id, kp_desc, kp_difficulty, create_time, update_time FROM knowledge_point LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            KnowledgePoint point = new KnowledgePoint();
            point.setPointId(rs.getInt("kp_id"));
            point.setPointName(rs.getString("kp_name"));
            point.setCourseId(rs.getInt("course_id"));
            point.setParentId(rs.getObject("parent_kp_id", Integer.class));
            point.setDescription(rs.getString("kp_desc"));
            point.setDifficulty(rs.getString("kp_difficulty"));
            point.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            point.setUpdateTime(rs.getTimestamp("update_time").toLocalDateTime());
            return point;
        }, limit, offset);
    }

    /**
     * 查询知识点总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM knowledge_point";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
