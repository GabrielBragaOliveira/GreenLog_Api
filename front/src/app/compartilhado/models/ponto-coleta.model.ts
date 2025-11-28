import { BairroResponse } from './bairro.model';
import { TipoResiduoResponse } from './tipo-residuo.model';

export interface PontoColetaResponse {
  id: number;
  nomeResponsavel: string;
  contato: string;
  endereco: string;
  bairro: BairroResponse;
  tiposResiduosAceitos: TipoResiduoResponse[];
}

export interface PontoColetaRequest {
  nomeResponsavel: string;
  contato: string;
  endereco: string;
  bairroId: number;
  tiposResiduosIds: number[];
}