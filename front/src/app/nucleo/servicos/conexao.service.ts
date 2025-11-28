import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { ConexaoBairroRequest, ConexaoBairroResponse } from '../../compartilhado/models/conexao-bairro.model';

@Injectable({
  providedIn: 'root'
})
export class ConexaoService extends ApiBaseService<ConexaoBairroResponse, ConexaoBairroRequest> {
  protected endpoint = 'conexoes';
}