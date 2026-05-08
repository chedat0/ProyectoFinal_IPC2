import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthServicio } from '../../../../servicios/auth.servicio';
import { Footer } from '../../../shared/footer/footer';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Footer],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form: FormGroup;
  loading = false;
  error = '';
  showPass = false;

  constructor(private fb: FormBuilder, private auth: AuthServicio, private router: Router, private cdr: ChangeDetectorRef) {
    this.form = this.fb.group({ username: ['', Validators.required], password: ['', Validators.required] });
  }

  get f() {
    return this.form.controls;
  }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    const { username, password } = this.form.value;
    this.auth.login(username, password).subscribe({
      next: (res: any) => {
        this.loading = false;        
        const rol = res?.tipoUsuario || res?.data?.tipoUsuario || res?.rol;
        if (rol === 'CLIENTE') this.router.navigate(['/cliente/dashboard']);
        else if (rol === 'FREELANCER') this.router.navigate(['/freelancer/dashboard']);
        else if (rol === 'ADMINISTRADOR') this.router.navigate(['/admin/dashboard']);
        else this.error = 'Rol no reconocido: ' + JSON.stringify(rol);
        this.cdr.detectChanges();
      },
      error: (e: any) => {
        this.error = e?.message || 'Credenciales incorrectas';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

}
