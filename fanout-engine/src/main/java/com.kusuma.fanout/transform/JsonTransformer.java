package com.kusuma.fanout.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kusuma.fanout.model.Record;

public class JsonTransformer implements Transformer {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String transform(Record record) {
        try {
            return mapper.writeValueAsString(record);
        } catch (Exception e) {
            throw new RuntimeException("JSON Transformation Failed");
        }
    }
}
