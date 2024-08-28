package com.api.notificacao.domin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Evento {
    private String idTransacao;
    private DadosEvento dados;

}
