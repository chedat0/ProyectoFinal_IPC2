import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backEnd } from '../app.config';

@Injectable({ providedIn: 'root' })
export class CatalogoServicio {
    private url = backEnd.apiUrl;
    constructor(private http: HttpClient) { }

    getCategorias(): Observable<any> { return this.http.get(`${this.url}/admin/categorias`); }
    getHabilidades(categoriaId?: number): Observable<any> {
        let params = new HttpParams();
        if (categoriaId) params = params.set('categoriaId', categoriaId);
        return this.http.get(`${this.url}/admin/habilidades`, { params });
    }
}
