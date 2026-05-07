export interface Usuario {
  id: number;
  username: string;
  correo: string;
  nombre: string;
  apellido?: string;
  cui?: string;
  telefono?: string;
  fotoPerfil?: string;
  rol: 'CLIENTE' | 'FREELANCER' | 'ADMINISTRADOR';
  activo: boolean;
  fechaRegistro: string;
  ultimaSesion?: string;
}