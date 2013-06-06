package com.sun.javaws.xdg;

import com.sun.deploy.xml.XMLNode;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

class Menu
{
  private String name;
  private Menu parent;
  private ArrayList subMenus = new ArrayList();
  private ArrayList entries = new ArrayList();

  Menu()
  {
  }

  Menu(Menu paramMenu)
  {
    this();
    this.parent = paramMenu;
    if (paramMenu != null)
      paramMenu.addSubMenu(this);
  }

  Menu(String paramString, Menu paramMenu)
  {
    this(paramMenu);
    this.name = paramString;
  }

  String getName()
  {
    return this.name;
  }

  private String[] splitPath(String paramString)
  {
    int i = paramString.indexOf('/');
    String str1;
    String str2;
    if (i >= 0)
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1, paramString.length());
    }
    else
    {
      str1 = paramString;
      str2 = null;
    }
    return new String[] { str1, str2 };
  }

  Menu addMenuPath(String paramString)
  {
    String[] arrayOfString = splitPath(paramString);
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    Menu localMenu = getSubMenu(str1);
    if (localMenu == null)
      localMenu = new Menu(str1, this);
    if (str2 != null)
      return localMenu.addMenuPath(str2);
    return localMenu;
  }

  Menu getMenuPath(String paramString)
  {
    String[] arrayOfString = splitPath(paramString);
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    Menu localMenu = getSubMenu(str1);
    if ((str2 != null) && (localMenu != null))
      return localMenu.addMenuPath(str2);
    return localMenu;
  }

  Menu getSubMenu(String paramString)
  {
    Object localObject = null;
    Iterator localIterator = this.subMenus.iterator();
    while ((localIterator.hasNext()) && (localObject == null))
    {
      Menu localMenu = (Menu)localIterator.next();
      if (localMenu.name.equals(paramString))
        localObject = localMenu;
    }
    return localObject;
  }

  void addSubMenu(Menu paramMenu)
  {
    if (!this.subMenus.contains(paramMenu))
      this.subMenus.add(paramMenu);
  }

  void removeSubMenu(Menu paramMenu)
  {
    this.subMenus.remove(paramMenu);
    cleanupEmptyMenus();
  }

  void addEntry(String paramString)
  {
    this.entries.add(paramString);
  }

  void removeEntry(String paramString)
  {
    this.entries.remove(paramString);
    cleanupEmptyMenus();
  }

  private void cleanupEmptyMenus()
  {
    if ((isEmpty()) && (this.parent != null) && (!MenuDatabase.isJavaWSMenu(this)))
      this.parent.removeSubMenu(this);
  }

  private boolean isEmpty()
  {
    return (this.entries.size() == 0) && (this.subMenus.size() == 0);
  }

  void write(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("<Menu>\n");
    paramWriter.write("<Name>" + this.name + "</Name>\n");
    Iterator localIterator = this.subMenus.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Menu)localIterator.next();
      ((Menu)localObject).write(paramWriter);
    }
    if (this.entries.size() > 0)
    {
      paramWriter.write("<Include>\n");
      localIterator = this.entries.iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        paramWriter.write("<Filename>" + (String)localObject + "</Filename>\n");
      }
      paramWriter.write("</Include>\n");
    }
    paramWriter.write("</Menu>\n");
  }

  void read(XMLNode paramXMLNode)
  {
    for (XMLNode localXMLNode1 = paramXMLNode.getNested(); localXMLNode1 != null; localXMLNode1 = localXMLNode1.getNext())
    {
      Object localObject;
      if (localXMLNode1.getName().equals("Menu"))
      {
        localObject = new Menu(this);
        ((Menu)localObject).read(localXMLNode1);
        addSubMenu((Menu)localObject);
      }
      else if (localXMLNode1.getName().equals("Name"))
      {
        localObject = localXMLNode1.getNested();
        this.name = ((XMLNode)localObject).getName();
      }
      else if (localXMLNode1.getName().equals("Include"))
      {
        for (localObject = localXMLNode1.getNested(); localObject != null; localObject = ((XMLNode)localObject).getNext())
        {
          XMLNode localXMLNode2 = ((XMLNode)localObject).getNested();
          addEntry(localXMLNode2.getName());
        }
      }
    }
  }
}

/* Location:           /home/user1/Temp/jvm/javaws.jar
 * Qualified Name:     com.sun.javaws.xdg.Menu
 * JD-Core Version:    0.6.2
 */