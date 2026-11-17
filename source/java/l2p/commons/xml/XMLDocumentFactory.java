package l2p.commons.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public final class XMLDocumentFactory
{
	//==================== SINGLETON BEGIN ====================
	private static volatile XMLDocumentFactory _instance = null;

	public static XMLDocumentFactory getInstance()
	{
		if(_instance == null)
		{
			synchronized(XMLDocumentFactory.class)
			{
				if(_instance == null)
				{
					XMLDocumentFactory _local = new XMLDocumentFactory();
					_instance = _local;
				}
			}
		}
		return _instance;
	}
	//==================== SINGLETON END ====================

	private static DocumentBuilder _builder = null;
	
	private XMLDocumentFactory()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
			
		try
		{
			_builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
	public final Document loadDocument(final String filePath) throws Exception
	{
		return loadDocument(new File(filePath));
	}
	
	public final Document loadDocument(final File file) throws Exception
	{
		if (!file.exists() || !file.isFile())
			throw new Exception("File: " + file.getAbsolutePath() + " doesn't exist and/or is not a file.");
		
		if(_builder != null)
			return _builder.parse(file);
		else
			return null;
	}
	
	public final Document newDocument()
	{
		if(_builder != null)
			return _builder.newDocument();
		else
			return null;
	}
}