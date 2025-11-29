import { Routes } from '@angular/router';
import { LoginComponent } from './funcionalidades/autenticacao/login/login.component';
import { MainLayoutComponent } from './estrutura/main-layout/main-layout.component';
import { authGuard } from './nucleo/guards/auth.guard';
import { formExitGuard } from './nucleo/guards/form-exit.guard';
import { CaminhaoListaComponent } from './funcionalidades/caminhao/caminhao-lista/caminhao-lista.component';
import { CaminhaoFormComponent } from './funcionalidades/caminhao/caminhao-form/caminhao-form.component';
import { PontosListaComponent } from './funcionalidades/pontos/pontos-lista/pontos-lista.component';
import { PontosFormComponent } from './funcionalidades/pontos/pontos-form/pontos-form.component';
import { ConexoesListaComponent } from './funcionalidades/conexoes/conexoes-lista/conexoes-lista.component';
import { ConexoesFormComponent } from './funcionalidades/conexoes/conexoes-form/conexoes-form.component';
import { RoteamentoCalculoComponent } from './funcionalidades/rotas/roteamento-calculo/roteamento-calculo.component';
import { BairrosListaComponent } from './funcionalidades/bairros/bairros-lista/bairros-lista.component';
import { BairrosFormComponent } from './funcionalidades/bairros/bairros-form/bairros-form.component';
import { UsuariosListaComponent } from './funcionalidades/usuarios/usuarios-lista/usuarios-lista.component';
import { UsuariosFormComponent } from './funcionalidades/usuarios/usuarios-form/usuarios-form.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'rotas', pathMatch: 'full' },
      {
        path: 'rotas',
        component: RoteamentoCalculoComponent
      },
      {
        path: 'caminhoes',
        component: CaminhaoListaComponent
      },
      {
        path: 'caminhoes/novo',
        component: CaminhaoFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'caminhoes/editar/:id',
        component: CaminhaoFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'pontos-coleta',
        component: PontosListaComponent
      },
      {
        path: 'pontos-coleta/novo',
        component: PontosFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'pontos-coleta/editar/:id',
        component: PontosFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'conexoes',
        component: ConexoesListaComponent
      },
      {
        path: 'conexoes/novo',
        component: ConexoesFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'conexoes/editar/:id',
        component: ConexoesFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'bairros',
        component: BairrosListaComponent
      },
      {
        path: 'bairros/novo',
        component: BairrosFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'bairros/editar/:id',
        component: BairrosFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'usuarios',
        component: UsuariosListaComponent
      },
      {
        path: 'usuarios/novo',
        component: UsuariosFormComponent,
        canDeactivate: [formExitGuard]
      },
      {
        path: 'usuarios/editar/:id',
        component: UsuariosFormComponent,
        canDeactivate: [formExitGuard]
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
