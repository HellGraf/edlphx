package commands.admin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2p.commons.util.GArray;
import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.ai.L2CharacterAI;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.instancemanager.ServerVariables;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Spawn;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.L2WorldRegion;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.model.instances.L2RaidBossInstance;
import l2p.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.NpcTable;
import l2p.gameserver.templates.L2NpcTemplate;

/**
 * This class handles following admin commands: - help path = shows
 * /data/html/admin/path file to char, should not be used by GM's directly
 */
public class AdminServer extends Functions implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_server",
		"admin_gc",
		"admin_pstat",
		"admin_check_actor",
		"admin_find_broken_ai",
		"admin_setvar",
		"admin_set_ai_interval",
		"admin_spawn2"
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

		if(command.equals("admin_server"))
		{
			try
			{
				showHelpPage(activeChar, args);
			}
			catch(Exception e) {}
		}
		else if(command.equals("admin_gc"))
		{
			try
			{
				System.gc();
			}
			catch(Exception e) {}
			activeChar.sendMessage("OK! - garbage collector called.");
		}
		else if(command.equals("admin_pstat"))
		{
			if(args.equals("on") || args.equals("true"))
				activeChar.packetsCount = true;
			else if(args.equals("off") || args.equals("false"))
			{
				activeChar.packetsCount = false;
				activeChar.packetsStat = null;
			}
			else if(activeChar.packetsCount)
			{
				activeChar.packetsCount = false;
				for(Entry<String, Integer> entry : activeChar.packetsStat.entrySet())
					activeChar.sendMessage(entry.getValue() + " : " + entry.getKey());
				activeChar.packetsCount = true;
			}
		}
		else if(command.equals("admin_check_actor"))
		{
			L2Object target = activeChar.getTarget();
			if(target == null)
			{
				activeChar.sendMessage("target == null");
				return false;
			}

			if(!target.isCharacter())
			{
				activeChar.sendMessage("target is not a character");
				return false;
			}

			L2CharacterAI ai = target.getAI();
			if(ai == null)
			{
				activeChar.sendMessage("ai == null");
				return false;
			}

			L2Character actor = ai.getActor();
			if(actor == null)
			{
				activeChar.sendMessage("actor == null");
				return false;
			}

			activeChar.sendMessage("actor: " + actor);
		}
		else if(command.equals("admin_find_broken_ai"))
		{
			for(L2NpcInstance npc : L2ObjectsStorage.getAllNpcsForIterate())
				if(npc.getAI().getActor() != npc)
				{
					activeChar.sendMessage("type 1");
					activeChar.teleToLocation(npc.getLoc());
					return true;
				}
				else if(!npc.isVisible())
				{
					L2WorldRegion region = L2World.getRegion(npc);
					if(region != null && region.getNpcsList(new GArray<L2NpcInstance>(region.getObjectsSize()), 0, npc.getReflection().getId()).contains(npc))
					{
						activeChar.sendMessage("type 2");
						activeChar.teleToLocation(npc.getLoc());
						return true;
					}

					L2WorldRegion currentRegion = npc.getCurrentRegion();
					if(currentRegion != null)
					{
						activeChar.sendMessage("type 3");
						activeChar.teleToLocation(npc.getLoc());
						return true;
					}
				}
				else if(npc.isDead())
					for(AggroInfo aggro : npc.getAggroMap().values())
						if(aggro.damage > 0 && aggro.hate == 0 && aggro.attacker != null && !aggro.attacker.isDead())
						{
							activeChar.sendMessage("type 4");
							activeChar.teleToLocation(npc.getLoc());
							return true;
						}
		}
		else if(command.equals("admin_setvar"))
		{			
			if(wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //setvar key value");
				return false;
			}
			String value = args.substring(wordList[0].length() + 1);
			ServerVariables.set(wordList[0], value);
			activeChar.sendMessage("ServerVariable " + wordList[0] + " cahnged to " + value);
		}
		else if(command.equals("admin_set_ai_interval"))
		{
			int interval = Integer.parseInt(args);
			int count = 0;
			int count2 = 0;
			for(final L2NpcInstance npc : L2ObjectsStorage.getAllNpcsForIterate())
			{
				if(npc == null || npc instanceof L2RaidBossInstance)
					continue;
				final L2CharacterAI char_ai = npc.getAI();
				if(char_ai instanceof DefaultAI)
					try
					{
						final java.lang.reflect.Field field = l2p.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
						field.setAccessible(true);
						field.set(char_ai, interval);

						if(char_ai.isActive())
						{
							char_ai.stopAITask();
							char_ai.teleportHome(true);
							count++;
							L2WorldRegion region = npc.getCurrentRegion();
							if(region != null && !region.areNeighborsEmpty())
							{
								char_ai.startAITask();
								count2++;
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
			activeChar.sendMessage(count + " AI stopped, " + count2 + " AI started");
		}
		else if(command.equals("admin_spawn2")) // Игнорирует запрет на спавн рейдбоссов
		{
			int count = 1;
			int respawn = 30;
			try
			{
				count = Integer.parseInt(wordList[1]);
				 respawn = Integer.parseInt(wordList[2]);
			}
			catch(Exception e) {}
			try
			{
				spawnMonster(activeChar, wordList[0], respawn, count);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("USAGE: //spawn2 id [count] [respawn_time]");
			}
		}

		return true;
	}

	public void setSocial(int npcId, String faction, int range)
	{
		try
		{
			L2NpcTemplate t = NpcTable.getTemplate(npcId);

			java.lang.reflect.Field factionId = t.getClass().getDeclaredField("factionId");
			factionId.setAccessible(true);
			factionId.set(t, faction);

			java.lang.reflect.Field factionRange = t.getClass().getDeclaredField("factionRange");
			factionRange.setAccessible(true);
			factionRange.set(t, (short) range);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// PUBLIC & STATIC so other classes from package can include it directly
	public static void showHelpPage(L2Player targetChar, String filename)
	{
		File file = new File("./", "data/html/admin/" + filename);
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(file);
			byte[] raw = new byte[fis.available()];
			fis.read(raw);

			String content = new String(raw, "UTF-8");

			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

			adminReply.setHtml(content);
			targetChar.sendPacket(adminReply);
		}
		catch(Exception e)
		{}
		finally
		{
			try
			{
				if(fis != null)
					fis.close();
			}
			catch(Exception e1)
			{}
		}
	}

	private void spawnMonster(L2Player activeChar, String monsterId, int respawnTime, int mobCount)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		L2NpcTemplate template;
		if(regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template = NpcTable.getTemplate(monsterTemplate);
		}
		else
		{
			// First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template = NpcTable.getTemplateByName(monsterId);
		}

		if(template == null)
		{
			activeChar.sendMessage("Incorrect monster template.");
			return;
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(target.getLoc());
			spawn.setLocation(0);
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			spawn.setReflection(activeChar.getReflection().getId());
			spawn.init();
			if(respawnTime == 0)
				spawn.stopRespawn();
			activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
