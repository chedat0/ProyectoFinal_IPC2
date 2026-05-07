export interface Calificacion {
    id: number;
    contratoId: number;
    calificadorId: number;
    calificadoId: number;
    tipoCalificador: 'CLIENTE' | 'FREELANCER';
    puntuacion: number;
    comentario?: string;
    fechaCalificacion: string;
}