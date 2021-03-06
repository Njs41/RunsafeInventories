package no.runsafe.runsafeinventories.events;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.player.IPlayerPortalEvent;
import no.runsafe.framework.api.event.player.IPlayerTeleportEvent;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerPortalEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerTeleportEvent;
import no.runsafe.runsafeinventories.InventoryHandler;
import no.runsafe.runsafeinventories.RegionInventoryHandler;

public class PlayerTeleport implements IPlayerTeleportEvent, IPlayerPortalEvent
{
	public PlayerTeleport(InventoryHandler inventoryHandler, RegionInventoryHandler regionInventoryHandler, IDebug output)
	{
		this.inventoryHandler = inventoryHandler;
		this.regionInventoryHandler = regionInventoryHandler;
		this.debugger = output;
	}

	@Override
	public void OnPlayerTeleport(RunsafePlayerTeleportEvent event)
	{
		this.debugger.debugFine("Detected teleport event: " + event.getPlayer().getName());
		ILocation from = event.getFrom();
		ILocation to = event.getTo();
		this.checkTeleportEvent(to == null ? null : to.getWorld(), from == null ? null : from.getWorld(), event.getPlayer());
	}

	@Override
	public void OnPlayerPortalEvent(RunsafePlayerPortalEvent event)
	{
		if (event.isCancelled())
			return;

		this.debugger.debugFine("Detected portal event: " + event.getPlayer().getName());
		ILocation from = event.getFrom();
		ILocation to = event.getTo();
		this.checkTeleportEvent(to == null ? null : to.getWorld(), from == null ? null : from.getWorld(), event.getPlayer());
	}

	private void checkTeleportEvent(IWorld to, IWorld from, IPlayer player)
	{
		String goingFrom = null;
		String goingTo = null;
		if (from != null)
			goingFrom = String.format("%s/%s", from.getName(), from.getUniverse().getName());

		if (to != null)
			goingTo = String.format("%s/%s", to.getName(), to.getUniverse().getName());

		debugger.debugFine("Going from %s to %s", goingFrom, goingTo);
		if (to != null && from != null && !to.equals(from))
		{
			regionInventoryHandler.blacklistCurrentRegionsExit(player); // Blacklist current region exit events.
			inventoryHandler.handlePreWorldChange(player);
		}
	}

	private final InventoryHandler inventoryHandler;
	private final RegionInventoryHandler regionInventoryHandler;
	private final IDebug debugger;
}
