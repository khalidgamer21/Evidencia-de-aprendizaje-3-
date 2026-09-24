package co.edu.iudigital.radio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.delay

private val Navy = Color(0xFF072B4C)
private val Blue = Color(0xFF0057B8)
private val Aqua = Color(0xFF19A7A0)
private val Orange = Color(0xFFF7A51A)
private val Canvas = Color(0xFFF4F7FB)
private val Muted = Color(0xFF5D6B79)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { IUDigitalRadioApp() }
    }
}

@Composable
fun IUDigitalRadioApp() {
    val colors = lightColorScheme(
        primary = Blue,
        secondary = Aqua,
        tertiary = Orange,
        background = Canvas,
        surface = Color.White,
        onPrimary = Color.White,
        onBackground = Navy,
        onSurface = Navy,
    )

    MaterialTheme(colorScheme = colors) {
        Surface(modifier = Modifier.fillMaxSize(), color = Canvas) {
            RadioScreen()
        }
    }
}

@Composable
private fun RadioScreen() {
    val context = LocalContext.current
    var selectedStationId by rememberSaveable { mutableStateOf(stations.first().id) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isMuted by rememberSaveable { mutableStateOf(false) }
    var profilePhotoBytes by rememberSaveable { mutableStateOf<ByteArray?>(null) }
    var permissionMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var playerMessage by rememberSaveable { mutableStateOf("Listo para reproducir") }
    var usingLocalFallback by rememberSaveable { mutableStateOf(false) }

    val selectedStation = stations.first { it.id == selectedStationId }
    val player = remember(context.applicationContext) {
        ExoPlayer.Builder(context.applicationContext).build()
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                playerMessage = when (playbackState) {
                    Player.STATE_BUFFERING -> "Conectando con la emisora..."
                    Player.STATE_READY -> when {
                        isPlaying && usingLocalFallback -> "Modo local de respaldo activo"
                        isPlaying -> "Transmitiendo en vivo"
                        else -> "En pausa"
                    }
                    Player.STATE_ENDED -> "Transmision finalizada"
                    else -> playerMessage
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                if (playing) {
                    playerMessage = if (usingLocalFallback) {
                        "Modo local de respaldo activo"
                    } else {
                        "Transmitiendo en vivo"
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (!usingLocalFallback) {
                    usingLocalFallback = true
                    player.repeatMode = Player.REPEAT_MODE_ONE
                    player.setMediaItem(
                        MediaItem.fromUri(
                            "android.resource://${context.packageName}/${R.raw.radio_preview}",
                        ),
                    )
                    player.prepare()
                    player.playWhenReady = isPlaying
                    playerMessage = "Modo local de respaldo listo"
                } else {
                    isPlaying = false
                    playerMessage = "No fue posible iniciar el audio"
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(selectedStation.id) {
        usingLocalFallback = false
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.setMediaItem(MediaItem.fromUri(selectedStation.streamUrl))
        player.prepare()
        player.playWhenReady = isPlaying
        playerMessage = if (isPlaying) "Conectando con la emisora..." else "Emisora seleccionada"
    }

    LaunchedEffect(isPlaying, usingLocalFallback) {
        if (isPlaying) {
            playerMessage = if (usingLocalFallback) {
                "Modo local de respaldo activo"
            } else {
                "Conectando con la emisora..."
            }
            player.play()
        } else {
            player.pause()
            if (player.playbackState == Player.STATE_READY) playerMessage = "En pausa"
        }
    }

    LaunchedEffect(isPlaying, selectedStation.id, usingLocalFallback) {
        if (isPlaying && !usingLocalFallback) {
            delay(6_000)
            if (!player.isPlaying && !usingLocalFallback) {
                usingLocalFallback = true
                player.repeatMode = Player.REPEAT_MODE_ONE
                player.setMediaItem(
                    MediaItem.fromUri(
                        "android.resource://${context.packageName}/${R.raw.radio_preview}",
                    ),
                )
                player.prepare()
                player.playWhenReady = true
                playerMessage = "Modo local de respaldo activo"
            }
        }
    }

    LaunchedEffect(isMuted) {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, isMuted)
            .build()
        player.volume = if (isMuted) 0f else 1f
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            profilePhotoBytes = bitmap.toPngBytes()
            permissionMessage = "Foto de perfil actualizada"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            permissionMessage = null
            cameraLauncher.launch(null)
        } else {
            permissionMessage = "Permiso de camara denegado. Puedes habilitarlo en Ajustes."
        }
    }

    Scaffold(containerColor = Canvas) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            Header()
            Spacer(Modifier.height(14.dp))
            ProfileCard(
                photoBytes = profilePhotoBytes,
                message = permissionMessage,
                onTakePhoto = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA,
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            )
            Spacer(Modifier.height(14.dp))
            PlayerCard(
                station = selectedStation,
                isPlaying = isPlaying,
                isMuted = isMuted,
                status = playerMessage,
                onPlay = {
                    vibrate(context)
                    isPlaying = true
                },
                onPause = {
                    vibrate(context)
                    isPlaying = false
                },
                onMute = {
                    vibrate(context)
                    isMuted = !isMuted
                },
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Emisoras disponibles",
                color = Navy,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Toca una opcion para cambiar la transmision activa.",
                color = Muted,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                items(stations, key = { it.id }) { station ->
                    StationRow(
                        station = station,
                        selected = station.id == selectedStationId,
                        onSelect = {
                            selectedStationId = station.id
                            vibrate(context)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "IU DIGITAL",
                color = Blue,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
            )
            Text(
                text = "Radio",
                color = Navy,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Navy)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text("EN VIVO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileCard(
    photoBytes: ByteArray?,
    message: String?,
    onTakePhoto: () -> Unit,
) {
    val bitmap = remember(photoBytes) {
        photoBytes?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Blue, Aqua)),
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Fotografia del perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text("IU", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Mi perfil", color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = message ?: "Personaliza tu perfil con la camara del dispositivo.",
                    color = if (message?.contains("denegado") == true) Color(0xFFB42318) else Muted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
            Spacer(Modifier.size(8.dp))
            Button(
                onClick = onTakePhoto,
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text("CAMARA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PlayerCard(
    station: Station,
    isPlaying: Boolean,
    isMuted: Boolean,
    status: String,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onMute: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (isPlaying) "AHORA SUENA" else "EMISORA ACTIVA",
                color = Aqua,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.6.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = station.name,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(station.genre, color = Color(0xFFC4D2E0), fontSize = 14.sp)
            Spacer(Modifier.height(7.dp))
            Text(
                text = status,
                color = if (isPlaying) Color(0xFF7EE2C8) else Color(0xFFB7C5D2),
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ControlButton(
                    label = "PLAY",
                    selected = isPlaying,
                    modifier = Modifier.weight(1f),
                    onClick = onPlay,
                )
                ControlButton(
                    label = "PAUSA",
                    selected = !isPlaying,
                    modifier = Modifier.weight(1f),
                    onClick = onPause,
                )
                ControlButton(
                    label = if (isMuted) "SONIDO" else "MUTE",
                    selected = isMuted,
                    modifier = Modifier.weight(1f),
                    onClick = onMute,
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Aqua else Color(0xFF173F61),
            contentColor = Color.White,
        ),
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StationRow(
    station: Station,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFE2F1FF) else Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (selected) Blue else Color(0xFFE7EDF3)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (selected) "ON" else "FM",
                    color = if (selected) Color.White else Muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(station.name, color = Navy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(station.genre, color = Muted, fontSize = 12.sp)
            }
            Text(
                text = if (selected) "SELECCIONADA" else "ELEGIR",
                color = if (selected) Blue else Muted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun Bitmap.toPngBytes(): ByteArray = ByteArrayOutputStream().use { output ->
    compress(Bitmap.CompressFormat.PNG, 100, output)
    output.toByteArray()
}

@Suppress("DEPRECATION")
private fun vibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java).defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (!vibrator.hasVibrator()) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(45L, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(45L)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RadioPreview() {
    IUDigitalRadioApp()
}
