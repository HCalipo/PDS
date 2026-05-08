# TaskU

## 1. Resumen del proyecto

**TaskU** es una aplicación de gestión de tableros colaborativos tipo Kanban/Scrum. Permite crear tableros con listas y tarjetas, compartirlos con otros usuarios con distintos roles, y registrar un historial de actividad.

### Integrantes del equipo

- Aarón Ruiz Martínez — aaron.r.m@um.es
- Álvaro Pujante Cánovas — alvaro.pujantec@um.es
- Hugo Polo Molina — h.polomolina@um.es

---

## 2. Stack tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Backend (API REST) | Spring Boot | 4.0.4 |
| Base de datos | H2 (embedded, file-mode) | 2.x |
| Persistencia | Spring Data JPA / Hibernate | — |
| Desktop UI | JavaFX | 21.0.2 |
| JSON | Jackson (databind + jsr310) | 2.18.1 |
| Logging | SLF4J 2.0.17 + Log4j2 2.25.3 | — |
| Build | Maven (con wrapper 3.9.12) | — |

---

## 3. Estructura del proyecto

```
tasku-workspace/                  (pom.xml agregador)
├── core/                         Módulo backend (API REST)
│   ├── pom.xml
│   ├── data/                     Base de datos H2 (archivo .mv.db)
│   ├── logs/                     Logs del módulo core
│   ├── src/main/java/com/tasku/core/
│   │   ├── application/          Casos de uso (servicios de aplicación)
│   │   │   ├── port/             Interfaces de salida (EventPublisher)
│   │   │   ├── usuario/usecase/  Use case de usuario
│   │   │   ├── tablero/usecase/  Use cases de tablero, tarjeta, traza
│   │   │   │   ├── dto/          Objetos de petición (records)
│   │   │   │   └── event/        Eventos de dominio (TarjetaCreadaEvent, TarjetaMovidaEvent)
│   │   │   └── util/             Utilidades (UseCaseValidator)
│   │   ├── domain/               Capa de dominio pura (sin frameworks)
│   │   │   ├── board/
│   │   │   │   ├── exception/    Excepciones de dominio
│   │   │   │   └── port/         Interfaces de repositorio (stores)
│   │   │   └── model/            Entidades, Value Objects, Enums
│   │   └── infrastructure/       Adaptadores (REST, JPA, eventos, scheduler)
│   │       ├── api/rest/         Controladores REST y DTOs de request/response
│   │       ├── bootstrap/        CoreApplication (entry point Spring Boot)
│   │       ├── config/           Configuración (scheduling, properties)
│   │       ├── events/           Publicador y listeners de eventos
│   │       ├── persistence/jpa/  Entidades JPA, adaptadores, repositorios
│   │       └── scheduler/        Job de compactación de trazas
│   └── src/test/java/com/tasku/core/
│       ├── domain/model/         Tests unitarios del dominio (18 clases)
│       └── integration/          Tests de integración (PersistenceIntegrationTest)
│
└── ui/                           Módulo cliente desktop (JavaFX)
    ├── pom.xml
    └── src/main/java/com/tasku/ui/
        ├── bootstrap/            TaskuDesktopApplication (entry point JavaFX)
        ├── SceneManager.java     Singleton: gestión de escenas y estado de sesión
        ├── client/               Cliente HTTP y DTOs
        │   ├── dto/request/      Peticiones a la API (records)
        │   ├── dto/response/     Respuestas de la API (records)
        │   └── http/             TaskuApiClient + DesktopApiException
        └── presentation/controllers/  Controladores JavaFX (12 pantallas)


```

---

## 4. Glosario de Términos

