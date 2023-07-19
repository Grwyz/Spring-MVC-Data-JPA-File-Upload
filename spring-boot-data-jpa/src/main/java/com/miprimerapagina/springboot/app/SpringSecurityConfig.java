package com.miprimerapagina.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.miprimerapagina.springboot.app.auth.handler.LoginSuccessHandler;
import com.miprimerapagina.springboot.app.models.service.JpaUserDetailsService;

@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig {

	@Autowired
	private LoginSuccessHandler successHandler;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	//Inyectamos la clase de servicio
	@Autowired
	private JpaUserDetailsService userDetailService;

	@Autowired
	public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
		build.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
	}

	// Método que configura las reglas de autorización y autenticación
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) {
		// Todo se envuelve en un bloque "try-catch" en caso de haber algún error
		// durante la configuración
		try {
			http.authorizeHttpRequests((authz) -> authz
					// Todos pueden acceder a la vista principal
					.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar**", "/locale", "/api/clientes/**").permitAll()
					// Cualquier petición deberá ser autenticada
					.anyRequest().authenticated())
					// Cualquier persona puede acceder a la vista para iniciar sesión
					.formLogin((formLogin) -> formLogin.successHandler(successHandler).loginPage("/login").permitAll())
					// Cualquier persona que haya iniciado sesión puede acceder a la vista para
					// cerrar sesión
					.logout((logout) -> logout.permitAll())

					// Manejo de excepciones de acceso denegado
					.exceptionHandling((exceptionHandling) -> exceptionHandling
							.accessDeniedHandler((request, response, accessDeniedException) -> {
								// Redirigimos a una página de error personalizada
								response.sendRedirect("/error_403");
							}));

			// Construye la configuración de las reglas de autorización y autenticación
			return http.build();
		} catch (Exception e) {
			// Manejar la excepción aquí
			e.printStackTrace();
			// Realizar cualquier otra acción necesaria, como lanzar una excepción
			// personalizada o registrar el error
			return null; // Otra opción de manejo de excepción, retorna null o un valor predeterminado en
							// caso de error
		}
	}

}
