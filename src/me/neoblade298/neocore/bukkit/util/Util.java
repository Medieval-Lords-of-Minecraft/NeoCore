package me.neoblade298.neocore.bukkit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import me.neoblade298.neocore.shared.util.SharedUtil;

public class Util {
	public static void msgGroup(Collection<Player> s, String msg, boolean hasPrefix) {
		for (CommandSender sender : s) {
			msg(sender, msg, hasPrefix);
		}
	}
	
	public static void msgGroup(Collection<Player> s, String msg) {
		for (CommandSender sender : s) {
			msg(sender, msg);
		}
	}

	public static void msg(CommandSender s, String msg) {
		msg(s, msg, true);
	}

	public static void msg(CommandSender s, String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		s.sendMessage(SharedUtil.translateColors(msg));
	}

	public static void msgCentered(CommandSender s, String msg) {
		s.sendMessage(SharedUtil.center(msg));
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
	
}
