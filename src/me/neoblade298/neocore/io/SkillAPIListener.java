package me.neoblade298.neocore.io;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sucy.skill.api.event.PlayerLoadCompleteEvent;
import com.sucy.skill.api.event.PlayerSaveEvent;

public class SkillAPIListener implements Listener {

	
	@EventHandler
	public void onSAPISave(PlayerSaveEvent e) {
		IOManager.save(e.getPlayer());
	}
	
	@EventHandler
	public void onSkillAPILoad(PlayerLoadCompleteEvent e) {
		IOManager.load(e.getPlayer());
	}
}
