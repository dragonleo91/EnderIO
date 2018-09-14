package crazypants.enderio.endergy.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.endergy.EnderIOEndergy;
import crazypants.enderio.endergy.capacitor.ItemEndergyCapacitor;
import crazypants.enderio.endergy.capacitor.ItemTotemicCapacitor;
import crazypants.enderio.endergy.conduit.ItemEndergyConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOEndergy.MODID)
public enum EndergyObject implements IModObjectBase {

//  itemEndergyAlloy(ItemEndergyAlloy.class),
  itemCapacitorGrainy(ItemEndergyCapacitor.class, "create_grainy"),
  itemCapacitorCrystalline(ItemEndergyCapacitor.class, "create_crystalline"),
  itemCapacitorMelodic(ItemEndergyCapacitor.class, "create_melodic"),
  itemCapacitorStellar(ItemEndergyCapacitor.class, "create_stellar"),
  itemCapacitorTotemic(ItemTotemicCapacitor.class),
  itemEndergyConduit(ItemEndergyConduit.class),
  itemCapacitorSilver(ItemEndergyCapacitor.class, "create_silver"),
  itemCapacitorLead(ItemEndergyCapacitor.class, "create_lead"),
  itemCapacitorElectrum(ItemEndergyCapacitor.class, "create_electrum"),

  // Tools and Armour
  itemStellarAlloySword(ItemDarkSteelSword.class, "createStellarAlloy"),
  itemStellarAlloyPickaxe(ItemDarkSteelPickaxe.class, "createStellarAlloy"),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(EndergyObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;
  protected final @Nullable String blockMethodName, itemMethodName;
  protected final @Nullable IModTileEntity modTileEntity;

  private EndergyObject(@Nonnull Class<?> clazz) {
    this(clazz, "create", (IModTileEntity) null);
  }

  private EndergyObject(@Nonnull Class<?> clazz, @Nullable IModTileEntity modTileEntity) {
    this(clazz, "create", modTileEntity);
  }

  private EndergyObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz, methodName, (IModTileEntity) null);
  }

  private EndergyObject(@Nonnull Class<?> clazz, @Nonnull String blockMethodName, @Nonnull String itemMethodName) {
    this(clazz, blockMethodName, itemMethodName, null);
  }

  private EndergyObject(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    if (Block.class.isAssignableFrom(clazz)) {
      this.blockMethodName = methodName;
      this.itemMethodName = null;
    } else if (Item.class.isAssignableFrom(clazz)) {
      this.blockMethodName = null;
      this.itemMethodName = methodName;
    } else {
      throw new RuntimeException("Clazz " + clazz + " unexpectedly is neither a Block nor an Item.");
    }
    this.modTileEntity = modTileEntity;
  }

  private EndergyObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    this.modTileEntity = modTileEntity;
  }

  @Override
  public @Nonnull Class<?> getClazz() {
    return clazz;
  }

  @Override
  public void setItem(@Nullable Item obj) {
    this.item = obj;
  }

  @Override
  public void setBlock(@Nullable Block obj) {
    this.block = obj;
  }

  @Nonnull
  @Override
  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Nonnull
  @Override
  public ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, getUnlocalisedName());
  }

  @Nullable
  @Override
  public Block getBlock() {
    return block;
  }

  @Nullable
  @Override
  public Item getItem() {
    return item;
  }

  @Override
  @Nullable
  public IModTileEntity getTileEntity() {
    return modTileEntity;
  }

  @Override
  public final @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setUnlocalizedName(getUnlocalisedName());
    blockIn.setRegistryName(getRegistryName());
    return blockIn;
  }

  @Override
  public final @Nonnull <I extends Item> I apply(@Nonnull I itemIn) {
    itemIn.setUnlocalizedName(getUnlocalisedName());
    itemIn.setRegistryName(getRegistryName());
    return itemIn;
  }

  @Override
  @Nullable
  public String getBlockMethodName() {
    return blockMethodName;
  }

  @Override
  @Nullable
  public String getItemMethodName() {
    return itemMethodName;
  }

}
