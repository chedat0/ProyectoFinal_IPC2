import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthServicio } from '../../../../servicios/auth.servicio';
import { CatalogoServicio } from '../../../../servicios/catalogo.servicio';
import { Footer } from '../../../shared/footer/footer';

@Component({
  selector: 'app-registro-freelancer',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Footer],
  templateUrl: './registro-freelancer.html',
  styleUrl: './registro-freelancer.css',
})
export class RegistroFreelancer implements OnInit{
  form: FormGroup;
  habilidades: any[] = [];
  seleccionadas: number[] = [];
  loading = false; error = ''; showPass = false;

  constructor(private fb: FormBuilder, private auth: AuthServicio, private catalogo: CatalogoServicio, private router: Router) {
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
  ngOnInit() {
    this.catalogo.getHabilidades().subscribe({ next: (r: any) => this.habilidades = r?.data || [] });
  }
  toggle(id: number) {
    const i = this.seleccionadas.indexOf(id);
    i === -1 ? this.seleccionadas.push(id) : this.seleccionadas.splice(i, 1);
  }
  isSelected(id: number) { return this.seleccionadas.includes(id); }
  get f() { return this.form.controls; }
  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true; this.error = '';
    const data = { ...this.form.value, tipoUsuario: 'FREELANCER', habilidadIds: this.seleccionadas };
    this.auth.register(data).subscribe({
      next: () => this.router.navigate(['/auth/login']),
      error: (e: any) => { this.error = e?.message || 'Error al registrar'; this.loading = false; }
    });
  }
}
