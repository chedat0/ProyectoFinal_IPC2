import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthServicio } from '../../../servicios/auth.servicio';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})

export class Header {
  @Input() userName = '';
  @Input() role = '';
  menuOpen = false;
  constructor(private auth: AuthServicio, private router: Router) {}
  logout() { this.auth.logout(); this.router.navigate(['/auth/login']); }
  toggle() { this.menuOpen = !this.menuOpen; }
  get roleLabel() {
    return this.role === 'CLIENTE' ? 'Cliente' : this.role === 'FREELANCER' ? 'Freelancer' : 'Admin';
  }
}
