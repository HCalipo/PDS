package com.tasku.ui.client.http;

import com.tasku.ui.client.dto.request.CreateBoardApiRequest;
import com.tasku.ui.client.dto.request.CreateCardApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

public class TaskuApiClient {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final String baseUrl;
    private final RestClient restClient;

    public TaskuApiClient() {
        this(resolveBaseUrl());
    }

    TaskuApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<BoardApiResponse> findBoardsByOwner(String ownerEmail) {
        try {
            List<BoardApiResponse> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/boards/owned")
                            .queryParam("ownerEmail", ownerEmail)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            return response == null ? List.of() : response;
        } catch (RestClientResponseException ex) {
            throw toDesktopException(ex);
        } catch (ResourceAccessException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        }
    }

    public BoardApiResponse createBoard(CreateBoardApiRequest payload) {
        try {
            BoardApiResponse response = restClient.post()
                    .uri("/api/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(BoardApiResponse.class);
            if (response == null) {
                throw new DesktopApiException("La API respondio vacio al crear el tablero.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            throw toDesktopException(ex);
        } catch (ResourceAccessException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        }
    }

    public CardApiResponse createCard(CreateCardApiRequest payload) {
        try {
            CardApiResponse response = restClient.post()
                    .uri("/api/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(CardApiResponse.class);
            if (response == null) {
                throw new DesktopApiException("La API respondio vacio al crear la tarjeta.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            throw toDesktopException(ex);
        } catch (ResourceAccessException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        }
    }

    private DesktopApiException toDesktopException(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        String message = (body == null || body.isBlank())
                ? ex.getStatusText()
                : body;
        return new DesktopApiException("Error API (" + ex.getStatusCode().value() + "): " + message,
                ex.getStatusCode().value());
    }

    private static String resolveBaseUrl() {
        String envValue = System.getenv("TASKU_API_BASE_URL");
        if (envValue != null && !envValue.isBlank()) {
            return envValue.strip();
        }

        String systemValue = System.getProperty("tasku.api.base-url");
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.strip();
        }

        return DEFAULT_BASE_URL;
    }
}
