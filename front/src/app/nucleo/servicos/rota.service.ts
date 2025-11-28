import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { RotaRequest, RotaResponse } from '../../compartilhado/models/rota.model';

@Injectable({
  providedIn: 'root'
})
export class RotaService extends ApiBaseService<RotaResponse, RotaRequest> {
  protected endpoint = 'rotas';
}