* **Usuario:** Persona identificada en el sistema mediante un correo electrónico (`Email`) y una fecha de registro. Puede crear tableros o colaborar en ellos.
* **Tablero:** Raíz de agregado y espacio de trabajo principal. Contiene listas (`ListaTablero`), comparticiones (`TableroCompartido`), estado (`EstadoTablero`) y se identifica por su `TableroUrl`.
* **ListaTablero:** Contenedor de tarjetas dentro de un tablero. Representa una fase del flujo de trabajo (ej. *TODO, DOING, DONE*). Tiene un límite de tarjetas (`cardLimit`) y un color.
* **Tarjeta:** La unidad base de la aplicación (clase abstracta). Puede moverse entre listas, recibir etiquetas (`EtiquetaTarjeta`) y archivarse.
* **TarjetaTarea:** Subtipo concreto de `Tarjeta` con `TipoTarjeta.TAREA`. No añade campos extra al supertipo.
* **TarjetaChecklist:** Subtipo concreto de `Tarjeta` con `TipoTarjeta.CHECKLIST`. Contiene una lista de `ElementoChecklist` que se pueden marcar individualmente.
* **ElementoChecklist:** Value Object que representa una subtarea dentro de una `TarjetaChecklist`: tiene descripción y estado completado/pendiente.
* **EtiquetaTarjeta:** Value Object que clasifica visualmente una tarjeta mediante un nombre y un color hexadecimal.
* **TableroCompartido:** Entidad que registra con qué email y con qué rol (`RolComparticion`) se ha compartido un tablero.
* **TrazaActividad:** Registro de una acción ocurrida en un tablero: guarda la URL del tablero, el email del autor, una descripción y la fecha/hora.
* **TableroUrl:** Value Object que encapsula y normaliza la URL de un tablero. Acepta UUID bare o formato completo `tasku://tablero/<uuid>`.
* **Email:** Value Object que encapsula y valida el correo electrónico de un usuario (normalizado a minúsculas).
* **TarjetaId:** Value Object que identifica de forma única una tarjeta dentro del dominio (`UUID`).
* **ListaTableroId:** Value Object que identifica de forma única una lista dentro de un tablero (`UUID`).
* **DefinicionListaInicial:** Value Object usado al crear un tablero para declarar las listas iniciales (nombre + límite de tarjetas).
* **EstadoTablero:** Enum con valores `ACTIVE` y `BLOCKED`. Cuando está `BLOCKED` se impide la creación, renombrado, eliminación, completado y asignación de etiquetas en tarjetas. El movimiento de tarjetas entre listas **sí está permitido** aunque el tablero esté bloqueado.
* **TipoTarjeta:** Enum con valores `TAREA` y `CHECKLIST`. Determina el subtipo concreto de una tarjeta.
* **RolComparticion:** Enum con valores `VIEWER`, `EDITOR` y `ADMIN`. Controla los permisos de un colaborador sobre el tablero (`canEdit()`, `isAdmin()`).
* **Dueño del tablero:** Usuario cuyo email aparece como `ownerEmail` en el tablero. Su rol efectivo siempre es `ADMIN`.
* **Colaborador:** Usuario añadido a `sharedWith` del tablero con un `RolComparticion` explícito.


## 5. Arquitectura

### 5.1. Arquitectura Hexagonal (Ports & Adapters)

El proyecto sigue una arquitectura hexagonal con DDD, donde el dominio es puro (sin dependencias de frameworks) y la infraestructura implementa los puertos definidos en el dominio.

```
┌──────────────────────────────────────────────────────────────────┐
│  INFRAESTRUCTURA  (cambia según tecnología)                      │
│                                                                  │
│   ┌──────────────────┐            ┌──────────────────────────┐   │
│   │   API REST       │            │   Persistencia JPA / H2  │   │
│   │  CardRestCtrl    │            │   JpaTableroStoreAdapter │   │
│   │  BoardRestCtrl   │            │   SpringDataRepository   │   │
│   └────────┬─────────┘            └────────────┬─────────────┘   │
│            │ llama                             ▲  implementa     │
│   ┌────────▼───────────────────────────────────┴──────────────┐  │
│   │  APLICACIÓN  (orquesta, no inventa reglas)                │  │
│   │                                                           │  │
│   │   TableroUseCaseService    TarjetaApplicationService      │  │
│   │   UsuarioUseCaseService    TrazaActividadUseCaseService   │  │
│   │                                                           │  │
│   │   ┌───────────────────────────────────────────────────┐   │  │
│   │   │  DOMINIO  (reglas de negocio puras)               │   │  │
│   │   │                                                   │   │  │
│   │   │   Tablero · ListaTablero · Tarjeta · Usuario      │   │  │
│   │   │   TrazaActividad · TableroCompartido              │   │  │
│   │   │                                                   │   │  │
│   │   │   Puertos: TableroStore · TarjetaStore            │   │  │
│   │   │            UsuarioStore · TrazaStore              │   │  │
│   │   │            ListaTableroStore                      │   │  │
│   │   └───────────────────────────────────────────────────┘   │  │
│   └───────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

### 5.2. Flujo de datos

```
Cliente JavaFX (ui)
    │  HTTP REST (HttpClient + Jackson)
    ▼
