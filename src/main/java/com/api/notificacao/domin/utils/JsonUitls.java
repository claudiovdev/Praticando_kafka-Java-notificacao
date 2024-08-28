package com.api.notificacao.domin.utils;

import com.api.notificacao.domin.model.Evento;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonUitls {

    private final ObjectMapper objectMapper;

    public String toJson(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        }catch (Exception e){
            return "";
        }
    }

    public Evento toEvento(String json){
        try {
            return objectMapper.readValue(json, Evento.class);
        }catch (Exception ex){
            return null;
        }
    }
}
