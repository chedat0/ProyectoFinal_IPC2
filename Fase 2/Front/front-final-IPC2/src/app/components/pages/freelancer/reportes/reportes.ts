import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
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
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})

export class Reportes implements OnInit {
  nav = NAV; 
  tab = 'contratos'; 
  datos: any[] = []; 
  loading = false; 
  form!: FormGroup;

  constructor(private service: FreelancerServicio, private fb: FormBuilder) { }
  
  ngOnInit() { 
    this.form = this.fb.group({ 
      fechaInicio: [''], 
      fechaFin: [''] 
    }); 
    this.cargar(); 
  }

  cargar() {
    this.loading = true;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'contratos' ? this.service.reporteContratos(fechaInicio, fechaFin) : this.tab === 'categorias' ? this.service.reporteCategorias() : this.service.reportePropuestas();
    obs.subscribe({ next: (r: any) => { 
      this.datos = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  cambiar(t: string) { 
    this.tab = t; 
    this.cargar();
  }
}
