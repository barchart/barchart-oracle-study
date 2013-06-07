/*    */ package com.sun.istack.internal.localization;
/*    */ 
/*    */ public class LocalizableMessageFactory
/*    */ {
/*    */   private final String _bundlename;
/*    */ 
/*    */   public LocalizableMessageFactory(String bundlename)
/*    */   {
/* 36 */     this._bundlename = bundlename;
/*    */   }
/*    */ 
/*    */   public Localizable getMessage(String key, Object[] args) {
/* 40 */     return new LocalizableMessage(this._bundlename, key, args);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.istack.internal.localization.LocalizableMessageFactory
 * JD-Core Version:    0.6.2
 */