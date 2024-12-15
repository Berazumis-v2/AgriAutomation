import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { PlantService } from '../../services/plant.service';
import { Plant } from '../../interfaces/plant.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-plant-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="plants.png" alt="Growing Plants">
                </div>
                <h2 class="section-title text-center">Plants for Sensor #{{sensorId}}</h2>
            </div>
            
            <a *ngIf="isLoggedIn" [routerLink]="['new']" class="paper-btn btn-primary btn-block margin-bottom text-center">Add New Plant</a>
            
            <div class="grid-container">
                <div class="card plant-card" *ngFor="let plant of plants">
                    <div class="card-body">
                        <h4 class="card-title">{{plant.name}}</h4>
                        <div class="growth-stage">
                            <span class="stage-label">Growth Stage:</span>
                            <span class="stage-value">{{plant.growthStage}}</span>
                        </div>
                        <div class="details">
                            <p><strong>Created By:</strong> {{plant.createdBy.username}}</p>
                        </div>
                        <div *ngIf="isLoggedIn" class="actions">
                            <button class="btn-small" (click)="editPlant(plant.id)">Edit</button>
                            <button class="btn-small btn-danger" (click)="deletePlant(plant.id)">Delete</button>
                        </div>
                    </div>
                </div>
            </div>

            <button class="paper-btn btn-block margin-top" (click)="backToSensors()">
                Back to Sensors
            </button>
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
        text-align: center;  // Center the section content
    }

    .banner-image-container {
        min-height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--primary-shaded-70);
        padding: 1rem;
        margin: 0 auto;  // Center the container
    }

    .banner-image {
        max-width: 100%;
        max-height: 300px;
        height: auto;
        object-fit: contain;
        display: block;  // Remove any inline spacing
        margin: 0 auto;  // Center the image
    }

    .section-title {
        padding: 1rem;
        margin: 0;
        background-color: var(--primary-shaded-70);
        text-align: center;  // Center the title
    }
    `]
})
export class PlantListComponent implements OnInit {
    plants: Plant[] = [];
    plantCareSystemId: number;
    sensorId: number;

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
            },
            error: (error) => {
                console.error('Error loading plants:', error);
            }
        });
    }

    deletePlant(id: number) {
        if (confirm('Are you sure you want to delete this plant?')) {
            this.plantService.delete(this.plantCareSystemId, this.sensorId, id).subscribe({
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
} 