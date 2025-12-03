import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { PontoColetaRequest, PontoColetaResponse } from '../../compartilhado/models/ponto-coleta.model';
import { Observable, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PontoColetaService extends ApiBaseService<PontoColetaResponse, PontoColetaRequest> {
  protected endpoint = 'pontos-coleta';

  buscarPorBairro(bairroId: number): Observable<PontoColetaResponse[]> {
    return this.http.get<PontoColetaResponse[]>(`${this.getUrl()}?bairroId=${bairroId}`)
      .pipe(catchError(error => this.tratarErro(error)));
  }
}