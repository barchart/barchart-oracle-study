/*    */ package com.sun.jndi.ldap;
/*    */ 
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.directory.Attributes;
/*    */ import javax.naming.directory.SearchResult;
/*    */ import javax.naming.ldap.Control;
/*    */ import javax.naming.ldap.HasControls;
/*    */ 
/*    */ class SearchResultWithControls extends SearchResult
/*    */   implements HasControls
/*    */ {
/*    */   private Control[] controls;
/*    */   private static final long serialVersionUID = 8476983938747908202L;
/*    */ 
/*    */   public SearchResultWithControls(String paramString, Object paramObject, Attributes paramAttributes, boolean paramBoolean, Control[] paramArrayOfControl)
/*    */   {
/* 38 */     super(paramString, paramObject, paramAttributes, paramBoolean);
/* 39 */     this.controls = paramArrayOfControl;
/*    */   }
/*    */ 
/*    */   public Control[] getControls() throws NamingException {
/* 43 */     return this.controls;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.SearchResultWithControls
 * JD-Core Version:    0.6.2
 */