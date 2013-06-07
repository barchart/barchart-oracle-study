/*    */ package com.sun.beans.editors;
/*    */ 
/*    */ public class ByteEditor extends NumberEditor
/*    */ {
/*    */   public String getJavaInitializationString()
/*    */   {
/* 38 */     Object localObject = getValue();
/* 39 */     return localObject != null ? "((byte)" + localObject + ")" : "null";
/*    */   }
/*    */ 
/*    */   public void setAsText(String paramString)
/*    */     throws IllegalArgumentException
/*    */   {
/* 45 */     setValue(paramString == null ? null : Byte.decode(paramString));
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.editors.ByteEditor
 * JD-Core Version:    0.6.2
 */