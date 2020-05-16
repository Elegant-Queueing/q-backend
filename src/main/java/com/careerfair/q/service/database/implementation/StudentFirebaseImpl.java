package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.util.exception.FirebaseException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.google.api.client.util.Lists;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.EMPLOYEE_COLLECTION;
import static com.careerfair.q.util.constant.Firebase.STUDENT_COLLECTION;

@Service
public class StudentFirebaseImpl implements StudentFirebase {

    // Use this method to test your firebase connection
    @Override
    public void test() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        HashMap<String, String> map = new HashMap<>();
        String name = "TEST NAME";  // change this to your name when testing
        map.put("name", name);
        ApiFuture<DocumentReference> collectionsApiFuture = dbFirestore.collection("test").add(map);
    }

    @Override
    public boolean registerStudent(String studentId, String employeeId) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        Student student = firestore.collection(STUDENT_COLLECTION)
                .document(studentId)
                .get().get()
                .toObject(Student.class);

        if (student == null) {
            throw new FirebaseException("No student found with student id=" + studentId);
        }

        Employee employee = firestore.collection(EMPLOYEE_COLLECTION)
                .document(employeeId)
                .get().get()
                .toObject(Employee.class);

        if (employee == null) {
            throw new FirebaseException("No student found with employee id=" + employeeId);
        }

        if (student.getEmployees() == null) {
            student.setEmployees(Lists.newArrayList());
        }
        if (employee.getStudents() == null) {
            employee.setStudents(Lists.newArrayList());
        }

        student.getEmployees().add(employeeId);
        employee.getStudents().add(studentId);

        firestore.collection(STUDENT_COLLECTION).document(studentId).update("employees",
                student.getEmployees());
        firestore.collection(EMPLOYEE_COLLECTION).document(employeeId).update("students",
                employee.getStudents());

        return true;
    }

    @Override
    public Student getStudentWithId(String studentId) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        DocumentSnapshot documentSnapshot = firestore.collection(STUDENT_COLLECTION)
                .document(studentId).get().get();
        Student student = documentSnapshot.toObject(Student.class);

        if (student == null) {
            throw new FirebaseException("No student with student id=" + studentId + " exists");
        }

        student.setStudentId(documentSnapshot.getId());
        return student;
    }

    @Override
    public Student getStudentWithEmail(String email) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection(STUDENT_COLLECTION);

        for (DocumentReference documentReference : collectionReference.listDocuments()) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            String documentEmail = (String) documentSnapshot.get("email");

            if (email.equals(documentEmail)) {
                Student student = documentSnapshot.toObject(Student.class);
                assert student != null;

                student.setStudentId(documentSnapshot.getId());
                return student;
            }
        }

        throw new FirebaseException("No student with email=" + email + " exists");
    }

//    @Override
//    public Student updateStudent(String studentId, JsonPatch patch) throws JsonPatchException, ExecutionException, InterruptedException, JsonProcessingException {
//        Firestore firestore = FirestoreClient.getFirestore();
//        DocumentSnapshot documentSnapshot = firestore.collection(STUDENT_COLLECTION)
//                .document(studentId).get().get();
//        Student student = documentSnapshot.toObject(Student.class);
//        if (student == null) {
//            throw new FirebaseException("No student with student id=" + studentId + " exists");
//        }
//        Student studentPatched = applyPatchToStudent(student, patch);
//        return student;
//    }
//
//    // Um should this be here?
//    private Student applyPatchToStudent(Student student, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode patched = patch.apply(objectMapper.convertValue(student, JsonNode.class));
//        return objectMapper.treeToValue(patched, Student.class);
//    }
//}
//
//@Configuration
//public class ProblemDemoConfiguration {
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        // In this example, stack traces support is enabled by default.
//        // If you want to disable stack traces just use new ProblemModule() instead of new ProblemModule().withStackTraces()
//        return new ObjectMapper().registerModules(new ProblemModule().withStackTraces(), new ConstraintViolationProblemModule());
//    }
//}

    @Override
    public Student updateStudent(String studentId, Map<String, Object> updatedValues) throws ExecutionException,
            InterruptedException {

        // Is it required to return a Student object to frontend? Or only updated fields is enough?
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentSnapshot document = firestore.collection(STUDENT_COLLECTION)
                .document(studentId).get().get();
        if (!document.exists()) {
            throw new FirebaseException("No student with student id=" + studentId + " exists");
        }
        Student student = document.toObject(Student.class);

        // do i need this? will the above document exists not be enough
        assert student != null;
        updatedValues.forEach(
                (field, v) -> {
                    switch (field) {

                        // TODO: What if university_id/student_id is attempted to be modified?

                        // Since these attributes are mapped to camel case, have to separate them out
                        // Not the best way since it's 'hardcoded'
                        case "last_name": student.setLastName((String) v); break;
                        case "first_name": student.setFirstName((String) v); break;
                        case "grad_date":
                            LinkedHashMap tmp = (LinkedHashMap) v;
                            Integer seconds = (Integer) tmp.get("seconds");
                            Integer nanos = (Integer) tmp.get("nanos");
                            student.setGraduationDate(Timestamp.ofTimeSecondsAndNanos(seconds.longValue(), nanos));
                            break;
                        default:
                            Field studentField = ReflectionUtils.findField(Student.class, field);
                            try {
                                studentField.setAccessible(true);
                            } catch (NullPointerException ex) {
                                throw new IllegalArgumentException("Invalid field name provided: " + field);
                            }
                            ReflectionUtils.setField(studentField, student, v);
                    }
                }
        );

        // This will update all the needed fields at once, instead of many accesses
        // Will only reach here if all the fields are valid
        firestore.collection(STUDENT_COLLECTION).document(studentId).update(updatedValues);
        return student;
    }
}
