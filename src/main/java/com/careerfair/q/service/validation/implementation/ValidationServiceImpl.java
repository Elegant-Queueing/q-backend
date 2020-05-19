package com.careerfair.q.service.validation.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
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

            if (!companyId.equals(employee.getCompanyId())) {
                throw new ValidationException("Employee with employee id=" + employeeId +
                        " is not associated with company with company id=" + companyId);
            } else if (employee.getRole() != role) {
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
        checkStudentRequestConstraints(studentRequest);
    }

    private <T extends StudentRequest> void checkStudentRequestConstraints(T studentRequest)
            throws ValidationException {
        if (studentRequest.getFirstName() == null || studentRequest.getFirstName().isEmpty()) {
            throw new ValidationException("Empty or missing first name");
        } else if (studentRequest.getLastName() == null || studentRequest.getLastName().isEmpty()) {
            throw new ValidationException("Empty or missing last name");
        } else if (studentRequest.getUniversityId() == null ||
                studentRequest.getUniversityId().isEmpty()) {
            throw new ValidationException("Empty or missing university name");
        } else if (studentRequest.getMajor() == null || studentRequest.getMajor().isEmpty()) {
            throw new ValidationException("Empty or missing major");
        } else if (studentRequest.getRole() == null) {
            throw new ValidationException("Empty or missing role");
        } else if (studentRequest.getBio() == null || studentRequest.getBio().isEmpty()) {
            throw new ValidationException("Empty or missing bio");
        } else if (studentRequest.getGpa() == null || studentRequest.getGpa().isNaN()) {
            throw new ValidationException("Empty or Invalid GPA");
        } else if (studentRequest.getGraduationDate() == null) {
            throw new ValidationException("Missing graduation date");
        } else if (studentRequest.getInternational() == null) {
            throw new ValidationException("Missing international field");
        } else if (studentRequest.getEmail() == null || !checkValidEmail(studentRequest.getEmail())) {
            throw new ValidationException("Missing or invalid email format");
        }
    }

    private boolean checkValidEmail(String email) {
        String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(emailRegex);
    }
}
