package org.fh.resource;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.fh.api.AddTaskRequest;
import org.fh.api.CreateTaskListRequest;
import org.fh.db.Task;
import org.fh.db.TaskList;
import org.fh.service.TaskListService;
import org.fh.service.TaskService;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/tasklist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskListResource {

    @Inject
    private TaskListService taskListService;

    @Inject
    private TaskService taskService;

    private static final Logger logger = Logger.getLogger(TaskListResource.class);

    @GET
    public List<TaskList> list() {
        return taskListService.findAll();
    }

    @GET
    @Path("/{id}")
    public Optional<TaskList> getTaskList(@PathParam("id") String taskListId) {
        return taskListService.find(taskListId);
    }

    @POST
    public Response create(CreateTaskListRequest request) {

        if (request.getName() == null || "".equals(request.getName())) {
            return Response.status(400).build();
        }

        var createdTaskListOpt = taskListService.create(request.getName());

        logger.info("Task list create returned " + createdTaskListOpt);

        return createdTaskListOpt.map(value -> Response.ok(value).status(202).build())
                .orElseGet(() -> Response.status(500).build());

    }

    @POST
    @Path("{id}/task")
    @Transactional
    public Response addTask(@PathParam("id") String uuid, AddTaskRequest request) {

        if (!request.isValid()) {
            return Response.status(400).build();
        }

        var taskListOpt = taskListService.find(uuid);

        if (taskListOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var taskList = taskListOpt.get();
        var task = new Task(UUID.randomUUID(), request.getName(), request.getDescription());

        try {
            task.setTaskList(taskList);
            task.persistAndFlush();
        } catch (ConstraintViolationException e) {
            return Response.status(409).build();
        }

        // return Response.status(200).entity(taskList).build();

        try {

            var newTasks = taskList.getTasks();
            newTasks.add(task);
            taskList.setTasks(newTasks);
            taskList.persist();
            return Response.ok(taskList).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }

    }

    @PUT
    @Path("{taskListId}/task/{taskId}")
    @Transactional
    public Response updateTask(@PathParam("taskListId") String taskListId,
                               @PathParam("taskId") String taskId,
                               AddTaskRequest request) {

        if (!request.isValid()) {
            return Response.status(400).build();
        }

        var updatedTaskOpt = taskListService.updateTask(taskListId, taskId, request);

        return updatedTaskOpt.map(value -> Response.accepted(value).build())
                .orElseGet(() -> Response.status(500).build());

    }

    /* To implement
        delete a task
        delete an entire list and all tasks. Unit test delete and check taskRepository.count() < 1
        move tasks /tasklist/{id}/task/{id}/move/{newId}
     */

    @DELETE
    @Path("{taskListId}/task/{taskId}")
    @Transactional
    public Response deleteTask(@PathParam("taskListId") String taskListId,
                               @PathParam("taskId") String taskId) {

        var taskListOpt = taskListService.find(taskListId);

        if (taskListOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var taskOpt = taskService.find(taskId);

        if (taskOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var task = taskOpt.get();

        task.delete();

        return Response.status(200).build();

    }

    @DELETE
    @Path("{taskListId}")
    @Transactional
    public Response deleteTaskList(@PathParam("taskListId") String taskListId) {

        var taskListOpt = taskListService.find(taskListId);

        if (taskListOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var taskList = taskListOpt.get();

        taskList.getTasks().forEach(PanacheEntityBase::delete);

        taskList.delete();

        return Response.status(200).build();
    }

    @PUT
    @Path("{sourceTaskListId}/task/{taskId}/move/{destinationTaskListId}")
    @Transactional
    public Response moveTask(@PathParam("sourceTaskListId") String sourceTaskListId,
                             @PathParam("taskId") String taskId,
                             @PathParam("destinationTaskListId") String destinationTaskListId) {

        var sourceTaskListOpt = taskListService.find(sourceTaskListId);
        var destinationTaskListOpt = taskListService.find(destinationTaskListId);

        if (sourceTaskListOpt.isEmpty() || destinationTaskListOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var taskOpt = taskService.find(taskId);

        if (taskOpt.isEmpty()) {
            return Response.status(404).build();
        }

        var task = taskOpt.get();

        task.setTaskList(destinationTaskListOpt.get());
        task.persist();

        return Response.status(200).build();
    }


}
