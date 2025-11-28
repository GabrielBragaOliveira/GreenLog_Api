import { Injectable } from '@angular/core';
import { ApiBaseService } from './api-base.service';
import { ItinerarioRequest, ItinerarioResponse } from '../../compartilhado/models/itinerario.model';

@Injectable({
  providedIn: 'root'
})
export class ItinerarioService extends ApiBaseService<ItinerarioResponse, ItinerarioRequest> {
  protected endpoint = 'itinerarios';
}