import { Habilidad } from "./habilidad";

export interface Proyecto {
    id: number;
    clienteId: number;
    categoriaId: number;
    categoriaNombre?: string;
    titulo: string;
    descripcion: string;
    presupuestoMin: number;
    presupuestoMax: number;
    fechaLimite: string;
    estado: 'ABIERTO' | 'EN_PROGRESO' | 'COMPLETADO' | 'CANCELADO';
    fechaCreacion: string;
    habilidades?: Habilidad[];
}