import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environmental.prod';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
    // Test connection when service is initialized
    this.testConnection().subscribe(
      response => console.log('Successfully connected to backend:', response),
      error => console.error('Failed to connect to backend:', error)
    );
  }

  testConnection(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }
}