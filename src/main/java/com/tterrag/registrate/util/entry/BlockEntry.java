package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntry<T extends Block> extends ItemProviderEntry<Block, T> {

    public BlockEntry(AbstractRegistrate<?> owner, ResourceKey<Block> key) {
        super(owner, key);
    }

    public BlockState getDefaultState() {
        return get().defaultBlockState();
    }

    public boolean has(BlockState state) {
        return is(state.getBlock());
    }
    
    public static <T extends Block> BlockEntry<T> cast(RegistryEntry<Block, T> entry) {
        return RegistryEntry.cast(BlockEntry.class, entry);
    }
}
