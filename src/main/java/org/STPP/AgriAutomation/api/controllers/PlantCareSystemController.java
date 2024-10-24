package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.api.services.PlantCareSystemService;
import org.STPP.AgriAutomation.data.entities.PlantCareSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/plant-care-systems")
@CrossOrigin(origins = "*")
public class PlantCareSystemController {

    private final PlantCareSystemService plantCareSystemService;

    @Autowired
    public PlantCareSystemController(PlantCareSystemService plantCareSystemService) {
        this.plantCareSystemService = plantCareSystemService;
    }

    @GetMapping
    public ResponseEntity<List<PlantCareSystem>> getAllPlantCareSystems() {
        Iterable<PlantCareSystem> pcsIterable = plantCareSystemService.findAll();
        List<PlantCareSystem> pcsList = (List<PlantCareSystem>) pcsIterable;
        return ResponseEntity.ok(pcsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantCareSystem> getPlantCareSystemById(@PathVariable int id) {
        Optional<PlantCareSystem> pcsOpt = plantCareSystemService.findById(id);
        return pcsOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlantCareSystem> createPlantCareSystem(@RequestBody PlantCareSystem plantCareSystem) {
        PlantCareSystem createdPcs = plantCareSystemService.save(plantCareSystem);
        return ResponseEntity.ok(createdPcs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantCareSystem> updatePlantCareSystem(
            @PathVariable int id,
            @RequestBody PlantCareSystem plantCareSystem) {
        if (!plantCareSystemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        plantCareSystem.setId(id);
        PlantCareSystem updatedPcs = plantCareSystemService.save(plantCareSystem);
        return ResponseEntity.ok(updatedPcs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlantCareSystem(@PathVariable int id) {
        if (!plantCareSystemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        plantCareSystemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
