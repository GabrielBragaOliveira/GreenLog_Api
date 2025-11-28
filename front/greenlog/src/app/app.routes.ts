import { Routes } from '@angular/router';
import { LoginComponent } from './funcionalidades/autenticacao/login/login.component';
import { MainLayoutComponent } from './estrutura/main-layout/main-layout.component';
import { inject } from '@angular/core';
import { AuthService } from './nucleo/servicos/auth.service';

// Guard simples funcional para proteger rotas
const authGuard = () => {
  const authService = inject(AuthService);
  if (authService.isLogado()) return true;
  authService.logout(); // Redireciona
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
    canActivate: [authGuard], // Protege todo o layout
    children: [
      { path: '', redirectTo: 'rotas', pathMatch: 'full' }, // Home padrão
      
      // Aqui carregaremos os módulos lazy-loaded futuramente
      // Ex: { path: 'bairros', loadComponent: () => import(...).then(m => m.BairrosListaComponent) }
    ]
  },
  { path: '**', redirectTo: '' }
];