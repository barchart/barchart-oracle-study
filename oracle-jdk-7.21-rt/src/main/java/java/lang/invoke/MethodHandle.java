/*      */ package java.lang.invoke;
/*      */ 
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.annotation.Retention;
/*      */ import java.lang.annotation.RetentionPolicy;
/*      */ import java.lang.annotation.Target;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import sun.invoke.util.ValueConversions;
/*      */ 
/*      */ public abstract class MethodHandle
/*      */ {
/*      */   private byte vmentry;
/*      */   Object vmtarget;
/*      */   static final int INT_FIELD = 0;
/*      */   static final long LONG_FIELD = 0L;
/*      */   private MethodType type;
/*      */ 
/*      */   public MethodType type()
/*      */   {
/*  442 */     return this.type;
/*      */   }
/*      */ 
/*      */   MethodHandle(MethodType paramMethodType)
/*      */   {
/*  452 */     paramMethodType.getClass();
/*  453 */     this.type = paramMethodType;
/*      */   }
/*      */ 
/*      */   @PolymorphicSignature
/*      */   public final native Object invokeExact(Object[] paramArrayOfObject)
/*      */     throws Throwable;
/*      */ 
/*      */   @PolymorphicSignature
/*      */   public final native Object invoke(Object[] paramArrayOfObject)
/*      */     throws Throwable;
/*      */ 
/*      */   public Object invokeWithArguments(Object[] paramArrayOfObject)
/*      */     throws Throwable
/*      */   {
/*  559 */     int i = paramArrayOfObject == null ? 0 : paramArrayOfObject.length;
/*  560 */     MethodType localMethodType = type();
/*  561 */     if ((localMethodType.parameterCount() != i) || (isVarargsCollector()))
/*      */     {
/*  563 */       return asType(MethodType.genericMethodType(i)).invokeWithArguments(paramArrayOfObject);
/*      */     }
/*  565 */     MethodHandle localMethodHandle = localMethodType.invokers().varargsInvoker();
/*  566 */     return localMethodHandle.invokeExact(this, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public Object invokeWithArguments(List<?> paramList)
/*      */     throws Throwable
/*      */   {
/*  588 */     return invokeWithArguments(paramList.toArray());
/*      */   }
/*      */ 
/*      */   public MethodHandle asType(MethodType paramMethodType)
/*      */   {
/*  690 */     if (!this.type.isConvertibleTo(paramMethodType)) {
/*  691 */       throw new WrongMethodTypeException("cannot convert " + this + " to " + paramMethodType);
/*      */     }
/*  693 */     return MethodHandleImpl.convertArguments(this, paramMethodType, 1);
/*      */   }
/*      */ 
/*      */   public MethodHandle asSpreader(Class<?> paramClass, int paramInt)
/*      */   {
/*  774 */     asSpreaderChecks(paramClass, paramInt);
/*  775 */     return MethodHandleImpl.spreadArguments(this, paramClass, paramInt);
/*      */   }
/*      */ 
/*      */   private void asSpreaderChecks(Class<?> paramClass, int paramInt) {
/*  779 */     spreadArrayChecks(paramClass, paramInt);
/*  780 */     int i = type().parameterCount();
/*  781 */     if ((i < paramInt) || (paramInt < 0))
/*  782 */       throw MethodHandleStatics.newIllegalArgumentException("bad spread array length");
/*  783 */     if ((paramClass != [Ljava.lang.Object.class) && (paramInt != 0)) {
/*  784 */       int j = 0;
/*  785 */       Class localClass = paramClass.getComponentType();
/*  786 */       for (int k = i - paramInt; k < i; k++) {
/*  787 */         if (!MethodType.canConvert(localClass, type().parameterType(k))) {
/*  788 */           j = 1;
/*  789 */           break;
/*      */         }
/*      */       }
/*  792 */       if (j != 0) {
/*  793 */         ArrayList localArrayList = new ArrayList(type().parameterList());
/*  794 */         for (int m = i - paramInt; m < i; m++) {
/*  795 */           localArrayList.set(m, localClass);
/*      */         }
/*      */ 
/*  798 */         asType(MethodType.methodType(type().returnType(), localArrayList));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void spreadArrayChecks(Class<?> paramClass, int paramInt) {
/*  804 */     Class localClass = paramClass.getComponentType();
/*  805 */     if (localClass == null)
/*  806 */       throw MethodHandleStatics.newIllegalArgumentException("not an array type", paramClass);
/*  807 */     if ((paramInt & 0x7F) != paramInt) {
/*  808 */       if ((paramInt & 0xFF) != paramInt)
/*  809 */         throw MethodHandleStatics.newIllegalArgumentException("array length is not legal", Integer.valueOf(paramInt));
/*  810 */       assert (paramInt >= 128);
/*  811 */       if ((localClass == Long.TYPE) || (localClass == Double.TYPE))
/*      */       {
/*  813 */         throw MethodHandleStatics.newIllegalArgumentException("array length is not legal for long[] or double[]", Integer.valueOf(paramInt));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public MethodHandle asCollector(Class<?> paramClass, int paramInt)
/*      */   {
/*  887 */     asCollectorChecks(paramClass, paramInt);
/*  888 */     MethodHandle localMethodHandle = ValueConversions.varargsArray(paramClass, paramInt);
/*  889 */     return MethodHandleImpl.collectArguments(this, this.type.parameterCount() - 1, localMethodHandle);
/*      */   }
/*      */ 
/*      */   private boolean asCollectorChecks(Class<?> paramClass, int paramInt)
/*      */   {
/*  894 */     spreadArrayChecks(paramClass, paramInt);
/*  895 */     int i = type().parameterCount();
/*  896 */     if (i != 0) {
/*  897 */       Class localClass = type().parameterType(i - 1);
/*  898 */       if (localClass == paramClass) return true;
/*  899 */       if (localClass.isAssignableFrom(paramClass)) return false;
/*      */     }
/*  901 */     throw MethodHandleStatics.newIllegalArgumentException("array type not assignable to trailing argument", this, paramClass);
/*      */   }
/*      */ 
/*      */   public MethodHandle asVarargsCollector(Class<?> paramClass)
/*      */   {
/* 1055 */     Class localClass = paramClass.getComponentType();
/* 1056 */     boolean bool = asCollectorChecks(paramClass, 0);
/* 1057 */     if ((isVarargsCollector()) && (bool))
/* 1058 */       return this;
/* 1059 */     return AdapterMethodHandle.makeVarargsCollector(this, paramClass);
/*      */   }
/*      */ 
/*      */   public boolean isVarargsCollector()
/*      */   {
/* 1078 */     return false;
/*      */   }
/*      */ 
/*      */   public MethodHandle asFixedArity()
/*      */   {
/* 1125 */     assert (!isVarargsCollector());
/* 1126 */     return this;
/*      */   }
/*      */ 
/*      */   public MethodHandle bindTo(Object paramObject)
/*      */   {
/*      */     Class localClass;
/* 1158 */     if ((type().parameterCount() == 0) || ((localClass = type().parameterType(0)).isPrimitive()))
/*      */     {
/* 1160 */       throw MethodHandleStatics.newIllegalArgumentException("no leading reference parameter", paramObject);
/* 1161 */     }paramObject = MethodHandles.checkValue(localClass, paramObject);
/*      */ 
/* 1163 */     MethodHandle localMethodHandle = MethodHandleImpl.bindReceiver(this, paramObject);
/* 1164 */     if (localMethodHandle != null) return localMethodHandle;
/* 1165 */     return MethodHandleImpl.bindArgument(this, 0, paramObject);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1185 */     if (MethodHandleStatics.DEBUG_METHOD_HANDLE_NAMES) return debugString();
/* 1186 */     return "MethodHandle" + this.type;
/*      */   }
/*      */ 
/*      */   String debugString()
/*      */   {
/* 1191 */     return MethodHandleStatics.getNameString(this);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  421 */     MethodHandleImpl.initStatics();
/*      */   }
/*      */ 
/*      */   @Target({java.lang.annotation.ElementType.METHOD})
/*      */   @Retention(RetentionPolicy.RUNTIME)
/*      */   static @interface PolymorphicSignature
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.MethodHandle
 * JD-Core Version:    0.6.2
 */