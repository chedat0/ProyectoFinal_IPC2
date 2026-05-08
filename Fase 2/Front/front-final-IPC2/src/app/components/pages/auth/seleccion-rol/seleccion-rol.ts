import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Footer } from '../../../shared/footer/footer';

@Component({
  selector: 'app-seleccion-rol',
  imports: [CommonModule, RouterModule, Footer],
  templateUrl: './seleccion-rol.html',
  styleUrl: './seleccion-rol.css',
})
export class SeleccionRol {
  selected: 'CLIENTE' | 'FREELANCER' | null = null;
  constructor(private router: Router) {}
  continuar() {
    if (!this.selected) return;
    this.router.navigate(['/auth/registro', this.selected === 'CLIENTE' ? 'cliente' : 'freelancer']);
  }
}
