package unach.sindicato.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.util.UddRequester;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class MaestroTest implements PersistenceTest {
    @LocalServerPort int port;

    @Autowired MaestroService service;
    @Autowired TestRestTemplate restTemplate;

    UddRequester requester;

    @BeforeEach
    public void init() {
        requester = new UddRequester(restTemplate);
    }

    @Test
    @Override
    public void testSave() {
        Optional<ObjectId> maestroId = Optional.empty();

        try {
            Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
                    .orElseThrow();

            var loginResponse = requester.login(
                    "http://localhost:%s/udd/api/admin/auth/login".formatted(port),
                    credencial
            );
            assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
            assertNotNull(loginResponse.getBody());

            String token = loginResponse
                    .getBody()
                    .jsonAs(Token.class)
                    .getToken();

            var maestro = JsonData.MAESTROS.first()
                    .orElseThrow();

            var saveResponse = requester.post(
                    "http://localhost:%s/udd/api/maestros/auth/register".formatted(port),
                    token,
                    maestro
            );
            assertEquals(HttpStatus.CREATED, saveResponse.getStatusCode());
            assertNotNull(saveResponse.getBody());

            maestroId = Optional.of(saveResponse
                    .getBody()
                    .jsonAs(new TypeReference<Token<Maestro>>() {})
                    .getDocument()
                    .getId());
        } finally {
            maestroId.ifPresent(id -> {
                boolean deletionResult = service.delete(id);
                assertTrue(deletionResult);
            });
        }
    }

    @Test
    @Override
    public void testFindById() {

    }

    @Test
    @Override
    public void testFindAll() {

    }

    @Test
    @Override
    public void testUpdate() {

    }
}
