package no.runsafe.runsafeinventories;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.player.IPlayerCustomEvent;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.runsafeinventories.events.InventoryRegionEnter;
import no.runsafe.runsafeinventories.events.InventoryRegionExit;
import no.runsafe.runsafeinventories.repositories.InventoryRegionRepository;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionInventoryHandler implements IConfigurationChanged, IPlayerCustomEvent
{
	/**
	 * Constructor for RegionInventoryHandler
	 * @param inventoryRegionRepository Inventory region repository instance.
	 * @param worldGuard WorldGuard bridge instance.
	 */
	public RegionInventoryHandler(InventoryRegionRepository inventoryRegionRepository, WorldGuardInterface worldGuard)
	{
		this.inventoryRegionRepository = inventoryRegionRepository;
		this.worldGuard = worldGuard;
	}

	/**
	 * Check if the player is currently in an inventory region.
	 * @param player The player to check for.
	 * @return The name of the region if one exists, otherwise null.
	 */
	public String getPlayerInventoryRegion(IPlayer player)
	{
		String worldName = player.getWorldName(); // Name of the world the player is in.
		List<String> regions = worldGuard.getApplicableRegions(player); // Get region list.

		// Check each region and see if we have an inventory associated with any.
		for (String region : regions)
			if (doesRegionHaveInventory(worldName, region))
				return region;

		return null;
	}

	/**
	 * Check if a region has a separate inventory associated with it.
	 * @param worldName Name of the world the region is in.
	 * @param regionName Name of the region to check.
	 * @return True if the region has it's own inventory associated with it.
	 */
	public boolean doesRegionHaveInventory(String worldName, String regionName)
	{
		return inventoryRegions.containsKey(worldName) && inventoryRegions.get(worldName).contains(regionName);
	}

	/**
	 * Prevent the player's current regions from triggering entry events.
	 * @param player The player to perform this on.
	 */
	public void blacklistCurrentRegionsEntry(IPlayer player)
	{
		// Get all current regions and blacklist their entry events.
		for (String region : worldGuard.getApplicableRegions(player))
			ignoreEntryEventRegions.add(getRegionKey(player, region));
	}

	/**
	 * Prevent the player's current regions from triggering exit events.
	 * @param player The player to perform this on.
	 */
	public void blacklistCurrentRegionsExit(IPlayer player)
	{
		// Get all current regions and blacklist their exit events.
		for (String region : worldGuard.getApplicableRegions(player))
			ignoreExitEventRegions.add(getRegionKey(player, region));
	}

	/**
	 * Sets a region as an inventory region.
	 * @param worldName Name of the world the region is in.
	 * @param regionName Name of the region.
	 */
	public void setInventoryRegion(String worldName, String regionName)
	{
		inventoryRegionRepository.setInventoryRegion(worldName, regionName);
	}

	/**
	 * Removes an inventory region.
	 * Region will still exist, it just won't be an inventory region anymore.
	 * @param worldName Name of the world the region is in.
	 * @param regionName Name of the region.
	 */
	public void removeInventoryRegion(String worldName, String regionName)
	{
		inventoryRegionRepository.removeInventoryRegion(worldName, regionName);
	}

	/**
	 * Remove a region from the ignore list for a player, allowing it to trigger events again.
	 * @param player The player who the event should be pardoned for.
	 * @param region The region which should no longer be ignored.
	 */
	private void removeIgnoredRegionEntry(IPlayer player, String region)
	{
		// Remove the region from the ignore list.
		ignoreEntryEventRegions.remove(getRegionKey(player, region));
	}

	/**
	 * Remove a region from the ignore list for a player, allowing it to trigger exit events again.
	 * @param player The player who the event should be ignored for.
	 * @param region The region which should no longer be ignored.
	 */
	private void removeIgnoredRegionExit(IPlayer player, String region)
	{
		// Remove the region from the ignore list.
		ignoreExitEventRegions.remove(getRegionKey(player, region));
	}

	/**
	 * Check if entry events for a specific region are currently ignored.
	 * @param player The player to check the event for.
	 * @param region The region to check the event for.
	 * @return True if entry events for the specified region are being ignored.
	 */
	private boolean isRegionEntryIgnored(IPlayer player, String region)
	{
		return ignoreEntryEventRegions.contains(getRegionKey(player, region));
	}

	/**
	 * Check if exit events for a specific region are currently ignored.
	 * @param player The player to check the event for.
	 * @param region The region to check the event for,
	 * @return True if the event events for the specified region are being ignored.
	 */
	private boolean isRegionExitIgnored(IPlayer player, String region)
	{
		return ignoreExitEventRegions.contains(getRegionKey(player, region));
	}

	/**
	 * Present a single string representing a player and world specific region.
	 * @param player The player to associate this key with.
	 * @param region The region to associate this key with.
	 * @return A combined key for the player, world and region.
	 */
	private String getRegionKey(IPlayer player, String region)
	{
		return String.format("%s-%s-%s", player.getName(), player.getWorldName(), region);
	}

	/**
	 * Called when the configuration for this plug-in is changed.
	 * @param configuration Object for accessing this plug-ins configuration.
	 */
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		inventoryRegions = inventoryRegionRepository.getInventoryRegions();
	}

	/**
	 * Called when a custom event is fired from within the framework.
	 * @param event Object presenting event related data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void OnPlayerCustomEvent(RunsafeCustomEvent event)
	{
		String eventName = event.getEvent();

		if (eventName.equals("region.enter") || eventName.equals("region.leave"))
		{
			Map<String, String> data = (Map<String, String>) event.getData();
			IPlayer player = event.getPlayer();
			String region = data.get("region");

			if (eventName.equals("region.enter"))
			{
				if (!isRegionEntryIgnored(player, region))
				{
					// Fire an inventory region enter event.
					new InventoryRegionEnter(player, region).Fire();
				}
				else
				{
					// The region is ignored, so let's remove it so next time it isn't ignored.
					removeIgnoredRegionEntry(player, region);
				}
			}
			else if (eventName.equals("region.leave"))
			{
				// Handle region leaving.
				if (!isRegionExitIgnored(player, region))
				{
					// Fire an inventory region exit event.
					new InventoryRegionExit(player, region).Fire();
				}
				else
				{
					// The region is ignored, so let's remove it so next time it isn't ignored.
					removeIgnoredRegionExit(player, region);
				}
			}
		}
	}

	private final InventoryRegionRepository inventoryRegionRepository;
	private List<String> ignoreEntryEventRegions = new ArrayList<String>();
	private List<String> ignoreExitEventRegions = new ArrayList<String>();
	private HashMap<String, List<String>> inventoryRegions = new HashMap<String, List<String>>();
	private final WorldGuardInterface worldGuard;
}
