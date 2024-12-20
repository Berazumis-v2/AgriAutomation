import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, take } from 'rxjs/operators';
import { throwError } from 'rxjs';

let isRefreshing = false;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    
    // Skip auth header for auth-related endpoints except validate-token
    if (req.url.includes('/auth/') && !req.url.includes('/auth/validate-token')) {
        return next(req);
    }

    // Add auth header if available
    const token = authService.getToken();
    if (token) {
        req = addToken(req, token);
    }

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401 && !req.url.includes('auth/refresh-token')) {
                return handle401Error(req, next, authService);
            }
            return throwError(() => error);
        })
    );
};

function addToken(request: HttpRequest<unknown>, token: string) {
    return request.clone({
        setHeaders: {
            Authorization: `Bearer ${token}`
        }
    });
}

function handle401Error(
    request: HttpRequest<unknown>, 
    next: HttpHandlerFn, 
    authService: AuthService
) {
    if (!isRefreshing) {
        isRefreshing = true;

        return authService.handleTokenRefresh().pipe(
            take(1),
            switchMap((response) => {
                isRefreshing = false;
                return next(addToken(request, response.accessToken));
            }),
            catchError((error) => {
                isRefreshing = false;
                return throwError(() => error);
            })
        );
    }
    return next(request);
} 