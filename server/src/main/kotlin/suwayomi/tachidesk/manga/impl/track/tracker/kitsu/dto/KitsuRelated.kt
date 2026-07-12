package suwayomi.tachidesk.manga.impl.track.tracker.kitsu.dto

import kotlinx.serialization.Serializable

@Serializable
data class KitsuRelatedResult(
    val data: List<KitsuRelationshipData> = emptyList(),
    val included: List<KitsuIncludedMedia> = emptyList(),
)

@Serializable
data class KitsuRelationshipData(
    val attributes: KitsuRelationshipAttributes,
    val relationships: KitsuMediaRelationships,
)

@Serializable
data class KitsuRelationshipAttributes(
    val role: String, // prequel, sequel, adaptation, etc.
)

@Serializable
data class KitsuMediaRelationships(
    val destination: KitsuMediaRelationshipData,
)

@Serializable
data class KitsuMediaRelationshipData(
    val data: KitsuMediaDataPointer,
)

@Serializable
data class KitsuMediaDataPointer(
    val id: String,
    val type: String,
)

@Serializable
data class KitsuIncludedMedia(
    val id: String,
    val type: String,
    val attributes: KitsuMediaAttributes,
    val links: KitsuLinks,
)

@Serializable
data class KitsuLinks(
    val self: String? = null,
)

@Serializable
data class KitsuMediaAttributes(
    val canonicalTitle: String,
    val posterImage: KitsuPosterImage? = null,
)

@Serializable
data class KitsuPosterImage(
    val original: String? = null,
    val large: String? = null,
)

@Serializable
data class KitsuRecommendationsResult(
    val data: List<KitsuRecommendationData> = emptyList(),
    val included: List<KitsuIncludedMedia> = emptyList(),
)

@Serializable
data class KitsuRecommendationData(
    val relationships: KitsuRecommendationRelationships,
)

@Serializable
data class KitsuRecommendationRelationships(
    val item: KitsuMediaRelationshipData,
)
