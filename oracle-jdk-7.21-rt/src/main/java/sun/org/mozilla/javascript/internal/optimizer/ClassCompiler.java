/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ import sun.org.mozilla.javascript.internal.CompilerEnvirons;
/*     */ import sun.org.mozilla.javascript.internal.IRFactory;
/*     */ import sun.org.mozilla.javascript.internal.JavaAdapter;
/*     */ import sun.org.mozilla.javascript.internal.ObjToIntMap;
/*     */ import sun.org.mozilla.javascript.internal.Parser;
/*     */ import sun.org.mozilla.javascript.internal.ScriptRuntime;
/*     */ import sun.org.mozilla.javascript.internal.ast.AstRoot;
/*     */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*     */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*     */ 
/*     */ public class ClassCompiler
/*     */ {
/*     */   private String mainMethodClassName;
/*     */   private CompilerEnvirons compilerEnv;
/*     */   private Class<?> targetExtends;
/*     */   private Class<?>[] targetImplements;
/*     */ 
/*     */   public ClassCompiler(CompilerEnvirons paramCompilerEnvirons)
/*     */   {
/*  61 */     if (paramCompilerEnvirons == null) throw new IllegalArgumentException();
/*  62 */     this.compilerEnv = paramCompilerEnvirons;
/*  63 */     this.mainMethodClassName = "sun.org.mozilla.javascript.internal.optimizer.OptRuntime";
/*     */   }
/*     */ 
/*     */   public void setMainMethodClass(String paramString)
/*     */   {
/*  77 */     this.mainMethodClassName = paramString;
/*     */   }
/*     */ 
/*     */   public String getMainMethodClass()
/*     */   {
/*  86 */     return this.mainMethodClassName;
/*     */   }
/*     */ 
/*     */   public CompilerEnvirons getCompilerEnv()
/*     */   {
/*  94 */     return this.compilerEnv;
/*     */   }
/*     */ 
/*     */   public Class<?> getTargetExtends()
/*     */   {
/* 102 */     return this.targetExtends;
/*     */   }
/*     */ 
/*     */   public void setTargetExtends(Class<?> paramClass)
/*     */   {
/* 112 */     this.targetExtends = paramClass;
/*     */   }
/*     */ 
/*     */   public Class<?>[] getTargetImplements()
/*     */   {
/* 120 */     return this.targetImplements == null ? null : (Class[])this.targetImplements.clone();
/*     */   }
/*     */ 
/*     */   public void setTargetImplements(Class<?>[] paramArrayOfClass)
/*     */   {
/* 131 */     this.targetImplements = (paramArrayOfClass == null ? null : (Class[])paramArrayOfClass.clone());
/*     */   }
/*     */ 
/*     */   protected String makeAuxiliaryClassName(String paramString1, String paramString2)
/*     */   {
/* 144 */     return paramString1 + paramString2;
/*     */   }
/*     */ 
/*     */   public Object[] compileToClassFiles(String paramString1, String paramString2, int paramInt, String paramString3)
/*     */   {
/* 165 */     Parser localParser = new Parser(this.compilerEnv);
/* 166 */     AstRoot localAstRoot = localParser.parse(paramString1, paramString2, paramInt);
/* 167 */     IRFactory localIRFactory = new IRFactory(this.compilerEnv);
/* 168 */     ScriptNode localScriptNode = localIRFactory.transformTree(localAstRoot);
/*     */ 
/* 171 */     localIRFactory = null;
/* 172 */     localAstRoot = null;
/* 173 */     localParser = null;
/*     */ 
/* 175 */     Class localClass = getTargetExtends();
/* 176 */     Class[] arrayOfClass = getTargetImplements();
/*     */ 
/* 178 */     int i = (arrayOfClass == null) && (localClass == null) ? 1 : 0;
/*     */     String str1;
/* 179 */     if (i != 0)
/* 180 */       str1 = paramString3;
/*     */     else {
/* 182 */       str1 = makeAuxiliaryClassName(paramString3, "1");
/*     */     }
/*     */ 
/* 185 */     Codegen localCodegen = new Codegen();
/* 186 */     localCodegen.setMainMethodClass(this.mainMethodClassName);
/* 187 */     byte[] arrayOfByte1 = localCodegen.compileToClassFile(this.compilerEnv, str1, localScriptNode, localScriptNode.getEncodedSource(), false);
/*     */ 
/* 192 */     if (i != 0) {
/* 193 */       return new Object[] { str1, arrayOfByte1 };
/*     */     }
/* 195 */     int j = localScriptNode.getFunctionCount();
/* 196 */     ObjToIntMap localObjToIntMap = new ObjToIntMap(j);
/* 197 */     for (int k = 0; k != j; k++) {
/* 198 */       FunctionNode localFunctionNode = localScriptNode.getFunctionNode(k);
/* 199 */       String str2 = localFunctionNode.getName();
/* 200 */       if ((str2 != null) && (str2.length() != 0)) {
/* 201 */         localObjToIntMap.put(str2, localFunctionNode.getParamCount());
/*     */       }
/*     */     }
/* 204 */     if (localClass == null) {
/* 205 */       localClass = ScriptRuntime.ObjectClass;
/*     */     }
/* 207 */     byte[] arrayOfByte2 = JavaAdapter.createAdapterCode(localObjToIntMap, paramString3, localClass, arrayOfClass, str1);
/*     */ 
/* 212 */     return new Object[] { paramString3, arrayOfByte2, str1, arrayOfByte1 };
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.ClassCompiler
 * JD-Core Version:    0.6.2
 */