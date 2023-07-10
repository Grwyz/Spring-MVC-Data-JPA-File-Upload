package com.miprimerapagina.springboot.app;

//import java.nio.file.Paths;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	
}
