import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { FreelancerServicio } from '../../../../servicios/freelancer.servicio';
import { CatalogoServicio } from '../../../../servicios/catalogo.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/freelancer/dashboard' },
  { label: 'Explorar', icon: '🔍', path: '/freelancer/explorar' },
  { label: 'Propuestas', icon: '📋', path: '/freelancer/propuestas' },
  { label: 'Contratos', icon: '📄', path: '/freelancer/contratos' },
  { label: 'Reportes', icon: '📈', path: '/freelancer/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/freelancer/perfil' },
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
  habilidades: any[] = []; 
  selHabs: number[] = []; 
  loading = true; 
  saving = false; 
  error = ''; 
  success = '';

  constructor(private service: FreelancerServicio, private catalogo: CatalogoServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.form = this.fb.group({ 
      especialidad: ['', Validators.required], 
      descripcion: [''], 
      nivelExperiencia: [''], 
      tarifaHora: ['', Validators.required], 
      portafolioUrl: [''], 
      paisResidencia: ['Guatemala'] 
    });
    Promise.all([
      this.service.getPerfil().toPromise(), 
      this.catalogo.getHabilidades().toPromise()
    ])
      .then(([p, h]: any[]) => {
        if (p?.data) { 
          this.form.patchValue({ 
            especialidad: p.data.especialidad || '', 
            descripcion: p.data.descripcion || '', 
            nivelExperiencia: p.data.nivelExperiencia || '', 
            tarifaHora: p.data.tarifaHora || '', 
            portafolioUrl: p.data.portafolioUrl || '', 
            paisResidencia: p.data.paisResidencia || 'Guatemala' }); 
            this.selHabs = (p.data.habilidades || []).map((h: any) => h.id); }
        this.habilidades = h?.data || []; 
        this.loading = false;
      }).catch(() => this.loading = false);
  }

  toggle(id: number) { 
    const i = this.selHabs.indexOf(id); 
    i === -1 ? this.selHabs.push(id) : this.selHabs.splice(i, 1); 
  }

  isSel(id: number) { 
    return this.selHabs.includes(id); 
  }

  get f() { 
    return this.form.controls; 
  }

  save() {
    if (this.form.invalid) { 
      this.form.markAllAsTouched(); 
      return; 
    }

    this.saving = true; 
    this.error = ''; 
    this.success = '';
    const data = { ...this.form.value, habilidadIds: this.selHabs };
    this.service.actualizarPerfil(data).subscribe({ next: (r: any) => { 
      if (r?.success) 
        this.success = 'Perfil actualizado.'; 
      else this.error = r?.message || 'Error'; 
      this.saving = false; 
    }, error: (e: any) => { 
      this.error = e?.message || 'Error'; 
      this.saving = false; 
    } });
  }
}
