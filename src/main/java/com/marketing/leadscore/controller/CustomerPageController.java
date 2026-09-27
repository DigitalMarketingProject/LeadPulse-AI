package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerPageController {

    private final CustomerService service;

    public CustomerPageController(CustomerService service) {
        this.service = service;
    }

   @GetMapping("/customers-page")
public String customerPage(

        @RequestParam(required = false)
        String keyword,

        Model model){

    model.addAttribute(
            "customers",
            service.searchCustomer(keyword));

    model.addAttribute(
            "customer",
            new Customer());

    model.addAttribute(
            "keyword",
            keyword);

    return "customers";

}

@PostMapping("/delete/{id}")
public String deleteCustomer(@PathVariable Long id){

    service.deleteCustomer(id);

    return "redirect:/customers-page";

}

@GetMapping("/edit/{id}")
public String editCustomer(@PathVariable Long id,
                           Model model){

    model.addAttribute(
            "customer",
            service.getCustomerById(id));

    return "edit-customer";

}

@PostMapping("/updateCustomer")
public String updateCustomer(Customer customer){

    service.updateCustomer(customer);

    return "redirect:/customers-page";

}

    @PostMapping("/saveCustomer")
    public String saveCustomer(Customer customer) {

        service.saveCustomer(customer);

        return "redirect:/customers-page";
    }

}