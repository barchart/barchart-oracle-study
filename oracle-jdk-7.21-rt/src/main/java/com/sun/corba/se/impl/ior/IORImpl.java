/*     */ package com.sun.corba.se.impl.ior;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.EncapsOutputStream;
/*     */ import com.sun.corba.se.impl.encoding.MarshalOutputStream;
/*     */ import com.sun.corba.se.impl.logging.IORSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.HexOutputStream;
/*     */ import com.sun.corba.se.spi.ior.IORFactories;
/*     */ import com.sun.corba.se.spi.ior.IORTemplate;
/*     */ import com.sun.corba.se.spi.ior.IORTemplateList;
/*     */ import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
/*     */ import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
/*     */ import com.sun.corba.se.spi.ior.ObjectId;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
/*     */ import com.sun.corba.se.spi.ior.TaggedProfile;
/*     */ import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.omg.CORBA_2_3.portable.InputStream;
/*     */ import org.omg.CORBA_2_3.portable.OutputStream;
/*     */ import org.omg.IOP.IORHelper;
/*     */ 
/*     */ public class IORImpl extends IdentifiableContainerBase
/*     */   implements com.sun.corba.se.spi.ior.IOR
/*     */ {
/*     */   private String typeId;
/*  84 */   private ORB factory = null;
/*  85 */   private boolean isCachedHashValue = false;
/*     */   private int cachedHashValue;
/*     */   IORSystemException wrapper;
/* 102 */   private IORTemplateList iortemps = null;
/*     */ 
/*     */   public ORB getORB()
/*     */   {
/*  91 */     return this.factory;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 106 */     if (paramObject == null) {
/* 107 */       return false;
/*     */     }
/* 109 */     if (!(paramObject instanceof com.sun.corba.se.spi.ior.IOR)) {
/* 110 */       return false;
/*     */     }
/* 112 */     com.sun.corba.se.spi.ior.IOR localIOR = (com.sun.corba.se.spi.ior.IOR)paramObject;
/*     */ 
/* 114 */     return (super.equals(paramObject)) && (this.typeId.equals(localIOR.getTypeId()));
/*     */   }
/*     */ 
/*     */   public synchronized int hashCode()
/*     */   {
/* 119 */     if (!this.isCachedHashValue) {
/* 120 */       this.cachedHashValue = (super.hashCode() ^ this.typeId.hashCode());
/* 121 */       this.isCachedHashValue = true;
/*     */     }
/* 123 */     return this.cachedHashValue;
/*     */   }
/*     */ 
/*     */   public IORImpl(ORB paramORB)
/*     */   {
/* 130 */     this(paramORB, "");
/*     */   }
/*     */ 
/*     */   public IORImpl(ORB paramORB, String paramString)
/*     */   {
/* 135 */     this.factory = paramORB;
/* 136 */     this.wrapper = IORSystemException.get(paramORB, "oa.ior");
/*     */ 
/* 138 */     this.typeId = paramString;
/*     */   }
/*     */ 
/*     */   public IORImpl(ORB paramORB, String paramString, IORTemplate paramIORTemplate, ObjectId paramObjectId)
/*     */   {
/* 146 */     this(paramORB, paramString);
/*     */ 
/* 148 */     this.iortemps = IORFactories.makeIORTemplateList();
/* 149 */     this.iortemps.add(paramIORTemplate);
/*     */ 
/* 151 */     addTaggedProfiles(paramIORTemplate, paramObjectId);
/*     */ 
/* 153 */     makeImmutable();
/*     */   }
/*     */ 
/*     */   private void addTaggedProfiles(IORTemplate paramIORTemplate, ObjectId paramObjectId)
/*     */   {
/* 158 */     ObjectKeyTemplate localObjectKeyTemplate = paramIORTemplate.getObjectKeyTemplate();
/* 159 */     Iterator localIterator = paramIORTemplate.iterator();
/*     */ 
/* 161 */     while (localIterator.hasNext()) {
/* 162 */       TaggedProfileTemplate localTaggedProfileTemplate = (TaggedProfileTemplate)localIterator.next();
/*     */ 
/* 165 */       TaggedProfile localTaggedProfile = localTaggedProfileTemplate.create(localObjectKeyTemplate, paramObjectId);
/*     */ 
/* 167 */       add(localTaggedProfile);
/*     */     }
/*     */   }
/*     */ 
/*     */   public IORImpl(ORB paramORB, String paramString, IORTemplateList paramIORTemplateList, ObjectId paramObjectId)
/*     */   {
/* 176 */     this(paramORB, paramString);
/*     */ 
/* 178 */     this.iortemps = paramIORTemplateList;
/*     */ 
/* 180 */     Iterator localIterator = paramIORTemplateList.iterator();
/* 181 */     while (localIterator.hasNext()) {
/* 182 */       IORTemplate localIORTemplate = (IORTemplate)localIterator.next();
/* 183 */       addTaggedProfiles(localIORTemplate, paramObjectId);
/*     */     }
/*     */ 
/* 186 */     makeImmutable();
/*     */   }
/*     */ 
/*     */   public IORImpl(InputStream paramInputStream)
/*     */   {
/* 191 */     this((ORB)paramInputStream.orb(), paramInputStream.read_string());
/*     */ 
/* 193 */     IdentifiableFactoryFinder localIdentifiableFactoryFinder = this.factory.getTaggedProfileFactoryFinder();
/*     */ 
/* 196 */     EncapsulationUtility.readIdentifiableSequence(this, localIdentifiableFactoryFinder, paramInputStream);
/*     */ 
/* 198 */     makeImmutable();
/*     */   }
/*     */ 
/*     */   public String getTypeId()
/*     */   {
/* 203 */     return this.typeId;
/*     */   }
/*     */ 
/*     */   public void write(OutputStream paramOutputStream)
/*     */   {
/* 208 */     paramOutputStream.write_string(this.typeId);
/* 209 */     EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
/*     */   }
/*     */ 
/*     */   public String stringify()
/*     */   {
/* 216 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream(this.factory);
/* 217 */     localEncapsOutputStream.putEndian();
/* 218 */     write((OutputStream)localEncapsOutputStream);
/* 219 */     StringWriter localStringWriter = new StringWriter();
/*     */     try {
/* 221 */       localEncapsOutputStream.writeTo(new HexOutputStream(localStringWriter));
/*     */     } catch (IOException localIOException) {
/* 223 */       throw this.wrapper.stringifyWriteError(localIOException);
/*     */     }
/*     */ 
/* 226 */     return "IOR:" + localStringWriter;
/*     */   }
/*     */ 
/*     */   public synchronized void makeImmutable()
/*     */   {
/* 231 */     makeElementsImmutable();
/*     */ 
/* 233 */     if (this.iortemps != null) {
/* 234 */       this.iortemps.makeImmutable();
/*     */     }
/* 236 */     super.makeImmutable();
/*     */   }
/*     */ 
/*     */   public org.omg.IOP.IOR getIOPIOR() {
/* 240 */     EncapsOutputStream localEncapsOutputStream = new EncapsOutputStream(this.factory);
/* 241 */     write(localEncapsOutputStream);
/* 242 */     InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
/* 243 */     return IORHelper.read(localInputStream);
/*     */   }
/*     */ 
/*     */   public boolean isNil()
/*     */   {
/* 253 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public boolean isEquivalent(com.sun.corba.se.spi.ior.IOR paramIOR)
/*     */   {
/* 258 */     Iterator localIterator1 = iterator();
/* 259 */     Iterator localIterator2 = paramIOR.iterator();
/* 260 */     while ((localIterator1.hasNext()) && (localIterator2.hasNext())) {
/* 261 */       TaggedProfile localTaggedProfile1 = (TaggedProfile)localIterator1.next();
/* 262 */       TaggedProfile localTaggedProfile2 = (TaggedProfile)localIterator2.next();
/* 263 */       if (!localTaggedProfile1.isEquivalent(localTaggedProfile2)) {
/* 264 */         return false;
/*     */       }
/*     */     }
/* 267 */     return localIterator1.hasNext() == localIterator2.hasNext();
/*     */   }
/*     */ 
/*     */   private void initializeIORTemplateList()
/*     */   {
/* 273 */     HashMap localHashMap = new HashMap();
/*     */ 
/* 275 */     this.iortemps = IORFactories.makeIORTemplateList();
/* 276 */     Iterator localIterator = iterator();
/* 277 */     ObjectId localObjectId = null;
/* 278 */     while (localIterator.hasNext()) {
/* 279 */       TaggedProfile localTaggedProfile = (TaggedProfile)localIterator.next();
/* 280 */       TaggedProfileTemplate localTaggedProfileTemplate = localTaggedProfile.getTaggedProfileTemplate();
/* 281 */       ObjectKeyTemplate localObjectKeyTemplate = localTaggedProfile.getObjectKeyTemplate();
/*     */ 
/* 285 */       if (localObjectId == null)
/* 286 */         localObjectId = localTaggedProfile.getObjectId();
/* 287 */       else if (!localObjectId.equals(localTaggedProfile.getObjectId())) {
/* 288 */         throw this.wrapper.badOidInIorTemplateList();
/*     */       }
/*     */ 
/* 291 */       IORTemplate localIORTemplate = (IORTemplate)localHashMap.get(localObjectKeyTemplate);
/* 292 */       if (localIORTemplate == null) {
/* 293 */         localIORTemplate = IORFactories.makeIORTemplate(localObjectKeyTemplate);
/* 294 */         localHashMap.put(localObjectKeyTemplate, localIORTemplate);
/* 295 */         this.iortemps.add(localIORTemplate);
/*     */       }
/*     */ 
/* 298 */       localIORTemplate.add(localTaggedProfileTemplate);
/*     */     }
/*     */ 
/* 301 */     this.iortemps.makeImmutable();
/*     */   }
/*     */ 
/*     */   public synchronized IORTemplateList getIORTemplates()
/*     */   {
/* 312 */     if (this.iortemps == null) {
/* 313 */       initializeIORTemplateList();
/*     */     }
/* 315 */     return this.iortemps;
/*     */   }
/*     */ 
/*     */   public IIOPProfile getProfile()
/*     */   {
/* 324 */     IIOPProfile localIIOPProfile = null;
/* 325 */     Iterator localIterator = iteratorById(0);
/* 326 */     if (localIterator.hasNext()) {
/* 327 */       localIIOPProfile = (IIOPProfile)localIterator.next();
/*     */     }
/* 329 */     if (localIIOPProfile != null) {
/* 330 */       return localIIOPProfile;
/*     */     }
/*     */ 
/* 334 */     throw this.wrapper.iorMustHaveIiopProfile();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.ior.IORImpl
 * JD-Core Version:    0.6.2
 */