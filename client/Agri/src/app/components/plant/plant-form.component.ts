import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PlantService } from '../../services/plant.service';

@Component({
    selector: 'app-plant-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
        <div class="paper container">
            <h2>{{isEditing ? 'Edit' : 'Create'}} Plant</h2>
            
            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-group">
                <div class="form-group">
                    <label for="name">Name</label>
                    <input 
                        type="text" 
                        id="name" 
                        formControlName="name" 
                        class="input-block">
                    <div class="text-danger" *ngIf="form.get('name')?.touched && form.get('name')?.errors?.['required']">
                        Name is required
                    </div>
                </div>

                <div class="form-group">
                    <label for="growthStage">Growth Stage</label>
                    <select id="growthStage" formControlName="growthStage" class="input-block">
                        <option value="Seedling">Seedling</option>
                        <option value="Vegetative">Vegetative</option>
                        <option value="Flowering">Flowering</option>
                        <option value="Fruiting">Fruiting</option>
                    </select>
                </div>

                <div class="row flex-edges">
                    <button type="button" class="btn-secondary" (click)="goBack()">Cancel</button>
                    <button type="submit" class="btn-primary" [disabled]="!form.valid">
                        {{isEditing ? 'Update' : 'Create'}}
                    </button>
                </div>
            </form>
        </div>
    `
})
export class PlantFormComponent implements OnInit {
    form: FormGroup;
    isEditing = false;
    plantId?: number;
    plantCareSystemId: number;
    sensorId: number;

    constructor(
        private fb: FormBuilder,
        private plantService: PlantService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
        this.sensorId = Number(this.route.snapshot.paramMap.get('sensorId'));
        
        this.form = this.fb.group({
            name: ['', Validators.required],
            growthStage: ['Seedling']
        });
    }

    ngOnInit() {
        this.plantId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.plantId) {
            this.isEditing = true;
            this.loadPlant(this.plantId);
        }
    }

    loadPlant(id: number) {
        this.plantService.getById(this.plantCareSystemId, this.sensorId, id).subscribe({
            next: (plant) => {
                this.form.patchValue({
                    name: plant.name,
                    growthStage: plant.growthStage
                });
            },
            error: (error) => {
                console.error('Error loading plant:', error);
            }
        });
    }

    onSubmit() {
        if (this.form.valid) {
            const operation = this.isEditing
                ? this.plantService.update(this.plantCareSystemId, this.sensorId, this.plantId!, this.form.value)
                : this.plantService.create(this.plantCareSystemId, this.sensorId, this.form.value);

            operation.subscribe({
                next: () => {
                    this.goBack();
                },
                error: (error) => {
                    console.error('Error saving plant:', error);
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems', this.plantCareSystemId, 'sensors', this.sensorId, 'plants']);
    }
} 