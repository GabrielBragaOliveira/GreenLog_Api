import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { PontoColetaRequest, PontoColetaResponse } from '../../compartilhado/models/ponto-coleta.model';

@Injectable({
  providedIn: 'root'
})
export class PontoColetaService extends ApiBaseService<PontoColetaResponse, PontoColetaRequest> {
  protected endpoint = 'pontos-coleta';
}