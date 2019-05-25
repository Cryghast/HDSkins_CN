package com.minelittlepony.hdskins;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import java.nio.file.Path;
import java.util.function.Function;

public interface IModUtilities {

    <T extends Entity> void addRenderer(Class<T> type, Function<EntityRenderDispatcher, EntityRenderer<T>> renderer);

    Path getAssetsDirectory();
}
