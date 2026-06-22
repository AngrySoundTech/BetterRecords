package com.hemogoblins.betterrecords.mixin;

import com.hemogoblins.betterrecords.BRConfig;
import com.hemogoblins.betterrecords.BetterRecords;
import com.hemogoblins.betterrecords.api.client.sound.FileSoundInstance;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public abstract class MixinSoundEngine {

    @Shadow @Final private ChannelAccess channelAccess;

    @Unique
    private final Map<FileSoundInstance, ChannelAccess.ChannelHandle> betterRecords$soundHandles = new HashMap<>();

    /**
     * Sounds whose audio is still being decoded off-thread. A stop() that
     * arrives while a sound is in here cancels it before it ever plays.
     * Only ever touched on the main thread.
     */
    @Unique
    private final Set<FileSoundInstance> betterRecords$loading = new HashSet<>();

    // TODO: Ability to boost and reduce the distance of sounds based on their network.
    // For now, four chunks is pretty good.

    /** Maximum distance in blocks at which the sound can be heard */
    @Unique
    private static final float MAX_DISTANCE = 64.0F;

    /** Base volume of the sound */
    @Unique
    private static final float BASE_VOLUME = 1.0F;

    /**
     * Calculates the volume of a sound based on the distance between the player and the sound source.
     * The volume decreases linearly with distance, reaching 0 at MAX_DISTANCE.
     *
     * @param soundPos The position of the sound source
     * @return The calculated volume
     */
    @Unique
    private float betterRecords$calculateVolumeByDistance(Vec3 soundPos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return BASE_VOLUME;

        Vec3 playerPos = minecraft.player.position();
        double distance = playerPos.distanceTo(soundPos);

        // Linear attenuation: volume decreases linearly with distance
        if (distance >= MAX_DISTANCE) return 0.0F;
        return BASE_VOLUME * (1.0F - (float)(distance / MAX_DISTANCE));
    }

    @Inject(method="play", at = @At("HEAD"), cancellable = true)
    public void onPlay(SoundInstance sound, CallbackInfo ci) {
        if (!(sound instanceof FileSoundInstance fileSound)) {
            return;
        }

        // We fully handle our own file-backed sounds, so skip vanilla's handling.
        ci.cancel();

        File soundFile = new File(BRConfig.Client.INSTANCE.getCacheDirectory().get(), fileSound.getFile());

        // Mark the sound as loading so a stop() arriving before it is ready can cancel it.
        betterRecords$loading.add(fileSound);

        CompletableFuture<SoundBuffer> bufferFuture =
                CompletableFuture.supplyAsync(() -> betterRecords$decode(soundFile), Util.backgroundExecutor());
        bufferFuture

                .thenAcceptBothAsync(
                        this.channelAccess.createHandle(Library.Pool.STREAMING),
                        (soundBuffer, channelHandle) -> betterRecords$startPlaying(fileSound, soundBuffer, channelHandle),
                        Minecraft.getInstance())
                .exceptionally(throwable -> {
                    betterRecords$loading.remove(fileSound);
                    BetterRecords.INSTANCE.getLogger().error("Failed to play record sound", throwable);
                    return null;
                });
    }

    /** Reads and decodes an OGG file into a static buffer. Runs off the main thread. */
    @Unique
    private SoundBuffer betterRecords$decode(File soundFile) {
        if (!soundFile.exists()) {
            throw new RuntimeException("Sound file not found: " + soundFile.getAbsolutePath());
        }

        try (FileInputStream stream = new FileInputStream(soundFile);
             OggAudioStream oggStream = new OggAudioStream(stream)) {
            return new SoundBuffer(oggStream.readAll(), oggStream.getFormat());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Attaches the decoded buffer to a channel and starts playback. Runs on the main thread. */
    @Unique
    private void betterRecords$startPlaying(FileSoundInstance fileSound, SoundBuffer soundBuffer, ChannelAccess.ChannelHandle channelHandle) {
        // If the sound was stopped while it was still loading, don't start it.
        boolean wanted = betterRecords$loading.remove(fileSound);

        if (channelHandle == null) {
            return;
        }

        if (!wanted) {
            channelHandle.execute(Channel::stop);
            return;
        }

        // TODO: Should this just be a blockpos?
        Vec3 soundPos = new Vec3(fileSound.getX(), fileSound.getY(), fileSound.getZ());

        channelHandle.execute((channel) -> {
            channel.setPitch(fileSound.getPitch());
            channel.setVolume(betterRecords$calculateVolumeByDistance(soundPos));
            channel.attachStaticBuffer(soundBuffer);
            channel.play();
        });

        betterRecords$soundHandles.put(fileSound, channelHandle);
    }

    @Inject(method="stop", at = @At("HEAD"), cancellable = true)
    public void onStop(SoundInstance sound, CallbackInfo ci) {
        if (sound instanceof FileSoundInstance fileSound) {
            // Cancel playback if the sound is still being decoded
            betterRecords$loading.remove(fileSound);

            ChannelAccess.ChannelHandle channelHandle = betterRecords$soundHandles.remove(fileSound);

            if (channelHandle != null) {
                channelHandle.execute(Channel::stop);
            }

            ci.cancel();
        }
    }

    /**
     * Updates the volume of all active sounds based on the player's distance from the sound source.
     */
    @Inject(method="tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        for (Map.Entry<FileSoundInstance, ChannelAccess.ChannelHandle> entry : betterRecords$soundHandles.entrySet()) {
            FileSoundInstance sound = entry.getKey();
            ChannelAccess.ChannelHandle handle = entry.getValue();
            Vec3 soundPos = new Vec3(sound.getX(), sound.getY(), sound.getZ());
            float volume = betterRecords$calculateVolumeByDistance(soundPos);
            handle.execute(channel -> channel.setVolume(volume));
        }
    }
}
