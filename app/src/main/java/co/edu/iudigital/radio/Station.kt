package co.edu.iudigital.radio

data class Station(
    val id: String,
    val name: String,
    val genre: String,
    val streamUrl: String,
)

val stations = listOf(
    Station(
        id = "groove-salad",
        name = "Groove Salad",
        genre = "Ambient / downtempo",
        streamUrl = "https://ice2.somafm.com/groovesalad-128-mp3",
    ),
    Station(
        id = "secret-agent",
        name = "Secret Agent",
        genre = "Cinematic / lounge",
        streamUrl = "https://ice2.somafm.com/secretagent-128-mp3",
    ),
    Station(
        id = "drone-zone",
        name = "Drone Zone",
        genre = "Atmospheric ambient",
        streamUrl = "https://ice2.somafm.com/dronezone-128-mp3",
    ),
    Station(
        id = "sonic-universe",
        name = "Sonic Universe",
        genre = "Jazz / avant-garde",
        streamUrl = "https://ice2.somafm.com/sonicuniverse-128-mp3",
    ),
)
