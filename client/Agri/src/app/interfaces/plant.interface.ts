export interface Plant {
    id: number;
    name: string;
    growthStage: string;
    sensorId: number;
    createdBy: {
        username: string;
    };
}

export interface PlantRequest {
    name: string;
    growthStage: string;
} 