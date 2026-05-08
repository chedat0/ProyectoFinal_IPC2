import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
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
  selector: 'app-contratos',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './contratos.html',
  styleUrl: './contratos.css',
})

export class Contratos implements OnInit {
  nav = NAV;
  contratos: any[] = [];
  entregas: any[] = [];
  contratoSel: any = null;
  loading = true;
  error = '';
  success = '';
  rechForm!: FormGroup;
  showRechModal = false;
  entregaRechId = 0;
  calForm!: FormGroup;
  showCalModal = false;
  contratoCalId = 0;

  constructor(private service: ClienteServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.rechForm = this.fb.group({ comentario: ['', Validators.required] });
    this.calForm = this.fb.group({ estrellas: [5, [Validators.required, Validators.min(1), Validators.max(5)]], comentario: [''] });
    this.service.getContratos().subscribe({
      next: (r: any) => {
        this.contratos = r?.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      }, error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  stars(n: number): string {
    return '★'.repeat(n) + '☆'.repeat(5 - n);
  }

  verEntregas(c: any) {
    this.contratoSel = this.contratoSel?.id === c.id ? null : c;
    if (this.contratoSel) this.service.getEntregas(c.id).subscribe({ next: (r: any) => { this.entregas = r?.data || []; this.cdr.detectChanges(); } });
  }

  aprobar(id: number) {
    this.service.aprobarEntrega(id).subscribe({
      next: (r: any) => {
        if (r?.success) {
          this.success = 'Entrega aprobada. Se procesó el pago.';
          this.ngOnInit();
          this.cdr.detectChanges();
        } else this.error = r?.message || 'Error';
      }, error: (e: any) => { this.error = e?.message || 'Error'; this.cdr.detectChanges(); }
    });
  }

  abrirRech(id: number) {
    this.entregaRechId = id;
    this.rechForm.reset();
    this.showRechModal = true;
  }

  rechazar() {
    if (this.rechForm.invalid) return;
    this.service.rechazarEntrega(this.entregaRechId, this.rechForm.value.comentario).subscribe({
      next: () => { this.showRechModal = false; this.ngOnInit(); }
    });
  }

  cancelar(id: number) {
    const motivo = prompt('Motivo de cancelación:');
    if (!motivo) return;
    this.service.cancelarContrato(id, motivo).subscribe({
      next: (r: any) => {
        if (r?.success) this.ngOnInit();
        else this.error = r?.message || 'Error';
        this.cdr.detectChanges();
      }, error: (e: any) => { this.error = e?.message || 'Error'; this.cdr.detectChanges(); }
    });
  }

  abrirCal(contratoId: number) {
    this.contratoCalId = contratoId;
    this.calForm.reset({ estrellas: 5 });
    this.showCalModal = true;
  }

  calificar() {
    if (this.calForm.invalid) return;
    const payload = {
      contratoId: this.contratoCalId,
      estrellas: this.calForm.value.estrellas,
      comentario: this.calForm.value.comentario || null
    };
    this.service.crearCalificacion(payload).subscribe({
      next: (r: any) => {
        if (r?.success || r?.data) {
          this.showCalModal = false;
          this.success = '¡Calificación enviada!';
          this.ngOnInit();
        } else {
          this.error = r?.message || 'Error al enviar calificación';
        }
        this.cdr.detectChanges();
      },
      error: (e: any) => { this.error = e?.message || 'Error'; this.cdr.detectChanges(); }
    });
  }
}
