import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PlantService } from '../../services/plant.service';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomValidators } from '../../validators/custom-validators';

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
                    <div class="validation-feedback" *ngIf="form.get('name')?.touched">
                        <div *ngIf="form.get('name')?.errors?.['required']" class="text-danger">
                            Name is required
                        </div>
                        <div *ngIf="form.get('name')?.errors?.['minlength'] || form.get('name')?.errors?.['maxlength']" class="text-danger">
                            Name must be between 2 and 50 characters
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="growthStage">Growth Stage</label>
                    <select 
                        id="growthStage" 
                        formControlName="growthStage" 
                        class="input-block">
                        <option value="">Select a growth stage</option>
                        <option value="Seedling">Seedling</option>
                        <option value="Vegetative">Vegetative</option>
                        <option value="Flowering">Flowering</option>
                        <option value="Fruiting">Fruiting</option>
                    </select>
                    <div class="validation-feedback" *ngIf="form.get('growthStage')?.touched">
                        <div *ngIf="form.get('growthStage')?.errors?.['required']" class="text-danger">
                            Growth stage is required
                        </div>
                        <div *ngIf="form.get('growthStage')?.errors?.['growthStage']" class="text-danger">
                            Please select a valid growth stage
                        </div>
                    </div>
                </div>

                <div class="row flex-edges">
                    <button type="button" class="btn-secondary" (click)="goBack()">Cancel</button>
                    <button type="submit" class="btn-primary" [disabled]="!form.valid">
                        {{isEditing ? 'Update' : 'Create'}}
                    </button>
                </div>
            </form>
        </div>
    `,
    styles: [`
        .validation-feedback {
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .text-danger {
            color: var(--danger);
        }
    `]
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
        private route: ActivatedRoute,
        private messageService: MessageService
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
        this.sensorId = Number(this.route.snapshot.paramMap.get('sensorId'));
        
        this.form = this.fb.group({
            name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
            growthStage: ['', [Validators.required, CustomValidators.growthStage]]
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
                    this.messageService.showSuccess(
                        `Plant successfully ${this.isEditing ? 'updated' : 'created'}`
                    );
                    this.goBack();
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to modify this plant');
                    } else {
                        this.messageService.showError(`Error ${this.isEditing ? 'updating' : 'creating'} plant`);
                    }
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems', this.plantCareSystemId, 'sensors', this.sensorId, 'plants']);
    }
} 