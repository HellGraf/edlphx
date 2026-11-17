package commands.admin;

import java.sql.ResultSet;

import l2p.commons.database.DatabaseUtils;
import l2p.commons.database.FiltredPreparedStatement;
import l2p.commons.database.L2DatabaseFactory;
import l2p.commons.database.ThreadConnection;
import l2p.gameserver.handler.AdminCommandHandler;
import l2p.gameserver.handler.interfaces.IAdminCommandHandler;
import l2p.gameserver.model.L2Player;
import l2p.gameserver.scripts.ScriptFile;

public class AdminRepairChar implements IAdminCommandHandler, ScriptFile
{
	private static String[] _commands =
	{
		"admin_restore",
		"admin_repair"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return _commands;
	}

	public boolean useAdminCommand(L2Player activeChar, String command, String args)
	{
		if(activeChar.getPlayerAccess() == null || !activeChar.getPlayerAccess().CanEditChar)
			return false;

		if(args.length() == 0)
			return false;

		String cmd = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(cmd);
			statement.setString(1, args);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			statement = con.prepareStatement("SELECT obj_id FROM characters where char_name=?");
			statement.setString(1, args);
			rset = statement.executeQuery();
			int objId = 0;
			if(rset.next())
				objId = rset.getInt(1);

			DatabaseUtils.closeDatabaseSR(statement, rset);

			if(objId == 0)
				return false;

			// con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			statement.setInt(1, objId);
			statement.execute();
			DatabaseUtils.closeStatement(statement);

			// con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items SET loc='INVENTORY' WHERE owner_id=? AND loc!='WAREHOUSE'");
			statement.setInt(1, objId);
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
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
