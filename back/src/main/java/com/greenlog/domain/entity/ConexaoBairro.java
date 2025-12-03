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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 *
 * @author Kayqu
 */
@Entity
@Table(name = "conexao_bairro")
public class ConexaoBairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O bairro de origem é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_origem_id", nullable = false)
    private Bairro bairroOrigem;

    @NotNull(message = "O bairro de destino é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_destino_id", nullable = false)
    private Bairro bairroDestino;

    @NotNull(message = "A distância é obrigatória.")
    @DecimalMin(value = "0.0", inclusive = false, message = "A distância deve ser maior que zero.")
    @Column(nullable = false)
    private Double distancia;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public ConexaoBairro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getBairroOrigem() {
        return bairroOrigem;
    }

    public void setBairroOrigem(Bairro bairroOrigem) {
        this.bairroOrigem = bairroOrigem;
    }

    public Bairro getBairroDestino() {
        return bairroDestino;
    }

    public void setBairroDestino(Bairro bairroDestino) {
        this.bairroDestino = bairroDestino;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
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
        ConexaoBairro that = (ConexaoBairro) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
