package com.miprimerapagina.springboot.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	@Autowired
    private MessageSource messageSource;
	
	//Método para mostrar la vista "login"
	@GetMapping("/login")
	public String login(@RequestParam(value="error", required = false) String error,
			@RequestParam(value="logout", required = false) String logout,
			Model model, Principal principal, RedirectAttributes flash, Locale locale) {
		
		//Validamos si el objeto "principal" no es nulo (Indica que ya hay una sesión iniciada)
		if(principal != null) {
			flash.addFlashAttribute("info", messageSource.getMessage("text.login.already", null, locale));
			return "redirect:/";
		}
		
		//Validamos si el objeto "error" no es nulo (Indica que hay un error en el usuario o la contraseña)
		if(error != null) {
			model.addAttribute("error", messageSource.getMessage("text.login.error", null, locale));
		}
		
		//Validamos si el objeto "logout" no es nulo (Indica que se cerró una sesión antes de traer la vista "/login")
		if(logout != null) {
			model.addAttribute("success", messageSource.getMessage("text.login.logout", null, locale));
		}
		
		//Regresamos la vista "login"
		return "login";
	}
}
