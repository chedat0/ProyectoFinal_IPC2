import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Header } from '../header/header';
import { Sidebar, NavItem } from '../sidebar/sidebar';
import { Footer } from '../footer/footer';
import { AuthServicio } from '../../../servicios/auth.servicio';

@Component({
  selector: 'app-layout',
  imports: [CommonModule, RouterModule, Header, Sidebar, Footer],
  templateUrl: './layout.html'  
})
export class Layout implements OnInit{
  @Input() navItems: NavItem[] = [];
  userName = ''; 
  role = '';
  constructor(private auth: AuthServicio) {}

  ngOnInit() {
    this.auth.currentUser$.subscribe((u: any) => {
      if (u) { 
        this.userName = u.nombreCompleto || u.username || ''; 
        this.role = u.rol || u.tipoUsuario || ''; }
    });
  }
}
