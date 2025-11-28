import { BairroResponse } from './bairro.model';

export interface RotaResponse {
  id: number;
  nome: string;
  listaDeBairros: BairroResponse[];
}

export interface RotaRequest {
  nome: string;
  listaDeBairrosIds: number[];
}