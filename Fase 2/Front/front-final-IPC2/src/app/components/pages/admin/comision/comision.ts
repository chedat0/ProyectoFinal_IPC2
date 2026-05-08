import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule,  } from '@angular/forms';
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
  selector: 'app-comision',
  imports: [CommonModule, ReactiveFormsModule, RouterModule,Layout],
  templateUrl: './comision.html',
  styleUrl: './comision.css',
})

export class Comision implements OnInit {
  nav=NAV; 
  comision: any=null; 
  historial: any[]=[]; 
  loading= true; 
  saving= false; 
  error= ''; 
  success= '';
  form!: FormGroup;

  constructor(private service:AdminServicio, private fb:FormBuilder){}

  ngOnInit(){
    this.form=this.fb.group({porcentaje:['',[ Validators.required,Validators.min(0), Validators.max(100)]]});
    Promise.all([
      this.service.getComisionActual().toPromise(),
      this.service.getHistorialComision().toPromise()])
      .then(([c,h]:any[])=>{if(c?.data) {
        this.comision=c.data;
        this.form.patchValue({porcentaje:c.data.porcentaje});
      }
      this.historial= h ?.data || [];
      this.loading= false; 
    }).catch(()=>this.loading= false);
  }

  guardar(){
    if(this.form.invalid){this.form.markAllAsTouched();return;}
    this.saving=true;
    this.error='';
    this.success='';
    this.service.setComision(this.form.value).subscribe({next:(r:any)=>{if(r?.success){
      this.success='Comisión actualizada correctamente.';
      this.ngOnInit(); 
    } else 
      this.error=r?.message||'Error';
      this.saving=false;
    },
    error:(e:any)=>{
      this.error=e?.message||'Error';
      this.saving=false;
    
    }});
  }

  get f() {
    return this.form.controls;
  }
}
