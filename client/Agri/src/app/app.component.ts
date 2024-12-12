import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ConnectionService } from './services/connection.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  connectionMessage: string = 'Checking connection...';
  connectionStatus: 'pending' | 'success' | 'error' = 'pending';
  errorDetails: string = '';

  constructor(private connectionService: ConnectionService) {}

  ngOnInit() {
    this.checkBackendConnection();
  }

  private checkBackendConnection() {
    this.connectionService.checkConnection().subscribe({
      next: (response) => {
        this.connectionMessage = response.status;
        this.connectionStatus = 'success';
      },
      error: (error) => {
        this.connectionStatus = 'error';
        this.connectionMessage = 'Failed to connect to backend';
        this.errorDetails = error;
        console.error('Connection error:', error);
      }
    });
  }
}