Controlador REST (core/api/rest)
    │
    ▼
Caso de Uso (application/usecase)
    │
    ├──► Puerto de dominio (domain/port)
    │       │
    │       ▼
    │   Adaptador JPA (infrastructure/persistence/jpa/adapter)
    │       │
    │       ▼
    │   Spring Data Repository
    │       │
    │       ▼
    │   H2 Database
    │
    └──► EventPublisher (application/port)
            │
            ▼
        SpringEventPublisher (infrastructure/events)
            │
            ▼
        Listeners → TrazaActividad persistida
```

---

### 5.3. DTOs y flujo de datos entre capas

Los DTOs son registros inmutables implementados mediante records que transportan datos entre las capas sin exponer el modelo de dominio.

1. **Application DTOs** (`core/application/.../dto/`) Son la entrada a los casos de uso. Traducen las peticiones externas a objetos que el dominio entiende, utilizando tipos de dominio como `TarjetaId`, `Email`, etc.

2. **API DTOs** (`core/infrastructure/api/rest/request/` y `response/`) Definen el contrato de la API REST. Los mappers (`CardRequestMapper`, `ApiRestMapper`) se encargan de convertirlos hacia/desde los objetos de aplicación y dominio.

3. **Client DTOs** (`ui/client/dto/request/` y `response/`) Son un espejo de los API DTOs del core, redefinidos en el módulo `ui` porque ambos módulos son independientes. Esto implica que cualquier cambio en los DTOs del core debe replicarse manualmente en el ui.

**Flujo típico:** Controlador REST recibe un API request DTO → `CardRequestMapper` lo convierte a Application DTO → Caso de uso opera con el dominio → La respuesta se mapea con `ApiRestMapper` a un API response DTO → JSON. En el lado del cliente, `TaskuApiClient` serializa/deserializa los mismos DTOs con Jackson.

---

## 6. Modelo de Dominio (DDD) y Arquitectura



## 6.1. Arquitectura Hexagonal (Puertos y Adaptadores)

Se ha implementado con una arquitectura Hexagonal:

* **El Dominio (Capa Interna):** No tiene dependencias externas. Contiene los Agregados, Entidades, *Value Objects* y las interfaces (Puertos) que definen qué necesita el dominio del mundo exterior (ej. `TableroStore`).
* **La Capa de Aplicación:** Orquesta los Casos de Uso (ej. `TableroUseCaseService`), delegando la lógica compleja al dominio y coordinando las transacciones.
* **La Infraestructura (Adaptadores):** Implementa los Puertos. Aquí residen Spring Boot, JPA y la base de datos H2. Se utilizan clases adaptadoras (ej. `JpaTableroStoreAdapter`) y Mapeadores (ej. `TableroJpaMapper`) para traducir las entidades de base de datos (`TableroJpaEntity`) a entidades de dominio puro, garantizando un acoplamiento nulo.

## 6.2. Diseño Táctico: Agregados y Entidades

El diseño divide la complejidad del sistema en distintos agregados. 

### 6.2.1. Agregado: Tablero
El `Tablero` es el agregado principal que gestiona la estructura del espacio de trabajo.
* **Entidades Internas:** Custodia colecciones de `ListaTablero` (las columnas del tablero Kanban) y `TableroCompartido` (la gestión de accesos y roles de los invitados).
* **Invariantes:** Garantiza que no se puedan crear listas duplicadas o realizar mutaciones si su estado (`EstadoTablero`) se encuentra bloqueado (`BLOCKED`).

### 6.2.2 Agregado: Tarjeta
Para evitar cargar en memoria un tablero enorme cada vez que se edita una tarea, la `Tarjeta` se ha diseñado como un agregado independiente.
* **Relación por Identidad:** Una tarjeta no contiene una referencia en memoria a su tablero, sino que lo referencia mediante *Value Objects* (`TableroId`, `ListaTableroId`).
* **Polimorfismo:** El dominio maneja distintos tipos funcionales a través de subclases lógicas o discriminadores, diferenciando entre tarjetas de texto simple (`TarjetaTarea`) y tarjetas con subtareas (`TarjetaChecklist`).

### 6.2.3. Entidad: Usuario

La entidad `Usuario` es la responsable de modelar la identidad y el acceso al sistema. Su estructura interna mantiene la fecha de registro y el correo electrónico.

* **Validación de formato (Value Object):** El correo electrónico no se define como un tipo primitivo (`String`), sino que se encapsula en el *Value Object* `Email`. Esta decisión de diseño permite que la propia instanciación del objeto valide mediante expresiones regulares que el formato es correcto, impidiendo que un usuario con un correo inválido llegue a existir en memoria.
* **Invariante de unicidad y Puertos:** La lógica de negocio establece que no pueden existir dos usuarios con el mismo correo. Para cumplir esta regla sin acoplar el dominio a la base de datos, los casos de uso (como `UsuarioUseCaseService`) interactúan con la interfaz `UsuarioStore`. Este puerto define los métodos necesarios para consultar la existencia previa del correo antes de proceder con el registro.

## 6.3. Value Objects (Objetos de Valor)

Se ha hecho un uso extensivo de *Value Objects* para dotar de semántica al modelo y centralizar validaciones primitivas. Ejemplos clave son:

* `Email`: Valida el formato correcto en su instanciación.

* `TableroUrl` y `TarjetaId`: Encapsulan la identidad (UUIDs), evitando el paso de cadenas de texto (`String`) genéricas que propician errores.

| Value Object | Qué valida | Qué normaliza |
|-------------|-----------|--------------|
| `Email` | Regex `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$` | Convierte a minúsculas, trim |
| `TableroUrl` | Contiene UUID válido | Acepta UUID bare o `tasku://tablero/uuid` → siempre `tasku://tablero/uuid` |
| `ListaTableroId` | UUID no nulo | — |
| `TarjetaId` | UUID no nulo | — |
| `EtiquetaTarjeta` | `name` y `colorHex` no vacíos | Trim |
| `ElementoChecklist` | `description` no vacía | Trim |
| `DefinicionListaInicial` | `name` no vacío · `cardLimit > 0` | Trim del nombre |


