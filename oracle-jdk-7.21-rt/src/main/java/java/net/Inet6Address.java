/*     */ package java.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Enumeration;
/*     */ 
/*     */ public final class Inet6Address extends InetAddress
/*     */ {
/*     */   static final int INADDRSZ = 16;
/* 172 */   private transient int cached_scope_id = 0;
/*     */   byte[] ipaddress;
/* 185 */   private int scope_id = 0;
/*     */ 
/* 191 */   private boolean scope_id_set = false;
/*     */ 
/* 197 */   private transient NetworkInterface scope_ifname = null;
/*     */ 
/* 203 */   private boolean scope_ifname_set = false;
/*     */   private static final long serialVersionUID = 6880410070516793377L;
/*     */   private static final int INT16SZ = 2;
/*     */   private String ifname;
/*     */ 
/*     */   Inet6Address()
/*     */   {
/* 216 */     holder().hostName = null;
/* 217 */     this.ipaddress = new byte[16];
/* 218 */     holder().family = 2;
/*     */   }
/*     */ 
/*     */   Inet6Address(String paramString, byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 225 */     holder().hostName = paramString;
/* 226 */     if (paramArrayOfByte.length == 16) {
/* 227 */       holder().family = 2;
/* 228 */       this.ipaddress = ((byte[])paramArrayOfByte.clone());
/*     */     }
/* 230 */     if (paramInt >= 0) {
/* 231 */       this.scope_id = paramInt;
/* 232 */       this.scope_id_set = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   Inet6Address(String paramString, byte[] paramArrayOfByte) {
/*     */     try {
/* 238 */       initif(paramString, paramArrayOfByte, null); } catch (UnknownHostException localUnknownHostException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   Inet6Address(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
/* 243 */     initif(paramString, paramArrayOfByte, paramNetworkInterface);
/*     */   }
/*     */ 
/*     */   Inet6Address(String paramString1, byte[] paramArrayOfByte, String paramString2) throws UnknownHostException {
/* 247 */     initstr(paramString1, paramArrayOfByte, paramString2);
/*     */   }
/*     */ 
/*     */   public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
/*     */     throws UnknownHostException
/*     */   {
/* 271 */     if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && 
/* 272 */       (paramString.charAt(paramString.length() - 1) == ']')) {
/* 273 */       paramString = paramString.substring(1, paramString.length() - 1);
/*     */     }
/*     */ 
/* 276 */     if ((paramArrayOfByte != null) && 
/* 277 */       (paramArrayOfByte.length == 16)) {
/* 278 */       return new Inet6Address(paramString, paramArrayOfByte, paramNetworkInterface);
/*     */     }
/*     */ 
/* 281 */     throw new UnknownHostException("addr is of illegal length");
/*     */   }
/*     */ 
/*     */   public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfByte, int paramInt)
/*     */     throws UnknownHostException
/*     */   {
/* 302 */     if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && 
/* 303 */       (paramString.charAt(paramString.length() - 1) == ']')) {
/* 304 */       paramString = paramString.substring(1, paramString.length() - 1);
/*     */     }
/*     */ 
/* 307 */     if ((paramArrayOfByte != null) && 
/* 308 */       (paramArrayOfByte.length == 16)) {
/* 309 */       return new Inet6Address(paramString, paramArrayOfByte, paramInt);
/*     */     }
/*     */ 
/* 312 */     throw new UnknownHostException("addr is of illegal length");
/*     */   }
/*     */ 
/*     */   private void initstr(String paramString1, byte[] paramArrayOfByte, String paramString2) throws UnknownHostException {
/*     */     try {
/* 317 */       NetworkInterface localNetworkInterface = NetworkInterface.getByName(paramString2);
/* 318 */       if (localNetworkInterface == null) {
/* 319 */         throw new UnknownHostException("no such interface " + paramString2);
/*     */       }
/* 321 */       initif(paramString1, paramArrayOfByte, localNetworkInterface);
/*     */     } catch (SocketException localSocketException) {
/* 323 */       throw new UnknownHostException("SocketException thrown" + paramString2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initif(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
/* 328 */     holder().hostName = paramString;
/* 329 */     if (paramArrayOfByte.length == 16) {
/* 330 */       holder().family = 2;
/* 331 */       this.ipaddress = ((byte[])paramArrayOfByte.clone());
/*     */     }
/* 333 */     if (paramNetworkInterface != null) {
/* 334 */       this.scope_ifname = paramNetworkInterface;
/* 335 */       this.scope_ifname_set = true;
/* 336 */       this.scope_id = deriveNumericScope(paramNetworkInterface);
/* 337 */       this.scope_id_set = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean differentLocalAddressTypes(Inet6Address paramInet6Address)
/*     */   {
/* 348 */     if ((isLinkLocalAddress()) && (!paramInet6Address.isLinkLocalAddress())) {
/* 349 */       return false;
/*     */     }
/* 351 */     if ((isSiteLocalAddress()) && (!paramInet6Address.isSiteLocalAddress())) {
/* 352 */       return false;
/*     */     }
/* 354 */     return true;
/*     */   }
/*     */ 
/*     */   private int deriveNumericScope(NetworkInterface paramNetworkInterface) throws UnknownHostException {
/* 358 */     Enumeration localEnumeration = paramNetworkInterface.getInetAddresses();
/* 359 */     while (localEnumeration.hasMoreElements()) {
/* 360 */       InetAddress localInetAddress = (InetAddress)localEnumeration.nextElement();
/* 361 */       if ((localInetAddress instanceof Inet6Address))
/*     */       {
/* 364 */         Inet6Address localInet6Address = (Inet6Address)localInetAddress;
/*     */ 
/* 366 */         if (differentLocalAddressTypes(localInet6Address))
/*     */         {
/* 371 */           return localInet6Address.scope_id;
/*     */         }
/*     */       }
/*     */     }
/* 373 */     throw new UnknownHostException("no scope_id found");
/*     */   }
/*     */ 
/*     */   private int deriveNumericScope(String paramString) throws UnknownHostException {
/*     */     Enumeration localEnumeration1;
/*     */     try {
/* 379 */       localEnumeration1 = NetworkInterface.getNetworkInterfaces();
/*     */     } catch (SocketException localSocketException) {
/* 381 */       throw new UnknownHostException("could not enumerate local network interfaces");
/*     */     }
/* 383 */     while (localEnumeration1.hasMoreElements()) {
/* 384 */       NetworkInterface localNetworkInterface = (NetworkInterface)localEnumeration1.nextElement();
/* 385 */       if (localNetworkInterface.getName().equals(paramString)) {
/* 386 */         Enumeration localEnumeration2 = localNetworkInterface.getInetAddresses();
/* 387 */         while (localEnumeration2.hasMoreElements()) {
/* 388 */           InetAddress localInetAddress = (InetAddress)localEnumeration2.nextElement();
/* 389 */           if ((localInetAddress instanceof Inet6Address))
/*     */           {
/* 392 */             Inet6Address localInet6Address = (Inet6Address)localInetAddress;
/*     */ 
/* 394 */             if (differentLocalAddressTypes(localInet6Address))
/*     */             {
/* 399 */               return localInet6Address.scope_id;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 403 */     throw new UnknownHostException("No matching address found for interface : " + paramString);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 413 */     this.scope_ifname = null;
/* 414 */     this.scope_ifname_set = false;
/*     */ 
/* 416 */     if (getClass().getClassLoader() != null) {
/* 417 */       throw new SecurityException("invalid address type");
/*     */     }
/*     */ 
/* 420 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 422 */     if ((this.ifname != null) && (!"".equals(this.ifname))) {
/*     */       try {
/* 424 */         this.scope_ifname = NetworkInterface.getByName(this.ifname);
/* 425 */         if (this.scope_ifname == null)
/*     */         {
/* 428 */           this.scope_id_set = false;
/* 429 */           this.scope_ifname_set = false;
/* 430 */           this.scope_id = 0;
/*     */         } else {
/*     */           try {
/* 433 */             this.scope_id = deriveNumericScope(this.scope_ifname);
/*     */           }
/*     */           catch (UnknownHostException localUnknownHostException)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (SocketException localSocketException)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 445 */     this.ipaddress = ((byte[])this.ipaddress.clone());
/*     */ 
/* 448 */     if (this.ipaddress.length != 16) {
/* 449 */       throw new InvalidObjectException("invalid address length: " + this.ipaddress.length);
/*     */     }
/*     */ 
/* 453 */     if (holder().getFamily() != 2)
/* 454 */       throw new InvalidObjectException("invalid address family type");
/*     */   }
/*     */ 
/*     */   public boolean isMulticastAddress()
/*     */   {
/* 469 */     return (this.ipaddress[0] & 0xFF) == 255;
/*     */   }
/*     */ 
/*     */   public boolean isAnyLocalAddress()
/*     */   {
/* 480 */     int i = 0;
/* 481 */     for (int j = 0; j < 16; j++) {
/* 482 */       i = (byte)(i | this.ipaddress[j]);
/*     */     }
/* 484 */     return i == 0;
/*     */   }
/*     */ 
/*     */   public boolean isLoopbackAddress()
/*     */   {
/* 496 */     int i = 0;
/* 497 */     for (int j = 0; j < 15; j++) {
/* 498 */       i = (byte)(i | this.ipaddress[j]);
/*     */     }
/* 500 */     return (i == 0) && (this.ipaddress[15] == 1);
/*     */   }
/*     */ 
/*     */   public boolean isLinkLocalAddress()
/*     */   {
/* 512 */     return ((this.ipaddress[0] & 0xFF) == 254) && ((this.ipaddress[1] & 0xC0) == 128);
/*     */   }
/*     */ 
/*     */   public boolean isSiteLocalAddress()
/*     */   {
/* 525 */     return ((this.ipaddress[0] & 0xFF) == 254) && ((this.ipaddress[1] & 0xC0) == 192);
/*     */   }
/*     */ 
/*     */   public boolean isMCGlobal()
/*     */   {
/* 539 */     return ((this.ipaddress[0] & 0xFF) == 255) && ((this.ipaddress[1] & 0xF) == 14);
/*     */   }
/*     */ 
/*     */   public boolean isMCNodeLocal()
/*     */   {
/* 553 */     return ((this.ipaddress[0] & 0xFF) == 255) && ((this.ipaddress[1] & 0xF) == 1);
/*     */   }
/*     */ 
/*     */   public boolean isMCLinkLocal()
/*     */   {
/* 567 */     return ((this.ipaddress[0] & 0xFF) == 255) && ((this.ipaddress[1] & 0xF) == 2);
/*     */   }
/*     */ 
/*     */   public boolean isMCSiteLocal()
/*     */   {
/* 581 */     return ((this.ipaddress[0] & 0xFF) == 255) && ((this.ipaddress[1] & 0xF) == 5);
/*     */   }
/*     */ 
/*     */   public boolean isMCOrgLocal()
/*     */   {
/* 596 */     return ((this.ipaddress[0] & 0xFF) == 255) && ((this.ipaddress[1] & 0xF) == 8);
/*     */   }
/*     */ 
/*     */   public byte[] getAddress()
/*     */   {
/* 609 */     return (byte[])this.ipaddress.clone();
/*     */   }
/*     */ 
/*     */   public int getScopeId()
/*     */   {
/* 620 */     return this.scope_id;
/*     */   }
/*     */ 
/*     */   public NetworkInterface getScopedInterface()
/*     */   {
/* 631 */     return this.scope_ifname;
/*     */   }
/*     */ 
/*     */   public String getHostAddress()
/*     */   {
/* 644 */     String str = numericToTextFormat(this.ipaddress);
/* 645 */     if (this.scope_ifname_set)
/* 646 */       str = str + "%" + this.scope_ifname.getName();
/* 647 */     else if (this.scope_id_set) {
/* 648 */       str = str + "%" + this.scope_id;
/*     */     }
/* 650 */     return str;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 660 */     if (this.ipaddress != null)
/*     */     {
/* 662 */       int i = 0;
/* 663 */       int j = 0;
/* 664 */       while (j < 16) {
/* 665 */         int k = 0;
/* 666 */         int m = 0;
/* 667 */         while ((k < 4) && (j < 16)) {
/* 668 */           m = (m << 8) + this.ipaddress[j];
/* 669 */           k++;
/* 670 */           j++;
/*     */         }
/* 672 */         i += m;
/*     */       }
/* 674 */       return i;
/*     */     }
/*     */ 
/* 677 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 699 */     if ((paramObject == null) || (!(paramObject instanceof Inet6Address)))
/*     */     {
/* 701 */       return false;
/*     */     }
/* 703 */     Inet6Address localInet6Address = (Inet6Address)paramObject;
/*     */ 
/* 705 */     for (int i = 0; i < 16; i++) {
/* 706 */       if (this.ipaddress[i] != localInet6Address.ipaddress[i]) {
/* 707 */         return false;
/*     */       }
/*     */     }
/* 710 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isIPv4CompatibleAddress()
/*     */   {
/* 722 */     if ((this.ipaddress[0] == 0) && (this.ipaddress[1] == 0) && (this.ipaddress[2] == 0) && (this.ipaddress[3] == 0) && (this.ipaddress[4] == 0) && (this.ipaddress[5] == 0) && (this.ipaddress[6] == 0) && (this.ipaddress[7] == 0) && (this.ipaddress[8] == 0) && (this.ipaddress[9] == 0) && (this.ipaddress[10] == 0) && (this.ipaddress[11] == 0))
/*     */     {
/* 728 */       return true;
/*     */     }
/* 730 */     return false;
/*     */   }
/*     */ 
/*     */   static String numericToTextFormat(byte[] paramArrayOfByte)
/*     */   {
/* 745 */     StringBuffer localStringBuffer = new StringBuffer(39);
/* 746 */     for (int i = 0; i < 8; i++) {
/* 747 */       localStringBuffer.append(Integer.toHexString(paramArrayOfByte[(i << 1)] << 8 & 0xFF00 | paramArrayOfByte[((i << 1) + 1)] & 0xFF));
/*     */ 
/* 749 */       if (i < 7) {
/* 750 */         localStringBuffer.append(":");
/*     */       }
/*     */     }
/* 753 */     return localStringBuffer.toString();
/*     */   }
/*     */ 
/*     */   private static native void init();
/*     */ 
/*     */   private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 774 */     if (this.scope_ifname_set) {
/* 775 */       this.ifname = this.scope_ifname.getName();
/*     */     }
/* 777 */     paramObjectOutputStream.defaultWriteObject();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 211 */     init();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.Inet6Address
 * JD-Core Version:    0.6.2
 */