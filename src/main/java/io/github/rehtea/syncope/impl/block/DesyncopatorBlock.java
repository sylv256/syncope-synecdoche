package io.github.rehtea.syncope.impl.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DesyncopatorBlock extends Block {
	public static final int MAX_STRENGTH = 4;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final IntegerProperty STRENGTH = IntegerProperty.create("strength", 0, MAX_STRENGTH);

	public DesyncopatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED, STRENGTH);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	private void setStrength(int strength, BlockPos pos, BlockState state, Level level) {
		level.setBlock(pos, state.setValue(DesyncopatorBlock.STRENGTH, strength), 0);
	}

	public void setPowered(boolean powered, BlockPos pos, BlockState state, Level level) {
		state = state.setValue(DesyncopatorBlock.POWERED, powered);

		if (powered) {
			state = state.setValue(DesyncopatorBlock.STRENGTH, MAX_STRENGTH);
		}

		level.setBlock(pos, state, Block.UPDATE_ALL);

		if (powered) {
			level.scheduleTick(pos, this, 2);
		}
	}

	private void updateNeighbours(final Level level, final BlockPos pos) {
		level.updateNeighborsAt(pos, this, null);
	}

	@Override
	protected void tick(
			BlockState state,
			ServerLevel level,
			BlockPos pos,
			RandomSource random
	) {
		if (state.getValue(POWERED)) {
			if (state.getValue(STRENGTH) == 0) {
				this.setPowered(false, pos, state, level);
			} else {
				this.setStrength(state.getValue(STRENGTH) - 1, pos, state, level);
				level.scheduleTick(pos, this, 2);
			}
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(
			BlockState state,
			ServerLevel level,
			BlockPos pos,
			boolean movedByPiston
	) {
		if (state.getValue(POWERED)) {
			this.updateNeighbours(level, pos);
		}
	}

	@Override
	protected int getSignal(
			BlockState state,
			BlockGetter level,
			BlockPos pos,
			Direction direction
	) {
		return state.getValue(POWERED) ? 15 : 0;
	}
}
