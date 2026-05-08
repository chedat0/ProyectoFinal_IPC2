import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/clientes/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/clientes/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/clientes/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/clientes/recargas' },
  { label: 'Reportes', icon: '📈', path: '/clientes/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/clientes/perfil' },
];

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})

export class Reportes implements OnInit {
  nav = NAV; 
  tab = 'proyectos'; 
  datos: any[] = []; 
  loading = false;
  form!: FormGroup;
  
  constructor(private service: ClienteServicio, private fb: FormBuilder) { }
  
  ngOnInit() { 
    this.form = this.fb.group({ 
      fechaInicio: [''], 
      fechaFin: [''] }); 
      this.cargar(); 
  }

  cargar() {
    this.loading = true;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'proyectos' ? this.service.reporteProyectos(fechaInicio, fechaFin) : this.tab === 'recargas' ? this.service.reporteRecargas() : this.service.reporteGastos(fechaInicio, fechaFin);
    obs.subscribe({ next: (r: any) => { 
      this.datos = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  cambiarTab(t: string) { 
    this.tab = t; 
    this.cargar(); 
  }
  
}
