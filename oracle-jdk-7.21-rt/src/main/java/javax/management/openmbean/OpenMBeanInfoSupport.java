/*     */ package javax.management.openmbean;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import javax.management.Descriptor;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ 
/*     */ public class OpenMBeanInfoSupport extends MBeanInfo
/*     */   implements OpenMBeanInfo
/*     */ {
/*     */   static final long serialVersionUID = 4349395935420511492L;
/*  63 */   private transient Integer myHashCode = null;
/*  64 */   private transient String myToString = null;
/*     */ 
/*     */   public OpenMBeanInfoSupport(String paramString1, String paramString2, OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo, OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo, OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo)
/*     */   {
/* 118 */     this(paramString1, paramString2, paramArrayOfOpenMBeanAttributeInfo, paramArrayOfOpenMBeanConstructorInfo, paramArrayOfOpenMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, (Descriptor)null);
/*     */   }
/*     */ 
/*     */   public OpenMBeanInfoSupport(String paramString1, String paramString2, OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo, OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo, OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor)
/*     */   {
/* 180 */     super(paramString1, paramString2, attributeArray(paramArrayOfOpenMBeanAttributeInfo), constructorArray(paramArrayOfOpenMBeanConstructorInfo), operationArray(paramArrayOfOpenMBeanOperationInfo), paramArrayOfMBeanNotificationInfo == null ? null : (MBeanNotificationInfo[])paramArrayOfMBeanNotificationInfo.clone(), paramDescriptor);
/*     */   }
/*     */ 
/*     */   private static MBeanAttributeInfo[] attributeArray(OpenMBeanAttributeInfo[] paramArrayOfOpenMBeanAttributeInfo)
/*     */   {
/* 192 */     if (paramArrayOfOpenMBeanAttributeInfo == null)
/* 193 */       return null;
/* 194 */     MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = new MBeanAttributeInfo[paramArrayOfOpenMBeanAttributeInfo.length];
/* 195 */     System.arraycopy(paramArrayOfOpenMBeanAttributeInfo, 0, arrayOfMBeanAttributeInfo, 0, paramArrayOfOpenMBeanAttributeInfo.length);
/*     */ 
/* 197 */     return arrayOfMBeanAttributeInfo;
/*     */   }
/*     */ 
/*     */   private static MBeanConstructorInfo[] constructorArray(OpenMBeanConstructorInfo[] paramArrayOfOpenMBeanConstructorInfo)
/*     */   {
/* 202 */     if (paramArrayOfOpenMBeanConstructorInfo == null)
/* 203 */       return null;
/* 204 */     MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = new MBeanConstructorInfo[paramArrayOfOpenMBeanConstructorInfo.length];
/* 205 */     System.arraycopy(paramArrayOfOpenMBeanConstructorInfo, 0, arrayOfMBeanConstructorInfo, 0, paramArrayOfOpenMBeanConstructorInfo.length);
/*     */ 
/* 207 */     return arrayOfMBeanConstructorInfo;
/*     */   }
/*     */ 
/*     */   private static MBeanOperationInfo[] operationArray(OpenMBeanOperationInfo[] paramArrayOfOpenMBeanOperationInfo)
/*     */   {
/* 212 */     if (paramArrayOfOpenMBeanOperationInfo == null)
/* 213 */       return null;
/* 214 */     MBeanOperationInfo[] arrayOfMBeanOperationInfo = new MBeanOperationInfo[paramArrayOfOpenMBeanOperationInfo.length];
/* 215 */     System.arraycopy(paramArrayOfOpenMBeanOperationInfo, 0, arrayOfMBeanOperationInfo, 0, paramArrayOfOpenMBeanOperationInfo.length);
/* 216 */     return arrayOfMBeanOperationInfo;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 254 */     if (paramObject == null) {
/* 255 */       return false;
/*     */     }
/*     */ 
/*     */     OpenMBeanInfo localOpenMBeanInfo;
/*     */     try
/*     */     {
/* 262 */       localOpenMBeanInfo = (OpenMBeanInfo)paramObject;
/*     */     } catch (ClassCastException localClassCastException) {
/* 264 */       return false;
/*     */     }
/*     */ 
/* 272 */     if (!getClassName().equals(localOpenMBeanInfo.getClassName())) {
/* 273 */       return false;
/*     */     }
/*     */ 
/* 277 */     if (!sameArrayContents(getAttributes(), localOpenMBeanInfo.getAttributes())) {
/* 278 */       return false;
/*     */     }
/*     */ 
/* 282 */     if (!sameArrayContents(getConstructors(), localOpenMBeanInfo.getConstructors())) {
/* 283 */       return false;
/*     */     }
/*     */ 
/* 287 */     if (!sameArrayContents(getOperations(), localOpenMBeanInfo.getOperations()))
/*     */     {
/* 289 */       return false;
/*     */     }
/*     */ 
/* 293 */     if (!sameArrayContents(getNotifications(), localOpenMBeanInfo.getNotifications())) {
/* 294 */       return false;
/*     */     }
/*     */ 
/* 298 */     return true;
/*     */   }
/*     */ 
/*     */   private static <T> boolean sameArrayContents(T[] paramArrayOfT1, T[] paramArrayOfT2) {
/* 302 */     return new HashSet(Arrays.asList(paramArrayOfT1)).equals(new HashSet(Arrays.asList(paramArrayOfT2)));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 343 */     if (this.myHashCode == null) {
/* 344 */       int i = 0;
/* 345 */       i += getClassName().hashCode();
/* 346 */       i += arraySetHash(getAttributes());
/* 347 */       i += arraySetHash(getConstructors());
/* 348 */       i += arraySetHash(getOperations());
/* 349 */       i += arraySetHash(getNotifications());
/* 350 */       this.myHashCode = Integer.valueOf(i);
/*     */     }
/*     */ 
/* 355 */     return this.myHashCode.intValue();
/*     */   }
/*     */ 
/*     */   private static <T> int arraySetHash(T[] paramArrayOfT) {
/* 359 */     return new HashSet(Arrays.asList(paramArrayOfT)).hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 387 */     if (this.myToString == null) {
/* 388 */       this.myToString = (getClass().getName() + "(mbean_class_name=" + getClassName() + ",attributes=" + Arrays.asList(getAttributes()).toString() + ",constructors=" + Arrays.asList(getConstructors()).toString() + ",operations=" + Arrays.asList(getOperations()).toString() + ",notifications=" + Arrays.asList(getNotifications()).toString() + ",descriptor=" + getDescriptor() + ")");
/*     */     }
/*     */ 
/* 409 */     return this.myToString;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.openmbean.OpenMBeanInfoSupport
 * JD-Core Version:    0.6.2
 */