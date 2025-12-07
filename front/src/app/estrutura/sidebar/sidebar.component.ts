import { Component, OnInit, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../nucleo/servicos/auth.service';
import { PanelMenuModule } from 'primeng/panelmenu';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, PanelMenuModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {
  private authService = inject(AuthService);
  
  items: MenuItem[] = [];

  constructor() {
    effect(() => {
      if (this.authService.getUsuarioLogado()) {
        this.construirMenu();
      }
    });
  }

  ngOnInit() {
    this.construirMenu();
  }

  construirMenu() {
    const isAdmin = this.authService.isAdmin();

    this.items = [
      {
        label: 'Painel',
        icon: 'pi pi-fw pi-home',
        routerLink: ['/']
      },
      {
        label: 'Operacional',
        icon: 'pi pi-fw pi-compass',
        items: [
          { label: 'Mapa Interativo', icon: 'pi pi-share-alt', routerLink: ['/mapa'] },
          { label: 'Planejar Rotas', icon: 'pi pi-map', routerLink: ['/rotas'] },
          { label: 'Itinerários', icon: 'pi pi-calendar', routerLink: ['/itinerarios'] }
        ]
      }
    ];

    if (isAdmin) {
      this.items.push({
        label: 'Cadastros',
        icon: 'pi pi-fw pi-database',
        items: [
          { label: 'Bairros', icon: 'pi pi-building', routerLink: ['/bairros'] },
          { label: 'Conexões', icon: 'pi pi-share-alt', routerLink: ['/conexoes'] },
          { label: 'Rotas', icon: 'pi pi-map', routerLink: ['/gestao-rotas'] },
          { label: 'Caminhões', icon: 'pi pi-truck', routerLink: ['/caminhoes'] },
          { label: 'Pontos de Coleta', icon: 'pi pi-map-marker', routerLink: ['/pontos-coleta'] },
          { label: 'Tipos de Resíduo', icon: 'pi pi-trash', routerLink: ['/tipos-residuo'] },
          { label: 'Usuários', icon: 'pi pi-users', routerLink: ['/usuarios'] }
        ]
      });
    }
  }
}