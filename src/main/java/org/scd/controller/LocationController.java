package org.scd.controller;

import org.scd.config.exception.BusinessException;
import org.scd.model.Location;
import org.scd.model.dto.InputLocationDTO;
import org.scd.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/locations")
@SuppressWarnings("unused")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService){
        this.locationService=locationService;
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(UriComponentsBuilder componentsBuilder,
                                                   @RequestBody final InputLocationDTO inputLocationDTO) throws BusinessException {
        Long locationId = locationService.createLocation(inputLocationDTO);
        UriComponents uriComponents= componentsBuilder.path("/locations/"+locationId).build();
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @GetMapping(path="/{locationId}")
    public ResponseEntity<Location> getSomeLocation(@PathVariable Long locationId) throws BusinessException {
        return ResponseEntity.ok(locationService.getSomeLocation(locationId));
    }


    @PutMapping(path="/{locationId}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long locationId,
                                                   @RequestBody final InputLocationDTO inputLocationDTO) throws BusinessException {
        return ResponseEntity.ok(locationService.updateLocation(locationId,inputLocationDTO));
    }

    @DeleteMapping(path="/{locationId}")
    public ResponseEntity<Location> deleteLocation(@PathVariable Long locationId) throws BusinessException{
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<String>> filterLocation(
            @RequestParam(name="userId") final Long userId,
            @RequestParam(name="startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                    Date endDate) throws BusinessException {
        return ResponseEntity.ok(locationService.filterLocation(userId,startDate,endDate));
    }
}
