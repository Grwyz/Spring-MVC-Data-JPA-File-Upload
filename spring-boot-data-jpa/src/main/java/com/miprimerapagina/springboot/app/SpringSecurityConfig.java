package com.miprimerapagina.springboot.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;

@Configuration
public class SpringSecurityConfig {

	// Registramos nuestro "passwordEncoder" como un componente de Spring (lo
	// utilizamos para codificar las contraseñas)
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() throws Exception {

		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		// Creamos dos nuevos usuarios: Uno con el rol "USER" y otro con los roles
		// "USER" y "ADMIN"
		manager.createUser(
				User.withUsername("miguel").password(passwordEncoder().encode("12345")).roles("USER").build());

		manager.createUser(
				User.withUsername("admin").password(passwordEncoder().encode("admin")).roles("ADMIN", "USER").build());

		return manager;
	}
	
	
	//Método que configura las reglas de autorización y autenticación
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) {
		//Todo se envuelve en un bloque "try-catch" en caso de haber algún error durante la configuración
	    try {
	        http
	            .authorizeHttpRequests((authz) -> authz
	            	//Todos pueden acceder a la vista principal
	                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
	                //Todos aquellos quienes tengan el rol "USER" o superior podrán acceder a estas vistas
	                .requestMatchers("/uploads/**").hasAnyRole("USER")
	                .requestMatchers("/ver/**").hasRole("USER")
	                //Solo aquellos que tengan el rol "ADMIN" podrán acceder a estas vistas
	                .requestMatchers("/factura/**").hasRole("ADMIN")
	                .requestMatchers("/form/**").hasRole("ADMIN")
	                .requestMatchers("/eliminar/**").hasRole("ADMIN")
	                //Cualquier petición deberá ser autenticada
	                .anyRequest().authenticated()
	            )
	            //Cualquier persona puede acceder a la vista para iniciar sesión
	            .formLogin((formLogin) -> formLogin
	                .loginPage("/login").permitAll()
	            )
	            //Cualquier persona que haya iniciado sesión puede acceder a la vista para cerrar sesión
	            .logout((logout) -> logout
	                .permitAll()
	            );
	        
	        //Construye la configuración de las reglas de autorización y autenticación
	        return http.build();
	    } catch (Exception e) {
	        // Manejar la excepción aquí
	        e.printStackTrace();
	        // Realizar cualquier otra acción necesaria, como lanzar una excepción personalizada o registrar el error
	        return null; // Otra opción de manejo de excepción, retorna null o un valor predeterminado en caso de error
	    }
	}
	
	/*
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> {
			try {
				authz.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
						.requestMatchers("/uploads/**").hasAnyRole("USER").requestMatchers("/ver/**").hasRole("USER")
						.requestMatchers("/factura/**").hasRole("ADMIN").requestMatchers("/form/**").hasRole("ADMIN")
						.requestMatchers("/eliminar/**").hasRole("ADMIN").anyRequest().authenticated();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return http.build();

	}
	*/
	
}
