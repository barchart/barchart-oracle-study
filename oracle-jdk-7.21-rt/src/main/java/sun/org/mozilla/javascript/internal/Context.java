/*      */ package sun.org.mozilla.javascript.internal;
/*      */ 
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.CharArrayWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import sun.org.mozilla.javascript.internal.ast.AstRoot;
/*      */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*      */ import sun.org.mozilla.javascript.internal.debug.DebuggableScript;
/*      */ import sun.org.mozilla.javascript.internal.debug.Debugger;
/*      */ import sun.org.mozilla.javascript.internal.xml.XMLLib;
/*      */ import sun.org.mozilla.javascript.internal.xml.XMLLib.Factory;
/*      */ 
/*      */ public class Context
/*      */ {
/*      */   public static final int VERSION_UNKNOWN = -1;
/*      */   public static final int VERSION_DEFAULT = 0;
/*      */   public static final int VERSION_1_0 = 100;
/*      */   public static final int VERSION_1_1 = 110;
/*      */   public static final int VERSION_1_2 = 120;
/*      */   public static final int VERSION_1_3 = 130;
/*      */   public static final int VERSION_1_4 = 140;
/*      */   public static final int VERSION_1_5 = 150;
/*      */   public static final int VERSION_1_6 = 160;
/*      */   public static final int VERSION_1_7 = 170;
/*      */   public static final int VERSION_1_8 = 180;
/*      */   public static final int FEATURE_NON_ECMA_GET_YEAR = 1;
/*      */   public static final int FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME = 2;
/*      */   public static final int FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER = 3;
/*      */   public static final int FEATURE_TO_STRING_AS_SOURCE = 4;
/*      */   public static final int FEATURE_PARENT_PROTO_PROPERTIES = 5;
/*      */ 
/*      */   /** @deprecated */
/*      */   public static final int FEATURE_PARENT_PROTO_PROPRTIES = 5;
/*      */   public static final int FEATURE_E4X = 6;
/*      */   public static final int FEATURE_DYNAMIC_SCOPE = 7;
/*      */   public static final int FEATURE_STRICT_VARS = 8;
/*      */   public static final int FEATURE_STRICT_EVAL = 9;
/*      */   public static final int FEATURE_LOCATION_INFORMATION_IN_ERROR = 10;
/*      */   public static final int FEATURE_STRICT_MODE = 11;
/*      */   public static final int FEATURE_WARNING_AS_ERROR = 12;
/*      */   public static final int FEATURE_ENHANCED_JAVA_ACCESS = 13;
/*      */   public static final String languageVersionProperty = "language version";
/*      */   public static final String errorReporterProperty = "error reporter";
/*  324 */   public static final Object[] emptyArgs = ScriptRuntime.emptyArgs;
/*      */   private TimeZone thisContextTimeZone;
/*      */   private double LocalTZA;
/* 2487 */   private static Class<?> codegenClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.optimizer.Codegen");
/*      */ 
/* 2489 */   private static Class<?> interpreterClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.Interpreter");
/*      */   private static String implementationVersion;
/*      */   private final ContextFactory factory;
/*      */   private boolean sealed;
/*      */   private Object sealKey;
/*      */   Scriptable topCallScope;
/*      */   boolean isContinuationsTopCall;
/*      */   NativeCall currentActivationCall;
/*      */   XMLLib cachedXMLLib;
/*      */   ObjToIntMap iterating;
/*      */   Object interpreterSecurityDomain;
/*      */   int version;
/*      */   private SecurityController securityController;
/*      */   private boolean hasClassShutter;
/*      */   private ClassShutter classShutter;
/*      */   private ErrorReporter errorReporter;
/*      */   RegExpProxy regExpProxy;
/*      */   private Locale locale;
/*      */   private boolean generatingDebug;
/*      */   private boolean generatingDebugChanged;
/* 2662 */   private boolean generatingSource = true;
/*      */   boolean compileFunctionsWithDynamicScopeFlag;
/*      */   boolean useDynamicScope;
/*      */   private int optimizationLevel;
/*      */   private int maximumInterpreterStackDepth;
/*      */   private WrapFactory wrapFactory;
/*      */   Debugger debugger;
/*      */   private Object debuggerData;
/*      */   private int enterCount;
/*      */   private Object propertyListeners;
/*      */   private Map<Object, Object> threadLocalMap;
/*      */   private ClassLoader applicationClassLoader;
/*      */   Set<String> activationNames;
/*      */   Object lastInterpreterFrame;
/*      */   ObjArray previousInterpreterInvocations;
/*      */   int instructionCount;
/*      */   int instructionThreshold;
/*      */   int scratchIndex;
/*      */   long scratchUint32;
/*      */   Scriptable scratchScriptable;
/* 2702 */   public boolean generateObserverCount = false;
/*      */ 
/*      */   public void setTimeZone()
/*      */   {
/*  331 */     this.thisContextTimeZone = TimeZone.getDefault();
/*      */   }
/*      */   public TimeZone getTimeZone() {
/*  334 */     return this.thisContextTimeZone;
/*      */   }
/*      */   public void setLocalTZA() {
/*  337 */     this.LocalTZA = this.thisContextTimeZone.getRawOffset();
/*      */   }
/*      */   public double getLocalTZA() {
/*  340 */     return this.LocalTZA;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Context()
/*      */   {
/*  358 */     this(ContextFactory.getGlobal());
/*      */   }
/*      */ 
/*      */   protected Context(ContextFactory paramContextFactory)
/*      */   {
/*  372 */     if (paramContextFactory == null) {
/*  373 */       throw new IllegalArgumentException("factory == null");
/*      */     }
/*  375 */     this.factory = paramContextFactory;
/*  376 */     setLanguageVersion(0);
/*  377 */     this.optimizationLevel = (codegenClass != null ? 0 : -1);
/*  378 */     this.maximumInterpreterStackDepth = 2147483647;
/*      */   }
/*      */ 
/*      */   public static Context getCurrentContext()
/*      */   {
/*  395 */     Object localObject = VMBridge.instance.getThreadContextHelper();
/*  396 */     return VMBridge.instance.getContext(localObject);
/*      */   }
/*      */ 
/*      */   public static Context enter()
/*      */   {
/*  409 */     return enter(null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Context enter(Context paramContext)
/*      */   {
/*  429 */     return enter(paramContext, ContextFactory.getGlobal());
/*      */   }
/*      */ 
/*      */   static final Context enter(Context paramContext, ContextFactory paramContextFactory)
/*      */   {
/*  434 */     Object localObject = VMBridge.instance.getThreadContextHelper();
/*  435 */     Context localContext = VMBridge.instance.getContext(localObject);
/*  436 */     if (localContext != null) {
/*  437 */       paramContext = localContext;
/*      */     } else {
/*  439 */       if (paramContext == null) {
/*  440 */         paramContext = paramContextFactory.makeContext();
/*  441 */         if (paramContext.enterCount != 0) {
/*  442 */           throw new IllegalStateException("factory.makeContext() returned Context instance already associated with some thread");
/*      */         }
/*  444 */         paramContextFactory.onContextCreated(paramContext);
/*  445 */         if ((paramContextFactory.isSealed()) && (!paramContext.isSealed())) {
/*  446 */           paramContext.seal(null);
/*      */         }
/*      */       }
/*  449 */       else if (paramContext.enterCount != 0) {
/*  450 */         throw new IllegalStateException("can not use Context instance already associated with some thread");
/*      */       }
/*      */ 
/*  453 */       VMBridge.instance.setContext(localObject, paramContext);
/*      */     }
/*  455 */     paramContext.enterCount += 1;
/*  456 */     return paramContext;
/*      */   }
/*      */ 
/*      */   public static void exit()
/*      */   {
/*  472 */     Object localObject = VMBridge.instance.getThreadContextHelper();
/*  473 */     Context localContext = VMBridge.instance.getContext(localObject);
/*  474 */     if (localContext == null) {
/*  475 */       throw new IllegalStateException("Calling Context.exit without previous Context.enter");
/*      */     }
/*      */ 
/*  478 */     if (localContext.enterCount < 1) Kit.codeBug();
/*  479 */     if (--localContext.enterCount == 0) {
/*  480 */       VMBridge.instance.setContext(localObject, null);
/*  481 */       localContext.factory.onContextReleased(localContext);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Object call(ContextAction paramContextAction)
/*      */   {
/*  500 */     return call(ContextFactory.getGlobal(), paramContextAction);
/*      */   }
/*      */ 
/*      */   public static Object call(ContextFactory paramContextFactory, Callable paramCallable, final Scriptable paramScriptable1, final Scriptable paramScriptable2, final Object[] paramArrayOfObject)
/*      */   {
/*  522 */     if (paramContextFactory == null) {
/*  523 */       paramContextFactory = ContextFactory.getGlobal();
/*      */     }
/*  525 */     return call(paramContextFactory, new ContextAction() {
/*      */       public Object run(Context paramAnonymousContext) {
/*  527 */         return this.val$callable.call(paramAnonymousContext, paramScriptable1, paramScriptable2, paramArrayOfObject);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   static Object call(ContextFactory paramContextFactory, ContextAction paramContextAction)
/*      */   {
/*  536 */     Context localContext = enter(null, paramContextFactory);
/*      */     try {
/*  538 */       return paramContextAction.run(localContext);
/*      */     }
/*      */     finally {
/*  541 */       exit();
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void addContextListener(ContextListener paramContextListener)
/*      */   {
/*  553 */     String str = "sun.org.mozilla.javascript.internal.tools.debugger.Main";
/*  554 */     if (str.equals(paramContextListener.getClass().getName())) {
/*  555 */       Class localClass1 = paramContextListener.getClass();
/*  556 */       Class localClass2 = Kit.classOrNull("sun.org.mozilla.javascript.internal.ContextFactory");
/*      */ 
/*  558 */       Class[] arrayOfClass = { localClass2 };
/*  559 */       Object[] arrayOfObject = { ContextFactory.getGlobal() };
/*      */       try {
/*  561 */         Method localMethod = localClass1.getMethod("attachTo", arrayOfClass);
/*  562 */         localMethod.invoke(paramContextListener, arrayOfObject);
/*      */       } catch (Exception localException) {
/*  564 */         RuntimeException localRuntimeException = new RuntimeException();
/*  565 */         Kit.initCause(localRuntimeException, localException);
/*  566 */         throw localRuntimeException;
/*      */       }
/*  568 */       return;
/*      */     }
/*      */ 
/*  571 */     ContextFactory.getGlobal().addListener(paramContextListener);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void removeContextListener(ContextListener paramContextListener)
/*      */   {
/*  581 */     ContextFactory.getGlobal().addListener(paramContextListener);
/*      */   }
/*      */ 
/*      */   public final ContextFactory getFactory()
/*      */   {
/*  589 */     return this.factory;
/*      */   }
/*      */ 
/*      */   public final boolean isSealed()
/*      */   {
/*  600 */     return this.sealed;
/*      */   }
/*      */ 
/*      */   public final void seal(Object paramObject)
/*      */   {
/*  617 */     if (this.sealed) onSealedMutation();
/*  618 */     this.sealed = true;
/*  619 */     this.sealKey = paramObject;
/*      */   }
/*      */ 
/*      */   public final void unseal(Object paramObject)
/*      */   {
/*  633 */     if (paramObject == null) throw new IllegalArgumentException();
/*  634 */     if (this.sealKey != paramObject) throw new IllegalArgumentException();
/*  635 */     if (!this.sealed) throw new IllegalStateException();
/*  636 */     this.sealed = false;
/*  637 */     this.sealKey = null;
/*      */   }
/*      */ 
/*      */   static void onSealedMutation()
/*      */   {
/*  642 */     throw new IllegalStateException();
/*      */   }
/*      */ 
/*      */   public final int getLanguageVersion()
/*      */   {
/*  655 */     return this.version;
/*      */   }
/*      */ 
/*      */   public void setLanguageVersion(int paramInt)
/*      */   {
/*  670 */     if (this.sealed) onSealedMutation();
/*  671 */     checkLanguageVersion(paramInt);
/*  672 */     Object localObject = this.propertyListeners;
/*  673 */     if ((localObject != null) && (paramInt != this.version)) {
/*  674 */       firePropertyChangeImpl(localObject, "language version", Integer.valueOf(this.version), Integer.valueOf(paramInt));
/*      */     }
/*      */ 
/*  678 */     this.version = paramInt;
/*      */   }
/*      */ 
/*      */   public static boolean isValidLanguageVersion(int paramInt)
/*      */   {
/*  683 */     switch (paramInt) {
/*      */     case 0:
/*      */     case 100:
/*      */     case 110:
/*      */     case 120:
/*      */     case 130:
/*      */     case 140:
/*      */     case 150:
/*      */     case 160:
/*      */     case 170:
/*      */     case 180:
/*  694 */       return true;
/*      */     }
/*  696 */     return false;
/*      */   }
/*      */ 
/*      */   public static void checkLanguageVersion(int paramInt)
/*      */   {
/*  701 */     if (isValidLanguageVersion(paramInt)) {
/*  702 */       return;
/*      */     }
/*  704 */     throw new IllegalArgumentException("Bad language version: " + paramInt);
/*      */   }
/*      */ 
/*      */   public final String getImplementationVersion()
/*      */   {
/*  728 */     if (implementationVersion == null) {
/*  729 */       implementationVersion = ScriptRuntime.getMessage0("implementation.version");
/*      */     }
/*      */ 
/*  732 */     return implementationVersion;
/*      */   }
/*      */ 
/*      */   public final ErrorReporter getErrorReporter()
/*      */   {
/*  742 */     if (this.errorReporter == null) {
/*  743 */       return DefaultErrorReporter.instance;
/*      */     }
/*  745 */     return this.errorReporter;
/*      */   }
/*      */ 
/*      */   public final ErrorReporter setErrorReporter(ErrorReporter paramErrorReporter)
/*      */   {
/*  756 */     if (this.sealed) onSealedMutation();
/*  757 */     if (paramErrorReporter == null) throw new IllegalArgumentException();
/*  758 */     ErrorReporter localErrorReporter = getErrorReporter();
/*  759 */     if (paramErrorReporter == localErrorReporter) {
/*  760 */       return localErrorReporter;
/*      */     }
/*  762 */     Object localObject = this.propertyListeners;
/*  763 */     if (localObject != null) {
/*  764 */       firePropertyChangeImpl(localObject, "error reporter", localErrorReporter, paramErrorReporter);
/*      */     }
/*      */ 
/*  767 */     this.errorReporter = paramErrorReporter;
/*  768 */     return localErrorReporter;
/*      */   }
/*      */ 
/*      */   public final Locale getLocale()
/*      */   {
/*  780 */     if (this.locale == null)
/*  781 */       this.locale = Locale.getDefault();
/*  782 */     return this.locale;
/*      */   }
/*      */ 
/*      */   public final Locale setLocale(Locale paramLocale)
/*      */   {
/*  792 */     if (this.sealed) onSealedMutation();
/*  793 */     Locale localLocale = this.locale;
/*  794 */     this.locale = paramLocale;
/*  795 */     return localLocale;
/*      */   }
/*      */ 
/*      */   public final void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/*  807 */     if (this.sealed) onSealedMutation();
/*  808 */     this.propertyListeners = Kit.addListener(this.propertyListeners, paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   public final void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
/*      */   {
/*  820 */     if (this.sealed) onSealedMutation();
/*  821 */     this.propertyListeners = Kit.removeListener(this.propertyListeners, paramPropertyChangeListener);
/*      */   }
/*      */ 
/*      */   final void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
/*      */   {
/*  837 */     Object localObject = this.propertyListeners;
/*  838 */     if (localObject != null)
/*  839 */       firePropertyChangeImpl(localObject, paramString, paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   private void firePropertyChangeImpl(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
/*      */   {
/*  846 */     for (int i = 0; ; i++) {
/*  847 */       Object localObject = Kit.getListener(paramObject1, i);
/*  848 */       if (localObject == null)
/*      */         break;
/*  850 */       if ((localObject instanceof PropertyChangeListener)) {
/*  851 */         PropertyChangeListener localPropertyChangeListener = (PropertyChangeListener)localObject;
/*  852 */         localPropertyChangeListener.propertyChange(new PropertyChangeEvent(this, paramString, paramObject2, paramObject3));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void reportWarning(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
/*      */   {
/*  872 */     Context localContext = getContext();
/*  873 */     if (localContext.hasFeature(12))
/*  874 */       reportError(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */     else
/*  876 */       localContext.getErrorReporter().warning(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */   }
/*      */ 
/*      */   public static void reportWarning(String paramString)
/*      */   {
/*  888 */     int[] arrayOfInt = { 0 };
/*  889 */     String str = getSourcePositionFromStack(arrayOfInt);
/*  890 */     reportWarning(paramString, str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public static void reportWarning(String paramString, Throwable paramThrowable)
/*      */   {
/*  895 */     int[] arrayOfInt = { 0 };
/*  896 */     String str = getSourcePositionFromStack(arrayOfInt);
/*  897 */     StringWriter localStringWriter = new StringWriter();
/*  898 */     PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
/*  899 */     localPrintWriter.println(paramString);
/*  900 */     paramThrowable.printStackTrace(localPrintWriter);
/*  901 */     localPrintWriter.flush();
/*  902 */     reportWarning(localStringWriter.toString(), str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public static void reportError(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
/*      */   {
/*  919 */     Context localContext = getCurrentContext();
/*  920 */     if (localContext != null) {
/*  921 */       localContext.getErrorReporter().error(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */     }
/*      */     else
/*  924 */       throw new EvaluatorException(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */   }
/*      */ 
/*      */   public static void reportError(String paramString)
/*      */   {
/*  937 */     int[] arrayOfInt = { 0 };
/*  938 */     String str = getSourcePositionFromStack(arrayOfInt);
/*  939 */     reportError(paramString, str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public static EvaluatorException reportRuntimeError(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
/*      */   {
/*  960 */     Context localContext = getCurrentContext();
/*  961 */     if (localContext != null) {
/*  962 */       return localContext.getErrorReporter().runtimeError(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */     }
/*      */ 
/*  966 */     throw new EvaluatorException(paramString1, paramString2, paramInt1, paramString3, paramInt2);
/*      */   }
/*      */ 
/*      */   static EvaluatorException reportRuntimeError0(String paramString)
/*      */   {
/*  973 */     String str = ScriptRuntime.getMessage0(paramString);
/*  974 */     return reportRuntimeError(str);
/*      */   }
/*      */ 
/*      */   static EvaluatorException reportRuntimeError1(String paramString, Object paramObject)
/*      */   {
/*  980 */     String str = ScriptRuntime.getMessage1(paramString, paramObject);
/*  981 */     return reportRuntimeError(str);
/*      */   }
/*      */ 
/*      */   static EvaluatorException reportRuntimeError2(String paramString, Object paramObject1, Object paramObject2)
/*      */   {
/*  987 */     String str = ScriptRuntime.getMessage2(paramString, paramObject1, paramObject2);
/*  988 */     return reportRuntimeError(str);
/*      */   }
/*      */ 
/*      */   static EvaluatorException reportRuntimeError3(String paramString, Object paramObject1, Object paramObject2, Object paramObject3)
/*      */   {
/*  995 */     String str = ScriptRuntime.getMessage3(paramString, paramObject1, paramObject2, paramObject3);
/*  996 */     return reportRuntimeError(str);
/*      */   }
/*      */ 
/*      */   static EvaluatorException reportRuntimeError4(String paramString, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
/*      */   {
/* 1003 */     String str = ScriptRuntime.getMessage4(paramString, paramObject1, paramObject2, paramObject3, paramObject4);
/*      */ 
/* 1005 */     return reportRuntimeError(str);
/*      */   }
/*      */ 
/*      */   public static EvaluatorException reportRuntimeError(String paramString)
/*      */   {
/* 1016 */     int[] arrayOfInt = { 0 };
/* 1017 */     String str = getSourcePositionFromStack(arrayOfInt);
/* 1018 */     return reportRuntimeError(paramString, str, arrayOfInt[0], null, 0);
/*      */   }
/*      */ 
/*      */   public final ScriptableObject initStandardObjects()
/*      */   {
/* 1037 */     return initStandardObjects(null, false);
/*      */   }
/*      */ 
/*      */   public final Scriptable initStandardObjects(ScriptableObject paramScriptableObject)
/*      */   {
/* 1060 */     return initStandardObjects(paramScriptableObject, false);
/*      */   }
/*      */ 
/*      */   public ScriptableObject initStandardObjects(ScriptableObject paramScriptableObject, boolean paramBoolean)
/*      */   {
/* 1093 */     return ScriptRuntime.initStandardObjects(this, paramScriptableObject, paramBoolean);
/*      */   }
/*      */ 
/*      */   public static Object getUndefinedValue()
/*      */   {
/* 1101 */     return Undefined.instance;
/*      */   }
/*      */ 
/*      */   public final Object evaluateString(Scriptable paramScriptable, String paramString1, String paramString2, int paramInt, Object paramObject)
/*      */   {
/* 1125 */     Script localScript = compileString(paramString1, paramString2, paramInt, paramObject);
/*      */ 
/* 1127 */     if (localScript != null) {
/* 1128 */       return localScript.exec(this, paramScriptable);
/*      */     }
/* 1130 */     return null;
/*      */   }
/*      */ 
/*      */   public final Object evaluateReader(Scriptable paramScriptable, Reader paramReader, String paramString, int paramInt, Object paramObject)
/*      */     throws IOException
/*      */   {
/* 1156 */     Script localScript = compileReader(paramScriptable, paramReader, paramString, paramInt, paramObject);
/*      */ 
/* 1158 */     if (localScript != null) {
/* 1159 */       return localScript.exec(this, paramScriptable);
/*      */     }
/* 1161 */     return null;
/*      */   }
/*      */ 
/*      */   public Object executeScriptWithContinuations(Script paramScript, Scriptable paramScriptable)
/*      */     throws ContinuationPending
/*      */   {
/* 1181 */     if ((!(paramScript instanceof InterpretedFunction)) || (!((InterpretedFunction)paramScript).isScript()))
/*      */     {
/* 1185 */       throw new IllegalArgumentException("Script argument was not a script or was not created by interpreted mode ");
/*      */     }
/*      */ 
/* 1188 */     return callFunctionWithContinuations((InterpretedFunction)paramScript, paramScriptable, ScriptRuntime.emptyArgs);
/*      */   }
/*      */ 
/*      */   public Object callFunctionWithContinuations(Callable paramCallable, Scriptable paramScriptable, Object[] paramArrayOfObject)
/*      */     throws ContinuationPending
/*      */   {
/* 1209 */     if (!(paramCallable instanceof InterpretedFunction))
/*      */     {
/* 1211 */       throw new IllegalArgumentException("Function argument was not created by interpreted mode ");
/*      */     }
/*      */ 
/* 1214 */     if (ScriptRuntime.hasTopCall(this)) {
/* 1215 */       throw new IllegalStateException("Cannot have any pending top calls when executing a script with continuations");
/*      */     }
/*      */ 
/* 1220 */     this.isContinuationsTopCall = true;
/* 1221 */     return ScriptRuntime.doTopCall(paramCallable, this, paramScriptable, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public ContinuationPending captureContinuation()
/*      */   {
/* 1238 */     return new ContinuationPending(Interpreter.captureContinuation(this));
/*      */   }
/*      */ 
/*      */   public Object resumeContinuation(Object paramObject1, Scriptable paramScriptable, Object paramObject2)
/*      */     throws ContinuationPending
/*      */   {
/* 1262 */     Object[] arrayOfObject = { paramObject2 };
/* 1263 */     return Interpreter.restartContinuation((NativeContinuation)paramObject1, this, paramScriptable, arrayOfObject);
/*      */   }
/*      */ 
/*      */   public final boolean stringIsCompilableUnit(String paramString)
/*      */   {
/* 1286 */     int i = 0;
/* 1287 */     CompilerEnvirons localCompilerEnvirons = new CompilerEnvirons();
/* 1288 */     localCompilerEnvirons.initFromContext(this);
/*      */ 
/* 1291 */     localCompilerEnvirons.setGeneratingSource(false);
/* 1292 */     Parser localParser = new Parser(localCompilerEnvirons, DefaultErrorReporter.instance);
/*      */     try {
/* 1294 */       localParser.parse(paramString, null, 1);
/*      */     } catch (EvaluatorException localEvaluatorException) {
/* 1296 */       i = 1;
/*      */     }
/*      */ 
/* 1301 */     if ((i != 0) && (localParser.eof())) {
/* 1302 */       return false;
/*      */     }
/* 1304 */     return true;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final Script compileReader(Scriptable paramScriptable, Reader paramReader, String paramString, int paramInt, Object paramObject)
/*      */     throws IOException
/*      */   {
/* 1317 */     return compileReader(paramReader, paramString, paramInt, paramObject);
/*      */   }
/*      */ 
/*      */   public final Script compileReader(Reader paramReader, String paramString, int paramInt, Object paramObject)
/*      */     throws IOException
/*      */   {
/* 1341 */     if (paramInt < 0)
/*      */     {
/* 1343 */       paramInt = 0;
/*      */     }
/* 1345 */     return (Script)compileImpl(null, paramReader, null, paramString, paramInt, paramObject, false, null, null);
/*      */   }
/*      */ 
/*      */   public final Script compileString(String paramString1, String paramString2, int paramInt, Object paramObject)
/*      */   {
/* 1369 */     if (paramInt < 0)
/*      */     {
/* 1371 */       paramInt = 0;
/*      */     }
/* 1373 */     return compileString(paramString1, null, null, paramString2, paramInt, paramObject);
/*      */   }
/*      */ 
/*      */   final Script compileString(String paramString1, Evaluator paramEvaluator, ErrorReporter paramErrorReporter, String paramString2, int paramInt, Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/* 1384 */       return (Script)compileImpl(null, null, paramString1, paramString2, paramInt, paramObject, false, paramEvaluator, paramErrorReporter);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/* 1389 */     throw new RuntimeException();
/*      */   }
/*      */ 
/*      */   public final Function compileFunction(Scriptable paramScriptable, String paramString1, String paramString2, int paramInt, Object paramObject)
/*      */   {
/* 1414 */     return compileFunction(paramScriptable, paramString1, null, null, paramString2, paramInt, paramObject);
/*      */   }
/*      */ 
/*      */   final Function compileFunction(Scriptable paramScriptable, String paramString1, Evaluator paramEvaluator, ErrorReporter paramErrorReporter, String paramString2, int paramInt, Object paramObject)
/*      */   {
/*      */     try
/*      */     {
/* 1425 */       return (Function)compileImpl(paramScriptable, null, paramString1, paramString2, paramInt, paramObject, true, paramEvaluator, paramErrorReporter);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*      */ 
/* 1432 */     throw new RuntimeException();
/*      */   }
/*      */ 
/*      */   public final String decompileScript(Script paramScript, int paramInt)
/*      */   {
/* 1447 */     NativeFunction localNativeFunction = (NativeFunction)paramScript;
/* 1448 */     return localNativeFunction.decompile(paramInt, 0);
/*      */   }
/*      */ 
/*      */   public final String decompileFunction(Function paramFunction, int paramInt)
/*      */   {
/* 1466 */     if ((paramFunction instanceof BaseFunction)) {
/* 1467 */       return ((BaseFunction)paramFunction).decompile(paramInt, 0);
/*      */     }
/* 1469 */     return "function " + paramFunction.getClassName() + "() {\n\t[native code]\n}\n";
/*      */   }
/*      */ 
/*      */   public final String decompileFunctionBody(Function paramFunction, int paramInt)
/*      */   {
/* 1488 */     if ((paramFunction instanceof BaseFunction)) {
/* 1489 */       BaseFunction localBaseFunction = (BaseFunction)paramFunction;
/* 1490 */       return localBaseFunction.decompile(paramInt, 1);
/*      */     }
/*      */ 
/* 1493 */     return "[native code]\n";
/*      */   }
/*      */ 
/*      */   public final Scriptable newObject(Scriptable paramScriptable)
/*      */   {
/* 1506 */     return newObject(paramScriptable, "Object", ScriptRuntime.emptyArgs);
/*      */   }
/*      */ 
/*      */   public final Scriptable newObject(Scriptable paramScriptable, String paramString)
/*      */   {
/* 1521 */     return newObject(paramScriptable, paramString, ScriptRuntime.emptyArgs);
/*      */   }
/*      */ 
/*      */   public final Scriptable newObject(Scriptable paramScriptable, String paramString, Object[] paramArrayOfObject)
/*      */   {
/* 1546 */     paramScriptable = ScriptableObject.getTopLevelScope(paramScriptable);
/* 1547 */     Function localFunction = ScriptRuntime.getExistingCtor(this, paramScriptable, paramString);
/*      */ 
/* 1549 */     if (paramArrayOfObject == null) paramArrayOfObject = ScriptRuntime.emptyArgs;
/* 1550 */     return localFunction.construct(this, paramScriptable, paramArrayOfObject);
/*      */   }
/*      */ 
/*      */   public final Scriptable newArray(Scriptable paramScriptable, int paramInt)
/*      */   {
/* 1563 */     NativeArray localNativeArray = new NativeArray(paramInt);
/* 1564 */     ScriptRuntime.setObjectProtoAndParent(localNativeArray, paramScriptable);
/* 1565 */     return localNativeArray;
/*      */   }
/*      */ 
/*      */   public final Scriptable newArray(Scriptable paramScriptable, Object[] paramArrayOfObject)
/*      */   {
/* 1580 */     if (paramArrayOfObject.getClass().getComponentType() != ScriptRuntime.ObjectClass)
/* 1581 */       throw new IllegalArgumentException();
/* 1582 */     NativeArray localNativeArray = new NativeArray(paramArrayOfObject);
/* 1583 */     ScriptRuntime.setObjectProtoAndParent(localNativeArray, paramScriptable);
/* 1584 */     return localNativeArray;
/*      */   }
/*      */ 
/*      */   public final Object[] getElements(Scriptable paramScriptable)
/*      */   {
/* 1606 */     return ScriptRuntime.getArrayElements(paramScriptable);
/*      */   }
/*      */ 
/*      */   public static boolean toBoolean(Object paramObject)
/*      */   {
/* 1620 */     return ScriptRuntime.toBoolean(paramObject);
/*      */   }
/*      */ 
/*      */   public static double toNumber(Object paramObject)
/*      */   {
/* 1636 */     return ScriptRuntime.toNumber(paramObject);
/*      */   }
/*      */ 
/*      */   public static String toString(Object paramObject)
/*      */   {
/* 1650 */     return ScriptRuntime.toString(paramObject);
/*      */   }
/*      */ 
/*      */   public static Scriptable toObject(Object paramObject, Scriptable paramScriptable)
/*      */   {
/* 1672 */     return ScriptRuntime.toObject(paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Scriptable toObject(Object paramObject, Scriptable paramScriptable, Class<?> paramClass)
/*      */   {
/* 1682 */     return ScriptRuntime.toObject(paramScriptable, paramObject);
/*      */   }
/*      */ 
/*      */   public static Object javaToJS(Object paramObject, Scriptable paramScriptable)
/*      */   {
/* 1715 */     if (((paramObject instanceof String)) || ((paramObject instanceof Number)) || ((paramObject instanceof Boolean)) || ((paramObject instanceof Scriptable)))
/*      */     {
/* 1718 */       return paramObject;
/* 1719 */     }if ((paramObject instanceof Character)) {
/* 1720 */       return String.valueOf(((Character)paramObject).charValue());
/*      */     }
/* 1722 */     Context localContext = getContext();
/* 1723 */     return localContext.getWrapFactory().wrap(localContext, paramScriptable, paramObject, null);
/*      */   }
/*      */ 
/*      */   public static Object jsToJava(Object paramObject, Class<?> paramClass)
/*      */     throws EvaluatorException
/*      */   {
/* 1741 */     return NativeJavaObject.coerceTypeImpl(paramClass, paramObject);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Object toType(Object paramObject, Class<?> paramClass)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     try
/*      */     {
/* 1755 */       return jsToJava(paramObject, paramClass);
/*      */     }
/*      */     catch (EvaluatorException localEvaluatorException) {
/* 1758 */       IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(localEvaluatorException.getMessage());
/* 1759 */       Kit.initCause(localIllegalArgumentException, localEvaluatorException);
/* 1760 */       throw localIllegalArgumentException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static RuntimeException throwAsScriptRuntimeEx(Throwable paramThrowable)
/*      */   {
/* 1783 */     while ((paramThrowable instanceof InvocationTargetException)) {
/* 1784 */       paramThrowable = ((InvocationTargetException)paramThrowable).getTargetException();
/*      */     }
/*      */ 
/* 1787 */     if ((paramThrowable instanceof Error)) {
/* 1788 */       Context localContext = getContext();
/* 1789 */       if ((localContext == null) || (!localContext.hasFeature(13)))
/*      */       {
/* 1792 */         throw ((Error)paramThrowable);
/*      */       }
/*      */     }
/* 1795 */     if ((paramThrowable instanceof RhinoException)) {
/* 1796 */       throw ((RhinoException)paramThrowable);
/*      */     }
/* 1798 */     throw new WrappedException(paramThrowable);
/*      */   }
/*      */ 
/*      */   public final boolean isGeneratingDebug()
/*      */   {
/* 1807 */     return this.generatingDebug;
/*      */   }
/*      */ 
/*      */   public final void setGeneratingDebug(boolean paramBoolean)
/*      */   {
/* 1819 */     if (this.sealed) onSealedMutation();
/* 1820 */     this.generatingDebugChanged = true;
/* 1821 */     if ((paramBoolean) && (getOptimizationLevel() > 0))
/* 1822 */       setOptimizationLevel(0);
/* 1823 */     this.generatingDebug = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final boolean isGeneratingSource()
/*      */   {
/* 1832 */     return this.generatingSource;
/*      */   }
/*      */ 
/*      */   public final void setGeneratingSource(boolean paramBoolean)
/*      */   {
/* 1847 */     if (this.sealed) onSealedMutation();
/* 1848 */     this.generatingSource = paramBoolean;
/*      */   }
/*      */ 
/*      */   public final int getOptimizationLevel()
/*      */   {
/* 1861 */     return this.optimizationLevel;
/*      */   }
/*      */ 
/*      */   public final void setOptimizationLevel(int paramInt)
/*      */   {
/* 1883 */     if (this.sealed) onSealedMutation();
/* 1884 */     if (paramInt == -2)
/*      */     {
/* 1886 */       paramInt = -1;
/*      */     }
/* 1888 */     checkOptimizationLevel(paramInt);
/* 1889 */     if (codegenClass == null)
/* 1890 */       paramInt = -1;
/* 1891 */     this.optimizationLevel = paramInt;
/*      */   }
/*      */ 
/*      */   public static boolean isValidOptimizationLevel(int paramInt)
/*      */   {
/* 1896 */     return (-1 <= paramInt) && (paramInt <= 9);
/*      */   }
/*      */ 
/*      */   public static void checkOptimizationLevel(int paramInt)
/*      */   {
/* 1901 */     if (isValidOptimizationLevel(paramInt)) {
/* 1902 */       return;
/*      */     }
/* 1904 */     throw new IllegalArgumentException("Optimization level outside [-1..9]: " + paramInt);
/*      */   }
/*      */ 
/*      */   public final int getMaximumInterpreterStackDepth()
/*      */   {
/* 1924 */     return this.maximumInterpreterStackDepth;
/*      */   }
/*      */ 
/*      */   public final void setMaximumInterpreterStackDepth(int paramInt)
/*      */   {
/* 1946 */     if (this.sealed) onSealedMutation();
/* 1947 */     if (this.optimizationLevel != -1) {
/* 1948 */       throw new IllegalStateException("Cannot set maximumInterpreterStackDepth when optimizationLevel != -1");
/*      */     }
/* 1950 */     if (paramInt < 1) {
/* 1951 */       throw new IllegalArgumentException("Cannot set maximumInterpreterStackDepth to less than 1");
/*      */     }
/* 1953 */     this.maximumInterpreterStackDepth = paramInt;
/*      */   }
/*      */ 
/*      */   public final void setSecurityController(SecurityController paramSecurityController)
/*      */   {
/* 1969 */     if (this.sealed) onSealedMutation();
/* 1970 */     if (paramSecurityController == null) throw new IllegalArgumentException();
/* 1971 */     if (this.securityController != null) {
/* 1972 */       throw new SecurityException("Can not overwrite existing SecurityController object");
/*      */     }
/* 1974 */     if (SecurityController.hasGlobal()) {
/* 1975 */       throw new SecurityException("Can not overwrite existing global SecurityController object");
/*      */     }
/* 1977 */     this.securityController = paramSecurityController;
/*      */   }
/*      */ 
/*      */   public final synchronized void setClassShutter(ClassShutter paramClassShutter)
/*      */   {
/* 1990 */     if (this.sealed) onSealedMutation();
/* 1991 */     if (paramClassShutter == null) throw new IllegalArgumentException();
/* 1992 */     if (this.hasClassShutter) {
/* 1993 */       throw new SecurityException("Cannot overwrite existing ClassShutter object");
/*      */     }
/*      */ 
/* 1996 */     this.classShutter = paramClassShutter;
/* 1997 */     this.hasClassShutter = true;
/*      */   }
/*      */ 
/*      */   final synchronized ClassShutter getClassShutter()
/*      */   {
/* 2002 */     return this.classShutter;
/*      */   }
/*      */ 
/*      */   public final synchronized ClassShutterSetter getClassShutterSetter()
/*      */   {
/* 2011 */     if (this.hasClassShutter)
/* 2012 */       return null;
/* 2013 */     this.hasClassShutter = true;
/* 2014 */     return new ClassShutterSetter() {
/*      */       public void setClassShutter(ClassShutter paramAnonymousClassShutter) {
/* 2016 */         Context.this.classShutter = paramAnonymousClassShutter;
/*      */       }
/*      */       public ClassShutter getClassShutter() {
/* 2019 */         return Context.this.classShutter;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public final Object getThreadLocal(Object paramObject)
/*      */   {
/* 2041 */     if (this.threadLocalMap == null)
/* 2042 */       return null;
/* 2043 */     return this.threadLocalMap.get(paramObject);
/*      */   }
/*      */ 
/*      */   public final synchronized void putThreadLocal(Object paramObject1, Object paramObject2)
/*      */   {
/* 2054 */     if (this.sealed) onSealedMutation();
/* 2055 */     if (this.threadLocalMap == null)
/* 2056 */       this.threadLocalMap = new HashMap();
/* 2057 */     this.threadLocalMap.put(paramObject1, paramObject2);
/*      */   }
/*      */ 
/*      */   public final void removeThreadLocal(Object paramObject)
/*      */   {
/* 2067 */     if (this.sealed) onSealedMutation();
/* 2068 */     if (this.threadLocalMap == null)
/* 2069 */       return;
/* 2070 */     this.threadLocalMap.remove(paramObject);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final boolean hasCompileFunctionsWithDynamicScope()
/*      */   {
/* 2080 */     return this.compileFunctionsWithDynamicScopeFlag;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final void setCompileFunctionsWithDynamicScope(boolean paramBoolean)
/*      */   {
/* 2090 */     if (this.sealed) onSealedMutation();
/* 2091 */     this.compileFunctionsWithDynamicScopeFlag = paramBoolean;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void setCachingEnabled(boolean paramBoolean)
/*      */   {
/*      */   }
/*      */ 
/*      */   public final void setWrapFactory(WrapFactory paramWrapFactory)
/*      */   {
/* 2113 */     if (this.sealed) onSealedMutation();
/* 2114 */     if (paramWrapFactory == null) throw new IllegalArgumentException();
/* 2115 */     this.wrapFactory = paramWrapFactory;
/*      */   }
/*      */ 
/*      */   public final WrapFactory getWrapFactory()
/*      */   {
/* 2125 */     if (this.wrapFactory == null) {
/* 2126 */       this.wrapFactory = new WrapFactory();
/*      */     }
/* 2128 */     return this.wrapFactory;
/*      */   }
/*      */ 
/*      */   public final Debugger getDebugger()
/*      */   {
/* 2137 */     return this.debugger;
/*      */   }
/*      */ 
/*      */   public final Object getDebuggerContextData()
/*      */   {
/* 2146 */     return this.debuggerData;
/*      */   }
/*      */ 
/*      */   public final void setDebugger(Debugger paramDebugger, Object paramObject)
/*      */   {
/* 2158 */     if (this.sealed) onSealedMutation();
/* 2159 */     this.debugger = paramDebugger;
/* 2160 */     this.debuggerData = paramObject;
/*      */   }
/*      */ 
/*      */   public static DebuggableScript getDebuggableView(Script paramScript)
/*      */   {
/* 2170 */     if ((paramScript instanceof NativeFunction)) {
/* 2171 */       return ((NativeFunction)paramScript).getDebuggableView();
/*      */     }
/* 2173 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasFeature(int paramInt)
/*      */   {
/* 2204 */     ContextFactory localContextFactory = getFactory();
/* 2205 */     return localContextFactory.hasFeature(this, paramInt);
/*      */   }
/*      */ 
/*      */   public XMLLib.Factory getE4xImplementationFactory()
/*      */   {
/* 2220 */     return getFactory().getE4xImplementationFactory();
/*      */   }
/*      */ 
/*      */   public final int getInstructionObserverThreshold()
/*      */   {
/* 2233 */     return this.instructionThreshold;
/*      */   }
/*      */ 
/*      */   public final void setInstructionObserverThreshold(int paramInt)
/*      */   {
/* 2253 */     if (this.sealed) onSealedMutation();
/* 2254 */     if (paramInt < 0) throw new IllegalArgumentException();
/* 2255 */     this.instructionThreshold = paramInt;
/* 2256 */     setGenerateObserverCount(paramInt > 0);
/*      */   }
/*      */ 
/*      */   public void setGenerateObserverCount(boolean paramBoolean)
/*      */   {
/* 2271 */     this.generateObserverCount = paramBoolean;
/*      */   }
/*      */ 
/*      */   protected void observeInstructionCount(int paramInt)
/*      */   {
/* 2295 */     ContextFactory localContextFactory = getFactory();
/* 2296 */     localContextFactory.observeInstructionCount(this, paramInt);
/*      */   }
/*      */ 
/*      */   public GeneratedClassLoader createClassLoader(ClassLoader paramClassLoader)
/*      */   {
/* 2306 */     ContextFactory localContextFactory = getFactory();
/* 2307 */     return localContextFactory.createClassLoader(paramClassLoader);
/*      */   }
/*      */ 
/*      */   public final ClassLoader getApplicationClassLoader()
/*      */   {
/* 2312 */     if (this.applicationClassLoader == null) {
/* 2313 */       ContextFactory localContextFactory = getFactory();
/* 2314 */       ClassLoader localClassLoader1 = localContextFactory.getApplicationClassLoader();
/* 2315 */       if (localClassLoader1 == null) {
/* 2316 */         ClassLoader localClassLoader2 = VMBridge.instance.getCurrentThreadClassLoader();
/*      */ 
/* 2330 */         if (localClassLoader2 != null)
/*      */         {
/* 2336 */           return localClassLoader2;
/*      */         }
/*      */ 
/* 2341 */         Class localClass = localContextFactory.getClass();
/* 2342 */         if (localClass != ScriptRuntime.ContextFactoryClass)
/* 2343 */           localClassLoader1 = localClass.getClassLoader();
/*      */         else {
/* 2345 */           localClassLoader1 = getClass().getClassLoader();
/*      */         }
/*      */       }
/* 2348 */       this.applicationClassLoader = localClassLoader1;
/*      */     }
/* 2350 */     return this.applicationClassLoader;
/*      */   }
/*      */ 
/*      */   public final void setApplicationClassLoader(ClassLoader paramClassLoader)
/*      */   {
/* 2355 */     if (this.sealed) onSealedMutation();
/* 2356 */     if (paramClassLoader == null)
/*      */     {
/* 2358 */       this.applicationClassLoader = null;
/* 2359 */       return;
/*      */     }
/*      */ 
/* 2367 */     this.applicationClassLoader = paramClassLoader;
/*      */   }
/*      */ 
/*      */   static Context getContext()
/*      */   {
/* 2378 */     Context localContext = getCurrentContext();
/* 2379 */     if (localContext == null) {
/* 2380 */       throw new RuntimeException("No Context associated with current Thread");
/*      */     }
/*      */ 
/* 2383 */     return localContext;
/*      */   }
/*      */ 
/*      */   private Object compileImpl(Scriptable paramScriptable, Reader paramReader, String paramString1, String paramString2, int paramInt, Object paramObject, boolean paramBoolean, Evaluator paramEvaluator, ErrorReporter paramErrorReporter)
/*      */     throws IOException
/*      */   {
/* 2394 */     if (paramString2 == null) {
/* 2395 */       paramString2 = "unnamed script";
/*      */     }
/* 2397 */     if ((paramObject != null) && (getSecurityController() == null)) {
/* 2398 */       throw new IllegalArgumentException("securityDomain should be null if setSecurityController() was never called");
/*      */     }
/*      */ 
/* 2403 */     if (((paramReader == null ? 1 : 0) ^ (paramString1 == null ? 1 : 0)) == 0) Kit.codeBug();
/*      */ 
/* 2405 */     if (!(paramScriptable == null ^ paramBoolean)) Kit.codeBug();
/*      */ 
/* 2407 */     CompilerEnvirons localCompilerEnvirons = new CompilerEnvirons();
/* 2408 */     localCompilerEnvirons.initFromContext(this);
/* 2409 */     if (paramErrorReporter == null) {
/* 2410 */       paramErrorReporter = localCompilerEnvirons.getErrorReporter();
/*      */     }
/*      */ 
/* 2413 */     if ((this.debugger != null) && 
/* 2414 */       (paramReader != null)) {
/* 2415 */       paramString1 = Kit.readReader(paramReader);
/* 2416 */       paramReader = null;
/*      */     }
/*      */ 
/* 2420 */     Parser localParser = new Parser(localCompilerEnvirons, paramErrorReporter);
/* 2421 */     if (paramBoolean) {
/* 2422 */       localParser.calledByCompileFunction = true;
/*      */     }
/*      */ 
/* 2425 */     if (paramString1 != null)
/* 2426 */       localAstRoot = localParser.parse(paramString1, paramString2, paramInt);
/*      */     else {
/* 2428 */       localAstRoot = localParser.parse(paramReader, paramString2, paramInt);
/*      */     }
/* 2430 */     if (paramBoolean)
/*      */     {
/* 2432 */       if ((localAstRoot.getFirstChild() == null) || (localAstRoot.getFirstChild().getType() != 109))
/*      */       {
/* 2438 */         throw new IllegalArgumentException("compileFunction only accepts source with single JS function: " + paramString1);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2443 */     IRFactory localIRFactory = new IRFactory(localCompilerEnvirons, paramErrorReporter);
/* 2444 */     ScriptNode localScriptNode = localIRFactory.transformTree(localAstRoot);
/*      */ 
/* 2447 */     localParser = null;
/* 2448 */     AstRoot localAstRoot = null;
/* 2449 */     localIRFactory = null;
/*      */ 
/* 2451 */     if (paramEvaluator == null) {
/* 2452 */       paramEvaluator = createCompiler();
/*      */     }
/*      */ 
/* 2455 */     Object localObject1 = paramEvaluator.compile(localCompilerEnvirons, localScriptNode, localScriptNode.getEncodedSource(), paramBoolean);
/*      */     Object localObject2;
/* 2458 */     if (this.debugger != null) {
/* 2459 */       if (paramString1 == null) Kit.codeBug();
/* 2460 */       if ((localObject1 instanceof DebuggableScript)) {
/* 2461 */         localObject2 = (DebuggableScript)localObject1;
/* 2462 */         notifyDebugger_r(this, (DebuggableScript)localObject2, paramString1);
/*      */       } else {
/* 2464 */         throw new RuntimeException("NOT SUPPORTED");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2469 */     if (paramBoolean)
/* 2470 */       localObject2 = paramEvaluator.createFunctionObject(this, paramScriptable, localObject1, paramObject);
/*      */     else {
/* 2472 */       localObject2 = paramEvaluator.createScriptObject(localObject1, paramObject);
/*      */     }
/*      */ 
/* 2475 */     return localObject2;
/*      */   }
/*      */ 
/*      */   private static void notifyDebugger_r(Context paramContext, DebuggableScript paramDebuggableScript, String paramString)
/*      */   {
/* 2481 */     paramContext.debugger.handleCompilationDone(paramContext, paramDebuggableScript, paramString);
/* 2482 */     for (int i = 0; i != paramDebuggableScript.getFunctionCount(); i++)
/* 2483 */       notifyDebugger_r(paramContext, paramDebuggableScript.getFunction(i), paramString);
/*      */   }
/*      */ 
/*      */   private Evaluator createCompiler()
/*      */   {
/* 2494 */     Evaluator localEvaluator = null;
/* 2495 */     if ((this.optimizationLevel >= 0) && (codegenClass != null)) {
/* 2496 */       localEvaluator = (Evaluator)Kit.newInstanceOrNull(codegenClass);
/*      */     }
/* 2498 */     if (localEvaluator == null) {
/* 2499 */       localEvaluator = createInterpreter();
/*      */     }
/* 2501 */     return localEvaluator;
/*      */   }
/*      */ 
/*      */   static Evaluator createInterpreter()
/*      */   {
/* 2506 */     return (Evaluator)Kit.newInstanceOrNull(interpreterClass);
/*      */   }
/*      */ 
/*      */   static String getSourcePositionFromStack(int[] paramArrayOfInt)
/*      */   {
/* 2511 */     Context localContext = getCurrentContext();
/* 2512 */     if (localContext == null)
/* 2513 */       return null;
/* 2514 */     if (localContext.lastInterpreterFrame != null) {
/* 2515 */       localObject = createInterpreter();
/* 2516 */       if (localObject != null) {
/* 2517 */         return ((Evaluator)localObject).getSourcePositionFromStack(localContext, paramArrayOfInt);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2523 */     Object localObject = new CharArrayWriter();
/* 2524 */     RuntimeException localRuntimeException = new RuntimeException();
/* 2525 */     localRuntimeException.printStackTrace(new PrintWriter((Writer)localObject));
/* 2526 */     String str1 = ((CharArrayWriter)localObject).toString();
/* 2527 */     int i = -1;
/* 2528 */     int j = -1;
/* 2529 */     int k = -1;
/* 2530 */     for (int m = 0; m < str1.length(); m++) {
/* 2531 */       int n = str1.charAt(m);
/* 2532 */       if (n == 58) {
/* 2533 */         k = m;
/* 2534 */       } else if (n == 40) {
/* 2535 */         i = m;
/* 2536 */       } else if (n == 41) {
/* 2537 */         j = m;
/* 2538 */       } else if ((n == 10) && (i != -1) && (j != -1) && (k != -1) && (i < k) && (k < j))
/*      */       {
/* 2541 */         String str2 = str1.substring(i + 1, k);
/* 2542 */         if (!str2.endsWith(".java")) {
/* 2543 */           String str3 = str1.substring(k + 1, j);
/*      */           try {
/* 2545 */             paramArrayOfInt[0] = Integer.parseInt(str3);
/* 2546 */             if (paramArrayOfInt[0] < 0) {
/* 2547 */               paramArrayOfInt[0] = 0;
/*      */             }
/* 2549 */             return str2;
/*      */           }
/*      */           catch (NumberFormatException localNumberFormatException)
/*      */           {
/*      */           }
/*      */         }
/* 2555 */         i = j = k = -1;
/*      */       }
/*      */     }
/*      */ 
/* 2559 */     return null;
/*      */   }
/*      */ 
/*      */   RegExpProxy getRegExpProxy()
/*      */   {
/* 2564 */     if (this.regExpProxy == null) {
/* 2565 */       Class localClass = Kit.classOrNull("sun.org.mozilla.javascript.internal.regexp.RegExpImpl");
/*      */ 
/* 2567 */       if (localClass != null) {
/* 2568 */         this.regExpProxy = ((RegExpProxy)Kit.newInstanceOrNull(localClass));
/*      */       }
/*      */     }
/* 2571 */     return this.regExpProxy;
/*      */   }
/*      */ 
/*      */   final boolean isVersionECMA1()
/*      */   {
/* 2576 */     return (this.version == 0) || (this.version >= 130);
/*      */   }
/*      */ 
/*      */   SecurityController getSecurityController()
/*      */   {
/* 2582 */     SecurityController localSecurityController = SecurityController.global();
/* 2583 */     if (localSecurityController != null) {
/* 2584 */       return localSecurityController;
/*      */     }
/* 2586 */     return this.securityController;
/*      */   }
/*      */ 
/*      */   public final boolean isGeneratingDebugChanged()
/*      */   {
/* 2591 */     return this.generatingDebugChanged;
/*      */   }
/*      */ 
/*      */   public void addActivationName(String paramString)
/*      */   {
/* 2602 */     if (this.sealed) onSealedMutation();
/* 2603 */     if (this.activationNames == null)
/* 2604 */       this.activationNames = new HashSet();
/* 2605 */     this.activationNames.add(paramString);
/*      */   }
/*      */ 
/*      */   public final boolean isActivationNeeded(String paramString)
/*      */   {
/* 2618 */     return (this.activationNames != null) && (this.activationNames.contains(paramString));
/*      */   }
/*      */ 
/*      */   public void removeActivationName(String paramString)
/*      */   {
/* 2629 */     if (this.sealed) onSealedMutation();
/* 2630 */     if (this.activationNames != null)
/* 2631 */       this.activationNames.remove(paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface ClassShutterSetter
/*      */   {
/*      */     public abstract void setClassShutter(ClassShutter paramClassShutter);
/*      */ 
/*      */     public abstract ClassShutter getClassShutter();
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.Context
 * JD-Core Version:    0.6.2
 */