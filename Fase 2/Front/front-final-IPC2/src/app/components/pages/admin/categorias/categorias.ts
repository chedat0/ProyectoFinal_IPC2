import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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
  selector: 'app-categorias',
  imports: [CommonModule,ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css',
})
export class Categorias implements OnInit {
  nav = NAV; 
  categorias: any[] = []; 
  loading = true; 
  error = ''; 
  success = '';
  form!: FormGroup; 
  editando: any = null; 
  showModal = false; 
  saving = false;

  constructor(private service: AdminServicio, private fb: FormBuilder) { }
  
  ngOnInit() { 
    this.form = this.fb.group({ nombre: ['', Validators.required], descripcion: [''] }); 
      this.service.getCategorias().subscribe({ next: (r: any) => { 
        this.categorias = r?.data || []; 
        this.loading = false; 
      }, 
      error: () => this.loading = false }); 
  }

  nuevo() { 
    this.editando = null; 
    this.form.reset(); 
    this.error = ''; 
    this.showModal = true; 
  }

  editar(c: any) { 
    this.editando = c; 
    this.form.patchValue({ nombre: c.nombre, descripcion: c.descripcion }); 
    this.error = ''; 
    this.showModal = true; 
  }

  guardar() {
    if (this.form.invalid) { 
      this.form.markAllAsTouched(); 
      return; 
    }

    this.saving = true; 
    this.error = '';
    const obs = this.editando ? this.service.actualizarCategoria(this.editando.id, this.form.value) : this.service.crearCategoria(this.form.value);
    obs.subscribe({ next: (r: any) => { if (r?.success) { 
      this.success = this.editando ? 'Categoría actualizada.' : 'Categoría creada.'; 
      this.showModal = false; 
      this.ngOnInit(); 
    } else 
      this.error = r?.message || 'Error'; 
      this.saving = false; 
    }, 
    error: (e: any) => { 
      this.error = e?.message || 'Error'; 
      this.saving = false; 
    } });
  }

  toggle(c: any) { 
    this.service.toggleCategoria(c.id, !c.activa).subscribe({ next: (r: any) => { if (r?.success) c.activa = !c.activa; } }); 
  }

  get f() { 
    return this.form.controls; }
}
