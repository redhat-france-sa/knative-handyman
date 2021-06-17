package com.redhat.handyman.render;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;

@ApplicationScoped
@Named("viewUpdate")
public class ViewUpdate {

    @Inject
    private ProducerTemplate producer;

    public String update(Serializable data,String type, Boolean notify, Boolean updateHeader, Boolean changeData,
            String dataOperationType, String targetTableId) throws JsonProcessingException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(SocketData.defaultSocketData(data, type, notify, updateHeader, changeData, dataOperationType, targetTableId));
        return value;
    }

    public void updateHeader(Serializable data,String type, String targetTableId) throws JsonProcessingException {
        update(data, type, true, true, false, null, targetTableId);
    }

    public void appendData(Serializable data,String type, String targetTableId) throws JsonProcessingException {
        update(data, type, true, true, true, SocketData.APPEND_DATA, targetTableId);
    }

    public void upsertData(Serializable data,String type, String targetTableId) throws JsonProcessingException{
        update(data, type, true, true, true, SocketData.UPSERT_DATA, targetTableId);
    }

    public void refreshData(Serializable data,String type, String targetTableId) throws JsonProcessingException{
        update(data, type, true, true, true, SocketData.REFRESH_DATA, targetTableId);
    }
    public void clearData(String type, String targetTableId) throws JsonProcessingException {
        update(null, type, false, false, true, SocketData.CLEAR_DATA, targetTableId);
    }

}
