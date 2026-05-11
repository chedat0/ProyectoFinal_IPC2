import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthServicio } from '../../../../servicios/auth.servicio';
import { Footer } from '../../../shared/footer/footer';

@Component({
  selector: 'app-registro-cliente',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Footer],
  templateUrl: './registro-cliente.html',
  styleUrl: './registro-cliente.css',
})
export class RegistroCliente {
  form: FormGroup;
  loading = false; 
  error = ''; 
  showPass = false;

  constructor(private fb: FormBuilder, private auth: AuthServicio, private router: Router) {
    this.form = this.fb.group({
      nombreCompleto: ['', Validators.required],
      username: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      telefono: [''],
      direccion: [''],
      cui: ['', Validators.required],
      fechaNacimiento: ['', Validators.required]
    });
  }
  get f() { return this.form.controls; }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    const data = { ...this.form.value, tipoUsuario: 'CLIENTE' };
    this.auth.register(data).subscribe({
      next: () => this.router.navigate(['/cliente/perfil']),
      error: (e: any) => { this.error = e?.message || 'Error al registrar'; this.loading = false; }
    });
  }
}
