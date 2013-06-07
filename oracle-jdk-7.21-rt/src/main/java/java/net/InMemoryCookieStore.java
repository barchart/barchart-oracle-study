/*     */ package java.net;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ class InMemoryCookieStore
/*     */   implements CookieStore
/*     */ {
/*  48 */   private List<HttpCookie> cookieJar = null;
/*     */ 
/*  54 */   private Map<String, List<HttpCookie>> domainIndex = null;
/*  55 */   private Map<URI, List<HttpCookie>> uriIndex = null;
/*     */ 
/*  58 */   private ReentrantLock lock = null;
/*     */ 
/*     */   public InMemoryCookieStore()
/*     */   {
/*  65 */     this.cookieJar = new ArrayList();
/*  66 */     this.domainIndex = new HashMap();
/*  67 */     this.uriIndex = new HashMap();
/*     */ 
/*  69 */     this.lock = new ReentrantLock(false);
/*     */   }
/*     */ 
/*     */   public void add(URI paramURI, HttpCookie paramHttpCookie)
/*     */   {
/*  77 */     if (paramHttpCookie == null) {
/*  78 */       throw new NullPointerException("cookie is null");
/*     */     }
/*     */ 
/*  82 */     this.lock.lock();
/*     */     try
/*     */     {
/*  85 */       this.cookieJar.remove(paramHttpCookie);
/*     */ 
/*  88 */       if (paramHttpCookie.getMaxAge() != 0L) {
/*  89 */         this.cookieJar.add(paramHttpCookie);
/*     */ 
/*  91 */         if (paramHttpCookie.getDomain() != null) {
/*  92 */           addIndex(this.domainIndex, paramHttpCookie.getDomain(), paramHttpCookie);
/*     */         }
/*     */ 
/*  95 */         addIndex(this.uriIndex, getEffectiveURI(paramURI), paramHttpCookie);
/*     */       }
/*     */     } finally {
/*  98 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<HttpCookie> get(URI paramURI)
/*     */   {
/* 112 */     if (paramURI == null) {
/* 113 */       throw new NullPointerException("uri is null");
/*     */     }
/*     */ 
/* 116 */     ArrayList localArrayList = new ArrayList();
/* 117 */     boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
/* 118 */     this.lock.lock();
/*     */     try
/*     */     {
/* 121 */       getInternal1(localArrayList, this.domainIndex, paramURI.getHost(), bool);
/*     */ 
/* 123 */       getInternal2(localArrayList, this.uriIndex, getEffectiveURI(paramURI), bool);
/*     */     } finally {
/* 125 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 128 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public List<HttpCookie> getCookies()
/*     */   {
/* 137 */     this.lock.lock();
/*     */     List localList;
/*     */     try
/*     */     {
/* 139 */       Iterator localIterator = this.cookieJar.iterator();
/* 140 */       while (localIterator.hasNext())
/* 141 */         if (((HttpCookie)localIterator.next()).hasExpired())
/* 142 */           localIterator.remove();
/*     */     }
/*     */     finally
/*     */     {
/* 146 */       localList = Collections.unmodifiableList(this.cookieJar);
/* 147 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 150 */     return localList;
/*     */   }
/*     */ 
/*     */   public List<URI> getURIs()
/*     */   {
/* 158 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 160 */     this.lock.lock();
/*     */     try {
/* 162 */       Iterator localIterator = this.uriIndex.keySet().iterator();
/* 163 */       while (localIterator.hasNext()) {
/* 164 */         URI localURI = (URI)localIterator.next();
/* 165 */         List localList = (List)this.uriIndex.get(localURI);
/* 166 */         if ((localList == null) || (localList.size() == 0))
/*     */         {
/* 169 */           localIterator.remove();
/*     */         }
/*     */       }
/*     */     } finally {
/* 173 */       localArrayList.addAll(this.uriIndex.keySet());
/* 174 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 177 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public boolean remove(URI paramURI, HttpCookie paramHttpCookie)
/*     */   {
/* 186 */     if (paramHttpCookie == null) {
/* 187 */       throw new NullPointerException("cookie is null");
/*     */     }
/*     */ 
/* 190 */     boolean bool = false;
/* 191 */     this.lock.lock();
/*     */     try {
/* 193 */       bool = this.cookieJar.remove(paramHttpCookie);
/*     */     } finally {
/* 195 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 198 */     return bool;
/*     */   }
/*     */ 
/*     */   public boolean removeAll()
/*     */   {
/* 206 */     this.lock.lock();
/*     */     try {
/* 208 */       this.cookieJar.clear();
/* 209 */       this.domainIndex.clear();
/* 210 */       this.uriIndex.clear();
/*     */     } finally {
/* 212 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 215 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean netscapeDomainMatches(String paramString1, String paramString2)
/*     */   {
/* 235 */     if ((paramString1 == null) || (paramString2 == null)) {
/* 236 */       return false;
/*     */     }
/*     */ 
/* 240 */     boolean bool = ".local".equalsIgnoreCase(paramString1);
/* 241 */     int i = paramString1.indexOf('.');
/* 242 */     if (i == 0) {
/* 243 */       i = paramString1.indexOf('.', 1);
/*     */     }
/* 245 */     if ((!bool) && ((i == -1) || (i == paramString1.length() - 1))) {
/* 246 */       return false;
/*     */     }
/*     */ 
/* 250 */     int j = paramString2.indexOf('.');
/* 251 */     if ((j == -1) && (bool)) {
/* 252 */       return true;
/*     */     }
/*     */ 
/* 255 */     int k = paramString1.length();
/* 256 */     int m = paramString2.length() - k;
/* 257 */     if (m == 0)
/*     */     {
/* 259 */       return paramString2.equalsIgnoreCase(paramString1);
/* 260 */     }if (m > 0)
/*     */     {
/* 262 */       String str1 = paramString2.substring(0, m);
/* 263 */       String str2 = paramString2.substring(m);
/*     */ 
/* 265 */       return str2.equalsIgnoreCase(paramString1);
/* 266 */     }if (m == -1)
/*     */     {
/* 268 */       return (paramString1.charAt(0) == '.') && (paramString2.equalsIgnoreCase(paramString1.substring(1)));
/*     */     }
/*     */ 
/* 272 */     return false;
/*     */   }
/*     */ 
/*     */   private void getInternal1(List<HttpCookie> paramList, Map<String, List<HttpCookie>> paramMap, String paramString, boolean paramBoolean)
/*     */   {
/* 279 */     ArrayList localArrayList = new ArrayList();
/* 280 */     for (Map.Entry localEntry : paramMap.entrySet()) {
/* 281 */       String str = (String)localEntry.getKey();
/* 282 */       List localList = (List)localEntry.getValue();
/* 283 */       for (Iterator localIterator2 = localList.iterator(); localIterator2.hasNext(); ) { localHttpCookie = (HttpCookie)localIterator2.next();
/* 284 */         if (((localHttpCookie.getVersion() == 0) && (netscapeDomainMatches(str, paramString))) || ((localHttpCookie.getVersion() == 1) && (HttpCookie.domainMatches(str, paramString))))
/*     */         {
/* 286 */           if (this.cookieJar.indexOf(localHttpCookie) != -1)
/*     */           {
/* 288 */             if (!localHttpCookie.hasExpired())
/*     */             {
/* 291 */               if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie)))
/*     */               {
/* 293 */                 paramList.add(localHttpCookie);
/*     */               }
/*     */             }
/* 296 */             else localArrayList.add(localHttpCookie);
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 301 */             localArrayList.add(localHttpCookie);
/*     */           }
/*     */         }
/*     */       }
/* 306 */       HttpCookie localHttpCookie;
/* 306 */       for (localIterator2 = localArrayList.iterator(); localIterator2.hasNext(); ) { localHttpCookie = (HttpCookie)localIterator2.next();
/* 307 */         localList.remove(localHttpCookie);
/* 308 */         this.cookieJar.remove(localHttpCookie);
/*     */       }
/*     */ 
/* 311 */       localArrayList.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> void getInternal2(List<HttpCookie> paramList, Map<T, List<HttpCookie>> paramMap, Comparable<T> paramComparable, boolean paramBoolean)
/*     */   {
/* 323 */     for (Iterator localIterator1 = paramMap.keySet().iterator(); localIterator1.hasNext(); ) { Object localObject = localIterator1.next();
/* 324 */       if (paramComparable.compareTo(localObject) == 0) {
/* 325 */         List localList = (List)paramMap.get(localObject);
/*     */ 
/* 327 */         if (localList != null) {
/* 328 */           Iterator localIterator2 = localList.iterator();
/* 329 */           while (localIterator2.hasNext()) {
/* 330 */             HttpCookie localHttpCookie = (HttpCookie)localIterator2.next();
/* 331 */             if (this.cookieJar.indexOf(localHttpCookie) != -1)
/*     */             {
/* 333 */               if (!localHttpCookie.hasExpired())
/*     */               {
/* 335 */                 if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie)))
/*     */                 {
/* 337 */                   paramList.add(localHttpCookie);
/*     */                 }
/*     */               } else { localIterator2.remove();
/* 340 */                 this.cookieJar.remove(localHttpCookie);
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 345 */               localIterator2.remove();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> void addIndex(Map<T, List<HttpCookie>> paramMap, T paramT, HttpCookie paramHttpCookie)
/*     */   {
/* 358 */     if (paramT != null) {
/* 359 */       Object localObject = (List)paramMap.get(paramT);
/* 360 */       if (localObject != null)
/*     */       {
/* 362 */         ((List)localObject).remove(paramHttpCookie);
/*     */ 
/* 364 */         ((List)localObject).add(paramHttpCookie);
/*     */       } else {
/* 366 */         localObject = new ArrayList();
/* 367 */         ((List)localObject).add(paramHttpCookie);
/* 368 */         paramMap.put(paramT, localObject);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private URI getEffectiveURI(URI paramURI)
/*     */   {
/* 379 */     URI localURI = null;
/*     */     try {
/* 381 */       localURI = new URI("http", paramURI.getHost(), null, null, null);
/*     */     }
/*     */     catch (URISyntaxException localURISyntaxException)
/*     */     {
/* 388 */       localURI = paramURI;
/*     */     }
/*     */ 
/* 391 */     return localURI;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.net.InMemoryCookieStore
 * JD-Core Version:    0.6.2
 */