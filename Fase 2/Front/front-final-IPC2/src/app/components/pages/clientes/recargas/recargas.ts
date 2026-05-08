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
  selector: 'app-recargas',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './recargas.html',
  styleUrl: './recargas.css',
})

export class Recargas implements OnInit {
  nav = NAV; 
  historial: any[] = []; 
  loading = true; 
  saving = false; 
  error = ''; 
  success = '';
  form!: FormGroup;

  constructor(private service: ClienteServicio, private fb: FormBuilder) { }
  
  ngOnInit() {
    this.form = this.fb.group({ 
      monto: ['', [Validators.required, Validators.min(1)]], 
      metodoPago: ['TRANSFERENCIA', Validators.required], 
      referencia: [''] 
    });
    this.service.getRecargas().subscribe({ next: (r: any) => { 
      this.historial = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  recargar() {
    if (this.form.invalid) { 
      this.form.markAllAsTouched(); 
      return; 
    }

    this.saving = true; 
    this.error = ''; 
    this.success = '';

    const { monto, metodoPago, referencia } = this.form.value;
    
    this.service.recargarSaldo(monto, metodoPago, referencia).subscribe({ next: (r: any) => { 
      if (r?.success) { 
        this.success = 'Saldo recargado exitosamente.'; 
        this.form.reset({ metodoPago: 'TRANSFERENCIA' }); 
        this.ngOnInit(); 
      } else this.error = r?.message || 'Error'; this.saving = false; 
    }, 
    error: (e: any) => { this.error = e?.message || 'Error'; this.saving = false; 
    } });
  }
  
  get f() { 
    return this.form.controls; 
  }
  
}
