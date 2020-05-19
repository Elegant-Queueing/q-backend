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
        if (studentRequest.firstName == null || studentRequest.firstName.isEmpty()) {
            throw new ValidationException("Empty or missing first name");
        } else if (studentRequest.lastName == null || studentRequest.lastName.isEmpty()) {
            throw new ValidationException("Empty or missing last name");
        } else if (studentRequest.universityId == null || studentRequest.universityId.isEmpty()) {
            throw new ValidationException("Empty or missing university name");
        } else if (studentRequest.major == null || studentRequest.major.isEmpty()) {
            throw new ValidationException("Empty or missing major");
        } else if (studentRequest.role == null) {
            throw new ValidationException("Empty or missing role");
        } else if (studentRequest.bio == null || studentRequest.bio.isEmpty()) {
            throw new ValidationException("Empty or missing bio");
        } else if (studentRequest.gpa == null || studentRequest.gpa.isNaN()) {
            throw new ValidationException("Empty or Invalid GPA");
        } else if (studentRequest.graduationDate == null) {
            throw new ValidationException("Missing graduation date");
        } else if (studentRequest.international == null) {
            throw new ValidationException("Missing international field");
        } else if (studentRequest.email == null || !checkValidEmail(studentRequest.email)) {
            throw new ValidationException("Missing or invalid email format");
        }
    }

    private Boolean checkValidEmail(String email) {
        String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(emailRegex);
    }
}
