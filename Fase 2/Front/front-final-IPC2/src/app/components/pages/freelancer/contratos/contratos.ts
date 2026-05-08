import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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

@Component({
  selector: 'app-contratos',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './contratos.html',
  styleUrl: './contratos.css',
})

export class Contratos implements OnInit{
  nav = NAV; 
  contratos: any[] = []; 
  loading = true; 
  error = ''; 
  success = '';
  entForm!: FormGroup; 
  contratoActivo = 0; 
  showModal = false; 
  sending = false;

  constructor(private service: FreelancerServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.entForm = this.fb.group({ 
      descripcion: ['', Validators.required] });
    this.service.getContratos().subscribe({ next: (r: any) => { 
      this.contratos = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  abrir(id: number) { 
    this.contratoActivo = id; 
    this.entForm.reset(); 
    this.error = ''; 
    this.showModal = true; 
  }

  get ef() { 
    return this.entForm.controls; 
  }

  enviar() {
    if (this.entForm.invalid) { 
      this.entForm.markAllAsTouched(); 
      return; 
    }
    this.sending = true; 
    this.error = '';
    this.service.enviarEntrega({ 
      contratoId: this.contratoActivo, ...this.entForm.value })
      .subscribe({ next: (r: any) => { if (r?.success) { 
        this.success = 'Entrega enviada. El cliente la revisará pronto.'; 
        this.showModal = false; 
        this.ngOnInit(); 
      } else 
        this.error = r?.message || 'Error'; 
        this.sending = false; 
      }, error: (e: any) => { this.error = e?.message || 'Error'; this.sending = false; } });
  }
} 
