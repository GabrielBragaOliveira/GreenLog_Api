import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { CaminhaoRequest, CaminhaoResponse } from '../../compartilhado/models/caminhao.model';

@Injectable({
  providedIn: 'root'
})
export class CaminhaoService extends ApiBaseService<CaminhaoResponse, CaminhaoRequest> {
  protected endpoint = 'caminhoes';
}