package org.fh.resource;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.fh.repository.TaskListRepository;
import org.fh.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TaskResourceTest {

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TaskListRepository taskListRepository;

    @BeforeEach
    @Transactional
    void cleanDb() {
        taskRepository.deleteAll();
        taskListRepository.deleteAll();
    }

    @Test
    void canListTasks() {
        given()
                .when().get("/task")
                .then().statusCode(200)
                .body(is("[]"));
    }
}
