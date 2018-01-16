package net.earthcomputer.clientcommands;

import java.util.Map;

import net.earthcomputer.clientcommands.command.CommandAbort;
import net.earthcomputer.clientcommands.command.CommandCClear;
import net.earthcomputer.clientcommands.command.CommandCGive;
import net.earthcomputer.clientcommands.command.CommandCHelp;
import net.earthcomputer.clientcommands.command.CommandCalc;
import net.earthcomputer.clientcommands.command.CommandFind;
import net.earthcomputer.clientcommands.command.CommandFindBlock;
import net.earthcomputer.clientcommands.command.CommandFindItem;
import net.earthcomputer.clientcommands.command.CommandLook;
import net.earthcomputer.clientcommands.command.CommandNote;
import net.earthcomputer.clientcommands.command.CommandRelog;
import net.earthcomputer.clientcommands.command.CommandSimGen;
import net.earthcomputer.clientcommands.command.CommandTempRule;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameRules;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ClientCommandsMod.MODID, version = ClientCommandsMod.VERSION, clientSideOnly = true)
public class ClientCommandsMod {
	public static final String MODID = "clientcommands";
	public static final String VERSION = "1.0";

	@Instance(MODID)
	public static ClientCommandsMod INSTANCE;

	private GameRules tempRules;

	@NetworkCheckHandler
	public boolean checkConnect(Map<String, String> mods, Side otherSide) {
		return true;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		initTempRules();
		registerCommands();
		registerEventStuff();
	}

	private void initTempRules() {
		tempRules = new GameRules();
		Map<String, ?> rules = ReflectionHelper.getPrivateValue(GameRules.class, tempRules, "rules", "field_82771_a");
		rules.clear();
		resetTempRules();
		EventManager.addDisconnectListener(e -> resetTempRules());
	}

	private void registerCommands() {
		ClientCommandHandler.instance.registerCommand(new CommandFind());
		ClientCommandHandler.instance.registerCommand(new CommandFindBlock());
		ClientCommandHandler.instance.registerCommand(new CommandFindItem());
		ClientCommandHandler.instance.registerCommand(new CommandRelog());
		ClientCommandHandler.instance.registerCommand(new CommandLook());
		ClientCommandHandler.instance.registerCommand(new CommandCalc());
		ClientCommandHandler.instance.registerCommand(new CommandCHelp());
		ClientCommandHandler.instance.registerCommand(new CommandCClear());
		ClientCommandHandler.instance.registerCommand(new CommandCGive());
		ClientCommandHandler.instance.registerCommand(new CommandAbort());
		ClientCommandHandler.instance.registerCommand(new CommandNote());
		ClientCommandHandler.instance.registerCommand(new CommandTempRule());
		ClientCommandHandler.instance.registerCommand(new CommandSimGen());
	}

	private void registerEventStuff() {
		EventManager.addTickListener(e -> {
			if (Minecraft.getMinecraft().inGameHasFocus) {
				try {
					double reachDistance = Double.parseDouble(tempRules.getString("blockReachDistance"));
					Minecraft.getMinecraft().player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE)
							.setBaseValue(reachDistance);
				} catch (NumberFormatException e1) {
				}
			}
		});

		GuiBetterEnchantment.registerEvents();

		MinecraftForge.EVENT_BUS.register(EventManager.INSTANCE);
	}

	public void resetTempRules() {
		tempRules.addGameRule("enchantingPrediction", "false", GameRules.ValueType.BOOLEAN_VALUE);
		tempRules.addGameRule("blockReachDistance", "default", GameRules.ValueType.ANY_VALUE);
	}

	public GameRules getTempRules() {
		return tempRules;
	}
}
