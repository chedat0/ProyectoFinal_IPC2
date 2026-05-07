export interface Propuesta {
    id: number;
    proyectoId: number;
    proyectoTitulo?: string;
    freelancerId: number;
    freelancerNombre?: string;
    montoOfertado: number;
    tiempoEntregaDias: number;
    mensaje?: string;
    estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
    fechaEnvio: string;
}