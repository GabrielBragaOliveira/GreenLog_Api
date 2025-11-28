import { Routes } from '@angular/router';
import { LoginComponent } from './funcionalidades/autenticacao/login/login.component';
import { MainLayoutComponent } from './estrutura/main-layout/main-layout.component';
import { authGuard } from './nucleo/guards/auth.guard';
import { formExitGuard } from './nucleo/guards/form-exit.guard';
import { CaminhaoListaComponent } from './funcionalidades/caminhao/caminhao-lista/caminhao-lista.component';
import { CaminhaoFormComponent } from './funcionalidades/caminhao/caminhao-form/caminhao-form.component';

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
      { path: '', redirectTo: 'caminhoes', pathMatch: 'full' },
      
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

    ]
  },
  
  { path: '**', redirectTo: '' }
];