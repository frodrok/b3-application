package org.fh.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fh.db.Task;
import org.fh.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TaskService {

    @Inject
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.listAll();
    }

    public Optional<Task> find(String taskId) {
        try {
            var parsedUuid = UUID.fromString(taskId);
            return taskRepository.findByIdOptional(parsedUuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public long count() {
        return taskRepository.count();
    }
}
