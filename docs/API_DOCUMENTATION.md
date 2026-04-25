# Tasku API Documentation

## 1) Vision General Del Dominio

Tasku es una aplicacion de trabajo colaborativo centrada en el agregado `Tablero`.
Un tablero tiene listas, tarjetas (tarea/checklist), estado (`ACTIVE` o `BLOCKED`), comparticion por email y trazas de actividad.

Esta API REST permite conectar una GUI (por ejemplo React o Flutter) con la logica de negocio Java/Spring, manteniendo separacion por capas:

- Capa REST: controladores HTTP + DTOs JSON
- Capa de aplicacion: casos de uso en `TableroUseCaseService` y `TrazaActividadUseCaseService`
- Capa de dominio: reglas de negocio y entidades/value objects

## 2) Casos De Uso Consolidados

### 2.1 Casos De Uso Explicitos En DDDLO (API)

1. Generar y persistir tablero.
2. Recuperar tablero por URL.
3. Persistir listas (alta/modificacion).
4. Gestionar bloqueo/desbloqueo de tablero.
5. Crear tarjeta y validar reglas.
6. Mover tarjeta y registrar traza.
7. Completar tarjeta (autocompletado/logica de cierre).
8. Persistir etiquetas en tarjetas.
9. Consultar trazas historicas de un tablero.

### 2.2 Casos De Uso Faltantes Detectados Y Agregados

1. Listar tableros por dueno.
Justificacion: necesario para pantalla de inicio/dashboard.

2. Listar tableros compartidos con un usuario.
Justificacion: necesario para experiencia colaborativa completa.

3. Compartir tablero con rol (`VIEWER` o `EDITOR`).
Justificacion: el dominio ya contempla comparticion y roles.

4. Crear lista en tablero existente.
Justificacion: cierra la historia 2.2 (alta de lista) mas alla de listas iniciales.

5. Renombrar lista existente.
Justificacion: cierra la historia 2.2 (modificacion de lista).

6. Obtener tarjeta por id y listar tarjetas por lista.
Justificacion: necesario para render y refresco de columnas en GUI.

7. Regla tecnica de seguridad de estado: si tablero esta `BLOCKED`, se prohiben mutaciones de tarjetas.
Justificacion: coherencia con el lenguaje de dominio sobre estado de bloqueo.

## 3) Resumen De Endpoints

| Metodo | URL | Caso de uso | Exito |
|---|---|---|---|
| POST | /api/boards | Crear tablero | 201 |
| GET | /api/boards/by-url?url=... | Obtener tablero por URL | 200 |
| GET | /api/boards/owned?ownerEmail=... | Listar tableros por dueno | 200 |
| GET | /api/boards/shared?email=... | Listar tableros compartidos | 200 |
| POST | /api/boards/share | Compartir tablero | 200 |
| POST | /api/boards/lists | Crear lista en tablero | 201 |
| PATCH | /api/boards/lists/{listId} | Renombrar lista | 200 |
| PATCH | /api/boards/status | Cambiar estado del tablero | 200 |
| POST | /api/cards | Crear tarjeta | 201 |
| PATCH | /api/cards/move | Mover tarjeta | 200 |
| PATCH | /api/cards/complete | Completar (archivar) tarjeta | 200 |
| PATCH | /api/cards/labels | Asignar etiqueta a tarjeta | 200 |
| GET | /api/cards/{cardId} | Obtener tarjeta por id | 200 |
| GET | /api/cards?listId=... | Listar tarjetas por lista | 200 |
| GET | /api/traces?boardUrl=... | Consultar trazas de tablero | 200 |

## 4) Formato JSON De Error

Todos los errores se devuelven con este esquema:

```json
{
  "timestamp": "2026-04-23T18:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripcion del error",
  "path": "/api/cards"
}
```

Codigos utilizados por la API:

- 200 OK
- 201 Created
- 400 Bad Request
- 403 Forbidden
- 404 Not Found
- 500 Internal Server Error

## 5) Detalle Tecnico De Endpoints

### 5.1 POST /api/boards

Crea un tablero.

Request JSON:

```json
{
  "ownerEmail": "owner@tasku.dev",
  "name": "Roadmap",
  "color": "#0369A1",
  "description": "Tablero principal",
  "initialLists": [
    { "name": "TODO", "cardLimit": 100 },
    { "name": "DOING", "cardLimit": 100 },
    { "name": "DONE", "cardLimit": 100 }
  ]
}
```

Response JSON (201):

```json
{
  "url": "tasku://tablero/uuid",
  "name": "Roadmap",
  "ownerEmail": "owner@tasku.dev",
  "color": "#0369A1",
  "description": "Tablero principal",
  "status": "ACTIVE",
  "lists": [
    { "id": "uuid", "boardUrl": "tasku://tablero/uuid", "name": "TODO", "cardLimit": 100 }
  ],
  "sharedWith": []
}
```