## 6.4. Eventos de Dominio y Trazabilidad

Para satisfacer el requisito funcional de mantener un historial de auditoría sin ensuciar la lógica principal de las tarjetas, se emplean **Eventos de Dominio**.
* Cuando una tarjeta cambia de estado (por ejemplo, al moverse de lista), el agregado emite un `TarjetaMovidaEvent`.
* La capa de infraestructura escucha estos eventos (mediante `TarjetaMovidaTrazaListener`) y persiste de forma asíncrona o desacoplada una nueva entidad `TrazaActividad`.
* Esto permite que la gestión del historial y su posterior purgado (`CompactacionTrazasJob`) existan como un subdominio técnico totalmente independiente.

---

## 7. API REST (core)

### 7.1. Usuarios

| Método | Endpoint | Uso | Códigos |
|---|---|---|---|
| POST | `/api/usuarios/registro` | Registrar nuevo usuario | 201 |
| POST | `/api/usuarios/login` | Iniciar sesión | 200 |

### 7.2. Tableros

| Método | Endpoint | Uso | Códigos |
|---|---|---|---|
| POST | `/api/boards` | Crear tablero | 200/201 |
| GET | `/api/boards/by-url?url=` | Obtener tablero por URL | 200 |
| GET | `/api/boards/owned?ownerEmail=` | Tableros del usuario | 200 |
| GET | `/api/boards/shared?email=` | Tableros compartidos con el usuario | 200 |
| GET | `/api/boards/role?boardUrl=&email=` | Rol del usuario en el tablero | 200 |
| POST | `/api/boards/share` | Compartir tablero | 200 |
| POST | `/api/boards/lists` | Crear lista | 200/201 |
| PATCH | `/api/boards/lists/{listId}` | Renombrar lista | 200 |
| DELETE | `/api/boards/lists/{listId}` | Eliminar lista | 204 |
| PATCH | `/api/boards/status` | Cambiar estado (ACTIVE/BLOCKED) | 200/204 |
| POST | `/api/boards/{boardUrl}/join` | Unirse a tablero | 200/201 |

### 7.3. Tarjetas

