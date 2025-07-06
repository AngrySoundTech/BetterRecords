package com.hemogoblins.betterrecords.mixin;

import com.hemogoblins.betterrecords.BRConfig;
import com.hemogoblins.betterrecords.api.client.sound.FileSoundInstance;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public abstract class MixinSoundEngine {

    @Shadow @Final private ChannelAccess channelAccess;

    @Unique
    private final Map<FileSoundInstance, ChannelAccess.ChannelHandle> betterRecords$soundHandles = new HashMap<>();

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
        if (sound instanceof FileSoundInstance fileSound) {
            try {
                File soundFile = new File(BRConfig.Client.INSTANCE.getCacheDirectory().get(), fileSound.getFile());

                if (!soundFile.exists()) {
                    throw new RuntimeException("Sound file not found: " + soundFile.getAbsolutePath());
                }

                FileInputStream stream = new FileInputStream(soundFile);
                OggAudioStream oggStream = new OggAudioStream(stream);
                ByteBuffer audioData = oggStream.readAll();

                SoundBuffer soundBuffer = new SoundBuffer(audioData, oggStream.getFormat());

                CompletableFuture<ChannelAccess.ChannelHandle> handleFuture = this.channelAccess.createHandle(Library.Pool.STREAMING);
                ChannelAccess.ChannelHandle channelHandle = handleFuture.join();

                if (channelHandle != null) {
                    // TODO: Should this just be a blockpos?
                    Vec3 soundPos = new Vec3(fileSound.getX(), fileSound.getY(), fileSound.getZ());

                    channelHandle.execute((channel) -> {
                        channel.setPitch(sound.getPitch());
                        channel.setVolume(betterRecords$calculateVolumeByDistance(soundPos));
                        channel.attachStaticBuffer(soundBuffer);
                        channel.play();
                    });

                    betterRecords$soundHandles.put(fileSound, channelHandle);
                    ci.cancel();
                }

                oggStream.close();
                stream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Inject(method="stop", at = @At("HEAD"), cancellable = true)
    public void onStop(SoundInstance sound, CallbackInfo ci) {
        if (sound instanceof FileSoundInstance fileSound) {
            ChannelAccess.ChannelHandle channelHandle = betterRecords$soundHandles.get(fileSound);

            if (channelHandle != null) {
                channelHandle.execute(Channel::stop);
                betterRecords$soundHandles.remove(fileSound);
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
