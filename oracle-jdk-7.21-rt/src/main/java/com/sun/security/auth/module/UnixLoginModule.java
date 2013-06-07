/*     */ package com.sun.security.auth.module;
/*     */ 
/*     */ import com.sun.security.auth.UnixNumericGroupPrincipal;
/*     */ import com.sun.security.auth.UnixNumericUserPrincipal;
/*     */ import com.sun.security.auth.UnixPrincipal;
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.login.FailedLoginException;
/*     */ import javax.security.auth.login.LoginException;
/*     */ import javax.security.auth.spi.LoginModule;
/*     */ 
/*     */ public class UnixLoginModule
/*     */   implements LoginModule
/*     */ {
/*     */   private Subject subject;
/*     */   private CallbackHandler callbackHandler;
/*     */   private Map<String, ?> sharedState;
/*     */   private Map<String, ?> options;
/*  59 */   private boolean debug = true;
/*     */   private UnixSystem ss;
/*  65 */   private boolean succeeded = false;
/*  66 */   private boolean commitSucceeded = false;
/*     */   private UnixPrincipal userPrincipal;
/*     */   private UnixNumericUserPrincipal UIDPrincipal;
/*     */   private UnixNumericGroupPrincipal GIDPrincipal;
/*  72 */   private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups = new LinkedList();
/*     */ 
/*     */   public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
/*     */   {
/*  96 */     this.subject = paramSubject;
/*  97 */     this.callbackHandler = paramCallbackHandler;
/*  98 */     this.sharedState = paramMap1;
/*  99 */     this.options = paramMap2;
/*     */ 
/* 102 */     this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
/*     */   }
/*     */ 
/*     */   public boolean login()
/*     */     throws LoginException
/*     */   {
/* 122 */     long[] arrayOfLong = null;
/*     */ 
/* 124 */     this.ss = new UnixSystem();
/*     */ 
/* 126 */     if (this.ss == null) {
/* 127 */       this.succeeded = false;
/* 128 */       throw new FailedLoginException("Failed in attempt to import the underlying system identity information");
/*     */     }
/*     */ 
/* 132 */     this.userPrincipal = new UnixPrincipal(this.ss.getUsername());
/* 133 */     this.UIDPrincipal = new UnixNumericUserPrincipal(this.ss.getUid());
/* 134 */     this.GIDPrincipal = new UnixNumericGroupPrincipal(this.ss.getGid(), true);
/*     */     int i;
/* 135 */     if ((this.ss.getGroups() != null) && (this.ss.getGroups().length > 0)) {
/* 136 */       arrayOfLong = this.ss.getGroups();
/* 137 */       for (i = 0; i < arrayOfLong.length; i++) {
/* 138 */         UnixNumericGroupPrincipal localUnixNumericGroupPrincipal = new UnixNumericGroupPrincipal(arrayOfLong[i], false);
/*     */ 
/* 141 */         if (!localUnixNumericGroupPrincipal.getName().equals(this.GIDPrincipal.getName()))
/* 142 */           this.supplementaryGroups.add(localUnixNumericGroupPrincipal);
/*     */       }
/*     */     }
/* 145 */     if (this.debug) {
/* 146 */       System.out.println("\t\t[UnixLoginModule]: succeeded importing info: ");
/*     */ 
/* 148 */       System.out.println("\t\t\tuid = " + this.ss.getUid());
/* 149 */       System.out.println("\t\t\tgid = " + this.ss.getGid());
/* 150 */       arrayOfLong = this.ss.getGroups();
/* 151 */       for (i = 0; i < arrayOfLong.length; i++) {
/* 152 */         System.out.println("\t\t\tsupp gid = " + arrayOfLong[i]);
/*     */       }
/*     */     }
/* 155 */     this.succeeded = true;
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean commit()
/*     */     throws LoginException
/*     */   {
/* 184 */     if (!this.succeeded) {
/* 185 */       if (this.debug) {
/* 186 */         System.out.println("\t\t[UnixLoginModule]: did not add any Principals to Subject because own authentication failed.");
/*     */       }
/*     */ 
/* 190 */       return false;
/*     */     }
/* 192 */     if (this.subject.isReadOnly()) {
/* 193 */       throw new LoginException("commit Failed: Subject is Readonly");
/*     */     }
/*     */ 
/* 196 */     if (!this.subject.getPrincipals().contains(this.userPrincipal))
/* 197 */       this.subject.getPrincipals().add(this.userPrincipal);
/* 198 */     if (!this.subject.getPrincipals().contains(this.UIDPrincipal))
/* 199 */       this.subject.getPrincipals().add(this.UIDPrincipal);
/* 200 */     if (!this.subject.getPrincipals().contains(this.GIDPrincipal))
/* 201 */       this.subject.getPrincipals().add(this.GIDPrincipal);
/* 202 */     for (int i = 0; i < this.supplementaryGroups.size(); i++) {
/* 203 */       if (!this.subject.getPrincipals().contains(this.supplementaryGroups.get(i)))
/*     */       {
/* 205 */         this.subject.getPrincipals().add(this.supplementaryGroups.get(i));
/*     */       }
/*     */     }
/* 208 */     if (this.debug) {
/* 209 */       System.out.println("\t\t[UnixLoginModule]: added UnixPrincipal,");
/*     */ 
/* 211 */       System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
/* 212 */       System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
/* 213 */       System.out.println("\t\t\t to Subject");
/*     */     }
/*     */ 
/* 216 */     this.commitSucceeded = true;
/* 217 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean abort()
/*     */     throws LoginException
/*     */   {
/* 241 */     if (this.debug) {
/* 242 */       System.out.println("\t\t[UnixLoginModule]: aborted authentication attempt");
/*     */     }
/*     */ 
/* 246 */     if (!this.succeeded)
/* 247 */       return false;
/* 248 */     if ((this.succeeded == true) && (!this.commitSucceeded))
/*     */     {
/* 251 */       this.succeeded = false;
/* 252 */       this.ss = null;
/* 253 */       this.userPrincipal = null;
/* 254 */       this.UIDPrincipal = null;
/* 255 */       this.GIDPrincipal = null;
/* 256 */       this.supplementaryGroups = new LinkedList();
/*     */     }
/*     */     else
/*     */     {
/* 260 */       logout();
/*     */     }
/* 262 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean logout()
/*     */     throws LoginException
/*     */   {
/* 280 */     if (this.subject.isReadOnly()) {
/* 281 */       throw new LoginException("logout Failed: Subject is Readonly");
/*     */     }
/*     */ 
/* 285 */     this.subject.getPrincipals().remove(this.userPrincipal);
/* 286 */     this.subject.getPrincipals().remove(this.UIDPrincipal);
/* 287 */     this.subject.getPrincipals().remove(this.GIDPrincipal);
/* 288 */     for (int i = 0; i < this.supplementaryGroups.size(); i++) {
/* 289 */       this.subject.getPrincipals().remove(this.supplementaryGroups.get(i));
/*     */     }
/*     */ 
/* 293 */     this.ss = null;
/* 294 */     this.succeeded = false;
/* 295 */     this.commitSucceeded = false;
/* 296 */     this.userPrincipal = null;
/* 297 */     this.UIDPrincipal = null;
/* 298 */     this.GIDPrincipal = null;
/* 299 */     this.supplementaryGroups = new LinkedList();
/*     */ 
/* 301 */     if (this.debug) {
/* 302 */       System.out.println("\t\t[UnixLoginModule]: logged out Subject");
/*     */     }
/*     */ 
/* 305 */     return true;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.auth.module.UnixLoginModule
 * JD-Core Version:    0.6.2
 */