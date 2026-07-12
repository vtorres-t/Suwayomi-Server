package suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MUSeriesResponse(
    @SerialName("title") val title: String? = null,
    @SerialName("related_series") val relatedSeries: List<MURelatedSeries>? = null,
    @SerialName("category_recommendations") val recommendationSeries: List<MURecommendationSeries>? = null,
)

@Serializable
data class MURelatedSeries(
    @SerialName("related_series_id") val relatedSeriesId: Long,
    @SerialName("relation_type") val relationType: String? = null,
    @SerialName("related_series_name") val relatedSeriesName: String? = null,
    @SerialName("related_series_url") val relatedSeriesUrl: String? = null,
)

@Serializable
data class MURecommendationSeries(
    @SerialName("series_id") val seriesId: Long,
    @SerialName("series_name") val seriesName: String? = null,
    @SerialName("series_url") val seriesUrl: String? = null,
    @SerialName("series_image") val seriesImage: MURecommendationSeriesImage? = null,
)

@Serializable
data class MURecommendationSeriesImage(
    @SerialName("url") val url: MURecommendationSeriesImageUrl? = null,
)

@Serializable
data class MURecommendationSeriesImageUrl(
    @SerialName("thumb") val thumb: String? = null,
)
