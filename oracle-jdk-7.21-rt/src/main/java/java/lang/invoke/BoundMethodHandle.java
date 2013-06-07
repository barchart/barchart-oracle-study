/*     */ package java.lang.invoke;
/*     */ 
/*     */ import sun.invoke.util.VerifyType;
/*     */ import sun.invoke.util.Wrapper;
/*     */ 
/*     */ class BoundMethodHandle extends MethodHandle
/*     */ {
/*     */   private final Object argument;
/*     */   private final int vmargslot;
/*     */ 
/*     */   BoundMethodHandle(DirectMethodHandle paramDirectMethodHandle, Object paramObject)
/*     */   {
/*  49 */     super(paramDirectMethodHandle.type().dropParameterTypes(0, 1));
/*     */ 
/*  51 */     this.argument = checkReferenceArgument(paramObject, paramDirectMethodHandle, 0);
/*  52 */     this.vmargslot = type().parameterSlotCount();
/*  53 */     initTarget(paramDirectMethodHandle, 0);
/*     */   }
/*     */ 
/*     */   BoundMethodHandle(MethodHandle paramMethodHandle, Object paramObject, int paramInt)
/*     */   {
/*  61 */     this(paramMethodHandle.type().dropParameterTypes(paramInt, paramInt + 1), paramMethodHandle, paramObject, paramInt);
/*     */   }
/*     */ 
/*     */   BoundMethodHandle(MethodType paramMethodType, MethodHandle paramMethodHandle, Object paramObject, int paramInt)
/*     */   {
/*  69 */     super(paramMethodType);
/*  70 */     if (paramMethodHandle.type().parameterType(paramInt).isPrimitive())
/*  71 */       this.argument = bindPrimitiveArgument(paramObject, paramMethodHandle, paramInt);
/*     */     else {
/*  73 */       this.argument = checkReferenceArgument(paramObject, paramMethodHandle, paramInt);
/*     */     }
/*  75 */     this.vmargslot = paramMethodType.parameterSlotDepth(paramInt);
/*  76 */     initTarget(paramMethodHandle, paramInt);
/*     */   }
/*     */ 
/*     */   private void initTarget(MethodHandle paramMethodHandle, int paramInt)
/*     */   {
/*  81 */     MethodHandleNatives.init(this, paramMethodHandle, paramInt);
/*     */   }
/*     */ 
/*     */   BoundMethodHandle(MethodType paramMethodType, Object paramObject, int paramInt)
/*     */   {
/*  87 */     super(paramMethodType);
/*  88 */     this.argument = paramObject;
/*  89 */     this.vmargslot = paramInt;
/*  90 */     assert ((this instanceof AdapterMethodHandle));
/*     */   }
/*     */ 
/*     */   BoundMethodHandle(MethodHandle paramMethodHandle)
/*     */   {
/* 100 */     super(paramMethodHandle.type().dropParameterTypes(0, 1));
/* 101 */     this.argument = this;
/* 102 */     this.vmargslot = type().parameterSlotDepth(0);
/* 103 */     initTarget(paramMethodHandle, 0);
/*     */   }
/*     */ 
/*     */   static final Object checkReferenceArgument(Object paramObject, MethodHandle paramMethodHandle, int paramInt)
/*     */   {
/* 114 */     Class localClass = paramMethodHandle.type().parameterType(paramInt);
/* 115 */     if (!localClass.isPrimitive())
/*     */     {
/* 117 */       if (paramObject == null)
/* 118 */         return null;
/* 119 */       if (VerifyType.isNullReferenceConversion(paramObject.getClass(), localClass))
/* 120 */         return paramObject;
/*     */     }
/* 122 */     throw badBoundArgumentException(paramObject, paramMethodHandle, paramInt);
/*     */   }
/*     */ 
/*     */   static final Object bindPrimitiveArgument(Object paramObject, MethodHandle paramMethodHandle, int paramInt)
/*     */   {
/* 133 */     Class localClass = paramMethodHandle.type().parameterType(paramInt);
/* 134 */     Wrapper localWrapper = Wrapper.forPrimitiveType(localClass);
/* 135 */     Object localObject = localWrapper.zero();
/* 136 */     if (localObject != null)
/*     */     {
/* 138 */       if (paramObject == null) {
/* 139 */         if ((localClass != Integer.TYPE) && (localWrapper.isSubwordOrInt())) {
/* 140 */           return Integer.valueOf(0);
/*     */         }
/* 142 */         return localObject;
/* 143 */       }if (VerifyType.isNullReferenceConversion(paramObject.getClass(), localObject.getClass())) {
/* 144 */         if ((localClass != Integer.TYPE) && (localWrapper.isSubwordOrInt())) {
/* 145 */           return Wrapper.INT.wrap(paramObject);
/*     */         }
/* 147 */         return paramObject;
/*     */       }
/*     */     }
/* 149 */     throw badBoundArgumentException(paramObject, paramMethodHandle, paramInt);
/*     */   }
/*     */ 
/*     */   static final RuntimeException badBoundArgumentException(Object paramObject, MethodHandle paramMethodHandle, int paramInt) {
/* 153 */     String str = paramObject == null ? "null" : paramObject.getClass().toString();
/* 154 */     return new ClassCastException("cannot bind " + str + " argument to parameter #" + paramInt + " of " + paramMethodHandle.type());
/*     */   }
/*     */ 
/*     */   String debugString()
/*     */   {
/* 159 */     return MethodHandleStatics.addTypeString(baseName(), this);
/*     */   }
/*     */ 
/*     */   protected String baseName()
/*     */   {
/* 164 */     Object localObject1 = this;
/* 165 */     while ((localObject1 instanceof BoundMethodHandle)) {
/* 166 */       Object localObject2 = MethodHandleNatives.getTargetInfo((MethodHandle)localObject1);
/* 167 */       if ((localObject2 instanceof MethodHandle)) {
/* 168 */         localObject1 = (MethodHandle)localObject2;
/*     */       } else {
/* 170 */         String str = null;
/* 171 */         if ((localObject2 instanceof MemberName))
/* 172 */           str = ((MemberName)localObject2).getName();
/* 173 */         if (str != null) {
/* 174 */           return str;
/*     */         }
/* 176 */         return noParens(super.toString());
/*     */       }
/* 178 */       assert (localObject1 != this);
/*     */     }
/* 180 */     return noParens(((MethodHandle)localObject1).toString());
/*     */   }
/*     */ 
/*     */   private static String noParens(String paramString) {
/* 184 */     int i = paramString.indexOf('(');
/* 185 */     if (i >= 0) paramString = paramString.substring(0, i);
/* 186 */     return paramString;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.lang.invoke.BoundMethodHandle
 * JD-Core Version:    0.6.2
 */