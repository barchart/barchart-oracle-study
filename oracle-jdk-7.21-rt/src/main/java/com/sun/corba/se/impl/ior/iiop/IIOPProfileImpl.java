/*     */ package com.sun.corba.se.impl.ior.iiop;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsInputStream;
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.impl.ior.EncapsulationUtility;
/*     */ import com.sun.corba.se.impl.logging.IORSystemException;
/*     */ import com.sun.corba.se.impl.util.JDKBridge;
/*     */ import com.sun.corba.se.spi.ior.IORFactories;
/*     */ import com.sun.corba.se.spi.ior.IdentifiableBase;
/*     */ import com.sun.corba.se.spi.ior.ObjectAdapterId;
/*     */ import com.sun.corba.se.spi.ior.ObjectId;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
/*     */ import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
/*     */ import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
/*     */ import com.sun.corba.se.spi.oa.ObjectAdapter;
/*     */ import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBVersion;
/*     */ import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
/*     */ import java.util.Iterator;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ import org.omg.CORBA_2_3.portable.OutputStream;
/*     */ import org.omg.IOP.TaggedProfileHelper;
/*     */ 
/*     */ public class IIOPProfileImpl extends IdentifiableBase
/*     */   implements IIOPProfile
/*     */ {
/*     */   private ORB orb;
/*     */   private IORSystemException wrapper;
/*     */   private ObjectId oid;
/*     */   private IIOPProfileTemplate proftemp;
/*     */   private ObjectKeyTemplate oktemp;
/*  89 */   protected String codebase = null;
/*  90 */   protected boolean cachedCodebase = false;
/*     */ 
/*  92 */   private boolean checkedIsLocal = false;
/*  93 */   private boolean cachedIsLocal = false;
/*     */ 
/* 109 */   private GIOPVersion giopVersion = null;
/*     */ 
/*     */   public boolean equals(java.lang.Object paramObject)
/*     */   {
/* 113 */     if (!(paramObject instanceof IIOPProfileImpl)) {
/* 114 */       return false;
/*     */     }
/* 116 */     IIOPProfileImpl localIIOPProfileImpl = (IIOPProfileImpl)paramObject;
/*     */ 
/* 118 */     return (this.oid.equals(localIIOPProfileImpl.oid)) && (this.proftemp.equals(localIIOPProfileImpl.proftemp)) && (this.oktemp.equals(localIIOPProfileImpl.oktemp));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 124 */     return this.oid.hashCode() ^ this.proftemp.hashCode() ^ this.oktemp.hashCode();
/*     */   }
/*     */ 
/*     */   public ObjectId getObjectId()
/*     */   {
/* 129 */     return this.oid;
/*     */   }
/*     */ 
/*     */   public TaggedProfileTemplate getTaggedProfileTemplate()
/*     */   {
/* 134 */     return this.proftemp;
/*     */   }
/*     */ 
/*     */   public ObjectKeyTemplate getObjectKeyTemplate()
/*     */   {
/* 139 */     return this.oktemp;
/*     */   }
/*     */ 
/*     */   private IIOPProfileImpl(ORB paramORB)
/*     */   {
/* 144 */     this.orb = paramORB;
/* 145 */     this.wrapper = IORSystemException.get(paramORB, "oa.ior");
/*     */   }
/*     */ 
/*     */   public IIOPProfileImpl(ORB paramORB, ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, IIOPProfileTemplate paramIIOPProfileTemplate)
/*     */   {
/* 152 */     this(paramORB);
/* 153 */     this.oktemp = paramObjectKeyTemplate;
/* 154 */     this.oid = paramObjectId;
/* 155 */     this.proftemp = paramIIOPProfileTemplate;
/*     */   }
/*     */ 
/*     */   public IIOPProfileImpl(InputStream paramInputStream)
/*     */   {
/* 160 */     this((ORB)paramInputStream.orb());
/* 161 */     init(paramInputStream);
/*     */   }
/*     */ 
/*     */   public IIOPProfileImpl(ORB paramORB, org.omg.IOP.TaggedProfile paramTaggedProfile)
/*     */   {
/* 166 */     this(paramORB);
/*     */ 
/* 168 */     if ((paramTaggedProfile == null) || (paramTaggedProfile.tag != 0) || (paramTaggedProfile.profile_data == null))
/*     */     {
/* 170 */       throw this.wrapper.invalidTaggedProfile();
/*     */     }
/*     */ 
/* 173 */     EncapsInputStream localEncapsInputStream = new EncapsInputStream(paramORB, paramTaggedProfile.profile_data, paramTaggedProfile.profile_data.length);
/*     */ 
/* 175 */     localEncapsInputStream.consumeEndian();
/* 176 */     init(localEncapsInputStream);
/*     */   }
/*     */ 
/*     */   private void init(InputStream paramInputStream)
/*     */   {
/* 182 */     GIOPVersion localGIOPVersion = new GIOPVersion();
/* 183 */     localGIOPVersion.read(paramInputStream);
/* 184 */     IIOPAddressImpl localIIOPAddressImpl = new IIOPAddressImpl(paramInputStream);
/* 185 */     byte[] arrayOfByte = EncapsulationUtility.readOctets(paramInputStream);
/*     */ 
/* 187 */     ObjectKey localObjectKey = this.orb.getObjectKeyFactory().create(arrayOfByte);
/* 188 */     this.oktemp = localObjectKey.getTemplate();
/* 189 */     this.oid = localObjectKey.getId();
/*     */ 
/* 191 */     this.proftemp = IIOPFactories.makeIIOPProfileTemplate(this.orb, localGIOPVersion, localIIOPAddressImpl);
/*     */ 
/* 195 */     if (localGIOPVersion.getMinor() > 0) {
/* 196 */       EncapsulationUtility.readIdentifiableSequence(this.proftemp, this.orb.getTaggedComponentFactoryFinder(), paramInputStream);
/*     */     }
/*     */ 
/* 204 */     if (uncachedGetCodeBase() == null) {
/* 205 */       JavaCodebaseComponent localJavaCodebaseComponent = LocalCodeBaseSingletonHolder.comp;
/*     */ 
/* 207 */       if (localJavaCodebaseComponent != null) {
/* 208 */         if (localGIOPVersion.getMinor() > 0) {
/* 209 */           this.proftemp.add(localJavaCodebaseComponent);
/*     */         }
/* 211 */         this.codebase = localJavaCodebaseComponent.getURLs();
/*     */       }
/*     */ 
/* 216 */       this.cachedCodebase = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeContents(OutputStream paramOutputStream)
/*     */   {
/* 222 */     this.proftemp.write(this.oktemp, this.oid, paramOutputStream);
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/* 227 */     return this.proftemp.getId();
/*     */   }
/*     */ 
/*     */   public boolean isEquivalent(com.sun.corba.se.spi.ior.TaggedProfile paramTaggedProfile)
/*     */   {
/* 232 */     if (!(paramTaggedProfile instanceof IIOPProfile)) {
/* 233 */       return false;
/*     */     }
/* 235 */     IIOPProfile localIIOPProfile = (IIOPProfile)paramTaggedProfile;
/*     */ 
/* 237 */     return (this.oid.equals(localIIOPProfile.getObjectId())) && (this.proftemp.isEquivalent(localIIOPProfile.getTaggedProfileTemplate())) && (this.oktemp.equals(localIIOPProfile.getObjectKeyTemplate()));
/*     */   }
/*     */ 
/*     */   public ObjectKey getObjectKey()
/*     */   {
/* 244 */     ObjectKey localObjectKey = IORFactories.makeObjectKey(this.oktemp, this.oid);
/* 245 */     return localObjectKey;
/*     */   }
/*     */ 
/*     */   public org.omg.IOP.TaggedProfile getIOPProfile()
/*     */   {
/* 250 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream(this.orb);
/* 251 */     localEncapsOutputStream.write_long(getId());
/* 252 */     write(localEncapsOutputStream);
/* 253 */     InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
/* 254 */     return TaggedProfileHelper.read(localInputStream);
/*     */   }
/*     */ 
/*     */   private String uncachedGetCodeBase() {
/* 258 */     Iterator localIterator = this.proftemp.iteratorById(25);
/*     */ 
/* 260 */     if (localIterator.hasNext()) {
/* 261 */       JavaCodebaseComponent localJavaCodebaseComponent = (JavaCodebaseComponent)localIterator.next();
/* 262 */       return localJavaCodebaseComponent.getURLs();
/*     */     }
/*     */ 
/* 265 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized String getCodebase() {
/* 269 */     if (!this.cachedCodebase) {
/* 270 */       this.cachedCodebase = true;
/* 271 */       this.codebase = uncachedGetCodeBase();
/*     */     }
/*     */ 
/* 274 */     return this.codebase;
/*     */   }
/*     */ 
/*     */   public ORBVersion getORBVersion()
/*     */   {
/* 281 */     return this.oktemp.getORBVersion();
/*     */   }
/*     */ 
/*     */   public synchronized boolean isLocal()
/*     */   {
/* 286 */     if (!this.checkedIsLocal) {
/* 287 */       this.checkedIsLocal = true;
/* 288 */       String str = this.proftemp.getPrimaryAddress().getHost();
/*     */ 
/* 290 */       this.cachedIsLocal = ((this.orb.isLocalHost(str)) && (this.orb.isLocalServerId(this.oktemp.getSubcontractId(), this.oktemp.getServerId())) && (this.orb.getLegacyServerSocketManager().legacyIsLocalServerPort(this.proftemp.getPrimaryAddress().getPort())));
/*     */     }
/*     */ 
/* 298 */     return this.cachedIsLocal;
/*     */   }
/*     */ 
/*     */   public java.lang.Object getServant()
/*     */   {
/* 309 */     if (!isLocal()) {
/* 310 */       return null;
/*     */     }
/* 312 */     RequestDispatcherRegistry localRequestDispatcherRegistry = this.orb.getRequestDispatcherRegistry();
/* 313 */     ObjectAdapterFactory localObjectAdapterFactory = localRequestDispatcherRegistry.getObjectAdapterFactory(this.oktemp.getSubcontractId());
/*     */ 
/* 316 */     ObjectAdapterId localObjectAdapterId = this.oktemp.getObjectAdapterId();
/* 317 */     ObjectAdapter localObjectAdapter = null;
/*     */     try
/*     */     {
/* 320 */       localObjectAdapter = localObjectAdapterFactory.find(localObjectAdapterId);
/*     */     }
/*     */     catch (SystemException localSystemException)
/*     */     {
/* 325 */       this.wrapper.getLocalServantFailure(localSystemException, localObjectAdapterId.toString());
/* 326 */       return null;
/*     */     }
/*     */ 
/* 329 */     byte[] arrayOfByte = this.oid.getId();
/* 330 */     org.omg.CORBA.Object localObject = localObjectAdapter.getLocalServant(arrayOfByte);
/* 331 */     return localObject;
/*     */   }
/*     */ 
/*     */   public synchronized GIOPVersion getGIOPVersion()
/*     */   {
/* 341 */     return this.proftemp.getGIOPVersion();
/*     */   }
/*     */ 
/*     */   public void makeImmutable()
/*     */   {
/* 346 */     this.proftemp.makeImmutable();
/*     */   }
/*     */ 
/*     */   private static class LocalCodeBaseSingletonHolder
/*     */   {
/*     */     public static JavaCodebaseComponent comp;
/*     */ 
/*     */     static
/*     */     {
/* 100 */       String str = JDKBridge.getLocalCodebase();
/* 101 */       if (str == null)
/* 102 */         comp = null;
/*     */       else
/* 104 */         comp = IIOPFactories.makeJavaCodebaseComponent(str);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.ior.iiop.IIOPProfileImpl
 * JD-Core Version:    0.6.2
 */