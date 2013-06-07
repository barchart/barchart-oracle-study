/*     */ package sun.net.sdp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Formatter;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Scanner;
/*     */ import sun.net.NetHooks.Provider;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ public class SdpProvider extends NetHooks.Provider
/*     */ {
/*     */   private static final int MAX_PORT = 65535;
/*     */   private final boolean enabled;
/*     */   private final List<Rule> rules;
/*     */   private PrintStream log;
/*     */ 
/*     */   public SdpProvider()
/*     */   {
/*  60 */     String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.sdp.conf"));
/*     */ 
/*  62 */     if (str1 == null) {
/*  63 */       this.enabled = false;
/*  64 */       this.rules = null;
/*  65 */       return;
/*     */     }
/*     */ 
/*  69 */     List localList = null;
/*  70 */     if (str1 != null) {
/*     */       try {
/*  72 */         localList = loadRulesFromFile(str1);
/*     */       } catch (IOException localIOException1) {
/*  74 */         fail("Error reading %s: %s", new Object[] { str1, localIOException1.getMessage() });
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  79 */     PrintStream localPrintStream = null;
/*  80 */     String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.sdp.debug"));
/*     */ 
/*  82 */     if (str2 != null) {
/*  83 */       localPrintStream = System.out;
/*  84 */       if (str2.length() > 0)
/*     */         try {
/*  86 */           localPrintStream = new PrintStream(str2);
/*     */         }
/*     */         catch (IOException localIOException2) {
/*     */         }
/*     */     }
/*  91 */     this.enabled = (!localList.isEmpty());
/*  92 */     this.rules = localList;
/*  93 */     this.log = localPrintStream;
/*     */   }
/*     */ 
/*     */   private static int[] parsePortRange(String paramString)
/*     */   {
/* 165 */     int i = paramString.indexOf('-');
/*     */     try {
/* 167 */       int[] arrayOfInt = new int[2];
/* 168 */       if (i < 0) {
/* 169 */         boolean bool = paramString.equals("*");
/* 170 */         arrayOfInt[0] = (bool ? 0 : Integer.parseInt(paramString));
/* 171 */         arrayOfInt[1] = (bool ? 65535 : arrayOfInt[0]);
/*     */       } else {
/* 173 */         String str1 = paramString.substring(0, i);
/* 174 */         if (str1.length() == 0) str1 = "*";
/* 175 */         String str2 = paramString.substring(i + 1);
/* 176 */         if (str2.length() == 0) str2 = "*";
/* 177 */         arrayOfInt[0] = (str1.equals("*") ? 0 : Integer.parseInt(str1));
/* 178 */         arrayOfInt[1] = (str2.equals("*") ? 65535 : Integer.parseInt(str2));
/*     */       }
/* 180 */       return arrayOfInt; } catch (NumberFormatException localNumberFormatException) {
/*     */     }
/* 182 */     return new int[0];
/*     */   }
/*     */ 
/*     */   private static void fail(String paramString, Object[] paramArrayOfObject)
/*     */   {
/* 187 */     Formatter localFormatter = new Formatter();
/* 188 */     localFormatter.format(paramString, paramArrayOfObject);
/* 189 */     throw new RuntimeException(localFormatter.out().toString());
/*     */   }
/*     */ 
/*     */   private static List<Rule> loadRulesFromFile(String paramString)
/*     */     throws IOException
/*     */   {
/* 199 */     Scanner localScanner = new Scanner(new File(paramString));
/*     */     try {
/* 201 */       ArrayList localArrayList = new ArrayList();
/*     */       Object localObject1;
/* 202 */       while (localScanner.hasNextLine()) {
/* 203 */         localObject1 = localScanner.nextLine().trim();
/*     */ 
/* 206 */         if ((((String)localObject1).length() != 0) && (((String)localObject1).charAt(0) != '#'))
/*     */         {
/* 210 */           String[] arrayOfString = ((String)localObject1).split("\\s+");
/* 211 */           if (arrayOfString.length != 3) {
/* 212 */             fail("Malformed line '%s'", new Object[] { localObject1 });
/*     */           }
/*     */           else
/*     */           {
/* 217 */             Object localObject2 = null;
/*     */             Object localObject5;
/* 218 */             for (localObject5 : Action.values()) {
/* 219 */               if (arrayOfString[0].equalsIgnoreCase(localObject5.name())) {
/* 220 */                 localObject2 = localObject5;
/* 221 */                 break;
/*     */               }
/*     */             }
/* 224 */             if (localObject2 == null) {
/* 225 */               fail("Action '%s' not recognized", new Object[] { arrayOfString[0] });
/*     */             }
/*     */             else
/*     */             {
/* 230 */               ??? = parsePortRange(arrayOfString[2]);
/* 231 */               if (???.length == 0) {
/* 232 */                 fail("Malformed port range '%s'", new Object[] { arrayOfString[2] });
/*     */               }
/* 237 */               else if (arrayOfString[1].equals("*")) {
/* 238 */                 localArrayList.add(new PortRangeRule(localObject2, ???[0], ???[1]));
/*     */               }
/*     */               else
/*     */               {
/* 243 */                 ??? = arrayOfString[1].indexOf('/');
/*     */                 try
/*     */                 {
/*     */                   Object localObject4;
/* 245 */                   if (??? < 0)
/*     */                   {
/* 247 */                     localObject4 = InetAddress.getAllByName(arrayOfString[1]);
/* 248 */                     for (InetAddress localInetAddress : localObject4) {
/* 249 */                       int i1 = (localInetAddress instanceof Inet4Address) ? 32 : 128;
/*     */ 
/* 251 */                       localArrayList.add(new AddressPortRangeRule(localObject2, localInetAddress, i1, ???[0], ???[1]));
/*     */                     }
/*     */                   }
/*     */                   else
/*     */                   {
/* 256 */                     localObject4 = InetAddress.getByName(arrayOfString[1].substring(0, ???));
/*     */ 
/* 258 */                     int k = -1;
/*     */                     try {
/* 260 */                       k = Integer.parseInt(arrayOfString[1].substring(??? + 1));
/* 261 */                       if ((localObject4 instanceof Inet4Address))
/*     */                       {
/* 263 */                         if ((k < 0) || (k > 32)) k = -1;
/*     */ 
/*     */                       }
/* 266 */                       else if ((k < 0) || (k > 128)) k = -1;
/*     */                     }
/*     */                     catch (NumberFormatException localNumberFormatException)
/*     */                     {
/*     */                     }
/* 271 */                     if (k > 0) {
/* 272 */                       localArrayList.add(new AddressPortRangeRule(localObject2, (InetAddress)localObject4, k, ???[0], ???[1]));
/*     */                     }
/*     */                     else {
/* 275 */                       fail("Malformed prefix '%s'", new Object[] { arrayOfString[1] });
/* 276 */                       continue;
/*     */                     }
/*     */                   }
/*     */                 } catch (UnknownHostException localUnknownHostException) {
/* 280 */                   fail("Unknown host or malformed IP address '%s'", new Object[] { arrayOfString[1] });
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 284 */       return localArrayList;
/*     */     } finally {
/* 286 */       localScanner.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void convertTcpToSdpIfMatch(FileDescriptor paramFileDescriptor, Action paramAction, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 297 */     int i = 0;
/* 298 */     for (Object localObject = this.rules.iterator(); ((Iterator)localObject).hasNext(); ) { Rule localRule = (Rule)((Iterator)localObject).next();
/* 299 */       if (localRule.match(paramAction, paramInetAddress, paramInt)) {
/* 300 */         SdpSupport.convertSocket(paramFileDescriptor);
/* 301 */         i = 1;
/* 302 */         break;
/*     */       }
/*     */     }
/* 305 */     if (this.log != null) {
/* 306 */       localObject = "[" + paramInetAddress.getHostAddress() + "]";
/*     */ 
/* 308 */       if (i != 0)
/* 309 */         this.log.format("%s to %s:%d (socket converted to SDP protocol)\n", new Object[] { paramAction, localObject, Integer.valueOf(paramInt) });
/*     */       else
/* 311 */         this.log.format("%s to %s:%d (no match)\n", new Object[] { paramAction, localObject, Integer.valueOf(paramInt) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void implBeforeTcpBind(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 322 */     if (this.enabled)
/* 323 */       convertTcpToSdpIfMatch(paramFileDescriptor, Action.BIND, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   public void implBeforeTcpConnect(FileDescriptor paramFileDescriptor, InetAddress paramInetAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/* 332 */     if (this.enabled)
/* 333 */       convertTcpToSdpIfMatch(paramFileDescriptor, Action.CONNECT, paramInetAddress, paramInt);
/*     */   }
/*     */ 
/*     */   private static enum Action
/*     */   {
/*  98 */     BIND, 
/*  99 */     CONNECT;
/*     */   }
/*     */ 
/*     */   private static class AddressPortRangeRule extends SdpProvider.PortRangeRule
/*     */   {
/*     */     private final byte[] addressAsBytes;
/*     */     private final int prefixByteCount;
/*     */     private final byte mask;
/*     */ 
/*     */     AddressPortRangeRule(SdpProvider.Action paramAction, InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 136 */       super(paramInt2, paramInt3);
/* 137 */       this.addressAsBytes = paramInetAddress.getAddress();
/* 138 */       this.prefixByteCount = (paramInt1 >> 3);
/* 139 */       this.mask = ((byte)(255 << 8 - paramInt1 % 8));
/*     */     }
/*     */ 
/*     */     public boolean match(SdpProvider.Action paramAction, InetAddress paramInetAddress, int paramInt) {
/* 143 */       if (paramAction != action())
/* 144 */         return false;
/* 145 */       byte[] arrayOfByte = paramInetAddress.getAddress();
/*     */ 
/* 147 */       if (arrayOfByte.length != this.addressAsBytes.length) {
/* 148 */         return false;
/*     */       }
/* 150 */       for (int i = 0; i < this.prefixByteCount; i++) {
/* 151 */         if (arrayOfByte[i] != this.addressAsBytes[i]) {
/* 152 */           return false;
/*     */         }
/*     */       }
/* 155 */       if ((this.prefixByteCount < this.addressAsBytes.length) && ((arrayOfByte[this.prefixByteCount] & this.mask) != (this.addressAsBytes[this.prefixByteCount] & this.mask)))
/*     */       {
/* 158 */         return false;
/* 159 */       }return super.match(paramAction, paramInetAddress, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PortRangeRule
/*     */     implements SdpProvider.Rule
/*     */   {
/*     */     private final SdpProvider.Action action;
/*     */     private final int portStart;
/*     */     private final int portEnd;
/*     */ 
/*     */     PortRangeRule(SdpProvider.Action paramAction, int paramInt1, int paramInt2)
/*     */     {
/* 113 */       this.action = paramAction;
/* 114 */       this.portStart = paramInt1;
/* 115 */       this.portEnd = paramInt2;
/*     */     }
/*     */     SdpProvider.Action action() {
/* 118 */       return this.action;
/*     */     }
/*     */ 
/*     */     public boolean match(SdpProvider.Action paramAction, InetAddress paramInetAddress, int paramInt) {
/* 122 */       return (paramAction == this.action) && (paramInt >= this.portStart) && (paramInt <= this.portEnd);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface Rule
/*     */   {
/*     */     public abstract boolean match(SdpProvider.Action paramAction, InetAddress paramInetAddress, int paramInt);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.sdp.SdpProvider
 * JD-Core Version:    0.6.2
 */