package com.sun.javaws.xdg;

import com.sun.deploy.trace.Trace;
import com.sun.deploy.xdg.BaseDir;
import com.sun.deploy.xml.BadTokenException;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

final class MenuDatabase
{
  private static final String DEFAULT_JWS_MENU_NAME = "Java WebStart";
  private static MenuDatabase instance;
  private Menu rootMenu;

  static synchronized MenuDatabase getInstance()
  {
    if (instance == null)
      instance = new MenuDatabase();
    return instance;
  }

  MenuDatabase()
  {
    load();
  }

  private File getMenuFile()
  {
    BaseDir localBaseDir = BaseDir.getInstance();
    String str = localBaseDir.getUserConfigDir() + File.separatorChar + "menus" + File.separator + "applications-merged" + File.separator + "javaws.menu";
    return new File(str);
  }

  void load()
  {
    File localFile = getMenuFile();
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(localFile);
      localObject = new InputStreamReader(localFileInputStream);
      BufferedReader localBufferedReader = new BufferedReader((Reader)localObject);
      StringBuffer localStringBuffer = new StringBuffer();
      for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine())
        localStringBuffer.append(str);
      localBufferedReader.close();
      XMLParser localXMLParser = new XMLParser(localStringBuffer.toString());
      XMLNode localXMLNode = localXMLParser.parse();
      this.rootMenu = new Menu();
      this.rootMenu.read(localXMLNode);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      this.rootMenu = new Menu("Applications", null);
      Object localObject = new Menu("Java WebStart", this.rootMenu);
    }
    catch (IOException localIOException)
    {
      Trace.ignored(localIOException);
    }
    catch (BadTokenException localBadTokenException)
    {
      Trace.ignored(localBadTokenException);
    }
  }

  void save()
    throws IOException
  {
    File localFile = getMenuFile();
    if (localFile.getParentFile() != null)
      localFile.getParentFile().mkdirs();
    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
    OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream);
    BufferedWriter localBufferedWriter = new BufferedWriter(localOutputStreamWriter);
    this.rootMenu.write(localBufferedWriter);
    localBufferedWriter.close();
  }

  private Menu getJavaWSMenu()
  {
    return this.rootMenu.getSubMenu("Java WebStart");
  }

  static boolean isJavaWSMenu(Menu paramMenu)
  {
    return "Java WebStart".equals(paramMenu.getName());
  }

  Menu addMenu(String paramString)
  {
    return getJavaWSMenu().addMenuPath(paramString);
  }

  Menu getMenu(String paramString)
  {
    return getJavaWSMenu().getMenuPath(paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.xdg.MenuDatabase
 * JD-Core Version:    0.6.2
 */