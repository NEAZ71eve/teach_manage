package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.TrainingProgram;
import com.example.coursemanagement.service.TrainingProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 培养方案Controller
 */
@RestController
@RequestMapping("/training-program")
public class TrainingProgramController {

    @Autowired
    private TrainingProgramService trainingProgramService;

    /**
     * 查询所有培养方案
     */
    @GetMapping
    public ResponseEntity<List<TrainingProgram>> getAllTrainingPrograms() {
        return ResponseEntity.ok(trainingProgramService.list());
    }

    /**
     * 分页查询培养方案
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getTrainingProgramPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        List<TrainingProgram> programs = trainingProgramService.listPage(page, limit);
        int total = trainingProgramService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("records", programs);
        result.put("total", total);
        result.put("page", page);
        result.put("limit", limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询培养方案
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainingProgram> getTrainingProgramById(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingProgramService.getById(id));
    }

    /**
     * 新增培养方案
     */
    @PostMapping
    public ResponseEntity<Boolean> addTrainingProgram(@RequestBody TrainingProgram program) {
        try {
            System.out.println("收到新增培养方案请求: " + program);
            System.out.println("majorName: " + program.getMajorName());
            System.out.println("duration: " + program.getDuration());
            System.out.println("totalCredit: " + program.getTotalCredit());
            System.out.println("effectiveYear: " + program.getEffectiveYear());
            boolean result = trainingProgramService.save(program);
            System.out.println("新增培养方案结果: " + result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("新增培养方案异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    /**
     * 更新培养方案
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateTrainingProgram(@PathVariable Integer id, @RequestBody TrainingProgram program) {
        program.setProgramId(id);
        return ResponseEntity.ok(trainingProgramService.updateById(program));
    }

    /**
     * 删除培养方案
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTrainingProgram(@PathVariable Integer id) {
        return ResponseEntity.ok(trainingProgramService.removeById(id));
    }

    /**
     * 批量删除培养方案
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(trainingProgramService.removeByIds(ids));
    }
    
    /**
     * 根据老师ID查询培养方案列表
     */
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<TrainingProgram>> getTrainingProgramsByTeacher(@PathVariable Integer teacherId) {
        return ResponseEntity.ok(trainingProgramService.getByTeacherId(teacherId));
    }
}
