package com.kusuma.fanout.transform;

import com.kusuma.fanout.model.Record;

public interface Transformer {

    String transform(Record record);

}
