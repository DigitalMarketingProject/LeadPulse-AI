package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityRestController {

    private final ActivityService activityService;

    public ActivityRestController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            throw new ResourceNotFoundException("Activity with ID " + id + " not found.");
        }
        return activity;
    }

    @GetMapping("/customer/{customerId}")
    public List<Activity> getActivitiesByCustomerId(@PathVariable Long customerId) {
        return activityService.getActivitiesByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity saved = activityService.saveActivity(activity);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        Activity existing = activityService.getActivityById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Activity with ID " + id + " not found.");
        }
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
