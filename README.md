# IU Digital Radio - Evidencia de aprendizaje 3

Aplicacion Android nativa desarrollada completamente con Kotlin y Jetpack Compose. Implementa perfil con captura de camara, permisos en tiempo de ejecucion, reproductor de streaming con Media3 ExoPlayer, controles Play/Pausa/Mute con vibracion y catalogo dinamico de emisoras.

## Modalidad y equipo

Modalidad: **Software House - microcelula de tres integrantes**. La guia propone celulas de 4 a 6 personas; por la conformacion real del equipo, los modulos se distribuyen entre tres integrantes y todos apoyan integracion y pruebas.

| Integrante | Roles principales |
|---|---|
| Marlon Monterrosa Muñoz | Tech Lead, estado e integracion |
| Yimy Antonio Mosquera Asprilla | UI/UX y maquetacion Compose |
| Rafael de Jesús González Ayala | Hardware/permisos, audio y QA |

Los tres integrantes apoyan de forma transversal la integracion y las pruebas.

## Requisitos implementados

- RF-01: interfaz declarativa con `Column`, `Row`, `Card`, `LazyColumn` y `Modifier`.
- RF-02: captura con `ActivityResultContracts.TakePicturePreview` y foto circular.
- RF-03: permisos `CAMERA`, `VIBRATE` e `INTERNET`; camara solicitada en ejecucion.
- RF-04: `isPlaying`, `isMuted`, `selectedStationId` y foto preservados con `rememberSaveable`.
- RF-05: vibracion corta en Play, Pausa, Mute y seleccion de emisora.
- RF-06: lista dinamica que actualiza la emisora activa en tiempo real.
- RF-07: streaming real mediante Media3 ExoPlayer.

Si la red del emulador no esta disponible, ExoPlayer activa automaticamente una pista local de respaldo para que la reproduccion fisica y los controles sigan siendo demostrables.

## Abrir y ejecutar

1. Abrir esta carpeta en Android Studio.
2. Esperar la sincronizacion de Gradle.
3. Conectar un telefono o iniciar un emulador Android 7.0 o superior.
4. Ejecutar el modulo `app`.
5. Pulsar **CAMARA**, aceptar el permiso y tomar la foto.
6. Probar **PLAY**, **PAUSA**, **MUTE** y cambiar de emisora.

## Compilar el APK

En Android Studio: **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.

Resultado esperado:

`app/build/outputs/apk/debug/app-debug.apk`

## Enlaces que debe completar el equipo

- Repositorio publico: https://github.com/khalidgamer21/Evidencia-de-aprendizaje-3-
- Video de demostracion (Google Drive): `[PEGAR URL DEL VIDEO]`

## Privacidad

La fotografia de perfil se conserva solo en el estado local de la pantalla. No se envia a servidores ni se guarda como archivo permanente.
