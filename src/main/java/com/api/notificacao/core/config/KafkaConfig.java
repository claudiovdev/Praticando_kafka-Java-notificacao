package com.api.notificacao.core.config;

import com.api.notificacao.domin.model.Evento;
import com.api.notificacao.domin.model.Historico;
import com.api.notificacao.domin.utils.JsonUitls;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    public static final int REPLICA_COUNT = 1;
    public static final int PARTITION_COUNT = 1;
    private final JsonUitls jsonUitls;


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffSetReset;

    @Value("${spring.kafka.topic.notificacao-usuario}")
    private String topicoNotificacao;
    @Value("${spring.kafka.topic.notificacao-usuario-dlt}")
    private String topicoNotificacaoDlt;

    private Map<String,Object> consumerProps(){
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffSetReset);
        return props;
    }

    private Map<String, Object> producerProps(){
    var props = new HashMap<String, Object>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return props;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate){
        return new DefaultErrorHandler(((consumerRecord, exception) -> {
            var mensagem = consumerRecord.value().toString();
            try{
                Evento eventoComErro = jsonUitls.toEvento(mensagem);
                eventoComErro.getHistoricos().add(
                        Historico.builder()
                                .projeto("API-NOTIFICACAO")
                                .status("FALHA")
                                .message("Erro ao enviar mensagem, motivo: " + exception.getCause().getMessage())
                                .dataDeCriacao(LocalDateTime.now())
                                .build());
                String mensagemAtualizada = jsonUitls.toJson(eventoComErro);
                kafkaTemplate.send(consumerRecord.topic() + ".DLT", consumerRecord.key().toString(), mensagemAtualizada);
                log.info("Mensagem enviada ao DLT: {}", mensagemAtualizada);

            } catch (Exception e) {
                log.error("Erro ao modificar a mensagem antes de enviar ao DLT: {}", e.getMessage());
                kafkaTemplate.send(consumerRecord.topic() + ".DLT",  consumerRecord.key().toString(), consumerRecord.value().toString());
            }
        }),new FixedBackOff(1000L,3));

    }
    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    private NewTopic buildTopic(String name){
        return TopicBuilder
                .name(name)
                .replicas(REPLICA_COUNT)
                .partitions(PARTITION_COUNT)
                .build();
    }

    @Bean
    public NewTopic topicoNotificacao(){
        return buildTopic(topicoNotificacao);
    }
    @Bean
    public NewTopic topicoNotificacaoDlt(){
        return buildTopic(topicoNotificacaoDlt);
    }
}
