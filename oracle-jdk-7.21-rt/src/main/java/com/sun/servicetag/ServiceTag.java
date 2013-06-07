/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class ServiceTag
/*     */ {
/*     */   private String instanceURN;
/*     */   private String productName;
/*     */   private String productVersion;
/*     */   private String productURN;
/*     */   private String productParent;
/*     */   private String productParentURN;
/*     */   private String productDefinedInstanceID;
/*     */   private String productVendor;
/*     */   private String platformArch;
/*     */   private String container;
/*     */   private String source;
/*     */   private int installerUID;
/*     */   private Date timestamp;
/*  85 */   private final int MAX_URN_LEN = 255;
/*  86 */   private final int MAX_PRODUCT_NAME_LEN = 255;
/*  87 */   private final int MAX_PRODUCT_VERSION_LEN = 63;
/*  88 */   private final int MAX_PRODUCT_PARENT_LEN = 255;
/*  89 */   private final int MAX_PRODUCT_VENDOR_LEN = 63;
/*  90 */   private final int MAX_PLATFORM_ARCH_LEN = 63;
/*  91 */   private final int MAX_CONTAINER_LEN = 63;
/*  92 */   private final int MAX_SOURCE_LEN = 63;
/*     */ 
/*     */   private ServiceTag()
/*     */   {
/*     */   }
/*     */ 
/*     */   ServiceTag(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11, int paramInt, Date paramDate)
/*     */   {
/* 111 */     setInstanceURN(paramString1);
/* 112 */     setProductName(paramString2);
/* 113 */     setProductVersion(paramString3);
/* 114 */     setProductURN(paramString4);
/* 115 */     setProductParentURN(paramString6);
/* 116 */     setProductParent(paramString5);
/* 117 */     setProductDefinedInstanceID(paramString7);
/* 118 */     setProductVendor(paramString8);
/* 119 */     setPlatformArch(paramString9);
/* 120 */     setContainer(paramString10);
/* 121 */     setSource(paramString11);
/* 122 */     setInstallerUID(paramInt);
/* 123 */     setTimestamp(paramDate);
/*     */   }
/*     */ 
/*     */   public static ServiceTag newInstance(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10)
/*     */   {
/* 153 */     return new ServiceTag("", paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8, paramString9, paramString10, -1, null);
/*     */   }
/*     */ 
/*     */   public static ServiceTag newInstance(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, String paramString11)
/*     */   {
/* 197 */     return new ServiceTag(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8, paramString9, paramString10, paramString11, -1, null);
/*     */   }
/*     */ 
/*     */   static ServiceTag newInstanceWithUrnTimestamp(ServiceTag paramServiceTag)
/*     */   {
/* 215 */     String str = paramServiceTag.getInstanceURN().length() == 0 ? Util.generateURN() : paramServiceTag.getInstanceURN();
/*     */ 
/* 218 */     ServiceTag localServiceTag = new ServiceTag(str, paramServiceTag.getProductName(), paramServiceTag.getProductVersion(), paramServiceTag.getProductURN(), paramServiceTag.getProductParent(), paramServiceTag.getProductParentURN(), paramServiceTag.getProductDefinedInstanceID(), paramServiceTag.getProductVendor(), paramServiceTag.getPlatformArch(), paramServiceTag.getContainer(), paramServiceTag.getSource(), paramServiceTag.getInstallerUID(), new Date());
/*     */ 
/* 231 */     return localServiceTag;
/*     */   }
/*     */ 
/*     */   public static String generateInstanceURN()
/*     */   {
/* 242 */     return Util.generateURN();
/*     */   }
/*     */ 
/*     */   public String getInstanceURN()
/*     */   {
/* 251 */     return this.instanceURN;
/*     */   }
/*     */ 
/*     */   public String getProductName()
/*     */   {
/* 260 */     return this.productName;
/*     */   }
/*     */ 
/*     */   public String getProductVersion()
/*     */   {
/* 269 */     return this.productVersion;
/*     */   }
/*     */ 
/*     */   public String getProductURN()
/*     */   {
/* 278 */     return this.productURN;
/*     */   }
/*     */ 
/*     */   public String getProductParentURN()
/*     */   {
/* 287 */     return this.productParentURN;
/*     */   }
/*     */ 
/*     */   public String getProductParent()
/*     */   {
/* 296 */     return this.productParent;
/*     */   }
/*     */ 
/*     */   public String getProductDefinedInstanceID()
/*     */   {
/* 305 */     return this.productDefinedInstanceID;
/*     */   }
/*     */ 
/*     */   public String getProductVendor()
/*     */   {
/* 314 */     return this.productVendor;
/*     */   }
/*     */ 
/*     */   public String getPlatformArch()
/*     */   {
/* 324 */     return this.platformArch;
/*     */   }
/*     */ 
/*     */   public Date getTimestamp()
/*     */   {
/* 338 */     if (this.timestamp != null) {
/* 339 */       return (Date)this.timestamp.clone();
/*     */     }
/* 341 */     return null;
/*     */   }
/*     */ 
/*     */   public String getContainer()
/*     */   {
/* 352 */     return this.container;
/*     */   }
/*     */ 
/*     */   public String getSource()
/*     */   {
/* 361 */     return this.source;
/*     */   }
/*     */ 
/*     */   public int getInstallerUID()
/*     */   {
/* 376 */     return this.installerUID;
/*     */   }
/*     */ 
/*     */   private void setInstanceURN(String paramString)
/*     */   {
/* 383 */     if (paramString == null) {
/* 384 */       throw new NullPointerException("Parameter instanceURN cannot be null");
/*     */     }
/* 386 */     if (paramString.length() > 255) {
/* 387 */       throw new IllegalArgumentException("instanceURN \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 390 */     this.instanceURN = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductName(String paramString) {
/* 394 */     if (paramString == null) {
/* 395 */       throw new NullPointerException("Parameter productName cannot be null");
/*     */     }
/* 397 */     if (paramString.length() == 0) {
/* 398 */       throw new IllegalArgumentException("product name cannot be empty");
/*     */     }
/* 400 */     if (paramString.length() > 255) {
/* 401 */       throw new IllegalArgumentException("productName \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 404 */     this.productName = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductVersion(String paramString) {
/* 408 */     if (paramString == null) {
/* 409 */       throw new NullPointerException("Parameter productVersion cannot be null");
/*     */     }
/*     */ 
/* 412 */     if (paramString.length() == 0) {
/* 413 */       throw new IllegalArgumentException("product version cannot be empty");
/*     */     }
/* 415 */     if (paramString.length() > 63) {
/* 416 */       throw new IllegalArgumentException("productVersion \"" + paramString + "\" exceeds maximum length " + 63);
/*     */     }
/*     */ 
/* 420 */     this.productVersion = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductURN(String paramString) {
/* 424 */     if (paramString == null) {
/* 425 */       throw new NullPointerException("Parameter productURN cannot be null");
/*     */     }
/* 427 */     if (paramString.length() == 0) {
/* 428 */       throw new IllegalArgumentException("product URN cannot be empty");
/*     */     }
/* 430 */     if (paramString.length() > 255) {
/* 431 */       throw new IllegalArgumentException("productURN \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 434 */     this.productURN = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductParentURN(String paramString) {
/* 438 */     if (paramString == null) {
/* 439 */       throw new NullPointerException("Parameter productParentURN cannot be null");
/*     */     }
/*     */ 
/* 442 */     if (paramString.length() > 255) {
/* 443 */       throw new IllegalArgumentException("productParentURN \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 447 */     this.productParentURN = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductParent(String paramString) {
/* 451 */     if (paramString == null) {
/* 452 */       throw new NullPointerException("Parameter productParent cannot be null");
/*     */     }
/* 454 */     if (paramString.length() == 0) {
/* 455 */       throw new IllegalArgumentException("product parent cannot be empty");
/*     */     }
/* 457 */     if (paramString.length() > 255) {
/* 458 */       throw new IllegalArgumentException("productParent \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 462 */     this.productParent = paramString;
/*     */   }
/*     */ 
/*     */   void setProductDefinedInstanceID(String paramString) {
/* 466 */     if (paramString == null) {
/* 467 */       throw new NullPointerException("Parameter productDefinedInstanceID cannot be null");
/*     */     }
/* 469 */     if (paramString.length() > 255) {
/* 470 */       throw new IllegalArgumentException("productDefinedInstanceID \"" + paramString + "\" exceeds maximum length " + 255);
/*     */     }
/*     */ 
/* 475 */     this.productDefinedInstanceID = paramString;
/*     */   }
/*     */ 
/*     */   private void setProductVendor(String paramString) {
/* 479 */     if (paramString == null) {
/* 480 */       throw new NullPointerException("Parameter productVendor cannot be null");
/*     */     }
/* 482 */     if (paramString.length() == 0) {
/* 483 */       throw new IllegalArgumentException("product vendor cannot be empty");
/*     */     }
/* 485 */     if (paramString.length() > 63) {
/* 486 */       throw new IllegalArgumentException("productVendor \"" + paramString + "\" exceeds maximum length " + 63);
/*     */     }
/*     */ 
/* 490 */     this.productVendor = paramString;
/*     */   }
/*     */ 
/*     */   private void setPlatformArch(String paramString) {
/* 494 */     if (paramString == null) {
/* 495 */       throw new NullPointerException("Parameter platformArch cannot be null");
/*     */     }
/* 497 */     if (paramString.length() == 0) {
/* 498 */       throw new IllegalArgumentException("platform architecture cannot be empty");
/*     */     }
/* 500 */     if (paramString.length() > 63) {
/* 501 */       throw new IllegalArgumentException("platformArch \"" + paramString + "\" exceeds maximum length " + 63);
/*     */     }
/*     */ 
/* 505 */     this.platformArch = paramString;
/*     */   }
/*     */ 
/*     */   private void setTimestamp(Date paramDate)
/*     */   {
/* 510 */     this.timestamp = paramDate;
/*     */   }
/*     */ 
/*     */   private void setContainer(String paramString) {
/* 514 */     if (paramString == null) {
/* 515 */       throw new NullPointerException("Parameter container cannot be null");
/*     */     }
/* 517 */     if (paramString.length() == 0) {
/* 518 */       throw new IllegalArgumentException("container cannot be empty");
/*     */     }
/* 520 */     if (paramString.length() > 63) {
/* 521 */       throw new IllegalArgumentException("container \"" + paramString + "\" exceeds maximum length " + 63);
/*     */     }
/*     */ 
/* 525 */     this.container = paramString;
/*     */   }
/*     */ 
/*     */   private void setSource(String paramString) {
/* 529 */     if (paramString == null) {
/* 530 */       throw new NullPointerException("Parameter source cannot be null");
/*     */     }
/* 532 */     if (paramString.length() == 0) {
/* 533 */       throw new IllegalArgumentException("source cannot be empty");
/*     */     }
/* 535 */     if (paramString.length() > 63) {
/* 536 */       throw new IllegalArgumentException("source \"" + paramString + "\" exceeds maximum length " + 63);
/*     */     }
/*     */ 
/* 539 */     this.source = paramString;
/*     */   }
/*     */ 
/*     */   private void setInstallerUID(int paramInt) {
/* 543 */     this.installerUID = paramInt;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 558 */     if ((paramObject == null) || (!(paramObject instanceof ServiceTag))) {
/* 559 */       return false;
/*     */     }
/* 561 */     ServiceTag localServiceTag = (ServiceTag)paramObject;
/* 562 */     if (localServiceTag == this) {
/* 563 */       return true;
/*     */     }
/* 565 */     return localServiceTag.getInstanceURN().equals(getInstanceURN());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 574 */     int i = 7;
/* 575 */     i = 19 * i + (this.instanceURN != null ? this.instanceURN.hashCode() : 0);
/* 576 */     return i;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 586 */     StringBuilder localStringBuilder = new StringBuilder();
/* 587 */     localStringBuilder.append("instance_urn").append("=").append(this.instanceURN).append("\n");
/* 588 */     localStringBuilder.append("product_name").append("=").append(this.productName).append("\n");
/* 589 */     localStringBuilder.append("product_version").append("=").append(this.productVersion).append("\n");
/* 590 */     localStringBuilder.append("product_urn").append("=").append(this.productURN).append("\n");
/* 591 */     localStringBuilder.append("product_parent_urn").append("=").append(this.productParentURN).append("\n");
/* 592 */     localStringBuilder.append("product_parent").append("=").append(this.productParent).append("\n");
/* 593 */     localStringBuilder.append("product_defined_inst_id").append("=").append(this.productDefinedInstanceID).append("\n");
/* 594 */     localStringBuilder.append("product_vendor").append("=").append(this.productVendor).append("\n");
/* 595 */     localStringBuilder.append("platform_arch").append("=").append(this.platformArch).append("\n");
/* 596 */     localStringBuilder.append("timestamp").append("=").append(Util.formatTimestamp(this.timestamp)).append("\n");
/* 597 */     localStringBuilder.append("container").append("=").append(this.container).append("\n");
/* 598 */     localStringBuilder.append("source").append("=").append(this.source).append("\n");
/* 599 */     localStringBuilder.append("installer_uid").append("=").append(String.valueOf(this.installerUID)).append("\n");
/* 600 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   public static ServiceTag getJavaServiceTag(String paramString)
/*     */     throws IOException
/*     */   {
/* 631 */     return Installer.getJavaServiceTag(paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.ServiceTag
 * JD-Core Version:    0.6.2
 */