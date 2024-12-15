import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse } from '../interfaces/auth.interface';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();
    private isBrowser: boolean;

    constructor(
        private http: HttpClient,
        @Inject(PLATFORM_ID) platformId: Object,
        private router: Router
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
        if (this.isBrowser) {
            const savedToken = localStorage.getItem('accessToken');
            if (savedToken) {
                this.currentUserSubject.next({ accessToken: savedToken, refreshToken: '' });
            }
        }
    }

    login(credentials: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(
            `${environment.apiUrl}/auth/login`, 
            credentials,
            { withCredentials: true }
        ).pipe(
            tap(response => {
                if (this.isBrowser) {
                    localStorage.setItem('accessToken', response.accessToken);
                }
                this.currentUserSubject.next(response);
            })
        );
    }

    register(userData: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, userData)
            .pipe(
                tap(response => {
                    if (this.isBrowser) {
                        localStorage.setItem('currentUser', JSON.stringify(response));
                    }
                    this.currentUserSubject.next(response);
                })
            );
    }

    logout(): void {
        if (this.isBrowser) {
            localStorage.removeItem('accessToken');
        }
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
    }

    isLoggedIn(): boolean {
        return !!this.currentUserSubject.value;
    }

    getToken(): string | null {
        return this.currentUserSubject.value?.accessToken || null;
    }

    checkRefreshToken(): Observable<boolean> {
        return this.http.get<boolean>(
            `${environment.apiUrl}/auth/check-token`,
            { withCredentials: true }
        ).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }

    refreshToken(): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(
            `${environment.apiUrl}/auth/refresh-token`,
            {},
            { withCredentials: true }
        ).pipe(
            tap(response => {
                if (this.isBrowser) {
                    localStorage.setItem('accessToken', response.accessToken);
                }
                this.currentUserSubject.next(response);
            })
        );
    }
}