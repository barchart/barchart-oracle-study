/*    */ package com.sun.xml.internal.bind.v2.runtime.output;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class Pcdata
/*    */   implements CharSequence
/*    */ {
/*    */   public abstract void writeTo(UTF8XmlOutput paramUTF8XmlOutput)
/*    */     throws IOException;
/*    */ 
/*    */   public void writeTo(char[] buf, int start)
/*    */   {
/* 71 */     toString().getChars(0, length(), buf, start);
/*    */   }
/*    */ 
/*    */   public abstract String toString();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.runtime.output.Pcdata
 * JD-Core Version:    0.6.2
 */