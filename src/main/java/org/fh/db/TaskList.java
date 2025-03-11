package org.fh.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
public class TaskList extends PanacheEntityBase {

    @Id
    public UUID id;

    public String name;

    @OneToMany(mappedBy = "taskList", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    public List<Task> tasks;

    // TODO: Add lombok to project to skip this boilerplate
    public TaskList() {
    }

    public TaskList(UUID id, List<Task> tasks) {
        this.id = id;
        this.tasks = tasks;
    }

    public TaskList(UUID id, String name, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
