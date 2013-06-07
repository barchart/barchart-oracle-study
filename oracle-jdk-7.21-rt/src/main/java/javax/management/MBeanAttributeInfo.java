/*     */ package javax.management;
/*     */ 
/*     */ import com.sun.jmx.mbeanserver.GetPropertyAction;
/*     */ import com.sun.jmx.mbeanserver.Introspector;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ 
/*     */ public class MBeanAttributeInfo extends MBeanFeatureInfo
/*     */   implements Cloneable
/*     */ {
/*  66 */   private static final long serialVersionUID = l;
/*     */ 
/*  69 */   static final MBeanAttributeInfo[] NO_ATTRIBUTES = new MBeanAttributeInfo[0];
/*     */   private final String attributeType;
/*     */   private final boolean isWrite;
/*     */   private final boolean isRead;
/*     */   private final boolean is;
/*     */ 
/*     */   public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*     */   {
/* 115 */     this(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null);
/*     */   }
/*     */ 
/*     */   public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor)
/*     */   {
/* 146 */     super(paramString1, paramString3, paramDescriptor);
/*     */ 
/* 148 */     this.attributeType = paramString2;
/* 149 */     this.isRead = paramBoolean1;
/* 150 */     this.isWrite = paramBoolean2;
/* 151 */     if ((paramBoolean3) && (!paramBoolean1)) {
/* 152 */       throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-readable attribute");
/*     */     }
/*     */ 
/* 155 */     if ((paramBoolean3) && (!paramString2.equals("java.lang.Boolean")) && (!paramString2.equals("boolean")))
/*     */     {
/* 157 */       throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-boolean attribute");
/*     */     }
/*     */ 
/* 160 */     this.is = paramBoolean3;
/*     */   }
/*     */ 
/*     */   public MBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 183 */     this(paramString1, attributeType(paramMethod1, paramMethod2), paramString2, paramMethod1 != null, paramMethod2 != null, isIs(paramMethod1), ImmutableDescriptor.union(new Descriptor[] { Introspector.descriptorForElement(paramMethod1), Introspector.descriptorForElement(paramMethod2) }));
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 205 */       return super.clone();
/*     */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 218 */     return this.attributeType;
/*     */   }
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/* 227 */     return this.isRead;
/*     */   }
/*     */ 
/*     */   public boolean isWritable()
/*     */   {
/* 236 */     return this.isWrite;
/*     */   }
/*     */ 
/*     */   public boolean isIs()
/*     */   {
/* 245 */     return this.is;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     String str;
/* 250 */     if (isReadable()) {
/* 251 */       if (isWritable())
/* 252 */         str = "read/write";
/*     */       else
/* 254 */         str = "read-only";
/* 255 */     } else if (isWritable())
/* 256 */       str = "write-only";
/*     */     else {
/* 258 */       str = "no-access";
/*     */     }
/* 260 */     return getClass().getName() + "[" + "description=" + getDescription() + ", " + "name=" + getName() + ", " + "type=" + getType() + ", " + str + ", " + (isIs() ? "isIs, " : "") + "descriptor=" + getDescriptor() + "]";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 283 */     if (paramObject == this)
/* 284 */       return true;
/* 285 */     if (!(paramObject instanceof MBeanAttributeInfo))
/* 286 */       return false;
/* 287 */     MBeanAttributeInfo localMBeanAttributeInfo = (MBeanAttributeInfo)paramObject;
/* 288 */     return (localMBeanAttributeInfo.getName().equals(getName())) && (localMBeanAttributeInfo.getType().equals(getType())) && (localMBeanAttributeInfo.getDescription().equals(getDescription())) && (localMBeanAttributeInfo.getDescriptor().equals(getDescriptor())) && (localMBeanAttributeInfo.isReadable() == isReadable()) && (localMBeanAttributeInfo.isWritable() == isWritable()) && (localMBeanAttributeInfo.isIs() == isIs());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 304 */     return getName().hashCode() ^ getType().hashCode();
/*     */   }
/*     */ 
/*     */   private static boolean isIs(Method paramMethod) {
/* 308 */     return (paramMethod != null) && (paramMethod.getName().startsWith("is")) && ((paramMethod.getReturnType().equals(Boolean.TYPE)) || (paramMethod.getReturnType().equals(Boolean.class)));
/*     */   }
/*     */ 
/*     */   private static String attributeType(Method paramMethod1, Method paramMethod2)
/*     */     throws IntrospectionException
/*     */   {
/* 319 */     Class localClass = null;
/*     */ 
/* 321 */     if (paramMethod1 != null) {
/* 322 */       if (paramMethod1.getParameterTypes().length != 0) {
/* 323 */         throw new IntrospectionException("bad getter arg count");
/*     */       }
/* 325 */       localClass = paramMethod1.getReturnType();
/* 326 */       if (localClass == Void.TYPE) {
/* 327 */         throw new IntrospectionException("getter " + paramMethod1.getName() + " returns void");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 332 */     if (paramMethod2 != null) {
/* 333 */       Class[] arrayOfClass = paramMethod2.getParameterTypes();
/* 334 */       if (arrayOfClass.length != 1) {
/* 335 */         throw new IntrospectionException("bad setter arg count");
/*     */       }
/* 337 */       if (localClass == null)
/* 338 */         localClass = arrayOfClass[0];
/* 339 */       else if (localClass != arrayOfClass[0]) {
/* 340 */         throw new IntrospectionException("type mismatch between getter and setter");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 345 */     if (localClass == null) {
/* 346 */       throw new IntrospectionException("getter and setter cannot both be null");
/*     */     }
/*     */ 
/* 350 */     return localClass.getName();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  57 */     long l = 8644704819898565848L;
/*     */     try {
/*  59 */       GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
/*  60 */       String str = (String)AccessController.doPrivileged(localGetPropertyAction);
/*  61 */       if ("1.0".equals(str))
/*  62 */         l = 7043855487133450673L;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanAttributeInfo
 * JD-Core Version:    0.6.2
 */