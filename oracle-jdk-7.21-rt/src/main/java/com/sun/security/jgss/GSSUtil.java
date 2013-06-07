/*    */ package com.sun.security.jgss;
/*    */ 
/*    */ import javax.security.auth.Subject;
/*    */ import org.ietf.jgss.GSSCredential;
/*    */ import org.ietf.jgss.GSSName;
/*    */ 
/*    */ public class GSSUtil
/*    */ {
/*    */   public static Subject createSubject(GSSName paramGSSName, GSSCredential paramGSSCredential)
/*    */   {
/* 69 */     return sun.security.jgss.GSSUtil.getSubject(paramGSSName, paramGSSCredential);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.jgss.GSSUtil
 * JD-Core Version:    0.6.2
 */