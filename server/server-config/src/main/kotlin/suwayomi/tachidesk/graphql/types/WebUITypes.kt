package suwayomi.tachidesk.graphql.types

import suwayomi.tachidesk.server.serverConfig

enum class WebUIInterface {
    BROWSER,
    ELECTRON,
}

enum class WebUIChannel {
    BUNDLED, // the default webUI version bundled with the server release
    STABLE,
    PREVIEW,
    ;

    companion object {
        fun from(channel: String): WebUIChannel = entries.find { it.name.lowercase() == channel.lowercase() } ?: STABLE
    }
}

enum class WebUIFlavor(
    val uiName: String,
    val repoUrl: String,
    val versionMappingUrl: String,
    val latestReleaseInfoUrl: String,
    val baseFileName: String,
) {
    WEBUI(
        "WebUI",
        "https://github.com/vtorres-t/Suwayomi-WebUI",
        "https://raw.githubusercontent.com/vtorres-t/Suwayomi-WebUI/master/versionToServerVersionMapping.json",
        "https://api.github.com/repos/vtorres-t/Suwayomi-WebUI/releases/latest",
        "Suwayomi-WebUI",
    ),

    VUI(
        "VUI",
        "https://github.com/vtorres-t/Suwayomi-VUI",
        "https://raw.githubusercontent.com/vtorres-t/Suwayomi-VUI/main/versionToServerVersionMapping.json",
        "https://api.github.com/repos/vtorres-t/Suwayomi-VUI/releases/latest",
        "Suwayomi-VUI-Web",
    ),

    CUSTOM(
        "Custom",
        "repoURL",
        "versionMappingUrl",
        "latestReleaseInfoURL",
        "baseFileName",
    ),
    ;

    companion object {
        val default: WebUIFlavor = WEBUI

        fun from(value: String): WebUIFlavor = entries.find { it.uiName == value } ?: default

        val current: WebUIFlavor
            get() = serverConfig.webUIFlavor.value
    }
}
