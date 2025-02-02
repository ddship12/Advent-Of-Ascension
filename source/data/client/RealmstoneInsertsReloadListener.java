package net.tslat.aoa3.data.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.tslat.aoa3.advent.Logging;
import net.tslat.aoa3.client.gui.realmstone.BlankRealmstoneScreen;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class RealmstoneInsertsReloadListener implements IResourceManagerReloadListener {
	public static final HashMap<String, BlankRealmstoneScreen.RealmstoneWorldInsert> INSERTS = new HashMap<String, BlankRealmstoneScreen.RealmstoneWorldInsert>();
	private static final Gson GSON = new Gson();

	public RealmstoneInsertsReloadListener() {}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		try {
			INSERTS.clear();

			for (ResourceLocation resourceLocation : resourceManager.listResources("realmstonegui", path -> path.endsWith(".json"))) {
				for (IResource resource : resourceManager.getResources(resourceLocation)) {
					try (InputStream stream = resource.getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
						JsonObject json = JSONUtils.fromJson(GSON, reader, JsonObject.class);

						if (json != null) {
							BlankRealmstoneScreen.RealmstoneWorldInsert insert = BlankRealmstoneScreen.RealmstoneWorldInsert.fromJson(json);

							INSERTS.put(insert.getId(), insert);
						}
						else {
							Logging.logMessage(Level.ERROR, "Realmstone insert json " + resource.getLocation().toString() + " appears to be empty or null.");
						}
					}
					catch (RuntimeException | IOException ex) {
						Logging.logMessage(Level.ERROR, "Unable to deserialize realmstone insert json " + resourceLocation + ", skipping.", ex);
					}
					finally {
						IOUtils.closeQuietly(resource);
					}
				}
			}

			for (BlankRealmstoneScreen.RealmstoneWorldInsert insert : INSERTS.values()) {
				for (String id : insert.getParents()) {
					if (!INSERTS.containsKey(id))
						throw new IllegalArgumentException("Invalid parent node: " + id + " for realmstone insert " + insert.getId());
				}
			}
		}
		catch (IOException ex) {
			Logging.logMessage(Level.ERROR, "Failed to retrieve AoA3 realmstone insert resources, skipping.", ex);
		}
	}
}
