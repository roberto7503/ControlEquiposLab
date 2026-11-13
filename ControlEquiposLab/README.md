# Control de Equipos de Laboratorio

Aplicación de escritorio desarrollada con **JavaFX 21**, **Hibernate ORM 7.x** y **PostgreSQL 18** para la administración del inventario y catálogo de equipos de laboratorio universitario.

---

## 1. Requisitos de Ejecución
- **Java Development Kit (JDK):** Versión 21 o superior.
- **PostgreSQL:** Versión 15 o superior (ejecutándose en el puerto 5432).
- **IDE:** IntelliJ IDEA (o Eclipse / NetBeans con soporte Maven).

---

## 2. Configuración de la Base de Datos

1. Abre **pgAdmin** o tu terminal de PostgreSQL.
2. Crea una base de datos llamada `bd_laboratorio_orm`:
   ```sql
   CREATE DATABASE bd_laboratorio_orm;
   ```
3. Conéctate a dicha base de datos y ejecuta el script:
   - Archivo: `schema.sql` (ubicado en la raíz de este proyecto).
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

---

## 4. Pasos para Iniciar la Aplicación

1. Abre el proyecto en **IntelliJ IDEA**.
2. Espera a que Maven descargue las dependencias automáticamente.
3. Ejecuta la aplicación desde:
   - `src/main/java/org/example/controlequipos/Launcher.java` (o `EquipoApplication.java`) dándole clic derecho -> **Run**.

---

## 5. Arquitectura del Proyecto

El proyecto está organizado en una arquitectura por capas limpia:

```text
src/main/java/org/example/controlequipos/
├── model/           # Entidades JPA (TipoEquipo y Equipo con relación @ManyToOne)
├── database/        # Gestión de EntityManagerFactory (JPAUtil)
├── dao/             # Acceso a datos con transacciones completas (TipoEquipoDAO, EquipoDAO)
├── controller/      # Controlador de eventos y validaciones JavaFX (EquipoController)
├── EquipoApplication.java  # Clase principal JavaFX
└── Launcher.java    # Punto de entrada de la aplicación
```

---

## 6. Operaciones y Consultas Implementadas

- **Crear:** Persiste un nuevo equipo asociado a un tipo existente mediante `em.persist()` y `tx.commit()`.
- **Leer:** Lista los equipos en el `TableView` mediante consultas JPQL usando `JOIN FETCH` para evitar problemas de carga perezosa (`LazyInitializationException`).
- **Actualizar:** Modifica equipos existentes y sincroniza los cambios en PostgreSQL mediante `tx.commit()`.
- **Eliminar:** Elimina equipos mediante `em.remove()` tras solicitar confirmación al usuario con un diálogo `Alert`.
- **Búsqueda y Filtro Combinado:** Permite buscar por fragmento de nombre (`LIKE`) y filtrar por tipo de equipo usando JPQL parametrizado con `setParameter()`.
- **Transacciones y Rollback:** Todo el bloque CRUD maneja `tx.begin()`, `tx.commit()` y en caso de error o violación de restricciones se ejecuta `tx.rollback()` garantizando la integridad de los datos.
