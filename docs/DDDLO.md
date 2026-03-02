# Modelo de Dominio y Lenguaje Obicuo

Este documento define los conceptos fundamentales de nuestra aplicación de gestión de trabajo colaborativo, basándonos en los principios de DDD. Establecer este **Lenguaje Ubicuo** asegura que tanto el código como las discusiones del equipo utilicen exactamente los mismos términos.

## Glosario de Términos

* **Usuario:** Persona identificada en el sistema mediante un correo electrónico. Puede crear tableros o colaborar en ellos.
* **Tablero:** Es el espacio de trabajo principal. Contiene listas de tareas, gestiona los accesos mediante una URL única y registra la historia de acciones. Puede cambiar a un estado "Bloqueado".
* **Lista de Tareas:** Contenedor de tarjetas dentro de un tablero. Representa una fase o estado del flujo de trabajo (ej. *TODO, DOING, DONE*).
* **Lista de Completadas:** Una lista especial dentro del tablero que contiene las tarjetas que han sido finalizadas.
* **Tarjeta:** La unidad base de la aplicación. Puede moverse entre listas, recibir etiquetas y marcarse como completada.
* **Tarjeta de Tarea:** Subtipo de tarjeta enfocada en asignar una actividad concreta a un usuario.
* **Tarjeta de Checklist:** Subtipo de tarjeta que contiene una lista de comprobación de subtareas.
* **Etiqueta:** Elemento clasificador asignado a las tarjetas. Está definida por un color y una descripción.
* **Historial de movimientos:** Registro inmutable de una acción realizada por un usuario sobre un tablero.
* **URL de Acceso:** Identificador único que actúa como mecanismo de invitación y acceso al tablero para los colaboradores.

---

```mermaid
classDiagram
    direction LR

    class Usuario {
        - Email correo
        - String nombre
    }
    class Tablero {
        - URL url
        - boolean estaBloqueado
        - Email dueno
        - Set<Email> colaboradores
        - List<Tarjeta> tareas
        - ListaCompletadas listaCompletadas
        - List<HistorialMovimientos> historial
    }
    class ListaTareas {
        - String id
        - int orden
        - List<Tarjeta> tarjetas
    }
    class ListaCompletadas {
        - String id
        - List<Tarjeta> tarjetas
    }
    class Tarjeta {
        - String id
        - String titulo
        - String descripcion
        - boolean estaCompletada
        - LocalDateTime fechaCreacion
        - Set<Etiqueta> etiquetas
    }
    class TarjetaTarea {
        - Email asignadoA
    }
    class TarjetaChecklist {
        - List<ElementoChecklist> items
    }
    class Etiqueta {
        - String id
        - String texto
        - ColorEtiqueta color
    }
    class HistorialMovimientos {
        - String id
        - LocalDateTime fechaHora
        - String accionDetalle
        - Email autor
    }
    class Email {
        - String value
    }
    class URL {
        - String value
    }
    class ColorEtiqueta {
        - String value
    }
    class ElementoChecklist {
        - String descripcion
        - boolean estaMarcado
    }

    Tarjeta <|-- TarjetaTarea
    Tarjeta <|-- TarjetaChecklist

    Tablero o-- ListaTareas : listas
    Tablero o-- ListaCompletadas : listaCompletadas
    Tablero o-- HistorialMovimientos : historial
    Tablero o-- Email : dueno
    Tablero o-- URL : url
    ListaTareas o-- Tarjeta : tarjetas
    ListaCompletadas o-- Tarjeta : tarjetas
    Tarjeta o-- Etiqueta : etiquetas
    TarjetaTarea o-- Email : asignadoA
    TarjetaChecklist o-- ElementoChecklist : contiene
    Usuario --> Tablero : crea
    Etiqueta o-- ColorEtiqueta : color
    HistorialMovimientos o-- Email : autor
```

---

## Explicación del diagrama

Este diagrama de clases representa la estructura de nuestro dominio, definiendo cómo interactúan las Entidades y los Value Objects:

