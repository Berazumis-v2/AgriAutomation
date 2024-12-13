import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { PlantService } from '../../services/plant.service';
import { Plant } from '../../interfaces/plant.interface';

@Component({
    selector: 'app-plant-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
        <div class="paper container">
            <div class="row">
                <div class="col-fill">
                    <h2>Plants for Sensor #{{sensorId}}</h2>
                </div>
                <div class="col">
                    <a [routerLink]="['new']" class="paper-btn btn-primary">Add New Plant</a>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Growth Stage</th>
                                <th>Created by</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr *ngFor="let plant of plants">
                                <td>{{plant.name}}</td>
                                <td>{{plant.growthStage}}</td>
                                <td>{{plant.createdBy.username}}</td>
                                <td>
                                    <div class="row flex-edges">
                                        <button class="btn-small" (click)="editPlant(plant.id)">Edit</button>
                                        <button class="btn-small btn-danger" (click)="deletePlant(plant.id)">Delete</button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .btn-small {
            padding: 0.2rem 0.4rem;
            margin: 0 0.2rem;
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
        private route: ActivatedRoute
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
                },
                error: (error) => {
                    console.error('Error deleting plant:', error);
                }
            });
        }
    }

    editPlant(id: number) {
        this.router.navigate([id, 'edit'], { relativeTo: this.route });
    }
} 