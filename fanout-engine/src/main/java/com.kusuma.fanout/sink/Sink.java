package com.kusuma.fanout.sink;

import com.kusuma.fanout.model.Record;

public interface Sink {

    String getName();

    void send(Record record) throws Exception;
}
