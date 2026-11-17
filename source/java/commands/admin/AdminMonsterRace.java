package commands.admin;

import l2p.commons.threading.ThreadPoolManager;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.entity.MonsterRace;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.network.serverpackets.DeleteObject;
import l2p.gameserver.network.serverpackets.MonRaceInfo;
import l2p.gameserver.network.serverpackets.PlaySound;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.util.Location;

public class AdminMonsterRace implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_mons"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	protected static int state = -1;

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(command.equals("admin_mons"))
		{
			if(!activeChar.getPlayerAccess().MonsterRace)
				return false;
			handleSendPacket(activeChar);
		}

		return true;
	}

	private void handleSendPacket(L2Player activeChar)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race
		 * -1 0 to end the race
		 *
		 * 8003 to 8027
		 */

		int[][] codes = { { -1, 0 }, { 0, 15322 }, { 13765, -1 }, { -1, 0 } };
		MonsterRace race = MonsterRace.getInstance();

		if(state == -1)
		{
			state++;
			race.newRace();
			race.newSpeeds();
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
		}
		else if(state == 0)
		{
			state++;
			activeChar.sendPacket(Msg.THEYRE_OFF);
			activeChar.broadcastPacket(new PlaySound("S_Race"));
			activeChar.broadcastPacket(new PlaySound(0, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559)));
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));

			ThreadPoolManager.getInstance().scheduleGeneral(new RunRace(codes, activeChar), 5000);
		}
	}

	class RunRace implements Runnable
	{
		private int[][] codes;
		private L2Player activeChar;

		public RunRace(int[][] codes, L2Player activeChar)
		{
			this.codes = codes;
			this.activeChar = activeChar;
		}

		public void run()
		{
			// int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
			// MonsterRace.getInstance().newSpeeds();
			// int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
			/*
			 * int[] speed = new int[8]; for(int i=0; i<8; i++) { for(int j=0; j<20;
			 * j++) { //System.out.println("Adding "+speeds1[i][j] +" and "+
			 * speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); }
			 * System.out.println("Total speed for "+(i+1)+" = "+speed[i]); }
			 */

			activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds()));
			ThreadPoolManager.getInstance().scheduleGeneral(new RunEnd(activeChar), 30000);
		}
	}

	class RunEnd implements Runnable
	{
		private L2Player activeChar;

		public RunEnd(L2Player activeChar)
		{
			this.activeChar = activeChar;
		}

		public void run()
		{
			L2NpcInstance obj;

			for(int i = 0; i < 8; i++)
			{
				obj = MonsterRace.getInstance().getMonsters()[i];
				// FIXME i don't know, if it's needed (Styx)
				// L2World.removeObject(obj);
				activeChar.broadcastPacket(new DeleteObject(obj));

			}
			state = -1;
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}