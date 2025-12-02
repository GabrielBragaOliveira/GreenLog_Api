import { Routes } from '@angular/router';
import { LoginComponent } from './funcionalidades/autenticacao/login/login.component';
import { MainLayoutComponent } from './estrutura/main-layout/main-layout.component';
import { authGuard } from './nucleo/guards/auth.guard';
import { roteamentoRoutes } from './funcionalidades/rotas/roteamento.routes';
import { caminhaoRoutes } from './funcionalidades/caminhao/caminhao.routes';
import { pontosRoutes } from './funcionalidades/pontos/pontos.routes';
import { conexoesRoutes } from './funcionalidades/conexoes/conexoes.routes';
import { bairrosRoutes } from './funcionalidades/bairros/bairro.routes';
import { usuariosRoutes } from './funcionalidades/usuarios/usuarios.routes';
import { tiposResiduoRoutes } from './funcionalidades/residuos/tipo-residuos.routes';

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
      ...roteamentoRoutes,
      ...caminhaoRoutes,
      ...pontosRoutes,
      ...conexoesRoutes,
      ...bairrosRoutes,
      ...usuariosRoutes,
      ...tiposResiduoRoutes,
    ]
  },
  { path: '**', redirectTo: '' }
];
