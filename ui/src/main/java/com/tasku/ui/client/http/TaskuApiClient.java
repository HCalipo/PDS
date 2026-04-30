package com.tasku.ui.client.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasku.ui.client.dto.request.ChangeBoardStatusApiRequest;
import com.tasku.ui.client.dto.request.CreateBoardApiRequest;
import com.tasku.ui.client.dto.request.CreateCardApiRequest;
import com.tasku.ui.client.dto.request.CreateListApiRequest;
import com.tasku.ui.client.dto.request.LoginUserApiRequest;
import com.tasku.ui.client.dto.request.MoveCardApiRequest;
import com.tasku.ui.client.dto.request.RegisterUserApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class TaskuApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TaskuApiClient() {
        this(resolveBaseUrl());
    }

    TaskuApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<BoardApiResponse> findBoardsByOwner(String ownerEmail) {
        String encoded = URLEncoder.encode(ownerEmail, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/boards/owned?ownerEmail=" + encoded))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public BoardApiResponse getBoardByUrl(String boardUrl) {
        String encoded = URLEncoder.encode(boardUrl, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/boards/by-url?url=" + encoded))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), BoardApiResponse.class);
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public BoardApiResponse createBoard(CreateBoardApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/boards"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), BoardApiResponse.class);
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public CardApiResponse createCard(CreateCardApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/cards"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), CardApiResponse.class);
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public List<CardApiResponse> findCardsByList(UUID listId) {
        if (listId == null) {
            return List.of();
        }
        String encoded = URLEncoder.encode(listId.toString(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cards?listId=" + encoded))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public void changeBoardStatus(ChangeBoardStatusApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/boards/status"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new DesktopApiException(extractError(response), response.statusCode());
            }
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public BoardApiResponse createList(CreateListApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/boards/lists"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), BoardApiResponse.class);
            }
            throw new DesktopApiException(extractError(response), response.statusCode());
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    public void moveCard(MoveCardApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/cards/move"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new DesktopApiException(extractError(response), response.statusCode());
            }
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API en " + baseUrl + ".", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud interrumpida.", ex);
        }
    }

    private String extractError(HttpResponse<String> response) {
        String body = response.body();
        if (body == null || body.isBlank()) {
            return "Error HTTP " + response.statusCode();
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            if (root.hasNonNull("message")) return root.get("message").asText();
            if (root.hasNonNull("error")) return root.get("error").asText();
        } catch (Exception ignored) {}
        return "Error API (" + response.statusCode() + "): " + body;
    }

    private static String resolveBaseUrl() {
        String envValue = System.getenv("TASKU_API_BASE_URL");
        if (envValue != null && !envValue.isBlank()) return envValue.strip();
        String systemValue = System.getProperty("tasku.api.base-url");
        if (systemValue != null && !systemValue.isBlank()) return systemValue.strip();
        return DEFAULT_BASE_URL;
    }


    public void registerUser(RegisterUserApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/usuarios/registro"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            
            if (response.statusCode() != 201) {
                throw new DesktopApiException(extractError(response), response.statusCode());
            }
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API para el registro.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud de registro interrumpida.", ex);
        }
    }

    public void loginUser(LoginUserApiRequest payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/usuarios/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new DesktopApiException(extractError(response), response.statusCode());
            }
        } catch (DesktopApiException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new DesktopApiException("No se pudo conectar con la API para iniciar sesión.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DesktopApiException("Solicitud de inicio de sesión interrumpida.", ex);
        }
    }
    
}
