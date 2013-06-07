/*    */ package com.sun.xml.internal.ws.util.localization;
/*    */ 
/*    */ public abstract interface Localizable
/*    */ {
/* 62 */   public static final String NOT_LOCALIZABLE = new String("");
/*    */ 
/*    */   public abstract String getKey();
/*    */ 
/*    */   public abstract Object[] getArguments();
/*    */ 
/*    */   public abstract String getResourceBundleName();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.util.localization.Localizable
 * JD-Core Version:    0.6.2
 */