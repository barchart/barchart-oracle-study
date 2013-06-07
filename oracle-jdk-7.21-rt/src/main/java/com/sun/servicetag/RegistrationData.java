/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class RegistrationData
/*     */ {
/* 196 */   private final Map<String, String> environment = initEnvironment();
/* 197 */   private final Map<String, ServiceTag> svcTagMap = new LinkedHashMap();
/*     */   private final String urn;
/*     */ 
/*     */   public RegistrationData()
/*     */   {
/* 214 */     this(Util.generateURN());
/* 215 */     SystemEnvironment localSystemEnvironment = SystemEnvironment.getSystemEnvironment();
/* 216 */     setEnvironment("hostname", localSystemEnvironment.getHostname());
/* 217 */     setEnvironment("hostId", localSystemEnvironment.getHostId());
/* 218 */     setEnvironment("osName", localSystemEnvironment.getOsName());
/* 219 */     setEnvironment("osVersion", localSystemEnvironment.getOsVersion());
/* 220 */     setEnvironment("osArchitecture", localSystemEnvironment.getOsArchitecture());
/* 221 */     setEnvironment("systemModel", localSystemEnvironment.getSystemModel());
/* 222 */     setEnvironment("systemManufacturer", localSystemEnvironment.getSystemManufacturer());
/* 223 */     setEnvironment("cpuManufacturer", localSystemEnvironment.getCpuManufacturer());
/* 224 */     setEnvironment("serialNumber", localSystemEnvironment.getSerialNumber());
/*     */   }
/*     */ 
/*     */   RegistrationData(String paramString)
/*     */   {
/* 229 */     this.urn = paramString;
/*     */   }
/*     */ 
/*     */   private Map<String, String> initEnvironment() {
/* 233 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/* 234 */     localLinkedHashMap.put("hostname", "");
/* 235 */     localLinkedHashMap.put("hostId", "");
/* 236 */     localLinkedHashMap.put("osName", "");
/* 237 */     localLinkedHashMap.put("osVersion", "");
/* 238 */     localLinkedHashMap.put("osArchitecture", "");
/* 239 */     localLinkedHashMap.put("systemModel", "");
/* 240 */     localLinkedHashMap.put("systemManufacturer", "");
/* 241 */     localLinkedHashMap.put("cpuManufacturer", "");
/* 242 */     localLinkedHashMap.put("serialNumber", "");
/* 243 */     return localLinkedHashMap;
/*     */   }
/*     */ 
/*     */   public String getRegistrationURN()
/*     */   {
/* 254 */     return this.urn;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getEnvironmentMap()
/*     */   {
/* 267 */     return new LinkedHashMap(this.environment);
/*     */   }
/*     */ 
/*     */   public void setEnvironment(String paramString1, String paramString2)
/*     */   {
/* 278 */     if (paramString1 == null) {
/* 279 */       throw new NullPointerException("name is null");
/*     */     }
/* 281 */     if (paramString2 == null) {
/* 282 */       throw new NullPointerException("value is null");
/*     */     }
/* 284 */     if (this.environment.containsKey(paramString1)) {
/* 285 */       if (((paramString1.equals("hostname")) || (paramString1.equals("osName"))) && 
/* 286 */         (paramString2.length() == 0)) {
/* 287 */         throw new IllegalArgumentException("\"" + paramString1 + "\" requires non-empty value.");
/*     */       }
/*     */ 
/* 291 */       this.environment.put(paramString1, paramString2);
/*     */     } else {
/* 293 */       throw new IllegalArgumentException("\"" + paramString1 + "\" is not an environment element.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<ServiceTag> getServiceTags()
/*     */   {
/* 305 */     return new HashSet(this.svcTagMap.values());
/*     */   }
/*     */ 
/*     */   public synchronized ServiceTag addServiceTag(ServiceTag paramServiceTag)
/*     */   {
/* 324 */     ServiceTag localServiceTag = ServiceTag.newInstanceWithUrnTimestamp(paramServiceTag);
/*     */ 
/* 326 */     String str = localServiceTag.getInstanceURN();
/* 327 */     if (this.svcTagMap.containsKey(str)) {
/* 328 */       throw new IllegalArgumentException("Instance_urn = " + str + " already exists in the registration data.");
/*     */     }
/*     */ 
/* 331 */     this.svcTagMap.put(str, localServiceTag);
/*     */ 
/* 333 */     return localServiceTag;
/*     */   }
/*     */ 
/*     */   public synchronized ServiceTag getServiceTag(String paramString)
/*     */   {
/* 345 */     if (paramString == null) {
/* 346 */       throw new NullPointerException("instanceURN is null");
/*     */     }
/* 348 */     return (ServiceTag)this.svcTagMap.get(paramString);
/*     */   }
/*     */ 
/*     */   public synchronized ServiceTag removeServiceTag(String paramString)
/*     */   {
/* 363 */     if (paramString == null) {
/* 364 */       throw new NullPointerException("instanceURN is null");
/*     */     }
/*     */ 
/* 367 */     ServiceTag localServiceTag = null;
/* 368 */     if (this.svcTagMap.containsKey(paramString)) {
/* 369 */       localServiceTag = (ServiceTag)this.svcTagMap.remove(paramString);
/*     */     }
/* 371 */     return localServiceTag;
/*     */   }
/*     */ 
/*     */   public synchronized ServiceTag updateServiceTag(String paramString1, String paramString2)
/*     */   {
/* 388 */     ServiceTag localServiceTag = getServiceTag(paramString1);
/* 389 */     if (localServiceTag == null) {
/* 390 */       return null;
/*     */     }
/*     */ 
/* 393 */     localServiceTag = ServiceTag.newInstanceWithUrnTimestamp(localServiceTag);
/*     */ 
/* 395 */     localServiceTag.setProductDefinedInstanceID(paramString2);
/* 396 */     this.svcTagMap.put(paramString1, localServiceTag);
/* 397 */     return localServiceTag;
/*     */   }
/*     */ 
/*     */   public static RegistrationData loadFromXML(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 418 */       return RegistrationDocument.load(paramInputStream);
/*     */     } finally {
/* 420 */       paramInputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void storeToXML(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 435 */     RegistrationDocument.store(paramOutputStream, this);
/* 436 */     paramOutputStream.flush();
/*     */   }
/*     */ 
/*     */   public byte[] toXML()
/*     */   {
/*     */     try
/*     */     {
/* 448 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 449 */       storeToXML(localByteArrayOutputStream);
/* 450 */       return localByteArrayOutputStream.toByteArray();
/*     */     } catch (IOException localIOException) {
/*     */     }
/* 453 */     return new byte[0];
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 467 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 468 */       storeToXML(localByteArrayOutputStream);
/* 469 */       return localByteArrayOutputStream.toString("UTF-8");
/*     */     } catch (IOException localIOException) {
/*     */     }
/* 472 */     return "Error creating the return string.";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.RegistrationData
 * JD-Core Version:    0.6.2
 */