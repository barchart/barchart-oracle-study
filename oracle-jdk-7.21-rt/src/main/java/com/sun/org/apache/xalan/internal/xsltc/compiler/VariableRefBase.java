/*     */ package com.sun.org.apache.xalan.internal.xsltc.compiler;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
/*     */ 
/*     */ class VariableRefBase extends Expression
/*     */ {
/*     */   protected VariableBase _variable;
/*  44 */   protected Closure _closure = null;
/*     */ 
/*     */   public VariableRefBase(VariableBase variable) {
/*  47 */     this._variable = variable;
/*  48 */     variable.addReference(this);
/*     */   }
/*     */ 
/*     */   public VariableRefBase() {
/*  52 */     this._variable = null;
/*     */   }
/*     */ 
/*     */   public VariableBase getVariable()
/*     */   {
/*  59 */     return this._variable;
/*     */   }
/*     */ 
/*     */   public void addParentDependency()
/*     */   {
/*  75 */     SyntaxTreeNode node = this;
/*  76 */     while ((node != null) && (!(node instanceof TopLevelElement))) {
/*  77 */       node = node.getParent();
/*     */     }
/*     */ 
/*  80 */     TopLevelElement parent = (TopLevelElement)node;
/*  81 */     if (parent != null) {
/*  82 */       VariableBase var = this._variable;
/*  83 */       if (this._variable._ignore) {
/*  84 */         if ((this._variable instanceof Variable)) {
/*  85 */           var = parent.getSymbolTable().lookupVariable(this._variable._name);
/*     */         }
/*  87 */         else if ((this._variable instanceof Param)) {
/*  88 */           var = parent.getSymbolTable().lookupParam(this._variable._name);
/*     */         }
/*     */       }
/*     */ 
/*  92 */       parent.addDependency(var);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return this._variable == ((VariableRefBase)obj)._variable;
/*     */     } catch (ClassCastException e) {
/*     */     }
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 115 */     return "variable-ref(" + this._variable.getName() + '/' + this._variable.getType() + ')';
/*     */   }
/*     */ 
/*     */   public Type typeCheck(SymbolTable stable)
/*     */     throws TypeCheckError
/*     */   {
/* 122 */     if (this._type != null) return this._type;
/*     */ 
/* 125 */     if (this._variable.isLocal()) {
/* 126 */       SyntaxTreeNode node = getParent();
/*     */       do {
/* 128 */         if ((node instanceof Closure)) {
/* 129 */           this._closure = ((Closure)node);
/* 130 */           break;
/*     */         }
/* 132 */         if ((node instanceof TopLevelElement)) {
/*     */           break;
/*     */         }
/* 135 */         node = node.getParent();
/* 136 */       }while (node != null);
/*     */ 
/* 138 */       if (this._closure != null) {
/* 139 */         this._closure.addVariable(this);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 144 */     this._type = this._variable.getType();
/*     */ 
/* 148 */     if (this._type == null) {
/* 149 */       this._variable.typeCheck(stable);
/* 150 */       this._type = this._variable.getType();
/*     */     }
/*     */ 
/* 154 */     addParentDependency();
/*     */ 
/* 157 */     return this._type;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.compiler.VariableRefBase
 * JD-Core Version:    0.6.2
 */