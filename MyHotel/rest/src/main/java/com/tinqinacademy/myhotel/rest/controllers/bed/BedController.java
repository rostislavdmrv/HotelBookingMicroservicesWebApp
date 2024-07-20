package com.tinqinacademy.myhotel.rest.controllers.bed;

import com.tinqinacademy.myhotel.core.services.BedService;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/beds")
public class BedController {

    private  final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @PostMapping("/add")
    public ResponseEntity<Bed> createBed(@RequestBody Bed bed) {
        Bed createdBed = bedService.save(bed);
        return new ResponseEntity<>(createdBed, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bed> getBedById(@PathVariable UUID id) {
        Optional<Bed> bed = bedService.findById(id);
        return bed.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bed> updateBed(@PathVariable UUID id, @RequestBody Bed bed) {
        if (!bedService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bed.setId(id);
        Bed updatedBed = bedService.update(bed);
        return new ResponseEntity<>(updatedBed, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBed(@PathVariable UUID id) {
        if (!bedService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bedService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds() {
        List<Bed> beds = bedService.findAll();
        return new ResponseEntity<>(beds, HttpStatus.OK);
    }




}