| Método | Endpoint | Uso | Códigos |
|---|---|---|---|
| POST | `/api/cards` | Crear tarjeta (TAREA o CHECKLIST) | 200/201 |
| GET | `/api/cards?listId=` | Tarjetas de una lista | 200 |
| GET | `/api/cards/{cardId}` | Tarjeta por ID | 200 |
| GET | `/api/cards/completed?boardUrl=` | Tarjetas completadas | 200 |
| PATCH | `/api/cards/move` | Mover tarjeta entre listas | 200/204 |
| PATCH | `/api/cards/complete` | Completar/archivar tarjeta | 200 |
| PATCH | `/api/cards/labels` | Asignar etiqueta | 200 |
| PATCH | `/api/cards/checklist/toggle` | Marcar/desmarcar ítem de checklist | 200 |
| PATCH | `/api/cards/{cardId}` | Renombrar tarjeta | 200 |
| DELETE | `/api/cards/{cardId}` | Eliminar tarjeta | 204 |

### 7.4. Trazas / Historial

| Método | Endpoint | Uso | Códigos |
|---|---|---|---|
| GET | `/api/traces?boardUrl=` | Trazas de actividad del tablero | 200 |

---

## 8. Módulo UI (cliente JavaFX)

### 8.1. Gestión de escenas

`SceneManager` (singleton) maneja las transiciones entre pantallas. Expone:

- `switchTo(fxmlName)` — Cambia la raíz de la escena actual.
- `startMainApp()` — Abre una nueva ventana maximizada con `Principal.fxml` y cierra la ventana de login.
- `openDialog(fxmlName)` — Abre un diálogo modal.
- `openDialogAndGetController(fxmlName)` — Abre diálogo y devuelve el controlador.

Almacena el estado de sesión: `currentUserEmail`, `currentBoardUrl`, `currentBoardName`, `currentListId`, `currentUserRole`, `newUser`.

### 8.2. Pantallas (FXML + Controlador)

| FXML | Controlador | Descripción |
|---|---|---|
| `InicioSesion.fxml` | `InicioSesionController` | Login con email |
| `Registro.fxml` | `RegistroController` | Registro de nuevo usuario |
| `Principal.fxml` | `PrincipalController` | Pantalla principal del tablero |
| `ListaTareas.fxml` | `ListaTareasController` | Columna individual del tablero |
| `AñadirTablero.fxml` | `AñadirTableroController` | Crear nuevo tablero (plantillas) |
| `AñadirLista.fxml` | `AñadirListaController` | Crear nueva lista con color |
| `CreateCard.fxml` | `CrearTarjetaController` | Crear tarjeta (tarea/checklist) |
| `CreateTag.fxml` | `CreateTagController` | Crear etiqueta personalizada |
| `compartirTablero.fxml` | `CompartirTableroController` | Compartir tablero |
| `UnirTablero.fxml` | `UnirTableroController` | Unirse a tablero por URL |
| `Historial.fxml` | `HistorialController` | Historial de actividad |
| `Historial_card.fxml` | `Historial_cardController` | Item individual del historial |
| `TarjetaTexto.fxml` | — | Vista standard de tarjeta texto |
| `TarjetaChecklist.fxml` | — | Vista standard de tarjeta checklist |
| `EditarTarjetaTexto.fxml` | `EditarTarjetaTextoController` | Diálogo de edición |
| `EditarTarjetaChecklist.fxml` | `EditarTarjetaChecklistController` | Diálogo de edición |

### 8.3. Cliente HTTP

`TaskuApiClient` se conecta a `http://localhost:8080` por defecto.

URL base configurable:
- Variable de entorno: `TASKU_API_BASE_URL`
- Propiedad del sistema: `tasku.api.base-url`

Usa `java.net.http.HttpClient` + Jackson ObjectMapper con `JavaTimeModule` y `FAIL_ON_UNKNOWN_PROPERTIES = false`.

---

## 9. Persistencia

### 9.1. Estrategia

- Base de datos H2 en modo archivo: `./data/tasku-db`
- DDL automático: `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.open-in-view=false` (desactivado para evitar lazy loading en vistas)
- Herencia de tarjetas: `SINGLE_TABLE` con `DiscriminatorColumn("tipo_tarjeta")`

### 9.2. Entidades JPA y tablas

