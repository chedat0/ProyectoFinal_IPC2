import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

@Injectable({ providedIn: 'root' })
export class FreelancerServicio {
    private url = `${backEnd.apiUrl}/freelancer`;
    constructor(private http: HttpClient) { }

    getPerfil(): Observable<any> { return this.http.get(`${this.url}/perfil`); }
    actualizarPerfil(data: any): Observable<any> { return this.http.put(`${this.url}/perfil`, data); }
    actualizarHabilidades(habilidadIds: number[]): Observable<any> {
        return this.http.put(`${this.url}/habilidades`, { habilidadIds });
    }

    getProyectos(filtros?: any): Observable<any> {
        let params = new HttpParams();
        if (filtros) {
            if (filtros.categoriaId) params = params.set('categoriaId', filtros.categoriaId);
            if (filtros.habilidadId) params = params.set('habilidadId', filtros.habilidadId);
            if (filtros.presupuestoMin) params = params.set('presupuestoMin', filtros.presupuestoMin);
            if (filtros.presupuestoMax) params = params.set('presupuestoMax', filtros.presupuestoMax);
        }
        return this.http.get(`${this.url}/proyectos`, { params });
    }

    getPropuestas(): Observable<any> { return this.http.get(`${this.url}/propuestas`); }
    enviarPropuesta(data: any): Observable<any> { return this.http.post(`${this.url}/propuestas`, data); }
    eliminarPropuesta(id: number): Observable<any> { return this.http.delete(`${this.url}/propuestas/${id}`); }

    getContratos(): Observable<any> { return this.http.get(`${this.url}/contratos`); }
    getContrato(id: number): Observable<any> { return this.http.get(`${this.url}/contratos/${id}`); }

    getEntregas(contratoId: number): Observable<any> {
        return this.http.get(`${this.url}/contratos/${contratoId}/entregas`);
    }
    crearEntrega(contratoId: number, data: any): Observable<any> {
        return this.http.post(`${this.url}/contratos/${contratoId}/entregas`, data);
    }

    solicitarHabilidad(data: any): Observable<any> {
        return this.http.post(`${this.url}/solicitudes-habilidad`, data);
    }

    reporteContratos(fi?: string, ff?: string): Observable<any> {
        let params = new HttpParams();
        if (fi) params = params.set('fechaInicio', fi);
        if (ff) params = params.set('fechaFin', ff);
        return this.http.get(`${this.url}/reportes/contratos`, { params });
    }
    reporteCategorias(): Observable<any> { return this.http.get(`${this.url}/reportes/categorias`); }
    reportePropuestas(): Observable<any> { return this.http.get(`${this.url}/reportes/propuestas`); }
}