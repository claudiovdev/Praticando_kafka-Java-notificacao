package com.api.notificacao.domin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Historico {
    private String projeto;
    private String status;
    private String message;
    private LocalDateTime dataDeCriacao;
}
