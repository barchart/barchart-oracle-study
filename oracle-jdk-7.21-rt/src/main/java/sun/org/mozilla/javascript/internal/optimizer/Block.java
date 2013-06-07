/*     */ package sun.org.mozilla.javascript.internal.optimizer;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import sun.org.mozilla.javascript.internal.Node;
/*     */ import sun.org.mozilla.javascript.internal.ObjArray;
/*     */ import sun.org.mozilla.javascript.internal.ObjToIntMap;
/*     */ import sun.org.mozilla.javascript.internal.ObjToIntMap.Iterator;
/*     */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*     */ import sun.org.mozilla.javascript.internal.ast.Jump;
/*     */ 
/*     */ class Block
/*     */ {
/*     */   private Block[] itsSuccessors;
/*     */   private Block[] itsPredecessors;
/*     */   private int itsStartNodeIndex;
/*     */   private int itsEndNodeIndex;
/*     */   private int itsBlockID;
/*     */   private DataFlowBitSet itsLiveOnEntrySet;
/*     */   private DataFlowBitSet itsLiveOnExitSet;
/*     */   private DataFlowBitSet itsUseBeforeDefSet;
/*     */   private DataFlowBitSet itsNotDefSet;
/*     */   static final boolean DEBUG = false;
/*     */   private static int debug_blockCount;
/*     */ 
/*     */   Block(int paramInt1, int paramInt2)
/*     */   {
/*  88 */     this.itsStartNodeIndex = paramInt1;
/*  89 */     this.itsEndNodeIndex = paramInt2;
/*     */   }
/*     */ 
/*     */   static void runFlowAnalyzes(OptFunctionNode paramOptFunctionNode, Node[] paramArrayOfNode)
/*     */   {
/*  94 */     int i = paramOptFunctionNode.fnode.getParamCount();
/*  95 */     int j = paramOptFunctionNode.fnode.getParamAndVarCount();
/*  96 */     int[] arrayOfInt = new int[j];
/*     */ 
/*  98 */     for (int k = 0; k != i; k++) {
/*  99 */       arrayOfInt[k] = 3;
/*     */     }
/*     */ 
/* 103 */     for (k = i; k != j; k++) {
/* 104 */       arrayOfInt[k] = 0;
/*     */     }
/*     */ 
/* 107 */     Block[] arrayOfBlock = buildBlocks(paramArrayOfNode);
/*     */ 
/* 115 */     reachingDefDataFlow(paramOptFunctionNode, paramArrayOfNode, arrayOfBlock, arrayOfInt);
/* 116 */     typeFlow(paramOptFunctionNode, paramArrayOfNode, arrayOfBlock, arrayOfInt);
/*     */ 
/* 129 */     for (int m = i; m != j; m++)
/* 130 */       if (arrayOfInt[m] == 1)
/* 131 */         paramOptFunctionNode.setIsNumberVar(m);
/*     */   }
/*     */ 
/*     */   private static Block[] buildBlocks(Node[] paramArrayOfNode)
/*     */   {
/* 140 */     HashMap localHashMap = new HashMap();
/* 141 */     ObjArray localObjArray = new ObjArray();
/*     */ 
/* 144 */     int i = 0;
/*     */     FatBlock localFatBlock2;
/* 146 */     for (int j = 0; j < paramArrayOfNode.length; j++) {
/* 147 */       switch (paramArrayOfNode[j].getType())
/*     */       {
/*     */       case 131:
/* 150 */         if (j != i) {
/* 151 */           localFatBlock2 = newFatBlock(i, j - 1);
/* 152 */           if (paramArrayOfNode[i].getType() == 131)
/*     */           {
/* 154 */             localHashMap.put(paramArrayOfNode[i], localFatBlock2);
/* 155 */           }localObjArray.add(localFatBlock2);
/*     */ 
/* 157 */           i = j;
/* 158 */         }break;
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 165 */         localFatBlock2 = newFatBlock(i, j);
/* 166 */         if (paramArrayOfNode[i].getType() == 131)
/*     */         {
/* 168 */           localHashMap.put(paramArrayOfNode[i], localFatBlock2);
/* 169 */         }localObjArray.add(localFatBlock2);
/*     */ 
/* 171 */         i = j + 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 177 */     if (i != paramArrayOfNode.length) {
/* 178 */       FatBlock localFatBlock1 = newFatBlock(i, paramArrayOfNode.length - 1);
/* 179 */       if (paramArrayOfNode[i].getType() == 131)
/* 180 */         localHashMap.put(paramArrayOfNode[i], localFatBlock1);
/* 181 */       localObjArray.add(localFatBlock1);
/*     */     }
/*     */     Object localObject1;
/* 186 */     for (int k = 0; k < localObjArray.size(); k++) {
/* 187 */       localFatBlock2 = (FatBlock)localObjArray.get(k);
/*     */ 
/* 189 */       localObject1 = paramArrayOfNode[localFatBlock2.realBlock.itsEndNodeIndex];
/* 190 */       int n = ((Node)localObject1).getType();
/*     */       Object localObject2;
/* 192 */       if ((n != 5) && (k < localObjArray.size() - 1))
/*     */       {
/* 194 */         localObject2 = (FatBlock)localObjArray.get(k + 1);
/* 195 */         localFatBlock2.addSuccessor((FatBlock)localObject2);
/* 196 */         ((FatBlock)localObject2).addPredecessor(localFatBlock2);
/*     */       }
/*     */ 
/* 200 */       if ((n == 7) || (n == 6) || (n == 5))
/*     */       {
/* 203 */         localObject2 = ((Jump)localObject1).target;
/* 204 */         FatBlock localFatBlock3 = (FatBlock)localHashMap.get(localObject2);
/* 205 */         ((Node)localObject2).putProp(6, localFatBlock3.realBlock);
/*     */ 
/* 207 */         localFatBlock2.addSuccessor(localFatBlock3);
/* 208 */         localFatBlock3.addPredecessor(localFatBlock2);
/*     */       }
/*     */     }
/*     */ 
/* 212 */     Block[] arrayOfBlock = new Block[localObjArray.size()];
/*     */ 
/* 214 */     for (int m = 0; m < localObjArray.size(); m++) {
/* 215 */       localObject1 = (FatBlock)localObjArray.get(m);
/* 216 */       Block localBlock = ((FatBlock)localObject1).realBlock;
/* 217 */       localBlock.itsSuccessors = ((FatBlock)localObject1).getSuccessors();
/* 218 */       localBlock.itsPredecessors = ((FatBlock)localObject1).getPredecessors();
/* 219 */       localBlock.itsBlockID = m;
/* 220 */       arrayOfBlock[m] = localBlock;
/*     */     }
/*     */ 
/* 223 */     return arrayOfBlock;
/*     */   }
/*     */ 
/*     */   private static FatBlock newFatBlock(int paramInt1, int paramInt2)
/*     */   {
/* 228 */     FatBlock localFatBlock = new FatBlock(null);
/* 229 */     localFatBlock.realBlock = new Block(paramInt1, paramInt2);
/* 230 */     return localFatBlock;
/*     */   }
/*     */ 
/*     */   private static String toString(Block[] paramArrayOfBlock, Node[] paramArrayOfNode)
/*     */   {
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */   private static void reachingDefDataFlow(OptFunctionNode paramOptFunctionNode, Node[] paramArrayOfNode, Block[] paramArrayOfBlock, int[] paramArrayOfInt)
/*     */   {
/* 277 */     for (int i = 0; i < paramArrayOfBlock.length; i++) {
/* 278 */       paramArrayOfBlock[i].initLiveOnEntrySets(paramOptFunctionNode, paramArrayOfNode);
/*     */     }
/*     */ 
/* 285 */     boolean[] arrayOfBoolean1 = new boolean[paramArrayOfBlock.length];
/* 286 */     boolean[] arrayOfBoolean2 = new boolean[paramArrayOfBlock.length];
/* 287 */     int j = paramArrayOfBlock.length - 1;
/* 288 */     int k = 0;
/* 289 */     arrayOfBoolean1[j] = true;
/*     */     while (true) {
/* 291 */       if ((arrayOfBoolean1[j] != 0) || (arrayOfBoolean2[j] == 0)) {
/* 292 */         arrayOfBoolean2[j] = true;
/* 293 */         arrayOfBoolean1[j] = false;
/* 294 */         if (paramArrayOfBlock[j].doReachedUseDataFlow()) {
/* 295 */           Block[] arrayOfBlock = paramArrayOfBlock[j].itsPredecessors;
/* 296 */           if (arrayOfBlock != null) {
/* 297 */             for (int m = 0; m < arrayOfBlock.length; m++) {
/* 298 */               int n = arrayOfBlock[m].itsBlockID;
/* 299 */               arrayOfBoolean1[n] = true;
/* 300 */               k |= (n > j ? 1 : 0);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 305 */       if (j == 0) {
/* 306 */         if (k == 0) break;
/* 307 */         j = paramArrayOfBlock.length - 1;
/* 308 */         k = 0;
/*     */       }
/*     */       else
/*     */       {
/* 314 */         j--;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 322 */     paramArrayOfBlock[0].markAnyTypeVariables(paramArrayOfInt);
/*     */   }
/*     */ 
/*     */   private static void typeFlow(OptFunctionNode paramOptFunctionNode, Node[] paramArrayOfNode, Block[] paramArrayOfBlock, int[] paramArrayOfInt)
/*     */   {
/* 327 */     boolean[] arrayOfBoolean1 = new boolean[paramArrayOfBlock.length];
/* 328 */     boolean[] arrayOfBoolean2 = new boolean[paramArrayOfBlock.length];
/* 329 */     int i = 0;
/* 330 */     int j = 0;
/* 331 */     arrayOfBoolean1[i] = true;
/*     */     while (true) {
/* 333 */       if ((arrayOfBoolean1[i] != 0) || (arrayOfBoolean2[i] == 0)) {
/* 334 */         arrayOfBoolean2[i] = true;
/* 335 */         arrayOfBoolean1[i] = false;
/* 336 */         if (paramArrayOfBlock[i].doTypeFlow(paramOptFunctionNode, paramArrayOfNode, paramArrayOfInt))
/*     */         {
/* 338 */           Block[] arrayOfBlock = paramArrayOfBlock[i].itsSuccessors;
/* 339 */           if (arrayOfBlock != null) {
/* 340 */             for (int k = 0; k < arrayOfBlock.length; k++) {
/* 341 */               int m = arrayOfBlock[k].itsBlockID;
/* 342 */               arrayOfBoolean1[m] = true;
/* 343 */               j |= (m < i ? 1 : 0);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 348 */       if (i == paramArrayOfBlock.length - 1) {
/* 349 */         if (j == 0) break;
/* 350 */         i = 0;
/* 351 */         j = 0;
/*     */       }
/*     */       else
/*     */       {
/* 357 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean assignType(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
/* 363 */     return paramInt2 != (paramArrayOfInt[paramInt1] |= paramInt2);
/*     */   }
/*     */ 
/*     */   private void markAnyTypeVariables(int[] paramArrayOfInt)
/*     */   {
/* 368 */     for (int i = 0; i != paramArrayOfInt.length; i++)
/* 369 */       if (this.itsLiveOnEntrySet.test(i))
/* 370 */         assignType(paramArrayOfInt, i, 3);
/*     */   }
/*     */ 
/*     */   private void lookForVariableAccess(OptFunctionNode paramOptFunctionNode, Node paramNode)
/*     */   {
/*     */     Node localNode1;
/* 386 */     switch (paramNode.getType())
/*     */     {
/*     */     case 106:
/*     */     case 107:
/* 390 */       localNode1 = paramNode.getFirstChild();
/* 391 */       if (localNode1.getType() == 55) {
/* 392 */         int j = paramOptFunctionNode.getVarIndex(localNode1);
/* 393 */         if (!this.itsNotDefSet.test(j))
/* 394 */           this.itsUseBeforeDefSet.set(j);
/* 395 */         this.itsNotDefSet.set(j);
/*     */       }
/*     */ 
/* 398 */       break;
/*     */     case 56:
/* 401 */       localNode1 = paramNode.getFirstChild();
/* 402 */       Node localNode3 = localNode1.getNext();
/* 403 */       lookForVariableAccess(paramOptFunctionNode, localNode3);
/* 404 */       this.itsNotDefSet.set(paramOptFunctionNode.getVarIndex(paramNode));
/*     */ 
/* 406 */       break;
/*     */     case 55:
/* 409 */       int i = paramOptFunctionNode.getVarIndex(paramNode);
/* 410 */       if (!this.itsNotDefSet.test(i)) {
/* 411 */         this.itsUseBeforeDefSet.set(i);
/*     */       }
/* 413 */       break;
/*     */     default:
/* 415 */       Node localNode2 = paramNode.getFirstChild();
/* 416 */       while (localNode2 != null) {
/* 417 */         lookForVariableAccess(paramOptFunctionNode, localNode2);
/* 418 */         localNode2 = localNode2.getNext();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initLiveOnEntrySets(OptFunctionNode paramOptFunctionNode, Node[] paramArrayOfNode)
/*     */   {
/* 431 */     int i = paramOptFunctionNode.getVarCount();
/* 432 */     this.itsUseBeforeDefSet = new DataFlowBitSet(i);
/* 433 */     this.itsNotDefSet = new DataFlowBitSet(i);
/* 434 */     this.itsLiveOnEntrySet = new DataFlowBitSet(i);
/* 435 */     this.itsLiveOnExitSet = new DataFlowBitSet(i);
/* 436 */     for (int j = this.itsStartNodeIndex; j <= this.itsEndNodeIndex; j++) {
/* 437 */       Node localNode = paramArrayOfNode[j];
/* 438 */       lookForVariableAccess(paramOptFunctionNode, localNode);
/*     */     }
/* 440 */     this.itsNotDefSet.not();
/*     */   }
/*     */ 
/*     */   private boolean doReachedUseDataFlow()
/*     */   {
/* 451 */     this.itsLiveOnExitSet.clear();
/* 452 */     if (this.itsSuccessors != null)
/* 453 */       for (int i = 0; i < this.itsSuccessors.length; i++)
/* 454 */         this.itsLiveOnExitSet.or(this.itsSuccessors[i].itsLiveOnEntrySet);
/* 455 */     return this.itsLiveOnEntrySet.df2(this.itsLiveOnExitSet, this.itsUseBeforeDefSet, this.itsNotDefSet);
/*     */   }
/*     */ 
/*     */   private static int findExpressionType(OptFunctionNode paramOptFunctionNode, Node paramNode, int[] paramArrayOfInt)
/*     */   {
/* 468 */     switch (paramNode.getType()) {
/*     */     case 40:
/* 470 */       return 1;
/*     */     case 30:
/*     */     case 38:
/*     */     case 70:
/* 475 */       return 3;
/*     */     case 36:
/* 478 */       return 3;
/*     */     case 55:
/* 481 */       return paramArrayOfInt[paramOptFunctionNode.getVarIndex(paramNode)];
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 18:
/*     */     case 19:
/*     */     case 20:
/*     */     case 22:
/*     */     case 23:
/*     */     case 24:
/*     */     case 25:
/*     */     case 28:
/*     */     case 29:
/*     */     case 106:
/*     */     case 107:
/* 497 */       return 1;
/*     */     case 65:
/*     */     case 66:
/* 501 */       return 3;
/*     */     case 21:
/* 507 */       localNode = paramNode.getFirstChild();
/* 508 */       i = findExpressionType(paramOptFunctionNode, localNode, paramArrayOfInt);
/* 509 */       int j = findExpressionType(paramOptFunctionNode, localNode.getNext(), paramArrayOfInt);
/* 510 */       return i | j;
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/*     */     case 15:
/*     */     case 16:
/*     */     case 17:
/*     */     case 26:
/*     */     case 27:
/*     */     case 31:
/*     */     case 32:
/*     */     case 33:
/*     */     case 34:
/*     */     case 35:
/*     */     case 37:
/*     */     case 39:
/*     */     case 41:
/*     */     case 42:
/*     */     case 43:
/*     */     case 44:
/*     */     case 45:
/*     */     case 46:
/*     */     case 47:
/*     */     case 48:
/*     */     case 49:
/*     */     case 50:
/*     */     case 51:
/*     */     case 52:
/*     */     case 53:
/*     */     case 54:
/*     */     case 56:
/*     */     case 57:
/*     */     case 58:
/*     */     case 59:
/*     */     case 60:
/*     */     case 61:
/*     */     case 62:
/*     */     case 63:
/*     */     case 64:
/*     */     case 67:
/*     */     case 68:
/*     */     case 69:
/*     */     case 71:
/*     */     case 72:
/*     */     case 73:
/*     */     case 74:
/*     */     case 75:
/*     */     case 76:
/*     */     case 77:
/*     */     case 78:
/*     */     case 79:
/*     */     case 80:
/*     */     case 81:
/*     */     case 82:
/*     */     case 83:
/*     */     case 84:
/*     */     case 85:
/*     */     case 86:
/*     */     case 87:
/*     */     case 88:
/*     */     case 89:
/*     */     case 90:
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/*     */     case 94:
/*     */     case 95:
/*     */     case 96:
/*     */     case 97:
/*     */     case 98:
/*     */     case 99:
/*     */     case 100:
/*     */     case 101:
/*     */     case 102:
/*     */     case 103:
/*     */     case 104:
/* 514 */     case 105: } Node localNode = paramNode.getFirstChild();
/* 515 */     if (localNode == null) {
/* 516 */       return 3;
/*     */     }
/* 518 */     int i = 0;
/* 519 */     while (localNode != null) {
/* 520 */       i |= findExpressionType(paramOptFunctionNode, localNode, paramArrayOfInt);
/* 521 */       localNode = localNode.getNext();
/*     */     }
/* 523 */     return i;
/*     */   }
/*     */ 
/*     */   private static boolean findDefPoints(OptFunctionNode paramOptFunctionNode, Node paramNode, int[] paramArrayOfInt)
/*     */   {
/* 530 */     boolean bool = false;
/* 531 */     Node localNode1 = paramNode.getFirstChild();
/* 532 */     switch (paramNode.getType()) { default:
/*     */     case 106:
/*     */     case 107:
/*     */     case 35:
/*     */     case 139:
/* 534 */     case 56: } while (localNode1 != null) {
/* 535 */       bool |= findDefPoints(paramOptFunctionNode, localNode1, paramArrayOfInt);
/* 536 */       localNode1 = localNode1.getNext(); continue;
/*     */ 
/* 541 */       if (localNode1.getType() == 55)
/*     */       {
/* 543 */         int i = paramOptFunctionNode.getVarIndex(localNode1);
/* 544 */         bool |= assignType(paramArrayOfInt, i, 1);
/* 545 */         break;
/*     */ 
/* 549 */         if (localNode1.getType() == 55) {
/* 550 */           i = paramOptFunctionNode.getVarIndex(localNode1);
/* 551 */           assignType(paramArrayOfInt, i, 3);
/*     */         }
/* 553 */         while (localNode1 != null) {
/* 554 */           bool |= findDefPoints(paramOptFunctionNode, localNode1, paramArrayOfInt);
/* 555 */           localNode1 = localNode1.getNext(); continue;
/*     */ 
/* 559 */           Node localNode2 = localNode1.getNext();
/* 560 */           int j = findExpressionType(paramOptFunctionNode, localNode2, paramArrayOfInt);
/* 561 */           int k = paramOptFunctionNode.getVarIndex(paramNode);
/* 562 */           bool |= assignType(paramArrayOfInt, k, j);
/*     */         }
/*     */       }
/*     */     }
/* 566 */     return bool;
/*     */   }
/*     */ 
/*     */   private boolean doTypeFlow(OptFunctionNode paramOptFunctionNode, Node[] paramArrayOfNode, int[] paramArrayOfInt)
/*     */   {
/* 572 */     boolean bool = false;
/*     */ 
/* 574 */     for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; i++) {
/* 575 */       Node localNode = paramArrayOfNode[i];
/* 576 */       if (localNode != null) {
/* 577 */         bool |= findDefPoints(paramOptFunctionNode, localNode, paramArrayOfInt);
/*     */       }
/*     */     }
/* 580 */     return bool;
/*     */   }
/*     */ 
/*     */   private void printLiveOnEntrySet(OptFunctionNode paramOptFunctionNode)
/*     */   {
/*     */   }
/*     */ 
/*     */   private static class FatBlock
/*     */   {
/*  79 */     private ObjToIntMap successors = new ObjToIntMap();
/*     */ 
/*  81 */     private ObjToIntMap predecessors = new ObjToIntMap();
/*     */     Block realBlock;
/*     */ 
/*     */     private static Block[] reduceToArray(ObjToIntMap paramObjToIntMap)
/*     */     {
/*  59 */       Block[] arrayOfBlock = null;
/*  60 */       if (!paramObjToIntMap.isEmpty()) {
/*  61 */         arrayOfBlock = new Block[paramObjToIntMap.size()];
/*  62 */         int i = 0;
/*  63 */         ObjToIntMap.Iterator localIterator = paramObjToIntMap.newIterator();
/*  64 */         for (localIterator.start(); !localIterator.done(); localIterator.next()) {
/*  65 */           FatBlock localFatBlock = (FatBlock)localIterator.getKey();
/*  66 */           arrayOfBlock[(i++)] = localFatBlock.realBlock;
/*     */         }
/*     */       }
/*  69 */       return arrayOfBlock;
/*     */     }
/*     */     void addSuccessor(FatBlock paramFatBlock) {
/*  72 */       this.successors.put(paramFatBlock, 0); } 
/*  73 */     void addPredecessor(FatBlock paramFatBlock) { this.predecessors.put(paramFatBlock, 0); } 
/*     */     Block[] getSuccessors() {
/*  75 */       return reduceToArray(this.successors); } 
/*  76 */     Block[] getPredecessors() { return reduceToArray(this.predecessors); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.Block
 * JD-Core Version:    0.6.2
 */