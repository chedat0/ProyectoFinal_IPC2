export interface SolicitudHabilidad {
    id: number;
    nombre: string;
    descripcion?: string;
    categoriaId: number;
    estado: 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
    comentarioAdmin?: string;
    fechaSolicitud: string;
}

export interface SolicitudCategoria {
    id: number;
    nombre: string;
    descripcion?: string;
    estado: 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';
    comentarioAdmin?: string;
    fechaSolicitud: string;
}