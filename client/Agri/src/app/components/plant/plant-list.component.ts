import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { PlantService } from '../../services/plant.service';
import { Plant } from '../../interfaces/plant.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { ConfirmationModalComponent } from '../shared/confirmation-modal.component';

@Component({
    selector: 'app-plant-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ConfirmationModalComponent],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="plants.png" alt="Growing Plants" class="banner-image">
                </div>
                <h2 class="section-title text-center">Plants for Sensor #{{sensorId}}</h2>
            </div>
            
            <a *ngIf="isLoggedIn" [routerLink]="['new']" class="paper-btn btn-primary btn-block margin-bottom text-center">
                Add New Plant
            </a>

            <div class="search-container margin-bottom">
                <div class="search-wrapper">
                    <input 
                        type="text" 
                        [(ngModel)]="searchTerm" 
                        (input)="onSearch()"
                        placeholder="Search by name..."
                        class="search-input">
                    <input 
                        type="number" 
                        [(ngModel)]="searchId" 
                        (input)="onSearch()"
                        placeholder="ID"
                        class="id-input">
                    <button 
                        *ngIf="searchTerm || searchId" 
                        (click)="clearSearch()" 
                        class="btn-small clear-btn">
                        Clear
                    </button>
                </div>
            </div>

            <div *ngIf="filteredPlants.length === 0" class="empty-message">
                <p>No plants found</p>
                <p *ngIf="searchTerm" class="sub-text">Try adjusting your search term</p>
            </div>

            <div class="grid-container" *ngIf="filteredPlants.length > 0">
                <div class="card plant-card" *ngFor="let plant of filteredPlants">
                    <div class="card-body">
                        <div class="card-header">
                            <span class="plant-id">#{{plant.id}}</span>
                            <h4 class="card-title">{{plant.name}}</h4>
                        </div>
                        <div class="growth-stage">
                            <span class="stage-label">Growth Stage:</span>
                            <span class="stage-value">{{plant.growthStage}}</span>
                        </div>
                        <div class="details">
                            <p><strong>Created By:</strong> {{plant.createdBy.username}}</p>
                        </div>
                        <div class="card-actions">
                            <div *ngIf="isLoggedIn" class="action-buttons">
                                <button class="btn-small" (click)="editPlant(plant.id)" title="Edit">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                                    </svg>
                                </button>
                                <button class="btn-small btn-danger" (click)="deletePlant(plant)" title="Delete">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                                        <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <button class="paper-btn btn-block margin-top" (click)="backToSensors()">
                Back to Sensors
            </button>

            <app-confirmation-modal
                [show]="showDeleteModal"
                [itemName]="plantToDelete?.name || ''"
                (confirm)="confirmDelete()"
                (cancel)="cancelDelete()"
            ></app-confirmation-modal>
        </div>
    `,
    styles: [`
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1rem;
            padding: 1rem 0;
        }

        .plant-card {
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .card-body {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .growth-stage {
            margin: 1rem 0;
            padding: 0.5rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
            text-align: center;
            
            .stage-label {
                display: block;
                font-size: 0.875rem;
                color: var(--muted);
            }
            
            .stage-value {
                font-size: 1.25rem;
                font-weight: bold;
            }
        }

        .details {
            margin: 1rem 0;
            flex-grow: 1;
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

        .margin-left {
            margin-left: 1rem;
        }

        @media (max-width: 600px) {
            .grid-container {
                grid-template-columns: 1fr;
            }
        }

        .banner-section {
            margin: -2rem -2rem 2rem -2rem;
            text-align: center; /* Center the section content */
        }

        .banner-image-container {
            min-height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: var(--primary-shaded-70);
            padding: 1rem;
            margin: 0 auto; /* Center the container */
        }

        .banner-image {
            max-width: 100%;
            max-height: 300px;
            height: auto;
            object-fit: contain;
            display: block; /* Remove any inline spacing */
            margin: 0 auto; /* Center the image */
        }

        .section-title {
            padding: 1rem;
            margin: 0;
            background-color: var(--primary-shaded-70);
            text-align: center; /* Center the title */
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

        .card-header {
            display: flex;
            align-items: baseline;
            gap: 0.5rem;
            margin-bottom: 0.5rem;
        }

        .plant-id {
            font-size: 0.75rem;
            color: var(--muted);
            font-weight: normal;
        }

        .card-title {
            margin: 0;
            flex: 1;
        }

        .id-input {
            width: 80px;
            text-align: center;
        }

        .card-actions {
            display: flex;
            justify-content: flex-end;
            gap: 0.5rem;
            margin-top: auto;
        }

        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }

        .btn-small {
            padding: 0.2rem 0.4rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;

            svg {
                width: 16px;
                height: 16px;
            }
        }
    `]
})
export class PlantListComponent implements OnInit {
    plants: Plant[] = [];
    filteredPlants: Plant[] = [];
    plantCareSystemId: number;
    sensorId: number;
    searchTerm: string = '';
    searchId: string = '';
    showDeleteModal = false;
    plantToDelete: Plant | null = null;

    constructor(
        private plantService: PlantService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService,
        private authService: AuthService
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
        this.sensorId = Number(this.route.snapshot.paramMap.get('sensorId'));
    }

    ngOnInit() {
        this.loadPlants();
    }

    loadPlants() {
        this.plantService.getAll(this.plantCareSystemId, this.sensorId).subscribe({
            next: (data) => {
                this.plants = data;
                this.filteredPlants = data;
            },
            error: (error) => {
                console.error('Error loading plants:', error);
            }
        });
    }

    deletePlant(plant: Plant) {
        this.plantToDelete = plant;
        this.showDeleteModal = true;
    }

    confirmDelete() {
        if (this.plantToDelete) {
            this.plantService.delete(this.plantCareSystemId, this.sensorId, this.plantToDelete.id).subscribe({
                next: () => {
                    this.loadPlants();
                    this.messageService.showSuccess('Plant deleted successfully');
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to delete this plant');
                    } else {
                        this.messageService.showError('Error deleting plant');
                    }
                }
            });
        }
        this.showDeleteModal = false;
        this.plantToDelete = null;
    }

    cancelDelete() {
        this.showDeleteModal = false;
        this.plantToDelete = null;
    }

    editPlant(id: number) {
        this.plantService.getById(this.plantCareSystemId, this.sensorId, id).subscribe({
            next: () => {
                this.router.navigate([id, 'edit'], { relativeTo: this.route });
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 403) {
                    this.messageService.showError('You are not authorized to edit this plant');
                } else {
                    this.messageService.showError('Error accessing plant');
                }
            }
        });
    }

    backToSensors() {
        this.router.navigate(['/plant-care-systems', this.plantCareSystemId, 'sensors']);
    }

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    onSearch() {
        this.filteredPlants = this.plants.filter(plant => {
            const nameMatch = plant.name.toLowerCase().includes(this.searchTerm.toLowerCase());
            const idMatch = this.searchId ? plant.id === Number(this.searchId) : true;
            return nameMatch && idMatch;
        });
    }

    clearSearch() {
        this.searchTerm = '';
        this.searchId = '';
        this.filteredPlants = this.plants;
    }
} 