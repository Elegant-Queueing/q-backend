package com.careerfair.q.service.validation.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.ValidationException;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.*;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void checkValidStudentId(String studentId) throws ValidationException {
        checkValidId(studentId, STUDENT_COLLECTION, "The student with student id=" + studentId +
                " does not exist");
    }

    @Override
    public void checkValidEmployeeId(String employeeId) throws ValidationException {
        checkValidId(employeeId, EMPLOYEE_COLLECTION, "The employee with employee id=" +
                employeeId + " does not exist");
    }

    @Override
    public void checkValidCompanyId(String companyId) throws ValidationException {
        checkValidId(companyId, COMPANY_COLLECTION, "The company with company id=" + companyId +
                " does not exist");
    }

    @Override
    public void checkEmployeeAssociations(String companyId, String employeeId, Role role)
            throws ValidationException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            Employee employee = firestore.collection(EMPLOYEE_COLLECTION)
                    .document(employeeId)
                    .get().get()
                    .toObject(Employee.class);

            if (employee == null) {
                throw new ValidationException("No employee with employee id=" + employeeId);
            }

            if (!companyId.equals(employee.getCompanyId())) {
                throw new ValidationException("Employee with employee id=" + employeeId +
                        " is not associated with company with company id=" + companyId);
            } else if (employee.getRole() != role) {
                throw new ValidationException("Employee with employee id=" + employeeId +
                        " is not associated with the role=" + role);
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    /**
     * Checks whether the given id is present in the given collection
     *
     * @param id id to validate
     * @param collectionName name of the collection that the id should be present in
     * @param errorMessage message to throw if the id id not present
     * @throws ValidationException if the id is not present in the given collection
     */
    private void checkValidId(String id, String collectionName, String errorMessage)
            throws ValidationException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            if (!firestore.collection(collectionName).document(id).get().get().exists()) {
                throw new ValidationException(errorMessage);
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }
}
