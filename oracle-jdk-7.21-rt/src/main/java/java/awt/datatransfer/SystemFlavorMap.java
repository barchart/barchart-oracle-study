/*      */ package java.awt.datatransfer;
/*      */ 
/*      */ import java.awt.Toolkit;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import sun.awt.datatransfer.DataTransferer;
/*      */ 
/*      */ public final class SystemFlavorMap
/*      */   implements FlavorMap, FlavorTable
/*      */ {
/*   73 */   private static String JavaMIME = "JAVA_DATAFLAVOR:";
/*      */ 
/*   78 */   private static final WeakHashMap flavorMaps = new WeakHashMap();
/*      */   private static final String keyValueSeparators = "=: \t\r\n\f";
/*      */   private static final String strictKeyValueSeparators = "=:";
/*      */   private static final String whiteSpaceChars = " \t\r\n\f";
/*   91 */   private static final String[] UNICODE_TEXT_CLASSES = { "java.io.Reader", "java.lang.String", "java.nio.CharBuffer", "\"[C\"" };
/*      */ 
/*   99 */   private static final String[] ENCODED_TEXT_CLASSES = { "java.io.InputStream", "java.nio.ByteBuffer", "\"[B\"" };
/*      */   private static final String TEXT_PLAIN_BASE_TYPE = "text/plain";
/*      */   private static final boolean SYNTHESIZE_IF_NOT_FOUND = true;
/*  121 */   private Map nativeToFlavor = new HashMap();
/*      */ 
/*  142 */   private Map flavorToNative = new HashMap();
/*      */ 
/*  161 */   private boolean isMapInitialized = false;
/*      */ 
/*  167 */   private Map getNativesForFlavorCache = new HashMap();
/*      */ 
/*  173 */   private Map getFlavorsForNativeCache = new HashMap();
/*      */ 
/*  181 */   private Set disabledMappingGenerationKeys = new HashSet();
/*      */ 
/*      */   private Map getNativeToFlavor()
/*      */   {
/*  131 */     if (!this.isMapInitialized) {
/*  132 */       initSystemFlavorMap();
/*      */     }
/*  134 */     return this.nativeToFlavor;
/*      */   }
/*      */ 
/*      */   private synchronized Map getFlavorToNative()
/*      */   {
/*  152 */     if (!this.isMapInitialized) {
/*  153 */       initSystemFlavorMap();
/*      */     }
/*  155 */     return this.flavorToNative;
/*      */   }
/*      */ 
/*      */   public static FlavorMap getDefaultFlavorMap()
/*      */   {
/*  187 */     ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
/*      */ 
/*  189 */     if (localClassLoader == null)
/*  190 */       localClassLoader = ClassLoader.getSystemClassLoader();
/*      */     Object localObject1;
/*  195 */     synchronized (flavorMaps) {
/*  196 */       localObject1 = (FlavorTable)flavorMaps.get(localClassLoader);
/*  197 */       if (localObject1 == null) {
/*  198 */         localObject1 = new SystemFlavorMap();
/*  199 */         flavorMaps.put(localClassLoader, localObject1);
/*      */       }
/*      */     }
/*      */ 
/*  203 */     return localObject1;
/*      */   }
/*      */ 
/*      */   private void initSystemFlavorMap()
/*      */   {
/*  215 */     if (this.isMapInitialized) {
/*  216 */       return;
/*      */     }
/*      */ 
/*  219 */     this.isMapInitialized = true;
/*  220 */     BufferedReader localBufferedReader1 = (BufferedReader)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public BufferedReader run()
/*      */       {
/*  224 */         String str = System.getProperty("java.home") + File.separator + "lib" + File.separator + "flavormap.properties";
/*      */         try
/*      */         {
/*  231 */           return new BufferedReader(new InputStreamReader(new File(str).toURI().toURL().openStream(), "ISO-8859-1"));
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException)
/*      */         {
/*  235 */           System.err.println("MalformedURLException:" + localMalformedURLException + " while loading default flavormap.properties file:" + str);
/*      */         } catch (IOException localIOException) {
/*  237 */           System.err.println("IOException:" + localIOException + " while loading default flavormap.properties file:" + str);
/*      */         }
/*  239 */         return null;
/*      */       }
/*      */     });
/*  243 */     BufferedReader localBufferedReader2 = (BufferedReader)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public BufferedReader run()
/*      */       {
/*  247 */         String str = Toolkit.getProperty("AWT.DnD.flavorMapFileURL", null);
/*      */ 
/*  249 */         if (str == null) {
/*  250 */           return null;
/*      */         }
/*      */         try
/*      */         {
/*  254 */           return new BufferedReader(new InputStreamReader(new URL(str).openStream(), "ISO-8859-1"));
/*      */         }
/*      */         catch (MalformedURLException localMalformedURLException)
/*      */         {
/*  258 */           System.err.println("MalformedURLException:" + localMalformedURLException + " while reading AWT.DnD.flavorMapFileURL:" + str);
/*      */         } catch (IOException localIOException) {
/*  260 */           System.err.println("IOException:" + localIOException + " while reading AWT.DnD.flavorMapFileURL:" + str);
/*      */         }
/*  262 */         return null;
/*      */       }
/*      */     });
/*  266 */     if (localBufferedReader1 != null) {
/*      */       try {
/*  268 */         parseAndStoreReader(localBufferedReader1);
/*      */       } catch (IOException localIOException1) {
/*  270 */         System.err.println("IOException:" + localIOException1 + " while parsing default flavormap.properties file");
/*      */       }
/*      */     }
/*      */ 
/*  274 */     if (localBufferedReader2 != null)
/*      */       try {
/*  276 */         parseAndStoreReader(localBufferedReader2);
/*      */       } catch (IOException localIOException2) {
/*  278 */         System.err.println("IOException:" + localIOException2 + " while parsing AWT.DnD.flavorMapFileURL");
/*      */       }
/*      */   }
/*      */ 
/*      */   private void parseAndStoreReader(BufferedReader paramBufferedReader)
/*      */     throws IOException
/*      */   {
/*      */     while (true)
/*      */     {
/*  289 */       String str1 = paramBufferedReader.readLine();
/*  290 */       if (str1 == null) {
/*  291 */         return;
/*      */       }
/*      */ 
/*  294 */       if (str1.length() > 0)
/*      */       {
/*  296 */         int i = str1.charAt(0);
/*  297 */         if ((i != 35) && (i != 33))
/*      */         {
/*      */           int m;
/*  298 */           while (continueLine(str1)) {
/*  299 */             String str2 = paramBufferedReader.readLine();
/*  300 */             if (str2 == null) {
/*  301 */               str2 = "";
/*      */             }
/*  303 */             String str3 = str1.substring(0, str1.length() - 1);
/*      */ 
/*  306 */             m = 0;
/*  307 */             while ((m < str2.length()) && 
/*  308 */               (" \t\r\n\f".indexOf(str2.charAt(m)) != -1)) {
/*  307 */               m++;
/*      */             }
/*      */ 
/*  314 */             str2 = str2.substring(m, str2.length());
/*      */ 
/*  316 */             str1 = str3 + str2;
/*      */           }
/*      */ 
/*  320 */           int j = str1.length();
/*  321 */           int k = 0;
/*  322 */           while ((k < j) && 
/*  323 */             (" \t\r\n\f".indexOf(str1.charAt(k)) != -1)) {
/*  322 */             k++;
/*      */           }
/*      */ 
/*  330 */           if (k != j)
/*      */           {
/*  335 */             for (m = k; 
/*  336 */               m < j; m++) {
/*  337 */               n = str1.charAt(m);
/*  338 */               if (n == 92)
/*  339 */                 m++;
/*  340 */               else if ("=: \t\r\n\f".indexOf(n) != -1)
/*      */                 {
/*      */                   break;
/*      */                 }
/*      */ 
/*      */             }
/*      */ 
/*  347 */             int n = m;
/*  348 */             while ((n < j) && 
/*  349 */               (" \t\r\n\f".indexOf(str1.charAt(n)) != -1)) {
/*  348 */               n++;
/*      */             }
/*      */ 
/*  356 */             if ((n < j) && 
/*  357 */               ("=:".indexOf(str1.charAt(n)) != -1))
/*      */             {
/*  359 */               n++;
/*      */             }
/*      */ 
/*  364 */             while ((n < j) && 
/*  365 */               (" \t\r\n\f".indexOf(str1.charAt(n)) != -1))
/*      */             {
/*  369 */               n++;
/*      */             }
/*      */ 
/*  372 */             String str4 = str1.substring(k, m);
/*  373 */             String str5 = m < j ? str1.substring(n, j) : "";
/*      */ 
/*  378 */             str4 = loadConvert(str4);
/*  379 */             str5 = loadConvert(str5);
/*      */             try
/*      */             {
/*  382 */               MimeType localMimeType = new MimeType(str5);
/*  383 */               if ("text".equals(localMimeType.getPrimaryType())) {
/*  384 */                 String str6 = localMimeType.getParameter("charset");
/*  385 */                 if (DataTransferer.doesSubtypeSupportCharset(localMimeType.getSubType(), str6))
/*      */                 {
/*  392 */                   DataTransferer localDataTransferer = DataTransferer.getInstance();
/*      */ 
/*  394 */                   if (localDataTransferer != null) {
/*  395 */                     localDataTransferer.registerTextFlavorProperties(str4, str6, localMimeType.getParameter("eoln"), localMimeType.getParameter("terminators"));
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*  406 */                 localMimeType.removeParameter("charset");
/*  407 */                 localMimeType.removeParameter("class");
/*  408 */                 localMimeType.removeParameter("eoln");
/*  409 */                 localMimeType.removeParameter("terminators");
/*  410 */                 str5 = localMimeType.toString();
/*      */               }
/*      */             } catch (MimeTypeParseException localMimeTypeParseException) {
/*  413 */               localMimeTypeParseException.printStackTrace();
/*  414 */             }continue;
/*      */             DataFlavor localDataFlavor;
/*      */             try
/*      */             {
/*  419 */               localDataFlavor = new DataFlavor(str5);
/*      */             } catch (Exception localException1) {
/*      */               try {
/*  422 */                 localDataFlavor = new DataFlavor(str5, (String)null);
/*      */               } catch (Exception localException2) {
/*  424 */                 localException2.printStackTrace(); } 
/*  425 */             }continue;
/*      */ 
/*  431 */             if ("text".equals(localDataFlavor.getPrimaryType())) {
/*  432 */               store(str5, str4, getFlavorToNative());
/*  433 */               store(str4, str5, getNativeToFlavor());
/*      */             } else {
/*  435 */               store(localDataFlavor, str4, getFlavorToNative());
/*  436 */               store(str4, localDataFlavor, getNativeToFlavor());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean continueLine(String paramString)
/*      */   {
/*  447 */     int i = 0;
/*  448 */     int j = paramString.length() - 1;
/*  449 */     while ((j >= 0) && (paramString.charAt(j--) == '\\')) {
/*  450 */       i++;
/*      */     }
/*  452 */     return i % 2 == 1;
/*      */   }
/*      */ 
/*      */   private String loadConvert(String paramString)
/*      */   {
/*  460 */     int j = paramString.length();
/*  461 */     StringBuilder localStringBuilder = new StringBuilder(j);
/*      */ 
/*  463 */     for (int k = 0; k < j; ) {
/*  464 */       int i = paramString.charAt(k++);
/*  465 */       if (i == 92) {
/*  466 */         i = paramString.charAt(k++);
/*  467 */         if (i == 117)
/*      */         {
/*  469 */           int m = 0;
/*  470 */           for (int n = 0; n < 4; n++) {
/*  471 */             i = paramString.charAt(k++);
/*  472 */             switch (i) { case 48:
/*      */             case 49:
/*      */             case 50:
/*      */             case 51:
/*      */             case 52:
/*      */             case 53:
/*      */             case 54:
/*      */             case 55:
/*      */             case 56:
/*      */             case 57:
/*  475 */               m = (m << 4) + i - 48;
/*  476 */               break;
/*      */             case 97:
/*      */             case 98:
/*      */             case 99:
/*      */             case 100:
/*      */             case 101:
/*      */             case 102:
/*  480 */               m = (m << 4) + 10 + i - 97;
/*  481 */               break;
/*      */             case 65:
/*      */             case 66:
/*      */             case 67:
/*      */             case 68:
/*      */             case 69:
/*      */             case 70:
/*  485 */               m = (m << 4) + 10 + i - 65;
/*  486 */               break;
/*      */             case 58:
/*      */             case 59:
/*      */             case 60:
/*      */             case 61:
/*      */             case 62:
/*      */             case 63:
/*      */             case 64:
/*      */             case 71:
/*      */             case 72:
/*      */             case 73:
/*      */             case 74:
/*      */             case 75:
/*      */             case 76:
/*      */             case 77:
/*      */             case 78:
/*      */             case 79:
/*      */             case 80:
/*      */             case 81:
/*      */             case 82:
/*      */             case 83:
/*      */             case 84:
/*      */             case 85:
/*      */             case 86:
/*      */             case 87:
/*      */             case 88:
/*      */             case 89:
/*      */             case 90:
/*      */             case 91:
/*      */             case 92:
/*      */             case 93:
/*      */             case 94:
/*      */             case 95:
/*      */             case 96:
/*      */             default:
/*  489 */               throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  494 */           localStringBuilder.append((char)m);
/*      */         } else {
/*  496 */           if (i == 116)
/*  497 */             i = 9;
/*  498 */           else if (i == 114)
/*  499 */             i = 13;
/*  500 */           else if (i == 110)
/*  501 */             i = 10;
/*  502 */           else if (i == 102) {
/*  503 */             i = 12;
/*      */           }
/*  505 */           localStringBuilder.append(i);
/*      */         }
/*      */       } else {
/*  508 */         localStringBuilder.append(i);
/*      */       }
/*      */     }
/*  511 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   private void store(Object paramObject1, Object paramObject2, Map paramMap)
/*      */   {
/*  521 */     Object localObject = (List)paramMap.get(paramObject1);
/*  522 */     if (localObject == null) {
/*  523 */       localObject = new ArrayList(1);
/*  524 */       paramMap.put(paramObject1, localObject);
/*      */     }
/*  526 */     if (!((List)localObject).contains(paramObject2))
/*  527 */       ((List)localObject).add(paramObject2);
/*      */   }
/*      */ 
/*      */   private List nativeToFlavorLookup(String paramString)
/*      */   {
/*  538 */     Object localObject1 = (List)getNativeToFlavor().get(paramString);
/*      */     Object localObject2;
/*      */     Object localObject3;
/*  540 */     if ((paramString != null) && (!this.disabledMappingGenerationKeys.contains(paramString))) {
/*  541 */       localObject2 = DataTransferer.getInstance();
/*  542 */       if (localObject2 != null) {
/*  543 */         localObject3 = ((DataTransferer)localObject2).getPlatformMappingsForNative(paramString);
/*      */ 
/*  545 */         if (!((List)localObject3).isEmpty()) {
/*  546 */           if (localObject1 != null) {
/*  547 */             ((List)localObject3).removeAll(new HashSet((Collection)localObject1));
/*      */ 
/*  552 */             ((List)localObject3).addAll((Collection)localObject1);
/*      */           }
/*  554 */           localObject1 = localObject3;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  559 */     if ((localObject1 == null) && (isJavaMIMEType(paramString))) {
/*  560 */       localObject2 = decodeJavaMIMEType(paramString);
/*  561 */       localObject3 = null;
/*      */       try
/*      */       {
/*  564 */         localObject3 = new DataFlavor((String)localObject2);
/*      */       } catch (Exception localException) {
/*  566 */         System.err.println("Exception \"" + localException.getClass().getName() + ": " + localException.getMessage() + "\"while constructing DataFlavor for: " + (String)localObject2);
/*      */       }
/*      */ 
/*  572 */       if (localObject3 != null) {
/*  573 */         localObject1 = new ArrayList(1);
/*  574 */         getNativeToFlavor().put(paramString, localObject1);
/*  575 */         ((List)localObject1).add(localObject3);
/*  576 */         this.getFlavorsForNativeCache.remove(paramString);
/*  577 */         this.getFlavorsForNativeCache.remove(null);
/*      */ 
/*  579 */         Object localObject4 = (List)getFlavorToNative().get(localObject3);
/*  580 */         if (localObject4 == null) {
/*  581 */           localObject4 = new ArrayList(1);
/*  582 */           getFlavorToNative().put(localObject3, localObject4);
/*      */         }
/*  584 */         ((List)localObject4).add(paramString);
/*  585 */         this.getNativesForFlavorCache.remove(localObject3);
/*  586 */         this.getNativesForFlavorCache.remove(null);
/*      */       }
/*      */     }
/*      */ 
/*  590 */     return localObject1 != null ? localObject1 : new ArrayList(0);
/*      */   }
/*      */ 
/*      */   private List flavorToNativeLookup(DataFlavor paramDataFlavor, boolean paramBoolean)
/*      */   {
/*  603 */     Object localObject1 = (List)getFlavorToNative().get(paramDataFlavor);
/*      */     Object localObject2;
/*      */     Object localObject3;
/*  605 */     if ((paramDataFlavor != null) && (!this.disabledMappingGenerationKeys.contains(paramDataFlavor))) {
/*  606 */       localObject2 = DataTransferer.getInstance();
/*  607 */       if (localObject2 != null) {
/*  608 */         localObject3 = ((DataTransferer)localObject2).getPlatformMappingsForFlavor(paramDataFlavor);
/*      */ 
/*  610 */         if (!((List)localObject3).isEmpty()) {
/*  611 */           if (localObject1 != null) {
/*  612 */             ((List)localObject3).removeAll(new HashSet((Collection)localObject1));
/*      */ 
/*  617 */             ((List)localObject3).addAll((Collection)localObject1);
/*      */           }
/*  619 */           localObject1 = localObject3;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  624 */     if (localObject1 == null) {
/*  625 */       if (paramBoolean) {
/*  626 */         localObject2 = encodeDataFlavor(paramDataFlavor);
/*  627 */         localObject1 = new ArrayList(1);
/*  628 */         getFlavorToNative().put(paramDataFlavor, localObject1);
/*  629 */         ((List)localObject1).add(localObject2);
/*  630 */         this.getNativesForFlavorCache.remove(paramDataFlavor);
/*  631 */         this.getNativesForFlavorCache.remove(null);
/*      */ 
/*  633 */         localObject3 = (List)getNativeToFlavor().get(localObject2);
/*  634 */         if (localObject3 == null) {
/*  635 */           localObject3 = new ArrayList(1);
/*  636 */           getNativeToFlavor().put(localObject2, localObject3);
/*      */         }
/*  638 */         ((List)localObject3).add(paramDataFlavor);
/*  639 */         this.getFlavorsForNativeCache.remove(localObject2);
/*  640 */         this.getFlavorsForNativeCache.remove(null);
/*      */       } else {
/*  642 */         localObject1 = new ArrayList(0);
/*      */       }
/*      */     }
/*      */ 
/*  646 */     return localObject1;
/*      */   }
/*      */ 
/*      */   public synchronized List<String> getNativesForFlavor(DataFlavor paramDataFlavor)
/*      */   {
/*  675 */     Object localObject1 = null;
/*      */ 
/*  678 */     SoftReference localSoftReference = (SoftReference)this.getNativesForFlavorCache.get(paramDataFlavor);
/*  679 */     if (localSoftReference != null) {
/*  680 */       localObject1 = (List)localSoftReference.get();
/*  681 */       if (localObject1 != null)
/*      */       {
/*  684 */         return new ArrayList((Collection)localObject1);
/*      */       }
/*      */     }
/*      */ 
/*  688 */     if (paramDataFlavor == null) {
/*  689 */       localObject1 = new ArrayList(getNativeToFlavor().keySet());
/*  690 */     } else if (this.disabledMappingGenerationKeys.contains(paramDataFlavor))
/*      */     {
/*  693 */       localObject1 = flavorToNativeLookup(paramDataFlavor, false);
/*      */     }
/*      */     else
/*      */     {
/*      */       Object localObject2;
/*  694 */       if (DataTransferer.isFlavorCharsetTextType(paramDataFlavor))
/*      */       {
/*  698 */         if ("text".equals(paramDataFlavor.getPrimaryType())) {
/*  699 */           localObject1 = (List)getFlavorToNative().get(paramDataFlavor.mimeType.getBaseType());
/*  700 */           if (localObject1 != null)
/*      */           {
/*  702 */             localObject1 = new ArrayList((Collection)localObject1);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  707 */         localObject2 = (List)getFlavorToNative().get("text/plain");
/*      */ 
/*  709 */         if ((localObject2 != null) && (!((List)localObject2).isEmpty()))
/*      */         {
/*  712 */           localObject2 = new ArrayList((Collection)localObject2);
/*  713 */           if ((localObject1 != null) && (!((List)localObject1).isEmpty()))
/*      */           {
/*  715 */             ((List)localObject2).removeAll(new HashSet((Collection)localObject1));
/*  716 */             ((List)localObject1).addAll((Collection)localObject2);
/*      */           } else {
/*  718 */             localObject1 = localObject2;
/*      */           }
/*      */         }
/*      */ 
/*  722 */         if ((localObject1 == null) || (((List)localObject1).isEmpty())) {
/*  723 */           localObject1 = flavorToNativeLookup(paramDataFlavor, true);
/*      */         }
/*      */         else
/*      */         {
/*  728 */           Object localObject3 = flavorToNativeLookup(paramDataFlavor, false);
/*      */ 
/*  733 */           if (!((List)localObject3).isEmpty())
/*      */           {
/*  736 */             localObject3 = new ArrayList((Collection)localObject3);
/*      */ 
/*  738 */             ((List)localObject3).removeAll(new HashSet((Collection)localObject1));
/*  739 */             ((List)localObject1).addAll((Collection)localObject3);
/*      */           }
/*      */         }
/*  742 */       } else if (DataTransferer.isFlavorNoncharsetTextType(paramDataFlavor)) {
/*  743 */         localObject1 = (List)getFlavorToNative().get(paramDataFlavor.mimeType.getBaseType());
/*      */ 
/*  745 */         if ((localObject1 == null) || (((List)localObject1).isEmpty())) {
/*  746 */           localObject1 = flavorToNativeLookup(paramDataFlavor, true);
/*      */         }
/*      */         else
/*      */         {
/*  751 */           localObject2 = flavorToNativeLookup(paramDataFlavor, false);
/*      */ 
/*  756 */           if (!((List)localObject2).isEmpty())
/*      */           {
/*  759 */             localObject1 = new ArrayList((Collection)localObject1);
/*  760 */             localObject2 = new ArrayList((Collection)localObject2);
/*      */ 
/*  762 */             ((List)localObject2).removeAll(new HashSet((Collection)localObject1));
/*  763 */             ((List)localObject1).addAll((Collection)localObject2);
/*      */           }
/*      */         }
/*      */       } else {
/*  767 */         localObject1 = flavorToNativeLookup(paramDataFlavor, true);
/*      */       }
/*      */     }
/*  770 */     this.getNativesForFlavorCache.put(paramDataFlavor, new SoftReference(localObject1));
/*      */ 
/*  772 */     return new ArrayList((Collection)localObject1);
/*      */   }
/*      */ 
/*      */   public synchronized List<DataFlavor> getFlavorsForNative(String paramString)
/*      */   {
/*  809 */     SoftReference localSoftReference = (SoftReference)this.getFlavorsForNativeCache.get(paramString);
/*  810 */     if (localSoftReference != null) {
/*  811 */       localObject1 = (ArrayList)localSoftReference.get();
/*  812 */       if (localObject1 != null) {
/*  813 */         return (List)((ArrayList)localObject1).clone();
/*      */       }
/*      */     }
/*      */ 
/*  817 */     Object localObject1 = new LinkedList();
/*      */     HashSet localHashSet;
/*      */     Object localObject3;
/*      */     Object localObject4;
/*      */     Object localObject5;
/*      */     Object localObject6;
/*  819 */     if (paramString == null) {
/*  820 */       localObject2 = getNativesForFlavor(null);
/*  821 */       localHashSet = new HashSet(((List)localObject2).size());
/*      */ 
/*  823 */       localObject3 = ((List)localObject2).iterator();
/*  824 */       while (((Iterator)localObject3).hasNext())
/*      */       {
/*  826 */         localObject4 = getFlavorsForNative((String)((Iterator)localObject3).next());
/*      */ 
/*  828 */         localObject5 = ((List)localObject4).iterator();
/*  829 */         while (((Iterator)localObject5).hasNext())
/*      */         {
/*  831 */           localObject6 = ((Iterator)localObject5).next();
/*  832 */           if (localHashSet.add(localObject6))
/*  833 */             ((LinkedList)localObject1).add(localObject6);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  838 */       localObject2 = nativeToFlavorLookup(paramString);
/*      */ 
/*  840 */       if (this.disabledMappingGenerationKeys.contains(paramString)) {
/*  841 */         return localObject2;
/*      */       }
/*      */ 
/*  844 */       localHashSet = new HashSet(((List)localObject2).size());
/*      */ 
/*  846 */       localObject3 = nativeToFlavorLookup(paramString);
/*      */ 
/*  848 */       localObject4 = ((List)localObject3).iterator();
/*      */ 
/*  850 */       while (((Iterator)localObject4).hasNext())
/*      */       {
/*  852 */         localObject5 = ((Iterator)localObject4).next();
/*  853 */         if ((localObject5 instanceof String)) {
/*  854 */           localObject6 = (String)localObject5;
/*  855 */           String str = null;
/*      */           try {
/*  857 */             MimeType localMimeType = new MimeType((String)localObject6);
/*  858 */             str = localMimeType.getSubType();
/*      */           }
/*      */           catch (MimeTypeParseException localMimeTypeParseException)
/*      */           {
/*  862 */             if (!$assertionsDisabled) throw new AssertionError();
/*      */           }
/*      */           Object localObject7;
/*  864 */           if (DataTransferer.doesSubtypeSupportCharset(str, null))
/*      */           {
/*  866 */             if (("text/plain".equals(localObject6)) && (localHashSet.add(DataFlavor.stringFlavor)))
/*      */             {
/*  869 */               ((LinkedList)localObject1).add(DataFlavor.stringFlavor);
/*      */             }
/*      */ 
/*  872 */             for (int i = 0; i < UNICODE_TEXT_CLASSES.length; i++) {
/*  873 */               localObject7 = null;
/*      */               try {
/*  875 */                 localObject7 = new DataFlavor((String)localObject6 + ";charset=Unicode;class=" + UNICODE_TEXT_CLASSES[i]);
/*      */               }
/*      */               catch (ClassNotFoundException localClassNotFoundException1)
/*      */               {
/*      */               }
/*  880 */               if (localHashSet.add(localObject7)) {
/*  881 */                 ((LinkedList)localObject1).add(localObject7);
/*      */               }
/*      */             }
/*      */ 
/*  885 */             Iterator localIterator = DataTransferer.standardEncodings();
/*      */ 
/*  887 */             while (localIterator.hasNext())
/*      */             {
/*  889 */               localObject7 = (String)localIterator.next();
/*      */ 
/*  891 */               for (int k = 0; k < ENCODED_TEXT_CLASSES.length; 
/*  892 */                 k++)
/*      */               {
/*  894 */                 DataFlavor localDataFlavor = null;
/*      */                 try {
/*  896 */                   localDataFlavor = new DataFlavor((String)localObject6 + ";charset=" + (String)localObject7 + ";class=" + ENCODED_TEXT_CLASSES[k]);
/*      */                 }
/*      */                 catch (ClassNotFoundException localClassNotFoundException3)
/*      */                 {
/*      */                 }
/*      */ 
/*  907 */                 if (localDataFlavor.equals(DataFlavor.plainTextFlavor)) {
/*  908 */                   localDataFlavor = DataFlavor.plainTextFlavor;
/*      */                 }
/*      */ 
/*  911 */                 if (localHashSet.add(localDataFlavor)) {
/*  912 */                   ((LinkedList)localObject1).add(localDataFlavor);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  917 */             if (("text/plain".equals(localObject6)) && (localHashSet.add(DataFlavor.plainTextFlavor)))
/*      */             {
/*  920 */               ((LinkedList)localObject1).add(DataFlavor.plainTextFlavor);
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  926 */             for (int j = 0; j < ENCODED_TEXT_CLASSES.length; j++) {
/*  927 */               localObject7 = null;
/*      */               try {
/*  929 */                 localObject7 = new DataFlavor((String)localObject6 + ";class=" + ENCODED_TEXT_CLASSES[j]);
/*      */               }
/*      */               catch (ClassNotFoundException localClassNotFoundException2)
/*      */               {
/*      */               }
/*  934 */               if (localHashSet.add(localObject7))
/*  935 */                 ((LinkedList)localObject1).add(localObject7);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  940 */           localObject6 = (DataFlavor)localObject5;
/*  941 */           if (localHashSet.add(localObject6)) {
/*  942 */             ((LinkedList)localObject1).add(localObject6);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  948 */     Object localObject2 = new ArrayList((Collection)localObject1);
/*  949 */     this.getFlavorsForNativeCache.put(paramString, new SoftReference(localObject2));
/*  950 */     return (List)((ArrayList)localObject2).clone();
/*      */   }
/*      */ 
/*      */   public synchronized Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] paramArrayOfDataFlavor)
/*      */   {
/*  981 */     if (paramArrayOfDataFlavor == null) {
/*  982 */       localObject = getFlavorsForNative(null);
/*  983 */       paramArrayOfDataFlavor = new DataFlavor[((List)localObject).size()];
/*  984 */       ((List)localObject).toArray(paramArrayOfDataFlavor);
/*      */     }
/*      */ 
/*  987 */     Object localObject = new HashMap(paramArrayOfDataFlavor.length, 1.0F);
/*  988 */     for (int i = 0; i < paramArrayOfDataFlavor.length; i++) {
/*  989 */       List localList = getNativesForFlavor(paramArrayOfDataFlavor[i]);
/*  990 */       String str = localList.isEmpty() ? null : (String)localList.get(0);
/*  991 */       ((HashMap)localObject).put(paramArrayOfDataFlavor[i], str);
/*      */     }
/*      */ 
/*  994 */     return localObject;
/*      */   }
/*      */ 
/*      */   public synchronized Map<String, DataFlavor> getFlavorsForNatives(String[] paramArrayOfString)
/*      */   {
/* 1026 */     if (paramArrayOfString == null) {
/* 1027 */       localObject = getNativesForFlavor(null);
/* 1028 */       paramArrayOfString = new String[((List)localObject).size()];
/* 1029 */       ((List)localObject).toArray(paramArrayOfString);
/*      */     }
/*      */ 
/* 1032 */     Object localObject = new HashMap(paramArrayOfString.length, 1.0F);
/* 1033 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1034 */       List localList = getFlavorsForNative(paramArrayOfString[i]);
/* 1035 */       DataFlavor localDataFlavor = localList.isEmpty() ? null : (DataFlavor)localList.get(0);
/*      */ 
/* 1037 */       ((HashMap)localObject).put(paramArrayOfString[i], localDataFlavor);
/*      */     }
/*      */ 
/* 1040 */     return localObject;
/*      */   }
/*      */ 
/*      */   public synchronized void addUnencodedNativeForFlavor(DataFlavor paramDataFlavor, String paramString)
/*      */   {
/* 1065 */     if ((paramDataFlavor == null) || (paramString == null)) {
/* 1066 */       throw new NullPointerException("null arguments not permitted");
/*      */     }
/*      */ 
/* 1069 */     Object localObject = (List)getFlavorToNative().get(paramDataFlavor);
/* 1070 */     if (localObject == null) {
/* 1071 */       localObject = new ArrayList(1);
/* 1072 */       getFlavorToNative().put(paramDataFlavor, localObject);
/* 1073 */     } else if (((List)localObject).contains(paramString)) {
/* 1074 */       return;
/*      */     }
/* 1076 */     ((List)localObject).add(paramString);
/* 1077 */     this.getNativesForFlavorCache.remove(paramDataFlavor);
/* 1078 */     this.getNativesForFlavorCache.remove(null);
/*      */   }
/*      */ 
/*      */   public synchronized void setNativesForFlavor(DataFlavor paramDataFlavor, String[] paramArrayOfString)
/*      */   {
/* 1111 */     if ((paramDataFlavor == null) || (paramArrayOfString == null)) {
/* 1112 */       throw new NullPointerException("null arguments not permitted");
/*      */     }
/*      */ 
/* 1115 */     getFlavorToNative().remove(paramDataFlavor);
/* 1116 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 1117 */       addUnencodedNativeForFlavor(paramDataFlavor, paramArrayOfString[i]);
/*      */     }
/* 1119 */     this.disabledMappingGenerationKeys.add(paramDataFlavor);
/*      */ 
/* 1121 */     this.getNativesForFlavorCache.remove(paramDataFlavor);
/* 1122 */     this.getNativesForFlavorCache.remove(null);
/*      */   }
/*      */ 
/*      */   public synchronized void addFlavorForUnencodedNative(String paramString, DataFlavor paramDataFlavor)
/*      */   {
/* 1145 */     if ((paramString == null) || (paramDataFlavor == null)) {
/* 1146 */       throw new NullPointerException("null arguments not permitted");
/*      */     }
/*      */ 
/* 1149 */     Object localObject = (List)getNativeToFlavor().get(paramString);
/* 1150 */     if (localObject == null) {
/* 1151 */       localObject = new ArrayList(1);
/* 1152 */       getNativeToFlavor().put(paramString, localObject);
/* 1153 */     } else if (((List)localObject).contains(paramDataFlavor)) {
/* 1154 */       return;
/*      */     }
/* 1156 */     ((List)localObject).add(paramDataFlavor);
/* 1157 */     this.getFlavorsForNativeCache.remove(paramString);
/* 1158 */     this.getFlavorsForNativeCache.remove(null);
/*      */   }
/*      */ 
/*      */   public synchronized void setFlavorsForNative(String paramString, DataFlavor[] paramArrayOfDataFlavor)
/*      */   {
/* 1190 */     if ((paramString == null) || (paramArrayOfDataFlavor == null)) {
/* 1191 */       throw new NullPointerException("null arguments not permitted");
/*      */     }
/*      */ 
/* 1194 */     getNativeToFlavor().remove(paramString);
/* 1195 */     for (int i = 0; i < paramArrayOfDataFlavor.length; i++) {
/* 1196 */       addFlavorForUnencodedNative(paramString, paramArrayOfDataFlavor[i]);
/*      */     }
/* 1198 */     this.disabledMappingGenerationKeys.add(paramString);
/*      */ 
/* 1200 */     this.getFlavorsForNativeCache.remove(paramString);
/* 1201 */     this.getFlavorsForNativeCache.remove(null);
/*      */   }
/*      */ 
/*      */   public static String encodeJavaMIMEType(String paramString)
/*      */   {
/* 1224 */     return paramString != null ? JavaMIME + paramString : null;
/*      */   }
/*      */ 
/*      */   public static String encodeDataFlavor(DataFlavor paramDataFlavor)
/*      */   {
/* 1253 */     return paramDataFlavor != null ? encodeJavaMIMEType(paramDataFlavor.getMimeType()) : null;
/*      */   }
/*      */ 
/*      */   public static boolean isJavaMIMEType(String paramString)
/*      */   {
/* 1267 */     return (paramString != null) && (paramString.startsWith(JavaMIME, 0));
/*      */   }
/*      */ 
/*      */   public static String decodeJavaMIMEType(String paramString)
/*      */   {
/* 1278 */     return isJavaMIMEType(paramString) ? paramString.substring(JavaMIME.length(), paramString.length()).trim() : null;
/*      */   }
/*      */ 
/*      */   public static DataFlavor decodeDataFlavor(String paramString)
/*      */     throws ClassNotFoundException
/*      */   {
/* 1294 */     String str = decodeJavaMIMEType(paramString);
/* 1295 */     return str != null ? new DataFlavor(str) : null;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.SystemFlavorMap
 * JD-Core Version:    0.6.2
 */