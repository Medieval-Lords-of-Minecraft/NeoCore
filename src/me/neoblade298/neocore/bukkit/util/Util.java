package me.neoblade298.neocore.bukkit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.neoblade298.neocore.bukkit.NeoCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Util {
	private static Component prefix;
	
	static {
		prefix = Component.text("[", NamedTextColor.RED)
				.append(Component.text("MLMC", NamedTextColor.DARK_RED, TextDecoration.BOLD))
				.append(Component.text("]", NamedTextColor.RED));
	}
	
	public static void msgGroup(Collection<Player> s, Component msg, boolean hasPrefix) {
		for (CommandSender sender : s) {
			msg(sender, msg, hasPrefix);
		}
	}
	
	public static void msgGroup(Collection<Player> s, Component msg) {
		msgGroup(s, msg, true);
	}
	
	public static void msgGroupRaw(Collection<Player> s, Component msg) {
		msgGroup(s, msg, false);
	}

	public static void msgRaw(CommandSender s, Component msg) {
		msg(s, msg, false);
	}

	public static void msg(CommandSender s, Component msg) {
		msg(s, msg, true);
	}

	public static void msg(CommandSender s, Component msg, boolean hasPrefix) {
		s.sendMessage(hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY));
	}
	
	public static void broadcast(Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
		}
	}

	public static Location stringToLoc(String loc) {
		String args[] = loc.split(" ");
		World w = Bukkit.getWorld(args[0]);
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[3]);
		float yaw = 0;
		float pitch = 0;
		if (args.length > 4) {
			yaw = Float.parseFloat(args[4]);
			pitch = Float.parseFloat(args[5]);
		}
		return new Location(w, x, y, z, yaw, pitch);
	}

	public static String locToString(Location loc, boolean round, boolean includePitch) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		String str = loc.getWorld().getName() + " " + x + " " + y + " " + z;
		if (round) {
			x = Math.round(x) + 0.5;
			y = Math.round(y) + 0.5;
			z = Math.round(z) + 0.5;
		}
		if (includePitch) {
			str += " " + loc.getYaw() + " " + loc.getPitch();
		}
		return str;
	}
	
	
    public static String toBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the array
            dataOutput.writeInt(items.length);
            
            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] fromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
    
            // Read the serialized array
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
    
    public static BukkitTask runTask(Runnable runnable, long delay) {
    	return new BukkitRunnable() {
    		public void run() {
    			runnable.run();
    		}
    	}.runTaskLater(NeoCore.inst(), delay);
    }
    
    public static void playSound(Player p, Sound sound, float volume, float pitch, boolean showAllPlayers) {
    	if (showAllPlayers) {
    		p.playSound(p, sound, volume, pitch);
    	}
    	else {
    		p.getWorld().playSound(p, sound, volume, pitch);
    	}
    }
    
    public static void playSound(Player p, Location loc, Sound sound, float volume, float pitch, boolean showAllPlayers) {
    	if (showAllPlayers) {
    		p.playSound(loc, sound, volume, pitch);
    	}
    	else {
    		loc.getWorld().playSound(loc, sound, volume, pitch);
    	}
    }

	public static void displayError(Player p, String error) {
		p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.7F);
		Util.msgRaw(p, Component.text(error, NamedTextColor.RED));
	}
	
}
