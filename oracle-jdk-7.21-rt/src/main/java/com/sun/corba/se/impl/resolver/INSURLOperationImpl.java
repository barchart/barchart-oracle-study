/*     */ package com.sun.corba.se.impl.resolver;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.logging.OMGSystemException;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
/*     */ import com.sun.corba.se.impl.naming.namingutil.CorbanameURL;
/*     */ import com.sun.corba.se.impl.naming.namingutil.IIOPEndpointInfo;
/*     */ import com.sun.corba.se.impl.naming.namingutil.INSURL;
/*     */ import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.IORFactories;
/*     */ import com.sun.corba.se.spi.ior.IORTemplate;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.orb.Operation;
/*     */ import com.sun.corba.se.spi.resolver.LocalResolver;
/*     */ import com.sun.corba.se.spi.resolver.Resolver;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.omg.CosNaming.NamingContextExt;
/*     */ import org.omg.CosNaming.NamingContextExtHelper;
/*     */ 
/*     */ public class INSURLOperationImpl
/*     */   implements Operation
/*     */ {
/*     */   ORB orb;
/*     */   ORBUtilSystemException wrapper;
/*     */   OMGSystemException omgWrapper;
/*     */   Resolver bootstrapResolver;
/*     */   private NamingContextExt rootNamingContextExt;
/*  85 */   private java.lang.Object rootContextCacheLock = new java.lang.Object();
/*     */ 
/*  88 */   private INSURLHandler insURLHandler = INSURLHandler.getINSURLHandler();
/*     */   private static final int NIBBLES_PER_BYTE = 2;
/*     */   private static final int UN_SHIFT = 4;
/*     */ 
/*     */   public INSURLOperationImpl(ORB paramORB, Resolver paramResolver)
/*     */   {
/*  92 */     this.orb = paramORB;
/*  93 */     this.wrapper = ORBUtilSystemException.get(paramORB, "orb.resolver");
/*     */ 
/*  95 */     this.omgWrapper = OMGSystemException.get(paramORB, "orb.resolver");
/*     */ 
/*  97 */     this.bootstrapResolver = paramResolver;
/*     */   }
/*     */ 
/*     */   private org.omg.CORBA.Object getIORFromString(String paramString)
/*     */   {
/* 109 */     if ((paramString.length() & 0x1) == 1) {
/* 110 */       throw this.wrapper.badStringifiedIorLen();
/*     */     }
/* 112 */     byte[] arrayOfByte = new byte[(paramString.length() - "IOR:".length()) / 2];
/* 113 */     int i = "IOR:".length(); for (int j = 0; i < paramString.length(); j++) {
/* 114 */       arrayOfByte[j] = ((byte)(ORBUtility.hexOf(paramString.charAt(i)) << 4 & 0xF0));
/*     */       int tmp72_70 = j;
/*     */       byte[] tmp72_69 = arrayOfByte; tmp72_69[tmp72_70] = ((byte)(tmp72_69[tmp72_70] | (byte)(ORBUtility.hexOf(paramString.charAt(i + 1)) & 0xF)));
/*     */ 
/* 113 */       i += 2;
/*     */     }
/*     */ 
/* 117 */     EncapsInputStream localEncapsInputStream = new EncapsInputStream(this.orb, arrayOfByte, arrayOfByte.length, this.orb.getORBData().getGIOPVersion());
/*     */ 
/* 119 */     localEncapsInputStream.consumeEndian();
/* 120 */     return localEncapsInputStream.read_Object();
/*     */   }
/*     */ 
/*     */   public java.lang.Object operate(java.lang.Object paramObject)
/*     */   {
/* 125 */     if ((paramObject instanceof String)) {
/* 126 */       String str = (String)paramObject;
/*     */ 
/* 128 */       if (str.startsWith("IOR:"))
/*     */       {
/* 130 */         return getIORFromString(str);
/*     */       }
/* 132 */       INSURL localINSURL = this.insURLHandler.parseURL(str);
/* 133 */       if (localINSURL == null)
/* 134 */         throw this.omgWrapper.soBadSchemeName();
/* 135 */       return resolveINSURL(localINSURL);
/*     */     }
/*     */ 
/* 139 */     throw this.wrapper.stringExpected();
/*     */   }
/*     */ 
/*     */   private org.omg.CORBA.Object resolveINSURL(INSURL paramINSURL)
/*     */   {
/* 144 */     if (paramINSURL.isCorbanameURL()) {
/* 145 */       return resolveCorbaname((CorbanameURL)paramINSURL);
/*     */     }
/* 147 */     return resolveCorbaloc((CorbalocURL)paramINSURL);
/*     */   }
/*     */ 
/*     */   private org.omg.CORBA.Object resolveCorbaloc(CorbalocURL paramCorbalocURL)
/*     */   {
/* 159 */     org.omg.CORBA.Object localObject = null;
/*     */ 
/* 161 */     if (paramCorbalocURL.getRIRFlag())
/* 162 */       localObject = this.bootstrapResolver.resolve(paramCorbalocURL.getKeyString());
/*     */     else {
/* 164 */       localObject = getIORUsingCorbaloc(paramCorbalocURL);
/*     */     }
/*     */ 
/* 167 */     return localObject;
/*     */   }
/*     */ 
/*     */   private org.omg.CORBA.Object resolveCorbaname(CorbanameURL paramCorbanameURL)
/*     */   {
/* 176 */     java.lang.Object localObject1 = null;
/*     */     try
/*     */     {
/* 179 */       NamingContextExt localNamingContextExt = null;
/*     */ 
/* 181 */       if (paramCorbanameURL.getRIRFlag())
/*     */       {
/* 183 */         localNamingContextExt = getDefaultRootNamingContext();
/*     */       }
/*     */       else {
/* 186 */         localObject2 = getIORUsingCorbaloc(paramCorbanameURL);
/*     */ 
/* 188 */         if (localObject2 == null) {
/* 189 */           return null;
/*     */         }
/*     */ 
/* 192 */         localNamingContextExt = NamingContextExtHelper.narrow((org.omg.CORBA.Object)localObject2);
/*     */       }
/*     */ 
/* 196 */       java.lang.Object localObject2 = paramCorbanameURL.getStringifiedName();
/*     */ 
/* 198 */       if (localObject2 == null)
/*     */       {
/* 200 */         return localNamingContextExt;
/*     */       }
/* 202 */       return localNamingContextExt.resolve_str((String)localObject2);
/*     */     }
/*     */     catch (Exception localException) {
/* 205 */       clearRootNamingContextCache();
/* 206 */     }return null;
/*     */   }
/*     */ 
/*     */   private org.omg.CORBA.Object getIORUsingCorbaloc(INSURL paramINSURL)
/*     */   {
/* 217 */     HashMap localHashMap = new HashMap();
/* 218 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 222 */     List localList = paramINSURL.getEndpointInfo();
/* 223 */     String str = paramINSURL.getKeyString();
/*     */ 
/* 225 */     if (str == null) {
/* 226 */       return null;
/*     */     }
/*     */ 
/* 229 */     ObjectKey localObjectKey = this.orb.getObjectKeyFactory().create(str.getBytes());
/*     */ 
/* 231 */     IORTemplate localIORTemplate = IORFactories.makeIORTemplate(localObjectKey.getTemplate());
/* 232 */     Iterator localIterator = localList.iterator();
/* 233 */     while (localIterator.hasNext()) {
/* 234 */       localObject1 = (IIOPEndpointInfo)localIterator.next();
/*     */ 
/* 236 */       localObject2 = IIOPFactories.makeIIOPAddress(this.orb, ((IIOPEndpointInfo)localObject1).getHost(), ((IIOPEndpointInfo)localObject1).getPort());
/*     */ 
/* 238 */       localObject3 = GIOPVersion.getInstance((byte)((IIOPEndpointInfo)localObject1).getMajor(), (byte)((IIOPEndpointInfo)localObject1).getMinor());
/*     */ 
/* 240 */       localObject4 = null;
/* 241 */       if (((GIOPVersion)localObject3).equals(GIOPVersion.V1_0)) {
/* 242 */         localObject4 = IIOPFactories.makeIIOPProfileTemplate(this.orb, (GIOPVersion)localObject3, (IIOPAddress)localObject2);
/*     */ 
/* 244 */         localArrayList.add(localObject4);
/*     */       }
/* 246 */       else if (localHashMap.get(localObject3) == null) {
/* 247 */         localObject4 = IIOPFactories.makeIIOPProfileTemplate(this.orb, (GIOPVersion)localObject3, (IIOPAddress)localObject2);
/*     */ 
/* 249 */         localHashMap.put(localObject3, localObject4);
/*     */       } else {
/* 251 */         localObject4 = (IIOPProfileTemplate)localHashMap.get(localObject3);
/* 252 */         localObject5 = IIOPFactories.makeAlternateIIOPAddressComponent((IIOPAddress)localObject2);
/*     */ 
/* 254 */         ((IIOPProfileTemplate)localObject4).add(localObject5);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 259 */     java.lang.Object localObject1 = this.orb.getORBData().getGIOPVersion();
/* 260 */     java.lang.Object localObject2 = (IIOPProfileTemplate)localHashMap.get(localObject1);
/* 261 */     if (localObject2 != null) {
/* 262 */       localIORTemplate.add(localObject2);
/* 263 */       localHashMap.remove(localObject1);
/*     */     }
/*     */ 
/* 267 */     java.lang.Object localObject3 = new Comparator() {
/*     */       public int compare(java.lang.Object paramAnonymousObject1, java.lang.Object paramAnonymousObject2) {
/* 269 */         GIOPVersion localGIOPVersion1 = (GIOPVersion)paramAnonymousObject1;
/* 270 */         GIOPVersion localGIOPVersion2 = (GIOPVersion)paramAnonymousObject2;
/* 271 */         return localGIOPVersion1.equals(localGIOPVersion2) ? 0 : localGIOPVersion1.lessThan(localGIOPVersion2) ? 1 : -1;
/*     */       }
/*     */     };
/* 276 */     java.lang.Object localObject4 = new ArrayList(localHashMap.keySet());
/* 277 */     Collections.sort((List)localObject4, (Comparator)localObject3);
/*     */ 
/* 280 */     java.lang.Object localObject5 = ((List)localObject4).iterator();
/* 281 */     while (((Iterator)localObject5).hasNext()) {
/* 282 */       localObject6 = (IIOPProfileTemplate)localHashMap.get(((Iterator)localObject5).next());
/* 283 */       localIORTemplate.add(localObject6);
/*     */     }
/*     */ 
/* 287 */     localIORTemplate.addAll(localArrayList);
/*     */ 
/* 289 */     java.lang.Object localObject6 = localIORTemplate.makeIOR(this.orb, "", localObjectKey.getId());
/* 290 */     return ORBUtility.makeObjectReference((IOR)localObject6);
/*     */   }
/*     */ 
/*     */   private NamingContextExt getDefaultRootNamingContext()
/*     */   {
/* 304 */     synchronized (this.rootContextCacheLock) {
/* 305 */       if (this.rootNamingContextExt == null) {
/*     */         try {
/* 307 */           this.rootNamingContextExt = NamingContextExtHelper.narrow(this.orb.getLocalResolver().resolve("NameService"));
/*     */         }
/*     */         catch (Exception localException)
/*     */         {
/* 311 */           this.rootNamingContextExt = null;
/*     */         }
/*     */       }
/*     */     }
/* 315 */     return this.rootNamingContextExt;
/*     */   }
/*     */ 
/*     */   private void clearRootNamingContextCache()
/*     */   {
/* 323 */     synchronized (this.rootContextCacheLock) {
/* 324 */       this.rootNamingContextExt = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.resolver.INSURLOperationImpl
 * JD-Core Version:    0.6.2
 */