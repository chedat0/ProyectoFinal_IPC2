import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
  selector: 'app-usuarios',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule, Layout],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})

export class Usuarios implements OnInit{

  nav= NAV; 
  usuarios: any[]= []; 
  loading= true; 
  error= ''; 
  success= ''; 
  filtro= ''; 
  rolFiltro= '';

  constructor(private service:AdminServicio) { }

  ngOnInit( ) {
    this.service.getUsuarios().subscribe({next:(r:any)=>{
      this.usuarios=r?.data||[];
      this.loading=false;
    },
    error:()=> this.loading=false });
  }

  toggle(u:any){
    if(!confirm(`¿${u.activo?'Desactivar':'Activar'} a ${
      u.nombreCompleto}?`
    ))
    return;
    this.service.toggleUsuario(u.id,!u.activo).subscribe({next:(r:any)=>{if(r?.success){
      u.activo=!u.activo;
      this.success='Usuario actualizado.'
    } else 
      this.error=r?.message||'Error';
    },error:(e:any)=>this.error=e?.message||'Error'});
  }

  get filtrados(){
    return this.usuarios.filter(u=>(
      !this.filtro||u.nombreCompleto?.toLowerCase().includes(
        this.filtro.toLowerCase())||u.correo?.toLowerCase().includes(
          this.filtro.toLowerCase()))&&(!this.rolFiltro||u.tipoUsuario===this.rolFiltro));
        }
}
