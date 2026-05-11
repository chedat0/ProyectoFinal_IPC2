import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { AdminServicio } from '../../../../servicios/admin.servicio';

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
  selector: 'app-usuarios',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, Layout],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})

export class Usuarios implements OnInit {

  nav = NAV;
  usuarios: any[] = [];
  loading = true;
  error = '';
  success = '';
  filtro = '';
  rolFiltro = '';

  showModal = false;
  editMode = false;
  adminEditId: number | null = null;
  saving = false;
  adminForm!: FormGroup;

  showPerfil = false;
  perfilLoading = false;
  perfilData: any = null;

  constructor(private service: AdminServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.adminForm = this.fb.group({
      nombre_completo: ['', Validators.required],
      username: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      telefono: [''],
      cui: [''],
      nivelAcceso: ['ESTANDAR', Validators.required],
    });
    this.cargar();
  }

  cargar() {
    this.loading = true;
    this.service.getUsuarios().subscribe({
      next: (r: any) => { this.usuarios = r?.data || []; this.loading = false; this.cdr.detectChanges();         
      },
      error: () => this.loading = false,
    });
  }

  toggle(u: any) {
    if (!confirm(`¿${u.activo ? 'Desactivar' : 'Activar'} a ${u.nombreCompleto}?`)) return;
    this.service.toggleUsuario(u.id).subscribe({
      next: (r: any) => {
        if (r?.success) { u.activo = !u.activo; this.success = 'Usuario actualizado.'; }
        else this.error = r?.message || 'Error';
      },
      error: (e: any) => this.error = e?.message || 'Error',
    });
  }

  get filtrados() {
    return this.usuarios.filter(u =>
      (!this.filtro ||
        u.nombreCompleto?.toLowerCase().includes(this.filtro.toLowerCase()) ||
        u.correo?.toLowerCase().includes(this.filtro.toLowerCase())) &&
      (!this.rolFiltro || u.tipoUsuario === this.rolFiltro)
    );
  }

  verPerfil(u: any) {
    this.perfilData = null;
    this.perfilLoading = true;
    this.showPerfil = true;
    this.service.getUsuarioPerfil(u.id).subscribe({
      next: (r: any) => { this.perfilData = r?.data || r; this.perfilLoading = false; },
      error: () => { this.perfilLoading = false; },
    });
  }
  
  abrirCrear() {
    this.editMode = false;
    this.adminEditId = null;
    this.adminForm.reset({ nivelAcceso: 'ESTANDAR' });
    this.adminForm.get('username')!.setValidators(Validators.required);
    this.adminForm.get('password')!.setValidators(Validators.required);
    this.adminForm.get('username')!.updateValueAndValidity();
    this.adminForm.get('password')!.updateValueAndValidity();
    this.error = ''; this.success = '';
    this.showModal = true;
  }

  abrirEditar(u: any) {
    this.editMode = true;
    this.adminEditId = u.id;
    this.adminForm.get('username')!.clearValidators();
    this.adminForm.get('password')!.clearValidators();
    this.adminForm.get('username')!.updateValueAndValidity();
    this.adminForm.get('password')!.updateValueAndValidity();
    this.adminForm.patchValue({
      nombre_completo: u.nombreCompleto || '',
      username: u.username || '',
      correo: u.correo || '',
      password: '',
      telefono: u.telefono || '',
      cui: u.cui || '',
      nivelAcceso: u.nivelAcceso || 'ESTANDAR',
    });
    this.error = ''; this.success = '';
    this.showModal = true;
  }

  guardar() {
    if (this.adminForm.invalid) { this.adminForm.markAllAsTouched(); return; }
    this.saving = true; this.error = '';
    const v = this.adminForm.value;

    if (!this.editMode) {
      this.service.crearAdministrador({
        username: v.username,
        correo: v.correo,
        password: v.password,
        nombre_completo: v.nombre_completo,
        telefono: v.telefono || null,
        cui: v.cui || null,
        nivelAcceso: v.nivelAcceso,
      }).subscribe({
        next: (r: any) => {
          this.saving = false;
          if (r?.success !== false) {
            this.success = 'Administrador creado exitosamente.';
            this.showModal = false;
            this.cargar();
            this.cdr.detectChanges();
          } else this.error = r?.message || 'Error al crear';
        },
        error: (e: any) => { this.saving = false; this.error = e?.error?.message || e?.message || 'Error'; },
      });
    } else {
      const data: any = {
        nombre_completo: v.nombre_completo,
        correo: v.correo,
        telefono: v.telefono || null,
        nivelAcceso: v.nivelAcceso,
      };
      if (v.password) data['password'] = v.password;

      this.service.actualizarAdministrador(this.adminEditId!, data).subscribe({
        next: (r: any) => {
          this.saving = false;
          if (r?.success !== false) {
            this.success = 'Administrador actualizado.';
            this.showModal = false;
            this.cargar();
            this.cdr.detectChanges();
          } else this.error = r?.message || 'Error al actualizar';
        },
        error: (e: any) => { this.saving = false; this.error = e?.error?.message || e?.message || 'Error'; },
      });
    }
  }
}
