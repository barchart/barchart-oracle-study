/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class LongElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Long.decode(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.LongElementHandler
 * JD-Core Version:    0.6.2
 */