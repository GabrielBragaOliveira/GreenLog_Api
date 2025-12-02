import { BairroResponse } from './bairro.model';
import { TipoResiduoResponse } from './tipo-residuo.model';

export interface PontoColetaResponse {
  id: number;
  nomePonto: string;
  nomeResponsavel: string;
  contato: string;
  email: string;
  endereco: string;
  bairro: BairroResponse;
  tiposResiduosAceitos: TipoResiduoResponse[];
  ativo: boolean;
}

export interface PontoColetaRequest {
  nomePonto: string;
  nomeResponsavel: string;
  contato: string;
  email: string;
  endereco: string;
  bairroId: number;
  tiposResiduosIds: number[];
}