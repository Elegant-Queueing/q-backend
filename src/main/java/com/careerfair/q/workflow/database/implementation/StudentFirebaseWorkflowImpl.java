package com.careerfair.q.workflow.database.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.StudentFirebaseWorkflow;
import com.google.api.client.util.Lists;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.STUDENT_COLLECTION;

@Component
public class StudentFirebaseWorkflowImpl implements StudentFirebaseWorkflow {

    private final static List<String> UPDATE_FIELDS = Arrays.asList("first_name", "last_name",
            "role", "major", "gpa", "grad_date","bio", "email", "international");

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

        if (!student.employees.contains(employeeId)) {
            student.employees.add(employeeId);
            firestore.collection(STUDENT_COLLECTION).document(studentId).update("employees",
                    student.employees);
        }
    }

    @Override
    public Student updateStudent(String studentId, Student updatedStudent)
            throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        checkValidStudentId(studentId);

        try {
            firestore.collection(STUDENT_COLLECTION).document(studentId)
                    .set(updatedStudent, SetOptions.mergeFields(UPDATE_FIELDS)).get();
            return getStudentWithId(studentId);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Student addStudent(Student newStudent) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection(STUDENT_COLLECTION).document();
        String studentId = documentReference.getId();

        try {
            documentReference.set(newStudent).get();
            return getStudentWithId(studentId);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Student deleteStudent(String studentId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        Student deletedStudent = getStudentWithId(studentId);
        firestore.collection(STUDENT_COLLECTION).document(studentId).delete();
        return deletedStudent;
    }
}
