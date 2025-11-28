/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.domain.entity;

import com.greenlog.util.RegexConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Kayqu
 */
@Entity
@Table(name = "caminhao")
public class Caminhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nao da para usar @Pattern(regexp = ValidadorRegexSingleton.getInstance().getPlacaRegex()) na entidade
    @Pattern(regexp = RegexConstants.PLACA_REGEX, message = "Formato de placa inválido (Ex: AAA-9999 ou ABC1D23).")
    @Column(nullable = false, unique = true)
    private String placa;

    @NotBlank(message = "O nome do motorista é obrigatório.")
    @Column(nullable = false)
    private String motorista;

    @NotNull(message = "A capacidade em Kg é obrigatória.")
    @DecimalMin(value = "0", inclusive = false, message = "A capacidade deve ser maior que zero.")
    @Column(nullable = false)
    private Integer capacidadeKg;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "caminhao_residuo",
            joinColumns = @JoinColumn(name = "caminhao_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_residuo_id"))
    private List<TipoResiduo> tiposSuportados;

    public Caminhao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMotorista() {
        return motorista;
    }

    public void setMotorista(String motorista) {
        this.motorista = motorista;
    }

    public Integer getCapacidadeKg() {
        return capacidadeKg;
    }

    public void setCapacidadeKg(Integer capacidadeKg) {
        this.capacidadeKg = capacidadeKg;
    }

    public List<TipoResiduo> getTiposSuportados() {
        return tiposSuportados;
    }

    public void setTiposSuportados(List<TipoResiduo> tiposSuportados) {
        this.tiposSuportados = tiposSuportados;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Caminhao caminhao = (Caminhao) o;
        return Objects.equals(id, caminhao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
