/*    */ package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;
/*    */ 
/*    */ class UniqueValue
/*    */ {
/* 53 */   private static int part = 0;
/*    */ 
/*    */   public static String getUniqueBoundaryValue()
/*    */   {
/* 63 */     StringBuffer s = new StringBuffer();
/*    */ 
/* 66 */     s.append("----=_Part_").append(part++).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
/*    */ 
/* 69 */     return s.toString();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.packaging.mime.internet.UniqueValue
 * JD-Core Version:    0.6.2
 */