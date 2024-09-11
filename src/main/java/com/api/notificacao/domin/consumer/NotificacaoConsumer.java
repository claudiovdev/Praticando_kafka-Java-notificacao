package com.api.notificacao.domin.consumer;

import com.api.notificacao.domin.model.Evento;
import com.api.notificacao.domin.service.EmailService;
import com.api.notificacao.domin.utils.JsonUitls;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
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
    public void consumerNotificacaoUsuario(ConsumerRecord<String, String> record){
        log.info("Evento {} recebido do topico notificacao-usuario", record);
        Evento evento = jsonUitls.toEvento(record.value());
        for (Header header : record.headers()) {
            log.info("Header Key: {}, Header Value: {}", header.key(), new String(header.value()));
        }
        emailService.enviarEmail(evento);
    }
}
