import { Routes } from '@angular/router';
import { ConexoesListaComponent } from './conexoes-lista/conexoes-lista.component';
import { ConexoesFormComponent } from './conexoes-form/conexoes-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const conexoesRoutes: Routes = [
  { path: 'conexoes', component: ConexoesListaComponent },
  { path: 'conexoes/novo', component: ConexoesFormComponent, canDeactivate: [formExitGuard] },
  { path: 'conexoes/editar/:id', component: ConexoesFormComponent, canDeactivate: [formExitGuard] },
];