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
package tech.feldman.betterrecords.extensions

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*

inline fun NBTTagList.forEachTag(action: (NBTTagCompound) -> Unit) {
    for (index in 0 until tagCount()) {
        action(getCompoundTagAt(index))
    }
}

inline fun NBTTagList.forEachTagIndexed(action: (Int, NBTTagCompound) -> Unit) {
    for (index in 0 until tagCount()) {
        action(index, getCompoundTagAt(index))
    }
}

operator fun NBTTagList.get(index: Int) = get(index)
operator fun NBTTagCompound.get(key: String) = getTag(key)

operator fun NBTTagCompound.set(key: String, value: NBTBase) = setTag(key, value)
operator fun NBTTagCompound.set(key: String, value: Byte) = setByte(key, value)
operator fun NBTTagCompound.set(key: String, value: Short) = setShort(key, value)
operator fun NBTTagCompound.set(key: String, value: Int) = setInteger(key, value)
operator fun NBTTagCompound.set(key: String, value: Long) = setLong(key, value)
operator fun NBTTagCompound.set(key: String, value: UUID) = setUniqueId(key, value)
operator fun NBTTagCompound.set(key: String, value: Float) = setFloat(key, value)
operator fun NBTTagCompound.set(key: String, value: Double) = setDouble(key, value)
operator fun NBTTagCompound.set(key: String, value: ByteArray) = setByteArray(key, value)
operator fun NBTTagCompound.set(key: String, value: IntArray) = setIntArray(key, value)
operator fun NBTTagCompound.set(key: String, value: Boolean) = setBoolean(key, value)
operator fun NBTTagCompound.set(key: String, value: String) = setString(key, value)
