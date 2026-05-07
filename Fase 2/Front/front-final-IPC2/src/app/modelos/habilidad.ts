export interface Habilidad {
    id: number;
    nombre: string;
    descripcion?: string;
    categoriaId: number;
    categoriaNombre?: string;
    activa: boolean;
}