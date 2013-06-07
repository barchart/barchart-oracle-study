/*     */ package java.nio.charset;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.spi.CharsetProvider;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.ServiceConfigurationError;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import sun.misc.ASCIICaseInsensitiveComparator;
/*     */ import sun.misc.VM;
/*     */ import sun.nio.cs.StandardCharsets;
/*     */ import sun.nio.cs.ThreadLocalCoders;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public abstract class Charset
/*     */   implements Comparable<Charset>
/*     */ {
/* 282 */   private static volatile String bugLevel = null;
/*     */ 
/* 325 */   private static CharsetProvider standardProvider = new StandardCharsets();
/*     */ 
/* 330 */   private static volatile Object[] cache1 = null;
/* 331 */   private static volatile Object[] cache2 = null;
/*     */ 
/* 389 */   private static ThreadLocal<ThreadLocal> gate = new ThreadLocal();
/*     */ 
/* 429 */   private static Object extendedProviderLock = new Object();
/* 430 */   private static boolean extendedProviderProbed = false;
/* 431 */   private static CharsetProvider extendedProvider = null;
/*     */   private static volatile Charset defaultCharset;
/*     */   private final String name;
/*     */   private final String[] aliases;
/* 633 */   private Set<String> aliasSet = null;
/*     */ 
/*     */   static boolean atBugLevel(String paramString)
/*     */   {
/* 285 */     String str = bugLevel;
/* 286 */     if (str == null) {
/* 287 */       if (!VM.isBooted())
/* 288 */         return false;
/* 289 */       bugLevel = str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.cs.bugLevel", ""));
/*     */     }
/*     */ 
/* 292 */     return str.equals(paramString);
/*     */   }
/*     */ 
/*     */   private static void checkName(String paramString)
/*     */   {
/* 305 */     int i = paramString.length();
/* 306 */     if ((!atBugLevel("1.4")) && 
/* 307 */       (i == 0)) {
/* 308 */       throw new IllegalCharsetNameException(paramString);
/*     */     }
/* 310 */     for (int j = 0; j < i; j++) {
/* 311 */       int k = paramString.charAt(j);
/* 312 */       if (((k < 65) || (k > 90)) && 
/* 313 */         ((k < 97) || (k > 122)) && 
/* 314 */         ((k < 48) || (k > 57)) && 
/* 315 */         ((k != 45) || (j == 0)) && 
/* 316 */         ((k != 43) || (j == 0)) && 
/* 317 */         ((k != 58) || (j == 0)) && 
/* 318 */         ((k != 95) || (j == 0)) && (
/* 319 */         (k != 46) || (j == 0)))
/* 320 */         throw new IllegalCharsetNameException(paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void cache(String paramString, Charset paramCharset)
/*     */   {
/* 334 */     cache2 = cache1;
/* 335 */     cache1 = new Object[] { paramString, paramCharset };
/*     */   }
/*     */ 
/*     */   private static Iterator providers()
/*     */   {
/* 343 */     return new Iterator()
/*     */     {
/* 345 */       ClassLoader cl = ClassLoader.getSystemClassLoader();
/* 346 */       ServiceLoader<CharsetProvider> sl = ServiceLoader.load(CharsetProvider.class, this.cl);
/*     */ 
/* 348 */       Iterator<CharsetProvider> i = this.sl.iterator();
/*     */ 
/* 350 */       Object next = null;
/*     */ 
/*     */       private boolean getNext() {
/* 353 */         while (this.next == null) {
/*     */           try {
/* 355 */             if (!this.i.hasNext())
/* 356 */               return false;
/* 357 */             this.next = this.i.next(); } catch (ServiceConfigurationError localServiceConfigurationError) {
/*     */           }
/* 359 */           if (!(localServiceConfigurationError.getCause() instanceof SecurityException))
/*     */           {
/* 363 */             throw localServiceConfigurationError;
/*     */           }
/*     */         }
/* 366 */         return true;
/*     */       }
/*     */ 
/*     */       public boolean hasNext() {
/* 370 */         return getNext();
/*     */       }
/*     */ 
/*     */       public Object next() {
/* 374 */         if (!getNext())
/* 375 */           throw new NoSuchElementException();
/* 376 */         Object localObject = this.next;
/* 377 */         this.next = null;
/* 378 */         return localObject;
/*     */       }
/*     */ 
/*     */       public void remove() {
/* 382 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static Charset lookupViaProviders(String paramString)
/*     */   {
/* 401 */     if (!VM.isBooted()) {
/* 402 */       return null;
/*     */     }
/* 404 */     if (gate.get() != null)
/*     */     {
/* 406 */       return null;
/*     */     }try {
/* 408 */       gate.set(gate);
/*     */ 
/* 410 */       return (Charset)AccessController.doPrivileged(new PrivilegedAction()
/*     */       {
/*     */         public Charset run() {
/* 413 */           for (Iterator localIterator = Charset.access$000(); localIterator.hasNext(); ) {
/* 414 */             CharsetProvider localCharsetProvider = (CharsetProvider)localIterator.next();
/* 415 */             Charset localCharset = localCharsetProvider.charsetForName(this.val$charsetName);
/* 416 */             if (localCharset != null)
/* 417 */               return localCharset;
/*     */           }
/* 419 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */     finally {
/* 424 */       gate.set(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void probeExtendedProvider()
/*     */   {
/* 434 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*     */         try {
/* 437 */           Class localClass = Class.forName("sun.nio.cs.ext.ExtendedCharsets");
/*     */ 
/* 439 */           Charset.access$102((CharsetProvider)localClass.newInstance());
/*     */         }
/*     */         catch (ClassNotFoundException localClassNotFoundException) {
/*     */         }
/*     */         catch (InstantiationException localInstantiationException) {
/* 444 */           throw new Error(localInstantiationException);
/*     */         } catch (IllegalAccessException localIllegalAccessException) {
/* 446 */           throw new Error(localIllegalAccessException);
/*     */         }
/* 448 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static Charset lookupExtendedCharset(String paramString) {
/* 454 */     CharsetProvider localCharsetProvider = null;
/* 455 */     synchronized (extendedProviderLock) {
/* 456 */       if (!extendedProviderProbed) {
/* 457 */         probeExtendedProvider();
/* 458 */         extendedProviderProbed = true;
/*     */       }
/* 460 */       localCharsetProvider = extendedProvider;
/*     */     }
/* 462 */     return localCharsetProvider != null ? localCharsetProvider.charsetForName(paramString) : null;
/*     */   }
/*     */ 
/*     */   private static Charset lookup(String paramString) {
/* 466 */     if (paramString == null)
/* 467 */       throw new IllegalArgumentException("Null charset name");
/*     */     Object[] arrayOfObject;
/* 470 */     if (((arrayOfObject = cache1) != null) && (paramString.equals(arrayOfObject[0]))) {
/* 471 */       return (Charset)arrayOfObject[1];
/*     */     }
/*     */ 
/* 475 */     return lookup2(paramString);
/*     */   }
/*     */ 
/*     */   private static Charset lookup2(String paramString)
/*     */   {
/*     */     Object[] arrayOfObject;
/* 480 */     if (((arrayOfObject = cache2) != null) && (paramString.equals(arrayOfObject[0]))) {
/* 481 */       cache2 = cache1;
/* 482 */       cache1 = arrayOfObject;
/* 483 */       return (Charset)arrayOfObject[1];
/*     */     }
/*     */     Charset localCharset;
/* 487 */     if (((localCharset = standardProvider.charsetForName(paramString)) != null) || ((localCharset = lookupExtendedCharset(paramString)) != null) || ((localCharset = lookupViaProviders(paramString)) != null))
/*     */     {
/* 491 */       cache(paramString, localCharset);
/* 492 */       return localCharset;
/*     */     }
/*     */ 
/* 496 */     checkName(paramString);
/* 497 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isSupported(String paramString)
/*     */   {
/* 517 */     return lookup(paramString) != null;
/*     */   }
/*     */ 
/*     */   public static Charset forName(String paramString)
/*     */   {
/* 540 */     Charset localCharset = lookup(paramString);
/* 541 */     if (localCharset != null)
/* 542 */       return localCharset;
/* 543 */     throw new UnsupportedCharsetException(paramString);
/*     */   }
/*     */ 
/*     */   private static void put(Iterator<Charset> paramIterator, Map<String, Charset> paramMap)
/*     */   {
/* 550 */     while (paramIterator.hasNext()) {
/* 551 */       Charset localCharset = (Charset)paramIterator.next();
/* 552 */       if (!paramMap.containsKey(localCharset.name()))
/* 553 */         paramMap.put(localCharset.name(), localCharset);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static SortedMap<String, Charset> availableCharsets()
/*     */   {
/* 584 */     return (SortedMap)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public SortedMap<String, Charset> run() {
/* 587 */         TreeMap localTreeMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
/*     */ 
/* 590 */         Charset.put(Charset.standardProvider.charsets(), localTreeMap);
/* 591 */         for (Iterator localIterator = Charset.access$000(); localIterator.hasNext(); ) {
/* 592 */           CharsetProvider localCharsetProvider = (CharsetProvider)localIterator.next();
/* 593 */           Charset.put(localCharsetProvider.charsets(), localTreeMap);
/*     */         }
/* 595 */         return Collections.unmodifiableSortedMap(localTreeMap);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static Charset defaultCharset()
/*     */   {
/* 614 */     if (defaultCharset == null) {
/* 615 */       synchronized (Charset.class) {
/* 616 */         String str = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
/*     */ 
/* 618 */         Charset localCharset = lookup(str);
/* 619 */         if (localCharset != null)
/* 620 */           defaultCharset = localCharset;
/*     */         else
/* 622 */           defaultCharset = forName("UTF-8");
/*     */       }
/*     */     }
/* 625 */     return defaultCharset;
/*     */   }
/*     */ 
/*     */   protected Charset(String paramString, String[] paramArrayOfString)
/*     */   {
/* 649 */     checkName(paramString);
/* 650 */     String[] arrayOfString = paramArrayOfString == null ? new String[0] : paramArrayOfString;
/* 651 */     for (int i = 0; i < arrayOfString.length; i++)
/* 652 */       checkName(arrayOfString[i]);
/* 653 */     this.name = paramString;
/* 654 */     this.aliases = arrayOfString;
/*     */   }
/*     */ 
/*     */   public final String name()
/*     */   {
/* 663 */     return this.name;
/*     */   }
/*     */ 
/*     */   public final Set<String> aliases()
/*     */   {
/* 672 */     if (this.aliasSet != null)
/* 673 */       return this.aliasSet;
/* 674 */     int i = this.aliases.length;
/* 675 */     HashSet localHashSet = new HashSet(i);
/* 676 */     for (int j = 0; j < i; j++)
/* 677 */       localHashSet.add(this.aliases[j]);
/* 678 */     this.aliasSet = Collections.unmodifiableSet(localHashSet);
/* 679 */     return this.aliasSet;
/*     */   }
/*     */ 
/*     */   public String displayName()
/*     */   {
/* 692 */     return this.name;
/*     */   }
/*     */ 
/*     */   public final boolean isRegistered()
/*     */   {
/* 704 */     return (!this.name.startsWith("X-")) && (!this.name.startsWith("x-"));
/*     */   }
/*     */ 
/*     */   public String displayName(Locale paramLocale)
/*     */   {
/* 720 */     return this.name;
/*     */   }
/*     */ 
/*     */   public abstract boolean contains(Charset paramCharset);
/*     */ 
/*     */   public abstract CharsetDecoder newDecoder();
/*     */ 
/*     */   public abstract CharsetEncoder newEncoder();
/*     */ 
/*     */   public boolean canEncode()
/*     */   {
/* 780 */     return true;
/*     */   }
/*     */ 
/*     */   public final CharBuffer decode(ByteBuffer paramByteBuffer)
/*     */   {
/*     */     try
/*     */     {
/* 810 */       return ThreadLocalCoders.decoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).decode(paramByteBuffer);
/*     */     }
/*     */     catch (CharacterCodingException localCharacterCodingException)
/*     */     {
/* 815 */       throw new Error(localCharacterCodingException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final ByteBuffer encode(CharBuffer paramCharBuffer)
/*     */   {
/*     */     try
/*     */     {
/* 846 */       return ThreadLocalCoders.encoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).encode(paramCharBuffer);
/*     */     }
/*     */     catch (CharacterCodingException localCharacterCodingException)
/*     */     {
/* 851 */       throw new Error(localCharacterCodingException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final ByteBuffer encode(String paramString)
/*     */   {
/* 869 */     return encode(CharBuffer.wrap(paramString));
/*     */   }
/*     */ 
/*     */   public final int compareTo(Charset paramCharset)
/*     */   {
/* 885 */     return name().compareToIgnoreCase(paramCharset.name());
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 894 */     return name().hashCode();
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object paramObject)
/*     */   {
/* 907 */     if (!(paramObject instanceof Charset))
/* 908 */       return false;
/* 909 */     if (this == paramObject)
/* 910 */       return true;
/* 911 */     return this.name.equals(((Charset)paramObject).name());
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 920 */     return name();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.nio.charset.Charset
 * JD-Core Version:    0.6.2
 */