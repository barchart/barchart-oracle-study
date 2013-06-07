/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ import sun.org.mozilla.javascript.internal.Node;
/*     */ import sun.org.mozilla.javascript.internal.ObjArray;
/*     */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*     */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*     */ 
/*     */ class Optimizer
/*     */ {
/*     */   static final int NoType = 0;
/*     */   static final int NumberType = 1;
/*     */   static final int AnyType = 3;
/*     */   private boolean inDirectCallFunction;
/*     */   OptFunctionNode theFunction;
/*     */   private boolean parameterUsedInNumberContext;
/*     */ 
/*     */   void optimize(ScriptNode paramScriptNode)
/*     */   {
/*  57 */     int i = paramScriptNode.getFunctionCount();
/*  58 */     for (int j = 0; j != i; j++) {
/*  59 */       OptFunctionNode localOptFunctionNode = OptFunctionNode.get(paramScriptNode, j);
/*  60 */       optimizeFunction(localOptFunctionNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void optimizeFunction(OptFunctionNode paramOptFunctionNode)
/*     */   {
/*  66 */     if (paramOptFunctionNode.fnode.requiresActivation()) return;
/*     */ 
/*  68 */     this.inDirectCallFunction = paramOptFunctionNode.isTargetOfDirectCall();
/*  69 */     this.theFunction = paramOptFunctionNode;
/*     */ 
/*  71 */     ObjArray localObjArray = new ObjArray();
/*  72 */     buildStatementList_r(paramOptFunctionNode.fnode, localObjArray);
/*  73 */     Node[] arrayOfNode = new Node[localObjArray.size()];
/*  74 */     localObjArray.toArray(arrayOfNode);
/*     */ 
/*  76 */     Block.runFlowAnalyzes(paramOptFunctionNode, arrayOfNode);
/*     */ 
/*  78 */     if (!paramOptFunctionNode.fnode.requiresActivation())
/*     */     {
/*  86 */       this.parameterUsedInNumberContext = false;
/*  87 */       for (int i = 0; i < arrayOfNode.length; i++) {
/*  88 */         rewriteForNumberVariables(arrayOfNode[i], 1);
/*     */       }
/*  90 */       paramOptFunctionNode.setParameterNumberContext(this.parameterUsedInNumberContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void markDCPNumberContext(Node paramNode)
/*     */   {
/* 129 */     if ((this.inDirectCallFunction) && (paramNode.getType() == 55)) {
/* 130 */       int i = this.theFunction.getVarIndex(paramNode);
/* 131 */       if (this.theFunction.isParameter(i))
/* 132 */         this.parameterUsedInNumberContext = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean convertParameter(Node paramNode)
/*     */   {
/* 139 */     if ((this.inDirectCallFunction) && (paramNode.getType() == 55)) {
/* 140 */       int i = this.theFunction.getVarIndex(paramNode);
/* 141 */       if (this.theFunction.isParameter(i)) {
/* 142 */         paramNode.removeProp(8);
/* 143 */         return true;
/*     */       }
/*     */     }
/* 146 */     return false;
/*     */   }
/*     */ 
/*     */   private int rewriteForNumberVariables(Node paramNode, int paramInt)
/*     */   {
/*     */     Node localNode2;
/*     */     Object localObject;
/*     */     int k;
/*     */     int n;
/*     */     int m;
/* 151 */     switch (paramNode.getType()) {
/*     */     case 133:
/* 153 */       Node localNode1 = paramNode.getFirstChild();
/* 154 */       int j = rewriteForNumberVariables(localNode1, 1);
/* 155 */       if (j == 1)
/* 156 */         paramNode.putIntProp(8, 0);
/* 157 */       return 0;
/*     */     case 40:
/* 160 */       paramNode.putIntProp(8, 0);
/* 161 */       return 1;
/*     */     case 55:
/* 165 */       int i = this.theFunction.getVarIndex(paramNode);
/* 166 */       if ((this.inDirectCallFunction) && (this.theFunction.isParameter(i)) && (paramInt == 1))
/*     */       {
/* 170 */         paramNode.putIntProp(8, 0);
/* 171 */         return 1;
/*     */       }
/* 173 */       if (this.theFunction.isNumberVar(i)) {
/* 174 */         paramNode.putIntProp(8, 0);
/* 175 */         return 1;
/*     */       }
/* 177 */       return 0;
/*     */     case 106:
/*     */     case 107:
/* 182 */       localNode2 = paramNode.getFirstChild();
/*     */ 
/* 184 */       if (localNode2.getType() == 55) {
/* 185 */         if ((rewriteForNumberVariables(localNode2, 1) == 1) && (!convertParameter(localNode2)))
/*     */         {
/* 188 */           paramNode.putIntProp(8, 0);
/* 189 */           markDCPNumberContext(localNode2);
/* 190 */           return 1;
/*     */         }
/* 192 */         return 0;
/*     */       }
/* 194 */       if (localNode2.getType() == 36) {
/* 195 */         return rewriteForNumberVariables(localNode2, 1);
/*     */       }
/* 197 */       return 0;
/*     */     case 56:
/* 200 */       localNode2 = paramNode.getFirstChild();
/* 201 */       localObject = localNode2.getNext();
/* 202 */       k = rewriteForNumberVariables((Node)localObject, 1);
/* 203 */       n = this.theFunction.getVarIndex(paramNode);
/* 204 */       if ((this.inDirectCallFunction) && (this.theFunction.isParameter(n)))
/*     */       {
/* 207 */         if (k == 1) {
/* 208 */           if (!convertParameter((Node)localObject)) {
/* 209 */             paramNode.putIntProp(8, 0);
/* 210 */             return 1;
/*     */           }
/* 212 */           markDCPNumberContext((Node)localObject);
/* 213 */           return 0;
/*     */         }
/*     */ 
/* 216 */         return k;
/*     */       }
/* 218 */       if (this.theFunction.isNumberVar(n)) {
/* 219 */         if (k != 1) {
/* 220 */           paramNode.removeChild((Node)localObject);
/* 221 */           paramNode.addChildToBack(new Node(150, (Node)localObject));
/*     */         }
/*     */ 
/* 224 */         paramNode.putIntProp(8, 0);
/* 225 */         markDCPNumberContext((Node)localObject);
/* 226 */         return 1;
/*     */       }
/*     */ 
/* 229 */       if ((k == 1) && 
/* 230 */         (!convertParameter((Node)localObject))) {
/* 231 */         paramNode.removeChild((Node)localObject);
/* 232 */         paramNode.addChildToBack(new Node(149, (Node)localObject));
/*     */       }
/*     */ 
/* 236 */       return 0;
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/* 243 */       localNode2 = paramNode.getFirstChild();
/* 244 */       localObject = localNode2.getNext();
/* 245 */       k = rewriteForNumberVariables(localNode2, 1);
/* 246 */       n = rewriteForNumberVariables((Node)localObject, 1);
/* 247 */       markDCPNumberContext(localNode2);
/* 248 */       markDCPNumberContext((Node)localObject);
/*     */ 
/* 250 */       if (convertParameter(localNode2)) {
/* 251 */         if (convertParameter((Node)localObject))
/* 252 */           return 0;
/* 253 */         if (n == 1) {
/* 254 */           paramNode.putIntProp(8, 2);
/*     */         }
/*     */       }
/* 257 */       else if (convertParameter((Node)localObject)) {
/* 258 */         if (k == 1) {
/* 259 */           paramNode.putIntProp(8, 1);
/*     */         }
/*     */ 
/*     */       }
/* 263 */       else if (k == 1) {
/* 264 */         if (n == 1) {
/* 265 */           paramNode.putIntProp(8, 0);
/*     */         }
/*     */         else {
/* 268 */           paramNode.putIntProp(8, 1);
/*     */         }
/*     */ 
/*     */       }
/* 272 */       else if (n == 1) {
/* 273 */         paramNode.putIntProp(8, 2);
/*     */       }
/*     */ 
/* 278 */       return 0;
/*     */     case 21:
/* 282 */       localNode2 = paramNode.getFirstChild();
/* 283 */       localObject = localNode2.getNext();
/* 284 */       k = rewriteForNumberVariables(localNode2, 1);
/* 285 */       n = rewriteForNumberVariables((Node)localObject, 1);
/*     */ 
/* 288 */       if (convertParameter(localNode2)) {
/* 289 */         if (convertParameter((Node)localObject)) {
/* 290 */           return 0;
/*     */         }
/*     */ 
/* 293 */         if (n == 1) {
/* 294 */           paramNode.putIntProp(8, 2);
/*     */         }
/*     */ 
/*     */       }
/* 299 */       else if (convertParameter((Node)localObject)) {
/* 300 */         if (k == 1) {
/* 301 */           paramNode.putIntProp(8, 1);
/*     */         }
/*     */ 
/*     */       }
/* 305 */       else if (k == 1) {
/* 306 */         if (n == 1) {
/* 307 */           paramNode.putIntProp(8, 0);
/* 308 */           return 1;
/*     */         }
/*     */ 
/* 311 */         paramNode.putIntProp(8, 1);
/*     */       }
/* 315 */       else if (n == 1) {
/* 316 */         paramNode.putIntProp(8, 2);
/*     */       }
/*     */ 
/* 322 */       return 0;
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 18:
/*     */     case 19:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/* 334 */       localNode2 = paramNode.getFirstChild();
/* 335 */       localObject = localNode2.getNext();
/* 336 */       k = rewriteForNumberVariables(localNode2, 1);
/* 337 */       n = rewriteForNumberVariables((Node)localObject, 1);
/* 338 */       markDCPNumberContext(localNode2);
/* 339 */       markDCPNumberContext((Node)localObject);
/* 340 */       if (k == 1) {
/* 341 */         if (n == 1) {
/* 342 */           paramNode.putIntProp(8, 0);
/* 343 */           return 1;
/*     */         }
/*     */ 
/* 346 */         if (!convertParameter((Node)localObject)) {
/* 347 */           paramNode.removeChild((Node)localObject);
/* 348 */           paramNode.addChildToBack(new Node(150, (Node)localObject));
/*     */ 
/* 350 */           paramNode.putIntProp(8, 0);
/*     */         }
/* 352 */         return 1;
/*     */       }
/*     */ 
/* 356 */       if (n == 1) {
/* 357 */         if (!convertParameter(localNode2)) {
/* 358 */           paramNode.removeChild(localNode2);
/* 359 */           paramNode.addChildToFront(new Node(150, localNode2));
/*     */ 
/* 361 */           paramNode.putIntProp(8, 0);
/*     */         }
/* 363 */         return 1;
/*     */       }
/*     */ 
/* 366 */       if (!convertParameter(localNode2)) {
/* 367 */         paramNode.removeChild(localNode2);
/* 368 */         paramNode.addChildToFront(new Node(150, localNode2));
/*     */       }
/*     */ 
/* 371 */       if (!convertParameter((Node)localObject)) {
/* 372 */         paramNode.removeChild((Node)localObject);
/* 373 */         paramNode.addChildToBack(new Node(150, (Node)localObject));
/*     */       }
/*     */ 
/* 376 */       paramNode.putIntProp(8, 0);
/* 377 */       return 1;
/*     */     case 37:
/*     */     case 140:
/* 383 */       localNode2 = paramNode.getFirstChild();
/* 384 */       localObject = localNode2.getNext();
/* 385 */       Node localNode3 = ((Node)localObject).getNext();
/* 386 */       n = rewriteForNumberVariables(localNode2, 1);
/* 387 */       if ((n == 1) && 
/* 388 */         (!convertParameter(localNode2))) {
/* 389 */         paramNode.removeChild(localNode2);
/* 390 */         paramNode.addChildToFront(new Node(149, localNode2));
/*     */       }
/*     */ 
/* 394 */       int i1 = rewriteForNumberVariables((Node)localObject, 1);
/* 395 */       if ((i1 == 1) && 
/* 396 */         (!convertParameter((Node)localObject)))
/*     */       {
/* 400 */         paramNode.putIntProp(8, 1);
/*     */       }
/*     */ 
/* 403 */       int i2 = rewriteForNumberVariables(localNode3, 1);
/* 404 */       if ((i2 == 1) && 
/* 405 */         (!convertParameter(localNode3))) {
/* 406 */         paramNode.removeChild(localNode3);
/* 407 */         paramNode.addChildToBack(new Node(149, localNode3));
/*     */       }
/*     */ 
/* 411 */       return 0;
/*     */     case 36:
/* 414 */       localNode2 = paramNode.getFirstChild();
/* 415 */       localObject = localNode2.getNext();
/* 416 */       m = rewriteForNumberVariables(localNode2, 1);
/* 417 */       if ((m == 1) && 
/* 418 */         (!convertParameter(localNode2))) {
/* 419 */         paramNode.removeChild(localNode2);
/* 420 */         paramNode.addChildToFront(new Node(149, localNode2));
/*     */       }
/*     */ 
/* 424 */       n = rewriteForNumberVariables((Node)localObject, 1);
/* 425 */       if ((n == 1) && 
/* 426 */         (!convertParameter((Node)localObject)))
/*     */       {
/* 430 */         paramNode.putIntProp(8, 2);
/*     */       }
/*     */ 
/* 433 */       return 0;
/*     */     case 38:
/* 437 */       localNode2 = paramNode.getFirstChild();
/*     */ 
/* 439 */       rewriteAsObjectChildren(localNode2, localNode2.getFirstChild());
/* 440 */       localNode2 = localNode2.getNext();
/*     */ 
/* 442 */       localObject = (OptFunctionNode)paramNode.getProp(9);
/*     */ 
/* 444 */       if (localObject != null)
/*     */       {
/* 449 */         while (localNode2 != null) {
/* 450 */           m = rewriteForNumberVariables(localNode2, 1);
/* 451 */           if (m == 1) {
/* 452 */             markDCPNumberContext(localNode2);
/*     */           }
/* 454 */           localNode2 = localNode2.getNext();
/*     */         }
/*     */       }
/* 457 */       rewriteAsObjectChildren(paramNode, localNode2);
/*     */ 
/* 459 */       return 0;
/*     */     }
/*     */ 
/* 462 */     rewriteAsObjectChildren(paramNode, paramNode.getFirstChild());
/* 463 */     return 0;
/*     */   }
/*     */ 
/*     */   private void rewriteAsObjectChildren(Node paramNode1, Node paramNode2)
/*     */   {
/* 471 */     while (paramNode2 != null) {
/* 472 */       Node localNode1 = paramNode2.getNext();
/* 473 */       int i = rewriteForNumberVariables(paramNode2, 0);
/* 474 */       if ((i == 1) && 
/* 475 */         (!convertParameter(paramNode2))) {
/* 476 */         paramNode1.removeChild(paramNode2);
/* 477 */         Node localNode2 = new Node(149, paramNode2);
/* 478 */         if (localNode1 == null)
/* 479 */           paramNode1.addChildToBack(localNode2);
/*     */         else {
/* 481 */           paramNode1.addChildBefore(localNode2, localNode1);
/*     */         }
/*     */       }
/* 484 */       paramNode2 = localNode1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void buildStatementList_r(Node paramNode, ObjArray paramObjArray)
/*     */   {
/* 490 */     int i = paramNode.getType();
/* 491 */     if ((i == 129) || (i == 141) || (i == 132) || (i == 109))
/*     */     {
/* 496 */       Node localNode = paramNode.getFirstChild();
/* 497 */       while (localNode != null) {
/* 498 */         buildStatementList_r(localNode, paramObjArray);
/* 499 */         localNode = localNode.getNext();
/*     */       }
/*     */     } else {
/* 502 */       paramObjArray.add(paramNode);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.Optimizer
 * JD-Core Version:    0.6.2
 */