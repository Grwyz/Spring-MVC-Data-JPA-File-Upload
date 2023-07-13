package com.miprimerapagina.springboot.app;

import java.util.Locale;

import org.springframework.context.annotation.Bean;

//import java.nio.file.Paths;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	//Método obsoleto -> Se implementó de una forma más sencilla en otro lado
	/*private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();
		log.info(resourcePath);
		
		registry.addResourceHandler("/uploads/**").addResourceLocations(resourcePath);
	}*/
	
	//Método para agregar controladores pre-configurados para códigos de respuesta y no tener que crear una clase Controller para ello
	public void addViewControllers(ViewControllerRegistry registry) {
		//Agregamos el controlador que nos lleve a la vista "error_403"
		registry.addViewController("/error_403").setViewName("error_403");
	}
	
	// Registramos nuestro "passwordEncoder" como un componente de Spring (lo
		// utilizamos para codificar las contraseñas)
		@Bean
		public static BCryptPasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		//Método para guardar el objeto "localeResolver" con nuestra nacionalización (el idioma por defecto de la página)
		@Bean
		public LocaleResolver localeResolver() {
			SessionLocaleResolver localeResolver = new SessionLocaleResolver();
			localeResolver.setDefaultLocale(new Locale("es", "ES"));
			return localeResolver();
		}
		
		//Método interceptor para cambiar el idioma de la página cada que se pase el parámetro "lang" por URL
		@Bean
		public LocaleChangeInterceptor localeChangeInterceptor() {
			LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
			localeInterceptor.setParamName("lang");
			return localeInterceptor;
			
		}

		//Registramos el interceptor en nuestra aplicación
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			// TODO Auto-generated method stub
			registry.addInterceptor(localeChangeInterceptor());
		}
		
		
	
}
