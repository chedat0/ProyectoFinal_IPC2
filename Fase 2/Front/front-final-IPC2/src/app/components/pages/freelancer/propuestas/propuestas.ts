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
  selector: 'app-propuestas',
  imports: [CommonModule, RouterModule, Layout],
  templateUrl: './propuestas.html',
  styleUrl: './propuestas.css',
})
export class Propuestas implements OnInit {
  nav = NAV; 
  propuestas: any[] = []; 
  loading = true; 
  error = ''; 
  success = '';

  constructor(private service: FreelancerServicio) { }
  
  ngOnInit() { 
    this.service.getPropuestas().subscribe({ next: (r: any) => { 
      this.propuestas = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false }); 
  }

  retirar(id: number) {
    if (!confirm('¿Retirar esta propuesta?')) 
      return;
    this.service.retirarPropuesta(id).subscribe({ next: (r: any) => { 
      if (r?.success) { 
        this.success = 'Propuesta retirada.'; 
        this.ngOnInit(); 
      } else 
        this.error = r?.message || 'Error'; 
      }, error: (e: any) => this.error = e?.message || 'Error' });
  }
}
