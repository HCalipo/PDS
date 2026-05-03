# Implementación del sistema de logging

Este documento describe cómo se ha configurado el sistema de logging en el proyecto TaskU, sustituyendo las salidas por terminal (`System.out`, `System.err`, `printStackTrace`) por un sistema de log estructurado basado en **SLF4J + Log4j2**.

---

## Tecnologías utilizadas

| Librería | Versión | Rol |
|---|---|---|
| `slf4j-api` | 2.0.17 | Fachada de logging: interfaz que usa el código |
| `log4j-core` | 2.25.3 | Implementación que procesa y escribe los logs |
| `log4j-slf4j2-impl` | 2.25.3 | Puente que conecta SLF4J con Log4j2 |

La fachada SLF4J desacopla el código de la implementación concreta: si en el futuro se cambia Log4j2 por otro sistema, el código fuente no necesita modificarse.

---

## Dependencias añadidas a los POM

Las tres dependencias se han añadido a **ambos módulos** (`core` y `ui`).

```xml
<!-- Log4j2 -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.25.3</version>
</dependency>

<!-- SLF4J -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.17</version>
</dependency>

<!-- Conector log4j y slf4j -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j2-impl</artifactId>
    <version>2.25.3</version>
    <scope>compile</scope>
</dependency>
```

### Exclusión en el módulo `core`

Spring Boot incluye por defecto **Logback** como implementación de logging. Para evitar el conflicto entre Logback y Log4j2 (dos implementaciones SLF4J en el classpath), se ha excluido `spring-boot-starter-logging` de cada starter que lo arrastra:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- Ídem para spring-boot-starter-validation y spring-boot-starter-webmvc -->
```

---

## Configuración: log4j2.xml

Cada módulo tiene su propio archivo de configuración en `src/main/resources/log4j2.xml`. Log4j2 lo detecta automáticamente al arrancar.

### Módulo `ui` — `ui/src/main/resources/log4j2.xml`

```xml
<Configuration status="WARN">
    <Appenders>
        <RollingFile name="FileAppender"
                     fileName="logs/tasku-ui.log"
                     filePattern="logs/tasku-ui-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <DefaultRolloverStrategy max="7"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

### Módulo `core` — `core/src/main/resources/log4j2.xml`

Igual que el de `ui` pero escribe en `logs/tasku-core.log` y filtra el ruido de Spring e Hibernate a nivel `WARN`:

```xml
<Loggers>
    <Logger name="org.springframework" level="WARN"/>
    <Logger name="org.hibernate"       level="WARN"/>
    <Root level="INFO">
        <AppenderRef ref="FileAppender"/>
    </Root>
</Loggers>
```

### Comportamiento del appender

- **Destino:** archivo en la carpeta `logs/` relativa al directorio de ejecución.
- **Rotación por fecha:** nuevo archivo cada día.
- **Rotación por tamaño:** nuevo archivo al superar 10 MB.
- **Compresión:** los archivos rotados se comprimen en `.log.gz`.
- **Retención:** se conservan hasta 7 archivos históricos.
- **Formato de cada línea:**

```
2026-05-03 14:23:01.456 [main] INFO  com.tasku.ui.SceneManager - Error al cargar la vista Principal
```

---

## Uso en el código

### Declaración del logger

En cada clase se declara un logger estático siguiendo el patrón estándar de SLF4J:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {
    private static final Logger log = LoggerFactory.getLogger(MiClase.class);
}
```

### Niveles de log empleados

| Nivel | Método | Cuándo se usa |
|---|---|---|
| `INFO` | `log.info(...)` | Eventos informativos del flujo normal (ej. cambio de estado del tablero) |
| `ERROR` | `log.error(...)` | Errores y excepciones capturadas |

### Sustituciones realizadas

Los siguientes usos de terminal han sido reemplazados en cuatro clases del módulo `ui`:

**`SceneManager`**

```java
// Antes
System.err.println("Error al cargar la vista " + fxmlName + ": " + e.getMessage());
e.printStackTrace();

// Después
log.error("Error al cargar la vista {}", fxmlName, e);
```

**`PrincipalController`**

```java
// Antes
System.out.println("Tablero " + (estaBloqueado ? "bloqueado" : "desbloqueado") + ".");
// Después
log.info("Tablero {}.", estaBloqueado ? "bloqueado" : "desbloqueado");

// Antes
e.printStackTrace();
// Después
log.error("Error al cargar columna FXML", e);
```

**`UnirTableroController`**

```java
// Antes
ex.printStackTrace();
System.err.println("Error (Unir Tablero): " + message);

// Después
log.error("Error inesperado de conexión al unirse al tablero", ex);
log.error("Error (Unir Tablero): {}", message);
```

**`TaskuApiClient`**

```java
// Antes
System.err.println("Error al cargar tableros compartidos: " + ex.getMessage());

// Después
log.error("Error al cargar tableros compartidos", ex);
```

> La excepción se pasa como **último argumento** del método `log.error`, lo que hace que Log4j2 incluya el stack trace completo en el archivo de log sin necesidad de llamar a `printStackTrace()` explícitamente.

---

## Archivos modificados

| Archivo | Cambio |
|---|---|
| `ui/pom.xml` | Añadidas 3 dependencias de logging |
| `core/pom.xml` | Añadidas 3 dependencias + exclusión de Logback |
| `ui/src/main/resources/log4j2.xml` | Nuevo — configuración de logging para UI |
| `core/src/main/resources/log4j2.xml` | Nuevo — configuración de logging para core |
| `ui/.../SceneManager.java` | 3 sustituciones |
| `ui/.../PrincipalController.java` | 2 sustituciones |
| `ui/.../UnirTableroController.java` | 2 sustituciones |
| `ui/.../TaskuApiClient.java` | 1 sustitución |
