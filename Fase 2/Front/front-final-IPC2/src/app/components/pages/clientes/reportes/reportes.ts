import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { ClienteServicio } from '../../../../servicios/cliente.servicio';
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/cliente/dashboard' },
  { label: 'Mis proyectos', icon: '📁', path: '/cliente/proyectos' },
  { label: 'Contratos', icon: '📄', path: '/cliente/contratos' },
  { label: 'Recargar saldo', icon: '💳', path: '/cliente/recargas' },
  { label: 'Reportes', icon: '📈', path: '/cliente/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/cliente/perfil' },
];

const TAB_LABELS: Record<string, string> = {
  proyectos: 'Historial de Proyectos',
  recargas: 'Recargas de Saldo',
  gastos: 'Gastos por Categoría',
};

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
  providers: [DatePipe],
})

export class Reportes implements OnInit {
  nav = NAV;
  tab = 'proyectos';
  datos: any[] = [];
  loading = false;
  form!: FormGroup;
  today = new Date();

  constructor(private service: ClienteServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef, private dp: DatePipe) { }

  ngOnInit() {
    this.form = this.fb.group({
      fechaInicio: [''],
      fechaFin: ['']
    });
    this.cargar();
  }

  get tabLabel(): string { return TAB_LABELS[this.tab] || ''; }

  get periodoLabel(): string {
    const { fechaInicio, fechaFin } = this.form.value;
    const desde = fechaInicio ? this.dp.transform(fechaInicio, 'dd/MM/yyyy') : '—';
    const hasta = fechaFin ? this.dp.transform(fechaFin, 'dd/MM/yyyy') : '—';
    return `Período: ${desde} al ${hasta}`;
  }

  cargar() {
    this.loading = true;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'proyectos'
      ? this.service.reporteProyectos(fechaInicio, fechaFin)
      : this.tab === 'recargas'
        ? this.service.reporteRecargas(fechaInicio, fechaFin)
        : this.service.reporteGastos(fechaInicio, fechaFin);
    obs.subscribe({
      next: (r: any) => { this.datos = r?.data || []; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  cambiarTab(t: string) {
    this.tab = t;
    this.cargar();
  }

  exportarPDF() {
    const doc = new jsPDF();
    const now = this.dp.transform(this.today, "dd/MM/yyyy HH:mm") ?? "";
    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text("ConnectWork — Cliente", 14, 18);
    doc.setFontSize(13);
    doc.setFont("helvetica", "normal");
    doc.text(this.tabLabel, 14, 27);
    doc.setFontSize(10);
    doc.setTextColor(100);
    doc.text(this.periodoLabel, 14, 34);
    doc.text("Generado el " + now, 14, 40);
    doc.setTextColor(0);
    if (this.tab === "proyectos") {
      autoTable(doc, {
        startY: 48,
        head: [["Titulo", "Estado", "Presupuesto", "Freelancer", "Fecha"]],
        body: (this.datos as any[]).map((d: any) => [
          d.titulo ?? "",
          d.estado ?? "",
          "Q" + Number(d.presupuesto ?? d.monto ?? 0).toFixed(2),
          d.freelancer ?? d.nombre_freelancer ?? "—",
          d.fecha_creacion ?? d.fecha ?? "",
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else if (this.tab === "recargas") {
      autoTable(doc, {
        startY: 48,
        head: [["Monto", "Metodo", "Fecha"]],
        body: (this.datos as any[]).map((d: any) => [
          "Q" + Number(d.monto ?? 0).toFixed(2),
          d.metodoPago ?? d.metodo ?? "",
          d.fecha ?? "",
        ]),
        styles: { fontSize: 11 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else {
      autoTable(doc, {
        startY: 48,
        head: [["Categoria", "Proyectos", "Total gastado"]],
        body: (this.datos as any[]).map((d: any) => [
          d.nombre ?? d.categoria ?? "",
          d.proyectos ?? d.contratos ?? 0,
          "Q" + Number(d.total_gastado ?? d.total ?? 0).toFixed(2),
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    }
    doc.save("reporte-cliente-" + this.tab + "-" + Date.now() + ".pdf");
  }

}
