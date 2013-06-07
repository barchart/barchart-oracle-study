/*     */ package com.sun.jndi.ldap;
/*     */ 
/*     */ import com.sun.jndi.toolkit.ctx.Continuation;
/*     */ import java.util.Vector;
/*     */ import javax.naming.CompositeName;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NameClassPair;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attributes;
/*     */ import javax.naming.directory.BasicAttributes;
/*     */ import javax.naming.directory.SearchControls;
/*     */ import javax.naming.directory.SearchResult;
/*     */ import javax.naming.ldap.LdapName;
/*     */ import javax.naming.spi.DirectoryManager;
/*     */ 
/*     */ final class LdapSearchEnumeration extends LdapNamingEnumeration
/*     */ {
/*     */   private Name startName;
/*  40 */   private LdapCtx.SearchArgs searchArgs = null;
/*     */ 
/*     */   LdapSearchEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, String paramString, LdapCtx.SearchArgs paramSearchArgs, Continuation paramContinuation)
/*     */     throws NamingException
/*     */   {
/*  46 */     super(paramLdapCtx, paramLdapResult, paramSearchArgs.name, paramContinuation);
/*     */ 
/*  51 */     this.startName = new LdapName(paramString);
/*  52 */     this.searchArgs = paramSearchArgs;
/*     */   }
/*     */ 
/*     */   protected NameClassPair createItem(String paramString, Attributes paramAttributes, Vector paramVector)
/*     */     throws NamingException
/*     */   {
/*  59 */     Object localObject1 = null;
/*     */ 
/*  63 */     boolean bool = true;
/*     */     String str1;
/*     */     String str2;
/*     */     try
/*     */     {
/*  69 */       LdapName localLdapName = new LdapName(paramString);
/*     */ 
/*  73 */       if ((this.startName != null) && (localLdapName.startsWith(this.startName))) {
/*  74 */         str1 = localLdapName.getSuffix(this.startName.size()).toString();
/*  75 */         str2 = localLdapName.getSuffix(this.homeCtx.currentParsedDN.size()).toString();
/*     */       } else {
/*  77 */         bool = false;
/*  78 */         str2 = str1 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, paramString, this.homeCtx.hasLdapsScheme);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (NamingException localNamingException1)
/*     */     {
/*  84 */       bool = false;
/*  85 */       str2 = str1 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, paramString, this.homeCtx.hasLdapsScheme);
/*     */     }
/*     */ 
/*  91 */     CompositeName localCompositeName1 = new CompositeName();
/*  92 */     if (!str1.equals("")) {
/*  93 */       localCompositeName1.add(str1);
/*     */     }
/*     */ 
/*  97 */     CompositeName localCompositeName2 = new CompositeName();
/*  98 */     if (!str2.equals("")) {
/*  99 */       localCompositeName2.add(str2);
/*     */     }
/*     */ 
/* 105 */     this.homeCtx.setParents(paramAttributes, localCompositeName2);
/*     */     Object localObject2;
/* 108 */     if (this.searchArgs.cons.getReturningObjFlag())
/*     */     {
/* 110 */       if (paramAttributes.get(Obj.JAVA_ATTRIBUTES[2]) != null)
/*     */       {
/* 113 */         localObject1 = Obj.decodeObject(paramAttributes);
/*     */       }
/*     */ 
/* 116 */       if (localObject1 == null) {
/* 117 */         localObject1 = new LdapCtx(this.homeCtx, paramString);
/*     */       }
/*     */ 
/*     */       Object localObject3;
/*     */       try
/*     */       {
/* 123 */         localObject1 = DirectoryManager.getObjectInstance(localObject1, localCompositeName2, bool ? this.homeCtx : null, this.homeCtx.envprops, paramAttributes);
/*     */       }
/*     */       catch (NamingException localNamingException2)
/*     */       {
/* 127 */         throw localNamingException2;
/*     */       } catch (Exception localException) {
/* 129 */         localObject3 = new NamingException("problem generating object using object factory");
/*     */ 
/* 132 */         ((NamingException)localObject3).setRootCause(localException);
/* 133 */         throw ((Throwable)localObject3);
/*     */       }
/*     */ 
/* 141 */       if ((localObject2 = this.searchArgs.reqAttrs) != null)
/*     */       {
/* 143 */         localObject3 = new BasicAttributes(true);
/* 144 */         for (int i = 0; i < localObject2.length; i++) {
/* 145 */           ((Attributes)localObject3).put(localObject2[i], null);
/*     */         }
/* 147 */         for (i = 0; i < Obj.JAVA_ATTRIBUTES.length; i++)
/*     */         {
/* 149 */           if (((Attributes)localObject3).get(Obj.JAVA_ATTRIBUTES[i]) == null) {
/* 150 */             paramAttributes.remove(Obj.JAVA_ATTRIBUTES[i]);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 164 */     if (paramVector != null) {
/* 165 */       localObject2 = new SearchResultWithControls(bool ? localCompositeName1.toString() : str1, localObject1, paramAttributes, bool, this.homeCtx.convertControls(paramVector));
/*     */     }
/*     */     else
/*     */     {
/* 169 */       localObject2 = new SearchResult(bool ? localCompositeName1.toString() : str1, localObject1, paramAttributes, bool);
/*     */     }
/*     */ 
/* 173 */     ((SearchResult)localObject2).setNameInNamespace(paramString);
/* 174 */     return localObject2;
/*     */   }
/*     */ 
/*     */   public void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException)
/*     */   {
/* 180 */     this.startName = null;
/* 181 */     super.appendUnprocessedReferrals(paramLdapReferralException);
/*     */   }
/*     */ 
/*     */   protected LdapNamingEnumeration getReferredResults(LdapReferralContext paramLdapReferralContext)
/*     */     throws NamingException
/*     */   {
/* 187 */     return (LdapSearchEnumeration)paramLdapReferralContext.search(this.searchArgs.name, this.searchArgs.filter, this.searchArgs.cons);
/*     */   }
/*     */ 
/*     */   protected void update(LdapNamingEnumeration paramLdapNamingEnumeration)
/*     */   {
/* 192 */     super.update(paramLdapNamingEnumeration);
/*     */ 
/* 195 */     LdapSearchEnumeration localLdapSearchEnumeration = (LdapSearchEnumeration)paramLdapNamingEnumeration;
/* 196 */     this.startName = localLdapSearchEnumeration.startName;
/*     */   }
/*     */ 
/*     */   void setStartName(Name paramName)
/*     */   {
/* 202 */     this.startName = paramName;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.LdapSearchEnumeration
 * JD-Core Version:    0.6.2
 */