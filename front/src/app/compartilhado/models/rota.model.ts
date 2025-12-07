import { BairroResponse } from './bairro.model';
import { PontoColetaResponse } from './ponto-coleta.model';

export interface RotaResponse {
  id: number;
  nome: string;
  listaDeBairros: BairroResponse[];
  pontoColetaDestino?: PontoColetaResponse;
  ativo: boolean;
}

export interface RotaRequest {
  nome: string;
  listaDeBairrosIds: number[];
  pontoColetaDestinoId: number;
}