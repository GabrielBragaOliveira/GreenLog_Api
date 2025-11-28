import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { UsuarioRequest, UsuarioResponse } from '../../compartilhado/models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService extends ApiBaseService<UsuarioResponse, UsuarioRequest> {
  protected endpoint = 'usuarios';
}