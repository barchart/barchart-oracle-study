/*    */ package com.sun.org.apache.xml.internal.utils;
/*    */ 
/*    */ public class CharKey
/*    */ {
/*    */   private char m_char;
/*    */ 
/*    */   public CharKey(char key)
/*    */   {
/* 43 */     this.m_char = key;
/*    */   }
/*    */ 
/*    */   public CharKey()
/*    */   {
/*    */   }
/*    */ 
/*    */   public final void setChar(char c)
/*    */   {
/* 60 */     this.m_char = c;
/*    */   }
/*    */ 
/*    */   public final int hashCode()
/*    */   {
/* 72 */     return this.m_char;
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object obj)
/*    */   {
/* 84 */     return ((CharKey)obj).m_char == this.m_char;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xml.internal.utils.CharKey
 * JD-Core Version:    0.6.2
 */