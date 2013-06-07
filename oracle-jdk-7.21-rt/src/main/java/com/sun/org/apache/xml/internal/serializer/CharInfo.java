/*     */ package com.sun.org.apache.xml.internal.serializer;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*     */ import com.sun.org.apache.xml.internal.serializer.utils.Messages;
/*     */ import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
/*     */ import com.sun.org.apache.xml.internal.serializer.utils.Utils;
/*     */ import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.PropertyResourceBundle;
/*     */ import java.util.ResourceBundle;
/*     */ import javax.xml.transform.TransformerException;
/*     */ 
/*     */ final class CharInfo
/*     */ {
/*  58 */   private HashMap m_charToString = new HashMap();
/*     */   public static final String HTML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.HTMLEntities";
/*     */   public static final String XML_ENTITIES_RESOURCE = "com.sun.org.apache.xml.internal.serializer.XMLEntities";
/*     */   public static final char S_HORIZONAL_TAB = '\t';
/*     */   public static final char S_LINEFEED = '\n';
/*     */   public static final char S_CARRIAGERETURN = '\r';
/*     */   final boolean onlyQuotAmpLtGt;
/*     */   private static final int ASCII_MAX = 128;
/*  96 */   private boolean[] isSpecialAttrASCII = new boolean[''];
/*     */ 
/* 101 */   private boolean[] isSpecialTextASCII = new boolean[''];
/*     */ 
/* 103 */   private boolean[] isCleanTextASCII = new boolean[''];
/*     */ 
/* 110 */   private int[] array_of_bits = createEmptySetOfIntegers(65535);
/*     */   private static final int SHIFT_PER_WORD = 5;
/*     */   private static final int LOW_ORDER_BITMASK = 31;
/*     */   private int firstWordNotUsed;
/* 538 */   private static HashMap m_getCharInfoCache = new HashMap();
/*     */ 
/*     */   private CharInfo(String entitiesResource, String method)
/*     */   {
/* 163 */     this(entitiesResource, method, false);
/*     */   }
/*     */ 
/*     */   private CharInfo(String entitiesResource, String method, boolean internal)
/*     */   {
/* 168 */     ResourceBundle entities = null;
/* 169 */     boolean noExtraEntities = true;
/*     */ 
/* 179 */     if (internal)
/*     */     {
/*     */       try
/*     */       {
/* 183 */         entities = PropertyResourceBundle.getBundle(entitiesResource);
/*     */       } catch (Exception e) {
/*     */       }
/*     */     }
/* 187 */     if (entities != null) {
/* 188 */       Enumeration keys = entities.getKeys();
/* 189 */       while (keys.hasMoreElements()) {
/* 190 */         String name = (String)keys.nextElement();
/* 191 */         String value = entities.getString(name);
/* 192 */         int code = Integer.parseInt(value);
/* 193 */         defineEntity(name, (char)code);
/* 194 */         if (extraEntity(code))
/* 195 */           noExtraEntities = false;
/*     */       }
/* 197 */       set(10);
/* 198 */       set(13);
/*     */     } else {
/* 200 */       InputStream is = null;
/*     */       try
/*     */       {
/* 205 */         if (internal) {
/* 206 */           is = CharInfo.class.getResourceAsStream(entitiesResource);
/*     */         } else {
/* 208 */           ClassLoader cl = ObjectFactory.findClassLoader();
/* 209 */           if (cl == null)
/* 210 */             is = ClassLoader.getSystemResourceAsStream(entitiesResource);
/*     */           else {
/* 212 */             is = cl.getResourceAsStream(entitiesResource);
/*     */           }
/*     */ 
/* 215 */           if (is == null)
/*     */             try {
/* 217 */               URL url = new URL(entitiesResource);
/* 218 */               is = url.openStream();
/*     */             }
/*     */             catch (Exception e) {
/*     */             }
/*     */         }
/* 223 */         if (is == null) {
/* 224 */           throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_FIND", new Object[] { entitiesResource, entitiesResource }));
/*     */         }
/*     */ 
/*     */         BufferedReader reader;
/*     */         try
/*     */         {
/* 251 */           reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */         } catch (UnsupportedEncodingException e) {
/* 253 */           reader = new BufferedReader(new InputStreamReader(is));
/*     */         }
/*     */ 
/* 256 */         String line = reader.readLine();
/*     */ 
/* 258 */         while (line != null) {
/* 259 */           if ((line.length() == 0) || (line.charAt(0) == '#')) {
/* 260 */             line = reader.readLine();
/*     */           }
/*     */           else
/*     */           {
/* 265 */             int index = line.indexOf(' ');
/*     */ 
/* 267 */             if (index > 1) {
/* 268 */               String name = line.substring(0, index);
/*     */ 
/* 270 */               index++;
/*     */ 
/* 272 */               if (index < line.length()) {
/* 273 */                 String value = line.substring(index);
/* 274 */                 index = value.indexOf(' ');
/*     */ 
/* 276 */                 if (index > 0) {
/* 277 */                   value = value.substring(0, index);
/*     */                 }
/*     */ 
/* 280 */                 int code = Integer.parseInt(value);
/*     */ 
/* 282 */                 defineEntity(name, (char)code);
/* 283 */                 if (extraEntity(code)) {
/* 284 */                   noExtraEntities = false;
/*     */                 }
/*     */               }
/*     */             }
/* 288 */             line = reader.readLine();
/*     */           }
/*     */         }
/* 291 */         is.close();
/* 292 */         set(10);
/* 293 */         set(13);
/*     */       } catch (Exception e) {
/* 295 */         throw new RuntimeException(Utils.messages.createMessage("ER_RESOURCE_COULD_NOT_LOAD", new Object[] { entitiesResource, e.toString(), entitiesResource, e.toString() }));
/*     */       }
/*     */       finally
/*     */       {
/* 303 */         if (is != null) {
/*     */           try {
/* 305 */             is.close();
/*     */           }
/*     */           catch (Exception except)
/*     */           {
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 316 */     for (int ch = 0; ch < 128; ch++) {
/* 317 */       if (((32 > ch) && (10 != ch) && (13 != ch) && (9 != ch)) || ((!get(ch)) || (34 == ch)))
/*     */       {
/* 320 */         this.isCleanTextASCII[ch] = true;
/* 321 */         this.isSpecialTextASCII[ch] = false;
/*     */       }
/*     */       else {
/* 324 */         this.isCleanTextASCII[ch] = false;
/* 325 */         this.isSpecialTextASCII[ch] = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 330 */     this.onlyQuotAmpLtGt = noExtraEntities;
/*     */ 
/* 333 */     for (int i = 0; i < 128; i++) {
/* 334 */       this.isSpecialAttrASCII[i] = get(i);
/*     */     }
/*     */ 
/* 345 */     if ("xml".equals(method))
/*     */     {
/* 347 */       this.isSpecialAttrASCII[9] = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void defineEntity(String name, char value)
/*     */   {
/* 364 */     StringBuilder sb = new StringBuilder("&");
/* 365 */     sb.append(name);
/* 366 */     sb.append(';');
/* 367 */     String entityString = sb.toString();
/*     */ 
/* 369 */     defineChar2StringMapping(entityString, value);
/*     */   }
/*     */ 
/*     */   String getOutputStringForChar(char value)
/*     */   {
/* 396 */     CharKey charKey = new CharKey();
/* 397 */     charKey.setChar(value);
/* 398 */     return (String)this.m_charToString.get(charKey);
/*     */   }
/*     */ 
/*     */   final boolean isSpecialAttrChar(int value)
/*     */   {
/* 416 */     if (value < 128) {
/* 417 */       return this.isSpecialAttrASCII[value];
/*     */     }
/*     */ 
/* 421 */     return get(value);
/*     */   }
/*     */ 
/*     */   final boolean isSpecialTextChar(int value)
/*     */   {
/* 439 */     if (value < 128) {
/* 440 */       return this.isSpecialTextASCII[value];
/*     */     }
/*     */ 
/* 444 */     return get(value);
/*     */   }
/*     */ 
/*     */   final boolean isTextASCIIClean(int value)
/*     */   {
/* 456 */     return this.isCleanTextASCII[value];
/*     */   }
/*     */ 
/*     */   private static CharInfo getCharInfoBasedOnPrivilege(String entitiesFileName, final String method, final boolean internal)
/*     */   {
/* 471 */     return (CharInfo)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 474 */         return new CharInfo(this.val$entitiesFileName, method, internal, null);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static CharInfo getCharInfo(String entitiesFileName, String method)
/*     */   {
/* 500 */     CharInfo charInfo = (CharInfo)m_getCharInfoCache.get(entitiesFileName);
/* 501 */     if (charInfo != null) {
/* 502 */       return charInfo;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 507 */       charInfo = getCharInfoBasedOnPrivilege(entitiesFileName, method, true);
/*     */ 
/* 509 */       m_getCharInfoCache.put(entitiesFileName, charInfo);
/* 510 */       return charInfo;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       try {
/* 515 */         return getCharInfoBasedOnPrivilege(entitiesFileName, method, false);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */         String absoluteEntitiesFileName;
/* 521 */         if (entitiesFileName.indexOf(':') < 0)
/* 522 */           absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURIFromRelative(entitiesFileName);
/*     */         else
/*     */           try
/*     */           {
/* 526 */             absoluteEntitiesFileName = SystemIDResolver.getAbsoluteURI(entitiesFileName, null);
/*     */           }
/*     */           catch (TransformerException te)
/*     */           {
/*     */             String absoluteEntitiesFileName;
/* 529 */             throw new WrappedRuntimeException(te);
/*     */           }
/*     */       }
/*     */     }
/* 533 */     return getCharInfoBasedOnPrivilege(entitiesFileName, method, false);
/*     */   }
/*     */ 
/*     */   private static int arrayIndex(int i)
/*     */   {
/* 547 */     return i >> 5;
/*     */   }
/*     */ 
/*     */   private static int bit(int i)
/*     */   {
/* 556 */     int ret = 1 << (i & 0x1F);
/* 557 */     return ret;
/*     */   }
/*     */ 
/*     */   private int[] createEmptySetOfIntegers(int max)
/*     */   {
/* 565 */     this.firstWordNotUsed = 0;
/*     */ 
/* 567 */     int[] arr = new int[arrayIndex(max - 1) + 1];
/* 568 */     return arr;
/*     */   }
/*     */ 
/*     */   private final void set(int i)
/*     */   {
/* 579 */     setASCIIdirty(i);
/*     */ 
/* 581 */     int j = i >> 5;
/* 582 */     int k = j + 1;
/*     */ 
/* 584 */     if (this.firstWordNotUsed < k) {
/* 585 */       this.firstWordNotUsed = k;
/*     */     }
/* 587 */     this.array_of_bits[j] |= 1 << (i & 0x1F);
/*     */   }
/*     */ 
/*     */   private final boolean get(int i)
/*     */   {
/* 603 */     boolean in_the_set = false;
/* 604 */     int j = i >> 5;
/*     */ 
/* 607 */     if (j < this.firstWordNotUsed) {
/* 608 */       in_the_set = (this.array_of_bits[j] & 1 << (i & 0x1F)) != 0;
/*     */     }
/*     */ 
/* 611 */     return in_the_set;
/*     */   }
/*     */ 
/*     */   private boolean extraEntity(int entityValue)
/*     */   {
/* 623 */     boolean extra = false;
/* 624 */     if (entityValue < 128)
/*     */     {
/* 626 */       switch (entityValue)
/*     */       {
/*     */       case 34:
/*     */       case 38:
/*     */       case 60:
/*     */       case 62:
/* 632 */         break;
/*     */       default:
/* 634 */         extra = true;
/*     */       }
/*     */     }
/* 637 */     return extra;
/*     */   }
/*     */ 
/*     */   private void setASCIIdirty(int j)
/*     */   {
/* 648 */     if ((0 <= j) && (j < 128))
/*     */     {
/* 650 */       this.isCleanTextASCII[j] = false;
/* 651 */       this.isSpecialTextASCII[j] = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setASCIIclean(int j)
/*     */   {
/* 663 */     if ((0 <= j) && (j < 128))
/*     */     {
/* 665 */       this.isCleanTextASCII[j] = true;
/* 666 */       this.isSpecialTextASCII[j] = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void defineChar2StringMapping(String outputString, char inputChar)
/*     */   {
/* 672 */     CharKey character = new CharKey(inputChar);
/* 673 */     this.m_charToString.put(character, outputString);
/* 674 */     set(inputChar);
/*     */   }
/*     */ 
/*     */   private static class CharKey
/*     */   {
/*     */     private char m_char;
/*     */ 
/*     */     public CharKey(char key)
/*     */     {
/* 699 */       this.m_char = key;
/*     */     }
/*     */ 
/*     */     public CharKey()
/*     */     {
/*     */     }
/*     */ 
/*     */     public final void setChar(char c)
/*     */     {
/* 718 */       this.m_char = c;
/*     */     }
/*     */ 
/*     */     public final int hashCode()
/*     */     {
/* 730 */       return this.m_char;
/*     */     }
/*     */ 
/*     */     public final boolean equals(Object obj)
/*     */     {
/* 742 */       return ((CharKey)obj).m_char == this.m_char;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serializer.CharInfo
 * JD-Core Version:    0.6.2
 */