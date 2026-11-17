package l2p.gameserver.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l2p.gameserver.Config;
import l2p.gameserver.model.L2Player;

public class Files
{
	private static Logger _log = Logger.getLogger(Strings.class.getName());

	private static final FastMap<String, String> cache = new FastMap<String, String>().setShared(true);

	public static String read(String name)
	{
		//XXX: временная заглушка, убирающая папку "data/" из имени файла
		if(name.startsWith("data/html/"))
			name = name.substring(10);
		else if(name.startsWith("data/scripts/"))
			name = name.substring(5);
		
		if(name == null)
			return null;

		if(Config.USE_FILE_CACHE && cache.containsKey(name))
			return cache.get(name);

		File file = new File("./data/html/" + name);

		//		_log.info("Get file "+file.getPath());

		if(!file.exists())
			return null;

		String content = null;

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new UnicodeReader(new FileInputStream(file), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String s = "";
			while((s = br.readLine()) != null)
				sb.append(s).append("\n");
			content = sb.toString();
			sb = null;
		}
		catch(Exception e)
		{ /* problem are ignored */}
		finally
		{
			try
			{
				if(br != null)
					br.close();
			}
			catch(Exception e1)
			{ /* problems ignored */}
		}

		if(Config.USE_FILE_CACHE)
			cache.put(name, content);

		return content;
	}

	public static void cacheClean()
	{
		cache.clear();
	}

	public static long lastModified(String name)
	{
		if(name == null)
			return 0;

		return new File(name).lastModified();
	}

	public static String read(String name, L2Player player)
	{
		if(player == null)
			return "";
		return read(name, player.getLang());
	}

	public static String langFileName(String name, String lang)
	{
		if(lang == null || lang.equalsIgnoreCase("en"))
			lang = "";

		String tmp;

		tmp = name.replaceAll("(.+)(\\.htm)", "$1-" + lang + "$2");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		tmp = name.replaceAll("(.+)(/[^/].+\\.htm)", "$1/" + lang + "$2");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		tmp = name.replaceAll("(.+?/html)/", "$1-" + lang + "/");
		if(!tmp.equals(name) && lastModified(tmp) > 0)
			return tmp;

		if(lastModified(name) > 0)
			return name;

		return null;
	}

	public static String read(String name, String lang)
	{		
		String tmp = langFileName(name, lang);

		long last_modif = lastModified(tmp); // время модификации локализованного файла
		if(last_modif > 0) // если он существует
		{
			if(last_modif >= lastModified(name) || !Config.CHECK_LANG_FILES_MODIFY) // и новее оригинального файла
				return Strings.bbParse(read(tmp)); // то вернуть локализованный

			_log.warning("Last modify of " + name + " more then " + tmp); // если он существует но устарел - выругаться в лог
		}

		return Strings.bbParse(read(name)); // если локализованный файл отсутствует вернуть оригинальный
	}
}