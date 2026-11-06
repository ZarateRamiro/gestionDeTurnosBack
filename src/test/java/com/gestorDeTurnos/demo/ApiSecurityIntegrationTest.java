package com.gestorDeTurnos.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:turnos;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.admin.email=admin@test.local",
    "app.admin.password=ClaveSeguraDePrueba25!",
    "app.admin.name=Admin de prueba",
    "app.jwt.secret=clave-secreta-para-pruebas-de-integracion-123456"
})
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {
  @Autowired private MockMvc mvc;

  @Test void loginEntregaUnJwtYPermiteCrearUnaCanchaProtegida() throws Exception {
    String response = mvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"admin@test.local\",\"password\":\"ClaveSeguraDePrueba25!\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString())
        .andReturn().getResponse().getContentAsString();
    var tokenMatcher = java.util.regex.Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(response);
    assertTrue(tokenMatcher.find());
    String jwt = tokenMatcher.group(1);

    mvc.perform(post("/api/v1/canchas")
            .header("Authorization", "Bearer " + jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"Cancha prueba\",\"direccion\":\"Calle 1\",\"tipoSuperficie\":\"Sintético\",\"capacidadJugadores\":10,\"precioPorHora\":30000}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nombre").value("Cancha prueba"));
  }

  @Test void altaDeCanchaSinTokenDevuelveNoAutorizado() throws Exception {
    mvc.perform(post("/api/v1/canchas")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test void listadoDeCanchasPermanecePublico() throws Exception {
    mvc.perform(get("/api/v1/canchas")).andExpect(status().isOk());
  }
}
