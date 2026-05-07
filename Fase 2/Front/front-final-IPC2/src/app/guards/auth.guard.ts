import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthServicio } from '../servicios/auth.servicio';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(private auth: AuthServicio, private router: Router) { }
    canActivate(): boolean {
        if (this.auth.isLoggedIn) return true;
        this.router.navigate(['/auth/login']);
        return false;
    }
}