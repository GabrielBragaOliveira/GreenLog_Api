/*
  Localização: src/app/app.config.ts
  Objetivo: Configuração global da aplicação. Adicionado MessageService.
*/
import { ApplicationConfig } from '@angular/core';
import { provideRouter, withViewTransitions } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { MessageService } from 'primeng/api'; // <--- Importe isso

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withViewTransitions()),
    provideHttpClient(withFetch()),
    provideAnimations(),
    
    // Provedor global para o serviço de mensagens (Toasts)
    MessageService 
  ]
};