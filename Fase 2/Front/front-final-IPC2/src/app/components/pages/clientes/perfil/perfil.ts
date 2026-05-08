import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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
  selector: 'app-perfil',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  nav = NAV; 
  form!: FormGroup; 
  loading = true; 
  saving = false; 
  success = ''; 
  error = '';

  constructor(private service: ClienteServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.form = this.fb.group({ 
      nombreEmpresa: [''], 
      descripcion: [''], 
      sector: [''], 
      sitioWeb: [''], 
      pais: ['Guatemala'] });
    this.service.getPerfil().subscribe({ next: (r: any) => { if (r?.data) this.form.patchValue({ 
      nombreEmpresa: r.data.nombreEmpresa || '', 
      descripcion: r.data.descripcion || '', 
      sector: r.data.sector || '', 
      sitioWeb: r.data.sitioWeb || '', 
      pais: r.data.pais || 'Guatemala' }); 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  get f() { 
    return this.form.controls; 
  }

  save() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = ''; this.success = '';
    this.service.updatePerfil(this.form.value).subscribe({ next: (r: any) => { 
      if (r?.success) this.success = 'Perfil actualizado correctamente.'; 
      else this.error = r?.message || 'Error'; 
      this.saving = false; 
    }, error: (e: any) => { 
      this.error = e?.message || 'Error'; 
      this.saving = false; 
    } });
  }
}
