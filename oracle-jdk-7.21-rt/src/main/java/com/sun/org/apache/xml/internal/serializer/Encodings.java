/*     */ package com.sun.org.apache.xml.internal.serializer;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
/*     */ import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public final class Encodings
/*     */ {
/*     */   private static final int m_defaultLastPrintable = 127;
/*     */   private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";
/*     */   private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";
/*     */   static final String DEFAULT_MIME_ENCODING = "UTF-8";
/* 460 */   private static final HashMap _encodingTableKeyJava = new HashMap();
/* 461 */   private static final HashMap _encodingTableKeyMime = new HashMap();
/* 462 */   private static final EncodingInfo[] _encodings = loadEncodingInfo();
/*     */ 
/*     */   static Writer getWriter(OutputStream output, String encoding)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  82 */     for (int i = 0; i < _encodings.length; i++)
/*     */     {
/*  84 */       if (_encodings[i].name.equalsIgnoreCase(encoding))
/*     */       {
/*     */         try
/*     */         {
/*  88 */           return new BufferedWriter(new OutputStreamWriter(output, _encodings[i].javaName));
/*     */         }
/*     */         catch (IllegalArgumentException iae)
/*     */         {
/*     */         }
/*     */         catch (UnsupportedEncodingException usee)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 106 */       return new BufferedWriter(new OutputStreamWriter(output, encoding));
/*     */     }
/*     */     catch (IllegalArgumentException iae) {
/*     */     }
/* 110 */     throw new UnsupportedEncodingException(encoding);
/*     */   }
/*     */ 
/*     */   public static int getLastPrintable()
/*     */   {
/* 123 */     return 127;
/*     */   }
/*     */ 
/*     */   static EncodingInfo getEncodingInfo(String encoding)
/*     */   {
/* 143 */     String normalizedEncoding = toUpperCaseFast(encoding);
/* 144 */     EncodingInfo ei = (EncodingInfo)_encodingTableKeyJava.get(normalizedEncoding);
/* 145 */     if (ei == null)
/* 146 */       ei = (EncodingInfo)_encodingTableKeyMime.get(normalizedEncoding);
/* 147 */     if (ei == null)
/*     */     {
/* 149 */       ei = new EncodingInfo(null, null);
/*     */     }
/*     */ 
/* 152 */     return ei;
/*     */   }
/*     */ 
/*     */   private static String toUpperCaseFast(String s)
/*     */   {
/* 167 */     boolean different = false;
/* 168 */     int mx = s.length();
/* 169 */     char[] chars = new char[mx];
/* 170 */     for (int i = 0; i < mx; i++) {
/* 171 */       char ch = s.charAt(i);
/*     */ 
/* 173 */       if (('a' <= ch) && (ch <= 'z'))
/*     */       {
/* 175 */         ch = (char)(ch + '\0,0');
/* 176 */         different = true;
/*     */       }
/* 178 */       chars[i] = ch;
/*     */     }
/*     */     String upper;
/*     */     String upper;
/* 184 */     if (different)
/* 185 */       upper = String.valueOf(chars);
/*     */     else {
/* 187 */       upper = s;
/*     */     }
/* 189 */     return upper;
/*     */   }
/*     */ 
/*     */   static String getMimeEncoding(String encoding)
/*     */   {
/* 214 */     if (null == encoding)
/*     */     {
/*     */       try
/*     */       {
/* 222 */         encoding = SecuritySupport.getSystemProperty("file.encoding", "UTF8");
/*     */ 
/* 224 */         if (null != encoding)
/*     */         {
/* 234 */           String jencoding = (encoding.equalsIgnoreCase("Cp1252")) || (encoding.equalsIgnoreCase("ISO8859_1")) || (encoding.equalsIgnoreCase("8859_1")) || (encoding.equalsIgnoreCase("UTF8")) ? "UTF-8" : convertJava2MimeEncoding(encoding);
/*     */ 
/* 242 */           encoding = null != jencoding ? jencoding : "UTF-8";
/*     */         }
/*     */         else
/*     */         {
/* 247 */           encoding = "UTF-8";
/*     */         }
/*     */       }
/*     */       catch (SecurityException se)
/*     */       {
/* 252 */         encoding = "UTF-8";
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 257 */       encoding = convertJava2MimeEncoding(encoding);
/*     */     }
/*     */ 
/* 260 */     return encoding;
/*     */   }
/*     */ 
/*     */   private static String convertJava2MimeEncoding(String encoding)
/*     */   {
/* 272 */     EncodingInfo enc = (EncodingInfo)_encodingTableKeyJava.get(encoding.toUpperCase());
/*     */ 
/* 274 */     if (null != enc)
/* 275 */       return enc.name;
/* 276 */     return encoding;
/*     */   }
/*     */ 
/*     */   public static String convertMime2JavaEncoding(String encoding)
/*     */   {
/* 289 */     for (int i = 0; i < _encodings.length; i++)
/*     */     {
/* 291 */       if (_encodings[i].name.equalsIgnoreCase(encoding))
/*     */       {
/* 293 */         return _encodings[i].javaName;
/*     */       }
/*     */     }
/*     */ 
/* 297 */     return encoding;
/*     */   }
/*     */ 
/*     */   private static EncodingInfo[] loadEncodingInfo()
/*     */   {
/*     */     try
/*     */     {
/* 311 */       String urlString = null;
/* 312 */       InputStream is = null;
/*     */       try
/*     */       {
/* 316 */         urlString = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.serialize.encodings", "");
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*     */       }
/*     */ 
/* 322 */       if ((urlString != null) && (urlString.length() > 0)) {
/* 323 */         URL url = new URL(urlString);
/* 324 */         is = url.openStream();
/*     */       }
/*     */ 
/* 327 */       if (is == null) {
/* 328 */         is = SecuritySupport.getResourceAsStream("com/sun/org/apache/xml/internal/serializer/Encodings.properties");
/*     */       }
/*     */ 
/* 331 */       Properties props = new Properties();
/* 332 */       if (is != null) {
/* 333 */         props.load(is);
/* 334 */         is.close();
/*     */       }
/*     */ 
/* 344 */       int totalEntries = props.size();
/* 345 */       int totalMimeNames = 0;
/* 346 */       Enumeration keys = props.keys();
/* 347 */       for (int i = 0; i < totalEntries; i++)
/*     */       {
/* 349 */         String javaName = (String)keys.nextElement();
/* 350 */         String val = props.getProperty(javaName);
/* 351 */         totalMimeNames++;
/* 352 */         int pos = val.indexOf(' ');
/* 353 */         for (int j = 0; j < pos; j++)
/* 354 */           if (val.charAt(j) == ',')
/* 355 */             totalMimeNames++;
/*     */       }
/* 357 */       EncodingInfo[] ret = new EncodingInfo[totalMimeNames];
/* 358 */       int j = 0;
/* 359 */       keys = props.keys();
/* 360 */       for (int i = 0; i < totalEntries; i++)
/*     */       {
/* 362 */         String javaName = (String)keys.nextElement();
/* 363 */         String val = props.getProperty(javaName);
/* 364 */         int pos = val.indexOf(' ');
/*     */         String mimeName;
/* 367 */         if (pos < 0)
/*     */         {
/* 372 */           mimeName = val;
/*     */         }
/*     */         else
/*     */         {
/* 379 */           StringTokenizer st = new StringTokenizer(val.substring(0, pos), ",");
/*     */ 
/* 381 */           for (boolean first = true; 
/* 382 */             st.hasMoreTokens(); 
/* 383 */             first = false)
/*     */           {
/* 385 */             String mimeName = st.nextToken();
/* 386 */             ret[j] = new EncodingInfo(mimeName, javaName);
/*     */ 
/* 388 */             _encodingTableKeyMime.put(mimeName.toUpperCase(), ret[j]);
/*     */ 
/* 391 */             if (first) {
/* 392 */               _encodingTableKeyJava.put(javaName.toUpperCase(), ret[j]);
/*     */             }
/*     */ 
/* 395 */             j++;
/*     */           }
/*     */         }
/*     */       }
/* 399 */       return ret;
/*     */     }
/*     */     catch (MalformedURLException mue)
/*     */     {
/* 403 */       throw new WrappedRuntimeException(mue);
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 407 */       throw new WrappedRuntimeException(ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   static boolean isHighUTF16Surrogate(char ch)
/*     */   {
/* 419 */     return (55296 <= ch) && (ch <= 56319);
/*     */   }
/*     */ 
/*     */   static boolean isLowUTF16Surrogate(char ch)
/*     */   {
/* 429 */     return (56320 <= ch) && (ch <= 57343);
/*     */   }
/*     */ 
/*     */   static int toCodePoint(char highSurrogate, char lowSurrogate)
/*     */   {
/* 440 */     int codePoint = (highSurrogate - 55296 << 10) + (lowSurrogate - 56320) + 65536;
/*     */ 
/* 444 */     return codePoint;
/*     */   }
/*     */ 
/*     */   static int toCodePoint(char ch)
/*     */   {
/* 456 */     int codePoint = ch;
/* 457 */     return codePoint;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.serializer.Encodings
 * JD-Core Version:    0.6.2
 */