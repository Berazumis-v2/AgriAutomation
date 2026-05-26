import { Injectable, PLATFORM_ID, Inject, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of, interval, Subscription } from 'rxjs';
import { tap, map, catchError, switchMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse } from '../interfaces/auth.interface';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { MessageService } from '../services/message.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService implements OnDestroy {
    private currentUserSubject: BehaviorSubject<AuthResponse | null>;
    public currentUser$: Observable<AuthResponse | null>;
    private isBrowser: boolean;
    private refreshInterval?: Subscription;
    private readonly REFRESH_INTERVAL = 2 * 60 * 1000; // 2 minutes in milliseconds

    constructor(
        private http: HttpClient,
        @Inject(PLATFORM_ID) platformId: Object,
        private router: Router,
        private messageService: MessageService
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
        
        // Initialize with stored token if it exists
        let initialState: AuthResponse | null = null;
        if (this.isBrowser) {
            const savedToken = localStorage.getItem('accessToken');
            if (savedToken) {
                initialState = { accessToken: savedToken, refreshToken: '' };
            }
        }
        
        this.currentUserSubject = new BehaviorSubject<AuthResponse | null>(initialState);
        this.currentUser$ = this.currentUserSubject.asObservable();

        // Start refresh cycle if we have a token
        if (initialState) {
            this.startTokenRefresh();
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
                this.startTokenRefresh();
            })
        );
    }

    register(userData: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, userData);
    }

    logout(): void {
        this.clearAuthState();
        
        this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true })
            .subscribe({
                next: () => {
                    this.messageService.showSuccess('Logged out successfully');
                    this.navigateToLogin();
                },
                error: (error) => {
                    console.warn('Logout endpoint error:', error);
                    this.messageService.showSuccess('Logged out successfully');
                    this.navigateToLogin();
                }
            });
    }

    private navigateToLogin(): void {
        this.router.navigate(['/login']).then(() => {
            if (this.isBrowser) {
                window.location.reload();
            }
        });
    }

    private startTokenRefresh(): void {
        this.stopTokenRefresh();

        this.refreshInterval = interval(this.REFRESH_INTERVAL)
            .pipe(
                switchMap(() => {
                    this.messageService.showInfo('Refreshing access token...');
                    return this.refreshToken();
                })
            )
            .subscribe({
                next: (response) => {
                    if (this.isBrowser) {
                        localStorage.setItem('accessToken', response.accessToken);
                    }
                    this.currentUserSubject.next(response);
                    this.messageService.showSuccess('Access token refreshed successfully');
                },
                error: (error) => {
                    console.error('Token refresh failed:', error);
                    this.messageService.showError('Token refresh failed. Please log in again.');
                    if (error.status === 401) {
                        this.logout();
                    }
                }
            });
    }

    private stopTokenRefresh(): void {
        if (this.refreshInterval) {
            this.refreshInterval.unsubscribe();
            this.refreshInterval = undefined;
        }
    }

    ngOnDestroy(): void {
        this.stopTokenRefresh();
    }

    isLoggedIn(): boolean {
        return !!this.currentUserSubject.value;
    }

    getToken(): string | null {
        return this.currentUserSubject.value?.accessToken || null;
    }

    checkRefreshToken(): Observable<boolean> {
        const token = this.getToken();
        if (!token) {
            return of(false);
        }
        
        return this.http.get<boolean>(
            `${environment.apiUrl}/auth/validate-token`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        ).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }

    checkAuthStatus(): boolean {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            return false;
        }
        return this.isLoggedIn();
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

    handleTokenRefresh(): Observable<AuthResponse> {
        this.messageService.showInfo('Session expired. Refreshing token...');
        return this.refreshToken().pipe(
            tap({
                next: (response) => {
                    if (this.isBrowser) {
                        localStorage.setItem('accessToken', response.accessToken);
                    }
                    this.currentUserSubject.next(response);
                    this.messageService.showSuccess('Session refreshed successfully');
                },
                error: (error) => {
                    this.messageService.showError('Session refresh failed. Please log in again.');
                    this.logout();
                }
            })
        );
    }

    private handleTokenValidationError(): void {
        if (this.router.url !== '/login') {
            this.messageService.showError('Session expired. Please log in again.');
            this.logout();
        } else {
            this.clearAuthState();
        }
    }

    private clearAuthState(): void {
        this.stopTokenRefresh();
        if (this.isBrowser) {
            localStorage.removeItem('accessToken');
        }
        this.currentUserSubject.next(null);
    }
}