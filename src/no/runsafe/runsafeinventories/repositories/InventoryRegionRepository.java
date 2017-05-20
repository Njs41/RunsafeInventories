package no.runsafe.runsafeinventories.repositories;

import no.runsafe.framework.api.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryRegionRepository extends Repository
{
	@Override
	public String getTableName()
	{
		return "runsafe_inventories_regions";
	}

	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
				"CREATE TABLE `runsafe_inventories_regions` (" +
						"`worldName` varchar(50) NOT NULL, " +
						"`regionName` varchar(50) NOT NULL," +
						"PRIMARY KEY (`worldName`)" +
						")"
		);

		return update;
	}

	/**
	 * Creates a new inventory region.
	 * @param worldName The world the region is in.
	 * @param regionName The region to add.
	 */
	public void setInventoryRegion(String worldName, String regionName)
	{
		database.execute(
			"INSERT INTO runsafe_inventories_region (`worldName`,`regionName`) VALUES (?,?)",
			worldName, regionName
		);
	}

	/**
	 * Removes a specific inventory region.
	 * @param worldName The world the region is in.
	 * @param regionName The region to remove.
	 */
	public void removeInventoryRegion(String worldName, String regionName)
	{
		database.execute(
			"DELETE FROM runsafe_inventories_regions WHERE `worldName`=? AND `regionName`=?",
			worldName, regionName
		);
	}

	/**
	 * Gets all the inventory regions in a world.
	 * @param worldName The world to get regions from.
	 * @return A list of all regions in the specified world.
	 */
	public List<String> getInventoryRegionsInWorld(String worldName)
	{
		return database.queryStrings(
			"SELECT DISTINCT `regionName` FROM runsafe_inventories_regions WHERE worldName=?",
			worldName
		);
	}

	/**
	 * Checks if a region is an inventory region.
	 * @param worldName The world the region is in.
	 * @param regionName The region to check.
	 * @return True if the region is a region inventory.
	 */
	public boolean isInventoryRegion(String worldName, String regionName)
	{
		return database.queryString(
			"SELECT `regionName` FROM runsafe_inventories_regions WHERE `worldName`=? AND `regionName`=?",
			worldName, regionName
		) != null;
	}

	public HashMap<String, List<String>> getInventoryRegions()
	{
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();

		ISet result = database.query("SELECT `worldName`, `regionName` FROM runsafe_inventories_regions");
		for (IRow row : result)
		{
			String worldName = row.String("worldName");
			if (!map.containsKey(worldName))
				map.put(worldName, new ArrayList<String>(1));

			map.get(worldName).add(row.String("regionName"));
		}

		return map;
	}
}
