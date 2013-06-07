/*    */ package com.sun.jndi.ldap;
/*    */ 
/*    */ import javax.naming.Binding;
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.ldap.Control;
/*    */ import javax.naming.ldap.HasControls;
/*    */ 
/*    */ class BindingWithControls extends Binding
/*    */   implements HasControls
/*    */ {
/*    */   private Control[] controls;
/*    */   private static final long serialVersionUID = 9117274533692320040L;
/*    */ 
/*    */   public BindingWithControls(String paramString, Object paramObject, Control[] paramArrayOfControl)
/*    */   {
/* 35 */     super(paramString, paramObject);
/* 36 */     this.controls = paramArrayOfControl;
/*    */   }
/*    */ 
/*    */   public Control[] getControls() throws NamingException {
/* 40 */     return this.controls;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.BindingWithControls
 * JD-Core Version:    0.6.2
 */