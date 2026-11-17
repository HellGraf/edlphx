package l2p.gameserver.network.clientpackets;

import java.util.Calendar;
import java.util.Collection;

import l2p.commons.database.mysql;
import l2p.commons.util.GArray;
import l2p.commons.util.Log;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.instancemanager.CoupleManager;
import l2p.gameserver.instancemanager.PlayerMessageStack;
import l2p.gameserver.instancemanager.QuestManager;
import l2p.gameserver.model.L2Character;
import l2p.gameserver.model.L2Clan;
import l2p.gameserver.model.L2Effect;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.model.L2Skill;
import l2p.gameserver.model.L2Summon;
import l2p.gameserver.model.L2WorldRegion;
import l2p.gameserver.model.entity.SevenSigns;
import l2p.gameserver.model.entity.vehicle.L2AirShip;
import l2p.gameserver.model.entity.vehicle.L2Ship;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.network.L2GameClient;
import l2p.gameserver.network.serverpackets.ChangeWaitType;
import l2p.gameserver.network.serverpackets.CharMoveToLocation;
import l2p.gameserver.network.serverpackets.ClientSetTime;
import l2p.gameserver.network.serverpackets.ConfirmDlg;
import l2p.gameserver.network.serverpackets.Die;
import l2p.gameserver.network.serverpackets.EtcStatusUpdate;
import l2p.gameserver.network.serverpackets.ExAutoSoulShot;
import l2p.gameserver.network.serverpackets.ExBasicActionList;
import l2p.gameserver.network.serverpackets.ExGetOnAirShip;
import l2p.gameserver.network.serverpackets.ExNoticePostArrived;
import l2p.gameserver.network.serverpackets.ExPCCafePointInfo;
import l2p.gameserver.network.serverpackets.ExSetCompassZoneCode;
import l2p.gameserver.network.serverpackets.ExStorageMaxCount;
import l2p.gameserver.network.serverpackets.GetOnVehicle;
import l2p.gameserver.network.serverpackets.HennaInfo;
import l2p.gameserver.network.serverpackets.ItemList;
import l2p.gameserver.network.serverpackets.L2FriendList;
import l2p.gameserver.network.serverpackets.L2FriendStatus;
import l2p.gameserver.network.serverpackets.L2GameServerPacket;
import l2p.gameserver.network.serverpackets.MagicSkillLaunched;
import l2p.gameserver.network.serverpackets.MagicSkillUse;
import l2p.gameserver.network.serverpackets.NpcHtmlMessage;
import l2p.gameserver.network.serverpackets.PartySmallWindowAll;
import l2p.gameserver.network.serverpackets.PartySpelled;
import l2p.gameserver.network.serverpackets.PetInfo;
import l2p.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2p.gameserver.network.serverpackets.PledgeShowMemberListAll;
import l2p.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2p.gameserver.network.serverpackets.PledgeSkillList;
import l2p.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import l2p.gameserver.network.serverpackets.PrivateStoreMsgSell;
import l2p.gameserver.network.serverpackets.QuestList;
import l2p.gameserver.network.serverpackets.RecipeShopMsg;
import l2p.gameserver.network.serverpackets.RelationChanged;
import l2p.gameserver.network.serverpackets.Ride;
import l2p.gameserver.network.serverpackets.ShortCutInit;
import l2p.gameserver.network.serverpackets.SkillList;
import l2p.gameserver.network.serverpackets.SystemMessage;
import l2p.gameserver.scripts.Scripts;
import l2p.gameserver.scripts.Scripts.ScriptClassAndMethod;
import l2p.gameserver.skills.AbnormalEffect;
import l2p.gameserver.tables.FriendsTable;
import l2p.gameserver.tables.SkillTable;

/**
 * Format: S
 * Format: bddddbdcccccccccccccccccccc
 */
