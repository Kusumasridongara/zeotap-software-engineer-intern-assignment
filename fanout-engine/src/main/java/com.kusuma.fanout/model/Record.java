package com.kusuma.fanout.model;

public class Record {

    private final String id;
    private final String name;
    private final String email;

    public Record(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Record{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
