package org.fh.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.fh.api.AddTaskRequest;
import org.fh.db.Task;
import org.fh.db.TaskList;
import org.fh.repository.TaskListRepository;
import org.fh.repository.TaskRepository;
import org.fh.resource.TaskListResource;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TaskListService {

    @Inject
    private TaskListRepository taskListRepository;

    @Inject
    private TaskRepository taskRepository;

    private static final Logger logger = Logger.getLogger(TaskListService.class);

    public List<TaskList> findAll() {
        return taskListRepository.listAll();
    }

    @Transactional
    public Optional<TaskList> create(String name) {
        var taskList = new TaskList(UUID.randomUUID(), name, List.of());

        try {
            taskListRepository.persist(taskList);
            return Optional.of(taskList);

        // I don't have time to investigate what to catch to catch all database errors
        } catch (Exception e) {
            logger.error(e);
            return Optional.empty();
        }
    }

    public Optional<TaskList> find(String uuid) {

        try {
            var parsedUuid = UUID.fromString(uuid);
            return taskListRepository.findByIdOptional(parsedUuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

    }

    public Optional<Task> updateTask(String taskListId, String taskId, AddTaskRequest request) {
        try {
            var parsedTaskListUuid = UUID.fromString(taskListId);
            var parsedTaskUuid = UUID.fromString(taskId);

            var taskOpt = taskRepository.findByIdOptional(parsedTaskUuid);

            if (taskOpt.isEmpty()) {
                return Optional.empty();
            }

            var task = taskOpt.get();

            task.setName(request.getName());
            task.setDescription(request.getDescription());

            task.persistAndFlush();

            return Optional.of(task);
        } catch (Exception e) {
            // Whatever exception is thrown we want to return empty
            return Optional.empty();
        }

    }

    public long count() {
        return taskListRepository.count();
    }
}
