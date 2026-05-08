import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})

export class Reportes implements OnInit {
  nav=NAV; 
  tab='ingresos'; 
  datos:any=null; 
  loading=false; 
  form!:FormGroup;

  constructor(private service:AdminServicio, private fb:FormBuilder){}
  
  ngOnInit(){
    this.form=this.fb.group({
      fechaInicio:[''],
      fechaFin:['']});
      this.cargar();
  }

  cargar(){
    this.loading=true; 
    this.datos=null;
    const {fechaInicio,fechaFin}=this.form.value;
    const obs=this.tab==='ingresos'?this.service.getIngresosTotales(fechaInicio,fechaFin)
      :this.tab==='freelancers'?this.service.getTopFreelancers(fechaInicio,fechaFin)
      :this.service.getTopCategorias(fechaInicio,fechaFin);
    obs.subscribe({next:(r:any)=>{
      this.datos=r?.data;
      this.loading=false;
    },
    error:()=>this.loading= false });
  }

  asArray() {
    return Array.isArray(this.datos)?
    this.datos:[];
  }
  
  cambiar(t:string){
    this.tab=t;
    this.cargar();
  }
}
