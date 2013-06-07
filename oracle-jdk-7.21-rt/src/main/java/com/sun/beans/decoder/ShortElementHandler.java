/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class ShortElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Short.decode(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.ShortElementHandler
 * JD-Core Version:    0.6.2
 */