/*    */ package com.sun.xml.internal.txw2;
/*    */ 
/*    */ final class EndDocument extends Content
/*    */ {
/*    */   boolean concludesPendingStartTag()
/*    */   {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   void accept(ContentVisitor visitor) {
/* 37 */     visitor.onEndDocument();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.txw2.EndDocument
 * JD-Core Version:    0.6.2
 */