# Registro de clientes

Aplicación Android del taller. Usa un formulario XML, Kotlin y una base de datos SQLite local llamada `ClientesDB`. Requiere Android 6.0 (API 23) o superior para ejecutarse.

## Probar en un celular sin Android Studio

Descomprime el ZIP, abre `RegistroClientes-debug.apk` en el celular y pulsa **Instalar**. Si Android te pide permitir la instalación desde la app de archivos que está abriendo el APK, sigue el aviso del sistema y vuelve al instalador. El APK es una compilación de depuración para probar el taller.

## Abrir el proyecto

1. Descomprime el ZIP y abre la carpeta `RegistroClientes` desde **File > Open** en Android Studio.
2. Usa un JDK 17 y asegúrate de tener instalados **Android SDK Platform 35** y **Android SDK Build-Tools 34.0.0** en SDK Manager. El proyecto incluye el Gradle Wrapper.
3. Espera la sincronización. Selecciona un teléfono o emulador y pulsa **Run**.

También puedes compilar desde Fedora con un JDK 17, el SDK anterior y la variable `ANDROID_HOME` apuntando al SDK. Desde la carpeta del proyecto, ejecuta:

```bash
./gradlew assembleDebug
```

Gradle deja el APK en `app/build/outputs/apk/debug/app-debug.apk`.

## Archivos principales

| Archivo | Qué hace |
| --- | --- |
| `app/src/main/res/layout/activity_main.xml` | Define los campos, los botones y el área de resultados. |
| `app/src/main/java/com/example/registroclientes/MainActivity.kt` | Lee y valida los datos, atiende los botones y muestra el resultado. |
| `app/src/main/java/com/example/registroclientes/DatabaseHelper.kt` | Crea la tabla `clientes` y ejecuta las operaciones de SQLite. |

La identificación es la clave primaria y se ingresa en el formulario. El teléfono se guarda como texto para poder conservar el signo + y los ceros iniciales. Al consultar por identificación, la app llena los campos para que puedas editarlos.

## Prueba del taller

| Paso | Acción | Resultado esperado |
| --- | --- | --- |
| 1 | Pulsa **Consultar** con la identificación vacía al abrir la app por primera vez. | Aparece «No hay clientes registrados». |
| 2 | Guarda el ID `1001`, el nombre `Carlos Pérez`, el correo `carlos@example.com` y el teléfono `3001234567`. | Se confirma el registro y se limpian los campos. |
| 3 | Cierra y vuelve a abrir la app. Escribe `1001` y pulsa **Consultar**. | Aparecen los datos guardados en el formulario y en el resultado. |
| 4 | Pulsa **Guardar** con ese formulario lleno. | Se indica que la identificación ya existe. |
| 5 | Cámbiale el teléfono a `3015550100` y pulsa **Actualizar**. Vuelve a consultar el ID. | La consulta muestra el teléfono nuevo. |
| 6 | Pulsa **Limpiar** y luego **Consultar**. | Aparece la lista con el cliente guardado. |
| 7 | Escribe `1001`, pulsa **Eliminar** y vuelve a consultarlo. | Se confirma la eliminación y la nueva consulta indica que el cliente no se encontró. |

## Cómo explicar la conexión

Cuando el usuario pulsa **Guardar**, `MainActivity` lee y valida el formulario XML. Luego le pasa un objeto `Cliente` a `DatabaseHelper`, que prepara los datos con `ContentValues` y los inserta en SQLite. Para consultar, actualizar o eliminar por identificación, el código pasa el valor por separado en una condición `id = ?`. Por último, la actividad muestra el resultado de la operación.
