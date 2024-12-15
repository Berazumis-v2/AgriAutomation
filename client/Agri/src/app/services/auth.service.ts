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
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();
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
        if (this.isBrowser) {
            // Check for existing token on service initialization
            const savedToken = localStorage.getItem('accessToken');
            if (savedToken) {
                // Verify token validity with backend
                this.checkRefreshToken().subscribe({
                    next: (isValid) => {
                        if (isValid) {
                            this.currentUserSubject.next({ accessToken: savedToken, refreshToken: '' });
                            this.startTokenRefresh();
                        } else {
                            // Token is invalid, clear it
                            localStorage.removeItem('accessToken');
                            this.currentUserSubject.next(null);
                        }
                    },
                    error: () => {
                        // On error, clear token
                        localStorage.removeItem('accessToken');
                        this.currentUserSubject.next(null);
                    }
                });
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
                this.startTokenRefresh();
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
        // First, stop the refresh interval
        this.stopTokenRefresh();

        // Clear local storage and user state
        if (this.isBrowser) {
            localStorage.removeItem('accessToken');
        }
        this.currentUserSubject.next(null);
        
        // Call logout endpoint to clear refresh token cookie
        this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true })
            .subscribe({
                next: () => {
                    this.messageService.showSuccess('Logged out successfully');
                    // Ensure navigation happens after state cleanup
                    this.router.navigate(['/login']).then(() => {
                        // Optional: Reload the page to ensure clean state
                        if (this.isBrowser) {
                            window.location.reload();
                        }
                    });
                },
                error: () => {
                    this.messageService.showError('Error during logout');
                    // Still navigate to login even if server logout fails
                    this.router.navigate(['/login']);
                }
            });
    }

    private startTokenRefresh(): void {
        // Clear any existing interval
        this.stopTokenRefresh();

        // Start new interval
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
                    // Make sure to update the current user state
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

    // Add method to handle token refresh from interceptor
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
}