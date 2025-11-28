import { TipoResiduoResponse } from './tipo-residuo.model';

export interface CaminhaoResponse {
  id: number;
  placa: string;
  motorista: string;
  capacidadeKg: number;
  tiposSuportados: TipoResiduoResponse[];
}

export interface CaminhaoRequest {
  placa: string;
  motorista: string;
  capacidadeKg: number;
  tiposSuportadosIds: number[];
}