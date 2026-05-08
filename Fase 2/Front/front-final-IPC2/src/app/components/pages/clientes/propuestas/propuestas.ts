import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/cliente/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/cliente/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/cliente/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/cliente/recargas' },
  { label: 'Reportes', icon: '📈', path: '/cliente/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/cliente/perfil' },
];

@Component({
  selector: 'app-propuestas',
  imports: [CommonModule, RouterModule, Layout],
  templateUrl: './propuestas.html',
  styleUrl: './propuestas.css',
})

export class Propuestas implements OnInit{
  nav = NAV; 
  propuestas: any[] = []; 
  proyectoId = 0; 
  loading = true; 
  error = ''; 
  success = '';

  constructor(private service: ClienteServicio, private route: ActivatedRoute) { }
  
  ngOnInit() {
    this.proyectoId = +this.route.snapshot.params['id'];
    this.service.getPropuestasProyecto(this.proyectoId).subscribe({ next: (r: any) => { 
      this.propuestas = r?.data || []; 
      this.loading = false; 
    }, error: () => this.loading = false });
  }

  aceptar(id: number) {
    if (!confirm('¿Aceptar esta propuesta? Se rechazarán las demás.')) return;
    this.service.aceptarPropuesta(id).subscribe({ next: (r: any) => { if (r?.success) { 
        this.success = 'Propuesta aceptada. Contrato creado.'; 
        this.ngOnInit(); 
      } else this.error = r?.message || 'Error'; 
    }, error: (e: any) => this.error = e?.message || 'Error' });
  }

  rechazar(id: number) {
    this.service.rechazarPropuesta(id).subscribe({ next: () => this.ngOnInit() });
  }
}
