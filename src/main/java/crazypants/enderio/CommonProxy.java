package crazypants.enderio;

import javax.annotation.Nonnull;

import com.thoughtworks.xstream.converters.ConversionException;

import crazypants.enderio.config.recipes.Recipes;
import crazypants.enderio.config.recipes.xml.InvalidRecipeConfigException;
import crazypants.enderio.sound.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonProxy {

  protected long serverTickCount = 0;
  protected long clientTickCount = 0;
  protected final TickTimer tickTimer = new TickTimer();

  public CommonProxy() {
  }

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;
  }

  public void loadIcons() {
  }
  
  public void preInit() {       
  }
  
  public void init() {
    MinecraftForge.EVENT_BUS.register(tickTimer);
    SoundRegistry.init();

    try {
      Recipes recipes = Recipes.fromFile();
      if (recipes.isValid()) {
        recipes.register();
      } else {
        Log.warn("Recipes config file is empty or invalid!");
      }
    } catch (ConversionException e) {
      if (e.getCause() instanceof InvalidRecipeConfigException) {
        Log.warn("Recipes config file is invalid: " + e.getCause().getMessage());
      } else {
        throw e;
      }
    }
  }

  public long getTickCount() {
    return serverTickCount;
  }

  public boolean isAnEiInstalled() {
    return false;
  }

  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
  }

  protected void onServerTick() {
    ++serverTickCount;
  }

  protected void onClientTick() {
  }

  public final class TickTimer {

    @SubscribeEvent
    public void onTick(ServerTickEvent evt) {
      if(evt.phase == Phase.END) {
        onServerTick();
      }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent evt) {
      if(evt.phase == Phase.END) {
        onClientTick();
      }
    }
  }

  public @Nonnull ResourceLocation getGuiTexture(String name) {
    return new ResourceLocation(EnderIO.DOMAIN + ":unknown");
  }

}
