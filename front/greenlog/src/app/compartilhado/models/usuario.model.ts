import { Perfil } from './perfil.enum';

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha?: string;
  perfil: Perfil;
}

export interface LoginRequest {
  email: string;
  senha: string;
}