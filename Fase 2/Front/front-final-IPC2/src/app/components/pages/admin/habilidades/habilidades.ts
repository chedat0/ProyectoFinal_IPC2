import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
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
  selector: 'app-habilidades',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, Layout],
  templateUrl: './habilidades.html',
  styleUrl: './habilidades.css',
})

export class Habilidades implements OnInit{
  nav= NAV; 
  habilidades: any[]=[]; 
  categorias: any[]=[]; 
  loading= true; 
  error= ''; 
  success= '';
  form!: FormGroup; 
  editando: any= null; 
  showModal= false; 
  saving= false; 
  filtroCategoria= '';

  constructor(private service:AdminServicio,private fb:FormBuilder) { }

  ngOnInit(){
    this.form=this.fb.group({
      nombre:['',Validators.required] ,
      descripcion: [''],
      categoriaId: ['',Validators.required] });

    Promise.all([
      this.service.getHabilidades().toPromise(),
      this.service.getCategorias().toPromise()])
      .then(([h,c]:any[])=>{
        this.habilidades=h?.data||[];
        this.categorias=c?.data||[];
        this.loading=false; 
      }).catch(()=>this.loading= false);
  }

  get filtradas(){
    return this.filtroCategoria?this.habilidades.filter(h=>h.categoriaId==this.filtroCategoria):this.habilidades;
  }
  
  nuevo(){
    this.editando=null;
    this.form.reset();
    this.error='';
    this.showModal=true;
  }
  
  editar(h:any) {
    this.editando=h;
    this.form.patchValue({
      nombre:h.nombre,
      descripcion:h.descripcion,
      categoriaId:h.categoriaId});
      this.error='';
      this.showModal=true;
  }

  guardar(){
    if(this.form.invalid){
      this.form.markAllAsTouched();
      return;
    }
    
    this.saving=true;
    this.error='';
    const obs=this.editando?this.service.updateHabilidad(
      this.editando.id,
      this.form.value):
      this.service.createHabilidad(this.form.value);

    obs.subscribe({next:(r:any)=>{if(r?.success){
      this.success='Guardado.';
      this.showModal=false;
      this.ngOnInit();
    } else 
      this.error=r?.message||'Error';
      this.saving=false;
    },
    error:(e:any)=>{this.error=e?.message||'Error';
      this.saving=false;
    }});
  }

  toggle(h:any){
    this.service.toggleHabilidad(h.id,!h.activa).subscribe({next:(r:any)=>{if(r?.success)h.activa=!h.activa;}});
  }

  get f(){
    return this.form.controls;
  }
}
