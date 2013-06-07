/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.Introspector;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class MBeanConstructorInfo extends MBeanFeatureInfo
/*     */   implements Cloneable
/*     */ {
/*     */   static final long serialVersionUID = 4433990064191844427L;
/*  45 */   static final MBeanConstructorInfo[] NO_CONSTRUCTORS = new MBeanConstructorInfo[0];
/*     */   private final transient boolean arrayGettersSafe;
/*     */   private final MBeanParameterInfo[] signature;
/*     */ 
/*     */   public MBeanConstructorInfo(String paramString, Constructor<?> paramConstructor)
/*     */   {
/*  68 */     this(paramConstructor.getName(), paramString, constructorSignature(paramConstructor), Introspector.descriptorForElement(paramConstructor));
/*     */   }
/*     */ 
/*     */   public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
/*     */   {
/*  85 */     this(paramString1, paramString2, paramArrayOfMBeanParameterInfo, null);
/*     */   }
/*     */ 
/*     */   public MBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, Descriptor paramDescriptor)
/*     */   {
/* 105 */     super(paramString1, paramString2, paramDescriptor);
/*     */ 
/* 107 */     if ((paramArrayOfMBeanParameterInfo == null) || (paramArrayOfMBeanParameterInfo.length == 0))
/* 108 */       paramArrayOfMBeanParameterInfo = MBeanParameterInfo.NO_PARAMS;
/*     */     else
/* 110 */       paramArrayOfMBeanParameterInfo = (MBeanParameterInfo[])paramArrayOfMBeanParameterInfo.clone();
/* 111 */     this.signature = paramArrayOfMBeanParameterInfo;
/* 112 */     this.arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanConstructorInfo.class);
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 130 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */   public MBeanParameterInfo[] getSignature()
/*     */   {
/* 151 */     if (this.signature.length == 0) {
/* 152 */       return this.signature;
/*     */     }
/* 154 */     return (MBeanParameterInfo[])this.signature.clone();
/*     */   }
/*     */ 
/*     */   private MBeanParameterInfo[] fastGetSignature() {
/* 158 */     if (this.arrayGettersSafe) {
/* 159 */       return this.signature;
/*     */     }
/* 161 */     return getSignature();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 165 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "signature=" + Arrays.asList(fastGetSignature()) + ", " + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 188 */     if (paramObject == this)
/* 189 */       return true;
/* 190 */     if (!(paramObject instanceof MBeanConstructorInfo))
/* 191 */       return false;
/* 192 */     MBeanConstructorInfo localMBeanConstructorInfo = (MBeanConstructorInfo)paramObject;
/* 193 */     return (localMBeanConstructorInfo.getName().equals(getName())) && (localMBeanConstructorInfo.getDescription().equals(getDescription())) && (Arrays.equals(localMBeanConstructorInfo.fastGetSignature(), fastGetSignature())) && (localMBeanConstructorInfo.getDescriptor().equals(getDescriptor()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 206 */     int i = getName().hashCode();
/* 207 */     MBeanParameterInfo[] arrayOfMBeanParameterInfo = fastGetSignature();
/* 208 */     for (int j = 0; j < arrayOfMBeanParameterInfo.length; j++)
/* 209 */       i ^= arrayOfMBeanParameterInfo[j].hashCode();
/* 210 */     return i;
/*     */   }
/*     */ 
/*     */   private static MBeanParameterInfo[] constructorSignature(Constructor<?> paramConstructor) {
/* 214 */     Class[] arrayOfClass = paramConstructor.getParameterTypes();
/* 215 */     Annotation[][] arrayOfAnnotation = paramConstructor.getParameterAnnotations();
/* 216 */     return MBeanOperationInfo.parameters(arrayOfClass, arrayOfAnnotation);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanConstructorInfo
 * JD-Core Version:    0.6.2
 */