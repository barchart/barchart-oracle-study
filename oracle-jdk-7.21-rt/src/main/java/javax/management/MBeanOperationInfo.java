/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Introspector;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class MBeanOperationInfo extends MBeanFeatureInfo
/*     */   implements Cloneable
/*     */ {
/*     */   static final long serialVersionUID = -6178860474881375330L;
/*  45 */   static final MBeanOperationInfo[] NO_OPERATIONS = new MBeanOperationInfo[0];
/*     */   public static final int INFO = 0;
/*     */   public static final int ACTION = 1;
/*     */   public static final int ACTION_INFO = 2;
/*     */   public static final int UNKNOWN = 3;
/*     */   private final String type;
/*     */   private final MBeanParameterInfo[] signature;
/*     */   private final int impact;
/*     */   private final transient boolean arrayGettersSafe;
/*     */ 
/*     */   public MBeanOperationInfo(String paramString, Method paramMethod)
/*     */   {
/* 107 */     this(paramMethod.getName(), paramString, methodSignature(paramMethod), paramMethod.getReturnType().getName(), 3, Introspector.descriptorForElement(paramMethod));
/*     */   }
/*     */ 
/*     */   public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt)
/*     */   {
/* 133 */     this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt, (Descriptor)null);
/*     */   }
/*     */ 
/*     */   public MBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt, Descriptor paramDescriptor)
/*     */   {
/* 160 */     super(paramString1, paramString2, paramDescriptor);
/*     */ 
/* 162 */     if ((paramArrayOfMBeanParameterInfo == null) || (paramArrayOfMBeanParameterInfo.length == 0))
/* 163 */       paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
/*     */     else
/* 165 */       paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
/* 166 */     this.signature = paramArrayOfMBeanParameterInfo;
/* 167 */     this.type = paramString3;
/* 168 */     this.impact = paramInt;
/* 169 */     this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanOperationInfo.class);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 187 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 190 */     return null;
/*     */   }
/*     */ 
/*     */   public String getReturnType()
/*     */   {
/* 200 */     return this.type;
/*     */   }
/*     */ 
/*     */   public MBeanParameterInfo[] getSignature()
/*     */   {
/* 226 */     if (this.signature == null)
/*     */     {
/* 229 */       return MBeanParameterInfo.NO_PARAMS;
/* 230 */     }if (this.signature.length == 0) {
/* 231 */       return this.signature;
/*     */     }
/* 233 */     return (MBeanParameterInfo[])this.signature.clone();
/*     */   }
/*     */ 
/*     */   private MBeanParameterInfo[] fastGetSignature() {
/* 237 */     if (this.arrayGettersSafe)
/*     */     {
/* 241 */       if (this.signature == null)
/* 242 */         return MBeanParameterInfo.NO_PARAMS;
/* 243 */       return this.signature;
/* 244 */     }return getSignature();
/*     */   }
/*     */ 
/*     */   public int getImpact()
/*     */   {
/* 254 */     return this.impact;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String str;
/* 260 */     switch (getImpact()) { case 1:
/* 261 */       str = "action"; break;
/*     */     case 2:
/* 262 */       str = "action/info"; break;
/*     */     case 0:
/* 263 */       str = "info"; break;
/*     */     case 3:
/* 264 */       str = "unknown"; break;
/*     */     default:
/* 265 */       str = "(" + getImpact() + ")";
/*     */     }
/* 267 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "returnType=" + getReturnType() + ", " + "signature=" + Arrays.asList(fastGetSignature()) + ", " + "impact=" + str + ", " + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 291 */     if (paramObject == this)
/* 292 */       return true;
/* 293 */     if (!(paramObject instanceof MBeanOperationInfo))
/* 294 */       return false;
/* 295 */     MBeanOperationInfo localMBeanOperationInfo = (MBeanOperationInfo)paramObject;
/* 296 */     return (localMBeanOperationInfo.getName().equals(getName())) && (localMBeanOperationInfo.getReturnType().equals(getReturnType())) && (localMBeanOperationInfo.getDescription().equals(getDescription())) && (localMBeanOperationInfo.getImpact() == getImpact()) && (Arrays.equals(localMBeanOperationInfo.fastGetSignature(), fastGetSignature())) && (localMBeanOperationInfo.getDescriptor().equals(getDescriptor()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 312 */     return getName().hashCode() ^ getReturnType().hashCode();
/*     */   }
/*     */ 
/*     */   private static MBeanParameterInfo[] methodSignature(Method paramMethod) {
/* 316 */     Class[] arrayOfClass = paramMethod.getParameterTypes();
/* 317 */     Annotation[][] arrayOfAnnotation = paramMethod.getParameterAnnotations();
/* 318 */     return parameters(arrayOfClass, arrayOfAnnotation);
/*     */   }
/*     */ 
/*     */   static MBeanParameterInfo[] parameters(Class<?>[] paramArrayOfClass, Annotation[][] paramArrayOfAnnotation)
/*     */   {
/* 323 */     MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[paramArrayOfClass.length];
/*     */ 
/* 325 */     assert (paramArrayOfClass.length == paramArrayOfAnnotation.length);
/*     */ 
/* 327 */     for (int i = 0; i < paramArrayOfClass.length; i++) {
/* 328 */       Descriptor localDescriptor = Introspector.descriptorForAnnotations(paramArrayOfAnnotation[i]);
/* 329 */       String str = "p" + (i + 1);
/* 330 */       arrayOfMBeanParameterInfo[i] = new MBeanParameterInfo(str, paramArrayOfClass[i].getName(), "", localDescriptor);
/*     */     }
/*     */ 
/* 334 */     return arrayOfMBeanParameterInfo;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanOperationInfo
 * JD-Core Version:    0.6.2
 */