/*     */ package sun.net.dns;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import sun.security.action.LoadLibraryAction;
/*     */ 
/*     */ public class ResolverConfigurationImpl extends ResolverConfiguration
/*     */ {
/*     */   private static Object lock;
/*     */   private static long lastRefresh;
/*     */   private static final int TIMEOUT = 300000;
/*     */   private final ResolverConfiguration.Options opts;
/*     */   private LinkedList<String> searchlist;
/*     */   private LinkedList<String> nameservers;
/*     */ 
/*     */   private LinkedList<String> resolvconf(String paramString, int paramInt1, int paramInt2)
/*     */   {
/*  63 */     LinkedList localLinkedList = new LinkedList();
/*     */     try
/*     */     {
/*  66 */       BufferedReader localBufferedReader = new BufferedReader(new FileReader("/etc/resolv.conf"));
/*     */       label27: String str1;
/*  93 */       for (; (str1 = localBufferedReader.readLine()) != null; 
/*  93 */         paramInt2 == 0)
/*     */       {
/*  70 */         int i = paramInt1;
/*  71 */         if ((str1.length() == 0) || 
/*  73 */           (str1.charAt(0) == '#') || (str1.charAt(0) == ';') || 
/*  75 */           (!str1.startsWith(paramString)))
/*     */           break label27;
/*  77 */         String str2 = str1.substring(paramString.length());
/*  78 */         if ((str2.length() == 0) || (
/*  80 */           (str2.charAt(0) != ' ') && (str2.charAt(0) != '\t')))
/*     */           break label27;
/*  82 */         StringTokenizer localStringTokenizer = new StringTokenizer(str2, " \t");
/*     */ 
/*  89 */         for (; localStringTokenizer.hasMoreTokens(); 
/*  89 */           i == 0)
/*     */         {
/*  84 */           String str3 = localStringTokenizer.nextToken();
/*  85 */           if ((str3.charAt(0) == '#') || (str3.charAt(0) == ';')) {
/*     */             break;
/*     */           }
/*  88 */           localLinkedList.add(str3);
/*  89 */           i--;
/*     */         }
/*     */ 
/*  93 */         paramInt2--;
/*     */       }
/*     */ 
/*  97 */       localBufferedReader.close();
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*     */     }
/* 102 */     return localLinkedList;
/*     */   }
/*     */ 
/*     */   private void loadConfig()
/*     */   {
/* 112 */     assert (Thread.holdsLock(lock));
/*     */ 
/* 115 */     if (lastRefresh >= 0L) {
/* 116 */       long l = System.currentTimeMillis();
/* 117 */       if (l - lastRefresh < 300000L) {
/* 118 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 123 */     this.nameservers = ((LinkedList)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public LinkedList<String> run()
/*     */       {
/* 129 */         return ResolverConfigurationImpl.this.resolvconf("nameserver", 1, 5);
/*     */       }
/*     */     }));
/* 134 */     this.searchlist = getSearchList();
/*     */ 
/* 137 */     lastRefresh = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   private LinkedList<String> getSearchList()
/*     */   {
/* 149 */     LinkedList localLinkedList = (LinkedList)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public LinkedList<String> run()
/*     */       {
/* 155 */         LinkedList localLinkedList = ResolverConfigurationImpl.this.resolvconf("search", 6, 1);
/* 156 */         if (localLinkedList.size() > 0) {
/* 157 */           return localLinkedList;
/*     */         }
/*     */ 
/* 160 */         return null;
/*     */       }
/*     */     });
/* 165 */     if (localLinkedList != null) {
/* 166 */       return localLinkedList;
/*     */     }
/*     */ 
/* 174 */     String str1 = localDomain0();
/* 175 */     if ((str1 != null) && (str1.length() > 0)) {
/* 176 */       localLinkedList = new LinkedList();
/* 177 */       localLinkedList.add(str1);
/* 178 */       return localLinkedList;
/*     */     }
/*     */ 
/* 183 */     localLinkedList = (LinkedList)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public LinkedList<String> run()
/*     */       {
/* 188 */         LinkedList localLinkedList = ResolverConfigurationImpl.this.resolvconf("domain", 1, 1);
/* 189 */         if (localLinkedList.size() > 0) {
/* 190 */           return localLinkedList;
/*     */         }
/* 192 */         return null;
/*     */       }
/*     */     });
/* 196 */     if (localLinkedList != null) {
/* 197 */       return localLinkedList;
/*     */     }
/*     */ 
/* 203 */     localLinkedList = new LinkedList();
/* 204 */     String str2 = fallbackDomain0();
/* 205 */     if ((str2 != null) && (str2.length() > 0)) {
/* 206 */       localLinkedList.add(str2);
/*     */     }
/*     */ 
/* 209 */     return localLinkedList;
/*     */   }
/*     */ 
/*     */   ResolverConfigurationImpl()
/*     */   {
/* 216 */     this.opts = new OptionsImpl();
/*     */   }
/*     */ 
/*     */   public List<String> searchlist() {
/* 220 */     synchronized (lock) {
/* 221 */       loadConfig();
/*     */ 
/* 224 */       return (List)this.searchlist.clone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<String> nameservers() {
/* 229 */     synchronized (lock) {
/* 230 */       loadConfig();
/*     */ 
/* 233 */       return (List)this.nameservers.clone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ResolverConfiguration.Options options() {
/* 238 */     return this.opts;
/*     */   }
/*     */ 
/*     */   static native String localDomain0();
/*     */ 
/*     */   static native String fallbackDomain0();
/*     */ 
/*     */   static
/*     */   {
/*  44 */     lock = new Object();
/*     */ 
/*  47 */     lastRefresh = -1L;
/*     */ 
/* 249 */     AccessController.doPrivileged(new LoadLibraryAction("net"));
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.net.dns.ResolverConfigurationImpl
 * JD-Core Version:    0.6.2
 */