import { CaminhaoResponse } from './caminhao.model';
import { RotaResponse } from './rota.model';

export interface ItinerarioResponse {
  id: number;
  data: string; 
  caminhao: CaminhaoResponse;
  rota: RotaResponse;
}

export interface ItinerarioRequest {
  data: string;
  caminhaoId: number;
  rotaId: number;
}