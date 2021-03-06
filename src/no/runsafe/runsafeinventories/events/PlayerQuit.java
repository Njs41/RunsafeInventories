package no.runsafe.runsafeinventories.events;

import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;
import no.runsafe.runsafeinventories.InventoryHandler;

public class PlayerQuit implements IPlayerQuitEvent
{
	public PlayerQuit(InventoryHandler inventoryHandler)
	{
		this.inventoryHandler = inventoryHandler;
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		// Save on logout in-case we need to use the data during maintenance.
		this.inventoryHandler.saveInventory(event.getPlayer());
	}

	private final InventoryHandler inventoryHandler;
}