# Clima Móvil

App Android (Kotlin) que consulta el clima en tiempo real con la API de OpenWeatherMap y guarda las ubicaciones en una base de datos SQLite local.

Actividad: *Video. Conectando y almacenando en Android* — Desarrollo de Software en Plataformas Móviles (UNIMINUTO).

## Qué hace
- Agrega ciudades y consulta su clima actual (temperatura, humedad, viento, descripción).
- Guarda cada ubicación y su último clima en SQLite (`clima.db`, tabla `ubicaciones`).
- Elimina ubicaciones con el ícono de papelera.
- Botón «Actualizar» para volver a consultar todas las ciudades.
- Funciona sin internet: la lista se lee siempre de SQLite.
- Manejo de errores: ciudad no encontrada, clave inválida, sin conexión, límite de consultas.

## Tecnología
Kotlin · vistas XML con ViewBinding · RecyclerView · Retrofit + Gson · SQLiteOpenHelper · corrutinas.

## Cómo ejecutarla
1. Abre la carpeta en **Android Studio** (Koala o más reciente) y espera la sincronización de Gradle.
2. Crea una cuenta gratuita en https://openweathermap.org y copia tu API key (puede tardar un rato en activarse).
3. Copia `local.properties.example` como `local.properties` y pega tu clave:
   ```
   OPENWEATHER_API_KEY=tu_clave
   ```
   Ese archivo no se sube a GitHub.
4. Ejecuta en un emulador o en un celular (Android 8.0 / API 26 o superior).

## Estructura
```
app/src/main/java/com/danielorzt/climamovil/
├── MainActivity.kt          pantalla principal, diálogo de agregar, eliminar y actualizar
├── UbicacionAdapter.kt      RecyclerView con tarjetas de clima
├── api/                     Retrofit: WeatherApi, RetrofitClient, WeatherResponse
└── data/                    DbHelper (SQLite), Ubicacion, WeatherRepository
```

## Nota
Autor: Daniel Ortiz. La clave de la API se lee desde `local.properties` y llega al código por `BuildConfig`; nunca se escribe en el repositorio.
