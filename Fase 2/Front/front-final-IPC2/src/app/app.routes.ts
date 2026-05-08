import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

    // Autenticación 
    {
        path: 'auth/login',
        loadComponent: () => import('./components/pages/auth/login/login').then(m => m.Login)
    },
    {
        path: 'auth/registro',
        loadComponent: () => import('./components/pages/auth/seleccion-rol/seleccion-rol').then(m => m.SeleccionRol)
    },
    {
        path: 'auth/registro/cliente',
        loadComponent: () => import('./components/pages/auth/registro-cliente/registro-cliente').then(m => m.RegistroCliente)
    },
    {
        path: 'auth/registro/freelancer',
        loadComponent: () => import('./components/pages/auth/registro-freelancer/registro-freelancer').then(m => m.RegistroFreelancer)
    },

    // Cliente 
    {
        path: 'cliente/dashboard', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/dashboard/dashboard').then(m => m.Dashboard)
    },
    {
        path: 'cliente/perfil', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/perfil/perfil').then(m => m.Perfil)
    },
    {
        path: 'cliente/proyectos', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/proyectos/proyectos').then(m => m.Proyectos)
    },
    {
        path: 'cliente/proyectos/:id/propuestas', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/propuestas/propuestas').then(m => m.Propuestas)
    },
    {
        path: 'cliente/contratos', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/contratos/contratos').then(m => m.Contratos)
    },
    {
        path: 'cliente/recargas', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/recargas/recargas').then(m => m.Recargas)
    },
    {
        path: 'cliente/reportes', canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./components/pages/clientes/reportes/reportes').then(m => m.Reportes)
    },

    // Freelancer
    {
        path: 'freelancer/dashboard', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/clientes/dashboard/dashboard').then(m => m.Dashboard)
    },
    {
        path: 'freelancer/perfil', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/freelancer/perfil/perfil').then(m => m.Perfil)
    },
    {
        path: 'freelancer/explorar', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/freelancer/explorar/explorar').then(m => m.Explorar)
    },
    {
        path: 'freelancer/propuestas', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/freelancer/propuestas/propuestas').then(m => m.Propuestas)
    },
    {
        path: 'freelancer/contratos', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/freelancer/contratos/contratos').then(m => m.Contratos)
    },
    {
        path: 'freelancer/reportes', canActivate: [AuthGuard, RoleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./components/pages/freelancer/reportes/reportes').then(m => m.Reportes)
    },

    // Admin
    {
        path: 'admin/dashboard', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/dashboard/dashboard').then(m => m.Dashboard)
    },
    {
        path: 'admin/usuarios', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/usuarios/usuarios').then(m => m.Usuarios)
    },
    {
        path: 'admin/categorias', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/categorias/categorias').then(m => m.Categorias)
    },
    {
        path: 'admin/habilidades', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/habilidades/habilidades').then(m => m.Habilidades)
    },
    {
        path: 'admin/solicitudes', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/solicitudes/solicitudes').then(m => m.Solicitudes)
    },
    {
        path: 'admin/comision', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/comision/comision').then(m => m.Comision)
    },
    {
        path: 'admin/reportes', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./components/pages/admin/reportes/reportes').then(m => m.Reportes)
    },

    { path: '**', redirectTo: 'auth/login' }
];
