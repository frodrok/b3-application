package org.fh.api;

public class CreateTaskListRequest {

    String name;

    public CreateTaskListRequest() {
    }

    public CreateTaskListRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
