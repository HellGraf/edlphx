package commands.admin;

import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.entity.vehicle.L2AirShip;
import l2p.gameserver.model.entity.vehicle.L2Vehicle;
import l2p.gameserver.model.entity.vehicle.L2VehicleManager;
import l2p.gameserver.network.serverpackets.ExGetOffAirShip;
import l2p.gameserver.network.serverpackets.ExGetOnAirShip;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.util.Location;

public class AdminBoat implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_boat_reset",
		"admin_boat_reload",
		"admin_airship",
		"admin_airship_enter",
		"admin_airship_invite",
		"admin_airship_remove"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().Menu)
			return false;
		
		String[] wordList = args.split("\\s+");
		
		L2Vehicle boat = null;

		if(command.equals("admin_boat_reset"))
		{
			if(wordList.length == 2)
				boat = L2VehicleManager.getInstance().getBoat(Integer.parseInt(wordList[1]));
			else
				for(L2Character cha : L2World.getAroundCharacters(activeChar))
					if(cha != null && cha.isVehicle())
					{
						boat = (L2Vehicle) cha;
						break;
					}
			if(boat != null)
			{
				boat.despawn();
				boat.spawn();
			}
		}
		else if(command.equals("admin_boat_reload"))
		{
			for(L2Vehicle b : L2VehicleManager.getInstance().getBoats().values())
				if(b != null)
				{
					b.despawn();
					b.deleteMe();
				}
			L2VehicleManager.getInstance().getBoats().clear();
			L2VehicleManager.getInstance().loadShips();
		}
		else if(command.equals("admin_airship"))
		{
			boat = new L2AirShip(activeChar.getClan(), "airship", 0);
			boat.setHeading(activeChar.getHeading());
			boat.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getZ() + 333);
			L2VehicleManager.getInstance().addStaticItem(boat);
			boat.spawn();
		}
		else if(command.equals("admin_airship_enter"))
		{
			for(L2Character cha : L2World.getAroundCharacters(activeChar, 1000, 1000))
				if(cha.isAirShip())
				{
					activeChar._stablePoint = activeChar.getLoc().setH(0);
					L2AirShip airship = (L2AirShip) cha;
					activeChar.setInVehiclePosition(new Location());
					activeChar.setLoc(airship.getLoc());
					activeChar.setVehicle(airship);
					activeChar.broadcastPacket(new ExGetOnAirShip(activeChar, airship, activeChar.getInVehiclePosition()));
					break;
				}
		}
		else if(command.equals("admin_airship_invite"))
		{
			if(activeChar.getVehicle() == null)
				return false;
			
			L2Player target = null;
			L2Object obj = activeChar.getTarget();
			if(obj != null && obj.isPlayer())
			{
				target = (L2Player) obj;
				L2AirShip airship = (L2AirShip) activeChar.getVehicle();
				target.setInVehiclePosition(new Location());
				target.setLoc(airship.getLoc());
				target.setVehicle(airship);
				target.broadcastPacket(new ExGetOnAirShip(target, airship, target.getInVehiclePosition()));
			}
		}
		else if(command.equals("admin_airship_remove"))
		{
			for(L2Character cha : L2World.getAroundCharacters(activeChar, 1000, 1000))
				if(cha.isAirShip())
				{
					L2AirShip airship = (L2AirShip) cha;
					for(L2Player player : L2ObjectsStorage.getAllPlayersForIterate())
						if(player != null && player.getVehicle() == airship)
						{
							activeChar.setVehicle(null);
							activeChar.broadcastPacket(new ExGetOffAirShip(activeChar, airship, activeChar.getLoc()));
							activeChar.teleToLocation(activeChar.getLoc());
						}
					airship.deleteMe();
					break;
				}
		}

		return true;
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
