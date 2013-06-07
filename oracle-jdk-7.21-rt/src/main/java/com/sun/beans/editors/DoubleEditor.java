/*    */ package com.sun.beans.editors;
/*    */ 
/*    */ public class DoubleEditor extends NumberEditor
/*    */ {
/*    */   public void setAsText(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 38 */     setValue(paramString == null ? null : Double.valueOf(paramString));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.editors.DoubleEditor
 * JD-Core Version:    0.6.2
 */