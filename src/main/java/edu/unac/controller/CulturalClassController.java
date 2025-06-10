package edu.unac.controller;

import edu.unac.domain.CulturalClass;
import edu.unac.dto.CreateCulturalDto;
import edu.unac.dto.UpdateCulturalDto;
import edu.unac.services.CulturalClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cultural-classes")
@CrossOrigin(origins = "*")
public class CulturalClassController {
    private final CulturalClassService culturalClassService;

    public CulturalClassController(CulturalClassService culturalClassService) {
        this.culturalClassService = culturalClassService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CulturalClass create(@RequestBody CreateCulturalDto dto) {
        return culturalClassService.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CulturalClass update(@PathVariable Long id, @RequestBody UpdateCulturalDto dto) {
        return culturalClassService.update(id, dto);
    }

    @GetMapping
    public List<CulturalClass> findAll() {
        return culturalClassService.findAll();
    }

    @GetMapping("/{id}")
    public CulturalClass findById(@PathVariable Long id) {
        return culturalClassService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            culturalClassService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
