package com.tecsup.lab12_maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.Polyline
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
@Composable
fun MapScreen() {
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val yura = LatLng(-16.2520984, -71.6836503)            // Yura, Arequipa

    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )
    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )
    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    val locations = listOf(
        LatLng(-16.433415, -71.5442652),  // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994)  // Zamacola
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    LaunchedEffect(Unit) {
        delay(1500)
        val bounds = LatLngBounds.builder().apply {
            (plazaDeArmasPolygon + parqueLambramaniPolygon + mallAventuraPolygon)
                .forEach { include(it) }
        }.build()
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
            durationMs = 3000
        )
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    val tipos = listOf(
        "Normal" to MapType.NORMAL,
        "Satélite" to MapType.SATELLITE,
        "Terreno" to MapType.TERRAIN,
        "Híbrido" to MapType.HYBRID
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapType)
        ) {
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                title = "Arequipa, Perú"
            )

            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Ubicación",
                    snippet = "Punto de interés"
                )
            }

            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )

            // Ejemplo 1: ruta continua Plaza de Armas -> Parque Lambramani -> Mall Aventura
            Polyline(
                points = listOf(
                    plazaDeArmasPolygon.first(),
                    parqueLambramaniPolygon.first(),
                    mallAventuraPolygon.first()
                ),
                color = Color.Magenta,
                width = 10f
            )

            // Ejemplo 2: ruta punteada entre los puntos de interés
            Polyline(
                points = listOf(ArequipaLocation) + locations,
                color = Color.Green,
                width = 8f,
                pattern = listOf(Dash(30f), Gap(20f))
            )
        }

        // Botones del tipo de mapa: van FUERA del GoogleMap, encima del mapa
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tipos.forEach { (nombre, tipo) ->
                FilterChip(
                    selected = mapType == tipo,
                    onClick = { mapType = tipo },
                    label = { Text(nombre) }
                )
            }
        }
    }
}