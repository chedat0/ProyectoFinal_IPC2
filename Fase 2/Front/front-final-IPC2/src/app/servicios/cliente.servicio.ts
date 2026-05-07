import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

@Injectable({ providedIn: 'root' })
export class ClienteServicio {
    private url = `${backEnd.apiUrl}/cliente`;
    constructor(private http: HttpClient) { }

    getPerfil(): Observable<any> { return this.http.get(`${this.url}/perfil`); }
    updatePerfil(data: any): Observable<any> { return this.http.put(`${this.url}/perfil`, data); }

    getProyectos(): Observable<any> { return this.http.get(`${this.url}/proyectos`); }
    getProyecto(id: number): Observable<any> { return this.http.get(`${this.url}/proyectos/${id}`); }
    crearProyecto(data: any): Observable<any> { return this.http.post(`${this.url}/proyectos`, data); }
    actualizarProyecto(id: number, data: any): Observable<any> { return this.http.put(`${this.url}/proyectos/${id}`, data); }
    eliminarProyecto(id: number): Observable<any> { return this.http.delete(`${this.url}/proyectos/${id}`); }

    getPropuestasProyecto(proyId: number): Observable<any> { return this.http.get(`${this.url}/proyectos/${proyId}/propuestas`); }
    aceptarPropuesta(propId: number): Observable<any> { return this.http.put(`${this.url}/propuestas/${propId}/aceptar`, {}); }
    rechazarPropuesta(propId: number): Observable<any> { return this.http.put(`${this.url}/propuestas/${propId}/rechazar`, {}); }

    getContratos(): Observable<any> { return this.http.get(`${this.url}/contratos`); }
    getContrato(id: number): Observable<any> { return this.http.get(`${this.url}/contratos/${id}`); }
    cancelarContrato(id: number): Observable<any> { return this.http.put(`${this.url}/contratos/${id}/cancelar`, {}); }

    getEntregas(contratoId: number): Observable<any> { return this.http.get(`${this.url}/contratos/${contratoId}/entregas`); }
    aprobarEntrega(entId: number): Observable<any> { return this.http.put(`${this.url}/entregas/${entId}/aprobar`, {}); }
    rechazarEntrega(entId: number, comentario: string): Observable<any> {
        return this.http.put(`${this.url}/entregas/${entId}/rechazar`, { comentario });
    }

    recargarSaldo(monto: number, metodoPago: string, referencia?: string): Observable<any> {
        return this.http.post(`${this.url}/recargas`, { monto, metodoPago, referencia });
    }
    getRecargas(): Observable<any> { return this.http.get(`${this.url}/recargas`); }

    crearCalificacion(data: any): Observable<any> { return this.http.post(`${this.url}/calificaciones`, data); }
    solicitarCategoria(nombre: string, descripcion?: string): Observable<any> {
        return this.http.post(`${this.url}/solicitudes-categoria`, { nombre, descripcion });
    }

    reporteProyectos(fi?: string, ff?: string): Observable<any> {
        let params = new HttpParams();
        if (fi) params = params.set('fechaInicio', fi);
        if (ff) params = params.set('fechaFin', ff);
        return this.http.get(`${this.url}/reportes/proyectos`, { params });
    }
    reporteRecargas(fi?: string, ff?: string): Observable<any> {
        let params = new HttpParams();
        if (fi) params = params.set('fechaInicio', fi);
        if (ff) params = params.set('fechaFin', ff);
        return this.http.get(`${this.url}/reportes/recargas`, { params });
    }
    reporteGastos(fi?: string, ff?: string): Observable<any> {
        let params = new HttpParams();
        if (fi) params = params.set('fechaInicio', fi);
        if (ff) params = params.set('fechaFin', ff);
        return this.http.get(`${this.url}/reportes/gastos-categoria`, { params });
    }
}
