/*      */ package com.sun.security.auth.module;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import javax.security.auth.DestroyFailedException;
/*      */ import javax.security.auth.RefreshFailedException;
/*      */ import javax.security.auth.Subject;
/*      */ import javax.security.auth.callback.Callback;
/*      */ import javax.security.auth.callback.CallbackHandler;
/*      */ import javax.security.auth.callback.NameCallback;
/*      */ import javax.security.auth.callback.PasswordCallback;
/*      */ import javax.security.auth.callback.UnsupportedCallbackException;
/*      */ import javax.security.auth.kerberos.KerberosKey;
/*      */ import javax.security.auth.kerberos.KerberosPrincipal;
/*      */ import javax.security.auth.kerberos.KerberosTicket;
/*      */ import javax.security.auth.kerberos.KeyTab;
/*      */ import javax.security.auth.login.LoginException;
/*      */ import javax.security.auth.spi.LoginModule;
/*      */ import sun.misc.HexDumpEncoder;
/*      */ import sun.security.jgss.krb5.Krb5Util;
/*      */ import sun.security.jgss.krb5.Krb5Util.KeysFromKeyTab;
/*      */ import sun.security.krb5.Config;
/*      */ import sun.security.krb5.Credentials;
/*      */ import sun.security.krb5.EncryptionKey;
/*      */ import sun.security.krb5.KrbAsReqBuilder;
/*      */ import sun.security.krb5.KrbException;
/*      */ import sun.security.krb5.PrincipalName;
/*      */ 
/*      */ public class Krb5LoginModule
/*      */   implements LoginModule
/*      */ {
/*      */   private Subject subject;
/*      */   private CallbackHandler callbackHandler;
/*      */   private Map sharedState;
/*      */   private Map<String, ?> options;
/*  374 */   private boolean debug = false;
/*  375 */   private boolean storeKey = false;
/*  376 */   private boolean doNotPrompt = false;
/*  377 */   private boolean useTicketCache = false;
/*  378 */   private boolean useKeyTab = false;
/*  379 */   private String ticketCacheName = null;
/*  380 */   private String keyTabName = null;
/*  381 */   private String princName = null;
/*      */ 
/*  383 */   private boolean useFirstPass = false;
/*  384 */   private boolean tryFirstPass = false;
/*  385 */   private boolean storePass = false;
/*  386 */   private boolean clearPass = false;
/*  387 */   private boolean refreshKrb5Config = false;
/*  388 */   private boolean renewTGT = false;
/*      */ 
/*  392 */   private boolean isInitiator = true;
/*      */ 
/*  395 */   private boolean succeeded = false;
/*  396 */   private boolean commitSucceeded = false;
/*      */   private String username;
/*  401 */   private EncryptionKey[] encKeys = null;
/*      */ 
/*  403 */   KeyTab ktab = null;
/*      */ 
/*  405 */   private Credentials cred = null;
/*      */ 
/*  407 */   private PrincipalName principal = null;
/*  408 */   private KerberosPrincipal kerbClientPrinc = null;
/*  409 */   private KerberosTicket kerbTicket = null;
/*  410 */   private KerberosKey[] kerbKeys = null;
/*  411 */   private StringBuffer krb5PrincName = null;
/*  412 */   private char[] password = null;
/*      */   private static final String NAME = "javax.security.auth.login.name";
/*      */   private static final String PWD = "javax.security.auth.login.password";
/*  416 */   static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.util.AuthResources");
/*      */ 
/*      */   public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
/*      */   {
/*  441 */     this.subject = paramSubject;
/*  442 */     this.callbackHandler = paramCallbackHandler;
/*  443 */     this.sharedState = paramMap1;
/*  444 */     this.options = paramMap2;
/*      */ 
/*  448 */     this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
/*  449 */     this.storeKey = "true".equalsIgnoreCase((String)paramMap2.get("storeKey"));
/*  450 */     this.doNotPrompt = "true".equalsIgnoreCase((String)paramMap2.get("doNotPrompt"));
/*      */ 
/*  452 */     this.useTicketCache = "true".equalsIgnoreCase((String)paramMap2.get("useTicketCache"));
/*      */ 
/*  454 */     this.useKeyTab = "true".equalsIgnoreCase((String)paramMap2.get("useKeyTab"));
/*  455 */     this.ticketCacheName = ((String)paramMap2.get("ticketCache"));
/*  456 */     this.keyTabName = ((String)paramMap2.get("keyTab"));
/*  457 */     this.princName = ((String)paramMap2.get("principal"));
/*  458 */     this.refreshKrb5Config = "true".equalsIgnoreCase((String)paramMap2.get("refreshKrb5Config"));
/*      */ 
/*  460 */     this.renewTGT = "true".equalsIgnoreCase((String)paramMap2.get("renewTGT"));
/*      */ 
/*  464 */     String str = (String)paramMap2.get("isInitiator");
/*  465 */     if (str != null)
/*      */     {
/*  468 */       this.isInitiator = "true".equalsIgnoreCase(str);
/*      */     }
/*      */ 
/*  471 */     this.tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
/*      */ 
/*  474 */     this.useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
/*      */ 
/*  477 */     this.storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
/*      */ 
/*  479 */     this.clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
/*      */ 
/*  481 */     if (this.debug)
/*  482 */       System.out.print("Debug is  " + this.debug + " storeKey " + this.storeKey + " useTicketCache " + this.useTicketCache + " useKeyTab " + this.useKeyTab + " doNotPrompt " + this.doNotPrompt + " ticketCache is " + this.ticketCacheName + " isInitiator " + this.isInitiator + " KeyTab is " + this.keyTabName + " refreshKrb5Config is " + this.refreshKrb5Config + " principal is " + this.princName + " tryFirstPass is " + this.tryFirstPass + " useFirstPass is " + this.useFirstPass + " storePass is " + this.storePass + " clearPass is " + this.clearPass + "\n");
/*      */   }
/*      */ 
/*      */   public boolean login()
/*      */     throws LoginException
/*      */   {
/*  516 */     validateConfiguration();
/*  517 */     if (this.refreshKrb5Config) {
/*      */       try {
/*  519 */         if (this.debug) {
/*  520 */           System.out.println("Refreshing Kerberos configuration");
/*      */         }
/*  522 */         Config.refresh();
/*      */       } catch (KrbException localKrbException) {
/*  524 */         LoginException localLoginException1 = new LoginException(localKrbException.getMessage());
/*  525 */         localLoginException1.initCause(localKrbException);
/*  526 */         throw localLoginException1;
/*      */       }
/*      */     }
/*  529 */     String str = System.getProperty("sun.security.krb5.principal");
/*      */ 
/*  531 */     if (str != null) {
/*  532 */       this.krb5PrincName = new StringBuffer(str);
/*      */     }
/*  534 */     else if (this.princName != null) {
/*  535 */       this.krb5PrincName = new StringBuffer(this.princName);
/*      */     }
/*      */ 
/*  539 */     if (this.tryFirstPass) {
/*      */       try {
/*  541 */         attemptAuthentication(true);
/*  542 */         if (this.debug) {
/*  543 */           System.out.println("\t\t[Krb5LoginModule] authentication succeeded");
/*      */         }
/*  545 */         this.succeeded = true;
/*  546 */         cleanState();
/*  547 */         return true;
/*      */       }
/*      */       catch (LoginException localLoginException2) {
/*  550 */         cleanState();
/*  551 */         if (this.debug) {
/*  552 */           System.out.println("\t\t[Krb5LoginModule] tryFirstPass failed with:" + localLoginException2.getMessage());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*  557 */     else if (this.useFirstPass) {
/*      */       try {
/*  559 */         attemptAuthentication(true);
/*  560 */         this.succeeded = true;
/*  561 */         cleanState();
/*  562 */         return true;
/*      */       }
/*      */       catch (LoginException localLoginException3) {
/*  565 */         if (this.debug) {
/*  566 */           System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + localLoginException3.getMessage());
/*      */         }
/*      */ 
/*  570 */         this.succeeded = false;
/*  571 */         cleanState();
/*  572 */         throw localLoginException3;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  580 */       attemptAuthentication(false);
/*  581 */       this.succeeded = true;
/*  582 */       cleanState();
/*  583 */       return true;
/*      */     }
/*      */     catch (LoginException localLoginException4) {
/*  586 */       if (this.debug) {
/*  587 */         System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + localLoginException4.getMessage());
/*      */       }
/*      */ 
/*  591 */       this.succeeded = false;
/*  592 */       cleanState();
/*  593 */       throw localLoginException4;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void attemptAuthentication(boolean paramBoolean)
/*      */     throws LoginException
/*      */   {
/*      */     Object localObject;
/*  610 */     if (this.krb5PrincName != null) {
/*      */       try {
/*  612 */         this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
/*      */       }
/*      */       catch (KrbException localKrbException1)
/*      */       {
/*  616 */         localObject = new LoginException(localKrbException1.getMessage());
/*  617 */         ((LoginException)localObject).initCause(localKrbException1);
/*  618 */         throw ((Throwable)localObject);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  623 */       if (this.useTicketCache)
/*      */       {
/*  625 */         if (this.debug)
/*  626 */           System.out.println("Acquire TGT from Cache");
/*  627 */         this.cred = Credentials.acquireTGTFromCache(this.principal, this.ticketCacheName);
/*      */ 
/*  630 */         if (this.cred != null)
/*      */         {
/*  632 */           if (!isCurrent(this.cred)) {
/*  633 */             if (this.renewTGT) {
/*  634 */               this.cred = renewCredentials(this.cred);
/*      */             }
/*      */             else {
/*  637 */               this.cred = null;
/*  638 */               if (this.debug) {
/*  639 */                 System.out.println("Credentials are no longer valid");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  645 */         if (this.cred != null)
/*      */         {
/*  647 */           if (this.principal == null) {
/*  648 */             this.principal = this.cred.getClient();
/*      */           }
/*      */         }
/*  651 */         if (this.debug) {
/*  652 */           System.out.println("Principal is " + this.principal);
/*  653 */           if (this.cred == null) {
/*  654 */             System.out.println("null credentials from Ticket Cache");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  663 */       if (this.cred == null)
/*      */       {
/*  666 */         if (this.principal == null) {
/*  667 */           promptForName(paramBoolean);
/*  668 */           this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
/*      */         }
/*      */ 
/*  692 */         if (this.useKeyTab) {
/*  693 */           this.ktab = (this.keyTabName == null ? KeyTab.getInstance() : KeyTab.getInstance(new File(this.keyTabName)));
/*      */ 
/*  696 */           if ((this.isInitiator) && 
/*  697 */             (Krb5Util.keysFromJavaxKeyTab(this.ktab, this.principal).length == 0))
/*      */           {
/*  699 */             this.ktab = null;
/*  700 */             if (this.debug)
/*  701 */               System.out.println("Key for the principal " + this.principal + " not available in " + (this.keyTabName == null ? "default key tab" : this.keyTabName));
/*      */           }
/*      */         }
/*      */         KrbAsReqBuilder localKrbAsReqBuilder;
/*  714 */         if (this.ktab == null) {
/*  715 */           promptForPass(paramBoolean);
/*  716 */           localKrbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.password);
/*  717 */           if (this.isInitiator)
/*      */           {
/*  721 */             this.cred = localKrbAsReqBuilder.action().getCreds();
/*      */           }
/*  723 */           if (this.storeKey) {
/*  724 */             this.encKeys = localKrbAsReqBuilder.getKeys(this.isInitiator);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  729 */           localKrbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.ktab);
/*  730 */           if (this.isInitiator) {
/*  731 */             this.cred = localKrbAsReqBuilder.action().getCreds();
/*      */           }
/*      */         }
/*  734 */         localKrbAsReqBuilder.destroy();
/*      */ 
/*  736 */         if (this.debug) {
/*  737 */           System.out.println("principal is " + this.principal);
/*  738 */           localObject = new HexDumpEncoder();
/*  739 */           if (this.ktab != null)
/*  740 */             System.out.println("Will use keytab");
/*  741 */           else if (this.storeKey) {
/*  742 */             for (int i = 0; i < this.encKeys.length; i++) {
/*  743 */               System.out.println("EncryptionKey: keyType=" + this.encKeys[i].getEType() + " keyBytes (hex dump)=" + ((HexDumpEncoder)localObject).encodeBuffer(this.encKeys[i].getBytes()));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  752 */         if ((this.isInitiator) && (this.cred == null)) {
/*  753 */           throw new LoginException("TGT Can not be obtained from the KDC ");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (KrbException localKrbException2)
/*      */     {
/*  759 */       localObject = new LoginException(localKrbException2.getMessage());
/*  760 */       ((LoginException)localObject).initCause(localKrbException2);
/*  761 */       throw ((Throwable)localObject);
/*      */     } catch (IOException localIOException) {
/*  763 */       localObject = new LoginException(localIOException.getMessage());
/*  764 */       ((LoginException)localObject).initCause(localIOException);
/*  765 */       throw ((Throwable)localObject);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void promptForName(boolean paramBoolean) throws LoginException
/*      */   {
/*  771 */     this.krb5PrincName = new StringBuffer("");
/*  772 */     if (paramBoolean)
/*      */     {
/*  774 */       this.username = ((String)this.sharedState.get("javax.security.auth.login.name"));
/*  775 */       if (this.debug) {
/*  776 */         System.out.println("username from shared state is " + this.username + "\n");
/*      */       }
/*      */ 
/*  779 */       if (this.username == null) {
/*  780 */         System.out.println("username from shared state is null\n");
/*      */ 
/*  782 */         throw new LoginException("Username can not be obtained from sharedstate ");
/*      */       }
/*      */ 
/*  785 */       if (this.debug) {
/*  786 */         System.out.println("username from shared state is " + this.username + "\n");
/*      */       }
/*      */ 
/*  789 */       if ((this.username != null) && (this.username.length() > 0)) {
/*  790 */         this.krb5PrincName.insert(0, this.username);
/*  791 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  795 */     if (this.doNotPrompt) {
/*  796 */       throw new LoginException("Unable to obtain Princpal Name for authentication ");
/*      */     }
/*      */ 
/*  799 */     if (this.callbackHandler == null) {
/*  800 */       throw new LoginException("No CallbackHandler available to garner authentication information from the user");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  805 */       String str = System.getProperty("user.name");
/*      */ 
/*  807 */       Callback[] arrayOfCallback = new Callback[1];
/*  808 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Kerberos.username.defUsername."));
/*      */ 
/*  811 */       Object[] arrayOfObject = { str };
/*  812 */       arrayOfCallback[0] = new NameCallback(localMessageFormat.format(arrayOfObject));
/*  813 */       this.callbackHandler.handle(arrayOfCallback);
/*  814 */       this.username = ((NameCallback)arrayOfCallback[0]).getName();
/*  815 */       if ((this.username == null) || (this.username.length() == 0))
/*  816 */         this.username = str;
/*  817 */       this.krb5PrincName.insert(0, this.username);
/*      */     }
/*      */     catch (IOException localIOException) {
/*  820 */       throw new LoginException(localIOException.getMessage());
/*      */     } catch (UnsupportedCallbackException localUnsupportedCallbackException) {
/*  822 */       throw new LoginException(localUnsupportedCallbackException.getMessage() + " not available to garner " + " authentication information " + " from the user");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void promptForPass(boolean paramBoolean)
/*      */     throws LoginException
/*      */   {
/*  834 */     if (paramBoolean)
/*      */     {
/*  836 */       this.password = ((char[])this.sharedState.get("javax.security.auth.login.password"));
/*  837 */       if (this.password == null) {
/*  838 */         if (this.debug) {
/*  839 */           System.out.println("Password from shared state is null");
/*      */         }
/*      */ 
/*  842 */         throw new LoginException("Password can not be obtained from sharedstate ");
/*      */       }
/*      */ 
/*  845 */       if (this.debug) {
/*  846 */         System.out.println("password is " + new String(this.password));
/*      */       }
/*      */ 
/*  849 */       return;
/*      */     }
/*  851 */     if (this.doNotPrompt) {
/*  852 */       throw new LoginException("Unable to obtain password from user\n");
/*      */     }
/*      */ 
/*  855 */     if (this.callbackHandler == null) {
/*  856 */       throw new LoginException("No CallbackHandler available to garner authentication information from the user");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  861 */       Callback[] arrayOfCallback = new Callback[1];
/*  862 */       String str = this.krb5PrincName.toString();
/*  863 */       MessageFormat localMessageFormat = new MessageFormat(rb.getString("Kerberos.password.for.username."));
/*      */ 
/*  866 */       Object[] arrayOfObject = { str };
/*  867 */       arrayOfCallback[0] = new PasswordCallback(localMessageFormat.format(arrayOfObject), false);
/*      */ 
/*  870 */       this.callbackHandler.handle(arrayOfCallback);
/*  871 */       char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[0]).getPassword();
/*      */ 
/*  873 */       if (arrayOfChar == null)
/*      */       {
/*  875 */         arrayOfChar = new char[0];
/*      */       }
/*  877 */       this.password = new char[arrayOfChar.length];
/*  878 */       System.arraycopy(arrayOfChar, 0, this.password, 0, arrayOfChar.length);
/*      */ 
/*  880 */       ((PasswordCallback)arrayOfCallback[0]).clearPassword();
/*      */ 
/*  884 */       for (int i = 0; i < arrayOfChar.length; i++)
/*  885 */         arrayOfChar[i] = ' ';
/*  886 */       arrayOfChar = null;
/*  887 */       if (this.debug) {
/*  888 */         System.out.println("\t\t[Krb5LoginModule] user entered username: " + this.krb5PrincName);
/*      */ 
/*  891 */         System.out.println();
/*      */       }
/*      */     } catch (IOException localIOException) {
/*  894 */       throw new LoginException(localIOException.getMessage());
/*      */     } catch (UnsupportedCallbackException localUnsupportedCallbackException) {
/*  896 */       throw new LoginException(localUnsupportedCallbackException.getMessage() + " not available to garner " + " authentication information " + "from the user");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void validateConfiguration()
/*      */     throws LoginException
/*      */   {
/*  905 */     if ((this.doNotPrompt) && (!this.useTicketCache) && (!this.useKeyTab) && (!this.tryFirstPass) && (!this.useFirstPass))
/*      */     {
/*  907 */       throw new LoginException("Configuration Error - either doNotPrompt should be  false or at least one of useTicketCache,  useKeyTab, tryFirstPass and useFirstPass should be true");
/*      */     }
/*      */ 
/*  913 */     if ((this.ticketCacheName != null) && (!this.useTicketCache)) {
/*  914 */       throw new LoginException("Configuration Error  - useTicketCache should be set to true to use the ticket cache" + this.ticketCacheName);
/*      */     }
/*      */ 
/*  919 */     if (((this.keyTabName != null ? 1 : 0) & (!this.useKeyTab ? 1 : 0)) != 0) {
/*  920 */       throw new LoginException("Configuration Error - useKeyTab should be set to true to use the keytab" + this.keyTabName);
/*      */     }
/*      */ 
/*  923 */     if ((this.storeKey) && (this.doNotPrompt) && (!this.useKeyTab) && (!this.tryFirstPass) && (!this.useFirstPass))
/*      */     {
/*  925 */       throw new LoginException("Configuration Error - either doNotPrompt should be set to  false or at least one of tryFirstPass, useFirstPass or useKeyTab must be set to true for storeKey option");
/*      */     }
/*      */ 
/*  929 */     if ((this.renewTGT) && (!this.useTicketCache))
/*  930 */       throw new LoginException("Configuration Error - either useTicketCache should be  true or renewTGT should be false");
/*      */   }
/*      */ 
/*      */   private boolean isCurrent(Credentials paramCredentials)
/*      */   {
/*  938 */     Date localDate = paramCredentials.getEndTime();
/*  939 */     if (localDate != null) {
/*  940 */       return System.currentTimeMillis() <= localDate.getTime();
/*      */     }
/*  942 */     return true;
/*      */   }
/*      */ 
/*      */   private Credentials renewCredentials(Credentials paramCredentials)
/*      */   {
/*      */     Credentials localCredentials;
/*      */     try {
/*  949 */       if (!paramCredentials.isRenewable()) {
/*  950 */         throw new RefreshFailedException("This ticket is not renewable");
/*      */       }
/*  952 */       if (System.currentTimeMillis() > this.cred.getRenewTill().getTime()) {
/*  953 */         throw new RefreshFailedException("This ticket is past its last renewal time.");
/*      */       }
/*  955 */       localCredentials = paramCredentials.renew();
/*  956 */       if (this.debug)
/*  957 */         System.out.println("Renewed Kerberos Ticket");
/*      */     } catch (Exception localException) {
/*  959 */       localCredentials = null;
/*  960 */       if (this.debug) {
/*  961 */         System.out.println("Ticket could not be renewed : " + localException.getMessage());
/*      */       }
/*      */     }
/*  964 */     return localCredentials;
/*      */   }
/*      */ 
/*      */   public boolean commit()
/*      */     throws LoginException
/*      */   {
/*  998 */     if (!this.succeeded) {
/*  999 */       return false;
/*      */     }
/*      */ 
/* 1002 */     if ((this.isInitiator) && (this.cred == null)) {
/* 1003 */       this.succeeded = false;
/* 1004 */       throw new LoginException("Null Client Credential");
/*      */     }
/*      */ 
/* 1007 */     if (this.subject.isReadOnly()) {
/* 1008 */       cleanKerberosCred();
/* 1009 */       throw new LoginException("Subject is Readonly");
/*      */     }
/*      */ 
/* 1019 */     Set localSet1 = this.subject.getPrivateCredentials();
/* 1020 */     Set localSet2 = this.subject.getPrincipals();
/* 1021 */     this.kerbClientPrinc = new KerberosPrincipal(this.principal.getName());
/*      */ 
/* 1024 */     if (this.isInitiator) {
/* 1025 */       this.kerbTicket = Krb5Util.credsToTicket(this.cred);
/*      */     }
/*      */ 
/* 1028 */     if ((this.storeKey) && (this.encKeys != null)) {
/* 1029 */       if (this.encKeys.length == 0) {
/* 1030 */         this.succeeded = false;
/* 1031 */         throw new LoginException("Null Server Key ");
/*      */       }
/*      */ 
/* 1034 */       this.kerbKeys = new KerberosKey[this.encKeys.length];
/* 1035 */       for (int i = 0; i < this.encKeys.length; i++) {
/* 1036 */         Integer localInteger = this.encKeys[i].getKeyVersionNumber();
/* 1037 */         this.kerbKeys[i] = new KerberosKey(this.kerbClientPrinc, this.encKeys[i].getBytes(), this.encKeys[i].getEType(), localInteger == null ? 0 : localInteger.intValue());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1047 */     if (!localSet2.contains(this.kerbClientPrinc)) {
/* 1048 */       localSet2.add(this.kerbClientPrinc);
/*      */     }
/*      */ 
/* 1052 */     if ((this.kerbTicket != null) && 
/* 1053 */       (!localSet1.contains(this.kerbTicket))) {
/* 1054 */       localSet1.add(this.kerbTicket);
/*      */     }
/*      */ 
/* 1057 */     if (this.storeKey) {
/* 1058 */       if (this.encKeys == null) {
/* 1059 */         if (!localSet1.contains(this.ktab)) {
/* 1060 */           localSet1.add(this.ktab);
/*      */ 
/* 1062 */           for (KerberosKey localKerberosKey : this.ktab.getKeys(this.kerbClientPrinc))
/* 1063 */             localSet1.add(new Krb5Util.KeysFromKeyTab(localKerberosKey));
/*      */         }
/*      */       }
/*      */       else {
/* 1067 */         for (int j = 0; j < this.kerbKeys.length; j++) {
/* 1068 */           if (!localSet1.contains(this.kerbKeys[j])) {
/* 1069 */             localSet1.add(this.kerbKeys[j]);
/*      */           }
/* 1071 */           this.encKeys[j].destroy();
/* 1072 */           this.encKeys[j] = null;
/* 1073 */           if (this.debug) {
/* 1074 */             System.out.println("Added server's key" + this.kerbKeys[j]);
/*      */ 
/* 1076 */             System.out.println("\t\t[Krb5LoginModule] added Krb5Principal  " + this.kerbClientPrinc.toString() + " to Subject");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1085 */     this.commitSucceeded = true;
/* 1086 */     if (this.debug)
/* 1087 */       System.out.println("Commit Succeeded \n");
/* 1088 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean abort()
/*      */     throws LoginException
/*      */   {
/* 1111 */     if (!this.succeeded)
/* 1112 */       return false;
/* 1113 */     if ((this.succeeded == true) && (!this.commitSucceeded))
/*      */     {
/* 1115 */       this.succeeded = false;
/* 1116 */       cleanKerberosCred();
/*      */     }
/*      */     else
/*      */     {
/* 1120 */       logout();
/*      */     }
/* 1122 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean logout()
/*      */     throws LoginException
/*      */   {
/* 1140 */     if (this.debug) {
/* 1141 */       System.out.println("\t\t[Krb5LoginModule]: Entering logout");
/*      */     }
/*      */ 
/* 1145 */     if (this.subject.isReadOnly()) {
/* 1146 */       cleanKerberosCred();
/* 1147 */       throw new LoginException("Subject is Readonly");
/*      */     }
/*      */ 
/* 1150 */     this.subject.getPrincipals().remove(this.kerbClientPrinc);
/*      */ 
/* 1152 */     Iterator localIterator = this.subject.getPrivateCredentials().iterator();
/* 1153 */     while (localIterator.hasNext()) {
/* 1154 */       Object localObject = localIterator.next();
/* 1155 */       if (((localObject instanceof KerberosTicket)) || ((localObject instanceof KerberosKey)) || ((localObject instanceof KeyTab)))
/*      */       {
/* 1158 */         localIterator.remove();
/*      */       }
/*      */     }
/*      */ 
/* 1162 */     cleanKerberosCred();
/*      */ 
/* 1164 */     this.succeeded = false;
/* 1165 */     this.commitSucceeded = false;
/* 1166 */     if (this.debug) {
/* 1167 */       System.out.println("\t\t[Krb5LoginModule]: logged out Subject");
/*      */     }
/*      */ 
/* 1170 */     return true;
/*      */   }
/*      */ 
/*      */   private void cleanKerberosCred()
/*      */     throws LoginException
/*      */   {
/*      */     try
/*      */     {
/* 1179 */       if (this.kerbTicket != null)
/* 1180 */         this.kerbTicket.destroy();
/* 1181 */       if (this.kerbKeys != null)
/* 1182 */         for (int i = 0; i < this.kerbKeys.length; i++)
/* 1183 */           this.kerbKeys[i].destroy();
/*      */     }
/*      */     catch (DestroyFailedException localDestroyFailedException)
/*      */     {
/* 1187 */       throw new LoginException("Destroy Failed on Kerberos Private Credentials");
/*      */     }
/*      */ 
/* 1190 */     this.kerbTicket = null;
/* 1191 */     this.kerbKeys = null;
/* 1192 */     this.kerbClientPrinc = null;
/*      */   }
/*      */ 
/*      */   private void cleanState()
/*      */   {
/* 1202 */     if (this.succeeded) {
/* 1203 */       if ((this.storePass) && (!this.sharedState.containsKey("javax.security.auth.login.name")) && (!this.sharedState.containsKey("javax.security.auth.login.password")))
/*      */       {
/* 1206 */         this.sharedState.put("javax.security.auth.login.name", this.username);
/* 1207 */         this.sharedState.put("javax.security.auth.login.password", this.password);
/*      */       }
/*      */     }
/*      */     else {
/* 1211 */       this.encKeys = null;
/* 1212 */       this.ktab = null;
/* 1213 */       this.principal = null;
/*      */     }
/* 1215 */     this.username = null;
/* 1216 */     this.password = null;
/* 1217 */     if ((this.krb5PrincName != null) && (this.krb5PrincName.length() != 0))
/* 1218 */       this.krb5PrincName.delete(0, this.krb5PrincName.length());
/* 1219 */     this.krb5PrincName = null;
/* 1220 */     if (this.clearPass) {
/* 1221 */       this.sharedState.remove("javax.security.auth.login.name");
/* 1222 */       this.sharedState.remove("javax.security.auth.login.password");
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.auth.module.Krb5LoginModule
 * JD-Core Version:    0.6.2
 */