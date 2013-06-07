/*     */ package javax.management;
/*     */ 
/*     */ public class MBeanParameterInfo extends MBeanFeatureInfo
/*     */   implements Cloneable
/*     */ {
/*     */   static final long serialVersionUID = 7432616882776782338L;
/*  42 */   static final MBeanParameterInfo[] NO_PARAMS = new MBeanParameterInfo[0];
/*     */   private final String type;
/*     */ 
/*     */   public MBeanParameterInfo(String paramString1, String paramString2, String paramString3)
/*     */   {
/*  60 */     this(paramString1, paramString2, paramString3, (Descriptor)null);
/*     */   }
/*     */ 
/*     */   public MBeanParameterInfo(String paramString1, String paramString2, String paramString3, Descriptor paramDescriptor)
/*     */   {
/*  78 */     super(paramString1, paramString3, paramDescriptor);
/*     */ 
/*  80 */     this.type = paramString2;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/*  96 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 109 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 113 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "type=" + getType() + ", " + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 134 */     if (paramObject == this)
/* 135 */       return true;
/* 136 */     if (!(paramObject instanceof MBeanParameterInfo))
/* 137 */       return false;
/* 138 */     MBeanParameterInfo localMBeanParameterInfo = (MBeanParameterInfo)paramObject;
/* 139 */     return (localMBeanParameterInfo.getName().equals(getName())) && (localMBeanParameterInfo.getType().equals(getType())) && (localMBeanParameterInfo.getDescription().equals(getDescription())) && (localMBeanParameterInfo.getDescriptor().equals(getDescriptor()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 146 */     return getName().hashCode() ^ getType().hashCode();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanParameterInfo
 * JD-Core Version:    0.6.2
 */