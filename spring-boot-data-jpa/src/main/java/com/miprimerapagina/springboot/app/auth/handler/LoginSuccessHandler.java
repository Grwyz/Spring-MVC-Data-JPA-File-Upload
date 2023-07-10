package com.miprimerapagina.springboot.app.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	//Método para mandar un mensaje cuando se inicie sesión de manera exitosa
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		//Creamos el objeto "flashMapManager" (Aquí se guardan los mensajes)
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		
		//Creamos el objeto "flashMap" (Aquí se crean los mensajes)
		FlashMap flashMap = new FlashMap();
		
		//Creamos el mensaje
		flashMap.put("success", "Usuario: " + authentication.getName() + " - Sesión iniciada con éxito!");
		
		//Guardamos el mensaje en el flashMapManager
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		
		//Validamos que el objeto "authentication" no sea nulo
		if(authentication != null) {
			logger.info("El usuario '" + authentication.getName() +"' ha iniciado sesión con éxito");
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
