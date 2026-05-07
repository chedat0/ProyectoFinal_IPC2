import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

    // ── Auth ──────────────────────────────────────────────────────
    {
        path: 'auth/login',
        loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent)
    },
    {
        path: 'auth/registro',
        loadComponent: () => import('./auth/role-picker/role-picker.component').then(m => m.RolePickerComponent)
    },
    {
        path: 'auth/registro/cliente',
        loadComponent: () => import('./auth/register-cliente/register-cliente.component').then(m => m.RegisterClienteComponent)
    },
    {
        path: 'auth/registro/freelancer',
        loadComponent: () => import('./auth/register-freelancer/register-freelancer.component').then(m => m.RegisterFreelancerComponent)
    },

    // ── Cliente ───────────────────────────────────────────────────
    {
        path: 'cliente/dashboard', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/dashboard/dashboard.component').then(m => m.ClienteDashboardComponent)
    },
    {
        path: 'cliente/perfil', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/perfil/perfil.component').then(m => m.ClientePerfilComponent)
    },
    {
        path: 'cliente/proyectos', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/proyectos/proyectos.component').then(m => m.ClienteProyectosComponent)
    },
    {
        path: 'cliente/proyectos/:id/propuestas', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/propuestas/propuestas.component').then(m => m.ClientePropuestasComponent)
    },
    {
        path: 'cliente/contratos', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/contratos/contratos.component').then(m => m.ClienteContratosComponent)
    },
    {
        path: 'cliente/recargas', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/recargas/recargas.component').then(m => m.ClienteRecargasComponent)
    },
    {
        path: 'cliente/reportes', canActivate: [authGuard, roleGuard], data: { role: 'CLIENTE' },
        loadComponent: () => import('./cliente/reportes/reportes.component').then(m => m.ClienteReportesComponent)
    },

    // ── Freelancer ────────────────────────────────────────────────
    {
        path: 'freelancer/dashboard', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/dashboard/dashboard.component').then(m => m.FreelancerDashboardComponent)
    },
    {
        path: 'freelancer/perfil', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/perfil/perfil.component').then(m => m.FreelancerPerfilComponent)
    },
    {
        path: 'freelancer/explorar', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/explorar/explorar.component').then(m => m.FreelancerExplorarComponent)
    },
    {
        path: 'freelancer/propuestas', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/propuestas/propuestas.component').then(m => m.FreelancerPropuestasComponent)
    },
    {
        path: 'freelancer/contratos', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/contratos/contratos.component').then(m => m.FreelancerContratosComponent)
    },
    {
        path: 'freelancer/reportes', canActivate: [authGuard, roleGuard], data: { role: 'FREELANCER' },
        loadComponent: () => import('./freelancer/reportes/reportes.component').then(m => m.FreelancerReportesComponent)
    },

    // ── Admin ─────────────────────────────────────────────────────
    {
        path: 'admin/dashboard', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/dashboard/dashboard.component').then(m => m.AdminDashboardComponent)
    },
    {
        path: 'admin/usuarios', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/usuarios/usuarios.component').then(m => m.AdminUsuariosComponent)
    },
    {
        path: 'admin/categorias', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/categorias/categorias.component').then(m => m.AdminCategoriasComponent)
    },
    {
        path: 'admin/habilidades', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/habilidades/habilidades.component').then(m => m.AdminHabilidadesComponent)
    },
    {
        path: 'admin/solicitudes', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/solicitudes/solicitudes.component').then(m => m.AdminSolicitudesComponent)
    },
    {
        path: 'admin/comision', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/comision/comision.component').then(m => m.AdminComisionComponent)
    },
    {
        path: 'admin/reportes', canActivate: [authGuard, roleGuard], data: { role: 'ADMINISTRADOR' },
        loadComponent: () => import('./admin/reportes/reportes.component').then(m => m.AdminReportesComponent)
    },

    { path: '**', redirectTo: 'auth/login' }
];
