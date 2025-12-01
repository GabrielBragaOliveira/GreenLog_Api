import { BairroResponse } from './bairro.model';

export interface ResultadoRota {
  distanciaTotal: number;
  listaOrdenadaDeBairros: BairroResponse[]; 
}