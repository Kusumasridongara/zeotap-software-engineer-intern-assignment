package com.kusuma.fanout.retry;

import com.kusuma.fanout.model.Record;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeadLetterQueue {

    private final List<Record> failedRecords = new CopyOnWriteArrayList<>();

    public void add(Record record) {
        failedRecords.add(record);
    }

    public int size() {
        return failedRecords.size();
    }

    public List<Record> getAll() {
        return failedRecords;
    }
}
