package io.github.ender.towersofthewild.world.structures;

import com.google.common.collect.ImmutableMap;
import io.github.ender.towersofthewild.TowersOfTheWild;
import io.github.ender.towersofthewild.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class JungleTowerPieces {
	private static final Identifier JUNGLE_TOWER_TOP = new Identifier(TowersOfTheWild.MOD_ID, "jungle_tower_top");
	private static final Identifier JUNGLE_TOWER_BOTTOM = new Identifier(TowersOfTheWild.MOD_ID, "jungle_tower_bottom");

	private static final Map<Identifier, BlockPos> CENTER_TOP_OFFSETS = ImmutableMap.of(JUNGLE_TOWER_TOP, new BlockPos(7, 28, 7), JUNGLE_TOWER_BOTTOM, new BlockPos(4, 31, 4));
	private static final Map<Identifier, BlockPos> CORNER_RELATIVE_POSITIONS = ImmutableMap.of(JUNGLE_TOWER_TOP, new BlockPos(-3, 31, -3), JUNGLE_TOWER_BOTTOM, BlockPos.ORIGIN);

	public static void addPieces (StructureManager structureManager, BlockPos absolutePos, BlockRotation rotation, List<StructurePiece> pieces, Random random, DefaultFeatureConfig config) {
		pieces.add(new JungleTowerPieces.Piece(structureManager, JUNGLE_TOWER_BOTTOM, absolutePos, rotation));
		pieces.add(new JungleTowerPieces.Piece(structureManager, JUNGLE_TOWER_TOP, absolutePos, rotation));
	}

	public static class Piece extends AbstractTowerPiece {
		public Piece (StructureManager structureManager, Identifier structurePart, BlockPos absolutePos, BlockRotation rotation) {
			super(structureManager, structurePart, rotation, RegistryHandler.JUNGLE_TOWER_PIECE, CENTER_TOP_OFFSETS);
			BlockPos relativePos = CORNER_RELATIVE_POSITIONS.get(structurePart);
			this.pos = absolutePos.add(relativePos.getX(), relativePos.getY(), relativePos.getZ());
			this.initializeStructureData(structureManager);
		}

		public Piece (StructureManager structureManager, CompoundTag tag) {
			super(structureManager, RegistryHandler.JUNGLE_TOWER_PIECE, tag, CENTER_TOP_OFFSETS);
		}


		@Override
		public boolean generate (ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {

			if (this.structurePart.equals(JUNGLE_TOWER_BOTTOM)) {
				BlockPos blockpos1 = this.pos;

				// setting spawn height
				int height;
				int minHeight = Integer.MAX_VALUE;
				for (int i = 1; i < 8; ++i) {
					for (int j = 1; j < 8; ++j) {
						height = serverWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX() + i, blockpos1.getZ() + j);
						if (height < minHeight) {
							minHeight = height;
						}
					}
				}

				// replacing dirt or water blocks beneath tower by grass
				for (int i = 0; i < 9; ++i) {
					for (int j = 0; j < 9; ++j) {
						BlockPos grassPos = new BlockPos(blockpos1.getX() + i, minHeight - 1, blockpos1.getZ() + j);
						BlockState blockstate = serverWorldAccess.getBlockState(grassPos);
						if (blockstate.getBlock() == Blocks.DIRT) {
							serverWorldAccess.setBlockState(grassPos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
						}

						if (!((i == 0 || i == 8) && (j == 0 || j == 8))) {
							if (blockstate.getBlock() == Blocks.WATER) {
								serverWorldAccess.setBlockState(grassPos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
							}
						}
					}
				}
				this.pos = this.pos.add(0, minHeight - 90, 0);

			} else if (this.structurePart.equals(JUNGLE_TOWER_TOP)) {
				BlockPos blockpos1 = this.pos;
				int height;
				int minHeight = Integer.MAX_VALUE;
				for (int i = 1; i < 8; ++i) {
					for (int j = 1; j < 8; ++j) {
						height = serverWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX() + 3 + i, blockpos1.getZ() + 3 + j);
						if (height < minHeight) {
							minHeight = height;
						}
					}
				}
				this.pos = this.pos.add(0, minHeight - 90, 0);
			}
			return super.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
		}
	}
}
