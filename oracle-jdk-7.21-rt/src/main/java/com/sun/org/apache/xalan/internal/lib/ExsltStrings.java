/*     */ package com.sun.org.apache.xalan.internal.lib;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
/*     */ import com.sun.org.apache.xpath.internal.NodeSet;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class ExsltStrings extends ExsltBase
/*     */ {
/*     */   public static String align(String targetStr, String paddingStr, String type)
/*     */   {
/*  86 */     if (targetStr.length() >= paddingStr.length()) {
/*  87 */       return targetStr.substring(0, paddingStr.length());
/*     */     }
/*  89 */     if (type.equals("right"))
/*     */     {
/*  91 */       return paddingStr.substring(0, paddingStr.length() - targetStr.length()) + targetStr;
/*     */     }
/*  93 */     if (type.equals("center"))
/*     */     {
/*  95 */       int startIndex = (paddingStr.length() - targetStr.length()) / 2;
/*  96 */       return paddingStr.substring(0, startIndex) + targetStr + paddingStr.substring(startIndex + targetStr.length());
/*     */     }
/*     */ 
/* 101 */     return targetStr + paddingStr.substring(targetStr.length());
/*     */   }
/*     */ 
/*     */   public static String align(String targetStr, String paddingStr)
/*     */   {
/* 110 */     return align(targetStr, paddingStr, "left");
/*     */   }
/*     */ 
/*     */   public static String concat(NodeList nl)
/*     */   {
/* 123 */     StringBuffer sb = new StringBuffer();
/* 124 */     for (int i = 0; i < nl.getLength(); i++)
/*     */     {
/* 126 */       Node node = nl.item(i);
/* 127 */       String value = toString(node);
/*     */ 
/* 129 */       if ((value != null) && (value.length() > 0)) {
/* 130 */         sb.append(value);
/*     */       }
/*     */     }
/* 133 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String padding(double length, String pattern)
/*     */   {
/* 153 */     if ((pattern == null) || (pattern.length() == 0)) {
/* 154 */       return "";
/*     */     }
/* 156 */     StringBuffer sb = new StringBuffer();
/* 157 */     int len = (int)length;
/* 158 */     int numAdded = 0;
/* 159 */     int index = 0;
/* 160 */     while (numAdded < len)
/*     */     {
/* 162 */       if (index == pattern.length()) {
/* 163 */         index = 0;
/*     */       }
/* 165 */       sb.append(pattern.charAt(index));
/* 166 */       index++;
/* 167 */       numAdded++;
/*     */     }
/*     */ 
/* 170 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String padding(double length)
/*     */   {
/* 178 */     return padding(length, " ");
/*     */   }
/*     */ 
/*     */   public static NodeList split(String str, String pattern)
/*     */   {
/* 206 */     NodeSet resultSet = new NodeSet();
/* 207 */     resultSet.setShouldCacheNodes(true);
/*     */ 
/* 209 */     boolean done = false;
/* 210 */     int fromIndex = 0;
/* 211 */     int matchIndex = 0;
/* 212 */     String token = null;
/*     */ 
/* 214 */     while ((!done) && (fromIndex < str.length()))
/*     */     {
/* 216 */       matchIndex = str.indexOf(pattern, fromIndex);
/* 217 */       if (matchIndex >= 0)
/*     */       {
/* 219 */         token = str.substring(fromIndex, matchIndex);
/* 220 */         fromIndex = matchIndex + pattern.length();
/*     */       }
/*     */       else
/*     */       {
/* 224 */         done = true;
/* 225 */         token = str.substring(fromIndex);
/*     */       }
/*     */ 
/* 228 */       Document doc = DocumentHolder.m_doc;
/* 229 */       synchronized (doc)
/*     */       {
/* 231 */         Element element = doc.createElement("token");
/* 232 */         Text text = doc.createTextNode(token);
/* 233 */         element.appendChild(text);
/* 234 */         resultSet.addNode(element);
/*     */       }
/*     */     }
/*     */ 
/* 238 */     return resultSet;
/*     */   }
/*     */ 
/*     */   public static NodeList split(String str)
/*     */   {
/* 246 */     return split(str, " ");
/*     */   }
/*     */ 
/*     */   public static NodeList tokenize(String toTokenize, String delims)
/*     */   {
/* 286 */     NodeSet resultSet = new NodeSet();
/*     */ 
/* 288 */     if ((delims != null) && (delims.length() > 0))
/*     */     {
/* 290 */       StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
/*     */ 
/* 292 */       Document doc = DocumentHolder.m_doc;
/* 293 */       synchronized (doc)
/*     */       {
/* 295 */         while (lTokenizer.hasMoreTokens())
/*     */         {
/* 297 */           Element element = doc.createElement("token");
/* 298 */           element.appendChild(doc.createTextNode(lTokenizer.nextToken()));
/* 299 */           resultSet.addNode(element);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 308 */       Document doc = DocumentHolder.m_doc;
/* 309 */       synchronized (doc)
/*     */       {
/* 311 */         for (int i = 0; i < toTokenize.length(); i++)
/*     */         {
/* 313 */           Element element = doc.createElement("token");
/* 314 */           element.appendChild(doc.createTextNode(toTokenize.substring(i, i + 1)));
/* 315 */           resultSet.addNode(element);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 320 */     return resultSet;
/*     */   }
/*     */ 
/*     */   public static NodeList tokenize(String toTokenize)
/*     */   {
/* 328 */     return tokenize(toTokenize, " \t\n\r");
/*     */   }
/*     */ 
/*     */   private static class DocumentHolder
/*     */   {
/*     */     private static final Document m_doc;
/*     */ 
/*     */     static
/*     */     {
/*     */       try
/*     */       {
/* 346 */         m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
/*     */       }
/*     */       catch (ParserConfigurationException pce)
/*     */       {
/* 351 */         throw new WrappedRuntimeException(pce);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.lib.ExsltStrings
 * JD-Core Version:    0.6.2
 */