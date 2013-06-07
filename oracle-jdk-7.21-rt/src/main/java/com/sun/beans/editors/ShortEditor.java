/*    */ package com.sun.beans.editors;
/*    */ 
/*    */ public class ShortEditor extends NumberEditor
/*    */ {
/*    */   public String getJavaInitializationString()
/*    */   {
/* 39 */     Object localObject = getValue();
/* 40 */     return localObject != null ? "((short)" + localObject + ")" : "null";
/*    */   }
/*    */ 
/*    */   public void setAsText(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 46 */     setValue(paramString == null ? null : Short.decode(paramString));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.editors.ShortEditor
 * JD-Core Version:    0.6.2
 */