public class EnterWorld extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
	//readS(); - клиент всегда отправляет строку "narcasse"
	}

	@Override
	public void runImpl()
	{
		L2GameClient client = getClient();
		L2Player activeChar = client.getActiveChar();
		Collection<L2GameServerPacket> packets = new GArray<L2GameServerPacket>();

		if(activeChar == null)
		{
			client.closeNow(false);
			return;
		}

		//TODO: проверка на вход с одного HWID

		boolean first = activeChar.entering;

		if(first)
		{
			if(activeChar.getPlayerAccess().GodMode && !Config.SHOW_GM_LOGIN)
				activeChar.setInvisible(true);

			activeChar.spawnMe();
			activeChar.startRegeneration();
		}
		else if(activeChar.isTeleporting())
			activeChar.onTeleported();

		/*
		 * TODO SignsSky в Kamael заменен пакетом SSQInfo
		 * if(SevenSigns.getInstance().isSealValidationPeriod())
		 *	activeChar.sendPacket(new SignsSky());
		 */

		activeChar.getMacroses().sendUpdate();
		sendPacket(new HennaInfo(activeChar), new ItemList(activeChar, false), new ShortCutInit(activeChar), new SkillList(activeChar), Msg.WELCOME);

		Announcements.getInstance().showAnnouncements(activeChar);

		//add char to online characters
		activeChar.setOnlineStatus(true);

		// Вызов всех хэндлеров, определенных в скриптах
		if(first)
		{
			Object[] script_args = new Object[] { activeChar };
			for(ScriptClassAndMethod handler : Scripts.onPlayerEnter)
				activeChar.callScripts(handler.scriptClass, handler.method, script_args);
		}

		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);

		if(first && activeChar.getCreateTime() != 0)
		{
			Calendar create = Calendar.getInstance();
			create.setTimeInMillis(activeChar.getCreateTime());
			Calendar now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());

			if(create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) && create.get(Calendar.YEAR) != now.get(Calendar.YEAR))
			{
				if(create.get(Calendar.YEAR) - now.get(Calendar.YEAR) == 5)
					packets.add(Msg.FIVE_YEARS_HAVE_PASSED_SINCE_THIS_CHARACTERS_CREATION);
				packets.add(Msg.ExNotifyBirthDay);
				packets.add(Msg.YOUR_BIRTHDAY_GIFT_HAS_ARRIVED_YOU_CAN_OBTAIN_IT_FROM_THE_GATEKEEPER_IN_ANY_VILLAGE);
			}

			int daysDiff = create.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
			if(daysDiff <= 7 && daysDiff > 1)
				packets.add(new SystemMessage(SystemMessage.THERE_ARE_S1_DAYS_UNTIL_YOUR_CHARACTERS_BIRTHDAY_ON_THAT_DAY_YOU_CAN_OBTAIN_A_SPECIAL_GIFT_FROM_THE_GATEKEEPER_IN_ANY_VILLAGE).addNumber(daysDiff));

			sendPackets(packets);
			packets.clear();
		}

		if(activeChar.getClan() != null)
		{
			notifyClanMembers(activeChar);
			sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar), new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));

			if(activeChar.getClan().isAttacker())
				activeChar.setSiegeState(1);
			else if(activeChar.getClan().isDefender())
				activeChar.setSiegeState(2);
		}

		// engage and notify Partner
		if(first && Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance().engage(activeChar);
			CoupleManager.getInstance().notifyPartner(activeChar);
		}

		Log.LogChar(activeChar, Log.EnterWorld, "");

		if(first)
		{
			notifyFriends(activeChar, true);
			loadTutorial(activeChar);
			activeChar.restoreDisableSkills();
		}
		else
			packets.add(new L2FriendList(activeChar, false));

		packets.add(new ExStorageMaxCount(activeChar));
		packets.add(new QuestList(activeChar));
		packets.add(new ExBasicActionList());

		// refresh player info
		packets.add(new EtcStatusUpdate(activeChar));
		packets.add(new ExPCCafePointInfo(activeChar));

		sendPackets(packets);
		packets.clear();

		//activeChar.getInventory().refreshListeners();
		activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
		activeChar.checkDayNightMessages();

		if(!first)
		{
			if(activeChar.isCastingNow())
			{
				L2Character castingTarget = activeChar.getCastingTarget();
				L2Skill castingSkill = activeChar.getCastingSkill();
				long animationEndTime = activeChar.getAnimationEndTime();
				if(castingSkill != null && castingTarget != null && castingTarget.isCharacter() && activeChar.getAnimationEndTime() > 0)
					packets.add(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
			}

			if(activeChar.isInVehicle() && !activeChar.getVehicle().isClanAirShip())
				if(activeChar.getVehicle().isAirShip())
					packets.add(new ExGetOnAirShip(activeChar, (L2AirShip) activeChar.getVehicle(), activeChar.getInVehiclePosition()));
				else
					packets.add(new GetOnVehicle(activeChar, (L2Ship) activeChar.getVehicle(), activeChar.getInVehiclePosition()));

			if(activeChar.isMoving || activeChar.isFollow)
				packets.add(new CharMoveToLocation(activeChar));

			if(activeChar.getMountNpcId() != 0)
				packets.add(new Ride(activeChar));
		}
		sendPackets(packets);
		packets.clear();

		activeChar.entering = false;
		activeChar.sendUserInfo(true);

		if(activeChar.isSitting())
			activeChar.sendPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
			if(activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
				packets.add(new PrivateStoreMsgBuy(activeChar));
			else if(activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
				packets.add(new PrivateStoreMsgSell(activeChar));
			else if(activeChar.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
				packets.add(new RecipeShopMsg(activeChar));

		if(activeChar.isDead())
			packets.add(new Die(activeChar));

		sendPackets(packets);
		packets.clear();

		activeChar.unsetVar("offline");

		// на всякий случай
		activeChar.sendActionFailed();

		if(first && activeChar.isGM() && Config.SAVE_GM_EFFECTS && activeChar.getPlayerAccess().CanUseGMCommand)
		{
			//silence
			if(activeChar.getVarB("gm_silence"))
			{
				activeChar.setMessageRefusal(true);
				activeChar.sendPacket(Msg.MESSAGE_REFUSAL_MODE);
			}
			//invul
			if(activeChar.getVarB("gm_invul"))
			{
				activeChar.setIsInvul(true);
				activeChar.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
				activeChar.sendMessage(activeChar.getName() + " is now immortal.");
			}
			//gmspeed
			try
			{
				int var_gmspeed = Integer.parseInt(activeChar.getVar("gm_gmspeed"));
				if(var_gmspeed >= 1 && var_gmspeed <= 4)
					activeChar.doCast(SkillTable.getInstance().getInfo(7029, var_gmspeed), activeChar, true);
			}
			catch(Exception E)
			{}
		}

		PlayerMessageStack.getInstance().CheckMessages(activeChar);

		sendPacket(new ClientSetTime(), new ExSetCompassZoneCode(activeChar));
		checkNewMail(activeChar);

		if(activeChar.isReviveRequested())
			sendPacket(new ConfirmDlg(SystemMessage.S1_IS_MAKING_AN_ATTEMPT_AT_RESURRECTION_WITH_$S2_EXPERIENCE_POINTS_DO_YOU_WANT_TO_CONTINUE_WITH_THIS_RESURRECTION, 0, 2).addString("Other player").addString("some"));

		if(!first)
		{
			activeChar.updateEffectIcons();
			if(activeChar.getCurrentRegion() != null)
				for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
					neighbor.showObjectsToPlayer(activeChar);

			if(activeChar.getPet() != null)
				packets.add(new PetInfo(activeChar.getPet()));

			if(activeChar.isInParty())
			{
				L2Summon member_pet;
				//sends new member party window for all members
				//we do all actions before adding member to a list, this speeds things up a little
				packets.add(new PartySmallWindowAll(activeChar.getParty(), activeChar));

				for(L2Player member : activeChar.getParty().getPartyMembers())
					if(member != activeChar)
					{
						packets.add(new PartySpelled(member, true));
						if((member_pet = member.getPet()) != null)
							packets.add(new PartySpelled(member_pet, true));
						packets.addAll(RelationChanged.update(activeChar, member, activeChar));
					}

				// Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
				if(activeChar.getParty().isInCommandChannel())
					packets.add(Msg.ExMPCCOpen);
			}

			for(int shotId : activeChar.getAutoSoulShot())
				packets.add(new ExAutoSoulShot(shotId, true));

			for(L2Effect e : activeChar.getEffectList().getAllFirstEffects())
				if(e.getSkill().isToggle())
					packets.add(new MagicSkillLaunched(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar, e.getSkill().isOffensive()));

			sendPackets(packets);
			packets.clear();

			activeChar.broadcastUserInfo(false);
		}
		else
			activeChar.sendUserInfo(false); // Отобразит права в клане

		if(getClient().getBonus() < 0)
			activeChar.callScripts("services.Activation", "activation_page");
	}

	public static void notifyFriends(L2Player cha, boolean login)
	{
		if(login)
			cha.sendPacket(new L2FriendList(cha, false));
		try
		{
			for(Integer friend_id : FriendsTable.getInstance().getFriendsList(cha.getObjectId()))
			{
				L2Player friend = L2ObjectsStorage.getPlayer(friend_id);
				if(friend != null)
					if(login)
						friend.sendPacket(new SystemMessage(SystemMessage.S1_FRIEND_HAS_LOGGED_IN).addString(cha.getName()), new L2FriendStatus(cha, true));
					else
						friend.sendPacket(new L2FriendStatus(cha, false));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param activeChar
	 */
	private void notifyClanMembers(L2Player activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if(clan == null || clan.getClanMember(activeChar.getObjectId()) == null)
			return;

		clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
		//if(activeChar.isClanLeader())
		//{
		//	if(activeChar.getClan().getHasHideout() != 0 && ClanHallManager.getInstance().getClanHall(activeChar.getClan().getHasHideout()).getNotPaid())
		//		activeChar.sendPacket(Msg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
		//}

		int sponsor = activeChar.getSponsor();
		int apprentice = activeChar.getApprentice();
		SystemMessage msg = new SystemMessage(SystemMessage.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addString(activeChar.getName());
		PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(activeChar);
		for(L2Player clanMember : clan.getOnlineMembers(activeChar.getObjectId()))
			if(clanMember.getObjectId() == sponsor)
				clanMember.sendPacket(memberUpdate, new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_IN).addString(activeChar.getName()));
			else if(clanMember.getObjectId() == apprentice)
				clanMember.sendPacket(memberUpdate, new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_IN).addString(activeChar.getName()));
			else
				clanMember.sendPacket(memberUpdate, msg);

		if(clan.isNoticeEnabled() && clan.getNotice() != "")
		{
			NpcHtmlMessage notice = new NpcHtmlMessage(5);
			notice.setHtml("<html><body><center><font color=\"LEVEL\">" + activeChar.getClan().getName() + " Clan Notice</font></center><br>" + activeChar.getClan().getNotice() + "</body></html>");
			sendPacket(notice);
		}
	}

	private void loadTutorial(L2Player player)
	{
		Quest q = QuestManager.getQuest(255);
		if(q != null)
			player.processQuestEvent(q.getName(), "UC", null);
	}

	private void checkNewMail(L2Player activeChar)
	{
		if(mysql.simple_get_int("messageId", "mail", "unread AND receiver=" + activeChar.getObjectId()) > 0)
			sendPacket(new ExNoticePostArrived(0));
	}
}