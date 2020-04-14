/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tech.feldman.betterrecords.library

import tech.feldman.betterrecords.api.library.Song
import java.io.File
import java.net.URL

/**
 * Abstract class representing a library File
 */
sealed class Library {

    /**
     * The [LibraryContent], to be set after reading from whatever source
     */
    protected abstract val libraryContent: LibraryContent

    val name
        get() = libraryContent.name

    /**
     * The list of songs in this library
     */
    val songs: MutableList<Song>
        get() = libraryContent.songs

    override fun toString(): String {
        return libraryContent.toJson()
    }

    companion object {
        fun fromString(string: String): Library {
            return StringLibrary(string)
        }
    }
}

/**
 * Class representing a library that is stored as a local file
 */
class LocalLibrary(val file: File) : Library() {

    override val libraryContent = LibraryContent.fromJson(file.readText())

    /**
     * Save the library back to the file, in order to save changes
     */
    fun save() {
        file.writeText(libraryContent.toJson())
    }
}

/**
 * Class representing a library that is a remote url
 */
class RemoteLibrary(url: URL) : Library() {

    override val libraryContent = LibraryContent.fromJson(url.readText())

}

/**
 * Class representing a library that is loaded from a string (e.g. sent from the server)
 */
class StringLibrary(string: String) : Library() {

    override val libraryContent = LibraryContent.fromJson(string)
}
