package org.fh.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.fh.db.Task;

import java.util.UUID;

@ApplicationScoped
public class TaskRepository implements PanacheRepositoryBase<Task, UUID> {

}