import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthServicio } from '../servicios/auth.servicio';

@Injectable({ providedIn: 'root' })
export class PerfilGuard implements CanActivate {
    constructor(private auth: AuthServicio, private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot): boolean {
        const user = this.auth.currentUser;
        if (!user) {
            this.router.navigate(['/auth/login']);
            return false;
        }
        if (!user.perfilCompleto) {
            const rol = user.rol?.toLowerCase();            
            if (rol === 'cliente') {
                this.router.navigate(['/cliente/perfil']);
                return false;
            }
            if (rol === 'freelancer') {
                this.router.navigate(['/freelancer/perfil']);
                return false;
            }
        }
        return true;
    }
}