import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthServicio } from '../servicios/auth.servicio';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
    constructor(private auth: AuthServicio, private router: Router) { }
    canActivate(route: ActivatedRouteSnapshot): boolean {
        const roles: string[] = route.data['roles'] || [];
        if (this.auth.isLoggedIn && roles.includes(this.auth.rol!)) return true;
        this.router.navigate(['/auth/login']);
        return false;
    }
}