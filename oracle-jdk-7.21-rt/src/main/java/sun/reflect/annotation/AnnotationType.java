/*     */ package sun.reflect.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.annotation.Inherited;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.misc.JavaLangAccess;
/*     */ import sun.misc.SharedSecrets;
/*     */ 
/*     */ public class AnnotationType
/*     */ {
/*  48 */   private final Map<String, Class<?>> memberTypes = new HashMap();
/*     */ 
/*  53 */   private final Map<String, Object> memberDefaults = new HashMap();
/*     */ 
/*  60 */   private final Map<String, Method> members = new HashMap();
/*     */ 
/*  65 */   private RetentionPolicy retention = RetentionPolicy.RUNTIME;
/*     */ 
/*  70 */   private boolean inherited = false;
/*     */ 
/*     */   public static synchronized AnnotationType getInstance(Class<? extends Annotation> paramClass)
/*     */   {
/*  81 */     AnnotationType localAnnotationType = SharedSecrets.getJavaLangAccess().getAnnotationType(paramClass);
/*     */ 
/*  83 */     if (localAnnotationType == null) {
/*  84 */       localAnnotationType = new AnnotationType(paramClass);
/*     */     }
/*  86 */     return localAnnotationType;
/*     */   }
/*     */ 
/*     */   private AnnotationType(final Class<? extends Annotation> paramClass)
/*     */   {
/*  97 */     if (!paramClass.isAnnotation()) {
/*  98 */       throw new IllegalArgumentException("Not an annotation type");
/*     */     }
/* 100 */     Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Method[] run()
/*     */       {
/* 104 */         return paramClass.getDeclaredMethods();
/*     */       }
/*     */     });
/* 109 */     for (Object localObject2 : arrayOfMethod) {
/* 110 */       if (localObject2.getParameterTypes().length != 0)
/* 111 */         throw new IllegalArgumentException(localObject2 + " has params");
/* 112 */       String str = localObject2.getName();
/* 113 */       Class localClass = localObject2.getReturnType();
/* 114 */       this.memberTypes.put(str, invocationHandlerReturnType(localClass));
/* 115 */       this.members.put(str, localObject2);
/*     */ 
/* 117 */       Object localObject3 = localObject2.getDefaultValue();
/* 118 */       if (localObject3 != null) {
/* 119 */         this.memberDefaults.put(str, localObject3);
/*     */       }
/* 121 */       this.members.put(str, localObject2);
/*     */     }
/*     */ 
/* 124 */     SharedSecrets.getJavaLangAccess().setAnnotationType(paramClass, this);
/*     */ 
/* 129 */     if ((paramClass != Retention.class) && (paramClass != Inherited.class))
/*     */     {
/* 131 */       ??? = (Retention)paramClass.getAnnotation(Retention.class);
/* 132 */       this.retention = (??? == null ? RetentionPolicy.CLASS : ((Retention)???).value());
/* 133 */       this.inherited = paramClass.isAnnotationPresent(Inherited.class);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Class<?> invocationHandlerReturnType(Class<?> paramClass)
/*     */   {
/* 145 */     if (paramClass == Byte.TYPE)
/* 146 */       return Byte.class;
/* 147 */     if (paramClass == Character.TYPE)
/* 148 */       return Character.class;
/* 149 */     if (paramClass == Double.TYPE)
/* 150 */       return Double.class;
/* 151 */     if (paramClass == Float.TYPE)
/* 152 */       return Float.class;
/* 153 */     if (paramClass == Integer.TYPE)
/* 154 */       return Integer.class;
/* 155 */     if (paramClass == Long.TYPE)
/* 156 */       return Long.class;
/* 157 */     if (paramClass == Short.TYPE)
/* 158 */       return Short.class;
/* 159 */     if (paramClass == Boolean.TYPE) {
/* 160 */       return Boolean.class;
/*     */     }
/*     */ 
/* 163 */     return paramClass;
/*     */   }
/*     */ 
/*     */   public Map<String, Class<?>> memberTypes()
/*     */   {
/* 171 */     return this.memberTypes;
/*     */   }
/*     */ 
/*     */   public Map<String, Method> members()
/*     */   {
/* 179 */     return this.members;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> memberDefaults()
/*     */   {
/* 187 */     return this.memberDefaults;
/*     */   }
/*     */ 
/*     */   public RetentionPolicy retention()
/*     */   {
/* 194 */     return this.retention;
/*     */   }
/*     */ 
/*     */   public boolean isInherited()
/*     */   {
/* 201 */     return this.inherited;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 208 */     StringBuffer localStringBuffer = new StringBuffer("Annotation Type:\n");
/* 209 */     localStringBuffer.append("   Member types: " + this.memberTypes + "\n");
/* 210 */     localStringBuffer.append("   Member defaults: " + this.memberDefaults + "\n");
/* 211 */     localStringBuffer.append("   Retention policy: " + this.retention + "\n");
/* 212 */     localStringBuffer.append("   Inherited: " + this.inherited);
/* 213 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.annotation.AnnotationType
 * JD-Core Version:    0.6.2
 */