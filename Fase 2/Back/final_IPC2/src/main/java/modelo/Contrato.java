/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;

/**
 *
 * @author jeffm
 */
public class Contrato {
    private Integer id;
    private Integer propuestaId;
    private Integer proyectoId;
    private Integer clienteId;
    private Integer freelancerId;
    private Double monto;
    private Double porcentajeComision;
    private Double comisionMonto;
    private String estado; // ACTIVO, COMPLETADO, CANCELADO
    private String motivoCancelacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String proyectoTitulo;
    private String clienteNombre;
    private String freelancerNombre;
    
    public Contrato () {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPropuestaId() {
        return propuestaId;
    }

    public void setPropuestaId(Integer propuestaId) {
        this.propuestaId = propuestaId;
    }

    public Integer getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Integer proyectoId) {
        this.proyectoId = proyectoId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(Integer freelancerId) {
        this.freelancerId = freelancerId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(Double porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }

    public Double getComisionMonto() {
        return comisionMonto;
    }

    public void setComisionMonto(Double comisionMonto) {
        this.comisionMonto = comisionMonto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getProyectoTitulo() {
        return proyectoTitulo;
    }

    public void setProyectoTitulo(String proyectoTitulo) {
        this.proyectoTitulo = proyectoTitulo;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getFreelancerNombre() {
        return freelancerNombre;
    }

    public void setFreelancerNombre(String freelancerNombre) {
        this.freelancerNombre = freelancerNombre;
    }        
}
