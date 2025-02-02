package net.tslat.aoa3.data.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.tslat.aoa3.advent.Logging;
import net.tslat.aoa3.common.registration.custom.AoAResources;
import net.tslat.aoa3.player.PlayerDataManager;
import net.tslat.aoa3.player.resource.AoAResource;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class AoAResourcesReloadListener extends JsonReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String folder = "player/resources";

	private static final HashMap<AoAResource, JsonObject> RESOURCES = new HashMap<AoAResource, JsonObject>();

	public AoAResourcesReloadListener() {
		super(GSON, folder);
	}

	public static void populateResourceMap(PlayerDataManager plData, HashMap<AoAResource, AoAResource.Instance> resourceMap) {
		resourceMap.clear();

		for (Map.Entry<AoAResource, JsonObject> resource : RESOURCES.entrySet()) {
			resourceMap.put(resource.getKey(), resource.getKey().buildDefaultInstance(plData, resource.getValue()));
		}
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsonMap, IResourceManager resourceManager, IProfiler profiler) {
		RESOURCES.clear();

		for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
			ResourceLocation resourceId = entry.getKey();
			JsonElement json = entry.getValue();
			AoAResource resource = AoAResources.getResource(resourceId);

			if (resource == null) {
				Logging.logMessage(Level.WARN, "Unable to find registered resource: '" + resourceId.toString() + "' from datapack entry.");

				continue;
			}

			if (!json.isJsonObject()) {
				Logging.logMessage(Level.ERROR, "Invalidly formatted resource json '" + resourceId.toString() + "' from datapack entry.");

				continue;
			}

			RESOURCES.put(resource, json.getAsJsonObject());
		}
	}
}
