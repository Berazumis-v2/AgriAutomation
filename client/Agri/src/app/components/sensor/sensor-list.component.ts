import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { SensorService } from '../../services/sensor.service';
import { Sensor } from '../../interfaces/sensor.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-sensor-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="sensors.jpg" alt="Agricultural Sensors">
                </div>
                <h2 class="section-title text-center">Sensors for Plant Care System #{{plantCareSystemId}}</h2>
            </div>
            
            <a *ngIf="isLoggedIn" [routerLink]="['new']" class="paper-btn btn-primary btn-block margin-bottom text-center">Add New Sensor</a>
            
            <div class="grid-container">
                <div class="card sensor-card" *ngFor="let sensor of sensors">
                    <div class="card-body">
                        <h4 class="card-title">{{sensor.model}}</h4>
                        <div class="readings">
                            <div class="reading-item">
                                <span class="label">Temperature:</span>
                                <span class="value">{{sensor.temperature}}°C</span>
                            </div>
                            <div class="reading-item">
                                <span class="label">Humidity:</span>
                                <span class="value">{{sensor.humidity}}%</span>
                            </div>
                        </div>
                        <div class="details">
                            <p><strong>Last Reading:</strong> {{sensor.readingTimestamp | date:'medium'}}</p>
                            <p><strong>Last Calibration:</strong> {{sensor.calibrationTimestamp | date:'medium'}}</p>
                            <p><strong>Created By:</strong> {{sensor.createdBy.username}}</p>
                        </div>
                        <div *ngIf="isLoggedIn" class="actions">
                            <button class="btn-small" (click)="editSensor(sensor.id)">Edit</button>
                            <button class="btn-small" (click)="viewPlants(sensor.id)">Plants</button>
                            <button class="btn-small btn-danger" (click)="deleteSensor(sensor.id)">Delete</button>
                        </div>
                        <div *ngIf="!isLoggedIn" class="actions">
                            <button class="btn-small" (click)="viewPlants(sensor.id)">Plants</button>
                        </div>
                    </div>
                </div>
            </div>

            <button class="paper-btn btn-block margin-top" (click)="backToPlantCareSystems()">
                Back to Plant Care Systems
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

        .sensor-card {
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .card-body {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .readings {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin: 1rem 0;
            padding: 0.5rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
        }

        .reading-item {
            text-align: center;
            
            .label {
                display: block;
                font-size: 0.875rem;
                color: var(--muted);
            }
            
            .value {
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
        }


        .banner-image {
            width: 100%;
            height: 10%;
            object-fit: cover;
        }

.banner-image-container {
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--primary-shaded-70);
        padding: 1rem;
        margin: 0 auto;  // Center the container
    }

    .section-title {
        padding: 1rem;
        margin: 0;
        background-color: var(--primary-shaded-70);
        text-align: center;  // Center the title
    }
    `]
})
export class SensorListComponent implements OnInit {
    sensors: Sensor[] = [];
    plantCareSystemId: number;

    constructor(
        private sensorService: SensorService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService,
        private authService: AuthService
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
    }

    ngOnInit() {
        this.loadSensors();
    }

    loadSensors() {
        this.sensorService.getAll(this.plantCareSystemId).subscribe({
            next: (data) => {
                this.sensors = data;
            },
            error: (error) => {
                console.error('Error loading sensors:', error);
            }
        });
    }

    deleteSensor(id: number) {
        if (confirm('Are you sure you want to delete this sensor?')) {
            this.sensorService.delete(this.plantCareSystemId, id).subscribe({
                next: () => {
                    this.loadSensors();
                    this.messageService.showSuccess('Sensor deleted successfully');
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to delete this sensor');
                    } else {
                        this.messageService.showError('Error deleting sensor');
                    }
                }
            });
        }
    }

    editSensor(id: number) {
        this.sensorService.getById(this.plantCareSystemId, id).subscribe({
            next: () => {
                this.router.navigate([id, 'edit'], { relativeTo: this.route });
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 403) {
                    this.messageService.showError('You are not authorized to edit this sensor');
                } else {
                    this.messageService.showError('Error accessing sensor');
                }
            }
        });
    }

    viewPlants(id: number) {
        this.router.navigate([id, 'plants'], { relativeTo: this.route });
    }

    backToPlantCareSystems() {
        this.router.navigate(['/plant-care-systems']);
    }

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }
} 