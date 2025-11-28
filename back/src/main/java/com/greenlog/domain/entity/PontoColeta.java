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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "ponto_coleta")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do responsável é obrigatório.")
    @Column(nullable = false)
    private String nomeResponsavel;

    @Pattern(regexp = RegexConstants.TELEFONE_REGEX, message = "Formato de contato inválido.")
    @Column(nullable = false)
    private String contato;
    
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "O endereço é obrigatório.")
    @Column(nullable = false)
    private String endereco;
    
    @Column(nullable = false)
    private String horarioFuncionamento;

    @NotNull(message = "O bairro é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_id", nullable = false)
    private Bairro bairro;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ponto_residuo",
            joinColumns = @JoinColumn(name = "ponto_coleta_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_residuo_id"))
    private List<TipoResiduo> tiposResiduosAceitos;

    public PontoColeta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public List<TipoResiduo> getTiposResiduosAceitos() {
        return tiposResiduosAceitos;
    }

    public void setTiposResiduosAceitos(List<TipoResiduo> tiposResiduosAceitos) {
        this.tiposResiduosAceitos = tiposResiduosAceitos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }
    
    

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PontoColeta that = (PontoColeta) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
