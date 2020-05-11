package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Fair;

import java.util.List;
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

    /**
     * Gets all the fairs from firebase
     *
     * @return a list of all the fairs
     */
    List<Fair> getAllFairs() throws ExecutionException, InterruptedException;
}
