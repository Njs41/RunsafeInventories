package no.runsafe.runsafeinventories;

import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;
import java.util.UUID;

public class InventoryHistory
{
	public void save(IPlayer player)
	{
		this.history.put(player.getUniqueId(), player.getInventory().serialize());
	}

	public boolean restore(IPlayer player)
	{
		if (this.history.containsKey(player.getUniqueId()))
		{
			player.getInventory().unserialize(this.history.get(player.getUniqueId()));
			return true;
		}
		return false;
	}

	private final HashMap<UUID, String> history = new HashMap<UUID, String>();
}
