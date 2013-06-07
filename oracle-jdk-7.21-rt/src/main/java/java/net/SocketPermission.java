/*      */ package java.net;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.security.AccessController;
/*      */ import java.security.Permission;
/*      */ import java.security.PermissionCollection;
/*      */ import java.util.StringTokenizer;
/*      */ import sun.net.RegisteredDomain;
/*      */ import sun.net.util.IPAddressUtil;
/*      */ import sun.net.www.URLConnection;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.util.Debug;
/*      */ 
/*      */ public final class SocketPermission extends Permission
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -7204263841984476862L;
/*      */   private static final int CONNECT = 1;
/*      */   private static final int LISTEN = 2;
/*      */   private static final int ACCEPT = 4;
/*      */   private static final int RESOLVE = 8;
/*      */   private static final int NONE = 0;
/*      */   private static final int ALL = 15;
/*      */   private static final int PORT_MIN = 0;
/*      */   private static final int PORT_MAX = 65535;
/*      */   private static final int PRIV_PORT_MAX = 1023;
/*      */   private transient int mask;
/*      */   private String actions;
/*      */   private transient String hostname;
/*      */   private transient String cname;
/*      */   private transient InetAddress[] addresses;
/*      */   private transient boolean wildcard;
/*      */   private transient boolean init_with_ip;
/*      */   private transient boolean invalid;
/*      */   private transient int[] portrange;
/*  216 */   private transient boolean defaultDeny = false;
/*      */   private transient boolean untrusted;
/*      */   private transient boolean trusted;
/*  232 */   private static boolean trustNameService = localBoolean.booleanValue();
/*      */ 
/*  226 */   private static Debug debug = null;
/*  227 */   private static boolean debugInit = false;
/*      */   private transient String cdomain;
/*      */   private transient String hdomain;
/*      */ 
/*      */   private static synchronized Debug getDebug()
/*      */   {
/*  237 */     if (!debugInit) {
/*  238 */       debug = Debug.getInstance("access");
/*  239 */       debugInit = true;
/*      */     }
/*  241 */     return debug;
/*      */   }
/*      */ 
/*      */   public SocketPermission(String paramString1, String paramString2)
/*      */   {
/*  275 */     super(getHost(paramString1));
/*      */ 
/*  277 */     init(getName(), getMask(paramString2));
/*      */   }
/*      */ 
/*      */   SocketPermission(String paramString, int paramInt)
/*      */   {
/*  282 */     super(getHost(paramString));
/*      */ 
/*  284 */     init(getName(), paramInt);
/*      */   }
/*      */ 
/*      */   private void setDeny() {
/*  288 */     this.defaultDeny = true;
/*      */   }
/*      */ 
/*      */   private static String getHost(String paramString)
/*      */   {
/*  293 */     if (paramString.equals(""))
/*  294 */       return "localhost";
/*      */     int i;
/*  301 */     if ((paramString.charAt(0) != '[') && 
/*  302 */       ((i = paramString.indexOf(':')) != paramString.lastIndexOf(':')))
/*      */     {
/*  307 */       StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ":");
/*  308 */       int j = localStringTokenizer.countTokens();
/*  309 */       if (j == 9)
/*      */       {
/*  311 */         i = paramString.lastIndexOf(':');
/*  312 */         paramString = "[" + paramString.substring(0, i) + "]" + paramString.substring(i);
/*      */       }
/*  314 */       else if ((j == 8) && (paramString.indexOf("::") == -1))
/*      */       {
/*  316 */         paramString = "[" + paramString + "]";
/*      */       }
/*      */       else {
/*  319 */         throw new IllegalArgumentException("Ambiguous hostport part");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  324 */     return paramString;
/*      */   }
/*      */ 
/*      */   private int[] parsePort(String paramString)
/*      */     throws Exception
/*      */   {
/*  332 */     if ((paramString == null) || (paramString.equals("")) || (paramString.equals("*"))) {
/*  333 */       return new int[] { 0, 65535 };
/*      */     }
/*      */ 
/*  336 */     int i = paramString.indexOf('-');
/*      */ 
/*  338 */     if (i == -1) {
/*  339 */       int j = Integer.parseInt(paramString);
/*  340 */       return new int[] { j, j };
/*      */     }
/*  342 */     String str1 = paramString.substring(0, i);
/*  343 */     String str2 = paramString.substring(i + 1);
/*      */     int k;
/*  346 */     if (str1.equals(""))
/*  347 */       k = 0;
/*      */     else
/*  349 */       k = Integer.parseInt(str1);
/*      */     int m;
/*  352 */     if (str2.equals(""))
/*  353 */       m = 65535;
/*      */     else {
/*  355 */       m = Integer.parseInt(str2);
/*      */     }
/*  357 */     if ((k < 0) || (m < 0) || (m < k)) {
/*  358 */       throw new IllegalArgumentException("invalid port range");
/*      */     }
/*  360 */     return new int[] { k, m };
/*      */   }
/*      */ 
/*      */   private void init(String paramString, int paramInt)
/*      */   {
/*  372 */     if ((paramInt & 0xF) != paramInt) {
/*  373 */       throw new IllegalArgumentException("invalid actions mask");
/*      */     }
/*      */ 
/*  376 */     this.mask = (paramInt | 0x8);
/*      */ 
/*  387 */     int i = 0;
/*  388 */     int j = 0; int k = 0;
/*  389 */     int m = -1;
/*  390 */     String str1 = paramString;
/*  391 */     if (paramString.charAt(0) == '[') {
/*  392 */       j = 1;
/*  393 */       i = paramString.indexOf(']');
/*  394 */       if (i != -1)
/*  395 */         paramString = paramString.substring(j, i);
/*      */       else {
/*  397 */         throw new IllegalArgumentException("invalid host/port: " + paramString);
/*      */       }
/*      */ 
/*  400 */       m = str1.indexOf(':', i + 1);
/*      */     } else {
/*  402 */       j = 0;
/*  403 */       m = paramString.indexOf(':', i);
/*  404 */       k = m;
/*  405 */       if (m != -1) {
/*  406 */         paramString = paramString.substring(j, k);
/*      */       }
/*      */     }
/*      */ 
/*  410 */     if (m != -1) {
/*  411 */       String str2 = str1.substring(m + 1);
/*      */       try {
/*  413 */         this.portrange = parsePort(str2);
/*      */       } catch (Exception localException) {
/*  415 */         throw new IllegalArgumentException("invalid port range: " + str2);
/*      */       }
/*      */     }
/*      */     else {
/*  419 */       this.portrange = new int[] { 0, 65535 };
/*      */     }
/*      */ 
/*  422 */     this.hostname = paramString;
/*      */ 
/*  425 */     if (paramString.lastIndexOf('*') > 0) {
/*  426 */       throw new IllegalArgumentException("invalid host wildcard specification");
/*      */     }
/*  428 */     if (paramString.startsWith("*")) {
/*  429 */       this.wildcard = true;
/*  430 */       if (paramString.equals("*"))
/*  431 */         this.cname = "";
/*  432 */       else if (paramString.startsWith("*."))
/*  433 */         this.cname = paramString.substring(1).toLowerCase();
/*      */       else {
/*  435 */         throw new IllegalArgumentException("invalid host wildcard specification");
/*      */       }
/*      */ 
/*  438 */       return;
/*      */     }
/*  440 */     if (paramString.length() > 0)
/*      */     {
/*  442 */       char c = paramString.charAt(0);
/*  443 */       if ((c == ':') || (Character.digit(c, 16) != -1)) {
/*  444 */         byte[] arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
/*  445 */         if (arrayOfByte == null) {
/*  446 */           arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString);
/*      */         }
/*  448 */         if (arrayOfByte != null)
/*      */           try {
/*  450 */             this.addresses = new InetAddress[] { InetAddress.getByAddress(arrayOfByte) };
/*      */ 
/*  453 */             this.init_with_ip = true;
/*      */           }
/*      */           catch (UnknownHostException localUnknownHostException) {
/*  456 */             this.invalid = true;
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int getMask(String paramString)
/*      */   {
/*  472 */     if (paramString == null) {
/*  473 */       throw new NullPointerException("action can't be null");
/*      */     }
/*      */ 
/*  476 */     if (paramString.equals("")) {
/*  477 */       throw new IllegalArgumentException("action can't be empty");
/*      */     }
/*      */ 
/*  480 */     int i = 0;
/*      */ 
/*  483 */     if (paramString == "resolve")
/*  484 */       return 8;
/*  485 */     if (paramString == "connect")
/*  486 */       return 1;
/*  487 */     if (paramString == "listen")
/*  488 */       return 2;
/*  489 */     if (paramString == "accept")
/*  490 */       return 4;
/*  491 */     if (paramString == "connect,accept") {
/*  492 */       return 5;
/*      */     }
/*      */ 
/*  495 */     char[] arrayOfChar = paramString.toCharArray();
/*      */ 
/*  497 */     int j = arrayOfChar.length - 1;
/*  498 */     if (j < 0) {
/*  499 */       return i;
/*      */     }
/*  501 */     while (j != -1)
/*      */     {
/*      */       int k;
/*  505 */       while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9)))
/*      */       {
/*  510 */         j--;
/*      */       }
/*      */       int m;
/*  515 */       if ((j >= 6) && ((arrayOfChar[(j - 6)] == 'c') || (arrayOfChar[(j - 6)] == 'C')) && ((arrayOfChar[(j - 5)] == 'o') || (arrayOfChar[(j - 5)] == 'O')) && ((arrayOfChar[(j - 4)] == 'n') || (arrayOfChar[(j - 4)] == 'N')) && ((arrayOfChar[(j - 3)] == 'n') || (arrayOfChar[(j - 3)] == 'N')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'c') || (arrayOfChar[(j - 1)] == 'C')) && ((arrayOfChar[j] == 't') || (arrayOfChar[j] == 'T')))
/*      */       {
/*  523 */         m = 7;
/*  524 */         i |= 1;
/*      */       }
/*  526 */       else if ((j >= 6) && ((arrayOfChar[(j - 6)] == 'r') || (arrayOfChar[(j - 6)] == 'R')) && ((arrayOfChar[(j - 5)] == 'e') || (arrayOfChar[(j - 5)] == 'E')) && ((arrayOfChar[(j - 4)] == 's') || (arrayOfChar[(j - 4)] == 'S')) && ((arrayOfChar[(j - 3)] == 'o') || (arrayOfChar[(j - 3)] == 'O')) && ((arrayOfChar[(j - 2)] == 'l') || (arrayOfChar[(j - 2)] == 'L')) && ((arrayOfChar[(j - 1)] == 'v') || (arrayOfChar[(j - 1)] == 'V')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
/*      */       {
/*  534 */         m = 7;
/*  535 */         i |= 8;
/*      */       }
/*  537 */       else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'l') || (arrayOfChar[(j - 5)] == 'L')) && ((arrayOfChar[(j - 4)] == 'i') || (arrayOfChar[(j - 4)] == 'I')) && ((arrayOfChar[(j - 3)] == 's') || (arrayOfChar[(j - 3)] == 'S')) && ((arrayOfChar[(j - 2)] == 't') || (arrayOfChar[(j - 2)] == 'T')) && ((arrayOfChar[(j - 1)] == 'e') || (arrayOfChar[(j - 1)] == 'E')) && ((arrayOfChar[j] == 'n') || (arrayOfChar[j] == 'N')))
/*      */       {
/*  544 */         m = 6;
/*  545 */         i |= 2;
/*      */       }
/*  547 */       else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'a') || (arrayOfChar[(j - 5)] == 'A')) && ((arrayOfChar[(j - 4)] == 'c') || (arrayOfChar[(j - 4)] == 'C')) && ((arrayOfChar[(j - 3)] == 'c') || (arrayOfChar[(j - 3)] == 'C')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'p') || (arrayOfChar[(j - 1)] == 'P')) && ((arrayOfChar[j] == 't') || (arrayOfChar[j] == 'T')))
/*      */       {
/*  554 */         m = 6;
/*  555 */         i |= 4;
/*      */       }
/*      */       else
/*      */       {
/*  559 */         throw new IllegalArgumentException("invalid permission: " + paramString);
/*      */       }
/*      */ 
/*  565 */       int n = 0;
/*  566 */       while ((j >= m) && (n == 0)) {
/*  567 */         switch (arrayOfChar[(j - m)]) {
/*      */         case ',':
/*  569 */           n = 1;
/*      */         case '\t':
/*      */         case '\n':
/*      */         case '\f':
/*      */         case '\r':
/*      */         case ' ':
/*  573 */           break;
/*      */         default:
/*  575 */           throw new IllegalArgumentException("invalid permission: " + paramString);
/*      */         }
/*      */ 
/*  578 */         j--;
/*      */       }
/*      */ 
/*  582 */       j -= m;
/*      */     }
/*      */ 
/*  585 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean isUntrusted()
/*      */     throws UnknownHostException
/*      */   {
/*  591 */     if (this.trusted) return false;
/*  592 */     if ((this.invalid) || (this.untrusted)) return true; try
/*      */     {
/*  594 */       if ((!trustNameService) && ((this.defaultDeny) || (URLConnection.isProxiedHost(this.hostname))))
/*      */       {
/*  596 */         if (this.cname == null) {
/*  597 */           getCanonName();
/*      */         }
/*  599 */         if (!match(this.cname, this.hostname))
/*      */         {
/*  601 */           if (!authorized(this.hostname, this.addresses[0].getAddress())) {
/*  602 */             this.untrusted = true;
/*  603 */             Debug localDebug = getDebug();
/*  604 */             if ((localDebug != null) && (Debug.isOn("failure"))) {
/*  605 */               localDebug.println("socket access restriction: proxied host (" + this.addresses[0] + ")" + " does not match " + this.cname + " from reverse lookup");
/*      */             }
/*  607 */             return true;
/*      */           }
/*      */         }
/*  610 */         this.trusted = true;
/*      */       }
/*      */     } catch (UnknownHostException localUnknownHostException) {
/*  613 */       this.invalid = true;
/*  614 */       throw localUnknownHostException;
/*      */     }
/*  616 */     return false;
/*      */   }
/*      */ 
/*      */   void getCanonName()
/*      */     throws UnknownHostException
/*      */   {
/*  626 */     if ((this.cname != null) || (this.invalid) || (this.untrusted)) return;
/*      */ 
/*      */     try
/*      */     {
/*  634 */       if (this.addresses == null) {
/*  635 */         getIP();
/*      */       }
/*      */ 
/*  640 */       if (this.init_with_ip)
/*  641 */         this.cname = this.addresses[0].getHostName(false).toLowerCase();
/*      */       else
/*  643 */         this.cname = InetAddress.getByName(this.addresses[0].getHostAddress()).getHostName(false).toLowerCase();
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException)
/*      */     {
/*  647 */       this.invalid = true;
/*  648 */       throw localUnknownHostException;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean match(String paramString1, String paramString2)
/*      */   {
/*  655 */     String str1 = paramString1.toLowerCase();
/*  656 */     String str2 = paramString2.toLowerCase();
/*  657 */     if ((str1.startsWith(str2)) && ((str1.length() == str2.length()) || (str1.charAt(str2.length()) == '.')))
/*      */     {
/*  659 */       return true;
/*  660 */     }if (this.cdomain == null) {
/*  661 */       this.cdomain = RegisteredDomain.getRegisteredDomain(str1);
/*      */     }
/*  663 */     if (this.hdomain == null) {
/*  664 */       this.hdomain = RegisteredDomain.getRegisteredDomain(str2);
/*      */     }
/*      */ 
/*  667 */     return (this.cdomain.length() != 0) && (this.hdomain.length() != 0) && (this.cdomain.equals(this.hdomain));
/*      */   }
/*      */ 
/*      */   private boolean authorized(String paramString, byte[] paramArrayOfByte)
/*      */   {
/*  672 */     if (paramArrayOfByte.length == 4)
/*  673 */       return authorizedIPv4(paramString, paramArrayOfByte);
/*  674 */     if (paramArrayOfByte.length == 16) {
/*  675 */       return authorizedIPv6(paramString, paramArrayOfByte);
/*      */     }
/*  677 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean authorizedIPv4(String paramString, byte[] paramArrayOfByte) {
/*  681 */     String str = "";
/*      */     try
/*      */     {
/*  685 */       str = "auth." + (paramArrayOfByte[3] & 0xFF) + "." + (paramArrayOfByte[2] & 0xFF) + "." + (paramArrayOfByte[1] & 0xFF) + "." + (paramArrayOfByte[0] & 0xFF) + ".in-addr.arpa";
/*      */ 
/*  691 */       str = this.hostname + '.' + str;
/*  692 */       InetAddress localInetAddress = InetAddress.getAllByName0(str, false)[0];
/*  693 */       if (localInetAddress.equals(InetAddress.getByAddress(paramArrayOfByte))) {
/*  694 */         return true;
/*      */       }
/*  696 */       Debug localDebug1 = getDebug();
/*  697 */       if ((localDebug1 != null) && (Debug.isOn("failure")))
/*  698 */         localDebug1.println("socket access restriction: IP address of " + localInetAddress + " != " + InetAddress.getByAddress(paramArrayOfByte));
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException) {
/*  701 */       Debug localDebug2 = getDebug();
/*  702 */       if ((localDebug2 != null) && (Debug.isOn("failure"))) {
/*  703 */         localDebug2.println("socket access restriction: forward lookup failed for " + str);
/*      */       }
/*      */     }
/*  706 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean authorizedIPv6(String paramString, byte[] paramArrayOfByte) {
/*  710 */     String str = "";
/*      */     try
/*      */     {
/*  714 */       StringBuffer localStringBuffer = new StringBuffer(39);
/*      */ 
/*  716 */       for (int i = 15; i >= 0; i--) {
/*  717 */         localStringBuffer.append(Integer.toHexString(paramArrayOfByte[i] & 0xF));
/*  718 */         localStringBuffer.append('.');
/*  719 */         localStringBuffer.append(Integer.toHexString(paramArrayOfByte[i] >> 4 & 0xF));
/*  720 */         localStringBuffer.append('.');
/*      */       }
/*  722 */       str = "auth." + localStringBuffer.toString() + "IP6.ARPA";
/*      */ 
/*  724 */       str = this.hostname + '.' + str;
/*  725 */       InetAddress localInetAddress = InetAddress.getAllByName0(str, false)[0];
/*  726 */       if (localInetAddress.equals(InetAddress.getByAddress(paramArrayOfByte)))
/*  727 */         return true;
/*  728 */       localDebug = getDebug();
/*  729 */       if ((localDebug != null) && (Debug.isOn("failure")))
/*  730 */         localDebug.println("socket access restriction: IP address of " + localInetAddress + " != " + InetAddress.getByAddress(paramArrayOfByte));
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException) {
/*  733 */       Debug localDebug = getDebug();
/*  734 */       if ((localDebug != null) && (Debug.isOn("failure"))) {
/*  735 */         localDebug.println("socket access restriction: forward lookup failed for " + str);
/*      */       }
/*      */     }
/*  738 */     return false;
/*      */   }
/*      */ 
/*      */   void getIP()
/*      */     throws UnknownHostException
/*      */   {
/*  749 */     if ((this.addresses != null) || (this.wildcard) || (this.invalid)) return;
/*      */     try
/*      */     {
/*      */       String str;
/*  754 */       if (getName().charAt(0) == '[')
/*      */       {
/*  756 */         str = getName().substring(1, getName().indexOf(']'));
/*      */       } else {
/*  758 */         int i = getName().indexOf(":");
/*  759 */         if (i == -1)
/*  760 */           str = getName();
/*      */         else {
/*  762 */           str = getName().substring(0, i);
/*      */         }
/*      */       }
/*      */ 
/*  766 */       this.addresses = new InetAddress[] { InetAddress.getAllByName0(str, false)[0] };
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException)
/*      */     {
/*  770 */       this.invalid = true;
/*  771 */       throw localUnknownHostException;
/*      */     } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/*  773 */       this.invalid = true;
/*  774 */       throw new UnknownHostException(getName());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean implies(Permission paramPermission)
/*      */   {
/*  816 */     if (!(paramPermission instanceof SocketPermission)) {
/*  817 */       return false;
/*      */     }
/*  819 */     if (paramPermission == this) {
/*  820 */       return true;
/*      */     }
/*  822 */     SocketPermission localSocketPermission = (SocketPermission)paramPermission;
/*      */ 
/*  824 */     return ((this.mask & localSocketPermission.mask) == localSocketPermission.mask) && (impliesIgnoreMask(localSocketPermission));
/*      */   }
/*      */ 
/*      */   boolean impliesIgnoreMask(SocketPermission paramSocketPermission)
/*      */   {
/*  855 */     if ((paramSocketPermission.mask & 0x8) != paramSocketPermission.mask)
/*      */     {
/*  857 */       if ((paramSocketPermission.portrange[0] < this.portrange[0]) || (paramSocketPermission.portrange[1] > this.portrange[1]))
/*      */       {
/*  859 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  864 */     if ((this.wildcard) && ("".equals(this.cname))) {
/*  865 */       return true;
/*      */     }
/*      */ 
/*  868 */     if ((this.invalid) || (paramSocketPermission.invalid))
/*  869 */       return compareHostnames(paramSocketPermission);
/*      */     try
/*      */     {
/*      */       int i;
/*  873 */       if (this.init_with_ip) {
/*  874 */         if (paramSocketPermission.wildcard) {
/*  875 */           return false;
/*      */         }
/*  877 */         if (paramSocketPermission.init_with_ip) {
/*  878 */           return this.addresses[0].equals(paramSocketPermission.addresses[0]);
/*      */         }
/*  880 */         if (paramSocketPermission.addresses == null) {
/*  881 */           paramSocketPermission.getIP();
/*      */         }
/*  883 */         for (i = 0; i < paramSocketPermission.addresses.length; i++) {
/*  884 */           if (this.addresses[0].equals(paramSocketPermission.addresses[i])) {
/*  885 */             return true;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  890 */         return false;
/*      */       }
/*      */ 
/*  894 */       if ((this.wildcard) || (paramSocketPermission.wildcard))
/*      */       {
/*  898 */         if ((this.wildcard) && (paramSocketPermission.wildcard)) {
/*  899 */           return paramSocketPermission.cname.endsWith(this.cname);
/*      */         }
/*      */ 
/*  902 */         if (paramSocketPermission.wildcard) {
/*  903 */           return false;
/*      */         }
/*      */ 
/*  907 */         if (paramSocketPermission.cname == null) {
/*  908 */           paramSocketPermission.getCanonName();
/*      */         }
/*  910 */         return paramSocketPermission.cname.endsWith(this.cname);
/*      */       }
/*      */ 
/*  914 */       if (this.addresses == null) {
/*  915 */         getIP();
/*      */       }
/*      */ 
/*  918 */       if (paramSocketPermission.addresses == null) {
/*  919 */         paramSocketPermission.getIP();
/*      */       }
/*      */ 
/*  922 */       if ((!paramSocketPermission.init_with_ip) || (!isUntrusted())) {
/*  923 */         for (int j = 0; j < this.addresses.length; j++) {
/*  924 */           for (i = 0; i < paramSocketPermission.addresses.length; i++) {
/*  925 */             if (this.addresses[j].equals(paramSocketPermission.addresses[i])) {
/*  926 */               return true;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  932 */         if (this.cname == null) {
/*  933 */           getCanonName();
/*      */         }
/*      */ 
/*  936 */         if (paramSocketPermission.cname == null) {
/*  937 */           paramSocketPermission.getCanonName();
/*      */         }
/*      */ 
/*  940 */         return this.cname.equalsIgnoreCase(paramSocketPermission.cname);
/*      */       }
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException) {
/*  944 */       return compareHostnames(paramSocketPermission);
/*      */     }
/*      */ 
/*  950 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean compareHostnames(SocketPermission paramSocketPermission)
/*      */   {
/*  956 */     String str1 = this.hostname;
/*  957 */     String str2 = paramSocketPermission.hostname;
/*      */ 
/*  959 */     if (str1 == null) {
/*  960 */       return false;
/*      */     }
/*  962 */     return str1.equalsIgnoreCase(str2);
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  976 */     if (paramObject == this) {
/*  977 */       return true;
/*      */     }
/*  979 */     if (!(paramObject instanceof SocketPermission)) {
/*  980 */       return false;
/*      */     }
/*  982 */     SocketPermission localSocketPermission = (SocketPermission)paramObject;
/*      */ 
/*  987 */     if (this.mask != localSocketPermission.mask) return false;
/*      */ 
/*  989 */     if ((localSocketPermission.mask & 0x8) != localSocketPermission.mask)
/*      */     {
/*  991 */       if ((this.portrange[0] != localSocketPermission.portrange[0]) || (this.portrange[1] != localSocketPermission.portrange[1]))
/*      */       {
/*  993 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1004 */     if (getName().equalsIgnoreCase(localSocketPermission.getName())) {
/* 1005 */       return true;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1013 */       getCanonName();
/* 1014 */       localSocketPermission.getCanonName();
/*      */     } catch (UnknownHostException localUnknownHostException) {
/* 1016 */       return false;
/*      */     }
/*      */ 
/* 1019 */     if ((this.invalid) || (localSocketPermission.invalid)) {
/* 1020 */       return false;
/*      */     }
/* 1022 */     if (this.cname != null) {
/* 1023 */       return this.cname.equalsIgnoreCase(localSocketPermission.cname);
/*      */     }
/*      */ 
/* 1026 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1043 */     if ((this.init_with_ip) || (this.wildcard)) {
/* 1044 */       return getName().hashCode();
/*      */     }
/*      */     try
/*      */     {
/* 1048 */       getCanonName();
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException)
/*      */     {
/*      */     }
/* 1053 */     if ((this.invalid) || (this.cname == null)) {
/* 1054 */       return getName().hashCode();
/*      */     }
/* 1056 */     return this.cname.hashCode();
/*      */   }
/*      */ 
/*      */   int getMask()
/*      */   {
/* 1066 */     return this.mask;
/*      */   }
/*      */ 
/*      */   private static String getActions(int paramInt)
/*      */   {
/* 1080 */     StringBuilder localStringBuilder = new StringBuilder();
/* 1081 */     int i = 0;
/*      */ 
/* 1083 */     if ((paramInt & 0x1) == 1) {
/* 1084 */       i = 1;
/* 1085 */       localStringBuilder.append("connect");
/*      */     }
/*      */ 
/* 1088 */     if ((paramInt & 0x2) == 2) {
/* 1089 */       if (i != 0) localStringBuilder.append(','); else
/* 1090 */         i = 1;
/* 1091 */       localStringBuilder.append("listen");
/*      */     }
/*      */ 
/* 1094 */     if ((paramInt & 0x4) == 4) {
/* 1095 */       if (i != 0) localStringBuilder.append(','); else
/* 1096 */         i = 1;
/* 1097 */       localStringBuilder.append("accept");
/*      */     }
/*      */ 
/* 1101 */     if ((paramInt & 0x8) == 8) {
/* 1102 */       if (i != 0) localStringBuilder.append(','); else
/* 1103 */         i = 1;
/* 1104 */       localStringBuilder.append("resolve");
/*      */     }
/*      */ 
/* 1107 */     return localStringBuilder.toString();
/*      */   }
/*      */ 
/*      */   public String getActions()
/*      */   {
/* 1119 */     if (this.actions == null) {
/* 1120 */       this.actions = getActions(this.mask);
/*      */     }
/* 1122 */     return this.actions;
/*      */   }
/*      */ 
/*      */   public PermissionCollection newPermissionCollection()
/*      */   {
/* 1138 */     return new SocketPermissionCollection();
/*      */   }
/*      */ 
/*      */   private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */     throws IOException
/*      */   {
/* 1151 */     if (this.actions == null)
/* 1152 */       getActions();
/* 1153 */     paramObjectOutputStream.defaultWriteObject();
/*      */   }
/*      */ 
/*      */   private synchronized void readObject(ObjectInputStream paramObjectInputStream)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1164 */     paramObjectInputStream.defaultReadObject();
/* 1165 */     init(getName(), getMask(this.actions));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  230 */     Boolean localBoolean = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.net.trustNameService"));
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.SocketPermission
 * JD-Core Version:    0.6.2
 */