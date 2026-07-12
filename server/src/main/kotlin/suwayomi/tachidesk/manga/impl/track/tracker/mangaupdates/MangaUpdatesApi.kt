package suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates

import eu.kanade.tachiyomi.network.DELETE
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.network.PUT
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.network.parseAs
import eu.kanade.tachiyomi.util.lang.withIOContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import suwayomi.tachidesk.manga.impl.track.tracker.kitsu.KitsuApi
import suwayomi.tachidesk.manga.impl.track.tracker.kitsu.dto.KitsuRecommendationsResult
import suwayomi.tachidesk.manga.impl.track.tracker.kitsu.dto.KitsuRelatedResult
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.MangaUpdates.Companion.READING_LIST
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.MangaUpdates.Companion.WISH_LIST
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MUContext
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MUListItem
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MULoginResponse
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MURating
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MURecord
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MUSearchResult
import suwayomi.tachidesk.manga.impl.track.tracker.mangaupdates.dto.MUSeriesResponse
import suwayomi.tachidesk.manga.impl.track.tracker.model.Track
import suwayomi.tachidesk.manga.impl.track.tracker.model.TrackRelated
import suwayomi.tachidesk.manga.impl.track.tracker.model.TrackRelatedResult
import uy.kohesive.injekt.injectLazy
import kotlin.collections.associateBy
import kotlin.collections.mapNotNull
import kotlin.collections.orEmpty
import kotlin.collections.plus

class MangaUpdatesApi(
    interceptor: MangaUpdatesInterceptor,
    private val client: OkHttpClient,
) {
    private val json: Json by injectLazy()

    private val authClient by lazy {
        client
            .newBuilder()
            .addInterceptor(interceptor)
            .build()
    }

    suspend fun getSeriesListItem(track: Track): Pair<MUListItem, MURating?> {
        val listItem =
            with(json) {
                authClient
                    .newCall(GET("$BASE_URL/v1/lists/series/${track.remote_id}"))
                    .awaitSuccess()
                    .parseAs<MUListItem>()
            }

        val rating = getSeriesRating(track)

        return listItem to rating
    }

    suspend fun addSeriesToList(
        track: Track,
        hasReadChapters: Boolean,
    ) {
        val status = if (hasReadChapters) READING_LIST else WISH_LIST
        val body =
            buildJsonArray {
                addJsonObject {
                    putJsonObject("series") {
                        put("id", track.remote_id)
                    }
                    put("list_id", status)
                }
            }
        authClient
            .newCall(
                POST(
                    url = "$BASE_URL/v1/lists/series",
                    body = body.toString().toRequestBody(CONTENT_TYPE),
                ),
            ).awaitSuccess()
            .let {
                if (it.code == 200) {
                    track.status = status
                    track.last_chapter_read = 1.0
                }
            }
    }

    suspend fun updateSeriesListItem(track: Track) {
        val body =
            buildJsonArray {
                addJsonObject {
                    putJsonObject("series") {
                        put("id", track.remote_id)
                    }
                    put("list_id", track.status)
                    putJsonObject("status") {
                        put("chapter", track.last_chapter_read.toInt())
                    }
                }
            }
        authClient
            .newCall(
                POST(
                    url = "$BASE_URL/v1/lists/series/update",
                    body = body.toString().toRequestBody(CONTENT_TYPE),
                ),
            ).awaitSuccess()

        updateSeriesRating(track)
    }

    suspend fun deleteSeriesFromList(track: Track) {
        val body =
            buildJsonArray {
                add(track.remote_id)
            }
        authClient
            .newCall(
                POST(
                    url = "$BASE_URL/v1/lists/series/delete",
                    body = body.toString().toRequestBody(CONTENT_TYPE),
                ),
            ).awaitSuccess()
    }

    private suspend fun getSeriesRating(track: Track): MURating? =
        try {
            with(json) {
                authClient
                    .newCall(GET("$BASE_URL/v1/series/${track.remote_id}/rating"))
                    .awaitSuccess()
                    .parseAs<MURating>()
            }
        } catch (_: Exception) {
            null
        }

    private suspend fun updateSeriesRating(track: Track) {
        if (track.score < 0.0) return
        if (track.score != 0.0) {
            val body =
                buildJsonObject {
                    put("rating", track.score)
                }
            authClient
                .newCall(
                    PUT(
                        url = "$BASE_URL/v1/series/${track.remote_id}/rating",
                        body = body.toString().toRequestBody(CONTENT_TYPE),
                    ),
                ).awaitSuccess()
        } else {
            authClient
                .newCall(
                    DELETE(url = "$BASE_URL/v1/series/${track.remote_id}/rating"),
                ).awaitSuccess()
        }
    }

    suspend fun search(query: String): List<MURecord> {
        val body =
            buildJsonObject {
                put("search", query)
                put(
                    "filter_types",
                    buildJsonArray {
                        add("drama cd")
                        add("novel")
                    },
                )
            }

        return with(json) {
            client
                .newCall(
                    POST(
                        url = "$BASE_URL/v1/series/search",
                        body = body.toString().toRequestBody(CONTENT_TYPE),
                    ),
                ).awaitSuccess()
                .parseAs<MUSearchResult>()
                .results
                .map { it.record }
        }
    }

    suspend fun getMangaRelated(remoteId: Long): TrackRelatedResult =
        withIOContext {
            val seriesResponse =
                runCatching {
                    with(json) {
                        client
                            .newCall(GET("$BASE_URL/v1/series/$remoteId"))
                            .awaitSuccess()
                            .parseAs<MUSeriesResponse>()
                    }
                }.getOrNull()

            if (seriesResponse == null) return@withIOContext TrackRelatedResult()

            val relationsList =
                seriesResponse.relatedSeries
                    ?.map { related ->
                        TrackRelated(
                            remoteId = related.relatedSeriesId,
                            title = related.title ?: "Related Manga ID: ${related.relatedSeriesId}",
                            coverUrl = "",
                            trackingUrl = "https://mangaupdates.com{related.relatedSeriesId}",
                            relationType = related.relationType ?: "Other",
                        )
                    }.orEmpty()

            val recommendationsList = emptyList<TrackRelated>()

            TrackRelatedResult(
                relations = relationsList,
                recommendations = recommendationsList,
            )
        }

    suspend fun authenticate(
        username: String,
        password: String,
    ): MUContext? {
        val body =
            buildJsonObject {
                put("username", username)
                put("password", password)
            }
        return with(json) {
            client
                .newCall(
                    PUT(
                        url = "$BASE_URL/v1/account/login",
                        body = body.toString().toRequestBody(CONTENT_TYPE),
                    ),
                ).awaitSuccess()
                .parseAs<MULoginResponse>()
                .context
        }
    }

    companion object {
        private const val BASE_URL = "https://api.mangaupdates.com"

        private val CONTENT_TYPE = "application/vnd.api+json".toMediaType()
    }
}
