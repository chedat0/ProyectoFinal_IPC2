import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';
import { AuthServicio } from '../../../../servicios/auth.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/cliente/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/cliente/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/cliente/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/cliente/recargas' },
  { label: 'Reportes', icon: '📈', path: '/cliente/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/cliente/perfil' },
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
  editando = false;
  perfilData: any = null;
  success = '';
  error = '';

  showSolModal = false;
  solForm!: FormGroup;
  solSaving = false;
  solSuccess = '';
  solError = '';

  constructor(private service: ClienteServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef, private auth: AuthServicio) { }

  ngOnInit() {
    this.form = this.fb.group({
      nombreEmpresa: [''], descripcion: [''], sector: [''], sitioWeb: [''], pais: ['Guatemala'],
    });
    this.solForm = this.fb.group({ nombre: ['', Validators.required], descripcion: [''] });
    this.service.getPerfil().subscribe({
      next: (r: any) => {
        if (r?.data) {
          this.perfilData = r.data;
          this.form.patchValue({
            nombreEmpresa: r.data.nombreEmpresa || '', descripcion: r.data.descripcion || '',
            sector: r.data.sector || '', sitioWeb: r.data.sitioWeb || '', pais: r.data.pais || 'Guatemala',
          });
          this.editando = !r.data.nombreEmpresa && !r.data.descripcion;
        } else { this.editando = true; }
        this.loading = false; this.cdr.detectChanges();
      },
      error: () => { this.editando = true; this.loading = false; this.cdr.detectChanges(); },
    });
  }

  get f() { return this.form.controls; }
  iniciarEdicion() { this.editando = true; this.error = ''; this.success = ''; }
  cancelarEdicion() {
    if (this.perfilData) this.form.patchValue({
      nombreEmpresa: this.perfilData.nombreEmpresa || '',
      descripcion: this.perfilData.descripcion || '', sector: this.perfilData.sector || '',
      sitioWeb: this.perfilData.sitioWeb || '', pais: this.perfilData.pais || 'Guatemala'
    });
    this.editando = false; this.error = '';
  }

  save() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = ''; this.success = '';
    this.service.updatePerfil(this.form.value).subscribe({
      next: (r: any) => {
        if (r?.success) { this.success = 'Perfil actualizado.'; this.perfilData = { ...this.perfilData, ...this.form.value }; this.editando = false; }
        if (!this.perfilData?.perfilCompleto) { 
          this.auth.completarPerfil();
        } else this.error = r?.message || 'Error';
        this.saving = false; this.cdr.detectChanges();
      },
      error: (e: any) => { this.error = e?.message || 'Error'; this.saving = false; this.cdr.detectChanges(); },
    });
  }

  abrirSolicitud() { this.solForm.reset(); this.solError = ''; this.solSuccess = ''; this.showSolModal = true; }

  enviarSolicitud() {
    if (this.solForm.invalid) { this.solForm.markAllAsTouched(); return; }
    this.solSaving = true; this.solError = '';
    const v = this.solForm.value;
    this.service.solicitarCategoria(v.nombre, v.descripcion).subscribe({
      next: (r: any) => {
        this.solSaving = false;
        if (r?.success !== false) { this.solSuccess = 'Solicitud enviada. El administrador la revisará pronto.'; this.solForm.reset(); }
        else this.solError = r?.message || 'Error al enviar';
        this.cdr.detectChanges();
      },
      error: (e: any) => { this.solSaving = false; this.solError = e?.error?.message || e?.message || 'Error'; this.cdr.detectChanges(); },
    });
  }
}