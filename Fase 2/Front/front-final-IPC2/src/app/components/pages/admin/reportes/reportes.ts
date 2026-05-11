import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe} from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { AdminServicio } from '../../../../servicios/admin.servicio';
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const NAV = [
  { label:'Dashboard',   icon:'📊', path:'/admin/dashboard'   },
  { label:'Usuarios',    icon:'👥', path:'/admin/usuarios'    },
  { label:'Categorías',  icon:'🗂️', path:'/admin/categorias'  },
  { label:'Habilidades', icon:'⚙️', path:'/admin/habilidades' },
  { label:'Solicitudes', icon:'📬', path:'/admin/solicitudes' },
  { label:'Comisión',    icon:'💹', path:'/admin/comision'    },
  { label:'Reportes',    icon:'📈', path:'/admin/reportes'    },
];

const TAB_LABELS: Record<string, string> = {
  ingresos:    'Resumen de Ingresos y Comisiones',
  freelancers: 'Top Freelancers',
  categorias:  'Top Categorías',
};

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
  providers: [DatePipe],
})

export class Reportes implements OnInit {
  nav=NAV; 
  tab='ingresos'; 
  datos:any=null; 
  loading=false; 
  form!:FormGroup;
  today = new Date();

  constructor(private service:AdminServicio, private fb:FormBuilder, private cdr:ChangeDetectorRef, private dp: DatePipe){}
  
  ngOnInit(){
    this.form=this.fb.group({
      fechaInicio:[''],
      fechaFin:['']});
      this.cargar();
  }

  get tabLabel(): string { return TAB_LABELS[this.tab] || ''; }

  get periodoLabel(): string {
    const { fechaInicio, fechaFin } = this.form.value;
    const desde = fechaInicio ? this.dp.transform(fechaInicio, 'dd/MM/yyyy') : '—';
    const hasta  = fechaFin   ? this.dp.transform(fechaFin,    'dd/MM/yyyy') : '—';
    return `Período: ${desde} al ${hasta}`;
  }

  get hayDatos(): boolean {
    if (this.tab === 'ingresos') return this.datos !== null;
    return this.asArray().length > 0;
  }

  cargar() {
    this.loading = true;
    this.datos = null;
    const { fechaInicio, fechaFin } = this.form.value;
    const obs = this.tab === 'ingresos'
      ? this.service.reporteIngresos(fechaInicio, fechaFin)
      : this.tab === 'freelancers'
        ? this.service.reporteTopFreelancers()
        : this.service.reporteTopCategorias();
    obs.subscribe({
      next: (r: any) => { this.datos = r?.data; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  asArray() {
    return Array.isArray(this.datos)?
    this.datos:[];
  }
  
  cambiar(t:string){
    this.tab=t;
    this.cargar();
  }

  
  exportarPDF() {
    const doc = new jsPDF();
    const now = this.dp.transform(this.today, "dd/MM/yyyy HH:mm") ?? "";
    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text("ConnectWork — Administracion", 14, 18);
    doc.setFontSize(13);
    doc.setFont("helvetica", "normal");
    doc.text(this.tabLabel, 14, 27);
    doc.setFontSize(10);
    doc.setTextColor(100);
    doc.text(this.periodoLabel, 14, 34);
    doc.text("Generado el " + now, 14, 40);
    doc.setTextColor(0);
    if (this.tab === "ingresos") {
      autoTable(doc, {
        startY: 48,
        head: [["Metrica", "Valor"]],
        body: [
          ["Contratos completados", String(this.datos?.contratos_completados ?? 0)],
          ["Comisiones cobradas",   "Q" + (this.datos?.total_comisiones ?? 0).toFixed(2)],
        ],
        styles: { fontSize: 11 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else if (this.tab === "freelancers") {
      autoTable(doc, {
        startY: 48,
        head: [["#", "Freelancer", "Contratos", "Total generado", "Comision plataforma"]],
        body: this.asArray().map((d: any, i: number) => [
          i + 1, d.nombre_completo, d.contratos,
          "Q" + Number(d.total_generado).toFixed(2),
          "Q" + Number(d.comision_plataforma).toFixed(2),
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    } else {
      autoTable(doc, {
        startY: 48,
        head: [["#", "Categoria", "Contratos", "Total comisiones"]],
        body: this.asArray().map((d: any, i: number) => [
          i + 1, d.nombre, d.contratos,
          "Q" + Number(d.total_comisiones).toFixed(2),
        ]),
        styles: { fontSize: 10 },
        headStyles: { fillColor: [180, 90, 60] },
      });
    }
    doc.save("reporte-admin-" + this.tab + "-" + Date.now() + ".pdf");
  }
}
