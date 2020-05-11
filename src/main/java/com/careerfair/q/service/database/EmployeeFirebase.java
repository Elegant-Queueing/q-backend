package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Employee;

import java.util.concurrent.ExecutionException;

public interface EmployeeFirebase {

    /**
     * Gets the employee profile from firebase
     *
     * @param id employee's id
     * @return Employee
     */
    Employee getEmployee(String id) throws ExecutionException, InterruptedException,
            ClassNotFoundException;

    /**
     * Gets the employee profile from firebase
     *
     * @param email employee's email
     * @return Employee
     */
    Employee getEmployeeByEmail(String email) throws ExecutionException, InterruptedException;
}
