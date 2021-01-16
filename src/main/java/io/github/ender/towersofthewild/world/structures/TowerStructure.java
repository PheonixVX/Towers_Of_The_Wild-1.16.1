package io.github.ender.towersofthewild.world.structures;

import com.mojang.serialization.Codec;
import io.github.ender.towersofthewild.TowersOfTheWild;
import io.github.ender.towersofthewild.config.TowersOfTheWildConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class TowerStructure extends StructureFeature<DefaultFeatureConfig> {

	public static final String NAME = TowersOfTheWild.MOD_ID + ":tower";
	private static final int SEPARATION = 5;
	private static final int SEED_MODIFIER = 16897777;

	public TowerStructure (Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public String getName () {
		return NAME;
	}

	public int getRadius () {
		return 1;
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory () {
		return Start::new;
	}

	protected boolean method_27219 () {
		return false;
	}

	protected int getSeedModifier () {
		return SEED_MODIFIER;
	}

	@Nullable
	@Override
	public BlockPos locateStructure (WorldView worldView, StructureAccessor structureAccessor, BlockPos blockPos, int i, boolean skipExistingChunks, long l, StructureConfig structureConfig) {
		TowersOfTheWildConfig TOTWConfig = AutoConfig.getConfigHolder(TowersOfTheWildConfig.class).getConfig();
		return super.locateStructure(worldView, structureAccessor, blockPos, i, skipExistingChunks, l, new StructureConfig(TOTWConfig.rarity, SEPARATION, getSeedModifier()));
	}

	@Override
	protected boolean shouldStartAt (ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, DefaultFeatureConfig featureConfig) {
		return isSurfaceFlat(chunkGenerator, i, j);
	}

	protected boolean isSurfaceFlat (ChunkGenerator generator, int chunkX, int chunkZ) {
		// Size of the area to check.
		int offset = getRadius() * 16;

		int xStart = (chunkX << 4);
		int zStart = (chunkZ << 4);

		int i1 = generator.getHeightInGround(xStart, zStart, Heightmap.Type.WORLD_SURFACE_WG);
		int j1 = generator.getHeightInGround(xStart, zStart + offset, Heightmap.Type.WORLD_SURFACE_WG);
		int k1 = generator.getHeightInGround(xStart + offset, zStart, Heightmap.Type.WORLD_SURFACE_WG);
		int l1 = generator.getHeightInGround(xStart + offset, zStart + offset, Heightmap.Type.WORLD_SURFACE_WG);
		int minHeight = Math.min(Math.min(i1, j1), Math.min(k1, l1));
		int maxHeight = Math.max(Math.max(i1, j1), Math.max(k1, l1));
		return Math.abs(maxHeight - minHeight) <= 20;
	}

	@Override
	public ChunkPos method_27218 (StructureConfig config, long p_236392_2_, ChunkRandom random, int p_236392_5_, int p_236392_6_) {
		TowersOfTheWildConfig TOTWConfig = AutoConfig.getConfigHolder(TowersOfTheWildConfig.class).getConfig();
		int spacing = TOTWConfig.rarity; // Rarity
		int gridX = ((p_236392_5_ / spacing) * spacing);
		int gridZ = ((p_236392_6_ / spacing) * spacing);

		int offset = SEPARATION + 1;
		random.setRegionSeed(p_236392_2_, gridX, gridZ, this.getSeedModifier());
		int offsetX = random.nextInt(offset);
		int offsetZ = random.nextInt(offset);

		int gridOffsetX = gridX + offsetX;
		int gridOffsetZ = gridZ + offsetZ;

		return new ChunkPos(gridOffsetX, gridOffsetZ);
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start (StructureFeature<DefaultFeatureConfig> feature, int p_i225806_2_, int p_i225806_3_, BlockBox p_i225806_4_, int p_i225806_5_, long p_i225806_6_) {
			super(feature, p_i225806_2_, p_i225806_3_, p_i225806_4_, p_i225806_5_, p_i225806_6_);
		}

		@Override
		public void init(ChunkGenerator generator, StructureManager structureManager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig config) {

			int i = chunkX * 16;
			int j = chunkZ * 16;
			BlockPos blockpos = new BlockPos(i + 3, 90, j + 3);
			BlockRotation rotation = BlockRotation.NONE;

			if (biome.getCategory() == Biome.Category.JUNGLE) {
				JungleTowerPieces.addPieces(structureManager, blockpos, rotation, this.children, this.random, config);
			} else if (biome.getCategory() == Biome.Category.ICY) {
				IceTowerPieces.addPieces(structureManager, blockpos, rotation, this.children, this.random, config);
			} else {
				TowersOfTheWildConfig TOTWConfig = AutoConfig.getConfigHolder(TowersOfTheWildConfig.class).getConfig();
				if (this.random.nextInt(100) < TOTWConfig.derelictTowerProportion) {
					blockpos = new BlockPos(i, 90, j);
					if (biome.getCategory() == Biome.Category.PLAINS
						    || biome.getCategory() == Biome.Category.FOREST
						    || biome.getCategory() == Biome.Category.TAIGA
						    || biome.getCategory() == Biome.Category.SAVANNA
						    || biome.getCategory() == Biome.Category.EXTREME_HILLS) {
						DerelictTowerGrassPieces.addPieces(structureManager, blockpos, rotation, this.children, this.random, config);
					} else {
						DerelictTowerPieces.addPieces(structureManager, blockpos, rotation, this.children, this.random, config);
					}
				} else {
					TowerPieces.addPieces(structureManager, blockpos, rotation, this.children, this.random, config);
				}
			}
			this.setBoundingBoxFromChildren();
		}
	}
}