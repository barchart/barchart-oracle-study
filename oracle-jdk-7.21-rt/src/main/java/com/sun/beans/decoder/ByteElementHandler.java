/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class ByteElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 61 */     return Byte.decode(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.ByteElementHandler
 * JD-Core Version:    0.6.2
 */