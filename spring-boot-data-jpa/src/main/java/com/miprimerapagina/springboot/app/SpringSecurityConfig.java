package com.miprimerapagina.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.miprimerapagina.springboot.app.auth.handler.LoginSuccessHandler;

@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig {

	@Autowired
	private LoginSuccessHandler successHandler;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private DataSource dataSource;

	// Método que configura las reglas de autorización y autenticación
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) {
		// Todo se envuelve en un bloque "try-catch" en caso de haber algún error
		// durante la configuración
		try {
			http.authorizeHttpRequests((authz) -> authz
					// Todos pueden acceder a la vista principal
					.requestMatchers("/", "/css/**", "/js/**", "/images/**", "/listar").permitAll()
					/*
					 * // Todos aquellos quienes tengan el rol "USER" o superior podrán acceder a
					 * estas // vistas .requestMatchers("/uploads/**").hasAnyRole("USER")
					 * .requestMatchers("/ver/**").hasRole("USER") // Solo aquellos que tengan el
					 * rol "ADMIN" podrán acceder a estas vistas
					 * .requestMatchers("/factura/**").hasRole("ADMIN")
					 * .requestMatchers("/form/**").hasRole("ADMIN")
					 * .requestMatchers("/eliminar/**").hasRole("ADMIN") // Cualquier petición
					 * deberá ser autenticada
					 */
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

	// USO DE UN MÉTODO DEPRECADO - REVISAR Y ACTUALIZAR
	@Bean
	AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).jdbcAuthentication().dataSource(dataSource)
				.passwordEncoder(passwordEncoder)
				.usersByUsernameQuery("select username, password, enabled from users where username=?")
				.authoritiesByUsernameQuery(
						"select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?")
				.and().build();
	}
	
	/* Enlaces de interés:
	 * https://es.stackoverflow.com/questions/597594/cuál-es-la-alternativa-a-la-función-and-que-aparece-como-deprecated
	 * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/SecurityConfigurerAdapter.html#and()
	 * https://spring.io/blog/2019/11/21/spring-security-lambda-dsl
	*/

}
