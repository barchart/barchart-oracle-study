package com.sun.java.browser.plugin2;

import java.applet.Applet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.html.HTMLDocument;

public class DOM
{
  public static final Document getDocument(Applet paramApplet)
    throws DOMException
  {
    try
    {
      JSObject localJSObject1 = JSObject.getWindow(paramApplet);
      JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
      DOMObject localDOMObject = new DOMObject(localJSObject2);
      return new HTMLDocument(localDOMObject, null);
    }
    catch (JSException localJSException)
    {
      throw ((DOMException)new DOMException((short)9, "Error fetching document for applet").initCause(localJSException));
    }
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     com.sun.java.browser.plugin2.DOM
 * JD-Core Version:    0.6.2
 */