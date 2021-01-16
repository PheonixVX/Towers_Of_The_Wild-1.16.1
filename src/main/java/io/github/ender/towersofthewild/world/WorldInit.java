package io.github.ender.towersofthewild.world;

import io.github.ender.towersofthewild.config.TowersOfTheWildConfig;
import io.github.ender.towersofthewild.util.RegistryHandler;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WorldInit {

	public static void setup() {
		TowersOfTheWildConfig TOTWConfig = AutoConfig.getConfigHolder(TowersOfTheWildConfig.class).getConfig();
		for (Biome biome : Biome.BIOMES) {
			if (!TOTWConfig.biomeBlackList.contains(biome.getTranslationKey().replace("biome.", "").replace(".", ":"))) {
				System.out.println("Adding to " + biome.getTranslationKey().replace("biome.", "").replace(".", ":"));
				addSurfaceStructure(biome, RegistryHandler.TOWER);
			}
		}
	}

	private static void addSurfaceStructure(Biome biome, StructureFeature<DefaultFeatureConfig> structure) {
		biome.addStructureFeature(
			structure.configure(DefaultFeatureConfig.INSTANCE
			)
		);
	}
}
