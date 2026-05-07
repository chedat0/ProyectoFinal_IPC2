import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { backEnd } from '../app.config';
import { AuthUser } from '../modelos/authUser';

@Injectable({ providedIn: 'root' })
export class AuthServicio {
    private apiUrl = `${backEnd.apiUrl}/auth`;
    private _currentUser = new BehaviorSubject<AuthUser | null>(this.loadFromStorage());

    currentUser$ = this._currentUser.asObservable();

    constructor(private http: HttpClient, private router: Router) { }

    get currentUser(): AuthUser | null { return this._currentUser.value; }
    get isLoggedIn(): boolean { return !!this._currentUser.value; }
    get token(): string | null { return this._currentUser.value?.token ?? null; }
    get rol(): string | null { return this._currentUser.value?.rol ?? null; }

    login(username: string, password: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/login`, { username, password }).pipe(
            tap(res => {
                if (res.success && res.data) this.setUser(res.data);
            })
        );
    }

    register(payload: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, payload).pipe(
            tap(res => {
                if (res.success && res.data) this.setUser(res.data);
            })
        );
    }

    logout(): void {
        localStorage.removeItem('cw_user');
        this._currentUser.next(null);
        this.router.navigate(['/auth/login']);
    }

    private setUser(user: AuthUser): void {
        localStorage.setItem('cw_user', JSON.stringify(user));
        this._currentUser.next(user);
    }

    private loadFromStorage(): AuthUser | null {
        try {
            const raw = localStorage.getItem('cw_user');
            return raw ? JSON.parse(raw) : null;
        } catch { return null; }
    }
}
