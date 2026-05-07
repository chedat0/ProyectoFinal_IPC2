export interface Contrato {
    id: number;
    proyectoId: number;
    proyectoTitulo?: string;
    clienteId: number;
    freelancerId: number;
    propuestaId: number;
    montoAcordado: number;
    comisionPorcentaje: number;
    estado: 'ACTIVO' | 'COMPLETADO' | 'CANCELADO';
    fechaInicio: string;
    fechaFin?: string;
}