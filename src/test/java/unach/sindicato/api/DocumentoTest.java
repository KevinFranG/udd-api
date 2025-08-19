package unach.sindicato.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.util.UddRequester;
import unach.sindicato.api.persistence.data.Correo;
import unach.sindicato.api.persistence.data.Formatos;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentoTest implements PersistenceTest {

    @LocalServerPort int port;

    @Autowired TestRestTemplate restTemplate;

    UddRequester requester;

    @BeforeEach
    public void init() {
        this.requester = new UddRequester(restTemplate);
    }

    @Test
    @Override
    public void testSave() {
        Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
            .orElseThrow();

        var loginResponse = requester.login(
            "http://localhost:" + port + "/udd/api/admin/auth/login",
            credencial
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());

        String token = loginResponse
            .getBody()
            .jsonAs(Token.class)
            .getToken();

        Correo correo = new Correo();
        correo.setDireccion("erwin.bermudez@unach.mx");

        var getByCorreoResponse = requester.post(
            "http://localhost:%s/udd/api/maestros/where/correo-is".formatted(port),
            token,
            correo
        );
        assertEquals(HttpStatus.OK, getByCorreoResponse.getStatusCode());
        assertNotNull(getByCorreoResponse.getBody());

        Maestro erwin = getByCorreoResponse
            .getBody()
            .jsonAs(Maestro.class);

        Pdf pdf = new Pdf();
        pdf.setFormato(Formatos.ACTA_NACIMIENTO);
        try {
            Path path = Paths.get(Objects.requireNonNull(DocumentoTest.class
                    .getClassLoader()
                    .getResource("pdf/actividad3.pdf"))
                .toURI());
            pdf.setBytes(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        erwin.getDocumentos().clear();
        erwin.getDocumentos().add(pdf);
        erwin.setEstatus(null);

        var saveResponse = requester.post(
            "http://localhost:%s/udd/api/maestros/add/documentos".formatted(port),
            token,
            erwin
        );
        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody());
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

    @Test
    public void testAddReporte() {
        Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
                .orElseThrow();

        var loginResponse = requester.login(
                "http://localhost:" + port + "/udd/api/admin/auth/login",
                credencial
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());

        String token = loginResponse
                .getBody()
                .jsonAs(Token.class)
                .getToken();

        Correo correo = new Correo();
        correo.setDireccion("erwin.bermudez@unach.mx");

        var getByCorreoResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/where/correo-is".formatted(port),
                token,
                correo
        );
        assertEquals(HttpStatus.OK, getByCorreoResponse.getStatusCode());
        assertNotNull(getByCorreoResponse.getBody());

        var erwin = getByCorreoResponse
                .getBody()
                .jsonAs(Maestro.class);

        Documento.Reporte reporte = new Documento.Reporte(Documento.Estatus.REQUIERE_ACTUALIZAR);
        erwin.getDocumentos().stream()
                .findFirst()
                .ifPresent(doc -> doc.add(reporte));

        var saveResponse = requester.post(
                "http://localhost:%s/udd/api/admin/add/reportes".formatted(port),
                token,
                erwin
        );
        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody());
    }
}
