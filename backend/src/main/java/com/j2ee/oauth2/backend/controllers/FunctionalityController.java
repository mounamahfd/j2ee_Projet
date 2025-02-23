package com.j2ee.oauth2.backend.controllers;

import com.j2ee.oauth2.backend.models.Functionality;
import com.j2ee.oauth2.backend.services.FunctionalityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/functionalities")
@CrossOrigin("*")
public class FunctionalityController {

    private final FunctionalityService functionalityService;

    public FunctionalityController(FunctionalityService functionalityService) {
        this.functionalityService = functionalityService;
    }

    @GetMapping
    public List<Functionality> getAllFunctionalities() {
        return functionalityService.getAllFunctionalities();
    }

    @GetMapping("/{id}")
    public Functionality getFunctionalityById(@PathVariable Long id) {
        return functionalityService.getFunctionalityById(id);
    }

    @PostMapping
    public Functionality createFunctionality(@RequestBody Functionality functionality) {
        return functionalityService.createFunctionality(functionality);
    }

    @PutMapping("/{id}")
    public Functionality updateFunctionality(@PathVariable Long id, @RequestBody Functionality functionality) {
        return functionalityService.updateFunctionality(id, functionality);
    }

    @DeleteMapping("/{id}")
    public void deleteFunctionality(@PathVariable Long id) {
        functionalityService.deleteFunctionality(id);
    }
}
