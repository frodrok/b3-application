package org.fh.api;

public class AddTaskRequest implements RequestPayload {

    String name;
    String description;

    public AddTaskRequest() {
    }

    public AddTaskRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isValid() {
        return name != null
                && description != null
                && !"".equals(name)
                && !"".equals(description);
    }
}
