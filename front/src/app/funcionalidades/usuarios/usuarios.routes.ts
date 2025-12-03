import { Routes } from '@angular/router';
import { UsuariosListaComponent } from './usuarios-lista/usuarios-lista.component';
import { UsuariosFormComponent } from './usuarios-form/usuarios-form.component';
import { formExitGuard } from '../../nucleo/guards/form-exit.guard';

export const usuariosRoutes: Routes = [
  { path: 'usuarios', component: UsuariosListaComponent },
  { path: 'usuarios/novo', component: UsuariosFormComponent, canDeactivate: [formExitGuard] },
  { path: 'usuarios/editar/:id', component: UsuariosFormComponent, canDeactivate: [formExitGuard] },
];