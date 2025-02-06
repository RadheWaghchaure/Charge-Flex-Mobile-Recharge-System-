package com.Charge_Flex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Charge_Flex.dto.RecordDto;
import com.Charge_Flex.dto.UserDto;
import com.Charge_Flex.service.mail.EmailSenderService;
import com.Charge_Flex.services.auth.AuthServicesImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthServicesImpl authService;
	
	@Autowired
	EmailSenderService emailSenderService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signupCustomer(@RequestBody UserDto signupRequest){
		
		if(authService.hasCustomerWithEmail(signupRequest.getEmail()))
			return new ResponseEntity<>("Customer already exists with this email !",HttpStatus.NOT_ACCEPTABLE);
		UserDto createdCustomerDto = authService.createCustomer(signupRequest);
		
		if(createdCustomerDto == null) return new ResponseEntity<>("Customer not created",HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<>(createdCustomerDto,HttpStatus.CREATED);
	}
	
	@PostMapping("/saveRecord")
	public void saveRecord(@RequestBody RecordDto saveRequest){
		
		authService.createRecord(saveRequest);
		
		String name = saveRequest.getName();
		String plan = saveRequest.getPlan();
		String validity = saveRequest.getValidity();
		
		String toEmail = saveRequest.getEmail();
        String subject = "Recharge Successful";
        String body = "Dear "+name+ ", \nYour mobile prepaid recharge of ₹"+ plan + " was successful and is valid uptil "+validity+ ". \n\nThanks for using our services! \n\nRegards, \nTeam FlexiPay.";
        
        // Sending email using EmailSenderService
        emailSenderService.sendEmail(toEmail, subject, body);
		
	}
}
