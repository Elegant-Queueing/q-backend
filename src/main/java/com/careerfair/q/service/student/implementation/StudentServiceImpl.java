package com.careerfair.q.service.student.implementation;

import com.careerfair.q.model.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentFirebase studentFirebase;
    private final RedisTemplate<String, Student> studentRedisTemplate;

    private final HashOperations<String, Integer, Student> hashOperations;

    public StudentServiceImpl(@Autowired StudentFirebase studentFirebase,
                              @Autowired RedisTemplate<String, Student> studentRedisTemplate) {
        this.studentFirebase = studentFirebase;
        this.studentRedisTemplate = studentRedisTemplate;

        this.hashOperations = studentRedisTemplate.opsForHash();
    }

    @Override
    public GetStudentResponse getStudent(String id) {
        // TODO
        System.out.println(hashOperations.get("students2", 1));
        return null;
    }

    @Override
    public UpdateStudentResponse updateStudent(String id, UpdateStudentRequest updateStudentRequest) {
        // TODO
        return null;
    }

    @Override
    public DeleteStudentResponse deleteStudent(String id) {
        // TODO
        return null;
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        // TODO
        Student student = new Student(1, "test");
        studentRedisTemplate.opsForHash().put("students2", student.getId(), student);
        return null;
    }

    @Override
    public UpdateStudentResponse uploadStudentResume(String id, UpdateStudentRequest uploadStudentResume) {
        // TODO
        return null;
    }

    @Override
    public void testDatabaseConnection() {
        studentFirebase.test();
    }
}
