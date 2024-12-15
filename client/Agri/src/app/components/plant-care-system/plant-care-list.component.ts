import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { PlantCareSystemService } from '../../services/plant-care-system.service';
import { PlantCareSystem } from '../../interfaces/plant-care-system.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-plant-care-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="care-systems.jpg" alt="Agricultural Systems" class="height-[20%]">
                </div>
                <h2 class="section-title text-center">Plant Care Systems</h2>
            </div>
            
            <a *ngIf="isLoggedIn" routerLink="new" class="paper-btn btn-primary btn-block margin-bottom text-center">
                Add New System
            </a>
            
            <div class="search-container margin-bottom">
                <div class="search-wrapper">
                    <input 
                        type="text" 
                        [(ngModel)]="searchTerm" 
                        (input)="onSearch()"
                        placeholder="Search by name..."
                        class="search-input">
                    <button 
                        *ngIf="searchTerm" 
                        (click)="clearSearch()" 
                        class="btn-small clear-btn">
                        Clear
                    </button>
                </div>
            </div>
            
            <div *ngIf="filteredSystems.length === 0" class="empty-message">
                <p>No plant care systems found</p>
                <p *ngIf="searchTerm" class="sub-text">Try adjusting your search term</p>
            </div>

            <div class="grid-container" *ngIf="filteredSystems.length > 0">
                <div class="card system-card" *ngFor="let system of filteredSystems">
                    <div class="card-body">
                        <h4 class="card-title">{{system.name}}</h4>
                        <p class="description">{{system.description}}</p>
                        <div class="details">
                            <p><strong>Automation:</strong> {{system.automationEnabled ? 'Enabled' : 'Disabled'}}</p>
                            <p><strong>Maintenance:</strong> {{system.maintenanceTimeStamp | date:'medium'}}</p>
                            <p><strong>Created By:</strong> {{system.createdBy.username}}</p>
                        </div>
                        <div *ngIf="isLoggedIn" class="actions">
                            <button class="btn-small" (click)="editSystem(system.id)">Edit</button>
                            <button class="btn-small" (click)="viewSensors(system.id)">Sensors</button>
                            <button class="btn-small btn-danger" (click)="deleteSystem(system.id)">Delete</button>
                        </div>
                        <div *ngIf="!isLoggedIn" class="actions">
                            <button class="btn-small" (click)="viewSensors(system.id)">Sensors</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1rem;
            padding: 1rem 0;
        }

        .system-card {
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .card-body {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .description {
            flex-grow: 1;
            margin: 0.5rem 0;
        }

        .details {
            margin: 1rem 0;
            p {
                margin: 0.25rem 0;
            }
        }

        .actions {
            display: flex;
            gap: 0.5rem;
            justify-content: flex-end;
            margin-top: auto;
        }

        .btn-small {
            padding: 0.2rem 0.4rem;
        }

        @media (max-width: 600px) {
            .grid-container {
                grid-template-columns: 1fr;
            }
        }

         .banner-section {
        margin: -2rem -2rem 2rem -2rem;
        text-align: center;  /* Center the section content */
    }

    .banner-image-container {
        min-height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--primary-shaded-70);
        padding: 1rem;
        margin: 0 auto;  /* Center the container */
    }

    .section-title {
        padding: 1rem;
        margin: 0;
        background-color: var(--primary-shaded-70);
        text-align: center;  /* Center the title */
    }

    .search-container {
        max-width: 600px;
        margin: 0 auto;
    }

    .search-wrapper {
        display: flex;
        gap: 0.5rem;
        align-items: center;
    }

    .search-input {
        flex: 1;
    }

    .clear-btn {
        background-color: var(--danger-light);
        color: var(--danger);
        border: 1px solid var(--danger);
        &:hover {
            background-color: var(--danger);
            color: white;
        }
    }

    .empty-message {
        text-align: center;
        padding: 2rem;
        background-color: var(--primary-shaded-70);
        border-radius: 4px;
    }

    .sub-text {
        color: var(--muted);
        font-size: 0.875rem;
        margin-top: 0.5rem;
    }
    `]
})
export class PlantCareListComponent implements OnInit {
    systems: PlantCareSystem[] = [];
    filteredSystems: PlantCareSystem[] = [];
    searchTerm: string = '';

    constructor(
        private plantCareService: PlantCareSystemService,
        private router: Router,
        private messageService: MessageService,
        private authService: AuthService
    ) {}

    ngOnInit() {
        this.loadSystems();
    }

    loadSystems() {
        this.plantCareService.getAll().subscribe({
            next: (data) => {
                this.systems = data;
                this.filteredSystems = data;
            },
            error: (error) => {
                console.error('Error loading systems:', error);
                // You might want to add error handling/display here
            }
        });
    }

    deleteSystem(id: number) {
        if (confirm('Are you sure you want to delete this system?')) {
            this.plantCareService.delete(id).subscribe({
                next: () => {
                    this.loadSystems();
                    this.messageService.showSuccess('System deleted successfully');
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to delete this system');
                    } else {
                        this.messageService.showError('Error deleting system');
                    }
                }
            });
        }
    }

    editSystem(id: number) {
        this.plantCareService.getById(id).subscribe({
            next: () => {
                this.router.navigate(['/plant-care-systems', id, 'edit']);
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 403) {
                    this.messageService.showError('You are not authorized to edit this system');
                } else {
                    this.messageService.showError('Error accessing system');
                }
            }
        });
    }

    viewSensors(id: number) {
        this.router.navigate(['/plant-care-systems', id, 'sensors']);
    }

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    onSearch() {
        this.filteredSystems = this.systems.filter(system => system.name.toLowerCase().includes(this.searchTerm.toLowerCase()));
    }

    clearSearch() {
        this.searchTerm = '';
        this.onSearch();
    }
} 