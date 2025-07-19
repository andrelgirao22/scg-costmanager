package br.com.alg.scg.domain.sales.entity;

import lombok.Data;

@Data
public class Client {

    private Long id;
    private String nome;
    private Contact contact;
}
