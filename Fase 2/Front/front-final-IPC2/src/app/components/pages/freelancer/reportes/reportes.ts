import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { FreelancerServicio } from '../../../../servicios/freelancer.servicio';
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const NAV = [
  { label: 'Dashboard', icon: '📊', path: '/freelancer/dashboard' },
  { label: 'Explorar', icon: '🔍', path: '/freelancer/explorar' },
  { label: 'Propuestas', icon: '📋', path: '/freelancer/propuestas' },
  { label: 'Contratos', icon: '📄', path: '/freelancer/contratos' },
  { label: 'Reportes', icon: '📈', path: '/freelancer/reportes' },
  { label: 'Mi perfil', icon: '👤', path: '/freelancer/perfil' },
];

const TAB_LABELS: Record<string, string> = {
  contratos: 'Contratos Completados',
  categorias: 'Ingresos por Categoría',
  propuestas: 'Historial de Propuestas',
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
  tab = 'contratos';
  datos: any[] = [];
  loading = false;
  form!: FormGroup;
  today = new Date();

  constructor(private service: FreelancerServicio, private fb: FormBuilder, private cdr: ChangeDetectorRef, private dp: DatePipe) { }

  get tabLabel(): string { return TAB_LABELS[this.tab] || ''; }

  get periodoLabel(): string {
    const { fechaInicio, fechaFin } = this.form.value;
    const desde = fechaInicio ? this.dp.transform(fechaInicio, 'dd/MM/yyyy') : '—';
    const hasta = fechaFin ? this.dp.transform(fechaFin, 'dd/MM/yyyy') : '—';
    return `Período: ${desde} al ${hasta}`;
  }

  ngOnInit() {
    this.form = this.fb.group({
      fechaInicio: [''],
      fechaFin: ['']
    });
    this.cargar();
  }

  cargar() {
    this.loading = true;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'contratos'
      ? this.service.reporteContratos(fechaInicio, fechaFin)
      : this.tab === 'categorias'
        ? this.service.reporteCategorias()
        : this.service.reportePropuestas();
    obs.subscribe({
      next: (r: any) => { this.datos = r?.data || []; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  cambiar(t: string) {
    this.tab = t;
    this.cargar();
  }

  exportarPDF() {
    const doc = new jsPDF();
    const now = this.dp.transform(this.today, "dd/MM/yyyy HH:mm") ?? "";
    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text("ConnectWork — Freelancer", 14, 18);
    doc.setFontSize(13);
    doc.setFont("helvetica", "normal");
    doc.text(this.tabLabel, 14, 27);
    doc.setFontSize(10);
    doc.setTextColor(100);
    doc.text(this.periodoLabel, 14, 34);
    doc.text("Generado el " + now, 14, 40);
    doc.setTextColor(0);
    if (this.tab === "contratos") {
      autoTable(doc, {
        startY: 48,
        head: [["Proyecto", "Cliente", "Monto", "Comision", "Fecha"]],
        body: (this.datos as any[]).map((d: any) => [
          d.titulo_proyecto ?? d.proyecto ?? "",
          d.cliente ?? "",
          "Q" + Number(d.monto_total ?? d.monto ?? 0).toFixed(2),
          "Q" + Number(d.comision ?? 0).toFixed(2),
          d.fecha_fin ?? d.fecha ?? "",
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else if (this.tab === "categorias") {
      autoTable(doc, {
        startY: 48,
        head: [["Categoria", "Contratos", "Ingresos"]],
        body: (this.datos as any[]).map((d: any) => [
          d.nombre ?? d.categoria ?? "",
          d.contratos ?? 0,
          "Q" + Number(d.ingresos ?? d.total ?? 0).toFixed(2),
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else {
      autoTable(doc, {
        startY: 48,
        head: [["Total", "Pendientes", "Aceptadas", "Rechazadas"]],
        body: [[
          this.datos[0]?.total ?? 0,
          this.datos[0]?.pendientes ?? 0,
          this.datos[0]?.aceptadas ?? 0,
          this.datos[0]?.rechazadas ?? 0,
        ]],
        styles: { fontSize: 11 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    }
    doc.save("reporte-freelancer-" + this.tab + "-" + Date.now() + ".pdf");
  }
}
