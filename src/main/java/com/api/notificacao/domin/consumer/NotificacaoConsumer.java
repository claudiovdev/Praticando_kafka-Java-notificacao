package com.api.notificacao.domin.consumer;

import com.api.notificacao.domin.service.EmailService;
import com.api.notificacao.domin.utils.JsonUitls;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotificacaoConsumer {

    private final JsonUitls jsonUitls;
    private final EmailService emailService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notificacao-usuario}"
    )
    public void consumerNotificacaoUsuario(String payload){
        log.info("Evento {} recebido do topico notificacao-usuario", payload);
        var evento = jsonUitls.toEvento(payload);
        emailService.enviarEmail(evento);

    }
}
