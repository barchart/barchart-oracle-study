/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class FloatElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Float.valueOf(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.FloatElementHandler
 * JD-Core Version:    0.6.2
 */