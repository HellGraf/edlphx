package commands.admin;

import l2p.commons.util.GArray;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Effect;
import l2p.gameserver.model.L2Object;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2World;
import l2p.gameserver.model.L2WorldRegion;
import l2p.gameserver.model.instances.L2NpcInstance;
import l2p.gameserver.network.serverpackets.Earthquake;
import l2p.gameserver.network.serverpackets.NpcInfo;
import l2p.gameserver.network.serverpackets.SocialAction;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.skills.AbnormalEffect;
import l2p.gameserver.tables.SkillTable;

public class AdminEffects implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_invis",
		"admin_vis",
		"admin_offline_vis",
		"admin_offline_invis",
		"admin_earthquake",
		"admin_unpara_all",
		"admin_para_all",
		"admin_unpara",
		"admin_para",
		"admin_changename",
		"admin_gmspeed",
		"admin_invul",
		"admin_setinvul",
		"admin_getinvul",
		"admin_social",
		"admin_abnormal",
		"admin_transform"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(!activeChar.getPlayerAccess().GodMode)
			return false;

		int val;
		AbnormalEffect ae = AbnormalEffect.NULL;
		L2Object target = activeChar.getTarget();

		if(command.equals("admin_invis") || command.equals("admin_vis"))
		{
			if(activeChar.isInvisible())
			{
				activeChar.setInvisible(false);
				activeChar.broadcastUserInfo(true);
				if(activeChar.getPet() != null)
					activeChar.getPet().broadcastPetInfo();
			}
			else
			{
				activeChar.setInvisible(true);
				activeChar.sendUserInfo(true);
				if(activeChar.getCurrentRegion() != null)
					for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
						neighbor.removePlayerFromOtherPlayers(activeChar);
			}
		}
		else if(command.equals("admin_gmspeed"))
		{
			if(args.length() == 0)
				val = 0;
			else
				try
				{
					val = Integer.parseInt(args);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //gmspeed value=[0..4]");
					return false;
				}
			GArray<L2Effect> superhaste = activeChar.getEffectList().getEffectsBySkillId(7029);
			int sh_level = superhaste == null ? 0 : superhaste.isEmpty() ? 0 : superhaste.get(0).getSkill().getLevel();

			if(val == 0)
			{
				if(sh_level != 0)
					activeChar.doCast(SkillTable.getInstance().getInfo(7029, sh_level), activeChar, true); //снимаем еффект
				activeChar.unsetVar("gm_gmspeed");
			}
			else if(val >= 1 && val <= 4)
			{
				if(Config.SAVE_GM_EFFECTS)
					activeChar.setVar("gm_gmspeed", String.valueOf(val));
				if(val != sh_level)
				{
					if(sh_level != 0)
						activeChar.doCast(SkillTable.getInstance().getInfo(7029, sh_level), activeChar, true); //снимаем еффект
					activeChar.doCast(SkillTable.getInstance().getInfo(7029, val), activeChar, true);
				}
			}
			else
				activeChar.sendMessage("USAGE: //gmspeed [0 - 4]");
		}
		else if(command.equals("admin_invul"))
		{
			handleInvul(activeChar, activeChar);
			if(activeChar.isInvul())
			{
				if(Config.SAVE_GM_EFFECTS)
					activeChar.setVar("gm_invul", "true");
			}
			else
				activeChar.unsetVar("gm_invul");
		}
		else if(command.equals("admin_offline_vis"))
		{
			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player != null && player.isInOfflineMode())
				{
					player.setInvisible(false);
					player.decayMe();
					player.spawnMe();
				}
		}
		else if(command.equals("admin_offline_invis"))
		{
			for(L2Player player : L2ObjectsStorage.getAllPlayers())
				if(player != null && player.isInOfflineMode())
				{
					player.setInvisible(true);
					player.decayMe();
				}
		}
		else if(command.equals("admin_earthquake"))
		{
			String[] wordList = args.split("\\s+");
			try
			{
				int intensity = Integer.parseInt(wordList[0]);
				int duration = Integer.parseInt(wordList[1]);
				activeChar.broadcastPacket(new Earthquake(activeChar.getLoc(), intensity, duration));
			}
			catch(Exception e)
			{
				activeChar.sendMessage("USAGE: //earthquake intensity duration");
				return false;
			}
		}
		else if(command.equals("admin_unpara_all"))
		{
			for(L2Player player : L2World.getAroundPlayers(activeChar, 1250, 200))
			{
				player.stopAbnormalEffect(AbnormalEffect.HOLD_1);
				player.stopAbnormalEffect(AbnormalEffect.HOLD_2);
				player.setParalyzed(false);
			}
		}
		else if(command.equals("admin_para_all"))
		{
			ae = args.equals("2") ? AbnormalEffect.HOLD_2 : AbnormalEffect.HOLD_1;
			for(L2Player player : L2World.getAroundPlayers(activeChar, 1250, 200))
				if(player != null && !player.isGM())
				{
					player.startAbnormalEffect(ae);
					player.setParalyzed(true);
				}
		}
		else if(command.equals("admin_unpara"))
		{
			if(target == null || !target.isCharacter())
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			((L2Character) target).stopAbnormalEffect(AbnormalEffect.HOLD_1);
			((L2Character) target).stopAbnormalEffect(AbnormalEffect.HOLD_2);
			((L2Character) target).setParalyzed(false);
		}
		else if(command.equals("admin_para"))
		{
			ae = args.equals("2") ? AbnormalEffect.HOLD_2 : AbnormalEffect.HOLD_1;
			if(target == null || !target.isCharacter())
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			((L2Character) target).startAbnormalEffect(ae);
			((L2Character) target).setParalyzed(true);
		}
		else if(command.equals("admin_changename"))
		{
			if(target == null)
				target = activeChar;
			if(!target.isCharacter())
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			String oldName = ((L2Character) target).getName();
			String newName = args.trim();

			((L2Character) target).setName(newName);

			if(target.isPlayer())
				((L2Character) target).broadcastUserInfo(true);
			else if(target.isNpc())
				((L2Character) target).broadcastPacket(new NpcInfo((L2NpcInstance) target, null));

			activeChar.sendMessage("Changed name from " + oldName + " to " + newName + ".");
		}
		else if(command.equals("admin_setinvul"))
		{
			if(target == null || !target.isPlayer())
			{
				activeChar.sendPacket(Msg.INVALID_TARGET);
				return false;
			}
			handleInvul(activeChar, (L2Player) target);
		}
		else if(command.equals("admin_getinvul"))
		{
			if(target != null && target.isCharacter())
				activeChar.sendMessage("Target " + target.getName() + "(object ID: " + target.getObjectId() + ") is " + (!((L2Character) target).isInvul() ? "NOT " : "") + "invul");
		}
		else if(command.equals("admin_social"))
		{
			val = Rnd.get(1, 7);
			try
			{
				val = Integer.parseInt(args);
			}
			catch(NumberFormatException nfe)
			{
				activeChar.sendMessage("USAGE: //social [value]");
				return false;
			}
			if(target == null || target == activeChar)
				activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), val));
			else if(target.isCharacter())
				((L2Character) target).broadcastPacket(new SocialAction(target.getObjectId(), val));
		}
		else if(command.equals("admin_abnormal"))
		{
			try
			{
				ae = AbnormalEffect.getByName(args);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("USAGE: //abnormal name");
				activeChar.sendMessage("//abnormal - Clears all abnormal effects");
				return false;
			}

			L2Character effectTarget = target == null ? activeChar : (L2Character) target;

			if(ae == AbnormalEffect.NULL)
			{
				effectTarget.startAbnormalEffect(AbnormalEffect.NULL);
				effectTarget.sendMessage("Abnormal effects clearned by admin.");
				if(effectTarget != activeChar)
					effectTarget.sendMessage("Abnormal effects clearned.");
			}
			else
			{
				effectTarget.startAbnormalEffect(ae);
				effectTarget.sendMessage("Admin added abnormal effect: " + ae.getName());
				if(effectTarget != activeChar)
					effectTarget.sendMessage("Aadded abnormal effect: " + ae.getName());
			}
		}
		else if(command.equals("admin_transform"))
		{
			try
			{
				val = Integer.parseInt(args);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("USAGE: //transform transform_id");
				return false;
			}
			activeChar.setTransformation(val);
		}

		return true;
	}

	private void handleInvul(L2Player activeChar, L2Player target)
	{
		if(target.isInvul())
		{
			target.setIsInvul(false);
			target.stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
			if(target.getPet() != null)
			{
				target.getPet().setIsInvul(false);
				target.getPet().stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
			}
			activeChar.sendMessage(target.getName() + " is now mortal.");
		}
		else
		{
			target.setIsInvul(true);
			target.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
			if(target.getPet() != null)
			{
				target.getPet().setIsInvul(true);
				target.getPet().startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
			}
			activeChar.sendMessage(target.getName() + " is now immortal.");
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload(){}

	public void onShutdown(){}
}
