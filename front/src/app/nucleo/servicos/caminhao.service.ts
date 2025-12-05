import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { CaminhaoRequest, CaminhaoResponse } from '../../compartilhado/models/caminhao.model';
import { Observable, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CaminhaoService extends ApiBaseService<CaminhaoResponse, CaminhaoRequest> {
  protected endpoint = 'caminhoes';

  buscarCompativeisComRota(rotaId: number): Observable<CaminhaoResponse[]> {
    return this.http.get<CaminhaoResponse[]>(`${this.getUrl()}/compativeis/rota/${rotaId}`)
      .pipe(catchError(error => this.tratarErro(error)));
  }
}