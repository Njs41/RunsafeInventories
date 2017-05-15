package no.runsafe.runsafeinventories;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.api.command.Command;
import no.runsafe.framework.features.*;
import no.runsafe.runsafeinventories.commands.*;
import no.runsafe.runsafeinventories.events.*;
import no.runsafe.runsafeinventories.repositories.InventoryRegionRepository;
import no.runsafe.runsafeinventories.repositories.InventoryRepository;
import no.runsafe.runsafeinventories.repositories.TemplateRepository;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void pluginSetup()
	{
		// Framework features
		addComponent(Commands.class);
		addComponent(Events.class);
		addComponent(Database.class);
		addComponent(UniverseRegistration.class);

		// Handlers
		addComponent(InventoryHandler.class);
		addComponent(UniverseHandler.class);
		addComponent(InventoryHistory.class);
		addComponent(InventoryViewer.class);
		addComponent(RegionInventoryHandler.class);

		// Repositories
		addComponent(InventoryRepository.class);
		addComponent(TemplateRepository.class);
		addComponent(InventoryRegionRepository.class);

		// Commands
		addComponent(WipeWorld.class);

		Command inventoryCommand = new Command("inventory", "Inventory handling commands", null);
		inventoryCommand.addSubCommand(getInstance(DropItems.class));
		inventoryCommand.addSubCommand(getInstance(SwitchInventory.class));
		inventoryCommand.addSubCommand(getInstance(OpenInventory.class));
		inventoryCommand.addSubCommand(getInstance(RestoreInventory.class));
		inventoryCommand.addSubCommand(getInstance(CreateTemplate.class));
		inventoryCommand.addSubCommand(getInstance(ClearInventory.class));
		addComponent(inventoryCommand);

		// Events
		addComponent(InventoryRegionEnter.class);
		addComponent(InventoryRegionExit.class);
		addComponent(PlayerTeleport.class);
		addComponent(PlayerWorldChange.class);
		addComponent(PlayerQuit.class);
		addComponent(PlayerRespawn.class);
		addComponent(PlayerDeath.class);
		addComponent(PlayerRegionEvents.class);
	}
}
