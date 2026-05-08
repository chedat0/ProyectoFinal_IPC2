import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { FreelancerServicio } from '../../../../servicios/freelancer.servicio';
import { CatalogoServicio } from '../../../../servicios/catalogo.servicio';
import { TmplAstBoundAttribute } from '@angular/compiler';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/freelancer/dashboard' },
  { label: 'Explorar', icon: '🔍', path: '/freelancer/explorar' },
  { label: 'Propuestas', icon: '📋', path: '/freelancer/propuestas' },
  { label: 'Contratos', icon: '📄', path: '/freelancer/contratos' },
  { label: 'Reportes', icon: '📈', path: '/freelancer/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/freelancer/perfil' },
];

@Component({
  selector: 'app-explorar',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './explorar.html',
  styleUrl: './explorar.css',
})
export class Explorar implements OnInit {
  nav = NAV; 
  proyectos: any[] = []; 
  categorias: any[] = []; 
  habilidades: any[] = [];
  loading = false;
  filterForm!: FormGroup;
  propForm!: FormGroup; 
  proySel: any = null; 
  showModal = false; 
  sending = false; 
  error = ''; 
  success = '';

  constructor(private service: FreelancerServicio, private catalogo: CatalogoServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.filterForm = this.fb.group({ 
      categoriaId: [''], 
      habilidadId: [''], 
      presupuestoMin: [''], 
      presupuestoMax: [''] 
    });
    this.propForm = this.fb.group({ 
      montoOfertado: ['', [Validators.required, Validators.min(1)]], 
      tiempoEntregaDias: ['', [Validators.required, Validators.min(1)]], 
      cartaPresentacion: ['', Validators.required] 
    });
    Promise.all([
      this.catalogo.getCategorias().toPromise(), 
      this.catalogo.getHabilidades().toPromise()])
      .then(([c, h]: any[]) => { 
        this.categorias = c?.data || []; 
        this.habilidades = h?.data || []; 
      });
    this.buscar();
  }

  buscar() {
    this.loading = true;
    const filtros = this.filterForm.value;
    this.service.getProyectos(filtros).subscribe({ next: (r: any) => { 
        this.proyectos = r?.data || []; 
        this.loading = false; 
      }, error: () => this.loading = false });
  }

  limpiar() { 
    this.filterForm.reset(); 
    this.buscar(); 
  }

  abrir(p: any) { 
    this.proySel = p; 
    this.propForm.reset(); 
    this.error = ''; 
    this.success = ''; 
    this.showModal = true; 
  }

  get pf() { 
    return this.propForm.controls; 
  }

  enviar() {
    if (this.propForm.invalid) { 
      this.propForm.markAllAsTouched(); 
      return; 
    }
    this.sending = true; this.error = '';
    const data = { ...this.propForm.value, proyectoId: this.proySel.id };
    this.service.enviarPropuesta(data).subscribe({ next: (r: any) => { if (r?.success) { 
      this.success = 'Propuesta enviada exitosamente.'; 
      this.showModal = false; 
    } else 
      this.error = r?.message || 'Error'; 
      this.sending = false; 
    }, error: (e: any) => { 
      this.error = e?.message || 'Error'; 
      this.sending = false; } });
  }
}
