package com.careerfair.q.util.constant;

public class Queue {

    /**
     * Name of the employee cache in Redis
     */
    public static final String EMPLOYEE_CACHE_NAME = "employees";

    /**
     * Name of the student cache in Redis
     */
    public static final String STUDENT_CACHE_NAME = "students";

    /**
     * The time window (in seconds) that the student has to join the employee's queue
     */
    public static final int WINDOW = 300;

    /**
     * Buffer time (in seconds) that the student has to join the employee's queue
     */
    public static final int BUFFER = 10;

    /**
     * Max queue size for the given employee
     */
    public static final int MAX_EMPLOYEE_QUEUE_SIZE = 5;

    /**
     * Default total time spent for an employee
     */
    public static final long INITIAL_TIME_SPENT = 300;
}
