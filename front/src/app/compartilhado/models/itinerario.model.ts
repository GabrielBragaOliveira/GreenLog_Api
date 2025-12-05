import { CaminhaoResponse } from './caminhao.model';
import { RotaResponse } from './rota.model';
import { TipoResiduoResponse } from './tipo-residuo.model';

export interface ItinerarioResponse {
  id: number;
  data: string; 
  caminhao: CaminhaoResponse;
  rota: RotaResponse;
  tipoResiduo: TipoResiduoResponse;
}

export interface ItinerarioRequest {
  data: string;
  caminhaoId: number;
  rotaId: number;
  tipoResiduoId: number;
}