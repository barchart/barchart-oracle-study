/*    */ package com.sun.jndi.ldap;
/*    */ 
/*    */ import com.sun.jndi.toolkit.ctx.Continuation;
/*    */ import java.util.Vector;
/*    */ import javax.naming.Binding;
/*    */ import javax.naming.CompositeName;
/*    */ import javax.naming.Name;
/*    */ import javax.naming.NameClassPair;
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.directory.Attributes;
/*    */ import javax.naming.spi.DirectoryManager;
/*    */ 
/*    */ final class LdapBindingEnumeration extends LdapNamingEnumeration
/*    */ {
/*    */   LdapBindingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation)
/*    */     throws NamingException
/*    */   {
/* 40 */     super(paramLdapCtx, paramLdapResult, paramName, paramContinuation);
/*    */   }
/*    */ 
/*    */   protected NameClassPair createItem(String paramString, Attributes paramAttributes, Vector paramVector)
/*    */     throws NamingException
/*    */   {
/* 47 */     Object localObject1 = null;
/* 48 */     String str = getAtom(paramString);
/*    */ 
/* 50 */     if (paramAttributes.get(Obj.JAVA_ATTRIBUTES[2]) != null)
/*    */     {
/* 52 */       localObject1 = Obj.decodeObject(paramAttributes);
/*    */     }
/* 54 */     if (localObject1 == null)
/*    */     {
/* 56 */       localObject1 = new LdapCtx(this.homeCtx, paramString);
/*    */     }
/*    */ 
/* 59 */     CompositeName localCompositeName = new CompositeName();
/* 60 */     localCompositeName.add(str);
/*    */     try
/*    */     {
/* 63 */       localObject1 = DirectoryManager.getObjectInstance(localObject1, localCompositeName, this.homeCtx, this.homeCtx.envprops, paramAttributes);
/*    */     }
/*    */     catch (NamingException localNamingException1)
/*    */     {
/* 67 */       throw localNamingException1;
/*    */     }
/*    */     catch (Exception localException) {
/* 70 */       NamingException localNamingException2 = new NamingException("problem generating object using object factory");
/*    */ 
/* 73 */       localNamingException2.setRootCause(localException);
/* 74 */       throw localNamingException2;
/*    */     }
/*    */     Object localObject2;
/* 78 */     if (paramVector != null) {
/* 79 */       localObject2 = new BindingWithControls(localCompositeName.toString(), localObject1, this.homeCtx.convertControls(paramVector));
/*    */     }
/*    */     else {
/* 82 */       localObject2 = new Binding(localCompositeName.toString(), localObject1);
/*    */     }
/* 84 */     ((Binding)localObject2).setNameInNamespace(paramString);
/* 85 */     return localObject2;
/*    */   }
/*    */ 
/*    */   protected LdapNamingEnumeration getReferredResults(LdapReferralContext paramLdapReferralContext)
/*    */     throws NamingException
/*    */   {
/* 91 */     return (LdapNamingEnumeration)paramLdapReferralContext.listBindings(this.listArg);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.LdapBindingEnumeration
 * JD-Core Version:    0.6.2
 */