import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe} from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../shared/layout/layout';
import { AdminServicio } from '../../../../servicios/admin.servicio';

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

  exportarPDF() { window.print(); }
}
