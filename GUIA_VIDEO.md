# Guion de video de demostracion (2 a 4 minutos)

Duracion sugerida: 3 minutos.

## 0:00-0:20 - Presentacion

"Somos Marlon Monterrosa Muñoz, Yimy Antonio Mosquera Asprilla y Rafael de Jesús González Ayala. Presentamos IU Digital Radio, desarrollada en Kotlin con Jetpack Compose bajo la modalidad Software House."

Mostrar la app cerrada o la pantalla de inicio del dispositivo.

## 0:20-0:45 - Inicio y permisos

Abrir la app desde cero. Pulsar **CAMARA**. Mostrar la solicitud de permiso y elegir **Permitir mientras la app esta en uso**.

## 0:45-1:15 - Camara y perfil

Tomar una fotografia y confirmar que reemplaza el circulo del perfil. Mencionar que se utiliza `ActivityResultContracts.TakePicturePreview` y que el estado se conserva con `rememberSaveable`.

## 1:15-2:10 - Audio y vibracion

Pulsar **PLAY** y esperar el estado "Transmitiendo en vivo". Pulsar **MUTE**, luego **SONIDO**, y finalmente **PAUSA**. Mencionar que cada control produce una vibracion breve y que el streaming se reproduce con Media3 ExoPlayer.

## 2:10-2:40 - Lista dinamica

Seleccionar al menos dos emisoras en la lista. Mostrar que cambian el titulo de la tarjeta principal y el indicador **SELECCIONADA**.

## 2:40-3:00 - Cierre

"La solucion cumple los siete requerimientos funcionales: Compose, camara, permisos, estado reactivo, vibracion, lista dinamica y audio. El codigo y el APK se encuentran en el repositorio enlazado en el informe."

Subir el video a Google Drive, configurar acceso para cualquier persona con el enlace y pegar la URL en el informe tecnico.
