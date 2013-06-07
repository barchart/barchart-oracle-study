/*    */ package com.sun.corba.se.impl.orbutil;
/*    */ 
/*    */ public abstract class RepositoryIdFactory
/*    */ {
/* 33 */   private static final RepIdDelegator currentDelegator = new RepIdDelegator();
/*    */ 
/*    */   public static RepositoryIdStrings getRepIdStringsFactory()
/*    */   {
/* 41 */     return currentDelegator;
/*    */   }
/*    */ 
/*    */   public static RepositoryIdUtility getRepIdUtility()
/*    */   {
/* 49 */     return currentDelegator;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.orbutil.RepositoryIdFactory
 * JD-Core Version:    0.6.2
 */