import { Routes } from '@angular/router';
import { ResiduosListaComponent } from './residuos-lista/residuos-lista.component';
import { ResiduosFormComponent } from './residuos-form/residuos-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const tiposResiduoRoutes: Routes = [
  { path: 'tipos-residuo', component: ResiduosListaComponent },
  { path: 'tipos-residuo/novo', component: ResiduosFormComponent, canDeactivate: [formExitGuard] },
  { path: 'tipos-residuo/editar/:id', component: ResiduosFormComponent, canDeactivate: [formExitGuard] },
];
