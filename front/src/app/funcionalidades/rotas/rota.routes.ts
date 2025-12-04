import { Routes } from '@angular/router';
import { RotaListaComponent } from './rota-lista/rota-lista.component';

export const rotaRoutes: Routes = [
  {
    path: 'gestao-rotas',
    component: RotaListaComponent,
    title: 'Gerenciamento de Rotas'
  }
];