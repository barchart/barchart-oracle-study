/*     */ package javax.management.remote;
/*     */ 
/*     */ import com.sun.jmx.remote.util.ClassLogger;
/*     */ import com.sun.jmx.remote.util.EnvHelp;
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.BitSet;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class JMXServiceURL
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 8173364409860779292L;
/* 479 */   private static final Exception randomException = new Exception();
/*     */ 
/* 633 */   private static final BitSet alphaBitSet = new BitSet(128);
/* 634 */   private static final BitSet numericBitSet = new BitSet(128);
/* 635 */   private static final BitSet alphaNumericBitSet = new BitSet(128);
/* 636 */   private static final BitSet protocolBitSet = new BitSet(128);
/* 637 */   private static final BitSet hostNameBitSet = new BitSet(128);
/*     */   private final String protocol;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private final String urlPath;
/*     */   private transient String toString;
/* 688 */   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXServiceURL");
/*     */ 
/*     */   public JMXServiceURL(String paramString)
/*     */     throws MalformedURLException
/*     */   {
/* 139 */     int i = paramString.length();
/*     */ 
/* 143 */     for (int j = 0; j < i; j++) {
/* 144 */       k = paramString.charAt(j);
/* 145 */       if ((k < 32) || (k >= 127)) {
/* 146 */         throw new MalformedURLException("Service URL contains non-ASCII character 0x" + Integer.toHexString(k));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 154 */     int k = "service:jmx:".length();
/* 155 */     if (!paramString.regionMatches(true, 0, "service:jmx:", 0, k))
/*     */     {
/* 160 */       throw new MalformedURLException("Service URL must start with service:jmx:");
/*     */     }
/*     */ 
/* 165 */     int m = k;
/* 166 */     int n = indexOf(paramString, ':', m);
/* 167 */     this.protocol = paramString.substring(m, n).toLowerCase();
/*     */ 
/* 170 */     if (!paramString.regionMatches(n, "://", 0, 3)) {
/* 171 */       throw new MalformedURLException("Missing \"://\" after protocol name");
/*     */     }
/*     */ 
/* 176 */     int i1 = n + 3;
/*     */     int i2;
/* 178 */     if ((i1 < i) && (paramString.charAt(i1) == '['))
/*     */     {
/* 180 */       i2 = paramString.indexOf(']', i1) + 1;
/* 181 */       if (i2 == 0)
/* 182 */         throw new MalformedURLException("Bad host name: [ without ]");
/* 183 */       this.host = paramString.substring(i1 + 1, i2 - 1);
/* 184 */       if (!isNumericIPv6Address(this.host))
/* 185 */         throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
/*     */     }
/*     */     else
/*     */     {
/* 189 */       i2 = indexOfFirstNotInSet(paramString, hostNameBitSet, i1);
/*     */ 
/* 191 */       this.host = paramString.substring(i1, i2);
/*     */     }
/*     */     int i3;
/* 196 */     if ((i2 < i) && (paramString.charAt(i2) == ':')) {
/* 197 */       if (this.host.length() == 0) {
/* 198 */         throw new MalformedURLException("Cannot give port number without host name");
/*     */       }
/*     */ 
/* 201 */       i4 = i2 + 1;
/* 202 */       i3 = indexOfFirstNotInSet(paramString, numericBitSet, i4);
/*     */ 
/* 204 */       String str = paramString.substring(i4, i3);
/*     */       try {
/* 206 */         this.port = Integer.parseInt(str);
/*     */       } catch (NumberFormatException localNumberFormatException) {
/* 208 */         throw new MalformedURLException("Bad port number: \"" + str + "\": " + localNumberFormatException);
/*     */       }
/*     */     }
/*     */     else {
/* 212 */       i3 = i2;
/* 213 */       this.port = 0;
/*     */     }
/*     */ 
/* 217 */     int i4 = i3;
/* 218 */     if (i4 < i)
/* 219 */       this.urlPath = paramString.substring(i4);
/*     */     else {
/* 221 */       this.urlPath = "";
/*     */     }
/* 223 */     validate();
/*     */   }
/*     */ 
/*     */   public JMXServiceURL(String paramString1, String paramString2, int paramInt)
/*     */     throws MalformedURLException
/*     */   {
/* 250 */     this(paramString1, paramString2, paramInt, null);
/*     */   }
/*     */ 
/*     */   public JMXServiceURL(String paramString1, String paramString2, int paramInt, String paramString3)
/*     */     throws MalformedURLException
/*     */   {
/* 278 */     if (paramString1 == null) {
/* 279 */       paramString1 = "jmxmp";
/*     */     }
/* 281 */     if (paramString2 == null) {
/*     */       InetAddress localInetAddress;
/*     */       try {
/* 284 */         localInetAddress = InetAddress.getLocalHost();
/*     */       } catch (UnknownHostException localUnknownHostException) {
/* 286 */         throw new MalformedURLException("Local host name unknown: " + localUnknownHostException);
/*     */       }
/*     */ 
/* 290 */       paramString2 = localInetAddress.getHostName();
/*     */       try
/*     */       {
/* 300 */         validateHost(paramString2);
/*     */       } catch (MalformedURLException localMalformedURLException) {
/* 302 */         if (logger.fineOn()) {
/* 303 */           logger.fine("JMXServiceURL", "Replacing illegal local host name " + paramString2 + " with numeric IP address " + "(see RFC 1034)", localMalformedURLException);
/*     */         }
/*     */ 
/* 308 */         paramString2 = localInetAddress.getHostAddress();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 314 */     if (paramString2.startsWith("[")) {
/* 315 */       if (!paramString2.endsWith("]")) {
/* 316 */         throw new MalformedURLException("Host starts with [ but does not end with ]");
/*     */       }
/*     */ 
/* 319 */       paramString2 = paramString2.substring(1, paramString2.length() - 1);
/* 320 */       if (!isNumericIPv6Address(paramString2)) {
/* 321 */         throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
/*     */       }
/*     */ 
/* 324 */       if (paramString2.startsWith("[")) {
/* 325 */         throw new MalformedURLException("More than one [[...]]");
/*     */       }
/*     */     }
/* 328 */     this.protocol = paramString1.toLowerCase();
/* 329 */     this.host = paramString2;
/* 330 */     this.port = paramInt;
/*     */ 
/* 332 */     if (paramString3 == null)
/* 333 */       paramString3 = "";
/* 334 */     this.urlPath = paramString3;
/*     */ 
/* 336 */     validate();
/*     */   }
/*     */ 
/*     */   private void validate()
/*     */     throws MalformedURLException
/*     */   {
/* 343 */     int i = indexOfFirstNotInSet(this.protocol, protocolBitSet, 0);
/* 344 */     if ((i == 0) || (i < this.protocol.length()) || (!alphaBitSet.get(this.protocol.charAt(0))))
/*     */     {
/* 346 */       throw new MalformedURLException("Missing or invalid protocol name: \"" + this.protocol + "\"");
/*     */     }
/*     */ 
/* 352 */     validateHost();
/*     */ 
/* 356 */     if (this.port < 0) {
/* 357 */       throw new MalformedURLException("Bad port: " + this.port);
/*     */     }
/*     */ 
/* 361 */     if ((this.urlPath.length() > 0) && 
/* 362 */       (!this.urlPath.startsWith("/")) && (!this.urlPath.startsWith(";")))
/* 363 */       throw new MalformedURLException("Bad URL path: " + this.urlPath);
/*     */   }
/*     */ 
/*     */   private void validateHost() throws MalformedURLException
/*     */   {
/* 368 */     if (this.host.length() == 0) {
/* 369 */       if (this.port != 0) {
/* 370 */         throw new MalformedURLException("Cannot give port number without host name");
/*     */       }
/*     */ 
/* 373 */       return;
/*     */     }
/*     */ 
/* 376 */     validateHost(this.host);
/*     */   }
/*     */ 
/*     */   private static void validateHost(String paramString)
/*     */     throws MalformedURLException
/*     */   {
/* 382 */     if (isNumericIPv6Address(paramString))
/*     */     {
/*     */       try
/*     */       {
/* 390 */         InetAddress.getByName(paramString);
/*     */       }
/*     */       catch (Exception localException1)
/*     */       {
/* 396 */         MalformedURLException localMalformedURLException = new MalformedURLException("Bad IPv6 address: " + paramString);
/*     */ 
/* 398 */         EnvHelp.initCause(localMalformedURLException, localException1);
/* 399 */         throw localMalformedURLException;
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 422 */       int i = paramString.length();
/* 423 */       int j = 46;
/* 424 */       int k = 0;
/* 425 */       int m = 0;
/*     */       int i1;
/* 428 */       for (int n = 0; n < i; n++) {
/* 429 */         i1 = paramString.charAt(n);
/* 430 */         boolean bool = alphaNumericBitSet.get(i1);
/* 431 */         if (j == 46)
/* 432 */           m = i1;
/* 433 */         if (bool) {
/* 434 */           j = 97;
/* 435 */         } else if (i1 == 45) {
/* 436 */           if (j == 46)
/*     */             break;
/* 438 */           j = 45;
/* 439 */         } else if (i1 == 46) {
/* 440 */           k = 1;
/* 441 */           if (j != 97)
/*     */             break;
/* 443 */           j = 46;
/*     */         } else {
/* 445 */           j = 46;
/* 446 */           break;
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 451 */         if (j != 97)
/* 452 */           throw randomException;
/* 453 */         if ((k != 0) && (!alphaBitSet.get(m)))
/*     */         {
/* 461 */           StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".", true);
/* 462 */           for (i1 = 0; i1 < 4; i1++) {
/* 463 */             String str = localStringTokenizer.nextToken();
/* 464 */             int i2 = Integer.parseInt(str);
/* 465 */             if ((i2 < 0) || (i2 > 255))
/* 466 */               throw randomException;
/* 467 */             if ((i1 < 3) && (!localStringTokenizer.nextToken().equals(".")))
/* 468 */               throw randomException;
/*     */           }
/* 470 */           if (localStringTokenizer.hasMoreTokens())
/* 471 */             throw randomException;
/*     */         }
/*     */       } catch (Exception localException2) {
/* 474 */         throw new MalformedURLException("Bad host: \"" + paramString + "\"");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getProtocol()
/*     */   {
/* 488 */     return this.protocol;
/*     */   }
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 508 */     return this.host;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 518 */     return this.port;
/*     */   }
/*     */ 
/*     */   public String getURLPath()
/*     */   {
/* 530 */     return this.urlPath;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 553 */     if (this.toString != null)
/* 554 */       return this.toString;
/* 555 */     StringBuilder localStringBuilder = new StringBuilder("service:jmx:");
/* 556 */     localStringBuilder.append(getProtocol()).append("://");
/* 557 */     String str = getHost();
/* 558 */     if (isNumericIPv6Address(str))
/* 559 */       localStringBuilder.append('[').append(str).append(']');
/*     */     else
/* 561 */       localStringBuilder.append(str);
/* 562 */     int i = getPort();
/* 563 */     if (i != 0)
/* 564 */       localStringBuilder.append(':').append(i);
/* 565 */     localStringBuilder.append(getURLPath());
/* 566 */     this.toString = localStringBuilder.toString();
/* 567 */     return this.toString;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 585 */     if (!(paramObject instanceof JMXServiceURL))
/* 586 */       return false;
/* 587 */     JMXServiceURL localJMXServiceURL = (JMXServiceURL)paramObject;
/* 588 */     return (localJMXServiceURL.getProtocol().equalsIgnoreCase(getProtocol())) && (localJMXServiceURL.getHost().equalsIgnoreCase(getHost())) && (localJMXServiceURL.getPort() == getPort()) && (localJMXServiceURL.getURLPath().equals(getURLPath()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 596 */     return toString().hashCode();
/*     */   }
/*     */ 
/*     */   private static boolean isNumericIPv6Address(String paramString)
/*     */   {
/* 604 */     return paramString.indexOf(':') >= 0;
/*     */   }
/*     */ 
/*     */   private static int indexOf(String paramString, char paramChar, int paramInt)
/*     */   {
/* 609 */     int i = paramString.indexOf(paramChar, paramInt);
/* 610 */     if (i < 0) {
/* 611 */       return paramString.length();
/*     */     }
/* 613 */     return i;
/*     */   }
/*     */ 
/*     */   private static int indexOfFirstNotInSet(String paramString, BitSet paramBitSet, int paramInt)
/*     */   {
/* 618 */     int i = paramString.length();
/* 619 */     int j = paramInt;
/*     */ 
/* 621 */     while (j < i)
/*     */     {
/* 623 */       int k = paramString.charAt(j);
/* 624 */       if (k >= 128)
/*     */         break;
/* 626 */       if (!paramBitSet.get(k))
/*     */         break;
/* 628 */       j++;
/*     */     }
/* 630 */     return j;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 643 */     for (int i = 48; i <= 57; i = (char)(i + 1)) {
/* 644 */       numericBitSet.set(i);
/*     */     }
/* 646 */     for (i = 65; i <= 90; i = (char)(i + 1))
/* 647 */       alphaBitSet.set(i);
/* 648 */     for (i = 97; i <= 122; i = (char)(i + 1)) {
/* 649 */       alphaBitSet.set(i);
/*     */     }
/* 651 */     alphaNumericBitSet.or(alphaBitSet);
/* 652 */     alphaNumericBitSet.or(numericBitSet);
/*     */ 
/* 654 */     protocolBitSet.or(alphaNumericBitSet);
/* 655 */     protocolBitSet.set(43);
/* 656 */     protocolBitSet.set(45);
/*     */ 
/* 658 */     hostNameBitSet.or(alphaNumericBitSet);
/* 659 */     hostNameBitSet.set(45);
/* 660 */     hostNameBitSet.set(46);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.JMXServiceURL
 * JD-Core Version:    0.6.2
 */