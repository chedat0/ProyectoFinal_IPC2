export interface Cliente {
  id: number;
  usuarioId: number;
  empresa?: string;
  descripcion?: string;
  sitioWeb?: string;
  perfilCompleto: boolean;
  saldo: number;
  saldoBloqueado: number;
}