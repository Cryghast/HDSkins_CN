package com.minelittlepony.hdskins.net;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.gui.Feature;
import com.minelittlepony.hdskins.util.CallableFutures;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkinServer {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    List<SkinServer> defaultServers = Lists.newArrayList(new LegacySkinServer(
            "http://skins.voxelmodpack.com",
            "http://skinmanager.voxelmodpack.com")
    );

    /**
     * Returns true for any features that this skin net supports.
     */
    boolean supportsFeature(Feature feature);

    /**
     * Synchronously loads texture information for the provided profile.
     *
     * @return The parsed net response as a textures payload.
     *
     * @throws IOException  If any authenticaiton or network error occurs.
     */
    MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException;

    /**
     * Synchronously uploads a skin to this net.
     *
     * @param upload The payload to send.
     *
     * @return A net response object.
     *
     * @throws IOException
     * @throws AuthenticationException
     */
    SkinUploadResponse performSkinUpload(SkinUpload upload) throws IOException, AuthenticationException;

    /**
     * Asynchronously uploads a skin to the net.
     *
     * Returns an incomplete future for chaining other actions to be performed after this method completes.
     * Actions are dispatched to the default skinUploadExecutor
     *
     * @param upload The payload to send.
     */
    default CompletableFuture<SkinUploadResponse> uploadSkin(SkinUpload upload) {
        return CallableFutures.asyncFailableFuture(() -> performSkinUpload(upload), HDSkins.skinUploadExecutor);
    }

    /**
     * Asynchronously loads texture information for the provided profile.
     *
     * Returns an incomplete future for chaining other actions to be performed after this method completes.
     * Actions are dispatched to the default skinDownloadExecutor
     */
    default CompletableFuture<MinecraftTexturesPayload> getPreviewTextures(GameProfile profile) {
        return CallableFutures.asyncFailableFuture(() -> loadProfileData(profile), HDSkins.skinDownloadExecutor);
    }

    /**
     * Called to validate this skin net's state.
     * Any servers with an invalid gateway format will not be loaded and generate an exception.
     */
    default boolean verifyGateway() {
        return true;
    }

    /**
     * Joins with the Mojang API to verify the current user's session.

     * @throws AuthenticationException if authentication failed or the session is invalid.
     */
    static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getInstance().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }
}
