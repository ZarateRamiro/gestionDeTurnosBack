package com.gestorDeTurnos.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:application-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"app.jwt.secret=clave-para-prueba-de-contexto-32-caracteres-minimo"
})
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
