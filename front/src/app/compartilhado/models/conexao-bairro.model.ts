import { BairroResponse } from './bairro.model';

export interface ConexaoBairroResponse {
  id: number;
  bairroOrigem: BairroResponse;
  bairroDestino: BairroResponse;
  distancia: number;
  ativo: boolean;
}

export interface ConexaoBairroRequest {
  bairroOrigemId: number;
  bairroDestinoId: number;
  distancia: number;
}