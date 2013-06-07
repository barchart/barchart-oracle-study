/*    */ package com.sun.beans.decoder;
/*    */ 
/*    */ final class ClassElementHandler extends StringElementHandler
/*    */ {
/*    */   public Object getValue(String paramString)
/*    */   {
/* 60 */     return getOwner().findClass(paramString);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.beans.decoder.ClassElementHandler
 * JD-Core Version:    0.6.2
 */