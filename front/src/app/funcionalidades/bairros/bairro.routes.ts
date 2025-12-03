import { Routes } from '@angular/router';
import { BairrosListaComponent } from './bairros-lista/bairros-lista.component';
import { BairrosFormComponent } from './bairros-form/bairros-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const bairrosRoutes: Routes = [
  { path: 'bairros', component: BairrosListaComponent },
  { path: 'bairros/novo', component: BairrosFormComponent, canDeactivate: [formExitGuard] },
  { path: 'bairros/editar/:id', component: BairrosFormComponent, canDeactivate: [formExitGuard] },
];