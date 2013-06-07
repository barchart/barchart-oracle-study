/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ import sun.org.mozilla.javascript.internal.Kit;
/*     */ import sun.org.mozilla.javascript.internal.Node;
/*     */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*     */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*     */ 
/*     */ final class OptFunctionNode
/*     */ {
/*     */   FunctionNode fnode;
/*     */   private boolean[] numberVarFlags;
/* 147 */   private int directTargetIndex = -1;
/*     */   private boolean itsParameterNumberContext;
/*     */   boolean itsContainsCalls0;
/*     */   boolean itsContainsCalls1;
/*     */ 
/*     */   OptFunctionNode(FunctionNode paramFunctionNode)
/*     */   {
/*  50 */     this.fnode = paramFunctionNode;
/*  51 */     paramFunctionNode.setCompilerData(this);
/*     */   }
/*     */ 
/*     */   static OptFunctionNode get(ScriptNode paramScriptNode, int paramInt)
/*     */   {
/*  56 */     FunctionNode localFunctionNode = paramScriptNode.getFunctionNode(paramInt);
/*  57 */     return (OptFunctionNode)localFunctionNode.getCompilerData();
/*     */   }
/*     */ 
/*     */   static OptFunctionNode get(ScriptNode paramScriptNode)
/*     */   {
/*  62 */     return (OptFunctionNode)paramScriptNode.getCompilerData();
/*     */   }
/*     */ 
/*     */   boolean isTargetOfDirectCall()
/*     */   {
/*  67 */     return this.directTargetIndex >= 0;
/*     */   }
/*     */ 
/*     */   int getDirectTargetIndex()
/*     */   {
/*  72 */     return this.directTargetIndex;
/*     */   }
/*     */ 
/*     */   void setDirectTargetIndex(int paramInt)
/*     */   {
/*  78 */     if ((paramInt < 0) || (this.directTargetIndex >= 0))
/*  79 */       Kit.codeBug();
/*  80 */     this.directTargetIndex = paramInt;
/*     */   }
/*     */ 
/*     */   void setParameterNumberContext(boolean paramBoolean)
/*     */   {
/*  85 */     this.itsParameterNumberContext = paramBoolean;
/*     */   }
/*     */ 
/*     */   boolean getParameterNumberContext()
/*     */   {
/*  90 */     return this.itsParameterNumberContext;
/*     */   }
/*     */ 
/*     */   int getVarCount()
/*     */   {
/*  95 */     return this.fnode.getParamAndVarCount();
/*     */   }
/*     */ 
/*     */   boolean isParameter(int paramInt)
/*     */   {
/* 100 */     return paramInt < this.fnode.getParamCount();
/*     */   }
/*     */ 
/*     */   boolean isNumberVar(int paramInt)
/*     */   {
/* 105 */     paramInt -= this.fnode.getParamCount();
/* 106 */     if ((paramInt >= 0) && (this.numberVarFlags != null)) {
/* 107 */       return this.numberVarFlags[paramInt];
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */   void setIsNumberVar(int paramInt)
/*     */   {
/* 114 */     paramInt -= this.fnode.getParamCount();
/*     */ 
/* 116 */     if (paramInt < 0) Kit.codeBug();
/* 117 */     if (this.numberVarFlags == null) {
/* 118 */       int i = this.fnode.getParamAndVarCount() - this.fnode.getParamCount();
/* 119 */       this.numberVarFlags = new boolean[i];
/*     */     }
/* 121 */     this.numberVarFlags[paramInt] = true;
/*     */   }
/*     */ 
/*     */   int getVarIndex(Node paramNode)
/*     */   {
/* 126 */     int i = paramNode.getIntProp(7, -1);
/* 127 */     if (i == -1)
/*     */     {
/* 129 */       int j = paramNode.getType();
/*     */       Node localNode;
/* 130 */       if (j == 55)
/* 131 */         localNode = paramNode;
/* 132 */       else if ((j == 56) || (j == 156))
/*     */       {
/* 134 */         localNode = paramNode.getFirstChild();
/*     */       }
/* 136 */       else throw Kit.codeBug();
/*     */ 
/* 138 */       i = this.fnode.getIndexForNameNode(localNode);
/* 139 */       if (i < 0) throw Kit.codeBug();
/* 140 */       paramNode.putIntProp(7, i);
/*     */     }
/* 142 */     return i;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.OptFunctionNode
 * JD-Core Version:    0.6.2
 */