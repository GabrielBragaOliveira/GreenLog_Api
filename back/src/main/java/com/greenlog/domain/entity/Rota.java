/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Kayqu
 */
@Entity
@Table(name = "rota")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da rota é obrigatório.")
    @Column(nullable = false, unique = true)
    private String nome;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rota_bairro",
            joinColumns = @JoinColumn(name = "rota_id"),
            inverseJoinColumns = @JoinColumn(name = "bairro_id"))
    private List<Bairro> listaDeBairros;

    @NotNull(message = "O ponto de coleta de destino é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ponto_destino_id", nullable = false)
    private PontoColeta pontoColetaDestino;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Rota() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Bairro> getListaDeBairros() {
        return listaDeBairros;
    }

    public void setListaDeBairros(List<Bairro> listaDeBairros) {
        this.listaDeBairros = listaDeBairros;
    }

    public PontoColeta getPontoColetaDestino() {
        return pontoColetaDestino;
    }

    public void setPontoColetaDestino(PontoColeta pontoColetaDestino) {
        this.pontoColetaDestino = pontoColetaDestino;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo != null && ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rota rota = (Rota) o;
        return Objects.equals(id, rota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}