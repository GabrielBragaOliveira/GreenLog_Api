import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError, throwError, tap } from 'rxjs';
import { MessageService } from 'primeng/api';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export abstract class ApiBaseService<TResponse, TRequest> {

  protected http = inject(HttpClient);
  protected messageService = inject(MessageService);
  protected abstract endpoint: string;

  protected getUrl(): string {
    return `${environment.apiUrl}/${this.endpoint}`;
  }

  listar(busca?: string): Observable<TResponse[]> {
    let params = new HttpParams();
    let url = this.getUrl();

    if (busca) {
      url = `${this.getUrl()}/busca`;
      params = params.set('q', busca);
    }

    return this.http.get<TResponse[]>(url, { params })
      .pipe(catchError(error => this.tratarErro(error)));
  }

  buscarPorId(id: number): Observable<TResponse> {
    return this.http.get<TResponse>(`${this.getUrl()}/${id}`)
      .pipe(catchError(error => this.tratarErro(error)));
  }

  salvar(registro: TRequest): Observable<TResponse> {
    return this.http.post<TResponse>(this.getUrl(), registro)
      .pipe(
        tap(() => this.notificarSucesso('Registro salvo com sucesso!')),
        catchError(error => this.tratarErro(error))
      );
  }

  atualizar(id: number, registro: TRequest): Observable<TResponse> {
    return this.http.put<TResponse>(`${this.getUrl()}/${id}`, registro)
      .pipe(
        tap(() => this.notificarSucesso('Registro atualizado com sucesso!')),
        catchError(error => this.tratarErro(error))
      );
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.getUrl()}/${id}`)
      .pipe(
        tap(() => this.notificarSucesso('Registro excluído com sucesso!')),
        catchError(error => this.tratarErro(error))
      );
  }

  alterarStatus(id: number): Observable<void> {
    return this.http.patch<void>(`${this.getUrl()}/${id}/status`, {})
      .pipe(
        tap(() => this.notificarSucesso('Registro inativado com sucesso!')),
        catchError(error => this.tratarErro(error))
      );
  }

  protected notificarSucesso(mensagem: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Sucesso',
      detail: mensagem,
      life: 3000
    });
  }

  protected tratarErro(error: HttpErrorResponse): Observable<never> {
    let mensagemErro = 'Ocorreu um erro inesperado. Tente novamente mais tarde.';

    if (error.error && error.error.mensagem) {
      mensagemErro = error.error.mensagem;
    } else if (error.status === 0) {
      mensagemErro = 'Não foi possível conectar ao servidor.';
    }

    this.messageService.add({
      severity: 'error',
      summary: 'Erro',
      detail: mensagemErro,
      life: 5000
    });

    return throwError(() => new Error(mensagemErro));
  }
}