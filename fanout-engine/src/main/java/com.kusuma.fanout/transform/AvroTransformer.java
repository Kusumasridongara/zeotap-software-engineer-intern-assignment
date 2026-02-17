package com.kusuma.fanout.transform;

import com.kusuma.fanout.model.Record;

public class AvroTransformer implements Transformer {

    @Override
    public String transform(Record record) {
        return "{ \"id\": \"" + record.getId() + "\", " +
                "\"name\": \"" + record.getName() + "\", " +
                "\"email\": \"" + record.getEmail() + "\" }";
    }
}
