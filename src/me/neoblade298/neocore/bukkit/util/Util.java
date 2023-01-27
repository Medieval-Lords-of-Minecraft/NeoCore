package me.neoblade298.neocore.bukkit.util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}
