# Historias de Usuario

## 1. Gestión de Acceso e Identidad

* **HU01: Registro de nuevo usuario.** Como visitante, quiero registrarme en la plataforma utilizando mi correo electrónico para poder tener mi propio espacio de trabajo.
* **HU02: Inicio de sesión.** Como usuario registrado, quiero acceder al sistema con mi correo para visualizar y gestionar mis tableros.

## 2. Gestión de Tableros (Espacios de Trabajo)

* **HU03: Creación de tableros.** Como usuario, quiero crear un nuevo tablero en blanco para iniciar la gestión de un proyecto.
* **HU04: Listado de tableros.** Como usuario, quiero visualizar un panel con todos los tableros de los que soy propietario o miembro, para acceder rápidamente a ellos.
* **HU05: Bloqueo de tablero.** Como propietario del tablero, quiero cambiar su estado a "Bloqueado" (`BLOCKED`) para impedir que se añadan nuevas tarjetas temporalmente, permitiendo únicamente la reubicación de las existentes.
* **HU06: Desbloqueo de tablero.** Como propietario, quiero revertir el estado de bloqueo de un tablero para reanudar la creación normal de tareas.

## 3. Estructura y Listas

* **HU07: Creación de listas.** Como administrador del tablero, quiero añadir nuevas listas (ej. *Pendiente*, *En Proceso*) para definir el flujo de trabajo.
* **HU08: Renombrado de listas.** Como administrador del tablero, quiero modificar el título de una lista existente para adaptar la nomenclatura si el proyecto cambia.
* **HU09: Eliminación de listas.** Como administrador, quiero eliminar una lista que ya no necesito para mantener el tablero ordenado.
    * *Criterio de Aceptación:* Solo se puede eliminar si el dominio valida que el usuario tiene permisos suficientes.

## 4. Gestión de Tarjetas y Subtareas

* **HU10: Creación de tarjetas.** Como miembro del tablero, quiero añadir una nueva tarjeta dentro de una lista, especificando si es de tipo "Texto" o "Checklist".
* **HU11: Edición del título.** Como miembro, quiero renombrar una tarjeta para corregir su título.
* **HU12: Movimiento entre listas.** Como miembro, quiero mover una tarjeta de una lista a otra para reflejar su avance en el flujo de trabajo.
* **HU13: Gestión de Checklist.** Como miembro, quiero marcar y desmarcar elementos individuales dentro de una tarjeta de tipo Checklist para llevar el seguimiento de subtareas atómicas.
* **HU14: Clasificación por etiquetas.** Como miembro, quiero asignar o retirar etiquetas de colores a una tarjeta para categorizarla visualmente (ej. *Urgente*, *Bug*, *Mejora*).
* **HU15: Finalización automática.** Como miembro, quiero tener la opción de marcar una tarjeta como "Completada" para que el sistema la mueva automáticamente a la lista predeterminada de finalizadas.

## 5. Colaboración y Permisos

* **HU16: Generación de enlace de invitación.** Como propietario, quiero obtener la URL única de mi tablero para compartirla con mis compañeros.
* **HU17: Unirse a un tablero.** Como usuario registrado, quiero acceder mediante una URL compartida a un tablero de otro usuario para empezar a colaborar.
* **HU18: Gestión de Roles.** Como propietario, quiero que los invitados tengan un rol específico (como `ADMIN`, `EDITOR` o `VIEWER`) para restringir quién puede alterar la estructura (listas) y quién solo puede visualizar o mover tarjetas.

## 6. Auditoría y Trazabilidad

* **HU19: Historial de actividad.** Como usuario del tablero, quiero consultar un registro detallado de los eventos ocurridos (quién movió qué tarjeta, quién creó una lista y cuándo) para mantener el control sobre los cambios realizados por el equipo.