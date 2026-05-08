import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/cliente/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/cliente/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/cliente/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/cliente/recargas' },
  { label: 'Reportes', icon: '📈', path: '/cliente/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/cliente/perfil' },
];

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterModule, Layout],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  nav = NAV;
  stats = { proyectos: 0, activos: 0, contratos: 0, saldo: 0 };
  proyectos: any[] = []; 
  contratos: any[] = []; 
  loading = true;

  constructor(private service: ClienteServicio) { }
  
  ngOnInit() {
    Promise.all([this.service.getProyectos().toPromise(), this.service.getContratos().toPromise(), this.service.getPerfil().toPromise()])
      .then(([p, c, perf]: any[]) => {
        const proy = p?.data || []; const cont = c?.data || [];
        this.proyectos = proy.slice(0, 4); 
        this.contratos = cont.slice(0, 4);
        this.stats.proyectos = proy.length; 
        this.stats.activos = proy.filter((x: any) => x.estado === 'ABIERTO').length;
        this.stats.contratos = cont.length; 
        this.stats.saldo = perf?.data?.saldoDisponible || 0;
        this.loading = false;
      }).catch(() => this.loading = false);
  }
}
