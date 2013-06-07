/*     */ package com.sun.corba.se.spi.servicecontext;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.CDRInputStream;
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.util.Utility;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.org.omg.SendingContext.CodeBase;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.OctetSeqHelper;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ import org.omg.CORBA_2_3.portable.OutputStream;
/*     */ 
/*     */ public class ServiceContexts
/*     */ {
/*     */   private static final int JAVAIDL_ALIGN_SERVICE_ID = -1106033203;
/*     */   private ORB orb;
/*     */   private Map scMap;
/*     */   private boolean addAlignmentOnWrite;
/*     */   private CodeBase codeBase;
/*     */   private GIOPVersion giopVersion;
/*     */   private ORBUtilSystemException wrapper;
/*     */ 
/*     */   private static boolean isDebugging(OutputStream paramOutputStream)
/*     */   {
/*  64 */     ORB localORB = (ORB)paramOutputStream.orb();
/*  65 */     if (localORB == null)
/*  66 */       return false;
/*  67 */     return localORB.serviceContextDebugFlag;
/*     */   }
/*     */ 
/*     */   private static boolean isDebugging(InputStream paramInputStream)
/*     */   {
/*  72 */     ORB localORB = (ORB)paramInputStream.orb();
/*  73 */     if (localORB == null)
/*  74 */       return false;
/*  75 */     return localORB.serviceContextDebugFlag;
/*     */   }
/*     */ 
/*     */   private void dprint(String paramString)
/*     */   {
/*  80 */     ORBUtility.dprint(this, paramString);
/*     */   }
/*     */ 
/*     */   public static void writeNullServiceContext(OutputStream paramOutputStream)
/*     */   {
/*  85 */     if (isDebugging(paramOutputStream))
/*  86 */       ORBUtility.dprint("ServiceContexts", "Writing null service context");
/*  87 */     paramOutputStream.write_long(0);
/*     */   }
/*     */ 
/*     */   private void createMapFromInputStream(InputStream paramInputStream)
/*     */   {
/* 102 */     this.orb = ((ORB)paramInputStream.orb());
/* 103 */     if (this.orb.serviceContextDebugFlag) {
/* 104 */       dprint("Constructing ServiceContexts from input stream");
/*     */     }
/* 106 */     int i = paramInputStream.read_long();
/*     */ 
/* 108 */     if (this.orb.serviceContextDebugFlag) {
/* 109 */       dprint("Number of service contexts = " + i);
/*     */     }
/* 111 */     for (int j = 0; j < i; j++) {
/* 112 */       int k = paramInputStream.read_long();
/*     */ 
/* 114 */       if (this.orb.serviceContextDebugFlag) {
/* 115 */         dprint("Reading service context id " + k);
/*     */       }
/* 117 */       byte[] arrayOfByte = OctetSeqHelper.read(paramInputStream);
/*     */ 
/* 119 */       if (this.orb.serviceContextDebugFlag) {
/* 120 */         dprint("Service context" + k + " length: " + arrayOfByte.length);
/*     */       }
/* 122 */       this.scMap.put(new Integer(k), arrayOfByte);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServiceContexts(ORB paramORB)
/*     */   {
/* 128 */     this.orb = paramORB;
/* 129 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/* 132 */     this.addAlignmentOnWrite = false;
/*     */ 
/* 134 */     this.scMap = new HashMap();
/*     */ 
/* 139 */     this.giopVersion = paramORB.getORBData().getGIOPVersion();
/* 140 */     this.codeBase = null;
/*     */   }
/*     */ 
/*     */   public ServiceContexts(InputStream paramInputStream)
/*     */   {
/* 148 */     this((ORB)paramInputStream.orb());
/*     */ 
/* 154 */     this.codeBase = ((CDRInputStream)paramInputStream).getCodeBase();
/*     */ 
/* 156 */     createMapFromInputStream(paramInputStream);
/*     */ 
/* 159 */     this.giopVersion = ((CDRInputStream)paramInputStream).getGIOPVersion();
/*     */   }
/*     */ 
/*     */   private ServiceContext unmarshal(Integer paramInteger, byte[] paramArrayOfByte)
/*     */   {
/* 168 */     ServiceContextRegistry localServiceContextRegistry = this.orb.getServiceContextRegistry();
/*     */ 
/* 170 */     ServiceContextData localServiceContextData = localServiceContextRegistry.findServiceContextData(paramInteger.intValue());
/* 171 */     Object localObject = null;
/*     */ 
/* 173 */     if (localServiceContextData == null) {
/* 174 */       if (this.orb.serviceContextDebugFlag) {
/* 175 */         dprint("Could not find ServiceContextData for " + paramInteger + " using UnknownServiceContext");
/*     */       }
/*     */ 
/* 180 */       localObject = new UnknownServiceContext(paramInteger.intValue(), paramArrayOfByte);
/*     */     }
/*     */     else
/*     */     {
/* 184 */       if (this.orb.serviceContextDebugFlag) {
/* 185 */         dprint("Found " + localServiceContextData);
/*     */       }
/*     */ 
/* 200 */       EncapsInputStream localEncapsInputStream = new EncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length, this.giopVersion, this.codeBase);
/*     */ 
/* 206 */       localEncapsInputStream.consumeEndian();
/*     */ 
/* 213 */       localObject = localServiceContextData.makeServiceContext(localEncapsInputStream, this.giopVersion);
/* 214 */       if (localObject == null) {
/* 215 */         throw this.wrapper.svcctxUnmarshalError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */     }
/*     */ 
/* 219 */     return localObject;
/*     */   }
/*     */ 
/*     */   public void addAlignmentPadding()
/*     */   {
/* 229 */     this.addAlignmentOnWrite = true;
/*     */   }
/*     */ 
/*     */   public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
/*     */   {
/* 246 */     if (isDebugging(paramOutputStream)) {
/* 247 */       dprint("Writing service contexts to output stream");
/* 248 */       Utility.printStackTrace();
/*     */     }
/*     */ 
/* 251 */     int i = this.scMap.size();
/*     */ 
/* 253 */     if (this.addAlignmentOnWrite) {
/* 254 */       if (isDebugging(paramOutputStream)) {
/* 255 */         dprint("Adding alignment padding");
/*     */       }
/* 257 */       i++;
/*     */     }
/*     */ 
/* 260 */     if (isDebugging(paramOutputStream)) {
/* 261 */       dprint("Service context has " + i + " components");
/*     */     }
/* 263 */     paramOutputStream.write_long(i);
/*     */ 
/* 265 */     writeServiceContextsInOrder(paramOutputStream, paramGIOPVersion);
/*     */ 
/* 267 */     if (this.addAlignmentOnWrite) {
/* 268 */       if (isDebugging(paramOutputStream)) {
/* 269 */         dprint("Writing alignment padding");
/*     */       }
/* 271 */       paramOutputStream.write_long(-1106033203);
/* 272 */       paramOutputStream.write_long(4);
/* 273 */       paramOutputStream.write_octet((byte)0);
/* 274 */       paramOutputStream.write_octet((byte)0);
/* 275 */       paramOutputStream.write_octet((byte)0);
/* 276 */       paramOutputStream.write_octet((byte)0);
/*     */     }
/*     */ 
/* 279 */     if (isDebugging(paramOutputStream))
/* 280 */       dprint("Service context writing complete");
/*     */   }
/*     */ 
/*     */   private void writeServiceContextsInOrder(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion)
/*     */   {
/* 291 */     Integer localInteger1 = new Integer(9);
/*     */ 
/* 294 */     Object localObject = this.scMap.remove(localInteger1);
/*     */ 
/* 296 */     Iterator localIterator = this.scMap.keySet().iterator();
/*     */ 
/* 298 */     while (localIterator.hasNext()) {
/* 299 */       Integer localInteger2 = (Integer)localIterator.next();
/*     */ 
/* 301 */       writeMapEntry(paramOutputStream, localInteger2, this.scMap.get(localInteger2), paramGIOPVersion);
/*     */     }
/*     */ 
/* 307 */     if (localObject != null) {
/* 308 */       writeMapEntry(paramOutputStream, localInteger1, localObject, paramGIOPVersion);
/*     */ 
/* 310 */       this.scMap.put(localInteger1, localObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeMapEntry(OutputStream paramOutputStream, Integer paramInteger, Object paramObject, GIOPVersion paramGIOPVersion)
/*     */   {
/* 325 */     if ((paramObject instanceof byte[])) {
/* 326 */       if (isDebugging(paramOutputStream)) {
/* 327 */         dprint("Writing service context bytes for id " + paramInteger);
/*     */       }
/* 329 */       OctetSeqHelper.write(paramOutputStream, (byte[])paramObject);
/*     */     }
/*     */     else
/*     */     {
/* 335 */       ServiceContext localServiceContext = (ServiceContext)paramObject;
/*     */ 
/* 337 */       if (isDebugging(paramOutputStream)) {
/* 338 */         dprint("Writing service context " + localServiceContext);
/*     */       }
/* 340 */       localServiceContext.write(paramOutputStream, paramGIOPVersion);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(ServiceContext paramServiceContext)
/*     */   {
/* 349 */     Integer localInteger = new Integer(paramServiceContext.getId());
/* 350 */     this.scMap.put(localInteger, paramServiceContext);
/*     */   }
/*     */ 
/*     */   public void delete(int paramInt) {
/* 354 */     delete(new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public void delete(Integer paramInteger)
/*     */   {
/* 359 */     this.scMap.remove(paramInteger);
/*     */   }
/*     */ 
/*     */   public ServiceContext get(int paramInt) {
/* 363 */     return get(new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   public ServiceContext get(Integer paramInteger)
/*     */   {
/* 368 */     Object localObject = this.scMap.get(paramInteger);
/* 369 */     if (localObject == null) {
/* 370 */       return null;
/*     */     }
/*     */ 
/* 373 */     if ((localObject instanceof byte[]))
/*     */     {
/* 375 */       ServiceContext localServiceContext = unmarshal(paramInteger, (byte[])localObject);
/*     */ 
/* 377 */       this.scMap.put(paramInteger, localServiceContext);
/*     */ 
/* 379 */       return localServiceContext;
/*     */     }
/* 381 */     return (ServiceContext)localObject;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.spi.servicecontext.ServiceContexts
 * JD-Core Version:    0.6.2
 */