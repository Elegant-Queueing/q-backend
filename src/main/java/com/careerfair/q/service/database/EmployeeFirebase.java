package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Employee;

import java.util.concurrent.ExecutionException;

public interface EmployeeFirebase {

    /**
     * Gets the employee profile from firebase
     *
     * @param employeeId id of the employee whose data is to retrieved
     * @return Employee
     */
    Employee getEmployeeWithId(String employeeId) throws ExecutionException, InterruptedException;

    /**
     * Gets the employee profile from firebase
     *
     * @param email email of the employee whose data is to retrieved
     * @return Employee
     */
    Employee getEmployeeWithEmail(String email) throws ExecutionException, InterruptedException;
}
