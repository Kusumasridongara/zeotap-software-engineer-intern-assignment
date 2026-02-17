package com.kusuma.fanout.transform;

import com.kusuma.fanout.model.Record;

public class ProtobufTransformer implements Transformer {

    @Override
    public String transform(Record record) {
        return "PROTOBUF_BINARY_DATA_FOR_ID_" + record.getId();
    }
}
