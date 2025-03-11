package org.fh.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.fh.api.AddTaskRequest;
import org.fh.api.CreateTaskListRequest;
import org.fh.db.Task;
import org.fh.db.TaskList;
import org.fh.repository.TaskListRepository;
import org.fh.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TaskListResourceTest {

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private TaskListRepository taskListRepository;

    @AfterEach
    @Transactional
    void cleanDb() {
        taskRepository.deleteAll();
        taskListRepository.deleteAll();
    }

    @BeforeAll
    static void setup() {
//        RestAssured.defaultParser = Parser.JSON;
        // RestAssured.registerParser("text/html", Parser.JSON);
    }

    @Test
    void canFindAllTaskLists() {
        given()
                .when().get("/tasklist")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    void canCreateTaskList() {
        var request = new CreateTaskListRequest("Fredriks Agenda");

        var taskListResponse = given().body(request)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(request.getName(), taskListResponse.getName());
        assertEquals(0, taskListResponse.getTasks().size());

    }

    @Test
    void canAddTaskToList() {
        var request = new CreateTaskListRequest("Fredriks Agenda");

        var taskListResponse = given().body(request)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(request.getName(), taskListResponse.getName());
        assertEquals(0, taskListResponse.getTasks().size());

        var taskListId = taskListResponse.getId().toString();
        var url = "/tasklist/" + taskListId + "/task";
        var createTaskRequest = new AddTaskRequest("Fredriks första task", "Bör göras så snabbt som möjligt");
        var updatedTaskList = given()
                .body(createTaskRequest)
                .header("Content-Type", "application/json")
                .when().post(url)
                .then()
                .statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, updatedTaskList.getTasks().size());

    }

    @Test
    void canUpdateTaskWithinAList() {
        var request = new CreateTaskListRequest("Fredriks Agenda");

        var taskListResponse = given().body(request)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(request.getName(), taskListResponse.getName());
        assertEquals(0, taskListResponse.getTasks().size());

        var taskListId = taskListResponse.getId().toString();
        var url = "/tasklist/" + taskListId + "/task";
        var createTaskRequest = new AddTaskRequest("Fredriks första task", "Bör göras så snabbt som möjligt");

        var updatedTaskList = given()
                .body(createTaskRequest)
                .header("Content-Type", "application/json")
                .when().post(url)
                .then()
                .statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, updatedTaskList.getTasks().size());
        var taskOne = updatedTaskList.getTasks().get(0);
        assertEquals(createTaskRequest.getName(), taskOne.getName());
        assertEquals(createTaskRequest.getDescription(), taskOne.getDescription());

        var taskId = taskOne.getId().toString();

        var updateTaskUrl = "/tasklist/" + taskListId + "/task/" + taskId;
        var updateTaskRequest = new AddTaskRequest("Fredriks uppdaterade task", "Bör göras så långsamt som möjligt");

        var updatedTask = given()
                .body(updateTaskRequest)
                .header("Content-Type", "application/json")
                .when().put(updateTaskUrl)
                .then()
                .statusCode(202)
                .extract().body().as(Task.class);

        assertEquals(updateTaskRequest.getName(), updatedTask.getName());
        assertEquals(updateTaskRequest.getDescription(), updatedTask.getDescription());


    }

    @Test
    void canDeleteTask() {

        var request = new CreateTaskListRequest("Fredriks Agenda");

        var taskListResponse = given().body(request)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(request.getName(), taskListResponse.getName());
        assertEquals(0, taskListResponse.getTasks().size());

        var taskListId = taskListResponse.getId().toString();
        var url = "/tasklist/" + taskListId + "/task";
        var createTaskRequest = new AddTaskRequest("Fredriks första task", "Bör göras så snabbt som möjligt");

        var updatedTaskList = given()
                .body(createTaskRequest)
                .header("Content-Type", "application/json")
                .when().post(url)
                .then()
                .statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, updatedTaskList.getTasks().size());

        var taskOne = updatedTaskList.getTasks().get(0);

        var deleteUrl = "/tasklist/" + taskListId + "/task/" + taskOne.getId().toString();

        given()
                .when()
                .delete(deleteUrl)
                .then()
                .statusCode(200);

        var taskListUrl = String.format("/tasklist/%s", taskListId);

        updatedTaskList = given()
                .when().get(taskListUrl)
                .then().statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(0, updatedTaskList.getTasks().size());

    }

    @Test
    void canDeleteTaskList() {

        var request = new CreateTaskListRequest("Fredriks Agenda");

        var taskListResponse = given().body(request)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(request.getName(), taskListResponse.getName());
        assertEquals(0, taskListResponse.getTasks().size());

        var taskListId = taskListResponse.getId().toString();
        assertNotNull(taskListId);

        var url = "/tasklist/" + taskListId + "/task";
        var createTaskRequest = new AddTaskRequest("Fredriks första task", "Bör göras så snabbt som möjligt");

        var updatedTaskList = given()
                .body(createTaskRequest)
                .header("Content-Type", "application/json")
                .when().post(url)
                .then()
                .statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, updatedTaskList.getTasks().size());
        assertEquals(1, taskRepository.count());

        var getTaskList = given()
                .when()
                .get(String.format("/tasklist/%s", taskListId)).then().statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, getTaskList.getTasks().size());

        var deleteUrl = String.format("/tasklist/%s", taskListId);

        given().when().delete(deleteUrl).then().statusCode(200);

        assertEquals(0, taskListRepository.count());
        assertEquals(0, taskRepository.count());

    }

    @Test
    void canMoveTask() {

        var requestOne = new CreateTaskListRequest("Fredriks Agenda #1");

        var taskListOneResponse = given().body(requestOne)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(requestOne.getName(), taskListOneResponse.getName());
        assertEquals(0, taskListOneResponse.getTasks().size());

        var requestTwo = new CreateTaskListRequest("Fredriks Agenda #2");

        var taskListTwoResponse = given().body(requestTwo)
                .header("Content-Type", "application/json")
                .when().post("/tasklist")
                .then()
                .statusCode(202)
                .extract().body().as(TaskList.class);

        assertEquals(requestTwo.getName(), taskListTwoResponse.getName());
        assertEquals(0, taskListTwoResponse.getTasks().size());

        var taskListOneId = taskListOneResponse.getId().toString();
        var taskListTwoId = taskListTwoResponse.getId().toString();

        var url = "/tasklist/" + taskListOneId + "/task";
        var createTaskRequest = new AddTaskRequest("Fredriks första task", "Bör göras så snabbt som möjligt");

        var updatedTaskList = given()
                .body(createTaskRequest)
                .header("Content-Type", "application/json")
                .when().post(url)
                .then()
                .statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(1, updatedTaskList.getTasks().size());
        assertEquals(1, taskRepository.count());

        var taskId = updatedTaskList.getTasks().get(0).getId().toString();

        var moveUrl = String.format("/tasklist/%s/task/%s/move/%s", taskListOneId, taskId, taskListTwoId);

        given()
                .when()
                .put(moveUrl)
                .then()
                .statusCode(200);

        var getFirstTaskList = given().when().get(String.format("/tasklist/%s", taskListOneId)).then().statusCode(200)
                .extract().body().as(TaskList.class);

        var getSecondTaskList = given().when().get(String.format("/tasklist/%s", taskListTwoId)).then().statusCode(200)
                .extract().body().as(TaskList.class);

        assertEquals(0, getFirstTaskList.getTasks().size());
        assertEquals(1, getSecondTaskList.getTasks().size());



    }
}
