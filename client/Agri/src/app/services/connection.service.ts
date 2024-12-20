import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  checkConnection(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    
    if (typeof window === 'undefined') {
      // Server side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    } else if (error.status === 0) {
      // Client-side or network error
      errorMessage = 'Unable to connect to the server. Please check if the server is running.';
    } else {
      // Backend returned unsuccessful response code
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error?.message) {
        errorMessage += `\nDetails: ${error.error.message}`;
      }
    }
    
    console.error('An error occurred:', error);
    return throwError(() => errorMessage);
  }
} 