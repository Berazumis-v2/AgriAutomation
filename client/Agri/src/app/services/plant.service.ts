import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Plant, PlantRequest } from '../interfaces/plant.interface';

@Injectable({
    providedIn: 'root'
})
export class PlantService {
    constructor(private http: HttpClient) {}

    getAll(plantCareSystemId: number, sensorId: number): Observable<Plant[]> {
        return this.http.get<Plant[]>(
            `${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}/plants`
        );
    }

    getById(plantCareSystemId: number, sensorId: number, plantId: number): Observable<Plant> {
        return this.http.get<Plant>(
            `${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}/plants/${plantId}`
        );
    }

    create(plantCareSystemId: number, sensorId: number, plant: PlantRequest): Observable<Plant> {
        return this.http.post<Plant>(
            `${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}/plants`,
            plant
        );
    }

    update(plantCareSystemId: number, sensorId: number, plantId: number, plant: PlantRequest): Observable<Plant> {
        return this.http.put<Plant>(
            `${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}/plants/${plantId}`,
            plant
        );
    }

    delete(plantCareSystemId: number, sensorId: number, plantId: number): Observable<void> {
        return this.http.delete<void>(
            `${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}/plants/${plantId}`
        );
    }
} 