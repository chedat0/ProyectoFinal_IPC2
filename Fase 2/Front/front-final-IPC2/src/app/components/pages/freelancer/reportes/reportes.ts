import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe} from '@angular/common';
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

const TAB_LABELS: Record<string, string> = {
  contratos: 'Contratos Completados',
  categorias: 'Ingresos por Categoría',
  propuestas: 'Historial de Propuestas',
};

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
  providers: [DatePipe],
})

export class Reportes implements OnInit {
  nav = NAV; 
  tab = 'contratos'; 
  datos: any[] = []; 
  loading = false; 
  form!: FormGroup;
  today = new Date ();

  constructor(private service: FreelancerServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef, private dp: DatePipe) { }
  
  get tabLabel(): string { return TAB_LABELS[this.tab] || ''; }

  get periodoLabel(): string {
    const { fechaInicio, fechaFin } = this.form.value;
    const desde = fechaInicio ? this.dp.transform(fechaInicio, 'dd/MM/yyyy') : '—';
    const hasta = fechaFin ? this.dp.transform(fechaFin, 'dd/MM/yyyy') : '—';
    return `Período: ${desde} al ${hasta}`;
  }

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
    const obs = this.tab === 'contratos'
      ? this.service.reporteContratos(fechaInicio, fechaFin)
      : this.tab === 'categorias'
        ? this.service.reporteCategorias()
        : this.service.reportePropuestas();
    obs.subscribe({
      next: (r: any) => { this.datos = r?.data || []; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }
    
  cambiar(t: string) { 
    this.tab = t; 
    this.cargar();
  }

  exportarPDF() { window.print(); }
}
