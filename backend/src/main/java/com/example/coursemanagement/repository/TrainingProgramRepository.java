package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.TrainingProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 培养方案数据访问层，用Spring JDBC Template实现
 */
@Repository
public class TrainingProgramRepository implements TrainingProgramRepositoryApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private volatile Boolean durationColumnAvailable;
    private volatile Boolean totalCreditColumnAvailable;
    private volatile Boolean effectiveYearColumnAvailable;
    private volatile Boolean descriptionColumnAvailable;
    private volatile Boolean programCodeNullable;

    /**
     * 查询所有培养方案
     */
    public List<TrainingProgram> findAll() {
        String sql = "SELECT * FROM training_program";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class));
    }

    /**
     * 根据ID查询培养方案
     */
    public Optional<TrainingProgram> findById(Integer id) {
        String sql = "SELECT * FROM training_program WHERE program_id = ?";
        List<TrainingProgram> programs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), id);
        return programs.isEmpty() ? Optional.empty() : Optional.of(programs.get(0));
    }

    /**
     * 新增培养方案
     */
    public int save(TrainingProgram program) {
        ensureProgramCodeColumnNullable();
        List<ColumnMeta> columns = loadTrainingProgramColumns();
        List<String> columnNames = new ArrayList<>();
        List<String> valueSql = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        for (ColumnMeta column : columns) {
            if (column.isAutoIncrement()) {
                continue;
            }
            if (column.isTimeColumn()) {
                columnNames.add(column.name());
                valueSql.add("NOW()");
                continue;
            }
            ColumnValue columnValue = resolveColumnValue(program, column);
            if (!columnValue.include()) {
                continue;
            }
            columnNames.add(column.name());
            valueSql.add("?");
            params.add(columnValue.value());
        }

        String sql = "INSERT INTO training_program (" + String.join(", ", columnNames) + ") VALUES (" + String.join(", ", valueSql) + ")";
        return jdbcTemplate.update(sql, params.toArray());
    }

    /**
     * 更新培养方案
     */
    public int update(TrainingProgram program) {
        ensureProgramCodeColumnNullable();
        boolean hasDuration = ensureDurationColumnAvailable();
        boolean hasTotalCredit = ensureTotalCreditColumnAvailable();
        boolean hasEffectiveYear = ensureEffectiveYearColumnAvailable();
        boolean hasProgramName = isColumnAvailable("program_name");
        boolean hasDescription = isDescriptionColumnAvailable();

        List<String> updates = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        updates.add("major_name = ?");
        params.add(program.getMajorName());

        if (hasProgramName) {
            updates.add("program_name = ?");
            params.add(program.getMajorName());
        }

        if (hasDuration) {
            updates.add("duration = ?");
            params.add(program.getDuration());
        }

        if (hasTotalCredit) {
            updates.add("total_credit = ?");
            params.add(program.getTotalCredit());
        }

        if (hasEffectiveYear) {
            updates.add("effective_year = ?");
            params.add(program.getEffectiveYear());
        }

        if (hasDescription) {
            updates.add("description = ?");
            params.add(program.getDescription());
        }

        updates.add("update_time = NOW()");
        params.add(program.getProgramId());

        String sql = "UPDATE training_program SET " + String.join(", ", updates) + " WHERE program_id = ?";
        return jdbcTemplate.update(sql, params.toArray());
    }

    private boolean isDescriptionColumnAvailable() {
        if (descriptionColumnAvailable != null) {
            return descriptionColumnAvailable;
        }
        synchronized (this) {
            if (descriptionColumnAvailable != null) {
                return descriptionColumnAvailable;
            }
            descriptionColumnAvailable = isColumnAvailable("description");
            return descriptionColumnAvailable;
        }
    }

    private boolean ensureDurationColumnAvailable() {
        durationColumnAvailable = ensureColumnAvailable(
                durationColumnAvailable,
                "duration",
                "ALTER TABLE training_program ADD COLUMN duration INT NOT NULL DEFAULT 0 COMMENT '学制（年）' AFTER major_name"
        );
        return durationColumnAvailable;
    }

    private boolean ensureTotalCreditColumnAvailable() {
        totalCreditColumnAvailable = ensureColumnAvailable(
                totalCreditColumnAvailable,
                "total_credit",
                "ALTER TABLE training_program ADD COLUMN total_credit DECIMAL(4,1) NOT NULL DEFAULT 0 COMMENT '总学分' AFTER duration"
        );
        return totalCreditColumnAvailable;
    }

    private boolean ensureEffectiveYearColumnAvailable() {
        effectiveYearColumnAvailable = ensureColumnAvailable(
                effectiveYearColumnAvailable,
                "effective_year",
                "ALTER TABLE training_program ADD COLUMN effective_year INT NOT NULL DEFAULT 0 COMMENT '生效年份' AFTER total_credit"
        );
        return effectiveYearColumnAvailable;
    }

    private boolean ensureColumnAvailable(Boolean cachedValue, String columnName, String alterSql) {
        if (cachedValue != null) {
            return cachedValue;
        }
        synchronized (this) {
            if (cachedValue != null) {
                return cachedValue;
            }
            boolean columnAvailable = isColumnAvailable(columnName);
            if (columnAvailable) {
                return true;
            }
            try {
                jdbcTemplate.execute(alterSql);
                columnAvailable = true;
            } catch (Exception ignored) {
                columnAvailable = isColumnAvailable(columnName);
            }
            return columnAvailable;
        }
    }

    private List<ColumnMeta> loadTrainingProgramColumns() {
        String sql = "SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, EXTRA, DATA_TYPE FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'training_program' ORDER BY ORDINAL_POSITION";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ColumnMeta(
                rs.getString("COLUMN_NAME"),
                rs.getString("DATA_TYPE"),
                rs.getString("IS_NULLABLE"),
                rs.getString("COLUMN_DEFAULT"),
                rs.getString("EXTRA")
        ));
    }

    private ColumnValue resolveColumnValue(TrainingProgram program, ColumnMeta column) {
        String name = column.name();
        Object value = null;
        boolean hasValue = false;

        switch (name) {
            case "major_name" -> {
                value = program.getMajorName();
                hasValue = true;
            }
            case "program_name" -> {
                value = program.getProgramName() != null ? program.getProgramName() : program.getMajorName();
                hasValue = value != null;
            }
            case "program_code" -> {
                value = program.getProgramCode() != null ? program.getProgramCode() : program.getMajorName();
                hasValue = value != null;
            }
            case "major_id" -> {
                value = program.getMajorId();
                hasValue = value != null;
            }
            case "start_grade" -> {
                value = program.getStartGrade();
                hasValue = value != null;
            }
            case "teacher_id" -> {
                value = program.getTeacherId();
                hasValue = value != null;
            }
            case "duration" -> {
                value = program.getDuration();
                hasValue = value != null;
            }
            case "total_credit" -> {
                value = program.getTotalCredit();
                hasValue = value != null;
            }
            case "effective_year" -> {
                value = program.getEffectiveYear();
                hasValue = value != null;
            }
            case "description" -> {
                value = program.getDescription();
                hasValue = value != null;
            }
            default -> {
            }
        }

        if (hasValue) {
            return ColumnValue.include(value);
        }

        if (column.isRequiredWithoutDefault()) {
            return ColumnValue.include(defaultValueFor(column));
        }

        return ColumnValue.skip();
    }

    private Object defaultValueFor(ColumnMeta column) {
        String dataType = column.dataType();
        if (dataType == null) {
            return "";
        }
        if (isStringType(dataType)) {
            return "";
        }
        if (isNumericType(dataType)) {
            return 0;
        }
        if (isDateTimeType(dataType)) {
            return LocalDateTime.now();
        }
        return "";
    }

    private boolean isStringType(String dataType) {
        return switch (dataType) {
            case "varchar", "char", "text", "longtext", "mediumtext", "tinytext" -> true;
            default -> false;
        };
    }

    private boolean isNumericType(String dataType) {
        return switch (dataType) {
            case "int", "bigint", "smallint", "tinyint", "decimal", "double", "float" -> true;
            default -> false;
        };
    }

    private boolean isDateTimeType(String dataType) {
        return switch (dataType) {
            case "date", "datetime", "timestamp" -> true;
            default -> false;
        };
    }

    private record ColumnMeta(String name, String dataType, String nullable, String columnDefault, String extra) {
        boolean isAutoIncrement() {
            return extra != null && extra.contains("auto_increment");
        }

        boolean isRequiredWithoutDefault() {
            return !"YES".equalsIgnoreCase(nullable) && columnDefault == null && !isAutoIncrement();
        }

        boolean isTimeColumn() {
            return "create_time".equalsIgnoreCase(name) || "update_time".equalsIgnoreCase(name);
        }
    }

    private record ColumnValue(boolean include, Object value) {
        static ColumnValue include(Object value) {
            return new ColumnValue(true, value);
        }

        static ColumnValue skip() {
            return new ColumnValue(false, null);
        }
    }

    private void ensureProgramCodeColumnNullable() {
        if (programCodeNullable != null && programCodeNullable) {
            return;
        }
        synchronized (this) {
            if (programCodeNullable != null && programCodeNullable) {
                return;
            }
            if (!isColumnAvailable("program_code")) {
                programCodeNullable = false;
                return;
            }
            if (isColumnNullable("program_code")) {
                programCodeNullable = true;
                return;
            }
            String columnType = getColumnType("program_code");
            if (columnType == null) {
                programCodeNullable = false;
                return;
            }
            try {
                jdbcTemplate.execute("ALTER TABLE training_program MODIFY COLUMN program_code " + columnType + " NULL");
                programCodeNullable = true;
            } catch (Exception ignored) {
                programCodeNullable = isColumnNullable("program_code");
            }
        }
    }

    private boolean isColumnNullable(String columnName) {
        String nullable = jdbcTemplate.queryForObject(
                "SELECT IS_NULLABLE FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'training_program' AND column_name = ?",
                String.class,
                columnName
        );
        return "YES".equalsIgnoreCase(nullable);
    }

    private String getColumnType(String columnName) {
        return jdbcTemplate.queryForObject(
                "SELECT COLUMN_TYPE FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'training_program' AND column_name = ?",
                String.class,
                columnName
        );
    }

    private boolean isColumnAvailable(String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'training_program' AND column_name = ?",
                Integer.class,
                columnName
        );
        return count != null && count > 0;
    }

    /**
     * 删除培养方案
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM training_program WHERE program_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除培养方案
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM training_program WHERE program_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 分页查询培养方案
     */
    public List<TrainingProgram> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM training_program LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), limit, offset);
    }

    /**
     * 分页查询培养方案（支持过滤）
     */
    public List<TrainingProgram> findByPage(int page, int limit, Integer programId, Integer teacherId) {
        int offset = (page - 1) * limit;
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        
        // 优先使用teacherId过滤，如果没有teacherId则使用programId过滤
        if (teacherId != null && isColumnAvailable("teacher_id")) {
            conditions.add("teacher_id = ?");
            params.add(teacherId);
        } else if (programId != null) {
            conditions.add("program_id = ?");
            params.add(programId);
        }
        
        String whereClause = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
        String sql = "SELECT * FROM training_program" + whereClause + " LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), params.toArray());
    }

    /**
     * 查询培养方案总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM training_program";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * 查询培养方案总数（支持过滤）
     */
    public int count(Integer programId, Integer teacherId) {
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        
        // 优先使用teacherId过滤，如果没有teacherId则使用programId过滤
        if (teacherId != null && isColumnAvailable("teacher_id")) {
            conditions.add("teacher_id = ?");
            params.add(teacherId);
        } else if (programId != null) {
            conditions.add("program_id = ?");
            params.add(programId);
        }
        
        String whereClause = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
        String sql = "SELECT COUNT(*) FROM training_program" + whereClause;
        
        return jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
    }

    /**
     * 根据老师ID查询培养方案列表
     */
    public List<TrainingProgram> findByTeacherId(Integer teacherId) {
        if (teacherId == null) {
            return findAll();
        }
        if (!isColumnAvailable("teacher_id")) {
            return findAll();
        }
        String sql = "SELECT * FROM training_program WHERE teacher_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), teacherId);
    }
}
