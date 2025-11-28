export interface BairroResponse {
  id: number;
  nome: string;
  descricao?: string;
}

export interface BairroRequest {
  nome: string;
  descricao?: string;
}