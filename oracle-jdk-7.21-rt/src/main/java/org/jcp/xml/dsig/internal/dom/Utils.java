/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class Utils
/*     */ {
/*     */   public static byte[] readBytesFromStream(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  47 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*  48 */     byte[] arrayOfByte = new byte[1024];
/*     */     while (true) {
/*  50 */       int i = paramInputStream.read(arrayOfByte);
/*  51 */       if (i == -1) {
/*     */         break;
/*     */       }
/*  54 */       localByteArrayOutputStream.write(arrayOfByte, 0, i);
/*  55 */       if (i < 1024) {
/*     */         break;
/*     */       }
/*     */     }
/*  59 */     return localByteArrayOutputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   static Set toNodeSet(Iterator paramIterator)
/*     */   {
/*  70 */     HashSet localHashSet = new HashSet();
/*  71 */     while (paramIterator.hasNext()) {
/*  72 */       Node localNode = (Node)paramIterator.next();
/*  73 */       localHashSet.add(localNode);
/*     */ 
/*  75 */       if (localNode.getNodeType() == 1) {
/*  76 */         NamedNodeMap localNamedNodeMap = localNode.getAttributes();
/*  77 */         int i = 0; for (int j = localNamedNodeMap.getLength(); i < j; i++) {
/*  78 */           localHashSet.add(localNamedNodeMap.item(i));
/*     */         }
/*     */       }
/*     */     }
/*  82 */     return localHashSet;
/*     */   }
/*     */ 
/*     */   public static String parseIdFromSameDocumentURI(String paramString)
/*     */   {
/*  89 */     if (paramString.length() == 0) {
/*  90 */       return null;
/*     */     }
/*  92 */     String str = paramString.substring(1);
/*  93 */     if ((str != null) && (str.startsWith("xpointer(id("))) {
/*  94 */       int i = str.indexOf('\'');
/*  95 */       int j = str.indexOf('\'', i + 1);
/*  96 */       str = str.substring(i + 1, j);
/*     */     }
/*  98 */     return str;
/*     */   }
/*     */ 
/*     */   public static boolean sameDocumentURI(String paramString)
/*     */   {
/* 105 */     return (paramString != null) && ((paramString.length() == 0) || (paramString.charAt(0) == '#'));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.Utils
 * JD-Core Version:    0.6.2
 */