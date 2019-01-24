package tech.feldman.betterrecords.api.library

import tech.feldman.betterrecords.library.Libraries

fun urlExistsInAnyLibrary(url: String) =
        Libraries.libraries
                .flatMap { it.songs }
                .any { it.url == url }
