export interface BairroResponse {
  id: number;
  nome: string;
  descricao?: string;
  ativo: boolean;
}

export interface BairroRequest {
  nome: string;
  descricao?: string;
}