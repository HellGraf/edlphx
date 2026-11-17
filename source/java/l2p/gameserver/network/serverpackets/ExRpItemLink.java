package l2p.gameserver.network.serverpackets;

import javolution.util.FastMap;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.items.L2ItemInstance;

public class ExRpItemLink extends L2GameServerPacket
{
	private static final FastMap<Integer, ItemInfo> _cache = new FastMap<Integer, ItemInfo>().setShared(true);
	private static final long _cacheTime = 10000;
	private ItemInfo _item = null;

	public ExRpItemLink(int id)
	{
		if((_item = _cache.get(id)) == null)
		{
			//FIXME
			//System.out.println("ExRpItemLink cache is null for item: " + id);
		}
		else
		{
			_item.updateCache(L2ObjectsStorage.getItemByObjId(id), false);
		}
	}

	public static void addItem(int id)
	{
		if(_cache.containsKey(id))
		{
			_cache.get(id).updateCache(L2ObjectsStorage.getItemByObjId(id), true);
		}
		else
		{
			_cache.put(id, new ItemInfo(id));
		}
	}
	
	private static class ItemInfo
	{
		public int objectId, itemId, equipSlot, bodyPart;
		public long count, nextUpdate;
		public short type2FP, enchantLevel, cType1, cType2;
		public int augmentationId;
		//public int shadowLifeTime;
		public int[] attackElement, enchantOptions;
		public int defenceFire;
		public int defenceWater;
		public int defenceWind;
		public int defenceEarth;
		public int defenceHoly;
		public int defenceUnholy;
		public boolean isEquiped;

		private ItemInfo(int id)
		{
			L2ItemInstance item = L2ObjectsStorage.getItemByObjId(id);
			if(item == null)
			{
				objectId = 0;
				return;
			}
			objectId = item.getObjectId();
			itemId = item.getItemId();
			type2FP = (short) item.getItem().getType2ForPackets();
			bodyPart = item.getItem().getBodyPart();
			updateCache(item, true);
		}

		public synchronized void updateCache(L2ItemInstance item, boolean force)
		{
			if(item == null || item.getItemId() != itemId || !force && nextUpdate > System.currentTimeMillis())
				return;
			nextUpdate = System.currentTimeMillis() + _cacheTime;

			count = item.getCount();
			enchantLevel = (short) item.getEnchantLevel();
			equipSlot = item.getEquipSlot();
			type2FP = (short) item.getItem().getType2ForPackets();
			cType1 = (short)item.getCustomType1();
			cType2 = (short)item.getCustomType2();
			isEquiped = item.isEquipped();
			augmentationId = item.getAugmentationId();
			//shadowLifeTime = item.isShadowItem() ? item.getLifeTimeRemaining() : -1;
			attackElement = item.getAttackElement();
			defenceFire = item.getDefenceFire();
			defenceWater = item.getDefenceWater();
			defenceWind = item.getDefenceWind();
			defenceEarth = item.getDefenceEarth();
			defenceHoly = item.getDefenceHoly();
			defenceUnholy = item.getDefenceUnholy();
			enchantOptions = item.getEnchantOptions();
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(_item == null || _item.objectId == 0)
			return;
		
		writeC(EXTENDED_PACKET);
		writeH(0x6c);

		writeD(_item.objectId);
		writeD(_item.itemId);
		writeD(_item.equipSlot);
		writeQ(_item.count);
		writeH(_item.type2FP);
		writeH(_item.cType1);
		writeH(_item.isEquiped ? 0x01 : 0x00);
		writeD(_item.bodyPart);
		writeH(_item.enchantLevel);
		writeH(_item.cType2);
		writeD(_item.augmentationId);
		writeD(-1); //TODO: get mana shadowLifeTime
		writeD(-9999); //TODO: getRemainingTime
		writeH(_item.attackElement[0]);
		writeH(_item.attackElement[1]);
		writeH(_item.defenceFire);
		writeH(_item.defenceWater);
		writeH(_item.defenceWind);
		writeH(_item.defenceEarth);
		writeH(_item.defenceHoly);
		writeH(_item.defenceUnholy);
		for (int op : _item.enchantOptions)
		{
			writeH(op);
		}
	}
}