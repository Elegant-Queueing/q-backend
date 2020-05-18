package com.careerfair.q.workflow.database.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.model.exchange.StudentDTO;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.StudentFirebaseWorkflow;
import com.google.api.client.util.Lists;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.STUDENT_COLLECTION;

@Component
public class StudentFirebaseWorkflowImpl implements StudentFirebaseWorkflow {

    @Override
    public void checkValidStudentId(String studentId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            if (!firestore.collection(STUDENT_COLLECTION).document(studentId).get().get()
                    .exists()) {
                throw new FirebaseException("The student with student id=" + studentId +
                        " does not exist");
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Student getStudentWithId(String studentId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            Student student = firestore.collection(STUDENT_COLLECTION).document(studentId)
                    .get().get()
                    .toObject(Student.class);

            if (student == null) {
                throw new FirebaseException("No student with student id=" + studentId + " exists");
            }

            student.studentId = studentId;
            return student;
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Student getStudentWithEmail(String email) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection(STUDENT_COLLECTION);

        try {
            for (DocumentReference documentReference : collectionReference.listDocuments()) {
                DocumentSnapshot documentSnapshot = documentReference.get().get();
                String documentEmail = (String) documentSnapshot.get("email");

                if (email.equals(documentEmail)) {
                    Student student = documentSnapshot.toObject(Student.class);
                    assert student != null;

                    student.studentId = documentSnapshot.getId();
                    return student;
                }
            }

            throw new FirebaseException("No student with email=" + email + " exists");
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public void registerEmployeeToStudent(String studentId, String employeeId)
            throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        Student student = getStudentWithId(studentId);

        if (student.employees == null) {
            student.employees = Lists.newArrayList();
        }

        student.employees.add(employeeId);
        firestore.collection(STUDENT_COLLECTION).document(studentId).update("employees",
                student.employees);
    }

    @Override
    public Student updateStudent(String studentId, Student updatedStudent)
            throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        Student firebaseStudent = getStudentWithId(studentId);
        updateResponseStudent(studentId, firebaseStudent, updatedStudent);
        firestore.collection(STUDENT_COLLECTION).document(studentId).set(updatedStudent);
        return updatedStudent;
    }

    private void updateResponseStudent(String studentId, Student firebaseStudent,
                                       Student updatedStudent) {
        if (updatedStudent.firstName == null) {
            updatedStudent.firstName = firebaseStudent.firstName;
        }
        if (updatedStudent.lastName == null) {
            updatedStudent.lastName = firebaseStudent.lastName;
        }
        if (updatedStudent.major == null) {
            updatedStudent.major = firebaseStudent.major;
        }
        if (updatedStudent.role == null) {
            updatedStudent.role = firebaseStudent.role;
        }
        if (updatedStudent.bio == null) {
            updatedStudent.bio = firebaseStudent.bio;
        }
        if (updatedStudent.gpa == null) {
            updatedStudent.gpa = firebaseStudent.gpa;
        }
        if (updatedStudent.graduationDate == null) {
            updatedStudent.graduationDate = firebaseStudent.graduationDate;
        }
        if (updatedStudent.international == null) {
            updatedStudent.international = firebaseStudent.international;
        }
        updatedStudent.studentId = studentId;

        // TODO: work on university-id and email update
        updatedStudent.universityId = firebaseStudent.universityId;

        // TODO: work on employee update
        updatedStudent.employees = firebaseStudent.employees;
    }

    @Override
    public StudentDTO addStudent(StudentDTO addedStudent) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference studentCollection = firestore.collection(STUDENT_COLLECTION);
        studentCollection.add(addedStudent);
        return addedStudent;
    }
}
