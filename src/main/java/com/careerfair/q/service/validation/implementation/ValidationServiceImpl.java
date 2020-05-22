package com.careerfair.q.service.validation.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.employee.request.EmployeeRequest;
import com.careerfair.q.service.student.request.StudentRequest;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.util.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final FirebaseService firebaseService;

    public ValidationServiceImpl(@Autowired FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    public void checkValidStudentId(String studentId) throws ValidationException {
        try {
            firebaseService.checkValidStudentId(studentId);
        } catch (FirebaseException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public void checkValidEmployeeId(String employeeId) throws ValidationException {
        try {
            firebaseService.checkValidEmployeeId(employeeId);
        } catch (FirebaseException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public void checkValidCompanyId(String companyId) throws ValidationException {
        try {
            firebaseService.checkValidCompanyId(companyId);
        } catch (FirebaseException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public void checkEmployeeAssociations(String companyId, String employeeId, Role role)
            throws ValidationException {
        try {
            Employee employee = firebaseService.getEmployeeWithId(employeeId);

            if (!companyId.equals(employee.companyId)) {
                throw new ValidationException("Employee with employee id=" + employeeId +
                        " is not associated with company with company id=" + companyId);
            } else if (employee.role != role) {
                throw new ValidationException("Employee with employee id=" + employeeId +
                        " is not associated with the role=" + role);
            }
        } catch (FirebaseException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public <T extends StudentRequest> void checkValidStudentRequest(T studentRequest)
            throws ValidationException {
        checkRequestParameters(studentRequest.getFirstName(), "first_name");
        checkRequestParameters(studentRequest.getLastName(), "last_name");
        checkRequestParameters(studentRequest.getUniversityId(), "university_id");
        checkRequestParameters(studentRequest.getMajor(), "major");
        checkRequestParameters(studentRequest.getBio(), "bio");
        checkRequestParameters(studentRequest.getEmail(), "email");
        checkRequestParameters(studentRequest.getRole(), "role");
        checkRequestParameters(studentRequest.getGpa(), "gpa");
        checkRequestParameters(studentRequest.getInternational(), "international");
        checkRequestParameters(studentRequest.getGraduationDate(),
                "graduation_date");
    }

    @Override
    public <T extends EmployeeRequest> void checkValidEmployeeRequest(T employeeRequest)
            throws ValidationException {
        checkRequestParameters(employeeRequest.getName(), "name");
        checkRequestParameters(employeeRequest.getCompanyId(), "company_id");
        checkRequestParameters(employeeRequest.getBio(), "bio");
        checkRequestParameters(employeeRequest.getEmail(), "email");
        checkRequestParameters(employeeRequest.getRole(), "role");
    }

    // Checks if the request parameters are valid
    private <T> void checkRequestParameters(T fieldValue, String fieldName)
            throws ValidationException {
        if (fieldValue == null) {
            throw new ValidationException("Missing the field=" + fieldName);
        } else if ((fieldValue instanceof String) && fieldValue.toString().isEmpty()) {
            throw new ValidationException("Empty field=" + fieldName);
        } else if (fieldName.equals("email")) {
            checkValidEmail(fieldValue.toString());
        }
    }

    // Checks if the given email is in valid format
    private void checkValidEmail(String email) throws ValidationException {
        String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if (!email.matches(emailRegex)) {
            throw new ValidationException("Incorrect email format");
        }
    }
}
