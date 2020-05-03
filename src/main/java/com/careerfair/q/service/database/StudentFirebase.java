package com.careerfair.q.service.database;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;

public interface StudentFirebase {
    /**
     *
     */
    void test();

    boolean registerStudent(Student student, Employee employee);
}
