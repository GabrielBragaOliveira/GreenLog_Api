import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { TipoResiduoRequest, TipoResiduoResponse } from '../../compartilhado/models/tipo-residuo.model';

@Injectable({
  providedIn: 'root'
})
export class TipoResiduoService extends ApiBaseService<TipoResiduoResponse, TipoResiduoRequest> {
  protected endpoint = 'tipos-residuo';
}
