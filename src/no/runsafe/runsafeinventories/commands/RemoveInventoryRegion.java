package no.runsafe.runsafeinventories.commands;

import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.argument.WorldArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.runsafeinventories.RegionInventoryHandler;

public class RemoveInventoryRegion extends PlayerCommand
{
	public RemoveInventoryRegion(RegionInventoryHandler regionInventoryHandler)
	{
		super("setinventoryregion",
			"Sets the selected inventory as an inventory region.",
			"runsafe.inventories.region.create",
			new WorldArgument(WORLD).require(),
			new RequiredArgument(REGION)
		);
		this.regionInventoryHandler = regionInventoryHandler;
	}

	private static final String WORLD = "world";
	private static final String REGION = "region";

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		regionInventoryHandler.setInventoryRegion(((IWorld) parameters.getValue(WORLD)).getName(), parameters.getValue(REGION));
		return "Not implemented yet.";
	}

	private final RegionInventoryHandler regionInventoryHandler;
}
