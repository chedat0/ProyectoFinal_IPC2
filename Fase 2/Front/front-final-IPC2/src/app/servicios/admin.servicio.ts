import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

@Injectable({ providedIn: 'root' })
export class AdminServicio {
    private url = `${backEnd.apiUrl}/admin`;
    constructor(private http: HttpClient) { }

    getUsuarios(): Observable<any> { return this.http.get(`${this.url}/usuarios`); }
    toggleUsuario(id: number): Observable<any> { return this.http.put(`${this.url}/usuarios/${id}/toggle`, {}); }

    getAdministradores(): Observable<any> { return this.http.get(`${this.url}/administradores`); }
    actualizarAdministrador(usuarioId: number, data: any): Observable<any> {
        return this.http.put(`${this.url}/administradores/${usuarioId}`, data);
    }
    
    getCategorias(): Observable<any> { return this.http.get(`${this.url}/categorias`); }
    crearCategoria(data: any): Observable<any> { return this.http.post(`${this.url}/categorias`, data); }
    actualizarCategoria(id: number, data: any): Observable<any> { return this.http.put(`${this.url}/categorias/${id}`, data); }
    toggleCategoria(id: number): Observable<any> { return this.http.put(`${this.url}/categorias/${id}/toggle`, {}); }

    getHabilidades(categoriaId?: number): Observable<any> {
        let params = new HttpParams();
        if (categoriaId) params = params.set('categoriaId', categoriaId);
        return this.http.get(`${this.url}/habilidades`, { params });
    }
    crearHabilidad(data: any): Observable<any> { return this.http.post(`${this.url}/habilidades`, data); }
    actualizarHabilidad(id: number, data: any): Observable<any> { return this.http.put(`${this.url}/habilidades/${id}`, data); }
    toggleHabilidad(id: number): Observable<any> { return this.http.put(`${this.url}/habilidades/${id}/toggle`, {}); }

    getSolicitudesHabilidad(): Observable<any> { return this.http.get(`${this.url}/solicitudes-habilidad`); }
    responderSolicitudHabilidad(id: number, estado: string, comentario?: string): Observable<any> {
        return this.http.put(`${this.url}/solicitudes-habilidad/${id}`, { estado, comentario });
    }

    getSolicitudesCategoria(): Observable<any> { return this.http.get(`${this.url}/solicitudes-categoria`); }
    responderSolicitudCategoria(id: number, estado: string, comentario?: string): Observable<any> {
        return this.http.put(`${this.url}/solicitudes-categoria/${id}`, { estado, comentario });
    }

    getComisionActual(): Observable<any> { return this.http.get(`${this.url}/comision-actual`); }
    getHistorialComision(): Observable<any> { return this.http.get(`${this.url}/comision/historial`); }
    setComision(porcentaje: number): Observable<any> { return this.http.put(`${this.url}/comision`, { porcentaje }); }
    getSaldoGlobal(): Observable<any> { return this.http.get(`${this.url}/saldo-global`); }

    crearAdministrador(data: any): Observable<any> { return this.http.post(`${this.url}/administradores`, data); }

    reporteTopFreelancers(limit?: number): Observable<any> {
        let params = new HttpParams();
        if (limit) params = params.set('limit', limit);
        return this.http.get(`${this.url}/reportes/top-freelancers`, { params });
    }
    reporteTopCategorias(limit?: number): Observable<any> {
        let params = new HttpParams();
        if (limit) params = params.set('limit', limit);
        return this.http.get(`${this.url}/reportes/top-categorias`, { params });
    }
    reporteIngresos(fi?: string, ff?: string): Observable<any> {
        let params = new HttpParams();
        if (fi) params = params.set('fechaInicio', fi);
        if (ff) params = params.set('fechaFin', ff);
        return this.http.get(`${this.url}/reportes/ingresos`, { params });
    }
}