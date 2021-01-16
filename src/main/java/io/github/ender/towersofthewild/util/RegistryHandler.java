package io.github.ender.towersofthewild.util;

import io.github.ender.towersofthewild.TowersOfTheWild;
import io.github.ender.towersofthewild.world.structures.*;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class RegistryHandler {

	public static final StructureFeature<DefaultFeatureConfig> TOWER = registerStructure(
		"tower",
		new TowerStructure(DefaultFeatureConfig.CODEC)
	);

	public static <T extends StructureFeature<?>> T registerStructure(String name, T structure) {
		StructureFeature.STRUCTURES.put(TowersOfTheWild.MOD_ID + ":" + name, structure);
		StructureFeature.STRUCTURE_TO_GENERATION_STEP.put(structure, GenerationStep.Feature.SURFACE_STRUCTURES);
		TowersOfTheWild.LOGGER.info(name + " structure registered");
		return Registry.register(
			Registry.STRUCTURE_FEATURE,
			new Identifier(TowersOfTheWild.MOD_ID, "tower"),
			structure
		);
	}

	public static void registerPieces() {
		registerPiece("tower", TOWER_PIECE);
		registerPiece("jungle_tower", JUNGLE_TOWER_PIECE);
		registerPiece("ice_tower", ICE_TOWER_PIECE);
		registerPiece("derelict_tower", DERELICT_TOWER_PIECE);
		registerPiece("derelict_tower_grass", DERELICT_TOWER_GRASS_PIECE);
	}

	public static final StructurePieceType TOWER_PIECE = TowerPieces.Piece::new;
	public static final StructurePieceType JUNGLE_TOWER_PIECE = JungleTowerPieces.Piece::new;
	public static final StructurePieceType ICE_TOWER_PIECE = IceTowerPieces.Piece::new;
	public static final StructurePieceType DERELICT_TOWER_PIECE = DerelictTowerPieces.Piece::new;
	public static final StructurePieceType DERELICT_TOWER_GRASS_PIECE = DerelictTowerGrassPieces.Piece::new;

	private static void registerPiece(String key, StructurePieceType type) {
		TowersOfTheWild.LOGGER.info(key + " structure piece registered");
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(TowersOfTheWild.MOD_ID, key), type);
	}
}
