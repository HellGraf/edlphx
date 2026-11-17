package commands.user;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.extensions.multilang.CustomMessage;
import l2p.gameserver.handler.UserCommandHandler;
import l2p.gameserver.handler.interfaces.IUserCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Skill;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.tables.SkillTable;

/**
 * Support for /unstuck command
 */
public class Escape implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 52 };

	public void useUserCommand(L2Player player, int id)
	{
		if(player.isMovementDisabled() || player.isInOlympiadMode())
			return;

		if(player.getTeleMode() != 0)
		{
			player.sendMessage(new CustomMessage("common.TryLater", player));
			return;
		}

		if(player.isTerritoryFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		if(player.getDuel() != null || player.getTeam() != 0)
		{
			player.sendMessage(new CustomMessage("common.RecallInDuel", player));
			return;
		}

		player.abortAttack(true, true);
		player.abortCast(true);
		player.stopMove();

		L2Skill skill;
		if(player.getPlayerAccess().FastUnstuck)
			skill = SkillTable.getInstance().getInfo(1050, 2);
		else
			skill = SkillTable.getInstance().getInfo(2099, 1);

		if(skill != null && skill.checkCondition(player, player, false, false, true))
			player.getAI().Cast(skill, player, false, true);
	}

	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}