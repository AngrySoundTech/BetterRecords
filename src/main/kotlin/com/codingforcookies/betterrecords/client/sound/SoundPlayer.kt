package com.codingforcookies.betterrecords.client.sound

import com.codingforcookies.betterrecords.BetterRecords
import com.codingforcookies.betterrecords.ModConfig
import com.codingforcookies.betterrecords.api.record.IRecordAmplitude
import com.codingforcookies.betterrecords.api.sound.Sound
import com.codingforcookies.betterrecords.api.wire.IRecordWireHome
import com.codingforcookies.betterrecords.client.handler.ClientRenderHandler
import com.codingforcookies.betterrecords.util.downloadFile
import com.codingforcookies.betterrecords.util.getVolumeForPlayerFromBlock
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.sound.sampled.*
import kotlin.math.absoluteValue

object SoundPlayer {

    private val downloadFolder = File(Minecraft.getMinecraft().mcDataDir, "betterrecords/cache")

    private val playingSounds = HashMap<Pair<BlockPos, Int>, Sound>()

    fun playSound(pos: BlockPos, dimension: Int, sound: Sound) {
        BetterRecords.logger.info("Playing sound at $pos in Dimension $dimension")

        ClientRenderHandler.nowDownloading = sound.localName
        ClientRenderHandler.showDownloading = true

        val targetFile = File(downloadFolder, FilenameUtils.getName(sound.url).replace(Regex("[^a-zA-Z0-9_\\.]"), "_"))

        downloadFile(URL(sound.url), targetFile,
                update = { curr, total ->
                    ClientRenderHandler.downloadPercent = curr / total
                },
                success = {
                    ClientRenderHandler.showDownloading = false
                    playingSounds[Pair(pos, dimension)] = sound
                    ClientRenderHandler.showPlayingWithTimeout(sound.localName)
                    playFile(targetFile, pos, dimension)
                },
                failure = {
                    ClientRenderHandler.showDownloading = false
                }
        )
    }

    fun playSoundFromStream(pos: BlockPos, dimension: Int, sound: Sound) {
        BetterRecords.logger.info("Playing sound from stream at $pos in $dimension")

        val urlConn = IcyURLConnection(URL(if (sound.url.startsWith("http")) sound.url else "http://${sound.url}")).apply {
            instanceFollowRedirects = true
        }

        urlConn.connect()

        playingSounds[Pair(pos, dimension)] = sound

        ClientRenderHandler.showPlayingWithTimeout(sound.localName)
        playStream(urlConn.inputStream, pos, dimension)
    }

    fun isSoundPlayingAt(pos: BlockPos, dimension: Int) =
            playingSounds.containsKey(Pair(pos, dimension))

    fun getSoundPlayingAt(pos: BlockPos, dimension: Int) =
            playingSounds[Pair(pos, dimension)]

    fun stopPlayingAt(pos: BlockPos, dimension: Int) {
        BetterRecords.logger.info("Stopping sound at $pos in Dimension $dimension")
        playingSounds.remove(Pair(pos, dimension))
    }

    private fun playFile(file: File, pos: BlockPos, dimension: Int) {
        play(AudioSystem.getAudioInputStream(file), pos, dimension)
    }

    private fun playStream(stream: InputStream, pos: BlockPos, dimension: Int) {
        play(AudioSystem.getAudioInputStream(stream), pos, dimension)
    }

    private fun play(ain: AudioInputStream, pos: BlockPos, dimension: Int) {
        val baseFormat = ain.format
        val decodedFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.sampleRate, 16,
                baseFormat.channels,
                baseFormat.channels * 2,
                baseFormat.sampleRate,
                false
        )

        val din = AudioSystem.getAudioInputStream(decodedFormat, ain)
        rawPlay(decodedFormat, din, pos, dimension)
        ain.close()
    }

    private fun rawPlay(targetFormat: AudioFormat, din: AudioInputStream, pos: BlockPos, dimension: Int) {
        val line = getLine(targetFormat)

        val volumeControl = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl

        line.start()

        val buffer = ByteArray(4096)
        var bytes = din.read(buffer)

        while (bytes >= 0 && isSoundPlayingAt(pos, dimension)) {
            volumeControl.value = getVolumeForPlayerFromBlock(pos)
            line.write(buffer, 0, bytes)
            updateLights(buffer, pos, dimension)
            bytes = din.read(buffer)
        }

        stopPlayingAt(pos, dimension)

        line.drain()
        line.stop()
        line.close()
        din.close()
    }

    private fun getLine(audioFormat: AudioFormat): SourceDataLine {
        val info = DataLine.Info(SourceDataLine::class.java, audioFormat)
        val res = AudioSystem.getLine(info)
        res.open()
        return res as SourceDataLine
    }

    private fun updateLights(buffer: ByteArray, pos: BlockPos, dimension: Int) {
        if (Minecraft.getMinecraft().world.provider.dimension != dimension) {
            return
        }

        var unscaledTreble = -1F
        var unscaledBass = -1F

        val te = Minecraft.getMinecraft().world.getTileEntity(pos)

        (te as? IRecordWireHome)?.let {
                      te.addTreble(getUnscaledWaveform(buffer, true, false))
                      te.addBass(getUnscaledWaveform(buffer, false, false))

            for (connection in te.connections) {
                val connectedTe = Minecraft.getMinecraft().world.getTileEntity(BlockPos(connection.x2, connection.y2, connection.z2))

                (connectedTe as? IRecordAmplitude)?.let {
                    if (unscaledTreble == -1F || unscaledBass == 11F) {
                        unscaledTreble = getUnscaledWaveform(buffer, true, true)
                        unscaledBass = getUnscaledWaveform(buffer, false, true)
                    }

                    connectedTe.treble = unscaledTreble
                    connectedTe.bass = unscaledBass
                }
            }
        }
    }

    private fun getUnscaledWaveform(buffer: ByteArray, high: Boolean, control: Boolean): Float {
        val toReturn = ByteArray(buffer.size / 2)

        var avg = 0.0F

        for ((index, audioByte) in ((if (high) 0 else 1) until (buffer.size) step 2).withIndex()) {
            toReturn[index] = buffer[audioByte]
            avg += toReturn[index]
        }

        avg /= toReturn.size

        if (control) {
            if (avg < 0F) {
                avg = avg.absoluteValue
            }

            if (avg > 20F) {
                return if (ModConfig.client.flashMode < 3) {
                    1F
                } else {
                    2F
                }
            }
        }
        return avg
    }
}
