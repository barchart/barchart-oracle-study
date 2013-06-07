/*    */ package com.sun.corba.se.impl.orbutil.graph;
/*    */ 
/*    */ public class NodeData
/*    */ {
/*    */   private boolean visited;
/*    */   private boolean root;
/*    */ 
/*    */   public NodeData()
/*    */   {
/* 37 */     clear();
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 42 */     this.visited = false;
/* 43 */     this.root = true;
/*    */   }
/*    */ 
/*    */   boolean isVisited()
/*    */   {
/* 51 */     return this.visited;
/*    */   }
/*    */ 
/*    */   void visited()
/*    */   {
/* 56 */     this.visited = true;
/*    */   }
/*    */ 
/*    */   boolean isRoot()
/*    */   {
/* 63 */     return this.root;
/*    */   }
/*    */ 
/*    */   void notRoot()
/*    */   {
/* 68 */     this.root = false;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orbutil.graph.NodeData
 * JD-Core Version:    0.6.2
 */