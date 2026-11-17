package commands.admin;


import l2p.commons.database.DatabaseUtils;
import l2p.commons.database.FiltredPreparedStatement;
import l2p.commons.database.L2DatabaseFactory;
import l2p.commons.database.ThreadConnection;
import l2p.commons.util.Util;
import l2p.gameserver.ai.CtrlIntention;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Spawn;
import l2p.gameserver.model.L2Territory;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.NpcTable;
import l2p.gameserver.tables.ReflectionTable;
import l2p.gameserver.templates.L2NpcTemplate;
import l2p.gameserver.util.Location;


public class AdminTeleport implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_show_moves",
		"admin_show_moves_other",
		"admin_show_teleport",
		"admin_teleport_to_character",
		"admin_teleportto",
		"admin_teleport_to",
		"admin_move_to",
		"admin_moveto",
		"admin_teleport_character",
		"admin_recall",
		"admin_walk",
		"admin_recall_npc",
		"admin_gonorth",
		"admin_gosouth",
		"admin_goeast",
		"admin_gowest",
		"admin_goup",
		"admin_godown",
		"admin_tele",
		"admin_teleto",
		"admin_tele_to",
		"admin_failed",
		"admin_tonpc",
		"admin_to_npc",
		"admin_toobject",
		"admin_fix_gh_1",
		"admin_fix_gh_2",
		"admin_fix_gh_r",
		"admin_correct_tvt",
		"admin_setref",
		"admin_getref"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().CanTeleport)
			return false;

		if(command.equals("admin_show_moves"))
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/teleports.htm"));
		else if(command.equals("admin_show_moves_other"))
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/tele/other.htm"));
		else if(command.equals("admin_show_teleport"))
				showTeleportCharWindow(activeChar);
		else if(command.equals("admin_teleport_to_character"))
			teleportToCharacter(activeChar, activeChar.getTarget());
		else if(command.equals("admin_teleport_to") || command.equals("admin_teleportto"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //teleportto charName");
				return false;
			}
			L2Player cha = L2ObjectsStorage.getPlayer(args);
			if(cha == null)
			{
				activeChar.sendMessage("Player '" + args + "' not found in world");
				return false;
			}
			teleportToCharacter(activeChar, cha);
		}
		else if(command.equals("admin_move_to") || command.equals("admin_moveto"))
		{
			if(args.length() == 0)
				{
					activeChar.sendMessage("USAGE: //move_to x y z");
					return false;
				}
				teleportTo(activeChar, activeChar, args);
		}
		else if(command.equals("admin_walk"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //walk x y z");
				return false;
			}
			try
			{
				activeChar.moveToLocation(new Location(args), 0, true);
			}
			catch(IllegalArgumentException e)
			{
				activeChar.sendMessage("USAGE: //walk x y z");
				return false;
			}
		}
		else if(command.equals("admin_gonorth") ||
				command.equals("admin_gosouth") ||
				command.equals("admin_goeast") ||
				command.equals("admin_gowest") ||
				command.equals("admin_goup") ||
				command.equals("admin_godown"))
		{
			int val = args.length() == 0 ? 150 : Integer.parseInt(args);
			int x = activeChar.getX();
			int y = activeChar.getY();
			int z = activeChar.getZ();
			if(command.equals("admin_goup"))
				z += val;
			else if(command.equals("admin_godown"))
				z -= val;
			else if(command.equals("admin_goeast"))
				x += val;
			else if(command.equals("admin_gowest"))
				x -= val;
			else if(command.equals("admin_gosouth"))
				y += val;
			else if(command.equals("admin_gonorth"))
				y -= val;

			activeChar.teleToLocation(x, y, z);
			showTeleportWindow(activeChar);
		}
		else if(command.equals("admin_tele"))
			showTeleportWindow(activeChar);
		else if(command.equals("admin_teleto") || command.equals("admin_tele_to"))
		{
			if(args.equalsIgnoreCase("r"))
				activeChar.setTeleMode(2);
			else if(args.equalsIgnoreCase("end"))
				activeChar.setTeleMode(0);
			else
				activeChar.setTeleMode(1);
		}
		else if(command.equals("admin_failed"))
		{
			activeChar.sendMessage("Trying ActionFailed...");
			activeChar.sendActionFailed();
		}
		else if(command.equals("admin_tonpc") || command.equals("admin_to_npc"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //tonpc npcId|npcName");
				return false;
			}
			L2NpcInstance npc;
			try
			{
				if((npc = L2ObjectsStorage.getByNpcId(Integer.parseInt(args))) != null)
				{
					teleportToCharacter(activeChar, npc);
					return true;
				}
			}
			catch(Exception e)
			{}
			if((npc = L2ObjectsStorage.getNpc(args)) != null)
			{
				teleportToCharacter(activeChar, npc);
				return true;
			}
			activeChar.sendMessage("Npc " + args + " not found");
		}
		else if(command.equals("admin_toobject"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //toobject objectId");
				return false;
			}
			Integer target = Integer.parseInt(args);
			L2Object obj;
			if((obj = L2ObjectsStorage.findObject(target)) != null)
			{
				teleportToCharacter(activeChar, obj);
				return true;
			}
			activeChar.sendMessage("Object " + target + " not found");
		}

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		if(command.equals("admin_teleport_character"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //teleport_character x y z");
				return false;
			}
			teleportCharacter(activeChar, args);
			showTeleportCharWindow(activeChar);
		}
		else if(command.equals("admin_recall"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("USAGE: //recall charName");
				return false;
			}
			L2Player recall_player = L2ObjectsStorage.getPlayer(args);
			if(recall_player != null)
			{
				teleportTo(activeChar, recall_player, activeChar.getLoc());
				return true;
			}
			int obj_id = Util.GetCharIDbyName(args);
			if(obj_id > 0)
			{
				teleportCharacter_offline(obj_id, activeChar.getLoc());
				activeChar.sendMessage(args + " is offline. Offline teleport used...");
			}
			else
				activeChar.sendMessage("->" + args + "<- is incorrect.");
		}
		else if(command.equals("admin_fix_gh_1"))
		{
			L2Territory gh_spawn_loc = null;

			// Зона крафта
			gh_spawn_loc = new L2Territory(10000001);

			gh_spawn_loc.add(45704, 186617, -3480, -3380);
			gh_spawn_loc.add(46086, 186419, -3488, -3388);
			gh_spawn_loc.add(46733, 187506, -3480, -3380);
			gh_spawn_loc.add(46294, 187709, -3480, -3380);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE && player.getReflection().getId() == -2)
				{
					int[] point = gh_spawn_loc.getRandomPoint();
					player.decayMe();
					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона покупки
			gh_spawn_loc = new L2Territory(10000002);

			gh_spawn_loc.add(46091, 186412, -3488, -3388);
			gh_spawn_loc.add(47218, 185902, -3488, -3388);
			gh_spawn_loc.add(47761, 186929, -3480, -3380);
			gh_spawn_loc.add(46742, 187511, -3480, -3380);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY && player.getReflection().getId() == -2)
				{
					int[] point = gh_spawn_loc.getRandomPoint();
					player.decayMe();
					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона продажи
			gh_spawn_loc = new L2Territory(10000003);

			gh_spawn_loc.add(47665, 186755, -3480, -3380);
			gh_spawn_loc.add(48167, 186488, -3480, -3380);
			gh_spawn_loc.add(48397, 186625, -3480, -3380);
			gh_spawn_loc.add(50156, 184674, -3488, -3388);
			gh_spawn_loc.add(49292, 183916, -3488, -3388);
			gh_spawn_loc.add(47758, 185654, -3488, -3388);
			gh_spawn_loc.add(47244, 185894, -3488, -3388);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if((player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE) && player.getReflection().getId() == -2)
				{
					int[] point = gh_spawn_loc.getRandomPoint();
					player.decayMe();
					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}
		}
		else if(command.equals("admin_fix_gh_2"))
		{
			L2Territory gh_spawn_loc2 = null;

			// Зона покупки
			gh_spawn_loc2 = new L2Territory(10000004);

			gh_spawn_loc2.add(46091, 186412, -3488, -3388);
			gh_spawn_loc2.add(47218, 185902, -3488, -3388);
			gh_spawn_loc2.add(47761, 186929, -3480, -3380);
			gh_spawn_loc2.add(46742, 187511, -3480, -3380);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
				{
					int[] point = gh_spawn_loc2.getRandomPoint();

					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона крафта
			gh_spawn_loc2 = new L2Territory(10000005);

			gh_spawn_loc2.add(45704, 186617, -3480, -3380);
			gh_spawn_loc2.add(46086, 186419, -3488, -3388);
			gh_spawn_loc2.add(46733, 187506, -3480, -3380);
			gh_spawn_loc2.add(46294, 187709, -3480, -3380);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
				{
					int[] point = gh_spawn_loc2.getRandomPoint();
					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона продажи
			gh_spawn_loc2 = new L2Territory(10000006);

			gh_spawn_loc2.add(47665, 186755, -3480, -3380);
			gh_spawn_loc2.add(48167, 186488, -3480, -3380);
			gh_spawn_loc2.add(48397, 186625, -3480, -3380);
			gh_spawn_loc2.add(50156, 184674, -3488, -3388);
			gh_spawn_loc2.add(49292, 183916, -3488, -3388);
			gh_spawn_loc2.add(47758, 185654, -3488, -3388);
			gh_spawn_loc2.add(47244, 185894, -3488, -3388);

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
				{
					int[] point = gh_spawn_loc2.getRandomPoint();
					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}
		}
		else if(command.equals("admin_fix_gh_r"))
		{
			int radius = 500;
			if(args.length() > 0)
				radius = Integer.parseInt(args);

			L2Territory spawn_loc = null;

			// Зона покупки
			spawn_loc = new L2Territory(10000004);

			spawn_loc.add(46091, 186412, -3488, -3388);
			spawn_loc.add(47218, 185902, -3488, -3388);
			spawn_loc.add(47761, 186929, -3480, -3380);
			spawn_loc.add(46742, 187511, -3480, -3380);

			for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
				{
					int[] point = spawn_loc.getRandomPoint();

					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона крафта
			spawn_loc = new L2Territory(10000005);

			spawn_loc.add(45704, 186617, -3480, -3380);
			spawn_loc.add(46086, 186419, -3488, -3388);
			spawn_loc.add(46733, 187506, -3480, -3380);
			spawn_loc.add(46294, 187709, -3480, -3380);

			for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
				{
					int[] point = spawn_loc.getRandomPoint();
					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}

			// Зона продажи
			spawn_loc = new L2Territory(10000006);

			spawn_loc.add(47665, 186755, -3480, -3380);
			spawn_loc.add(48167, 186488, -3480, -3380);
			spawn_loc.add(48397, 186625, -3480, -3380);
			spawn_loc.add(50156, 184674, -3488, -3388);
			spawn_loc.add(49292, 183916, -3488, -3388);
			spawn_loc.add(47758, 185654, -3488, -3388);
			spawn_loc.add(47244, 185894, -3488, -3388);

			for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
				if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
				{
					int[] point = spawn_loc.getRandomPoint();
					player.decayMe();

					if(player.getReflection().getId() != -2)
					{
						player.setVar("backCoords", player.getLoc().toXYZString());
						player.setReflection(-2);
					}

					player.setXYZ(point[0], point[1], point[2]);
					player.spawnMe();
				}
		}
		else if(command.equals("admin_setref"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("Usage: //setref <reflection>");
				return false;
			}

			int ref_id = Integer.parseInt(args);
			if(ref_id != 0 && ReflectionTable.getInstance().get(ref_id) == null)
			{
				activeChar.sendMessage("Reflection <" + ref_id + "> not found.");
				return false;
			}

			L2Object target = activeChar;
			L2Object obj = activeChar.getTarget();
			if(obj != null)
				target = obj;

			target.setReflection(ref_id);
			target.decayMe();
			target.spawnMe();
		}
		else if(command.equals("admin_getref"))
		{
			if(args.length() == 0)
			{
				activeChar.sendMessage("Usage: //getref <char_name>");
				return false;
			}
			L2Player cha = L2ObjectsStorage.getPlayer(args);
			if(cha == null)
			{
				activeChar.sendMessage("Player '" + args + "' not found in world");
				return false;
			}
			activeChar.sendMessage("Player '" + args + "' in reflection: " + activeChar.getReflection().getId() + ", name: " + activeChar.getReflection().getName());
		}
		else if(command.equals("admin_correct_tvt"))
		{
			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player != null)
					try
					{
						String var = player.getVar("TvT_backCoords");
						if(var == null || var.equals(""))
							continue;
						String[] coords = var.split(" ");
						if(coords.length != 4)
							continue;
						player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), 0);
						player.unsetVar("TvT_backCoords");
					}
					catch(Exception e)
					{}

			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player != null)
					try
					{
						String var = player.getVar("LastHero_backCoords");
						if(var == null || var.equals(""))
							continue;
						String[] coords = var.split(" ");
						if(coords.length != 4)
							continue;
						player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), 0);
						player.unsetVar("LastHero_backCoords");
					}
					catch(Exception e)
					{}
		}

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		if(command.equals("admin_recall_npc"))
			recallNPC(activeChar);

		return true;
	}

	private void showTeleportWindow(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><title>Teleport Menu</title>");
		replyMSG.append("<body>");

		replyMSG.append("<br>");
		replyMSG.append("<center><table>");

		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

		replyMSG.append("</table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showTeleportCharWindow(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player = null;
		if(target.isPlayer())
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><title>Teleport Character</title>");
		replyMSG.append("<body>");
		replyMSG.append("The character you will teleport is " + player.getName() + ".");
		replyMSG.append("<br>");

		replyMSG.append("Co-ordinate x");
		replyMSG.append("<edit var=\"char_cord_x\" width=110>");
		replyMSG.append("Co-ordinate y");
		replyMSG.append("<edit var=\"char_cord_y\" width=110>");
		replyMSG.append("Co-ordinate z");
		replyMSG.append("<edit var=\"char_cord_z\" width=110>");
		replyMSG.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void teleportTo(L2Player activeChar, L2Player target, String Cords)
	{
		try
		{
			teleportTo(activeChar, target, new Location(Cords));
		}
		catch(IllegalArgumentException e)
		{
			activeChar.sendMessage("You must define 3 coordinates required to teleport");
			return;
		}
	}

	private void teleportTo(L2Player activeChar, L2Player target, Location loc)
	{
		if(!target.equals(activeChar))
			target.sendMessage("Admin is teleporting you.");

		target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		target.teleToLocation(loc, activeChar.getReflection().getId());

		if(target.equals(activeChar))
			activeChar.sendMessage("You have been teleported to " + loc);
	}

	private void teleportCharacter(L2Player activeChar, String Cords)
	{
		L2Object target = activeChar.getTarget();
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		if(target.getObjectId() == activeChar.getObjectId())
		{
			activeChar.sendMessage("You cannot teleport yourself.");
			return;
		}
		teleportTo(activeChar, (L2Player) target, Cords);
	}

	private void teleportCharacter_offline(int obj_id, Location loc)
	{
		if(obj_id == 0)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement st = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET x=?,y=?,z=? WHERE obj_Id=? LIMIT 1");
			st.setInt(1, loc.x);
			st.setInt(2, loc.y);
			st.setInt(3, loc.z);
			st.setInt(4, obj_id);
			st.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, st);
		}
	}

	private void teleportToCharacter(L2Player activeChar, L2Object target)
	{
		if(target == null)
			return;

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		activeChar.teleToLocation(target.getLoc().changeZ(25), target.getReflection().getId());

		activeChar.sendMessage("You have teleported to " + target);
	}

	private void recallNPC(L2Player activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if(obj != null && obj.isNpc())
		{
			L2NpcInstance target = (L2NpcInstance) obj;
			L2Spawn spawn = target.getSpawn();

			int monsterTemplate = target.getTemplate().npcId;

			L2NpcTemplate template1 = NpcTable.getTemplate(monsterTemplate);

			if(template1 == null)
			{
				activeChar.sendMessage("Incorrect monster template.");
				return;
			}

			int respawnTime = spawn.getRespawnDelay();

			target.deleteMe();
			spawn.stopRespawn();

			try
			{
				// L2MonsterInstance mob = new L2MonsterInstance(monsterTemplate,
				// template1);

				spawn = new L2Spawn(template1);
				spawn.setLoc(activeChar.getLoc());
				spawn.setAmount(1);
				spawn.setReflection(activeChar.getReflection().getId());
				spawn.setRespawnDelay(respawnTime);
				spawn.init();

				activeChar.sendMessage("Created " + template1.name + " on " + target.getObjectId() + ".");
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Target is not in game.");
			}

		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
