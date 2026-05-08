import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { AdminServicio } from '../../../../servicios/admin.servicio';
import { HttpClient } from '@angular/common/http';
import { backEnd } from '../../../../app.config';

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
  selector: 'app-solicitudes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './solicitudes.html',
  styleUrl: './solicitudes.css',
})

export class Solicitudes implements OnInit {
  nav = NAV;
  tab = 'habilidades';
  solHabilidades: any[] = [];
  solCategorias: any[] = [];
  categorias: any[] = [];
  loading = true;
  error = '';
  success = '';
  respForm!: FormGroup;
  solSel: any = null;
  showModal = false;
  saving = false;

  constructor(private service: AdminServicio, private fb: FormBuilder, private http: HttpClient) { }

  ngOnInit() {
    this.respForm = this.fb.group({ estado: ['ACEPTADA', Validators.required], comentario: [''], categoriaId: [''] });
    Promise.all([this.service.getSolicitudesHabilidad().toPromise(), this.service.getSolicitudesCategoria().toPromise(), this.service.getCategorias().toPromise()])
      .then(([sh, sc, c]: any[]) => {
        this.solHabilidades = sh?.data || [];
        this.solCategorias = sc?.data || [];
        this.categorias = c?.data || [];
        this.loading = false;
      }).catch(() => this.loading = false);
  }

  abrir(s: any) {
    this.solSel = s;
    this.respForm.reset({ estado: 'ACEPTADA' });
    this.error = '';
    this.showModal = true;
  }

  responder() {
    if (this.respForm.invalid) {
      this.respForm.markAllAsTouched();
      return;
    }

    const { estado, comentario, categoriaId } = this.respForm.value;
    if (
      this.tab === 'habilidades' && estado === 'ACEPTADA' && !categoriaId) {
      this.error = 'Debes seleccionar una categoría para la habilidad';
      return;
    }

    this.saving = true;
    this.error = '';
    const obs = this.tab === 'habilidades'
      ? this.http.put(`${backEnd.apiUrl}/admin/solicitudes-habilidad/${this.solSel.id}`, { estado, comentario, categoriaId })
      : this.service.responderSolicitudCategoria(this.solSel.id, estado, comentario);
    obs.subscribe({
      next: (r: any) => {
        if (r?.success) {
          this.success = 'Solicitud procesada.';
          this.showModal = false;
          this.ngOnInit();
        } else
          this.error = r?.message || 'Error';
        this.saving = false;
      },
      error: (e: any) => {
        this.error = e?.message || 'Error';
        this.saving = false;
      }
    });
  }

  get rf() {
    return this.respForm.controls;
  }

  get estadoVal() {
    return this.respForm.get('estado')?.value;
  }

  pendientesH() {
    return this.solHabilidades.filter(s => s.estado === 'PENDIENTE');
  }

  pendientesC() {
    return this.solCategorias.filter(s => s.estado === 'PENDIENTE');
  }

}
