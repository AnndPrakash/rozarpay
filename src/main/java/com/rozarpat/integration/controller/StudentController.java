package com.rozarpat.integration.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rozarpat.integration.entity.StudentOrder;
import com.rozarpat.integration.service.StudentService;



@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String init() {
        return "index";
    }

    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<StudentOrder> createOrder(@RequestBody StudentOrder studentOrder) throws Exception {
        StudentOrder createOrder = studentService.createOrder(studentOrder);
        return new ResponseEntity<>(createOrder, HttpStatus.CREATED);
    }

    @PostMapping("/handle-payment-callback")
    public String handlePaymentCallback(@RequestParam Map<String, String> resPayLoad) {
        System.out.println(resPayLoad);
        StudentOrder updateOrder = studentService.updateOrder(resPayLoad);
        return "success";
    }
}
