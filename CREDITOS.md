# CRÉDITOS — TaskU

## Aarón Ruiz Martínez — aaron.r.m@um.es

**Rol principal:** Backend, dominio, persistencia, API REST, arquitectura hexagonal.

- Inicialización del proyecto y modelo de dominio (Tablero, Tarjeta, Usuario, VOs).
- Reestructuración a arquitectura hexagonal con capas dominio/aplicación/infraestructura.
- Implementación de persistencia JPA, adaptadores y API REST.
- Sistema de compartición con 3 roles (VIEWER / EDITOR / ADMIN).
- Seguridad, logging, bloqueo de tablero y otras reglas de negocio.

Commits destacados:
- `f6be971` — Reestructuración a arquitectura hexagonal
- `c3835bb` — Compartición de tableros con 3 roles
- `adef94d` — Seguridad: ocultar botón compartir a usuarios sin permiso

## Álvaro Pujante Cánovas — alvaro.pujantec@um.es

**Rol principal:** Interfaces gráficas (FXML/CSS), tests de dominio, documentación.

- Creación de todas las pantallas de la aplicación (14 FXML + `styles.css`).
- Implementación de los 18 tests unitarios del modelo de dominio.
- Sustitución de cuadros de diálogo nativos por interfaces FXML dedicadas.
- Manual de usuario, diagrama de dominio e historias de usuario.

Commits destacados:
- `831226a` — Creación de interfaces FXML y estilos CSS
- `6376a2d` — Tests unitarios de dominio (18 clases)
- `8399d5b` — Sustitución de popups por interfaces dedicadas

## Hugo Polo Molina — h.polomolina@um.es

**Rol principal:** Controladores JavaFX, funcionalidad cliente y conexion con la API.

- Implementación del registro e inicio de sesión de usuarios.
- Funcionalidad de unirse a tablero mediante URL compartida.
- Menú contextual de tarjetas, ajustes visuales y de ventanas.
- Refactor de tests y mejoras en la interfaz principal.

Commits destacados:
- `8c683d5` — Registro de usuarios implementado
- `f40439a` — Implementación de unirse a tablero
- `da597ca` — Menú contextual de 3 puntos (editar/eliminar)

---
