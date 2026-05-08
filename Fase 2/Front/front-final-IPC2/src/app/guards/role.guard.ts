import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthServicio } from '../servicios/auth.servicio';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
    constructor(private auth: AuthServicio, private router: Router) { }
    
    canActivate(route: ActivatedRouteSnapshot): boolean {
        const role: string = route.data['role'] || '';
        if (this.auth.isLoggedIn && this.auth.rol === role) return true;
        this.router.navigate(['/auth/login']);
        return false;
    }
}