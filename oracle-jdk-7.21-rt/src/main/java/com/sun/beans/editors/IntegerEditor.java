/*    */ package com.sun.beans.editors;
/*    */ 
/*    */ public class IntegerEditor extends NumberEditor
/*    */ {
/*    */   public void setAsText(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 39 */     setValue(paramString == null ? null : Integer.decode(paramString));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.editors.IntegerEditor
 * JD-Core Version:    0.6.2
 */