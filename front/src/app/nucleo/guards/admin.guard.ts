import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../servicos/auth.service';
import { MessageService } from 'primeng/api';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const messageService = inject(MessageService);

  if (authService.isAdmin()) {
    return true;
  }

  messageService.add({ 
    severity: 'warn', 
    summary: 'Acesso Negado', 
    detail: 'Você não tem permissão para acessar esta área.' 
  });
  
  return false;
};