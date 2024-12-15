import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { PlantCareListComponent } from './components/plant-care-system/plant-care-list.component';
import { PlantCareFormComponent } from './components/plant-care-system/plant-care-form.component';
import { SensorListComponent } from './components/sensor/sensor-list.component';
import { SensorFormComponent } from './components/sensor/sensor-form.component';
import { PlantListComponent } from './components/plant/plant-list.component';
import { PlantFormComponent } from './components/plant/plant-form.component';

// Auth guard function
const authGuard = () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn()) {
        return true;
    }

    return router.parseUrl('/login');
};

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { 
        path: 'dashboard', 
        component: DashboardComponent,
        canActivate: [authGuard]
    },
    {
        path: 'plant-care-systems',
        children: [
            {
                path: '',  // This matches /plant-care-systems
                component: PlantCareListComponent,
            },
            {
                path: 'new',  // This matches /plant-care-systems/new
                component: PlantCareFormComponent,
                canActivate: [authGuard]
            },
            {
                path: ':id/edit',  // This matches /plant-care-systems/:id/edit
                component: PlantCareFormComponent,
                canActivate: [authGuard]
            },
            {
                path: ':plantCareSystemId/sensors',  // This matches /plant-care-systems/:plantCareSystemId/sensors
                children: [
                    {
                        path: '',
                        component: SensorListComponent,
                    },
                    {
                        path: 'new',
                        component: SensorFormComponent,
                        canActivate: [authGuard]
                    },
                    {
                        path: ':id/edit',
                        component: SensorFormComponent,
                        canActivate: [authGuard]
                    },
                    {
                        path: ':sensorId/plants',
                        children: [
                            {
                                path: '',
                                component: PlantListComponent,
                            },
                            {
                                path: 'new',
                                component: PlantFormComponent,
                                canActivate: [authGuard]
                            },
                            {
                                path: ':id/edit',
                                component: PlantFormComponent,
                                canActivate: [authGuard]
                            }
                        ]
                    }
                ]
            }
        ]
    },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
