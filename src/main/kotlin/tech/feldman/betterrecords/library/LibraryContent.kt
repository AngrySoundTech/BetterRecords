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
import tech.feldman.betterrecords.library.LibraryContent.Companion.fromJson
import com.google.gson.GsonBuilder

/**
 * A class used to read the contents of a json string.
 *
 * Normally, this class is only instantiated by Gson in [fromJson]
 *
 */
class LibraryContent(
        /** The name of this library */
        val name: String,
        /** A list of the songs contained in this library */
        val songs: MutableList<Song>
) {

    companion object {
        private var gson = GsonBuilder().setPrettyPrinting().create()

        /**
         * Construct a [LibraryContent] object from a json string
         */
        fun fromJson(json: String): LibraryContent {
            return gson.fromJson(json, LibraryContent::class.java)
        }
    }

    /**
     * Convert this object into a json string
     */
    fun toJson(): String {
        return gson.toJson(this)
    }
}
