import { Routes } from '@angular/router';
import { LoginComponent } from './funcionalidades/autenticacao/login/login.component';
import { MainLayoutComponent } from './estrutura/main-layout/main-layout.component';
import { inject } from '@angular/core';
import { AuthService } from './nucleo/servicos/auth.service';

const authGuard = () => {
  const authService = inject(AuthService);
  if (authService.isLogado()) return true;
  authService.logout();
  return false;
};

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
    ]
  },
  { path: '**', redirectTo: '' }
];