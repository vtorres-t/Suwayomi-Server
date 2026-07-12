package suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MUSeriesResponse(
    @SerialName("title") val title: String? = null,
    @SerialName("related_series") val relatedSeries: List<MURelatedSeries>? = null,
)

@Serializable
data class MURelatedSeries(
    @SerialName("related_series_id") val relatedSeriesId: Long,
    @SerialName("relation_type") val relationType: String? = null,
    @SerialName("title") val title: String? = null,
)

@Serializable
data class KitsuMediaConnection(
    val id: String,
    val type: String, // "manga" o "anime"
    val role: String, // "adaptation", "sequel", "prequel", "recommendation", etc.
    val titles: Map<String, String>?, // Mapa de idiomas y títulos (ej: "en" to "Title")
    val posterImage: String?, // URL de la portada del tomo/anime (si está disponible)
)
