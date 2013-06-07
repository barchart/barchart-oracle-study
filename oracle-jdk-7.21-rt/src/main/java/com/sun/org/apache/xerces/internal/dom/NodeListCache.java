/*    */ package com.sun.org.apache.xerces.internal.dom;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ class NodeListCache
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -7927529254918631002L;
/* 41 */   int fLength = -1;
/*    */ 
/* 44 */   int fChildIndex = -1;
/*    */   ChildNode fChild;
/*    */   ParentNode fOwner;
/*    */   NodeListCache next;
/*    */ 
/*    */   NodeListCache(ParentNode owner)
/*    */   {
/* 57 */     this.fOwner = owner;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.dom.NodeListCache
 * JD-Core Version:    0.6.2
 */