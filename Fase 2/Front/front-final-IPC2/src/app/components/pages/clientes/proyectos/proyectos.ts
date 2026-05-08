import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';
import { CatalogoServicio } from '../../../../servicios/catalogo.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/clientes/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/clientes/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/clientes/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/clientes/recargas' },
  { label: 'Reportes', icon: '📈', path: '/clientes/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/clientes/perfil' },
];

@Component({
  selector: 'app-proyectos',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './proyectos.html',
  styleUrl: './proyectos.css',
})

export class Proyectos implements OnInit{
  nav = NAV; 
  proyectos: any[] = []; 
  categorias: any[] = []; 
  habilidades: any[] = []; 
  selHabs: number[] = [];
  loading = true; 
  showModal = false; 
  editando: any = null;
  form!: FormGroup; 
  saving = false; 
  error = ''; 
  success = '';

  constructor(private service: ClienteServicio, private catalogo: CatalogoServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.form = this.fb.group({ 
      titulo: ['', Validators.required], 
      descripcion: ['', Validators.required], 
      categoriaId: ['', Validators.required], 
      presupuestoMaximo: ['', Validators.required], 
      fechaLimite: [''] 
    });
    Promise.all([
      this.service.getProyectos().toPromise(), 
      this.catalogo.getCategorias().toPromise(), 
      this.catalogo.getHabilidades().toPromise()])
      .then(([p, c, h]: any[]) => { 
        this.proyectos = p?.data || []; 
        this.categorias = c?.data || []; 
        this.habilidades = h?.data || []; 
        this.loading = false; 
      }).catch(() => this.loading = false);
  }

  toggleHab(id: number) { 
    const i = this.selHabs.indexOf(id); 
    i === -1 ? this.selHabs.push(id) : this.selHabs.splice(i, 1); 
  }

  isHab(id: number) { 
    return this.selHabs.includes(id); 
  }

  abrirModal(p?: any) { 
    this.editando = p || null; 
    this.form.reset(); 
    this.selHabs = []; 
    this.error = ''; 
    this.success = ''; 
    if (p) { 
      this.form.patchValue(p); 
      this.selHabs = (p.habilidades || []).map((h: any) => h.id); 
    } this.showModal = true; 
  }

  cerrar() { 
    this.showModal = false; 
  }

  guardar() {
    if (this.form.invalid) { 
      this.form.markAllAsTouched(); 
      return; 
    }

    this.saving = true; 
    this.error = '';
    const data = { ...this.form.value, habilidadIds: this.selHabs };
    const obs = this.editando ? this.service.actualizarProyecto(this.editando.id, data) : this.service.crearProyecto(data);
    obs.subscribe({ next: (r: any) => { 
      if (r?.success) { 
        this.success = 'Guardado.'; 
        this.showModal = false; 
        this.ngOnInit(); 
      } else 
        this.error = r?.message || 'Error'; 
        this.saving = false; 
      }, error: (e: any) => { 
        this.error = e?.message || 'Error'; 
        this.saving = false; 
      } 
    });
  }
  eliminar(id: number) {
    if (!confirm('¿Cancelar este proyecto?')) return;
    this.service.eliminarProyecto(id).subscribe({ next: () => this.ngOnInit() });
  }
}
