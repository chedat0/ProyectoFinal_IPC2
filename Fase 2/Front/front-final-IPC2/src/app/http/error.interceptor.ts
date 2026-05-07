import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthServicio } from '../servicios/auth.servicio';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private router: Router, private authServicio: AuthServicio) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError((err: HttpErrorResponse) => {
                if (err.status === 401) {
                    this.authServicio.logout();
                    this.router.navigate(['/auth/login']);
                }
                const message = err.error?.message || err.error?.error || 'Error de conexión con el servidor';
                return throwError(() => new Error(message));
            })
        );
    }
}