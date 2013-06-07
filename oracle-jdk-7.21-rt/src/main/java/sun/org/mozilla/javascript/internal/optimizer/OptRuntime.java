/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ import sun.org.mozilla.javascript.internal.Callable;
/*     */ import sun.org.mozilla.javascript.internal.Context;
/*     */ import sun.org.mozilla.javascript.internal.ContextAction;
/*     */ import sun.org.mozilla.javascript.internal.ContextFactory;
/*     */ import sun.org.mozilla.javascript.internal.JavaScriptException;
/*     */ import sun.org.mozilla.javascript.internal.NativeFunction;
/*     */ import sun.org.mozilla.javascript.internal.NativeGenerator;
/*     */ import sun.org.mozilla.javascript.internal.NativeIterator;
/*     */ import sun.org.mozilla.javascript.internal.Script;
/*     */ import sun.org.mozilla.javascript.internal.ScriptRuntime;
/*     */ import sun.org.mozilla.javascript.internal.Scriptable;
/*     */ import sun.org.mozilla.javascript.internal.ScriptableObject;
/*     */ 
/*     */ public final class OptRuntime extends ScriptRuntime
/*     */ {
/*  47 */   public static final Double zeroObj = new Double(0.0D);
/*  48 */   public static final Double oneObj = new Double(1.0D);
/*  49 */   public static final Double minusOneObj = new Double(-1.0D);
/*     */ 
/*     */   public static Object call0(Callable paramCallable, Scriptable paramScriptable1, Context paramContext, Scriptable paramScriptable2)
/*     */   {
/*  57 */     return paramCallable.call(paramContext, paramScriptable2, paramScriptable1, ScriptRuntime.emptyArgs);
/*     */   }
/*     */ 
/*     */   public static Object call1(Callable paramCallable, Scriptable paramScriptable1, Object paramObject, Context paramContext, Scriptable paramScriptable2)
/*     */   {
/*  66 */     return paramCallable.call(paramContext, paramScriptable2, paramScriptable1, new Object[] { paramObject });
/*     */   }
/*     */ 
/*     */   public static Object call2(Callable paramCallable, Scriptable paramScriptable1, Object paramObject1, Object paramObject2, Context paramContext, Scriptable paramScriptable2)
/*     */   {
/*  76 */     return paramCallable.call(paramContext, paramScriptable2, paramScriptable1, new Object[] { paramObject1, paramObject2 });
/*     */   }
/*     */ 
/*     */   public static Object callN(Callable paramCallable, Scriptable paramScriptable1, Object[] paramArrayOfObject, Context paramContext, Scriptable paramScriptable2)
/*     */   {
/*  86 */     return paramCallable.call(paramContext, paramScriptable2, paramScriptable1, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public static Object callName(Object[] paramArrayOfObject, String paramString, Context paramContext, Scriptable paramScriptable)
/*     */   {
/*  95 */     Callable localCallable = getNameFunctionAndThis(paramString, paramContext, paramScriptable);
/*  96 */     Scriptable localScriptable = lastStoredScriptable(paramContext);
/*  97 */     return localCallable.call(paramContext, paramScriptable, localScriptable, paramArrayOfObject);
/*     */   }
/*     */ 
/*     */   public static Object callName0(String paramString, Context paramContext, Scriptable paramScriptable)
/*     */   {
/* 106 */     Callable localCallable = getNameFunctionAndThis(paramString, paramContext, paramScriptable);
/* 107 */     Scriptable localScriptable = lastStoredScriptable(paramContext);
/* 108 */     return localCallable.call(paramContext, paramScriptable, localScriptable, ScriptRuntime.emptyArgs);
/*     */   }
/*     */ 
/*     */   public static Object callProp0(Object paramObject, String paramString, Context paramContext, Scriptable paramScriptable)
/*     */   {
/* 117 */     Callable localCallable = getPropFunctionAndThis(paramObject, paramString, paramContext, paramScriptable);
/* 118 */     Scriptable localScriptable = lastStoredScriptable(paramContext);
/* 119 */     return localCallable.call(paramContext, paramScriptable, localScriptable, ScriptRuntime.emptyArgs);
/*     */   }
/*     */ 
/*     */   public static Object add(Object paramObject, double paramDouble)
/*     */   {
/* 124 */     if ((paramObject instanceof Scriptable))
/* 125 */       paramObject = ((Scriptable)paramObject).getDefaultValue(null);
/* 126 */     if (!(paramObject instanceof String))
/* 127 */       return wrapDouble(toNumber(paramObject) + paramDouble);
/* 128 */     return ((String)paramObject).concat(toString(paramDouble));
/*     */   }
/*     */ 
/*     */   public static Object add(double paramDouble, Object paramObject)
/*     */   {
/* 133 */     if ((paramObject instanceof Scriptable))
/* 134 */       paramObject = ((Scriptable)paramObject).getDefaultValue(null);
/* 135 */     if (!(paramObject instanceof String))
/* 136 */       return wrapDouble(toNumber(paramObject) + paramDouble);
/* 137 */     return toString(paramDouble).concat((String)paramObject);
/*     */   }
/*     */ 
/*     */   public static Object elemIncrDecr(Object paramObject, double paramDouble, Context paramContext, int paramInt)
/*     */   {
/* 143 */     return ScriptRuntime.elemIncrDecr(paramObject, new Double(paramDouble), paramContext, paramInt);
/*     */   }
/*     */ 
/*     */   public static Object[] padStart(Object[] paramArrayOfObject, int paramInt)
/*     */   {
/* 148 */     Object[] arrayOfObject = new Object[paramArrayOfObject.length + paramInt];
/* 149 */     System.arraycopy(paramArrayOfObject, 0, arrayOfObject, paramInt, paramArrayOfObject.length);
/* 150 */     return arrayOfObject;
/*     */   }
/*     */ 
/*     */   public static void initFunction(NativeFunction paramNativeFunction, int paramInt, Scriptable paramScriptable, Context paramContext)
/*     */   {
/* 156 */     ScriptRuntime.initFunction(paramContext, paramScriptable, paramNativeFunction, paramInt, false);
/*     */   }
/*     */ 
/*     */   public static Object callSpecial(Context paramContext, Callable paramCallable, Scriptable paramScriptable1, Object[] paramArrayOfObject, Scriptable paramScriptable2, Scriptable paramScriptable3, int paramInt1, String paramString, int paramInt2)
/*     */   {
/* 165 */     return ScriptRuntime.callSpecial(paramContext, paramCallable, paramScriptable1, paramArrayOfObject, paramScriptable2, paramScriptable3, paramInt1, paramString, paramInt2);
/*     */   }
/*     */ 
/*     */   public static Object newObjectSpecial(Context paramContext, Object paramObject, Object[] paramArrayOfObject, Scriptable paramScriptable1, Scriptable paramScriptable2, int paramInt)
/*     */   {
/* 174 */     return ScriptRuntime.newSpecial(paramContext, paramObject, paramArrayOfObject, paramScriptable1, paramInt);
/*     */   }
/*     */ 
/*     */   public static Double wrapDouble(double paramDouble)
/*     */   {
/* 179 */     if (paramDouble == 0.0D) {
/* 180 */       if (1.0D / paramDouble > 0.0D)
/*     */       {
/* 182 */         return zeroObj;
/*     */       }
/*     */     } else { if (paramDouble == 1.0D)
/* 185 */         return oneObj;
/* 186 */       if (paramDouble == -1.0D)
/* 187 */         return minusOneObj;
/* 188 */       if (paramDouble != paramDouble)
/* 189 */         return NaNobj;
/*     */     }
/* 191 */     return new Double(paramDouble);
/*     */   }
/*     */ 
/*     */   static String encodeIntArray(int[] paramArrayOfInt)
/*     */   {
/* 197 */     if (paramArrayOfInt == null) return null;
/* 198 */     int i = paramArrayOfInt.length;
/* 199 */     char[] arrayOfChar = new char[1 + i * 2];
/* 200 */     arrayOfChar[0] = '\001';
/* 201 */     for (int j = 0; j != i; j++) {
/* 202 */       int k = paramArrayOfInt[j];
/* 203 */       int m = 1 + j * 2;
/* 204 */       arrayOfChar[m] = ((char)(k >>> 16));
/* 205 */       arrayOfChar[(m + 1)] = ((char)k);
/*     */     }
/* 207 */     return new String(arrayOfChar);
/*     */   }
/*     */ 
/*     */   private static int[] decodeIntArray(String paramString, int paramInt)
/*     */   {
/* 213 */     if (paramInt == 0) {
/* 214 */       if (paramString != null) throw new IllegalArgumentException();
/* 215 */       return null;
/*     */     }
/* 217 */     if ((paramString.length() != 1 + paramInt * 2) && (paramString.charAt(0) != '\001')) {
/* 218 */       throw new IllegalArgumentException();
/*     */     }
/* 220 */     int[] arrayOfInt = new int[paramInt];
/* 221 */     for (int i = 0; i != paramInt; i++) {
/* 222 */       int j = 1 + i * 2;
/* 223 */       arrayOfInt[i] = (paramString.charAt(j) << '\020' | paramString.charAt(j + 1));
/*     */     }
/* 225 */     return arrayOfInt;
/*     */   }
/*     */ 
/*     */   public static Scriptable newArrayLiteral(Object[] paramArrayOfObject, String paramString, int paramInt, Context paramContext, Scriptable paramScriptable)
/*     */   {
/* 234 */     int[] arrayOfInt = decodeIntArray(paramString, paramInt);
/* 235 */     return newArrayLiteral(paramArrayOfObject, arrayOfInt, paramContext, paramScriptable);
/*     */   }
/*     */ 
/*     */   public static void main(final Script paramScript, String[] paramArrayOfString)
/*     */   {
/* 240 */     ContextFactory.getGlobal().call(new ContextAction()
/*     */     {
/*     */       public Object run(Context paramAnonymousContext) {
/* 243 */         ScriptableObject localScriptableObject = ScriptRuntime.getGlobal(paramAnonymousContext);
/*     */ 
/* 247 */         Object[] arrayOfObject = new Object[this.val$args.length];
/* 248 */         System.arraycopy(this.val$args, 0, arrayOfObject, 0, this.val$args.length);
/* 249 */         Scriptable localScriptable = paramAnonymousContext.newArray(localScriptableObject, arrayOfObject);
/* 250 */         localScriptableObject.defineProperty("arguments", localScriptable, 2);
/*     */ 
/* 252 */         paramScript.exec(paramAnonymousContext, localScriptableObject);
/* 253 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static void throwStopIteration(Object paramObject) {
/* 259 */     throw new JavaScriptException(NativeIterator.getStopIterationObject((Scriptable)paramObject), "", 0);
/*     */   }
/*     */ 
/*     */   public static Scriptable createNativeGenerator(NativeFunction paramNativeFunction, Scriptable paramScriptable1, Scriptable paramScriptable2, int paramInt1, int paramInt2)
/*     */   {
/* 269 */     return new NativeGenerator(paramScriptable1, paramNativeFunction, new GeneratorState(paramScriptable2, paramInt1, paramInt2));
/*     */   }
/*     */ 
/*     */   public static Object[] getGeneratorStackState(Object paramObject)
/*     */   {
/* 274 */     GeneratorState localGeneratorState = (GeneratorState)paramObject;
/* 275 */     if (localGeneratorState.stackState == null)
/* 276 */       localGeneratorState.stackState = new Object[localGeneratorState.maxStack];
/* 277 */     return localGeneratorState.stackState;
/*     */   }
/*     */ 
/*     */   public static Object[] getGeneratorLocalsState(Object paramObject) {
/* 281 */     GeneratorState localGeneratorState = (GeneratorState)paramObject;
/* 282 */     if (localGeneratorState.localsState == null)
/* 283 */       localGeneratorState.localsState = new Object[localGeneratorState.maxLocals];
/* 284 */     return localGeneratorState.localsState; } 
/*     */   public static class GeneratorState { static final String CLASS_NAME = "sun/org/mozilla/javascript/internal/optimizer/OptRuntime$GeneratorState";
/*     */     public int resumptionPoint;
/*     */     static final String resumptionPoint_NAME = "resumptionPoint";
/*     */     static final String resumptionPoint_TYPE = "I";
/*     */     public Scriptable thisObj;
/*     */     static final String thisObj_NAME = "thisObj";
/*     */     static final String thisObj_TYPE = "Lsun/org/mozilla/javascript/internal/Scriptable;";
/*     */     Object[] stackState;
/*     */     Object[] localsState;
/*     */     int maxLocals;
/*     */     int maxStack;
/*     */ 
/* 306 */     GeneratorState(Scriptable paramScriptable, int paramInt1, int paramInt2) { this.thisObj = paramScriptable;
/* 307 */       this.maxLocals = paramInt1;
/* 308 */       this.maxStack = paramInt2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.OptRuntime
 * JD-Core Version:    0.6.2
 */