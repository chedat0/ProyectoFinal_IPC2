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
public class Propuesta {
    private Integer id;
    private Integer proyectoId;
    private Integer freelancerId;
    private double montoOfertado;
    private Integer plazoDias;
    private String cartaPresentacion;
    private String estado; // PENDIENTE, ACEPTADA, RECHAZADA, RETIRADA
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaRespuesta;
    private String freelancerNombre;
    private Double freelancerCalificacion;
    private String proyectoTitulo;
    
    public Propuesta() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Integer proyectoId) {
        this.proyectoId = proyectoId;
    }

    public Integer getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(Integer freelancerId) {
        this.freelancerId = freelancerId;
    }

    public double getMontoOfertado() {
        return montoOfertado;
    }

    public void setMontoOfertado(Double montoOfertado) {
        this.montoOfertado = montoOfertado;
    }

    public Integer getPlazoDias() {
        return plazoDias;
    }

    public void setPlazoDias(Integer plazoDias) {
        this.plazoDias = plazoDias;
    }

    public String getCartaPresentacion() {
        return cartaPresentacion;
    }

    public void setCartaPresentacion(String cartaPresentacion) {
        this.cartaPresentacion = cartaPresentacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getFreelancerNombre() {
        return freelancerNombre;
    }

    public void setFreelancerNombre(String freelancerNombre) {
        this.freelancerNombre = freelancerNombre;
    }

    public Double getFreelancerCalificacion() {
        return freelancerCalificacion;
    }

    public void setFreelancerCalificacion(Double freelancerCalificacion) {
        this.freelancerCalificacion = freelancerCalificacion;
    }

    public String getProyectoTitulo() {
        return proyectoTitulo;
    }

    public void setProyectoTitulo(String proyectoTitulo) {
        this.proyectoTitulo = proyectoTitulo;
    }           
}
