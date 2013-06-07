/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class DoubleElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Double.valueOf(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.DoubleElementHandler
 * JD-Core Version:    0.6.2
 */