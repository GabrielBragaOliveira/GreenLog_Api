import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { BairroRequest, BairroResponse } from '../../compartilhado/models/bairro.model';

@Injectable({
  providedIn: 'root'
})
export class BairroService extends ApiBaseService<BairroResponse, BairroRequest> {
  protected endpoint = 'bairros';
}