* **Tablero:** Es el componente principal. Contiene las listas de tareas, la lista de tarjetas terminadas y el historial de cambios. Cada tablero tiene una `URL` única para compartirlo y un dueño asignado mediante su `Email`.
* **Organización en Listas:** Un `Tablero` agrupa varias `ListaTareas` y una `ListaCompletadas`. Dentro de estas listas es donde se guardan y organizan las diferentes `Tarjetas`.
* **Tipos de Tarjetas (Herencia):** Existe una `Tarjeta` básica que guarda la información común (título, descripción, estado). De ella nacen dos tipos especiales: la `TarjetaTarea` (que se asigna a una persona) y la `TarjetaChecklist` (que tiene subtareas que se pueden ir marcando).
* **Value Objects:** En lugar de usar texto simple (`String`) para cosas importantes, creamos clases específicas como `Email`, `URL` o `ColorEtiqueta`. Así nos aseguramos de que un correo tenga formato válido o que un color sea correcto desde el momento en que se crean.
* **Etiquetas e Historial:** Las tarjetas usan `Etiqueta` para clasificarse visualmente. Por otro lado, el tablero usa el `HistorialMovimientos` como una "caja negra" para recordar qué usuario hizo cada cambio y en qué momento.
* **Usuarios:** El `Usuario` es la persona que usa la aplicación. Se identifica por su correo electrónico y puede crear sus propios tableros o colaborar en los tableros de otras personas.

---

## Historias de usuario

### Objetivo 1: Gestión de Tableros y Accesos

* **Historia 1.1: Creación de un nuevo tablero (GUI)**
  * Como usuario, quiero introducir mi correo electrónico en la pantalla inicial para poder crear un nuevo tablero de trabajo, colaborativo o no.
* **Historia 1.2: Generación y persistencia de tablero (API)**
  * Como sistema cliente (GUI), quiero enviar un correo electrónico al backend para que genere un nuevo tablero con una URL única y privada, a no ser que se quiera compartir.
* **Historia 1.3: Acceso mediante URL (GUI)**
  * Como usuario colaborador, quiero introducir la URL de un tablero en la aplicación para acceder a él y colaborar.
* **Historia 1.4: Recuperación de tablero por URL (API)**
  * Como sistema cliente (GUI), quiero solicitar al backend los datos de un tablero usando su URL para mostrarlos en pantalla.

---

### Objetivo 2: Configuración del Tablero (Listas y Bloqueos)

* **Historia 2.1: Creación y modificación de listas (GUI)**
  * Como usuario, quiero crear nuevas listas y cambiarles el nombre desde la interfaz para estructurar mi flujo de trabajo.
* **Historia 2.2: Persistencia de listas (API)**
  * Como sistema cliente (GUI), quiero enviar los datos de una nueva lista o la modificación de su nombre al backend para que se guarden.
* **Historia 2.3: Bloqueo temporal del tablero (GUI)**
  * Como usuario, quiero accionar un botón en la interfaz para bloquear temporalmente la creación de nuevas tarjetas en el tablero.
* **Historia 2.4: Gestión del estado de bloqueo (API)**
  * Como sistema cliente (GUI), quiero cambiar el estado de bloqueo de un tablero en el backend para aplicar las reglas de negocio.

---

### Objetivo 3: Gestión de Tarjetas (Tareas y Checklists)

* **Historia 3.1: Creación de tarjetas (GUI)**
  * Como usuario, quiero añadir una tarjeta nueva a una lista especificando si es una "Tarea" o un "Checklist" para definir el trabajo a realizar.
* **Historia 3.2: Creación de tarjetas y validación de reglas (API)**
  * Como sistema cliente (GUI), quiero enviar los datos de la nueva tarjeta al backend para que verifique las reglas y los guarde.
* **Historia 3.3: Mover tarjetas entre listas (GUI)**
  * Como usuario, quiero mover una tarjeta de una lista a otra para actualizar su estado de progreso.
* **Historia 3.4: Actualización de ubicación y generación de traza (API)**
  * Como sistema cliente (GUI), quiero informar al backend del movimiento de una tarjeta para que actualice su relación y registre la acción.
* **Historia 3.5: Completar tarjeta (GUI)**
  * Como usuario, quiero marcar una tarjeta como completada para quitarla de las listas activas.
* **Historia 3.6: Lógica de autocompletado (API)**
  * Como sistema cliente (GUI), quiero notificar al backend que una tarjeta se ha completado para que aplique la regla de negocio correspondiente.

---

### Objetivo 4: Etiquetas e Historial

* **Historia 4.1: Asignación de etiquetas (GUI)**
  * Como usuario, quiero asignar etiquetas de colores a las tarjetas desde su vista de detalle para clasificarlas visualmente.
* **Historia 4.2: Persistencia de etiquetas (API)**
  * Como sistema cliente (GUI), quiero enviar la asociación de una etiqueta y una tarjeta al backend para que se guarde esta relación.
* **Historia 4.3: Visualización del historial (GUI)**
  * Como usuario, quiero abrir un panel de historial para ver el registro de todas las acciones que han ocurrido en el tablero y, en caso de ser compartido, quién hizo esa acción.
* **Historia 4.4: Consulta de trazas de auditoría (API)**
  * Como sistema cliente (GUI), quiero solicitar al backend el listado de acciones históricas de un tablero.

---
