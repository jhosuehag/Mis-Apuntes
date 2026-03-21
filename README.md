# Cursos Apuntes

Aplicación Android para gestionar notas de cursos y estudios personales. Organiza tus apuntes por secciones/temas y mantén un registro de tus conocimientos con notas teóricas y algoritmos.

## Características

- **Gestión de Secciones**: Crea y organiza secciones para diferentes cursos o temas
- **Notas estructuradas**: Cada nota incluye título, descripción, tipo (Teoría/Algoritmo) y código de ejemplo opcional
- **Búsqueda**: Busca tanto en secciones como en notas individuales
- **Edición y Eliminación**: Mantén presionado (long press) sobre una sección o nota para editar o eliminar
- **Confirmación de eliminación**: Modal de confirmación antes de eliminar secciones (con advertencia de notas associadas) o notas
- **Persistencia local**: Todos los cambios se guardan automáticamente en la base de datos local
- **Interfaz moderna**: Diseño limpio con Jetpack Compose y Material Design 3
- **Modo Oscuro**: Cambia entre modo claro y oscuro desde la pantalla de ajustes
- **Ajustes**: Accede a través del icono de engranaje en la pantalla principal
- **Backup y Restauración**: Exporta tus notas a la carpeta Downloads y restaura desde un archivo de backup

## Capturas de Pantalla

La aplicación cuenta con cuatro pantallas principales:

1. **Pantalla Principal**: Muestra todas las secciones con el número de notas en cada una, más botón de ajustes
2. **Sección**: Lista de notas dentro de una sección específica
3. **Detalle de Nota**: Vista completa de una nota con toda la información
4. **Ajustes**: Pantalla con opciones de modo oscuro, guardar backup y restaurar datos

## Tecnologías

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - Framework de UI moderno
- **Hilt** - Inyección de dependencias
- **Navigation Compose** - Navegación entre pantallas
- **Room** - Base de datos local (configurado)
- **Coroutines & Flow** - Programación reactiva
- **Material Design 3** - Sistema de diseño

## Estructura del Proyecto

```
app/src/main/java/com/jhosue/cursosapuntes/
├── data/
│   ├── local/           # DAOs y configuración de base de datos
│   ├── model/           # Modelos de datos (Section, Note)
│   └── repository/      # Repositorio de notas
├── di/                 # Módulos de inyección de dependencias
├── navigation/         # Configuración de navegación
├── ui/
│   ├── components/     # Componentes reutilizables
│   ├── screens/        # Pantallas principales
│   └── theme/          # Temas y estilos
├── viewmodel/          # ViewModels para MVVM
├── MainActivity.kt     # Activity principal
└── MyNotesApp.kt      # Aplicación Hilt
```

## Requisitos

- Android SDK 30 (Android 11) o superior
- JDK 11
- Android Studio (versión reciente)

## Instalación

1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Ejecuta la aplicación en un emulador o dispositivo físico

## Uso

1. **Crear Sección**: Toca el botón (+) en la pantalla principal
2. **Ver Notas**: Toca una sección para ver sus notas
3. **Crear Nota**: Toca el botón (+) dentro de una sección
4. **Ver Detalle**: Toca una nota para ver su contenido completo
5. **Buscar**: Usa la barra de búsqueda en cualquier pantalla
6. **Editar/Eliminar**: Mantén presionado (long press) sobre una sección o nota para ver el menú contextual
7. **Ajustes**: Toca el icono de engranaje para acceder a la pantalla de ajustes
8. **Modo Oscuro**: Activa/desactiva el toggle en la pantalla de ajustes
9. **Guardar Backup**: Toca "Save Database" para exportar tus notas
10. **Restaurar Datos**: Toca "Restore Database" para importar un backup

## Datos de Ejemplo

La aplicación incluye datos de ejemplo precargados:

- **Data Structures** (5 notas): Arrays, Linked Lists, Binary Search Trees, Hash Tables
- **Web Development** (3 notas)
- **Computer Networks** (2 notas)

## Licencia

Este proyecto es para uso personal.
