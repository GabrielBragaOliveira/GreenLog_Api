import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { ConfirmationService } from 'primeng/api';

export interface ComponenteComFormulario {
  temMudancasNaoSalvas(): boolean;
}

export const formExitGuard: CanDeactivateFn<ComponenteComFormulario> = (component) => {
  
  if (!component.temMudancasNaoSalvas || !component.temMudancasNaoSalvas()) {
    return true;
  }

  const confirmationService = inject(ConfirmationService);

  return new Promise<boolean>((resolve) => {
    confirmationService.confirm({
      message: 'Você tem alterações não salvas. Deseja realmente sair e perder os dados?',
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim, sair',
      rejectLabel: 'Não, ficar',
      accept: () => resolve(true),
      reject: () => resolve(false)
    });
  });
};