/*    */ package com.sun.org.apache.xml.internal.utils.res;
/*    */ 
/*    */ public class CharArrayWrapper
/*    */ {
/*    */   private char[] m_char;
/*    */ 
/*    */   public CharArrayWrapper(char[] arg)
/*    */   {
/* 34 */     this.m_char = arg;
/*    */   }
/*    */ 
/*    */   public char getChar(int index) {
/* 38 */     return this.m_char[index];
/*    */   }
/*    */ 
/*    */   public int getLength() {
/* 42 */     return this.m_char.length;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.res.CharArrayWrapper
 * JD-Core Version:    0.6.2
 */