| Entidad JPA | Tabla | Tipo de PK |
|---|---|---|
| `UsuarioJpaEntity` | `usuarios` | String (email) |
| `TableroJpaEntity` | `tableros` | String (url) |
| `ListaTableroJpaEntity` | `listas` | UUID |
| `TableroCompartidoJpaEntity` | `tableros_compartidos` | Long (autoincrement) |
| `TarjetaJpaEntity` (abstract) → `TarjetaTareaJpaEntity` / `TarjetaChecklistJpaEntity` | `tarjetas` (single table) | UUID |
| `TrazaJpaEntity` | `trazas` | UUID |

### 9.3. Mapeo dominio <--> JPA

Cada store adapter implementa un puerto de dominio y traduce entre objetos de dominio y entidades JPA:

| Adaptador | Puerto | Mapper |
|---|---|---|
| `JpaUsuarioStoreAdapter` | `UsuarioStore` | `UsuarioJpaMapper` |
| `JpaTableroStoreAdapter` | `TableroStore` | `TableroJpaMapper` |
| `JpaTarjetaStoreAdapter` | `TarjetaStore` | `TarjetaJpaMapper` |
| `JpaTrazaStoreAdapter` | `TrazaStore` | `TrazaJpaMapper` |
| `JpaListaTableroStoreAdapter` | `ListaTableroStore` | (directo) |

---

## 10. Logging

### 10.1. Configuración

- **Fachada:** SLF4J 2.0.17
- **Implementación:** Log4j2 2.25.3
- **Archivo:** `~/.tasku/logs/tasku-core.log`
- **Niveles:** Spring/Hibernate → WARN, Root → INFO

### 10.2. Uso en código

```java
private static final Logger log = LoggerFactory.getLogger(MiClase.class);

log.info("Mensaje informativo");
log.warn("Situación anómala: {}", detalle);
log.error("Error al procesar {}", recurso, exception);
```

La excepción se pasa como último argumento para incluir el stack trace completo.

---

## 11. Eventos

### 11.1. Eventos de dominio

| Evento | Disparado por | Campos |
|---|---|---|
| `TarjetaCreadaEvent` | `TarjetaApplicationService.createCard()` | cardId, cardTitle, listId, listName, boardUrl, authorEmail, createdAt |
| `TarjetaMovidaEvent` | `TarjetaApplicationService.moveCard()` | cardId, cardTitle, sourceListId, sourceListName, destListId, destListName, boardUrl, authorEmail, movedAt |

### 11.2. Listeners

- `TarjetaCreadaTrazaListener`: Escucha `TarjetaCreadaEvent` y persiste una traza: `"Tarjeta 'X' creada en la lista 'Y'"`
- `TarjetaMovidaTrazaListener`: Escucha `TarjetaMovidaEvent` y persiste una traza: `"Tarjeta 'X' movida de 'A' a 'B'"`

---

## 12. Compilación y ejecución

### 12.1. Requisitos

- JDK 21
- Maven 3.9+ (o usar el wrapper incluido)

### 12.2. Compilar todo el proyecto

```bash
mvn clean compile
```

### 12.3. Ejecutar el backend (core)

```bash
 mvn -f core/pom.xml spring-boot:run
```

La API arranca en `http://localhost:8080`.

### 12.4. Ejecutar el cliente desktop (ui)

```bash
mvn -f ui/pom.xml javafx:run   
```

### 12.5. Orden de arranque

1. Iniciar el backend (`core`) con `mvn -f core/pom.xml spring-boot:run`
2. Iniciar el cliente (`ui`) con `mvn -f ui/pom.xml javafx:run `

---

## 13. Configuración

### 13.1. Backend (`application.properties`)

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `spring.datasource.url` | `jdbc:h2:file:./data/tasku-db` | URL de conexión H2 |
| `spring.h2.console.enabled` | `true` | Consola H2 en `/h2-console` |

### 13.2. Cliente UI

| Variable / Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `TASKU_API_BASE_URL` (env) | `http://localhost:8080` | URL base de la API |
| `tasku.api.base-url` (sysprop) | `http://localhost:8080` | URL base de la API |

---

## 14. Documentación relacionada

- `docs/diagramaDominio.md` — Modelo de dominio, lenguaje ubicuo, historias de usuario
- `docs/manualUsuario.md` — Manual de usuario con capturas de pantalla
- `docs/historiasUsuario.md` — Documento con las historias de usuario.