import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ResultadoRota } from '../../compartilhado/models/roteamento.model';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class RoteamentoService {
  
  private http = inject(HttpClient);
  private messageService = inject(MessageService);
  private apiUrl = `${environment.apiUrl}/roteamento`;

  calcularRota(origemId: number, destinoId: number): Observable<ResultadoRota> {
    return this.http.get<ResultadoRota>(`${this.apiUrl}/calcular`, {
      params: {
        origemId: origemId.toString(),
        destinoId: destinoId.toString()
      }
    }).pipe(
      catchError(error => {
        const msg = error.error?.mensagem || 'Erro ao calcular rota.';
        this.messageService.add({ severity: 'error', summary: 'Erro no Cálculo', detail: msg });
        throw error;
      })
    );
  }
}