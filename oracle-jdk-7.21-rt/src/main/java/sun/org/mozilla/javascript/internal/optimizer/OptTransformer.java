/*    */ package sun.org.mozilla.javascript.internal.optimizer;
/*    */ 
/*    */ import java.util.Map;
/*    */ import sun.org.mozilla.javascript.internal.Kit;
/*    */ import sun.org.mozilla.javascript.internal.Node;
/*    */ import sun.org.mozilla.javascript.internal.NodeTransformer;
/*    */ import sun.org.mozilla.javascript.internal.ObjArray;
/*    */ import sun.org.mozilla.javascript.internal.ast.FunctionNode;
/*    */ import sun.org.mozilla.javascript.internal.ast.ScriptNode;
/*    */ 
/*    */ class OptTransformer extends NodeTransformer
/*    */ {
/*    */   private Map<String, OptFunctionNode> possibleDirectCalls;
/*    */   private ObjArray directCallTargets;
/*    */ 
/*    */   OptTransformer(Map<String, OptFunctionNode> paramMap, ObjArray paramObjArray)
/*    */   {
/* 56 */     this.possibleDirectCalls = paramMap;
/* 57 */     this.directCallTargets = paramObjArray;
/*    */   }
/*    */ 
/*    */   protected void visitNew(Node paramNode, ScriptNode paramScriptNode)
/*    */   {
/* 62 */     detectDirectCall(paramNode, paramScriptNode);
/* 63 */     super.visitNew(paramNode, paramScriptNode);
/*    */   }
/*    */ 
/*    */   protected void visitCall(Node paramNode, ScriptNode paramScriptNode)
/*    */   {
/* 68 */     detectDirectCall(paramNode, paramScriptNode);
/* 69 */     super.visitCall(paramNode, paramScriptNode);
/*    */   }
/*    */ 
/*    */   private void detectDirectCall(Node paramNode, ScriptNode paramScriptNode)
/*    */   {
/* 74 */     if (paramScriptNode.getType() == 109) {
/* 75 */       Node localNode1 = paramNode.getFirstChild();
/*    */ 
/* 78 */       int i = 0;
/* 79 */       Node localNode2 = localNode1.getNext();
/* 80 */       while (localNode2 != null) {
/* 81 */         localNode2 = localNode2.getNext();
/* 82 */         i++;
/*    */       }
/*    */ 
/* 85 */       if (i == 0) {
/* 86 */         OptFunctionNode.get(paramScriptNode).itsContainsCalls0 = true;
/*    */       }
/*    */ 
/* 101 */       if (this.possibleDirectCalls != null) {
/* 102 */         String str = null;
/* 103 */         if (localNode1.getType() == 39)
/* 104 */           str = localNode1.getString();
/* 105 */         else if (localNode1.getType() == 33)
/* 106 */           str = localNode1.getFirstChild().getNext().getString();
/* 107 */         else if (localNode1.getType() == 34) {
/* 108 */           throw Kit.codeBug();
/*    */         }
/* 110 */         if (str != null)
/*    */         {
/* 112 */           OptFunctionNode localOptFunctionNode = (OptFunctionNode)this.possibleDirectCalls.get(str);
/* 113 */           if ((localOptFunctionNode != null) && (i == localOptFunctionNode.fnode.getParamCount()) && (!localOptFunctionNode.fnode.requiresActivation()))
/*    */           {
/* 120 */             if (i <= 32) {
/* 121 */               paramNode.putProp(9, localOptFunctionNode);
/* 122 */               if (!localOptFunctionNode.isTargetOfDirectCall()) {
/* 123 */                 int j = this.directCallTargets.size();
/* 124 */                 this.directCallTargets.add(localOptFunctionNode);
/* 125 */                 localOptFunctionNode.setDirectTargetIndex(j);
/*    */               }
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.javascript.internal.optimizer.OptTransformer
 * JD-Core Version:    0.6.2
 */