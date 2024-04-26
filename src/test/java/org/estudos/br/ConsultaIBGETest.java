package org.estudos.br;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConsultaIBGETest {
    @Mock
    private HttpURLConnection connectionMock;

    private static final String ESTADOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/";
    private static final String DISTRITOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/distritos/";
    private static final String JSON_RESPONSE = "{\"id\":33,\"sigla\":\"RJ\",\"nome\":\"Rio de Janeiro\",\"regiao\":{\"id\":3,\"sigla\":\"SE\",\"nome\":\"Sudeste\"}}";

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        InputStream inputStream = new ByteArrayInputStream(JSON_RESPONSE.getBytes());
        when(connectionMock.getInputStream()).thenReturn(inputStream);
    }

    @RepeatedTest(5)
    @DisplayName("Teste para consulta única de um estado")
    public void testConsultarEstado() throws IOException {
        // Arrange
        String estado = "SP";

        // Act
        String resposta = ConsultaIBGE.consultarEstado(estado);

        // Assert
        assertResponseNotEmptyAndStatusCodeOk(ESTADOS_API_URL + estado);
    }

    @ParameterizedTest
    @CsvSource({"520005005", "310010405", "520010005"})
    @DisplayName("Teste para consulta de distritos com CsvSource")
    public void testConsultarDistrito(int identificador) throws IOException {
        // Act
        String resposta = ConsultaIBGE.consultarDistrito(identificador);

        // Assert
        assertFalse(resposta.isEmpty(), "A resposta não deve estar vazia");
        assertResponseNotEmptyAndStatusCodeOk(DISTRITOS_API_URL + identificador);
    }

    @ParameterizedTest
    @CsvSource({"RO", "AC", "AM", "RR", "PA", "AP", "TO", "MA", "PI", "CE", "RN", "PB", "PE", "AL", "SE", "BA", "MG", "ES", "RJ", "SP", "PR", "SC", "RS", "MS", "MT", "GO", "DF"})
    @DisplayName("Teste para consulta de estados com CSV")
    public void testConsultarEstados(String sigla) throws IOException {
        // Act
        String resposta = ConsultaIBGE.consultarEstado(sigla);

        // Assert
        assertFalse(resposta.isEmpty(), "A resposta não deve estar vazia");
        assertResponseNotEmptyAndStatusCodeOk(ESTADOS_API_URL + sigla);
    }

    @Test
    @DisplayName("Consulta usando o Estado com Mock")
    public void testConsultarEstadoComMock() throws IOException {
        // Arrange
        String estado = "RJ";

        // Act
        String resposta = ConsultaIBGE.consultarEstado(estado);

        // Assert
        assertEquals(JSON_RESPONSE, resposta, "O JSON retornado deve corresponder ao esperado");
    }

    private void assertResponseNotEmptyAndStatusCodeOk(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode, "O status code da resposta da API deve ser 200 (OK)");
    }
}
