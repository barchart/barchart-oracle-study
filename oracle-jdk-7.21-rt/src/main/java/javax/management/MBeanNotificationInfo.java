/*     */ package javax.management;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class MBeanNotificationInfo extends MBeanFeatureInfo
/*     */   implements Cloneable
/*     */ {
/*     */   static final long serialVersionUID = -3888371564530107064L;
/*  62 */   private static final String[] NO_TYPES = new String[0];
/*     */ 
/*  64 */   static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
/*     */   private final String[] types;
/*     */   private final transient boolean arrayGettersSafe;
/*     */ 
/*     */   public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2)
/*     */   {
/*  88 */     this(paramArrayOfString, paramString1, paramString2, null);
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor)
/*     */   {
/* 109 */     super(paramString1, paramString2, paramDescriptor);
/*     */ 
/* 117 */     if (paramArrayOfString == null)
/* 118 */       paramArrayOfString = NO_TYPES;
/* 119 */     this.types = paramArrayOfString;
/* 120 */     this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanNotificationInfo.class);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 135 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getNotifTypes()
/*     */   {
/* 151 */     if (this.types.length == 0) {
/* 152 */       return NO_TYPES;
/*     */     }
/* 154 */     return (String[])this.types.clone();
/*     */   }
/*     */ 
/*     */   private String[] fastGetNotifTypes() {
/* 158 */     if (this.arrayGettersSafe) {
/* 159 */       return this.types;
/*     */     }
/* 161 */     return getNotifTypes();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 165 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "notifTypes=" + Arrays.asList(fastGetNotifTypes()) + ", " + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 189 */     if (paramObject == this)
/* 190 */       return true;
/* 191 */     if (!(paramObject instanceof MBeanNotificationInfo))
/* 192 */       return false;
/* 193 */     MBeanNotificationInfo localMBeanNotificationInfo = (MBeanNotificationInfo)paramObject;
/* 194 */     return (localMBeanNotificationInfo.getName().equals(getName())) && (localMBeanNotificationInfo.getDescription().equals(getDescription())) && (localMBeanNotificationInfo.getDescriptor().equals(getDescriptor())) && (Arrays.equals(localMBeanNotificationInfo.fastGetNotifTypes(), fastGetNotifTypes()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 201 */     int i = getName().hashCode();
/* 202 */     for (int j = 0; j < this.types.length; j++)
/* 203 */       i ^= this.types[j].hashCode();
/* 204 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanNotificationInfo
 * JD-Core Version:    0.6.2
 */