import { Routes } from '@angular/router';
import { CaminhaoListaComponent } from './caminhao-lista/caminhao-lista.component';
import { CaminhaoFormComponent } from './caminhao-form/caminhao-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const caminhaoRoutes: Routes = [
  { path: 'caminhoes', component: CaminhaoListaComponent },
  { path: 'caminhoes/novo', component: CaminhaoFormComponent, canDeactivate: [formExitGuard] },
  { path: 'caminhoes/editar/:id', component: CaminhaoFormComponent, canDeactivate: [formExitGuard] },
];