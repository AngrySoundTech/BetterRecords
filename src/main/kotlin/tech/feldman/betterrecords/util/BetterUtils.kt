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
package tech.feldman.betterrecords.util

import net.minecraftforge.fml.common.Mod
import org.apache.commons.lang3.text.WordUtils

object BetterUtils {

    fun getWordWrappedString(maxWidth: Int, string: String) =
            WordUtils.wrap(string, maxWidth, "\n", false)
                    .replace("\\n", "\n")
                    .split("\n")

    fun wrapInt(x: Int, min: Int, max: Int) =
            when {
                x in min..max -> x
                x < min -> max
                else -> min
            }

    /**
     * Reads a resource from the jar, located at [path]
     *
     * We need to use Mod::class, because our own classes use the bootstrapped class loader
     */
    fun getResourceFromJar(path: String) =
            Mod::class.java.classLoader.getResource(path)
}
