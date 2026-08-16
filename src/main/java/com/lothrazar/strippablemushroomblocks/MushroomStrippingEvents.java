package com.lothrazar.strippablemushroomblocks;

import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID)
public class MushroomStrippingEvents {

  //link DIR to actual face
  private static final Map<Direction, BooleanProperty> FACE_PROPS = Map.of(
      Direction.NORTH, HugeMushroomBlock.NORTH,
      Direction.SOUTH, HugeMushroomBlock.SOUTH,
      Direction.EAST, HugeMushroomBlock.EAST,
      Direction.WEST, HugeMushroomBlock.WEST,
      Direction.UP, HugeMushroomBlock.UP,
      Direction.DOWN, HugeMushroomBlock.DOWN
  );

  @SubscribeEvent
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    Player player = event.getEntity();
    Level level = event.getLevel();
    ItemStack stack = event.getItemStack();
    if (!(stack.getItem() instanceof AxeItem)) {
      return;
    }
    if (event.getPos() == null || event.getFace() == null) {
      return;
    }
    BlockState state = level.getBlockState(event.getPos());
    if (state.getBlock() != Blocks.RED_MUSHROOM_BLOCK
        && state.getBlock() != Blocks.BROWN_MUSHROOM_BLOCK
        && state.getBlock() != Blocks.MUSHROOM_STEM) {
      return;
    }
    if (!player.isShiftKeyDown()) {
      BooleanProperty prop = FACE_PROPS.get(event.getFace());
      if (prop == null) {
        return;
      }
      event.setCanceled(true);
      event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
      if (!level.isClientSide) {
        state = state.setValue(prop, !state.getValue(prop));
        level.setBlock(event.getPos(), state, Block.UPDATE_ALL_IMMEDIATE);
        EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
            ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(slot));
        level.playSound(null, event.getPos(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
    } else {
      event.setCanceled(true);
      event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
      if (!level.isClientSide) {
        for (BooleanProperty prop : FACE_PROPS.values()) {
          state = state.setValue(prop, !state.getValue(prop));
        }
        level.setBlock(event.getPos(), state, Block.UPDATE_ALL_IMMEDIATE);
        EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND
            ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(slot));
        level.playSound(null, event.getPos(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
    }
  }
}