Errores: 400, 500.

### 5.2 GET /api/boards/by-url?url=...

Obtiene tablero por URL de dominio.

Response JSON (200): `BoardApiResponse`.

Errores: 400, 404, 500.

### 5.3 GET /api/boards/owned?ownerEmail=...

Lista tableros creados por un dueno.

Response JSON (200): arreglo de `BoardApiResponse`.

Errores: 400, 500.

### 5.4 GET /api/boards/shared?email=...

Lista tableros compartidos con un email.

Response JSON (200): arreglo de `BoardApiResponse`.

Errores: 400, 500.

### 5.5 POST /api/boards/share

Comparte un tablero con rol.

Request JSON:

```json
{
  "boardUrl": "tasku://tablero/uuid",
  "email": "collab@tasku.dev",
  "role": "EDITOR"
}
```

Response JSON (200): `BoardApiResponse` actualizado.

Errores: 400, 404, 500.

### 5.6 POST /api/boards/lists

Crea una lista en tablero existente.

Request JSON:

```json
{
  "boardUrl": "tasku://tablero/uuid",
  "name": "QA",
  "cardLimit": 30
}
```

Response JSON (201): `BoardApiResponse` actualizado.

Errores: 400, 404, 500.

### 5.7 PATCH /api/boards/lists/{listId}

Renombra una lista existente.

Request JSON:

```json
{
  "boardUrl": "tasku://tablero/uuid",
  "name": "IN REVIEW"
}
```

Response JSON (200): `BoardApiResponse` actualizado.

Errores: 400, 404, 500.

### 5.8 PATCH /api/boards/status

Cambia el estado del tablero (`ACTIVE` o `BLOCKED`).

Request JSON:

```json
{
  "boardUrl": "tasku://tablero/uuid",
  "status": "BLOCKED"
}
```

Response JSON (200): `BoardApiResponse` actualizado.

Errores: 400, 404, 500.

### 5.9 POST /api/cards

Crea tarjeta de tipo `TAREA` o `CHECKLIST`.

Request JSON:

```json
{
  "listId": "uuid",
  "type": "CHECKLIST",
  "title": "Release checklist",
  "description": "Pre-produccion",
  "labels": [
    { "name": "release", "colorHex": "#16A34A" }
  ],
  "checklistItems": [
    { "description": "Build", "completed": true },
    { "description": "Deploy", "completed": false }
  ]
}
```

Response JSON (201):

```json
{
  "id": "uuid",
  "listId": "uuid",
  "type": "CHECKLIST",
  "title": "Release checklist",
  "description": "Pre-produccion",
  "archived": false,
  "labels": [
    { "name": "release", "colorHex": "#16A34A" }
  ],
  "checklistItems": [
    { "description": "Build", "completed": true }
  ]
}
```

Errores: 400, 403, 404, 500.

### 5.10 PATCH /api/cards/move

Mueve tarjeta entre listas del mismo tablero.

Request JSON:

```json
{
  "cardId": "uuid",
  "destinationListId": "uuid",
  "authorEmail": "owner@tasku.dev"
}
```

Response JSON (200): `CardApiResponse`.

Errores: 400, 403, 404, 500.

### 5.11 PATCH /api/cards/complete

Completa tarjeta (en este modelo se archiva).

Request JSON:

```json
{
  "cardId": "uuid",
  "authorEmail": "owner@tasku.dev"
}
```

Response JSON (200): `CardApiResponse` con `archived=true`.

Errores: 400, 403, 404, 500.

### 5.12 PATCH /api/cards/labels

Asigna etiqueta a tarjeta existente.

Request JSON:

```json
{
  "cardId": "uuid",
  "labelName": "backend",
  "colorHex": "#0EA5E9"
}
```

Response JSON (200): `CardApiResponse` actualizado.

Errores: 400, 403, 404, 500.

### 5.13 GET /api/cards/{cardId}

Obtiene una tarjeta por id.

Response JSON (200): `CardApiResponse`.

Errores: 404, 500.

### 5.14 GET /api/cards?listId=...

Lista tarjetas por lista.

Response JSON (200): arreglo de `CardApiResponse`.

Errores: 400, 500.

### 5.15 GET /api/traces?boardUrl=...

Consulta trazas historicas de un tablero.

Response JSON (200):

```json
[
  {
    "id": "uuid",
    "boardUrl": "tasku://tablero/uuid",
    "authorEmail": "owner@tasku.dev",
    "description": "Tarjeta ... movida de ... a ...",
    "date": "2026-04-23T18:50:00"
  }
]
```

Errores: 400, 500.

## 6) Notas De Implementacion

- Todas las entradas/salidas son JSON.
- La logica de negocio esta encapsulada en servicios de aplicacion.
- La capa REST no contiene reglas de dominio.
- Si un tablero esta `BLOCKED`, las mutaciones de tarjetas retornan 403.
