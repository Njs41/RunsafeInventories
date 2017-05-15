package no.runsafe.runsafeinventories.commands;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventory;
import no.runsafe.runsafeinventories.InventoryHistory;

public class SwitchInventory extends PlayerCommand
{
	public SwitchInventory(InventoryHistory history)
	{
		super(
			"switch", "Moves a players inventory to the target.", "runsafe.inventories.switch",
			new Player("source").require(), new Player("target").defaultToExecutor()
		);
		this.history = history;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer source = parameters.getValue("source");
		IPlayer target = parameters.getValue("target");

		if (source == null || target == null)
			return "&cInvalid player(s).";

		RunsafeInventory targetInventory = target.getInventory();
		RunsafeInventory sourceInventory = source.getInventory();

		if (sourceInventory == null || targetInventory == null)
			return "&cInvalid inventory.";

		this.history.save(target);
		this.history.save(source);

		targetInventory.clear();
		targetInventory.unserialize(sourceInventory.serialize());

		sourceInventory.clear();

		source.updateInventory();
		target.updateInventory();

		return String.format("Inventory of %s moved to %s.", source.getPrettyName(), target.getPrettyName());
	}

	private final InventoryHistory history;
}
