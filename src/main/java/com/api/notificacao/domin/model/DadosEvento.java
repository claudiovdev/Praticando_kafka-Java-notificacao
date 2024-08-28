package com.api.notificacao.domin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DadosEvento {

    private String id;
    private String nome;
    private String email;
}
