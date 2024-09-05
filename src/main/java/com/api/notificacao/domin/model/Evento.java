package com.api.notificacao.domin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Evento {
    private String idTransacao;
    private DadosEvento dados;
    private List<Historico> historicos;

}
