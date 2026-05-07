export interface AuthUser {
    token: string;
    userId: number;
    username: string;
    rol: 'CLIENTE' | 'FREELANCER' | 'ADMINISTRADOR';
    perfilCompleto: boolean;
}
