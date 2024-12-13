export interface PlantCareSystem {
    id: number;
    name: string;
    description: string;
    automationEnabled: boolean;
    maintenanceTimeStamp: string;
    createdBy: {
        username: string;
    };
    sensors?: any[]; // Optional, for future use
}

export interface PlantCareSystemRequest {
    name: string;
    description: string;
    automationEnabled: boolean;
    // maintenanceTimeStamp is optional and can be omitted during creation
} 