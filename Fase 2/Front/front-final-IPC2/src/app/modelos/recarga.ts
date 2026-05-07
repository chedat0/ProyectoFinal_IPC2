export interface RecargaSaldo {
    id: number;
    clienteId: number;
    monto: number;
    metodoPago: string;
    referencia?: string;
    fecha: string;
}