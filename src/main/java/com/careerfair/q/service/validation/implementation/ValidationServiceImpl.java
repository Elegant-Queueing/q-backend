package com.careerfair.q.service.validation.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
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
    public void checkValidStudentUpdateRequest(UpdateStudentRequest updateStudentRequest)
            throws ValidationException {
        if (updateStudentRequest.firstName == null || updateStudentRequest.firstName.isEmpty()) {
            throw new ValidationException("Empty or missing first name");
        } else if (updateStudentRequest.lastName == null ||
                updateStudentRequest.lastName.isEmpty()) {
            throw new ValidationException("Empty or missing last name");
        } else if (updateStudentRequest.universityId == null ||
                updateStudentRequest.universityId.isEmpty()) {
            throw new ValidationException("Empty or missing university name");
        } else if (updateStudentRequest.major == null || updateStudentRequest.major.isEmpty()) {
            throw new ValidationException("Empty or missing major");
        } else if (updateStudentRequest.role == null) {
            throw new ValidationException("Empty or missing role");
        } else if (updateStudentRequest.bio == null || updateStudentRequest.bio.isEmpty()) {
            throw new ValidationException("Empty or missing bio");
        } else if (updateStudentRequest.gpa == null || updateStudentRequest.gpa.isNaN()) {
            throw new ValidationException("Empty or Invalid GPA");
        } else if (updateStudentRequest.graduationDate == null) {
            throw new ValidationException("Missing graduation date");
        } else if (updateStudentRequest.international == null) {
            throw new ValidationException("Missing international field");
        } else if (updateStudentRequest.email == null ||
                !checkValidEmail(updateStudentRequest.email)) {
            throw new ValidationException("Missing or invalid email format");
        }
    }

    @Override
    public void checkValidStudentAddRequest(AddStudentRequest addStudentRequest)
            throws ValidationException {
        if (addStudentRequest.firstName == null || addStudentRequest.firstName.isEmpty()) {
            throw new ValidationException("Empty or missing first name");
        } else if (addStudentRequest.lastName == null
                || addStudentRequest.lastName.isEmpty()) {
            throw new ValidationException("Empty or missing last name");
        } else if (addStudentRequest.universityId == null
                || addStudentRequest.universityId.isEmpty()) {
            throw new ValidationException("Empty or missing university name");
        } else if (addStudentRequest.major == null || addStudentRequest.major.isEmpty()) {
            throw new ValidationException("Empty or missing major");
        } else if (addStudentRequest.role == null) {
            throw new ValidationException("Empty or missing role");
        } else if (addStudentRequest.bio == null || addStudentRequest.bio.isEmpty()) {
            throw new ValidationException("Empty or missing bio");
        } else if (addStudentRequest.gpa == null || addStudentRequest.gpa.isNaN()) {
            throw new ValidationException("Empty or Invalid GPA");
        } else if (addStudentRequest.graduationDate == null) {
            throw new ValidationException("Missing graduation date");
        } else if (addStudentRequest.international == null) {
            throw new ValidationException("Missing international field");
        } else if (addStudentRequest.email == null || !checkValidEmail(addStudentRequest.email)) {
            throw new ValidationException("Missing or invalid email format");
        }
    }

    private Boolean checkValidEmail(String email) {
        String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(emailRegex);
    }
}
