import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { FreelancerServicio } from '../../../../servicios/freelancer.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/freelancer/dashboard' },
  { label: 'Explorar', icon: '🔍', path: '/freelancer/explorar' },
  { label: 'Propuestas', icon: '📋', path: '/freelancer/propuestas' },
  { label: 'Contratos', icon: '📄', path: '/freelancer/contratos' },
  { label: 'Reportes', icon: '📈', path: '/freelancer/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/freelancer/perfil' },
];

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterModule, Layout],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit{
  nav = NAV; 
  stats = { propuestas: 0, contratos: 0, calificacion: 0, saldo: 0 };
  contratos: any[] = []; 
  propuestas: any[] = []; 
  loading = true;

  constructor(private service: FreelancerServicio) { }
  
  ngOnInit() {
    Promise.all([
      this.service.getPerfil().toPromise(), 
      this.service.getContratos().toPromise(), 
      this.service.getPropuestas().toPromise()])
      .then(([p, c, pr]: any[]) => {
        if (p?.data) { 
          this.stats.calificacion = p.data.calificacionPromedio || 0; 
          this.stats.saldo = p.data.saldo || 0; 
        }
        this.contratos = (c?.data || []).slice(0, 4); 
        this.stats.contratos = c?.data?.length || 0;
        this.propuestas = (pr?.data || []).slice(0, 4); 
        this.stats.propuestas = pr?.data?.length || 0;
        this.loading = false;
      }).catch(() => this.loading = false);
  }
  
}
