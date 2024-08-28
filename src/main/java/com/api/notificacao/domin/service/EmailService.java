package com.api.notificacao.domin.service;

import com.api.notificacao.domin.model.Evento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void enviarEmail(Evento evento){
        log.info("Email enviado com sucesso para : {}", evento.getDados().getEmail());
    }
}
