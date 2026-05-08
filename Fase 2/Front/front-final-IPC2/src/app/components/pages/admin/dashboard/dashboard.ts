import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { AdminServicio } from '../../../../servicios/admin.servicio';



const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/admin/dashboard' },
  { label: 'Usuarios', icon: '👥', path: '/admin/usuarios' },
  { label: 'Categorías', icon: '🗂️', path: '/admin/categorias' },
  { label: 'Habilidades', icon: '⚙️', path: '/admin/habilidades' },
  { label: 'Solicitudes', icon: '📬', path: '/admin/solicitudes' },
  { label: 'Comisión', icon: '💹', path: '/admin/comision' },
  { label: 'Reportes', icon: '📈', path: '/admin/reportes' },
];

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterModule, Layout],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit{
  nav = NAV; 
  stats: any = {}; 
  comision: any = null; 
  loading = true;
  
  constructor(private service: AdminServicio) { }

  ngOnInit() {
    Promise.all([
      this.service.getResumenGeneral().toPromise(), 
      this.service.getComision().toPromise()])
      .then(([r, c]: any[]) => { 
        if (r?.success) this.stats = r.data || {}; 
        if (c?.success) this.comision = c.data; 
        this.loading = false; 
      }).catch(() => this.loading = false );
  }
}
