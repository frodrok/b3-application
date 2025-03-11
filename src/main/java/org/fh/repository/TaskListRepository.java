package org.fh.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.fh.db.TaskList;

import java.util.UUID;

@ApplicationScoped
public class TaskListRepository implements PanacheRepositoryBase<TaskList, UUID> {
}
