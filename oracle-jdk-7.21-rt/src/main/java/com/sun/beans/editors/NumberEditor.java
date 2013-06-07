/*    */ package com.sun.beans.editors;
/*    */ 
/*    */ import java.beans.PropertyEditorSupport;
/*    */ 
/*    */ public abstract class NumberEditor extends PropertyEditorSupport
/*    */ {
/*    */   public String getJavaInitializationString()
/*    */   {
/* 38 */     Object localObject = getValue();
/* 39 */     return localObject != null ? localObject.toString() : "null";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.editors.NumberEditor
 * JD-Core Version:    0.6.2
 */