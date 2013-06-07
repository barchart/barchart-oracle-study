/*      */ package sun.org.mozilla.javascript.internal.optimizer;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import sun.org.mozilla.classfile.internal.ClassFileWriter;
/*      */ import sun.org.mozilla.classfile.internal.ClassFileWriter.ClassFileFormatException;
/*      */ import sun.org.mozilla.javascript.internal.CompilerEnvirons;
/*      */ import sun.org.mozilla.javascript.internal.Context;
/*      */ import sun.org.mozilla.javascript.internal.Evaluator;
/*      */ import sun.org.mozilla.javascript.internal.Function;
/*      */ import sun.org.mozilla.javascript.internal.GeneratedClassLoader;
/*      */ import sun.org.mozilla.javascript.internal.Kit;
/*      */ import sun.org.mozilla.javascript.internal.NativeFunction;
/*      */ import sun.org.mozilla.javascript.internal.ObjArray;
/*      */ import sun.org.mozilla.javascript.internal.ObjToIntMap;
/*      */ import sun.org.mozilla.javascript.internal.RhinoException;
/*      */ import sun.org.mozilla.javascript.internal.Script;
/*      */ import sun.org.mozilla.javascript.internal.ScriptRuntime;
/*      */ import sun.org.mozilla.javascript.internal.Scriptable;
/*      */ import sun.org.mozilla.javascript.internal.SecurityController;
/*      */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*      */ import sun.org.mozilla.javascript.internal.ast.Name;
/*      */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*      */ 
/*      */ public class Codegen
/*      */   implements Evaluator
/*      */ {
/*      */   static final String DEFAULT_MAIN_METHOD_CLASS = "sun.org.mozilla.javascript.internal.optimizer.OptRuntime";
/*      */   private static final String SUPER_CLASS_NAME = "sun.org.mozilla.javascript.internal.NativeFunction";
/*      */   static final String DIRECT_CALL_PARENT_FIELD = "_dcp";
/*      */   private static final String ID_FIELD_NAME = "_id";
/*      */   private static final String REGEXP_INIT_METHOD_NAME = "_reInit";
/*      */   private static final String REGEXP_INIT_METHOD_SIGNATURE = "(Lsun/org/mozilla/javascript/internal/RegExpProxy;Lsun/org/mozilla/javascript/internal/Context;)V";
/*      */   static final String REGEXP_ARRAY_FIELD_NAME = "_re";
/*      */   static final String REGEXP_ARRAY_FIELD_TYPE = "[Ljava/lang/Object;";
/*      */   static final String FUNCTION_INIT_SIGNATURE = "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)V";
/*      */   static final String FUNCTION_CONSTRUCTOR_SIGNATURE = "(Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;I)V";
/* 1339 */   private static final Object globalLock = new Object();
/*      */   private static int globalSerialClassCounter;
/*      */   private CompilerEnvirons compilerEnv;
/*      */   private ObjArray directCallTargets;
/*      */   ScriptNode[] scriptOrFnNodes;
/*      */   private ObjToIntMap scriptOrFnIndexes;
/* 1348 */   private String mainMethodClass = "sun.org.mozilla.javascript.internal.optimizer.OptRuntime";
/*      */   String mainClassName;
/*      */   String mainClassSignature;
/*      */   private double[] itsConstantList;
/*      */   private int itsConstantListSize;
/*      */ 
/*      */   public void captureStackInfo(RhinoException paramRhinoException)
/*      */   {
/*   65 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   public String getSourcePositionFromStack(Context paramContext, int[] paramArrayOfInt) {
/*   69 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   public String getPatchedStack(RhinoException paramRhinoException, String paramString) {
/*   73 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   public List<String> getScriptStack(RhinoException paramRhinoException) {
/*   77 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   public void setEvalScriptFlag(Script paramScript) {
/*   81 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   public Object compile(CompilerEnvirons paramCompilerEnvirons, ScriptNode paramScriptNode, String paramString, boolean paramBoolean)
/*      */   {
/*      */     int i;
/*   90 */     synchronized (globalLock) {
/*   91 */       i = ++globalSerialClassCounter;
/*      */     }
/*      */ 
/*   94 */     ??? = "c";
/*   95 */     if (paramScriptNode.getSourceName().length() > 0) {
/*   96 */       ??? = paramScriptNode.getSourceName().replaceAll("\\W", "_");
/*   97 */       if (!Character.isJavaIdentifierStart(((String)???).charAt(0))) {
/*   98 */         ??? = "_" + (String)???;
/*      */       }
/*      */     }
/*      */ 
/*  102 */     String str = "sun.org.mozilla.javascript.internal.gen." + (String)??? + "_" + i;
/*      */ 
/*  104 */     byte[] arrayOfByte = compileToClassFile(paramCompilerEnvirons, str, paramScriptNode, paramString, paramBoolean);
/*      */ 
/*  108 */     return new Object[] { str, arrayOfByte };
/*      */   }
/*      */ 
/*      */   public Script createScriptObject(Object paramObject1, Object paramObject2)
/*      */   {
/*  114 */     Class localClass = defineClass(paramObject1, paramObject2);
/*      */     Script localScript;
/*      */     try
/*      */     {
/*  118 */       localScript = (Script)localClass.newInstance();
/*      */     } catch (Exception localException) {
/*  120 */       throw new RuntimeException("Unable to instantiate compiled class:" + localException.toString());
/*      */     }
/*      */ 
/*  123 */     return localScript;
/*      */   }
/*      */ 
/*      */   public Function createFunctionObject(Context paramContext, Scriptable paramScriptable, Object paramObject1, Object paramObject2)
/*      */   {
/*  130 */     Class localClass = defineClass(paramObject1, paramObject2);
/*      */     NativeFunction localNativeFunction;
/*      */     try
/*      */     {
/*  134 */       Constructor localConstructor = localClass.getConstructors()[0];
/*  135 */       Object[] arrayOfObject = { paramScriptable, paramContext, Integer.valueOf(0) };
/*  136 */       localNativeFunction = (NativeFunction)localConstructor.newInstance(arrayOfObject);
/*      */     } catch (Exception localException) {
/*  138 */       throw new RuntimeException("Unable to instantiate compiled class:" + localException.toString());
/*      */     }
/*      */ 
/*  141 */     return localNativeFunction;
/*      */   }
/*      */ 
/*      */   private Class<?> defineClass(Object paramObject1, Object paramObject2) {
/*  147 */     Object[] arrayOfObject = (Object[])paramObject1;
/*  148 */     String str = (String)arrayOfObject[0];
/*  149 */     byte[] arrayOfByte = (byte[])arrayOfObject[1];
/*      */ 
/*  153 */     ClassLoader localClassLoader = getClass().getClassLoader();
/*      */ 
/*  155 */     GeneratedClassLoader localGeneratedClassLoader = SecurityController.createLoader(localClassLoader, paramObject2);
/*      */     Object localObject;
/*      */     try {
/*  159 */       Class localClass = localGeneratedClassLoader.defineClass(str, arrayOfByte);
/*  160 */       localGeneratedClassLoader.linkClass(localClass);
/*  161 */       return localClass;
/*      */     } catch (SecurityException localSecurityException) {
/*  163 */       localObject = localSecurityException;
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  165 */       localObject = localIllegalArgumentException;
/*      */     }
/*  167 */     throw new RuntimeException("Malformed optimizer package " + localObject);
/*      */   }
/*      */ 
/*      */   byte[] compileToClassFile(CompilerEnvirons paramCompilerEnvirons, String paramString1, ScriptNode paramScriptNode, String paramString2, boolean paramBoolean)
/*      */   {
/*  176 */     this.compilerEnv = paramCompilerEnvirons;
/*      */ 
/*  178 */     transform(paramScriptNode);
/*      */ 
/*  184 */     if (paramBoolean) {
/*  185 */       paramScriptNode = paramScriptNode.getFunctionNode(0);
/*      */     }
/*      */ 
/*  188 */     initScriptNodesData(paramScriptNode);
/*      */ 
/*  190 */     this.mainClassName = paramString1;
/*  191 */     this.mainClassSignature = ClassFileWriter.classNameToSignature(paramString1);
/*      */     try
/*      */     {
/*  195 */       return generateCode(paramString2);
/*      */     } catch (ClassFileWriter.ClassFileFormatException localClassFileFormatException) {
/*  197 */       throw reportClassFileFormatException(paramScriptNode, localClassFileFormatException.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   private RuntimeException reportClassFileFormatException(ScriptNode paramScriptNode, String paramString)
/*      */   {
/*  205 */     String str = (paramScriptNode instanceof FunctionNode) ? ScriptRuntime.getMessage2("msg.while.compiling.fn", ((FunctionNode)paramScriptNode).getFunctionName(), paramString) : ScriptRuntime.getMessage1("msg.while.compiling.script", paramString);
/*      */ 
/*  209 */     return Context.reportRuntimeError(str, paramScriptNode.getSourceName(), paramScriptNode.getLineno(), null, 0);
/*      */   }
/*      */ 
/*      */   private void transform(ScriptNode paramScriptNode)
/*      */   {
/*  215 */     initOptFunctions_r(paramScriptNode);
/*      */ 
/*  217 */     int i = this.compilerEnv.getOptimizationLevel();
/*      */ 
/*  219 */     HashMap localHashMap = null;
/*  220 */     if (i > 0)
/*      */     {
/*  226 */       if (paramScriptNode.getType() == 136) {
/*  227 */         int j = paramScriptNode.getFunctionCount();
/*  228 */         for (int k = 0; k != j; k++) {
/*  229 */           OptFunctionNode localOptFunctionNode = OptFunctionNode.get(paramScriptNode, k);
/*  230 */           if (localOptFunctionNode.fnode.getFunctionType() == 1)
/*      */           {
/*  233 */             String str = localOptFunctionNode.fnode.getName();
/*  234 */             if (str.length() != 0) {
/*  235 */               if (localHashMap == null) {
/*  236 */                 localHashMap = new HashMap();
/*      */               }
/*  238 */               localHashMap.put(str, localOptFunctionNode);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  245 */     if (localHashMap != null) {
/*  246 */       this.directCallTargets = new ObjArray();
/*      */     }
/*      */ 
/*  249 */     OptTransformer localOptTransformer = new OptTransformer(localHashMap, this.directCallTargets);
/*      */ 
/*  251 */     localOptTransformer.transform(paramScriptNode);
/*      */ 
/*  253 */     if (i > 0)
/*  254 */       new Optimizer().optimize(paramScriptNode);
/*      */   }
/*      */ 
/*      */   private static void initOptFunctions_r(ScriptNode paramScriptNode)
/*      */   {
/*  260 */     int i = 0; for (int j = paramScriptNode.getFunctionCount(); i != j; i++) {
/*  261 */       FunctionNode localFunctionNode = paramScriptNode.getFunctionNode(i);
/*  262 */       new OptFunctionNode(localFunctionNode);
/*  263 */       initOptFunctions_r(localFunctionNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initScriptNodesData(ScriptNode paramScriptNode)
/*      */   {
/*  269 */     ObjArray localObjArray = new ObjArray();
/*  270 */     collectScriptNodes_r(paramScriptNode, localObjArray);
/*      */ 
/*  272 */     int i = localObjArray.size();
/*  273 */     this.scriptOrFnNodes = new ScriptNode[i];
/*  274 */     localObjArray.toArray(this.scriptOrFnNodes);
/*      */ 
/*  276 */     this.scriptOrFnIndexes = new ObjToIntMap(i);
/*  277 */     for (int j = 0; j != i; j++)
/*  278 */       this.scriptOrFnIndexes.put(this.scriptOrFnNodes[j], j);
/*      */   }
/*      */ 
/*      */   private static void collectScriptNodes_r(ScriptNode paramScriptNode, ObjArray paramObjArray)
/*      */   {
/*  285 */     paramObjArray.add(paramScriptNode);
/*  286 */     int i = paramScriptNode.getFunctionCount();
/*  287 */     for (int j = 0; j != i; j++)
/*  288 */       collectScriptNodes_r(paramScriptNode.getFunctionNode(j), paramObjArray);
/*      */   }
/*      */ 
/*      */   private byte[] generateCode(String paramString)
/*      */   {
/*  294 */     int i = this.scriptOrFnNodes[0].getType() == 136 ? 1 : 0;
/*  295 */     int j = (this.scriptOrFnNodes.length > 1) || (i == 0) ? 1 : 0;
/*      */ 
/*  297 */     String str = null;
/*  298 */     if (this.compilerEnv.isGenerateDebugInfo()) {
/*  299 */       str = this.scriptOrFnNodes[0].getSourceName();
/*      */     }
/*      */ 
/*  302 */     ClassFileWriter localClassFileWriter = new ClassFileWriter(this.mainClassName, "sun.org.mozilla.javascript.internal.NativeFunction", str);
/*      */ 
/*  305 */     localClassFileWriter.addField("_id", "I", (short)2);
/*      */ 
/*  307 */     localClassFileWriter.addField("_dcp", this.mainClassSignature, (short)2);
/*      */ 
/*  309 */     localClassFileWriter.addField("_re", "[Ljava/lang/Object;", (short)2);
/*      */ 
/*  312 */     if (j != 0) {
/*  313 */       generateFunctionConstructor(localClassFileWriter);
/*      */     }
/*      */ 
/*  316 */     if (i != 0) {
/*  317 */       localClassFileWriter.addInterface("sun/org/mozilla/javascript/internal/Script");
/*  318 */       generateScriptCtor(localClassFileWriter);
/*  319 */       generateMain(localClassFileWriter);
/*  320 */       generateExecute(localClassFileWriter);
/*      */     }
/*      */ 
/*  323 */     generateCallMethod(localClassFileWriter);
/*  324 */     generateResumeGenerator(localClassFileWriter);
/*      */ 
/*  326 */     generateNativeFunctionOverrides(localClassFileWriter, paramString);
/*      */ 
/*  328 */     int k = this.scriptOrFnNodes.length;
/*  329 */     for (int m = 0; m != k; m++) {
/*  330 */       ScriptNode localScriptNode = this.scriptOrFnNodes[m];
/*      */ 
/*  332 */       BodyCodegen localBodyCodegen = new BodyCodegen();
/*  333 */       localBodyCodegen.cfw = localClassFileWriter;
/*  334 */       localBodyCodegen.codegen = this;
/*  335 */       localBodyCodegen.compilerEnv = this.compilerEnv;
/*  336 */       localBodyCodegen.scriptOrFn = localScriptNode;
/*  337 */       localBodyCodegen.scriptOrFnIndex = m;
/*      */       try
/*      */       {
/*  340 */         localBodyCodegen.generateBodyCode();
/*      */       } catch (ClassFileWriter.ClassFileFormatException localClassFileFormatException) {
/*  342 */         throw reportClassFileFormatException(localScriptNode, localClassFileFormatException.getMessage());
/*      */       }
/*      */ 
/*  345 */       if (localScriptNode.getType() == 109) {
/*  346 */         OptFunctionNode localOptFunctionNode = OptFunctionNode.get(localScriptNode);
/*  347 */         generateFunctionInit(localClassFileWriter, localOptFunctionNode);
/*  348 */         if (localOptFunctionNode.isTargetOfDirectCall()) {
/*  349 */           emitDirectConstructor(localClassFileWriter, localOptFunctionNode);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  354 */     if (this.directCallTargets != null) {
/*  355 */       m = this.directCallTargets.size();
/*  356 */       for (int n = 0; n != m; n++) {
/*  357 */         localClassFileWriter.addField(getDirectTargetFieldName(n), this.mainClassSignature, (short)2);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  363 */     emitRegExpInit(localClassFileWriter);
/*  364 */     emitConstantDudeInitializers(localClassFileWriter);
/*      */ 
/*  366 */     return localClassFileWriter.toByteArray();
/*      */   }
/*      */ 
/*      */   private void emitDirectConstructor(ClassFileWriter paramClassFileWriter, OptFunctionNode paramOptFunctionNode)
/*      */   {
/*  383 */     paramClassFileWriter.startMethod(getDirectCtorName(paramOptFunctionNode.fnode), getBodyMethodSignature(paramOptFunctionNode.fnode), (short)10);
/*      */ 
/*  388 */     int i = paramOptFunctionNode.fnode.getParamCount();
/*  389 */     int j = 4 + i * 3 + 1;
/*      */ 
/*  391 */     paramClassFileWriter.addALoad(0);
/*  392 */     paramClassFileWriter.addALoad(1);
/*  393 */     paramClassFileWriter.addALoad(2);
/*  394 */     paramClassFileWriter.addInvoke(182, "sun/org/mozilla/javascript/internal/BaseFunction", "createObject", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/*  400 */     paramClassFileWriter.addAStore(j);
/*      */ 
/*  402 */     paramClassFileWriter.addALoad(0);
/*  403 */     paramClassFileWriter.addALoad(1);
/*  404 */     paramClassFileWriter.addALoad(2);
/*  405 */     paramClassFileWriter.addALoad(j);
/*  406 */     for (int k = 0; k < i; k++) {
/*  407 */       paramClassFileWriter.addALoad(4 + k * 3);
/*  408 */       paramClassFileWriter.addDLoad(5 + k * 3);
/*      */     }
/*  410 */     paramClassFileWriter.addALoad(4 + i * 3);
/*  411 */     paramClassFileWriter.addInvoke(184, this.mainClassName, getBodyMethodName(paramOptFunctionNode.fnode), getBodyMethodSignature(paramOptFunctionNode.fnode));
/*      */ 
/*  415 */     k = paramClassFileWriter.acquireLabel();
/*  416 */     paramClassFileWriter.add(89);
/*  417 */     paramClassFileWriter.add(193, "sun/org/mozilla/javascript/internal/Scriptable");
/*  418 */     paramClassFileWriter.add(153, k);
/*      */ 
/*  420 */     paramClassFileWriter.add(192, "sun/org/mozilla/javascript/internal/Scriptable");
/*  421 */     paramClassFileWriter.add(176);
/*  422 */     paramClassFileWriter.markLabel(k);
/*      */ 
/*  424 */     paramClassFileWriter.addALoad(j);
/*  425 */     paramClassFileWriter.add(176);
/*      */ 
/*  427 */     paramClassFileWriter.stopMethod((short)(j + 1));
/*      */   }
/*      */ 
/*      */   static boolean isGenerator(ScriptNode paramScriptNode)
/*      */   {
/*  432 */     return (paramScriptNode.getType() == 109) && (((FunctionNode)paramScriptNode).isGenerator());
/*      */   }
/*      */ 
/*      */   private void generateResumeGenerator(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  450 */     int i = 0;
/*  451 */     for (int j = 0; j < this.scriptOrFnNodes.length; j++) {
/*  452 */       if (isGenerator(this.scriptOrFnNodes[j])) {
/*  453 */         i = 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  458 */     if (i == 0) {
/*  459 */       return;
/*      */     }
/*  461 */     paramClassFileWriter.startMethod("resumeGenerator", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", (short)17);
/*      */ 
/*  470 */     paramClassFileWriter.addALoad(0);
/*  471 */     paramClassFileWriter.addALoad(1);
/*  472 */     paramClassFileWriter.addALoad(2);
/*  473 */     paramClassFileWriter.addALoad(4);
/*  474 */     paramClassFileWriter.addALoad(5);
/*  475 */     paramClassFileWriter.addILoad(3);
/*      */ 
/*  477 */     paramClassFileWriter.addLoadThis();
/*  478 */     paramClassFileWriter.add(180, paramClassFileWriter.getClassName(), "_id", "I");
/*      */ 
/*  480 */     j = paramClassFileWriter.addTableSwitch(0, this.scriptOrFnNodes.length - 1);
/*  481 */     paramClassFileWriter.markTableSwitchDefault(j);
/*  482 */     int k = paramClassFileWriter.acquireLabel();
/*      */ 
/*  484 */     for (int m = 0; m < this.scriptOrFnNodes.length; m++) {
/*  485 */       ScriptNode localScriptNode = this.scriptOrFnNodes[m];
/*  486 */       paramClassFileWriter.markTableSwitchCase(j, m, 6);
/*  487 */       if (isGenerator(localScriptNode)) {
/*  488 */         String str = "(" + this.mainClassSignature + "Lsun/org/mozilla/javascript/internal/Context;" + "Lsun/org/mozilla/javascript/internal/Scriptable;" + "Ljava/lang/Object;" + "Ljava/lang/Object;I)Ljava/lang/Object;";
/*      */ 
/*  494 */         paramClassFileWriter.addInvoke(184, this.mainClassName, getBodyMethodName(localScriptNode) + "_gen", str);
/*      */ 
/*  498 */         paramClassFileWriter.add(176);
/*      */       } else {
/*  500 */         paramClassFileWriter.add(167, k);
/*      */       }
/*      */     }
/*      */ 
/*  504 */     paramClassFileWriter.markLabel(k);
/*  505 */     pushUndefined(paramClassFileWriter);
/*  506 */     paramClassFileWriter.add(176);
/*      */ 
/*  510 */     paramClassFileWriter.stopMethod((short)6);
/*      */   }
/*      */ 
/*      */   private void generateCallMethod(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  515 */     paramClassFileWriter.startMethod("call", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;", (short)17);
/*      */ 
/*  528 */     int i = paramClassFileWriter.acquireLabel();
/*  529 */     paramClassFileWriter.addALoad(1);
/*  530 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/ScriptRuntime", "hasTopCall", "(Lsun/org/mozilla/javascript/internal/Context;)Z");
/*      */ 
/*  535 */     paramClassFileWriter.add(154, i);
/*  536 */     paramClassFileWriter.addALoad(0);
/*  537 */     paramClassFileWriter.addALoad(1);
/*  538 */     paramClassFileWriter.addALoad(2);
/*  539 */     paramClassFileWriter.addALoad(3);
/*  540 */     paramClassFileWriter.addALoad(4);
/*  541 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/ScriptRuntime", "doTopCall", "(Lsun/org/mozilla/javascript/internal/Callable;Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
/*      */ 
/*  550 */     paramClassFileWriter.add(176);
/*  551 */     paramClassFileWriter.markLabel(i);
/*      */ 
/*  554 */     paramClassFileWriter.addALoad(0);
/*  555 */     paramClassFileWriter.addALoad(1);
/*  556 */     paramClassFileWriter.addALoad(2);
/*  557 */     paramClassFileWriter.addALoad(3);
/*  558 */     paramClassFileWriter.addALoad(4);
/*      */ 
/*  560 */     int j = this.scriptOrFnNodes.length;
/*  561 */     int k = 2 <= j ? 1 : 0;
/*      */ 
/*  563 */     int m = 0;
/*  564 */     int n = 0;
/*  565 */     if (k != 0) {
/*  566 */       paramClassFileWriter.addLoadThis();
/*  567 */       paramClassFileWriter.add(180, paramClassFileWriter.getClassName(), "_id", "I");
/*      */ 
/*  570 */       m = paramClassFileWriter.addTableSwitch(1, j - 1);
/*      */     }
/*      */ 
/*  573 */     for (int i1 = 0; i1 != j; i1++) {
/*  574 */       ScriptNode localScriptNode = this.scriptOrFnNodes[i1];
/*  575 */       if (k != 0) {
/*  576 */         if (i1 == 0) {
/*  577 */           paramClassFileWriter.markTableSwitchDefault(m);
/*  578 */           n = paramClassFileWriter.getStackTop();
/*      */         } else {
/*  580 */           paramClassFileWriter.markTableSwitchCase(m, i1 - 1, n);
/*      */         }
/*      */       }
/*      */ 
/*  584 */       if (localScriptNode.getType() == 109) {
/*  585 */         OptFunctionNode localOptFunctionNode = OptFunctionNode.get(localScriptNode);
/*  586 */         if (localOptFunctionNode.isTargetOfDirectCall()) {
/*  587 */           int i2 = localOptFunctionNode.fnode.getParamCount();
/*  588 */           if (i2 != 0)
/*      */           {
/*  591 */             for (int i3 = 0; i3 != i2; i3++) {
/*  592 */               paramClassFileWriter.add(190);
/*  593 */               paramClassFileWriter.addPush(i3);
/*  594 */               int i4 = paramClassFileWriter.acquireLabel();
/*  595 */               int i5 = paramClassFileWriter.acquireLabel();
/*  596 */               paramClassFileWriter.add(164, i4);
/*      */ 
/*  598 */               paramClassFileWriter.addALoad(4);
/*  599 */               paramClassFileWriter.addPush(i3);
/*  600 */               paramClassFileWriter.add(50);
/*  601 */               paramClassFileWriter.add(167, i5);
/*  602 */               paramClassFileWriter.markLabel(i4);
/*  603 */               pushUndefined(paramClassFileWriter);
/*  604 */               paramClassFileWriter.markLabel(i5);
/*      */ 
/*  606 */               paramClassFileWriter.adjustStackTop(-1);
/*  607 */               paramClassFileWriter.addPush(0.0D);
/*      */ 
/*  609 */               paramClassFileWriter.addALoad(4);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  614 */       paramClassFileWriter.addInvoke(184, this.mainClassName, getBodyMethodName(localScriptNode), getBodyMethodSignature(localScriptNode));
/*      */ 
/*  618 */       paramClassFileWriter.add(176);
/*      */     }
/*  620 */     paramClassFileWriter.stopMethod((short)5);
/*      */   }
/*      */ 
/*      */   private void generateMain(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  626 */     paramClassFileWriter.startMethod("main", "([Ljava/lang/String;)V", (short)9);
/*      */ 
/*  631 */     paramClassFileWriter.add(187, paramClassFileWriter.getClassName());
/*  632 */     paramClassFileWriter.add(89);
/*  633 */     paramClassFileWriter.addInvoke(183, paramClassFileWriter.getClassName(), "<init>", "()V");
/*      */ 
/*  636 */     paramClassFileWriter.add(42);
/*      */ 
/*  638 */     paramClassFileWriter.addInvoke(184, this.mainMethodClass, "main", "(Lsun/org/mozilla/javascript/internal/Script;[Ljava/lang/String;)V");
/*      */ 
/*  642 */     paramClassFileWriter.add(177);
/*      */ 
/*  644 */     paramClassFileWriter.stopMethod((short)1);
/*      */   }
/*      */ 
/*      */   private void generateExecute(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  649 */     paramClassFileWriter.startMethod("exec", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)Ljava/lang/Object;", (short)17);
/*      */ 
/*  659 */     paramClassFileWriter.addLoadThis();
/*  660 */     paramClassFileWriter.addALoad(1);
/*  661 */     paramClassFileWriter.addALoad(2);
/*  662 */     paramClassFileWriter.add(89);
/*  663 */     paramClassFileWriter.add(1);
/*  664 */     paramClassFileWriter.addInvoke(182, paramClassFileWriter.getClassName(), "call", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
/*      */ 
/*  673 */     paramClassFileWriter.add(176);
/*      */ 
/*  675 */     paramClassFileWriter.stopMethod((short)3);
/*      */   }
/*      */ 
/*      */   private void generateScriptCtor(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  680 */     paramClassFileWriter.startMethod("<init>", "()V", (short)1);
/*      */ 
/*  682 */     paramClassFileWriter.addLoadThis();
/*  683 */     paramClassFileWriter.addInvoke(183, "sun.org.mozilla.javascript.internal.NativeFunction", "<init>", "()V");
/*      */ 
/*  686 */     paramClassFileWriter.addLoadThis();
/*  687 */     paramClassFileWriter.addPush(0);
/*  688 */     paramClassFileWriter.add(181, paramClassFileWriter.getClassName(), "_id", "I");
/*      */ 
/*  690 */     paramClassFileWriter.add(177);
/*      */ 
/*  692 */     paramClassFileWriter.stopMethod((short)1);
/*      */   }
/*      */ 
/*      */   private void generateFunctionConstructor(ClassFileWriter paramClassFileWriter)
/*      */   {
/*  701 */     paramClassFileWriter.startMethod("<init>", "(Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Context;I)V", (short)1);
/*      */ 
/*  703 */     paramClassFileWriter.addALoad(0);
/*  704 */     paramClassFileWriter.addInvoke(183, "sun.org.mozilla.javascript.internal.NativeFunction", "<init>", "()V");
/*      */ 
/*  707 */     paramClassFileWriter.addLoadThis();
/*  708 */     paramClassFileWriter.addILoad(3);
/*  709 */     paramClassFileWriter.add(181, paramClassFileWriter.getClassName(), "_id", "I");
/*      */ 
/*  711 */     paramClassFileWriter.addLoadThis();
/*  712 */     paramClassFileWriter.addALoad(2);
/*  713 */     paramClassFileWriter.addALoad(1);
/*      */ 
/*  715 */     int i = this.scriptOrFnNodes[0].getType() == 136 ? 1 : 0;
/*  716 */     int j = this.scriptOrFnNodes.length;
/*  717 */     if (i == j) throw badTree();
/*  718 */     int k = 2 <= j - i ? 1 : 0;
/*      */ 
/*  720 */     int m = 0;
/*  721 */     int n = 0;
/*  722 */     if (k != 0) {
/*  723 */       paramClassFileWriter.addILoad(3);
/*      */ 
/*  726 */       m = paramClassFileWriter.addTableSwitch(i + 1, j - 1);
/*      */     }
/*      */ 
/*  729 */     for (int i1 = i; i1 != j; i1++) {
/*  730 */       if (k != 0) {
/*  731 */         if (i1 == i) {
/*  732 */           paramClassFileWriter.markTableSwitchDefault(m);
/*  733 */           n = paramClassFileWriter.getStackTop();
/*      */         } else {
/*  735 */           paramClassFileWriter.markTableSwitchCase(m, i1 - 1 - i, n);
/*      */         }
/*      */       }
/*      */ 
/*  739 */       OptFunctionNode localOptFunctionNode = OptFunctionNode.get(this.scriptOrFnNodes[i1]);
/*  740 */       paramClassFileWriter.addInvoke(183, this.mainClassName, getFunctionInitMethodName(localOptFunctionNode), "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)V");
/*      */ 
/*  744 */       paramClassFileWriter.add(177);
/*      */     }
/*      */ 
/*  748 */     paramClassFileWriter.stopMethod((short)4);
/*      */   }
/*      */ 
/*      */   private void generateFunctionInit(ClassFileWriter paramClassFileWriter, OptFunctionNode paramOptFunctionNode)
/*      */   {
/*  756 */     paramClassFileWriter.startMethod(getFunctionInitMethodName(paramOptFunctionNode), "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)V", (short)18);
/*      */ 
/*  762 */     paramClassFileWriter.addLoadThis();
/*  763 */     paramClassFileWriter.addALoad(1);
/*  764 */     paramClassFileWriter.addALoad(2);
/*  765 */     paramClassFileWriter.addInvoke(182, "sun/org/mozilla/javascript/internal/NativeFunction", "initScriptFunction", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;)V");
/*      */ 
/*  773 */     int i = paramOptFunctionNode.fnode.getRegexpCount();
/*  774 */     if (i != 0) {
/*  775 */       paramClassFileWriter.addLoadThis();
/*  776 */       pushRegExpArray(paramClassFileWriter, paramOptFunctionNode.fnode, 1, 2);
/*  777 */       paramClassFileWriter.add(181, this.mainClassName, "_re", "[Ljava/lang/Object;");
/*      */     }
/*      */ 
/*  781 */     paramClassFileWriter.add(177);
/*      */ 
/*  783 */     paramClassFileWriter.stopMethod((short)3);
/*      */   }
/*      */ 
/*      */   private void generateNativeFunctionOverrides(ClassFileWriter paramClassFileWriter, String paramString)
/*      */   {
/*  792 */     paramClassFileWriter.startMethod("getLanguageVersion", "()I", (short)1);
/*      */ 
/*  795 */     paramClassFileWriter.addPush(this.compilerEnv.getLanguageVersion());
/*  796 */     paramClassFileWriter.add(172);
/*      */ 
/*  799 */     paramClassFileWriter.stopMethod((short)1);
/*      */ 
/*  812 */     for (int i = 0; i != 6; i++)
/*  813 */       if ((i != 4) || (paramString != null))
/*      */       {
/*      */         short s;
/*  823 */         switch (i) {
/*      */         case 0:
/*  825 */           s = 1;
/*  826 */           paramClassFileWriter.startMethod("getFunctionName", "()Ljava/lang/String;", (short)1);
/*      */ 
/*  828 */           break;
/*      */         case 1:
/*  830 */           s = 1;
/*  831 */           paramClassFileWriter.startMethod("getParamCount", "()I", (short)1);
/*      */ 
/*  833 */           break;
/*      */         case 2:
/*  835 */           s = 1;
/*  836 */           paramClassFileWriter.startMethod("getParamAndVarCount", "()I", (short)1);
/*      */ 
/*  838 */           break;
/*      */         case 3:
/*  840 */           s = 2;
/*  841 */           paramClassFileWriter.startMethod("getParamOrVarName", "(I)Ljava/lang/String;", (short)1);
/*      */ 
/*  843 */           break;
/*      */         case 5:
/*  845 */           s = 3;
/*  846 */           paramClassFileWriter.startMethod("getParamOrVarConst", "(I)Z", (short)1);
/*      */ 
/*  848 */           break;
/*      */         case 4:
/*  850 */           s = 1;
/*  851 */           paramClassFileWriter.startMethod("getEncodedSource", "()Ljava/lang/String;", (short)1);
/*      */ 
/*  853 */           paramClassFileWriter.addPush(paramString);
/*  854 */           break;
/*      */         default:
/*  856 */           throw Kit.codeBug();
/*      */         }
/*      */ 
/*  859 */         int j = this.scriptOrFnNodes.length;
/*      */ 
/*  861 */         int k = 0;
/*  862 */         int m = 0;
/*  863 */         if (j > 1)
/*      */         {
/*  866 */           paramClassFileWriter.addLoadThis();
/*  867 */           paramClassFileWriter.add(180, paramClassFileWriter.getClassName(), "_id", "I");
/*      */ 
/*  871 */           k = paramClassFileWriter.addTableSwitch(1, j - 1);
/*      */         }
/*      */ 
/*  874 */         for (int n = 0; n != j; n++) {
/*  875 */           ScriptNode localScriptNode = this.scriptOrFnNodes[n];
/*  876 */           if (n == 0) {
/*  877 */             if (j > 1) {
/*  878 */               paramClassFileWriter.markTableSwitchDefault(k);
/*  879 */               m = paramClassFileWriter.getStackTop();
/*      */             }
/*      */           }
/*  882 */           else paramClassFileWriter.markTableSwitchCase(k, n - 1, m);
/*      */           int i1;
/*      */           int i3;
/*  887 */           switch (i)
/*      */           {
/*      */           case 0:
/*  890 */             if (localScriptNode.getType() == 136) {
/*  891 */               paramClassFileWriter.addPush("");
/*      */             } else {
/*  893 */               String str1 = ((FunctionNode)localScriptNode).getName();
/*  894 */               paramClassFileWriter.addPush(str1);
/*      */             }
/*  896 */             paramClassFileWriter.add(176);
/*  897 */             break;
/*      */           case 1:
/*  901 */             paramClassFileWriter.addPush(localScriptNode.getParamCount());
/*  902 */             paramClassFileWriter.add(172);
/*  903 */             break;
/*      */           case 2:
/*  907 */             paramClassFileWriter.addPush(localScriptNode.getParamAndVarCount());
/*  908 */             paramClassFileWriter.add(172);
/*  909 */             break;
/*      */           case 3:
/*  914 */             i1 = localScriptNode.getParamAndVarCount();
/*  915 */             if (i1 == 0)
/*      */             {
/*  919 */               paramClassFileWriter.add(1);
/*  920 */               paramClassFileWriter.add(176);
/*  921 */             } else if (i1 == 1)
/*      */             {
/*  924 */               paramClassFileWriter.addPush(localScriptNode.getParamOrVarName(0));
/*  925 */               paramClassFileWriter.add(176);
/*      */             }
/*      */             else {
/*  928 */               paramClassFileWriter.addILoad(1);
/*      */ 
/*  931 */               int i2 = paramClassFileWriter.addTableSwitch(1, i1 - 1);
/*      */ 
/*  933 */               for (i3 = 0; i3 != i1; i3++) {
/*  934 */                 if (paramClassFileWriter.getStackTop() != 0) Kit.codeBug();
/*  935 */                 String str2 = localScriptNode.getParamOrVarName(i3);
/*  936 */                 if (i3 == 0)
/*  937 */                   paramClassFileWriter.markTableSwitchDefault(i2);
/*      */                 else {
/*  939 */                   paramClassFileWriter.markTableSwitchCase(i2, i3 - 1, 0);
/*      */                 }
/*      */ 
/*  942 */                 paramClassFileWriter.addPush(str2);
/*  943 */                 paramClassFileWriter.add(176);
/*      */               }
/*      */             }
/*  946 */             break;
/*      */           case 5:
/*  951 */             i1 = localScriptNode.getParamAndVarCount();
/*  952 */             boolean[] arrayOfBoolean = localScriptNode.getParamAndVarConst();
/*  953 */             if (i1 == 0)
/*      */             {
/*  957 */               paramClassFileWriter.add(3);
/*  958 */               paramClassFileWriter.add(172);
/*  959 */             } else if (i1 == 1)
/*      */             {
/*  962 */               paramClassFileWriter.addPush(arrayOfBoolean[0]);
/*  963 */               paramClassFileWriter.add(172);
/*      */             }
/*      */             else {
/*  966 */               paramClassFileWriter.addILoad(1);
/*      */ 
/*  969 */               i3 = paramClassFileWriter.addTableSwitch(1, i1 - 1);
/*      */ 
/*  971 */               for (int i4 = 0; i4 != i1; i4++) {
/*  972 */                 if (paramClassFileWriter.getStackTop() != 0) Kit.codeBug();
/*  973 */                 if (i4 == 0)
/*  974 */                   paramClassFileWriter.markTableSwitchDefault(i3);
/*      */                 else {
/*  976 */                   paramClassFileWriter.markTableSwitchCase(i3, i4 - 1, 0);
/*      */                 }
/*      */ 
/*  979 */                 paramClassFileWriter.addPush(arrayOfBoolean[i4]);
/*  980 */                 paramClassFileWriter.add(172);
/*      */               }
/*      */             }
/*  983 */             break;
/*      */           case 4:
/*  988 */             paramClassFileWriter.addPush(localScriptNode.getEncodedSourceStart());
/*  989 */             paramClassFileWriter.addPush(localScriptNode.getEncodedSourceEnd());
/*  990 */             paramClassFileWriter.addInvoke(182, "java/lang/String", "substring", "(II)Ljava/lang/String;");
/*      */ 
/*  994 */             paramClassFileWriter.add(176);
/*  995 */             break;
/*      */           default:
/*  998 */             throw Kit.codeBug();
/*      */           }
/*      */         }
/*      */ 
/* 1002 */         paramClassFileWriter.stopMethod(s);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void emitRegExpInit(ClassFileWriter paramClassFileWriter)
/*      */   {
/* 1010 */     int i = 0;
/* 1011 */     for (int j = 0; j != this.scriptOrFnNodes.length; j++) {
/* 1012 */       i += this.scriptOrFnNodes[j].getRegexpCount();
/*      */     }
/* 1014 */     if (i == 0) {
/* 1015 */       return;
/*      */     }
/*      */ 
/* 1018 */     paramClassFileWriter.startMethod("_reInit", "(Lsun/org/mozilla/javascript/internal/RegExpProxy;Lsun/org/mozilla/javascript/internal/Context;)V", (short)42);
/*      */ 
/* 1021 */     paramClassFileWriter.addField("_reInitDone", "Z", (short)10);
/*      */ 
/* 1024 */     paramClassFileWriter.add(178, this.mainClassName, "_reInitDone", "Z");
/* 1025 */     j = paramClassFileWriter.acquireLabel();
/* 1026 */     paramClassFileWriter.add(153, j);
/* 1027 */     paramClassFileWriter.add(177);
/* 1028 */     paramClassFileWriter.markLabel(j);
/*      */ 
/* 1030 */     for (int k = 0; k != this.scriptOrFnNodes.length; k++) {
/* 1031 */       ScriptNode localScriptNode = this.scriptOrFnNodes[k];
/* 1032 */       int m = localScriptNode.getRegexpCount();
/* 1033 */       for (int n = 0; n != m; n++) {
/* 1034 */         String str1 = getCompiledRegexpName(localScriptNode, n);
/* 1035 */         String str2 = "Ljava/lang/Object;";
/* 1036 */         String str3 = localScriptNode.getRegexpString(n);
/* 1037 */         String str4 = localScriptNode.getRegexpFlags(n);
/* 1038 */         paramClassFileWriter.addField(str1, str2, (short)10);
/*      */ 
/* 1041 */         paramClassFileWriter.addALoad(0);
/* 1042 */         paramClassFileWriter.addALoad(1);
/* 1043 */         paramClassFileWriter.addPush(str3);
/* 1044 */         if (str4 == null)
/* 1045 */           paramClassFileWriter.add(1);
/*      */         else {
/* 1047 */           paramClassFileWriter.addPush(str4);
/*      */         }
/* 1049 */         paramClassFileWriter.addInvoke(185, "sun/org/mozilla/javascript/internal/RegExpProxy", "compileRegExp", "(Lsun/org/mozilla/javascript/internal/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
/*      */ 
/* 1055 */         paramClassFileWriter.add(179, this.mainClassName, str1, str2);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1060 */     paramClassFileWriter.addPush(1);
/* 1061 */     paramClassFileWriter.add(179, this.mainClassName, "_reInitDone", "Z");
/* 1062 */     paramClassFileWriter.add(177);
/* 1063 */     paramClassFileWriter.stopMethod((short)2);
/*      */   }
/*      */ 
/*      */   private void emitConstantDudeInitializers(ClassFileWriter paramClassFileWriter)
/*      */   {
/* 1068 */     int i = this.itsConstantListSize;
/* 1069 */     if (i == 0) {
/* 1070 */       return;
/*      */     }
/* 1072 */     paramClassFileWriter.startMethod("<clinit>", "()V", (short)24);
/*      */ 
/* 1075 */     double[] arrayOfDouble = this.itsConstantList;
/* 1076 */     for (int j = 0; j != i; j++) {
/* 1077 */       double d = arrayOfDouble[j];
/* 1078 */       String str1 = "_k" + j;
/* 1079 */       String str2 = getStaticConstantWrapperType(d);
/* 1080 */       paramClassFileWriter.addField(str1, str2, (short)10);
/*      */ 
/* 1083 */       int k = (int)d;
/* 1084 */       if (k == d) {
/* 1085 */         paramClassFileWriter.add(187, "java/lang/Integer");
/* 1086 */         paramClassFileWriter.add(89);
/* 1087 */         paramClassFileWriter.addPush(k);
/* 1088 */         paramClassFileWriter.addInvoke(183, "java/lang/Integer", "<init>", "(I)V");
/*      */       }
/*      */       else {
/* 1091 */         paramClassFileWriter.addPush(d);
/* 1092 */         addDoubleWrap(paramClassFileWriter);
/*      */       }
/* 1094 */       paramClassFileWriter.add(179, this.mainClassName, str1, str2);
/*      */     }
/*      */ 
/* 1098 */     paramClassFileWriter.add(177);
/* 1099 */     paramClassFileWriter.stopMethod((short)0);
/*      */   }
/*      */ 
/*      */   void pushRegExpArray(ClassFileWriter paramClassFileWriter, ScriptNode paramScriptNode, int paramInt1, int paramInt2)
/*      */   {
/* 1105 */     int i = paramScriptNode.getRegexpCount();
/* 1106 */     if (i == 0) throw badTree();
/*      */ 
/* 1108 */     paramClassFileWriter.addPush(i);
/* 1109 */     paramClassFileWriter.add(189, "java/lang/Object");
/*      */ 
/* 1111 */     paramClassFileWriter.addALoad(paramInt1);
/* 1112 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/ScriptRuntime", "checkRegExpProxy", "(Lsun/org/mozilla/javascript/internal/Context;)Lsun/org/mozilla/javascript/internal/RegExpProxy;");
/*      */ 
/* 1118 */     paramClassFileWriter.add(89);
/* 1119 */     paramClassFileWriter.addALoad(paramInt1);
/* 1120 */     paramClassFileWriter.addInvoke(184, this.mainClassName, "_reInit", "(Lsun/org/mozilla/javascript/internal/RegExpProxy;Lsun/org/mozilla/javascript/internal/Context;)V");
/*      */ 
/* 1122 */     for (int j = 0; j != i; j++)
/*      */     {
/* 1124 */       paramClassFileWriter.add(92);
/* 1125 */       paramClassFileWriter.addALoad(paramInt1);
/* 1126 */       paramClassFileWriter.addALoad(paramInt2);
/* 1127 */       paramClassFileWriter.add(178, this.mainClassName, getCompiledRegexpName(paramScriptNode, j), "Ljava/lang/Object;");
/*      */ 
/* 1130 */       paramClassFileWriter.addInvoke(185, "sun/org/mozilla/javascript/internal/RegExpProxy", "wrapRegExp", "(Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Ljava/lang/Object;)Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1138 */       paramClassFileWriter.addPush(j);
/* 1139 */       paramClassFileWriter.add(95);
/* 1140 */       paramClassFileWriter.add(83);
/*      */     }
/*      */ 
/* 1144 */     paramClassFileWriter.add(87);
/*      */   }
/*      */ 
/*      */   void pushNumberAsObject(ClassFileWriter paramClassFileWriter, double paramDouble)
/*      */   {
/* 1149 */     if (paramDouble == 0.0D) {
/* 1150 */       if (1.0D / paramDouble > 0.0D)
/*      */       {
/* 1152 */         paramClassFileWriter.add(178, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime", "zeroObj", "Ljava/lang/Double;");
/*      */       }
/*      */       else
/*      */       {
/* 1156 */         paramClassFileWriter.addPush(paramDouble);
/* 1157 */         addDoubleWrap(paramClassFileWriter);
/*      */       }
/*      */     } else {
/* 1160 */       if (paramDouble == 1.0D) {
/* 1161 */         paramClassFileWriter.add(178, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime", "oneObj", "Ljava/lang/Double;");
/*      */ 
/* 1164 */         return;
/*      */       }
/* 1166 */       if (paramDouble == -1.0D) {
/* 1167 */         paramClassFileWriter.add(178, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime", "minusOneObj", "Ljava/lang/Double;");
/*      */       }
/* 1171 */       else if (paramDouble != paramDouble) {
/* 1172 */         paramClassFileWriter.add(178, "sun/org/mozilla/javascript/internal/ScriptRuntime", "NaNobj", "Ljava/lang/Double;");
/*      */       }
/* 1176 */       else if (this.itsConstantListSize >= 2000)
/*      */       {
/* 1181 */         paramClassFileWriter.addPush(paramDouble);
/* 1182 */         addDoubleWrap(paramClassFileWriter);
/*      */       }
/*      */       else {
/* 1185 */         int i = this.itsConstantListSize;
/* 1186 */         int j = 0;
/* 1187 */         if (i == 0) {
/* 1188 */           this.itsConstantList = new double[64];
/*      */         } else {
/* 1190 */           localObject = this.itsConstantList;
/* 1191 */           while ((j != i) && (localObject[j] != paramDouble)) {
/* 1192 */             j++;
/*      */           }
/* 1194 */           if (i == localObject.length) {
/* 1195 */             localObject = new double[i * 2];
/* 1196 */             System.arraycopy(this.itsConstantList, 0, localObject, 0, i);
/* 1197 */             this.itsConstantList = ((double[])localObject);
/*      */           }
/*      */         }
/* 1200 */         if (j == i) {
/* 1201 */           this.itsConstantList[i] = paramDouble;
/* 1202 */           this.itsConstantListSize = (i + 1);
/*      */         }
/* 1204 */         Object localObject = "_k" + j;
/* 1205 */         String str = getStaticConstantWrapperType(paramDouble);
/* 1206 */         paramClassFileWriter.add(178, this.mainClassName, (String)localObject, str);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addDoubleWrap(ClassFileWriter paramClassFileWriter)
/*      */   {
/* 1213 */     paramClassFileWriter.addInvoke(184, "sun/org/mozilla/javascript/internal/optimizer/OptRuntime", "wrapDouble", "(D)Ljava/lang/Double;");
/*      */   }
/*      */ 
/*      */   private static String getStaticConstantWrapperType(double paramDouble)
/*      */   {
/* 1220 */     int i = (int)paramDouble;
/* 1221 */     if (i == paramDouble) {
/* 1222 */       return "Ljava/lang/Integer;";
/*      */     }
/* 1224 */     return "Ljava/lang/Double;";
/*      */   }
/*      */ 
/*      */   static void pushUndefined(ClassFileWriter paramClassFileWriter)
/*      */   {
/* 1229 */     paramClassFileWriter.add(178, "sun/org/mozilla/javascript/internal/Undefined", "instance", "Ljava/lang/Object;");
/*      */   }
/*      */ 
/*      */   int getIndex(ScriptNode paramScriptNode)
/*      */   {
/* 1235 */     return this.scriptOrFnIndexes.getExisting(paramScriptNode);
/*      */   }
/*      */ 
/*      */   static String getDirectTargetFieldName(int paramInt)
/*      */   {
/* 1240 */     return "_dt" + paramInt;
/*      */   }
/*      */ 
/*      */   String getDirectCtorName(ScriptNode paramScriptNode)
/*      */   {
/* 1245 */     return "_n" + getIndex(paramScriptNode);
/*      */   }
/*      */ 
/*      */   String getBodyMethodName(ScriptNode paramScriptNode)
/*      */   {
/* 1250 */     return "_c_" + cleanName(paramScriptNode) + "_" + getIndex(paramScriptNode);
/*      */   }
/*      */ 
/*      */   String cleanName(ScriptNode paramScriptNode)
/*      */   {
/* 1258 */     String str = "";
/* 1259 */     if ((paramScriptNode instanceof FunctionNode)) {
/* 1260 */       Name localName = ((FunctionNode)paramScriptNode).getFunctionName();
/* 1261 */       if (localName == null)
/* 1262 */         str = "anonymous";
/*      */       else
/* 1264 */         str = localName.getIdentifier();
/*      */     }
/*      */     else {
/* 1267 */       str = "script";
/*      */     }
/* 1269 */     return str;
/*      */   }
/*      */ 
/*      */   String getBodyMethodSignature(ScriptNode paramScriptNode)
/*      */   {
/* 1274 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1275 */     localStringBuffer.append('(');
/* 1276 */     localStringBuffer.append(this.mainClassSignature);
/* 1277 */     localStringBuffer.append("Lsun/org/mozilla/javascript/internal/Context;Lsun/org/mozilla/javascript/internal/Scriptable;Lsun/org/mozilla/javascript/internal/Scriptable;");
/*      */ 
/* 1280 */     if (paramScriptNode.getType() == 109) {
/* 1281 */       OptFunctionNode localOptFunctionNode = OptFunctionNode.get(paramScriptNode);
/* 1282 */       if (localOptFunctionNode.isTargetOfDirectCall()) {
/* 1283 */         int i = localOptFunctionNode.fnode.getParamCount();
/* 1284 */         for (int j = 0; j != i; j++) {
/* 1285 */           localStringBuffer.append("Ljava/lang/Object;D");
/*      */         }
/*      */       }
/*      */     }
/* 1289 */     localStringBuffer.append("[Ljava/lang/Object;)Ljava/lang/Object;");
/* 1290 */     return localStringBuffer.toString();
/*      */   }
/*      */ 
/*      */   String getFunctionInitMethodName(OptFunctionNode paramOptFunctionNode)
/*      */   {
/* 1295 */     return "_i" + getIndex(paramOptFunctionNode.fnode);
/*      */   }
/*      */ 
/*      */   String getCompiledRegexpName(ScriptNode paramScriptNode, int paramInt)
/*      */   {
/* 1300 */     return "_re" + getIndex(paramScriptNode) + "_" + paramInt;
/*      */   }
/*      */ 
/*      */   static RuntimeException badTree()
/*      */   {
/* 1305 */     throw new RuntimeException("Bad tree in codegen");
/*      */   }
/*      */ 
/*      */   void setMainMethodClass(String paramString)
/*      */   {
/* 1310 */     this.mainMethodClass = paramString;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.Codegen
 * JD-Core Version:    0.6.2
 */