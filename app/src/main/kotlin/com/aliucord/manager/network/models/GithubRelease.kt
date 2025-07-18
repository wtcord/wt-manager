package com.aliucord.manager.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(
    @SerialName("created_at")
    val createdAt: String,
    val assets: List<GithubReleaseAssets>,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("name")
    val name: String,
) {
    @Serializable
    data class GithubReleaseAssets(
        val name: String,
        @SerialName("browser_download_url")
        val browserDownloadUrl: String,
    )
}
