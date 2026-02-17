package com.kusuma.fanout.transform;

import com.kusuma.fanout.model.Record;

public class XmlTransformer implements Transformer {

    @Override
    public String transform(Record record) {
        return "<record>" +
                "<id>" + record.getId() + "</id>" +
                "<name>" + record.getName() + "</name>" +
                "<email>" + record.getEmail() + "</email>" +
                "</record>";
    }
}
