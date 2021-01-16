package io.github.ender.towersofthewild.world.structures;

import io.github.ender.towersofthewild.TowersOfTheWild;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.*;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import java.util.Map;
import java.util.Random;

public class AbstractTowerPiece extends SimpleStructurePiece {

	private static final Identifier TOWER_CHEST = new Identifier(TowersOfTheWild.MOD_ID, "chests/tower_chest");

	protected final Map<Identifier, BlockPos> centerTopOffsets;
	protected final Identifier structurePart;
	protected final BlockRotation rotation;

	public AbstractTowerPiece(StructureManager structureManager, Identifier structurePart, BlockRotation rotation, StructurePieceType piece, Map<Identifier, BlockPos> centerTopOffsets) {
		super(piece, 0);
		this.structurePart = structurePart;
		this.rotation = rotation;
		this.centerTopOffsets = centerTopOffsets;
	}

	public AbstractTowerPiece(StructureManager structureManager, StructurePieceType structurePieceTypeIn, CompoundTag tag, Map<Identifier, BlockPos> centerTopOffsets) {
		super(structurePieceTypeIn, tag);
		this.structurePart = new Identifier(tag.getString("Template"));
		this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
		this.centerTopOffsets = centerTopOffsets;
		this.initializeStructureData(structureManager);
	}

	protected void initializeStructureData(StructureManager structureManager) {
		Structure structure = structureManager.getStructureOrBlank(this.structurePart);
		StructurePlacementData placementData = (new StructurePlacementData()).setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition(centerTopOffsets.get(this.structurePart));
		this.setStructureData(structure, this.pos, placementData);
	}

	protected void toNbt(CompoundTag tagCompound) {
		super.toNbt(tagCompound);
		tagCompound.putString("Template", this.structurePart.toString());
		tagCompound.putString("Rot", this.rotation.name());
	}

	protected void handleMetadata(String function, BlockPos pos, WorldAccess world, Random rand, BlockBox sbb) {
		if ("chest".equals(function)) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			BlockEntity blockEntity = world.getBlockEntity(pos.down());
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity) blockEntity).setLootTable(TOWER_CHEST, rand.nextLong());
			}
		}
	}
}
