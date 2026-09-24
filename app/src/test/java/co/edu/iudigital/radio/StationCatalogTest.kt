package co.edu.iudigital.radio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StationCatalogTest {
    @Test
    fun stationIdsAreUnique() {
        assertEquals(stations.size, stations.map { it.id }.distinct().size)
    }

    @Test
    fun allStreamsUseHttps() {
        assertTrue(stations.all { it.streamUrl.startsWith("https://") })
    }
}
