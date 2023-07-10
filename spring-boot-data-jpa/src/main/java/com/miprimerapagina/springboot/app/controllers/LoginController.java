package com.miprimerapagina.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	//Método para mostrar la vista "login"
	@GetMapping("/login")
	public String login(@RequestParam(value="error", required = false) String error,
			@RequestParam(value="logout", required = false) String logout,
			Model model, Principal principal, RedirectAttributes flash) {
		
		//Validamos si el objeto "principal" no es nulo (Indica que ya hay una sesión iniciada)
		if(principal != null) {
			flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
			return "redirect:/";
		}
		
		//Validamos si el objeto "error" no es nulo (Indica que hay un error en el usuario o la contraseña)
		if(error != null) {
			model.addAttribute("error", "Error: Username o Password incorrecto");
		}
		
		//Validamos si el objeto "logout" no es nulo (Indica que se cerró una sesión antes de traer la vista "/login")
		if(logout != null) {
			model.addAttribute("success", "Ha cerrado sesión con éxito!");
		}
		
		//Regresamos la vista "login"
		return "login";
	}
}
