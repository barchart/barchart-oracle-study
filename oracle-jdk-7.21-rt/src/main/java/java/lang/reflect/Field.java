/*      */ package java.lang.reflect;
/*      */ 
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.util.Map;
/*      */ import sun.misc.JavaLangAccess;
/*      */ import sun.misc.SharedSecrets;
/*      */ import sun.reflect.FieldAccessor;
/*      */ import sun.reflect.Reflection;
/*      */ import sun.reflect.ReflectionFactory;
/*      */ import sun.reflect.annotation.AnnotationParser;
/*      */ import sun.reflect.generics.factory.CoreReflectionFactory;
/*      */ import sun.reflect.generics.factory.GenericsFactory;
/*      */ import sun.reflect.generics.repository.FieldRepository;
/*      */ import sun.reflect.generics.scope.ClassScope;
/*      */ 
/*      */ public final class Field extends AccessibleObject
/*      */   implements Member
/*      */ {
/*      */   private Class<?> clazz;
/*      */   private int slot;
/*      */   private String name;
/*      */   private Class<?> type;
/*      */   private int modifiers;
/*      */   private transient String signature;
/*      */   private transient FieldRepository genericInfo;
/*      */   private byte[] annotations;
/*      */   private FieldAccessor fieldAccessor;
/*      */   private FieldAccessor overrideFieldAccessor;
/*      */   private Field root;
/*      */   private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
/*      */ 
/*      */   private String getGenericSignature()
/*      */   {
/*   84 */     return this.signature;
/*      */   }
/*      */ 
/*      */   private GenericsFactory getFactory() {
/*   88 */     Class localClass = getDeclaringClass();
/*      */ 
/*   90 */     return CoreReflectionFactory.make(localClass, ClassScope.make(localClass));
/*      */   }
/*      */ 
/*      */   private FieldRepository getGenericInfo()
/*      */   {
/*   96 */     if (this.genericInfo == null)
/*      */     {
/*   98 */       this.genericInfo = FieldRepository.make(getGenericSignature(), getFactory());
/*      */     }
/*      */ 
/*  101 */     return this.genericInfo;
/*      */   }
/*      */ 
/*      */   Field(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte)
/*      */   {
/*  118 */     this.clazz = paramClass1;
/*  119 */     this.name = paramString1;
/*  120 */     this.type = paramClass2;
/*  121 */     this.modifiers = paramInt1;
/*  122 */     this.slot = paramInt2;
/*  123 */     this.signature = paramString2;
/*  124 */     this.annotations = paramArrayOfByte;
/*      */   }
/*      */ 
/*      */   Field copy()
/*      */   {
/*  140 */     Field localField = new Field(this.clazz, this.name, this.type, this.modifiers, this.slot, this.signature, this.annotations);
/*  141 */     localField.root = this;
/*      */ 
/*  143 */     localField.fieldAccessor = this.fieldAccessor;
/*  144 */     localField.overrideFieldAccessor = this.overrideFieldAccessor;
/*  145 */     return localField;
/*      */   }
/*      */ 
/*      */   public Class<?> getDeclaringClass()
/*      */   {
/*  153 */     return this.clazz;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  160 */     return this.name;
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  171 */     return this.modifiers;
/*      */   }
/*      */ 
/*      */   public boolean isEnumConstant()
/*      */   {
/*  183 */     return (getModifiers() & 0x4000) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isSynthetic()
/*      */   {
/*  195 */     return Modifier.isSynthetic(getModifiers());
/*      */   }
/*      */ 
/*      */   public Class<?> getType()
/*      */   {
/*  207 */     return this.type;
/*      */   }
/*      */ 
/*      */   public Type getGenericType()
/*      */   {
/*  235 */     if (getGenericSignature() != null) {
/*  236 */       return getGenericInfo().getGenericType();
/*      */     }
/*  238 */     return getType();
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  249 */     if ((paramObject != null) && ((paramObject instanceof Field))) {
/*  250 */       Field localField = (Field)paramObject;
/*  251 */       return (getDeclaringClass() == localField.getDeclaringClass()) && (getName() == localField.getName()) && (getType() == localField.getType());
/*      */     }
/*      */ 
/*  255 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  264 */     return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  286 */     int i = getModifiers();
/*  287 */     return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + getTypeName(getType()) + " " + getTypeName(getDeclaringClass()) + "." + getName();
/*      */   }
/*      */ 
/*      */   public String toGenericString()
/*      */   {
/*  313 */     int i = getModifiers();
/*  314 */     Type localType = getGenericType();
/*  315 */     return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + ((localType instanceof Class) ? getTypeName((Class)localType) : localType.toString()) + " " + getTypeName(getDeclaringClass()) + "." + getName();
/*      */   }
/*      */ 
/*      */   public Object get(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  372 */     return getFieldAccessor(paramObject).get(paramObject);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  400 */     return getFieldAccessor(paramObject).getBoolean(paramObject);
/*      */   }
/*      */ 
/*      */   public byte getByte(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  428 */     return getFieldAccessor(paramObject).getByte(paramObject);
/*      */   }
/*      */ 
/*      */   public char getChar(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  458 */     return getFieldAccessor(paramObject).getChar(paramObject);
/*      */   }
/*      */ 
/*      */   public short getShort(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  488 */     return getFieldAccessor(paramObject).getShort(paramObject);
/*      */   }
/*      */ 
/*      */   public int getInt(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  518 */     return getFieldAccessor(paramObject).getInt(paramObject);
/*      */   }
/*      */ 
/*      */   public long getLong(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  548 */     return getFieldAccessor(paramObject).getLong(paramObject);
/*      */   }
/*      */ 
/*      */   public float getFloat(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  578 */     return getFieldAccessor(paramObject).getFloat(paramObject);
/*      */   }
/*      */ 
/*      */   public double getDouble(Object paramObject)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  608 */     return getFieldAccessor(paramObject).getDouble(paramObject);
/*      */   }
/*      */ 
/*      */   public void set(Object paramObject1, Object paramObject2)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  680 */     getFieldAccessor(paramObject1).set(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public void setBoolean(Object paramObject, boolean paramBoolean)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  710 */     getFieldAccessor(paramObject).setBoolean(paramObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   public void setByte(Object paramObject, byte paramByte)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  740 */     getFieldAccessor(paramObject).setByte(paramObject, paramByte);
/*      */   }
/*      */ 
/*      */   public void setChar(Object paramObject, char paramChar)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  770 */     getFieldAccessor(paramObject).setChar(paramObject, paramChar);
/*      */   }
/*      */ 
/*      */   public void setShort(Object paramObject, short paramShort)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  800 */     getFieldAccessor(paramObject).setShort(paramObject, paramShort);
/*      */   }
/*      */ 
/*      */   public void setInt(Object paramObject, int paramInt)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  830 */     getFieldAccessor(paramObject).setInt(paramObject, paramInt);
/*      */   }
/*      */ 
/*      */   public void setLong(Object paramObject, long paramLong)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  860 */     getFieldAccessor(paramObject).setLong(paramObject, paramLong);
/*      */   }
/*      */ 
/*      */   public void setFloat(Object paramObject, float paramFloat)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  890 */     getFieldAccessor(paramObject).setFloat(paramObject, paramFloat);
/*      */   }
/*      */ 
/*      */   public void setDouble(Object paramObject, double paramDouble)
/*      */     throws IllegalArgumentException, IllegalAccessException
/*      */   {
/*  920 */     getFieldAccessor(paramObject).setDouble(paramObject, paramDouble);
/*      */   }
/*      */ 
/*      */   private FieldAccessor getFieldAccessor(Object paramObject)
/*      */     throws IllegalAccessException
/*      */   {
/*  927 */     doSecurityCheck(paramObject);
/*  928 */     boolean bool = this.override;
/*  929 */     FieldAccessor localFieldAccessor = bool ? this.overrideFieldAccessor : this.fieldAccessor;
/*  930 */     return localFieldAccessor != null ? localFieldAccessor : acquireFieldAccessor(bool);
/*      */   }
/*      */ 
/*      */   private FieldAccessor acquireFieldAccessor(boolean paramBoolean)
/*      */   {
/*  940 */     FieldAccessor localFieldAccessor = null;
/*  941 */     if (this.root != null) localFieldAccessor = this.root.getFieldAccessor(paramBoolean);
/*  942 */     if (localFieldAccessor != null) {
/*  943 */       if (paramBoolean)
/*  944 */         this.overrideFieldAccessor = localFieldAccessor;
/*      */       else
/*  946 */         this.fieldAccessor = localFieldAccessor;
/*      */     }
/*      */     else {
/*  949 */       localFieldAccessor = reflectionFactory.newFieldAccessor(this, paramBoolean);
/*  950 */       setFieldAccessor(localFieldAccessor, paramBoolean);
/*      */     }
/*      */ 
/*  953 */     return localFieldAccessor;
/*      */   }
/*      */ 
/*      */   private FieldAccessor getFieldAccessor(boolean paramBoolean)
/*      */   {
/*  959 */     return paramBoolean ? this.overrideFieldAccessor : this.fieldAccessor;
/*      */   }
/*      */ 
/*      */   private void setFieldAccessor(FieldAccessor paramFieldAccessor, boolean paramBoolean)
/*      */   {
/*  965 */     if (paramBoolean)
/*  966 */       this.overrideFieldAccessor = paramFieldAccessor;
/*      */     else {
/*  968 */       this.fieldAccessor = paramFieldAccessor;
/*      */     }
/*  970 */     if (this.root != null)
/*  971 */       this.root.setFieldAccessor(paramFieldAccessor, paramBoolean);
/*      */   }
/*      */ 
/*      */   private void doSecurityCheck(Object paramObject)
/*      */     throws IllegalAccessException
/*      */   {
/*  979 */     if ((!this.override) && 
/*  980 */       (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers))) {
/*  981 */       Class localClass = Reflection.getCallerClass(4);
/*      */ 
/*  983 */       checkAccess(localClass, this.clazz, paramObject, this.modifiers);
/*      */     }
/*      */   }
/*      */ 
/*      */   static String getTypeName(Class<?> paramClass)
/*      */   {
/*  992 */     if (paramClass.isArray())
/*      */       try {
/*  994 */         Object localObject = paramClass;
/*  995 */         int i = 0;
/*  996 */         while (((Class)localObject).isArray()) {
/*  997 */           i++;
/*  998 */           localObject = ((Class)localObject).getComponentType();
/*      */         }
/* 1000 */         StringBuffer localStringBuffer = new StringBuffer();
/* 1001 */         localStringBuffer.append(((Class)localObject).getName());
/* 1002 */         for (int j = 0; j < i; j++) {
/* 1003 */           localStringBuffer.append("[]");
/*      */         }
/* 1005 */         return localStringBuffer.toString();
/*      */       } catch (Throwable localThrowable) {
/*      */       }
/* 1008 */     return paramClass.getName();
/*      */   }
/*      */ 
/*      */   public <T extends Annotation> T getAnnotation(Class<T> paramClass)
/*      */   {
/* 1016 */     if (paramClass == null) {
/* 1017 */       throw new NullPointerException();
/*      */     }
/* 1019 */     return (Annotation)declaredAnnotations().get(paramClass);
/*      */   }
/*      */ 
/*      */   public Annotation[] getDeclaredAnnotations()
/*      */   {
/* 1026 */     return AnnotationParser.toArray(declaredAnnotations());
/*      */   }
/*      */ 
/*      */   private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
/*      */   {
/* 1032 */     if (this.declaredAnnotations == null) {
/* 1033 */       this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
/*      */     }
/*      */ 
/* 1038 */     return this.declaredAnnotations;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.reflect.Field
 * JD-Core Version:    0.6.2
 */