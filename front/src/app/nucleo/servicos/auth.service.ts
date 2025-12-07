import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, UsuarioResponse } from '../../compartilhado/models/usuario.model';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private messageService = inject(MessageService);
  private apiUrl = `${environment.apiUrl}/login`;
  private readonly STORAGE_KEY = 'greenlog_user';

  // O Signal que detém o estado do usuário (Reativo)
  private currentUser = signal<UsuarioResponse | null>(this.getUserFromStorage());

  login(credenciais: LoginRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(this.apiUrl, credenciais).pipe(
      tap(usuario => {
        // Atualiza o signal e o storage
        this.currentUser.set(usuario);
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(usuario));
        
        this.messageService.add({ severity: 'success', summary: 'Bem-vindo', detail: `Olá, ${usuario.nome}!` });
        this.router.navigate(['/']); // Vai para a home
      })
    );
  }

  getUsuarioLogado(): UsuarioResponse | null {
    const usuario = localStorage.getItem(this.STORAGE_KEY);
    return usuario ? JSON.parse(usuario) : null;
  }

  logout(): void {
    this.currentUser.set(null);
    localStorage.removeItem(this.STORAGE_KEY);
    this.router.navigate(['/login']);
  }

  // Recupera usuário ao recarregar a página (F5)
  private getUserFromStorage(): UsuarioResponse | null {
    const userJson = localStorage.getItem(this.STORAGE_KEY);
    return userJson ? JSON.parse(userJson) : null;
  }
  
  isLogado(): boolean {
    return !!this.currentUser();
  }
  
  isAdmin(): boolean {
    return this.currentUser()?.perfil === 'ADMIN';
  }
}