package me.neoblade298.neocore.velocity.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.neoblade298.neocore.velocity.VelocityCore;
import me.neoblade298.neocore.velocity.util.Util;
import me.neoblade298.neocore.shared.proxy.ProxyMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class VelocityListener {
    @Subscribe
    public void onJoin(PostLoginEvent e) {
    	VelocityCore.proxy().getScheduler().buildTask(VelocityCore.inst(), () -> {
    		VelocityCore.sendMotd(e.getPlayer());
		}).delay(VelocityCore.motdDelaySeconds(), TimeUnit.SECONDS);
    }
    
    @Subscribe
    public void onBungeeMessage(PluginMessageEvent e) {
		if (!VelocityCore.IDENTIFIER.equals(e.getIdentifier())) {
			return;
		}
		e.setResult(PluginMessageEvent.ForwardResult.handled());
		if (!(e.getSource() instanceof ServerConnection backend)) {
			VelocityCore.logger().warning("[NeoCore] Rejected proxy message from a non-server source");
			return;
		}
		try {
			ProxyMessage message = ProxyMessage.decode(e.getData());
			switch (message.type()) {
			case "broadcast":
				requireFields(message, 1);
				Util.broadcastRaw(JSONComponentSerializer.json().deserialize(message.fields().get(0)));
				break;
			case "command":
				requireFields(message, 1);
				VelocityCore.proxy().getCommandManager()
						.executeImmediatelyAsync(VelocityCore.proxy().getConsoleCommandSource(), message.fields().get(0))
						.exceptionally(ex -> {
							VelocityCore.logger().log(Level.WARNING, "Failed to execute backend proxy command", ex);
							return null;
						});
				break;
			case "connect":
				handleConnect(message);
				break;
			case "forward":
				handleForwardMessage(backend, message);
				break;
			default:
				VelocityCore.logger().warning("[NeoCore] Rejected unknown proxy message type " + message.type());
			}
		}
		catch (IllegalArgumentException ex) {
			VelocityCore.logger().log(Level.WARNING, "[NeoCore] Rejected malformed proxy message from "
					+ backend.getServerInfo().getName(), ex);
		}
    }
    
    private void handleConnect(ProxyMessage message) {
		requireFields(message, 2);
		UUID playerId = UUID.fromString(message.fields().get(0));
		Player player = VelocityCore.proxy().getPlayer(playerId)
				.orElseThrow(() -> new IllegalArgumentException("Unknown player " + playerId));
		RegisteredServer server = VelocityCore.proxy().getServer(message.fields().get(1))
				.orElseThrow(() -> new IllegalArgumentException("Unknown server " + message.fields().get(1)));
		player.createConnectionRequest(server).connect().whenComplete((result, error) -> {
			if (error != null) {
				VelocityCore.logger().log(Level.WARNING, "[NeoCore] Failed to connect " + player.getUsername()
						+ " to " + server.getServerInfo().getName(), error);
			}
			else if (!result.isSuccessful()) {
				VelocityCore.logger().warning("[NeoCore] Failed to connect " + player.getUsername() + " to "
						+ server.getServerInfo().getName() + ": " + result.getStatus());
			}
		});
    }

    private void handleForwardMessage(ServerConnection backend, ProxyMessage message) {
		List<String> fields = message.fields();
		if (fields.size() < 4) {
			throw new IllegalArgumentException("Forward message requires at least four fields");
		}
		if (!fields.get(0).equalsIgnoreCase("true") && !fields.get(0).equalsIgnoreCase("false")) {
			throw new IllegalArgumentException("Invalid forward queue flag");
		}
		boolean queue = Boolean.parseBoolean(fields.get(0));
		int targetCount;
		try {
			targetCount = Integer.parseInt(fields.get(1));
		}
		catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid forward target count", ex);
		}
		if (targetCount < 1 || fields.size() < targetCount + 3) {
			throw new IllegalArgumentException("Invalid forward target list");
		}
		List<String> targets = fields.subList(2, 2 + targetCount);
		String channel = fields.get(2 + targetCount);
		String[] payload = fields.subList(3 + targetCount, fields.size()).toArray(String[]::new);
		String[] outgoing = new String[payload.length + 1];
		outgoing[0] = channel;
		System.arraycopy(payload, 0, outgoing, 1, payload.length);

		Iterable<RegisteredServer> destinations;
		if (targets.stream().anyMatch(target -> target.equalsIgnoreCase("ALL"))) {
			destinations = VelocityCore.proxy().getAllServers();
		}
		else if (targets.stream().anyMatch(target -> target.equalsIgnoreCase("OTHER"))) {
			destinations = getAllOtherServers(backend.getServerInfo().getName());
		}
		else {
			ArrayList<RegisteredServer> resolved = new ArrayList<>();
			for (String target : targets) {
				VelocityCore.proxy().getServer(target).ifPresentOrElse(resolved::add,
						() -> VelocityCore.logger().warning("[NeoCore] Cannot forward to unknown server " + target));
			}
			destinations = resolved;
		}
		VelocityCore.sendPluginMessage(destinations, outgoing, queue);
    }

    private List<RegisteredServer> getAllOtherServers(String except) {
    	ProxyServer proxy = VelocityCore.proxy();
		return proxy.getAllServers().stream()
				.filter(server -> !server.getServerInfo().getName().equalsIgnoreCase(except))
				.toList();
    }

	@Subscribe
	public void onServerPostConnect(ServerPostConnectEvent event) {
		event.getPlayer().getCurrentServer().ifPresent(connection ->
				VelocityCore.flushQueuedMessages(connection.getServer()));
	}

	private void requireFields(ProxyMessage message, int count) {
		if (message.fields().size() != count) {
			throw new IllegalArgumentException("Proxy message " + message.type() + " expected " + count
					+ " fields but received " + message.fields().size());
		}
    }
}
