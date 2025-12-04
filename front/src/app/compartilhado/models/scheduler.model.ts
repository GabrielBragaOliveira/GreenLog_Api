import { CaminhaoResponse } from './caminhao.model';
import { ItinerarioResponse } from './itinerario.model';

export interface DiaCalendario {
  data: Date;
  diaMes: number;
  diaSemana: string;
  hoje: boolean;
  isoString: string; // "2023-12-05"
}

export interface LinhaCronograma {
  caminhao: CaminhaoResponse;
  agendamentosPorDia: { [key: string]: ItinerarioResponse | null };
}