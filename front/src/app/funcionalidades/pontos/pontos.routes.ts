import { Routes } from '@angular/router';
import { PontosListaComponent } from './pontos-lista/pontos-lista.component';
import { PontosFormComponent } from './pontos-form/pontos-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const pontosRoutes: Routes = [
  { path: 'pontos-coleta', component: PontosListaComponent },
  { path: 'pontos-coleta/novo', component: PontosFormComponent, canDeactivate: [formExitGuard] },
  { path: 'pontos-coleta/editar/:id', component: PontosFormComponent, canDeactivate: [formExitGuard] },
];