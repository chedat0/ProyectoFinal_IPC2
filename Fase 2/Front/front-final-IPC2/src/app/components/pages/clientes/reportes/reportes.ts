import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
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

const TAB_LABELS: Record<string, string> = {
  proyectos: 'Historial de Proyectos',
  recargas: 'Recargas de Saldo',
  gastos: 'Gastos por Categoría',
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
  tab = 'proyectos'; 
  datos: any[] = []; 
  loading = false;
  form!: FormGroup;
  today = new Date();
  
  constructor(private service: ClienteServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef, private dp: DatePipe) { }
  
  ngOnInit() { 
    this.form = this.fb.group({ 
      fechaInicio: [''], 
      fechaFin: [''] }); 
      this.cargar(); 
  }

  get tabLabel(): string { return TAB_LABELS[this.tab] || ''; }

  get periodoLabel(): string {
    const { fechaInicio, fechaFin } = this.form.value;
    const desde = fechaInicio ? this.dp.transform(fechaInicio, 'dd/MM/yyyy') : '—';
    const hasta = fechaFin ? this.dp.transform(fechaFin, 'dd/MM/yyyy') : '—';
    return `Período: ${desde} al ${hasta}`;
  }

  cargar() {
    this.loading = true;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'proyectos'
      ? this.service.reporteProyectos(fechaInicio, fechaFin)
      : this.tab === 'recargas'
        ? this.service.reporteRecargas(fechaInicio, fechaFin)
        : this.service.reporteGastos(fechaInicio, fechaFin);
    obs.subscribe({
      next: (r: any) => { this.datos = r?.data || []; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  cambiarTab(t: string) { 
    this.tab = t; 
    this.cargar(); 
  }

  exportarPDF() { window.print(); }
  
}
