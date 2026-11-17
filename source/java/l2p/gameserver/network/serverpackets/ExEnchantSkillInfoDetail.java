package l2p.gameserver.network.serverpackets;

import l2p.gameserver.tables.SkillTreeTable;

public class ExEnchantSkillInfoDetail extends L2GameServerPacket
{
	private final int _type;
	private final int _skillId;
	private final int _skillLvl;
	private final int _sp;
	private final int _chance;
	private final int _bookId, _adenaCount;

	public ExEnchantSkillInfoDetail(int type, int skillId, int skillLvl, int sp, int chance, int bookId, int adenaCount)
	{
		_type = type;
		_skillId = skillId;
		_skillLvl = skillLvl;
		_sp = sp;
		_chance = chance;
		_bookId = bookId;
		_adenaCount = adenaCount;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x5e);
		
		writeD(_type);
		writeD(_skillId);
		writeD(_skillLvl);
		writeD(_sp);
		writeD(_chance);

		writeD(0x02);
		writeD(57); // adena
		writeD(_adenaCount); // adena count ?
		if(_bookId > 0)
		{
			writeD(_bookId); // book
			writeD(1); // book count
		}
		else
		{
			writeD(SkillTreeTable.NORMAL_ENCHANT_BOOK); // book
			writeD(0); // book count
		}
	}
}