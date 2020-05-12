package com.careerfair.q.service.database;

import java.util.concurrent.ExecutionException;

public interface StudentFirebase {
    /**
     *
     */
    void test();

    boolean registerStudent(String studentId, String employeeId) throws ExecutionException, InterruptedException;
}
