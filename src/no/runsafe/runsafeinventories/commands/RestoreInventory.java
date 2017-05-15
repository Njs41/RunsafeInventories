package no.runsafe.runsafeinventories.commands;

import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.runsafeinventories.InventoryHistory;

public class RestoreInventory extends ExecutableCommand
{
	public RestoreInventory(InventoryHistory history)
	{
		super(
			"restore", "Reverts the last inventory switch/deletion", "runsafe.inventories.restore",
			new Player().defaultToExecutor()
		);
		this.history = history;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		IPlayer player = parameters.getValue("player");
		if (player == null)
			return "&cInvalid player.";

		if (!this.history.restore(player))
			return "&cThere is no stored restoration data for this players inventory.";

		player.updateInventory();
		return "The inventory for " + player.getPrettyName() + " has been restored.";
	}

	private final InventoryHistory history;
}
