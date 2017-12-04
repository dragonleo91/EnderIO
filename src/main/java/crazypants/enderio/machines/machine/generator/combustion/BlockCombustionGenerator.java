package crazypants.enderio.machines.machine.generator.combustion;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCombustionGenerator<T extends TileCombustionGenerator> extends AbstractMachineBlock<T>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  protected final @Nonnull GuiID guiID;
  protected boolean isEnhanced = false;

  public static BlockCombustionGenerator<TileCombustionGenerator> create(@Nonnull IModObject modObject) {
    BlockCombustionGenerator<TileCombustionGenerator> gen = new BlockCombustionGenerator<>(modObject, TileCombustionGenerator.class,
        GuiID.GUI_ID_COMBUSTION_GEN);
    gen.init();
    return gen;
  }

  public static BlockCombustionGenerator<TileCombustionGenerator.Enhanced> create_enhanced(@Nonnull IModObject modObject) {
    BlockCombustionGenerator<TileCombustionGenerator.Enhanced> gen = new BlockCombustionGenerator<>(modObject, TileCombustionGenerator.Enhanced.class,
        GuiID.GUI_ID_COMBUSTION_GEN_ENH);
    gen.init();
    gen.isEnhanced = true;
    return gen;
  }

  protected BlockCombustionGenerator(@Nonnull IModObject modObject, @Nonnull Class<T> teClass, @Nonnull GuiID guiID) {
    super(modObject, teClass);
    this.guiID = guiID;
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return 0;
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerCombustionGenerator<T>(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiCombustionGenerator<T>(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return guiID;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // If active, randomly throw some smoke around
    if (isActive(world, pos)) {

      TileEntity te = world.getTileEntity(pos);
      EnumFacing facing = EnumFacing.SOUTH;
      if (te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      for (int j = 0; j < (isEnhanced ? 3 : 1); j++) {

        boolean toTop = rand.nextBoolean();
        float offsetA = rand.nextFloat(); // top:front<->back or side:bottom<->top
        float offsetB = .5f + rand.nextFloat() * .2f - rand.nextFloat() * .2f; // right<->left

        float startX = pos.getX(), startY = pos.getY(), startZ = pos.getZ();

        if (toTop) {
          startY += 0.95f;
          switch (facing) {
          case NORTH:
          case SOUTH:
            startX += offsetB;
            startZ += offsetA;
            break;
          case EAST:
          case WEST:
          default:
            startX += offsetA;
            startZ += offsetB;
            break;
          }
        } else {
          boolean swap = rand.nextBoolean();
          startY += offsetA;
          switch (facing) {
          case NORTH:
          case SOUTH:
            startX += offsetB;
            startZ += swap ? 0.05f : 0.95f;
            break;
          case EAST:
          case WEST:
          default:
            startX += swap ? 0.05f : 0.95f;
            startZ += offsetB;
            break;
          }
        }

        for (int i = 0; i < (isEnhanced ? 5 : 2); i++) {
          ParticleManager er = Minecraft.getMinecraft().effectRenderer;
          Particle fx = er.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), startX, startY, startZ, 0.0D, 0.0D, 0.0D);
          if (fx != null && rand.nextFloat() > .75f) {
            fx.setRBGColorF(1 - (rand.nextFloat() * 0.2f), 1 - (rand.nextFloat() * 0.1f), 1 - (rand.nextFloat() * 0.2f));
          }
          startX += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
          startY += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
          startZ += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileCombustionGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}