import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PlantCareSystemService } from '../../services/plant-care-system.service';

@Component({
    selector: 'app-plant-care-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
        <div class="paper container">
            <h2>{{isEditing ? 'Edit' : 'Create'}} Plant Care System</h2>
            
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
                    <label for="description">Description</label>
                    <textarea 
                        id="description" 
                        formControlName="description" 
                        class="input-block">
                    </textarea>
                </div>

                <div class="form-group">
                    <label>
                        <input 
                            type="checkbox" 
                            formControlName="automationEnabled">
                        Enable Automation
                    </label>
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
export class PlantCareFormComponent implements OnInit {
    form: FormGroup;
    isEditing = false;
    systemId?: number;

    constructor(
        private fb: FormBuilder,
        private plantCareService: PlantCareSystemService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.form = this.fb.group({
            name: ['', Validators.required],
            description: [''],
            automationEnabled: [false]
        });
    }

    ngOnInit() {
        this.systemId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.systemId) {
            this.isEditing = true;
            this.loadSystem(this.systemId);
        }
    }

    loadSystem(id: number) {
        this.plantCareService.getById(id).subscribe({
            next: (system) => {
                this.form.patchValue({
                    name: system.name,
                    description: system.description,
                    automationEnabled: system.automationEnabled
                });
            },
            error: (error) => {
                console.error('Error loading system:', error);
                // Add error handling
            }
        });
    }

    onSubmit() {
        if (this.form.valid) {
            const operation = this.isEditing
                ? this.plantCareService.update(this.systemId!, this.form.value)
                : this.plantCareService.create(this.form.value);

            operation.subscribe({
                next: () => {
                    this.router.navigate(['/plant-care-systems']);
                },
                error: (error) => {
                    console.error('Error saving system:', error);
                    // Add error handling
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems']);
    }
} 