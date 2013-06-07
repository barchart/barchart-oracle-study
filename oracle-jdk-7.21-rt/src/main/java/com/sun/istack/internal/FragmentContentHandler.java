/*    */ package com.sun.istack.internal;
/*    */ 
/*    */ import org.xml.sax.ContentHandler;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.XMLReader;
/*    */ import org.xml.sax.helpers.XMLFilterImpl;
/*    */ 
/*    */ public class FragmentContentHandler extends XMLFilterImpl
/*    */ {
/*    */   public FragmentContentHandler()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FragmentContentHandler(XMLReader parent)
/*    */   {
/* 42 */     super(parent);
/*    */   }
/*    */ 
/*    */   public FragmentContentHandler(ContentHandler handler)
/*    */   {
/* 47 */     setContentHandler(handler);
/*    */   }
/*    */ 
/*    */   public void startDocument()
/*    */     throws SAXException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void endDocument()
/*    */     throws SAXException
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.istack.internal.FragmentContentHandler
 * JD-Core Version:    0.6.2
 */