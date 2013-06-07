/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class IntElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Integer.decode(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.IntElementHandler
 * JD-Core Version:    0.6.2
 */