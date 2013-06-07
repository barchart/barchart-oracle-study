/*    */ package com.sun.xml.internal.txw2;
/*    */ 
/*    */ abstract class Text extends Content
/*    */ {
/* 37 */   protected final StringBuilder buffer = new StringBuilder();
/*    */ 
/*    */   protected Text(Document document, NamespaceResolver nsResolver, Object obj) {
/* 40 */     document.writeValue(obj, nsResolver, this.buffer);
/*    */   }
/*    */ 
/*    */   boolean concludesPendingStartTag() {
/* 44 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.txw2.Text
 * JD-Core Version:    0.6.2
 */