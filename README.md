# Control de Equipos de Laboratorio

Aplicación de escritorio desarrollada con **JavaFX 21**, **Hibernate ORM 7.x** y **PostgreSQL 18** para la administración del inventario y catálogo de equipos de un laboratorio de informática universitario.

---

## 1. Requisitos de Ejecución
- **Java Development Kit (JDK):** Versión 21 o superior.
- **PostgreSQL:** Versión 15 o superior (ejecutándose en el puerto 5432).
- **IDE:** IntelliJ IDEA (o cualquier IDE con soporte Maven).

---

## 2. Configuración de la Base de Datos

1. Abre **pgAdmin** o tu terminal de PostgreSQL.
2. Crea una base de datos llamada `bd_laboratorio_orm`:
   ```sql
   CREATE DATABASE bd_laboratorio_orm;
   ```
3. Conéctate a dicha base de datos y ejecuta el script:
   - Archivo: `schema.sql` (ubicado en la raíz de este repositorio).
4. El script creará el esquema `inventario`, las tablas `tipo_equipo` y `equipo` con sus respectivas restricciones (`PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK`), e insertará datos iniciales de prueba.

---

## 3. Configuración Local del Proyecto

La conexión a la base de datos se encuentra centralizada en:
- `src/main/resources/META-INF/persistence.xml` (Unidad de persistencia: `laboratorioPU`).
- `src/main/java/org/example/controlequipos/database/JPAUtil.java`.

Para ejecutar la aplicación con tus credenciales locales de PostgreSQL, puedes configurar la contraseña de dos formas:
1. **Mediante variable de entorno (Recomendado):**  
   Define la variable de entorno del sistema con la contraseña de tu base de datos:
   ```bash
   BD_PASSWORD=tu_contraseña
   ```
2. **Directamente en el código:**  
   Coloca tu contraseña local en la clase `JPAUtil.java` (dentro del método `iniciar()`), en la variable destinada a la conexión:
   ```java
   String password = "tu_contraseña";
   ```

## 4. Pasos para Iniciar la Aplicación

1. Clona o abre el proyecto en **IntelliJ IDEA**.
2. Espera a que Maven descargue las dependencias necesarias.
3. Ejecuta la aplicación desde:
   - `src/main/java/org/example/controlequipos/Launcher.java` (o `EquipoApplication.java`) haciendo clic derecho -> **Run**.

---

## 5. Arquitectura del Proyecto

El proyecto implementa una arquitectura limpia separada en capas de responsabilidad:

```text
src/main/java/org/example/controlequipos/
├── model/           # Entidades JPA (TipoEquipo y Equipo con relación @ManyToOne)
├── database/        # Gestión del ciclo de vida de EntityManagerFactory (JPAUtil)
├── dao/             # Acceso a datos y transacciones con JPA (TipoEquipoDAO, EquipoDAO)
├── controller/      # Controlador de eventos, validaciones y alertas JavaFX (EquipoController)
├── EquipoApplication.java  # Configuración y arranque de la ventana JavaFX
└── Launcher.java    # Punto de entrada de la aplicación
```

---

## 6. Operaciones y Consultas Implementadas

- **Crear:** Persiste un nuevo equipo vinculado con un tipo existente mediante `em.persist()` y `tx.commit()`.
- **Leer:** Lista los equipos en el `TableView` mediante consultas JPQL usando `JOIN FETCH` para prevenir excepciones por carga perezosa (`LazyInitializationException`).
- **Actualizar:** Modifica atributos del equipo seleccionado y sincroniza los cambios en la base de datos mediante transacciones gestionadas y `commit()`.
- **Eliminar:** Elimina registros de la base de datos mediante `em.remove()` tras solicitar confirmación al usuario con un diálogo `Alert.CONFIRMATION`.
- **Búsqueda y Filtro Combinado:** Permite buscar por fragmento de nombre (`LIKE`) y filtrar por tipo de equipo utilizando JPQL parametrizado con `setParameter()`.
- **Transacciones y Rollback:** Las operaciones de escritura implementan bloques `try-catch` que ejecutan `tx.rollback()` si ocurre cualquier error o violación de restricciones de integridad, garantizando la atomicidad de los datos.
