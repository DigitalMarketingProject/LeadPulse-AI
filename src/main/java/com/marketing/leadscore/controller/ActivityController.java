package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.service.ActivityService;
import com.marketing.leadscore.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ActivityController {

    private final ActivityService service;
    private final CustomerService customerService;

    public ActivityController(ActivityService service, CustomerService customerService) {
        this.service = service;
        this.customerService = customerService;
    }

    @GetMapping("/activities")
    public String activities(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("activities", service.getAllActivities());
        model.addAttribute("customers", customerService.getAllCustomers());
        return "activities";
    }

    @PostMapping("/saveActivity")
    public String save(Activity activity) {
        service.saveActivity(activity);
        return "redirect:/activities";
    }

    @PostMapping("/deleteActivity/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteActivity(id);
        return "redirect:/activities";
    }
}