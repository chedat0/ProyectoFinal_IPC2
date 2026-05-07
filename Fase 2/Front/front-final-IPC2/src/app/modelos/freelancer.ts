import { Habilidad } from "./habilidad";

export interface Freelancer {
    id: number;
    usuarioId: number;
    descripcion?: string;
    especialidad?: string;
    tarifaHora?: number;
    portafolioUrl?: string;
    paisResidencia?: string;
    calificacionPromedio: number;
    totalCalificaciones: number;
    perfilCompleto: boolean;
    saldo: number;
    habilidades?: Habilidad[];
}