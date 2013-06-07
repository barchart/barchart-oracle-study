/*      */ package com.sun.jndi.ldap;
/*      */ 
/*      */ import com.sun.jndi.ldap.pool.PoolCallback;
/*      */ import com.sun.jndi.ldap.pool.PooledConnection;
/*      */ import com.sun.jndi.ldap.sasl.LdapSasl;
/*      */ import com.sun.jndi.ldap.sasl.SaslInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ import javax.naming.AuthenticationException;
/*      */ import javax.naming.AuthenticationNotSupportedException;
/*      */ import javax.naming.CommunicationException;
/*      */ import javax.naming.NamingEnumeration;
/*      */ import javax.naming.NamingException;
/*      */ import javax.naming.directory.Attribute;
/*      */ import javax.naming.directory.Attributes;
/*      */ import javax.naming.directory.BasicAttributes;
/*      */ import javax.naming.directory.InvalidAttributeValueException;
/*      */ import javax.naming.ldap.Control;
/*      */ 
/*      */ public final class LdapClient
/*      */   implements PooledConnection
/*      */ {
/*      */   private static final int debug = 0;
/*      */   static final boolean caseIgnore = true;
/*   84 */   private static final Hashtable defaultBinaryAttrs = new Hashtable(23, 0.75F);
/*      */   private static final String DISCONNECT_OID = "1.3.6.1.4.1.1466.20036";
/*      */   boolean isLdapv3;
/*  115 */   int referenceCount = 1;
/*      */   Connection conn;
/*      */   private final PoolCallback pcb;
/*      */   private final boolean pooled;
/*  122 */   private boolean authenticateCalled = false;
/*      */   static final int SCOPE_BASE_OBJECT = 0;
/*      */   static final int SCOPE_ONE_LEVEL = 1;
/*      */   static final int SCOPE_SUBTREE = 2;
/*      */   static final int ADD = 0;
/*      */   static final int DELETE = 1;
/*      */   static final int REPLACE = 2;
/*      */   static final int LDAP_VERSION3_VERSION2 = 32;
/*      */   static final int LDAP_VERSION2 = 2;
/*      */   static final int LDAP_VERSION3 = 3;
/*      */   static final int LDAP_VERSION = 3;
/*      */   static final int LDAP_REF_FOLLOW = 1;
/*      */   static final int LDAP_REF_THROW = 2;
/*      */   static final int LDAP_REF_IGNORE = 3;
/*      */   static final String LDAP_URL = "ldap://";
/*      */   static final String LDAPS_URL = "ldaps://";
/*      */   static final int LBER_BOOLEAN = 1;
/*      */   static final int LBER_INTEGER = 2;
/*      */   static final int LBER_BITSTRING = 3;
/*      */   static final int LBER_OCTETSTRING = 4;
/*      */   static final int LBER_NULL = 5;
/*      */   static final int LBER_ENUMERATED = 10;
/*      */   static final int LBER_SEQUENCE = 48;
/*      */   static final int LBER_SET = 49;
/*      */   static final int LDAP_SUPERIOR_DN = 128;
/*      */   static final int LDAP_REQ_BIND = 96;
/*      */   static final int LDAP_REQ_UNBIND = 66;
/*      */   static final int LDAP_REQ_SEARCH = 99;
/*      */   static final int LDAP_REQ_MODIFY = 102;
/*      */   static final int LDAP_REQ_ADD = 104;
/*      */   static final int LDAP_REQ_DELETE = 74;
/*      */   static final int LDAP_REQ_MODRDN = 108;
/*      */   static final int LDAP_REQ_COMPARE = 110;
/*      */   static final int LDAP_REQ_ABANDON = 80;
/*      */   static final int LDAP_REQ_EXTENSION = 119;
/*      */   static final int LDAP_REP_BIND = 97;
/*      */   static final int LDAP_REP_SEARCH = 100;
/*      */   static final int LDAP_REP_SEARCH_REF = 115;
/*      */   static final int LDAP_REP_RESULT = 101;
/*      */   static final int LDAP_REP_MODIFY = 103;
/*      */   static final int LDAP_REP_ADD = 105;
/*      */   static final int LDAP_REP_DELETE = 107;
/*      */   static final int LDAP_REP_MODRDN = 109;
/*      */   static final int LDAP_REP_COMPARE = 111;
/*      */   static final int LDAP_REP_EXTENSION = 120;
/*      */   static final int LDAP_REP_REFERRAL = 163;
/*      */   static final int LDAP_REP_EXT_OID = 138;
/*      */   static final int LDAP_REP_EXT_VAL = 139;
/*      */   static final int LDAP_CONTROLS = 160;
/*      */   static final String LDAP_CONTROL_MANAGE_DSA_IT = "2.16.840.1.113730.3.4.2";
/*      */   static final String LDAP_CONTROL_PREFERRED_LANG = "1.3.6.1.4.1.1466.20035";
/*      */   static final String LDAP_CONTROL_PAGED_RESULTS = "1.2.840.113556.1.4.319";
/*      */   static final String LDAP_CONTROL_SERVER_SORT_REQ = "1.2.840.113556.1.4.473";
/*      */   static final String LDAP_CONTROL_SERVER_SORT_RES = "1.2.840.113556.1.4.474";
/*      */   static final int LDAP_SUCCESS = 0;
/*      */   static final int LDAP_OPERATIONS_ERROR = 1;
/*      */   static final int LDAP_PROTOCOL_ERROR = 2;
/*      */   static final int LDAP_TIME_LIMIT_EXCEEDED = 3;
/*      */   static final int LDAP_SIZE_LIMIT_EXCEEDED = 4;
/*      */   static final int LDAP_COMPARE_FALSE = 5;
/*      */   static final int LDAP_COMPARE_TRUE = 6;
/*      */   static final int LDAP_AUTH_METHOD_NOT_SUPPORTED = 7;
/*      */   static final int LDAP_STRONG_AUTH_REQUIRED = 8;
/*      */   static final int LDAP_PARTIAL_RESULTS = 9;
/*      */   static final int LDAP_REFERRAL = 10;
/*      */   static final int LDAP_ADMIN_LIMIT_EXCEEDED = 11;
/*      */   static final int LDAP_UNAVAILABLE_CRITICAL_EXTENSION = 12;
/*      */   static final int LDAP_CONFIDENTIALITY_REQUIRED = 13;
/*      */   static final int LDAP_SASL_BIND_IN_PROGRESS = 14;
/*      */   static final int LDAP_NO_SUCH_ATTRIBUTE = 16;
/*      */   static final int LDAP_UNDEFINED_ATTRIBUTE_TYPE = 17;
/*      */   static final int LDAP_INAPPROPRIATE_MATCHING = 18;
/*      */   static final int LDAP_CONSTRAINT_VIOLATION = 19;
/*      */   static final int LDAP_ATTRIBUTE_OR_VALUE_EXISTS = 20;
/*      */   static final int LDAP_INVALID_ATTRIBUTE_SYNTAX = 21;
/*      */   static final int LDAP_NO_SUCH_OBJECT = 32;
/*      */   static final int LDAP_ALIAS_PROBLEM = 33;
/*      */   static final int LDAP_INVALID_DN_SYNTAX = 34;
/*      */   static final int LDAP_IS_LEAF = 35;
/*      */   static final int LDAP_ALIAS_DEREFERENCING_PROBLEM = 36;
/*      */   static final int LDAP_INAPPROPRIATE_AUTHENTICATION = 48;
/*      */   static final int LDAP_INVALID_CREDENTIALS = 49;
/*      */   static final int LDAP_INSUFFICIENT_ACCESS_RIGHTS = 50;
/*      */   static final int LDAP_BUSY = 51;
/*      */   static final int LDAP_UNAVAILABLE = 52;
/*      */   static final int LDAP_UNWILLING_TO_PERFORM = 53;
/*      */   static final int LDAP_LOOP_DETECT = 54;
/*      */   static final int LDAP_NAMING_VIOLATION = 64;
/*      */   static final int LDAP_OBJECT_CLASS_VIOLATION = 65;
/*      */   static final int LDAP_NOT_ALLOWED_ON_NON_LEAF = 66;
/*      */   static final int LDAP_NOT_ALLOWED_ON_RDN = 67;
/*      */   static final int LDAP_ENTRY_ALREADY_EXISTS = 68;
/*      */   static final int LDAP_OBJECT_CLASS_MODS_PROHIBITED = 69;
/*      */   static final int LDAP_AFFECTS_MULTIPLE_DSAS = 71;
/*      */   static final int LDAP_OTHER = 80;
/* 1323 */   static final String[] ldap_error_message = { "Success", "Operations Error", "Protocol Error", "Timelimit Exceeded", "Sizelimit Exceeded", "Compare False", "Compare True", "Authentication Method Not Supported", "Strong Authentication Required", null, "Referral", "Administrative Limit Exceeded", "Unavailable Critical Extension", "Confidentiality Required", "SASL Bind In Progress", null, "No Such Attribute", "Undefined Attribute Type", "Inappropriate Matching", "Constraint Violation", "Attribute Or Value Exists", "Invalid Attribute Syntax", null, null, null, null, null, null, null, null, null, null, "No Such Object", "Alias Problem", "Invalid DN Syntax", null, "Alias Dereferencing Problem", null, null, null, null, null, null, null, null, null, null, null, "Inappropriate Authentication", "Invalid Credentials", "Insufficient Access Rights", "Busy", "Unavailable", "Unwilling To Perform", "Loop Detect", null, null, null, null, null, null, null, null, null, "Naming Violation", "Object Class Violation", "Not Allowed On Non-leaf", "Not Allowed On RDN", "Entry Already Exists", "Object Class Modifications Prohibited", null, "Affects Multiple DSAs", null, null, null, null, null, null, null, null, "Other", null, null, null, null, null, null, null, null, null, null };
/*      */ 
/* 1477 */   private Vector unsolicited = new Vector(3);
/*      */ 
/*      */   LdapClient(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, PoolCallback paramPoolCallback)
/*      */     throws NamingException
/*      */   {
/*  136 */     this.conn = new Connection(this, paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream);
/*      */ 
/*  139 */     this.pcb = paramPoolCallback;
/*  140 */     this.pooled = (paramPoolCallback != null);
/*      */   }
/*      */ 
/*      */   synchronized boolean authenticateCalled() {
/*  144 */     return this.authenticateCalled;
/*      */   }
/*      */ 
/*      */   synchronized LdapResult authenticate(boolean paramBoolean, String paramString1, Object paramObject, int paramInt, String paramString2, Control[] paramArrayOfControl, Hashtable paramHashtable)
/*      */     throws NamingException
/*      */   {
/*  152 */     this.authenticateCalled = true;
/*      */     try
/*      */     {
/*  155 */       ensureOpen();
/*      */     } catch (IOException localIOException1) {
/*  157 */       CommunicationException localCommunicationException1 = new CommunicationException();
/*  158 */       localCommunicationException1.setRootCause(localIOException1);
/*  159 */       throw localCommunicationException1;
/*      */     }
/*      */ 
/*  162 */     switch (paramInt) {
/*      */     case 3:
/*      */     case 32:
/*  165 */       this.isLdapv3 = true;
/*  166 */       break;
/*      */     case 2:
/*  168 */       this.isLdapv3 = false;
/*  169 */       break;
/*      */     default:
/*  171 */       throw new CommunicationException("Protocol version " + paramInt + " not supported");
/*      */     }
/*      */ 
/*  175 */     LdapResult localLdapResult = null;
/*      */     CommunicationException localCommunicationException4;
/*  177 */     if ((paramString2.equalsIgnoreCase("none")) || (paramString2.equalsIgnoreCase("anonymous")))
/*      */     {
/*  182 */       if ((!paramBoolean) || (paramInt == 2) || (paramInt == 32) || ((paramArrayOfControl != null) && (paramArrayOfControl.length > 0)))
/*      */       {
/*      */         try
/*      */         {
/*  188 */           localLdapResult = ldapBind(paramString1 = null, (byte[])(paramObject = null), paramArrayOfControl, null, false);
/*      */ 
/*  190 */           if (localLdapResult.status == 0)
/*  191 */             this.conn.setBound();
/*      */         }
/*      */         catch (IOException localIOException2) {
/*  194 */           CommunicationException localCommunicationException2 = new CommunicationException("anonymous bind failed: " + this.conn.host + ":" + this.conn.port);
/*      */ 
/*  197 */           localCommunicationException2.setRootCause(localIOException2);
/*  198 */           throw localCommunicationException2;
/*      */         }
/*      */       }
/*      */       else {
/*  202 */         localLdapResult = new LdapResult();
/*  203 */         localLdapResult.status = 0;
/*      */       }
/*  205 */     } else if (paramString2.equalsIgnoreCase("simple"))
/*      */     {
/*  207 */       byte[] arrayOfByte1 = null;
/*      */       try {
/*  209 */         arrayOfByte1 = encodePassword(paramObject, this.isLdapv3);
/*  210 */         localLdapResult = ldapBind(paramString1, arrayOfByte1, paramArrayOfControl, null, false);
/*  211 */         if (localLdapResult.status == 0) {
/*  212 */           this.conn.setBound();
/*      */         }
/*      */ 
/*  223 */         if ((arrayOfByte1 != paramObject) && (arrayOfByte1 != null))
/*  224 */           for (int i = 0; i < arrayOfByte1.length; i++)
/*  225 */             arrayOfByte1[i] = 0;
/*      */       }
/*      */       catch (IOException localIOException4)
/*      */       {
/*  215 */         localCommunicationException4 = new CommunicationException("simple bind failed: " + this.conn.host + ":" + this.conn.port);
/*      */ 
/*  218 */         localCommunicationException4.setRootCause(localIOException4);
/*  219 */         throw localCommunicationException4;
/*      */       }
/*      */       finally
/*      */       {
/*  223 */         if ((arrayOfByte1 != paramObject) && (arrayOfByte1 != null)) {
/*  224 */           for (int k = 0; k < arrayOfByte1.length; k++)
/*  225 */             arrayOfByte1[k] = 0;
/*      */         }
/*      */       }
/*      */     }
/*  229 */     else if (this.isLdapv3)
/*      */     {
/*      */       try {
/*  232 */         localLdapResult = LdapSasl.saslBind(this, this.conn, this.conn.host, paramString1, paramObject, paramString2, paramHashtable, paramArrayOfControl);
/*      */ 
/*  234 */         if (localLdapResult.status == 0)
/*  235 */           this.conn.setBound();
/*      */       }
/*      */       catch (IOException localIOException3) {
/*  238 */         CommunicationException localCommunicationException3 = new CommunicationException("SASL bind failed: " + this.conn.host + ":" + this.conn.port);
/*      */ 
/*  241 */         localCommunicationException3.setRootCause(localIOException3);
/*  242 */         throw localCommunicationException3;
/*      */       }
/*      */     } else {
/*  245 */       throw new AuthenticationNotSupportedException(paramString2);
/*      */     }
/*      */ 
/*  251 */     if ((paramBoolean) && (localLdapResult.status == 2) && (paramInt == 32) && ((paramString2.equalsIgnoreCase("none")) || (paramString2.equalsIgnoreCase("anonymous")) || (paramString2.equalsIgnoreCase("simple"))))
/*      */     {
/*  258 */       byte[] arrayOfByte2 = null;
/*      */       try {
/*  260 */         this.isLdapv3 = false;
/*  261 */         arrayOfByte2 = encodePassword(paramObject, false);
/*  262 */         localLdapResult = ldapBind(paramString1, arrayOfByte2, paramArrayOfControl, null, false);
/*  263 */         if (localLdapResult.status == 0) {
/*  264 */           this.conn.setBound();
/*      */         }
/*      */ 
/*  275 */         if ((arrayOfByte2 != paramObject) && (arrayOfByte2 != null))
/*  276 */           for (int j = 0; j < arrayOfByte2.length; j++)
/*  277 */             arrayOfByte2[j] = 0;
/*      */       }
/*      */       catch (IOException localIOException5)
/*      */       {
/*  267 */         localCommunicationException4 = new CommunicationException(paramString2 + ":" + this.conn.host + ":" + this.conn.port);
/*      */ 
/*  270 */         localCommunicationException4.setRootCause(localIOException5);
/*  271 */         throw localCommunicationException4;
/*      */       }
/*      */       finally
/*      */       {
/*  275 */         if ((arrayOfByte2 != paramObject) && (arrayOfByte2 != null)) {
/*  276 */           for (int m = 0; m < arrayOfByte2.length; m++) {
/*  277 */             arrayOfByte2[m] = 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  289 */     if (localLdapResult.status == 32) {
/*  290 */       throw new AuthenticationException(getErrorMessage(localLdapResult.status, localLdapResult.errorMessage));
/*      */     }
/*      */ 
/*  293 */     this.conn.setV3(this.isLdapv3);
/*  294 */     return localLdapResult;
/*      */   }
/*      */ 
/*      */   public synchronized LdapResult ldapBind(String paramString1, byte[] paramArrayOfByte, Control[] paramArrayOfControl, String paramString2, boolean paramBoolean)
/*      */     throws IOException, NamingException
/*      */   {
/*  309 */     ensureOpen();
/*      */ 
/*  312 */     this.conn.abandonOutstandingReqs(null);
/*      */ 
/*  314 */     BerEncoder localBerEncoder = new BerEncoder();
/*  315 */     int i = this.conn.getMsgId();
/*  316 */     LdapResult localLdapResult = new LdapResult();
/*  317 */     localLdapResult.status = 1;
/*      */ 
/*  322 */     localBerEncoder.beginSeq(48);
/*  323 */     localBerEncoder.encodeInt(i);
/*  324 */     localBerEncoder.beginSeq(96);
/*  325 */     localBerEncoder.encodeInt(this.isLdapv3 ? 3 : 2);
/*  326 */     localBerEncoder.encodeString(paramString1, this.isLdapv3);
/*      */ 
/*  329 */     if (paramString2 != null) {
/*  330 */       localBerEncoder.beginSeq(163);
/*  331 */       localBerEncoder.encodeString(paramString2, this.isLdapv3);
/*  332 */       if (paramArrayOfByte != null) {
/*  333 */         localBerEncoder.encodeOctetString(paramArrayOfByte, 4);
/*      */       }
/*      */ 
/*  336 */       localBerEncoder.endSeq();
/*      */     }
/*  338 */     else if (paramArrayOfByte != null) {
/*  339 */       localBerEncoder.encodeOctetString(paramArrayOfByte, 128);
/*      */     } else {
/*  341 */       localBerEncoder.encodeOctetString(null, 128, 0, 0);
/*      */     }
/*      */ 
/*  344 */     localBerEncoder.endSeq();
/*      */ 
/*  347 */     if (this.isLdapv3) {
/*  348 */       encodeControls(localBerEncoder, paramArrayOfControl);
/*      */     }
/*  350 */     localBerEncoder.endSeq();
/*      */ 
/*  352 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i, paramBoolean);
/*  353 */     if (paramArrayOfByte != null) {
/*  354 */       localBerEncoder.reset();
/*      */     }
/*      */ 
/*  358 */     BerDecoder localBerDecoder = this.conn.readReply(localLdapRequest);
/*      */ 
/*  360 */     localBerDecoder.parseSeq(null);
/*  361 */     localBerDecoder.parseInt();
/*  362 */     if (localBerDecoder.parseByte() != 97) {
/*  363 */       return localLdapResult;
/*      */     }
/*      */ 
/*  366 */     localBerDecoder.parseLength();
/*  367 */     parseResult(localBerDecoder, localLdapResult, this.isLdapv3);
/*      */ 
/*  370 */     if ((this.isLdapv3) && (localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 135))
/*      */     {
/*  373 */       localLdapResult.serverCreds = localBerDecoder.parseOctetString(135, null);
/*      */     }
/*      */ 
/*  376 */     localLdapResult.resControls = (this.isLdapv3 ? parseControls(localBerDecoder) : null);
/*      */ 
/*  378 */     this.conn.removeRequest(localLdapRequest);
/*  379 */     return localLdapResult;
/*      */   }
/*      */ 
/*      */   boolean usingSaslStreams()
/*      */   {
/*  389 */     return this.conn.inStream instanceof SaslInputStream;
/*      */   }
/*      */ 
/*      */   synchronized void incRefCount() {
/*  393 */     this.referenceCount += 1;
/*      */   }
/*      */ 
/*      */   private static byte[] encodePassword(Object paramObject, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  405 */     if ((paramObject instanceof char[])) {
/*  406 */       paramObject = new String((char[])paramObject);
/*      */     }
/*      */ 
/*  409 */     if ((paramObject instanceof String)) {
/*  410 */       if (paramBoolean) {
/*  411 */         return ((String)paramObject).getBytes("UTF8");
/*      */       }
/*  413 */       return ((String)paramObject).getBytes("8859_1");
/*      */     }
/*      */ 
/*  416 */     return (byte[])paramObject;
/*      */   }
/*      */ 
/*      */   synchronized void close(Control[] paramArrayOfControl, boolean paramBoolean)
/*      */   {
/*  421 */     this.referenceCount -= 1;
/*      */ 
/*  429 */     if ((this.referenceCount <= 0) && (this.conn != null))
/*      */     {
/*  431 */       if (!this.pooled)
/*      */       {
/*  433 */         this.conn.cleanup(paramArrayOfControl, false);
/*  434 */         this.conn = null;
/*      */       }
/*  439 */       else if (paramBoolean) {
/*  440 */         this.conn.cleanup(paramArrayOfControl, false);
/*  441 */         this.conn = null;
/*  442 */         this.pcb.removePooledConnection(this);
/*      */       } else {
/*  444 */         this.pcb.releasePooledConnection(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void forceClose(boolean paramBoolean)
/*      */   {
/*  452 */     this.referenceCount = 0;
/*      */ 
/*  458 */     if (this.conn != null)
/*      */     {
/*  461 */       this.conn.cleanup(null, false);
/*  462 */       this.conn = null;
/*      */ 
/*  464 */       if (paramBoolean)
/*  465 */         this.pcb.removePooledConnection(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */   {
/*  472 */     forceClose(this.pooled);
/*      */   }
/*      */ 
/*      */   public synchronized void closeConnection()
/*      */   {
/*  479 */     forceClose(false);
/*      */   }
/*      */ 
/*      */   void processConnectionClosure()
/*      */   {
/*  490 */     if (this.unsolicited.size() > 0)
/*      */     {
/*      */       String str;
/*  492 */       if (this.conn != null)
/*  493 */         str = this.conn.host + ":" + this.conn.port + " connection closed";
/*      */       else {
/*  495 */         str = "Connection closed";
/*      */       }
/*  497 */       notifyUnsolicited(new CommunicationException(str));
/*      */     }
/*      */ 
/*  501 */     if (this.pooled)
/*  502 */       this.pcb.removePooledConnection(this);
/*      */   }
/*      */ 
/*      */   LdapResult search(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, String[] paramArrayOfString, String paramString2, int paramInt5, Control[] paramArrayOfControl, Hashtable paramHashtable, boolean paramBoolean2, int paramInt6)
/*      */     throws IOException, NamingException
/*      */   {
/*  523 */     ensureOpen();
/*      */ 
/*  525 */     LdapResult localLdapResult = new LdapResult();
/*      */ 
/*  527 */     BerEncoder localBerEncoder = new BerEncoder();
/*  528 */     int i = this.conn.getMsgId();
/*      */ 
/*  530 */     localBerEncoder.beginSeq(48);
/*  531 */     localBerEncoder.encodeInt(i);
/*  532 */     localBerEncoder.beginSeq(99);
/*  533 */     localBerEncoder.encodeString(paramString1 == null ? "" : paramString1, this.isLdapv3);
/*  534 */     localBerEncoder.encodeInt(paramInt1, 10);
/*  535 */     localBerEncoder.encodeInt(paramInt2, 10);
/*  536 */     localBerEncoder.encodeInt(paramInt3);
/*  537 */     localBerEncoder.encodeInt(paramInt4);
/*  538 */     localBerEncoder.encodeBoolean(paramBoolean1);
/*  539 */     Filter.encodeFilterString(localBerEncoder, paramString2, this.isLdapv3);
/*  540 */     localBerEncoder.beginSeq(48);
/*  541 */     localBerEncoder.encodeStringArray(paramArrayOfString, this.isLdapv3);
/*  542 */     localBerEncoder.endSeq();
/*  543 */     localBerEncoder.endSeq();
/*  544 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/*  545 */     localBerEncoder.endSeq();
/*      */ 
/*  547 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i, false, paramInt6);
/*      */ 
/*  550 */     localLdapResult.msgId = i;
/*  551 */     localLdapResult.status = 0;
/*  552 */     if (paramBoolean2)
/*      */     {
/*  554 */       localLdapResult = getSearchReply(localLdapRequest, paramInt5, localLdapResult, paramHashtable);
/*      */     }
/*  556 */     return localLdapResult;
/*      */   }
/*      */ 
/*      */   void clearSearchReply(LdapResult paramLdapResult, Control[] paramArrayOfControl)
/*      */   {
/*  563 */     if ((paramLdapResult != null) && (this.conn != null))
/*      */     {
/*  567 */       LdapRequest localLdapRequest = this.conn.findRequest(paramLdapResult.msgId);
/*  568 */       if (localLdapRequest == null) {
/*  569 */         return;
/*      */       }
/*      */ 
/*  577 */       if (localLdapRequest.hasSearchCompleted())
/*  578 */         this.conn.removeRequest(localLdapRequest);
/*      */       else
/*  580 */         this.conn.abandonRequest(localLdapRequest, paramArrayOfControl);
/*      */     }
/*      */   }
/*      */ 
/*      */   LdapResult getSearchReply(int paramInt, LdapResult paramLdapResult, Hashtable paramHashtable)
/*      */     throws IOException, NamingException
/*      */   {
/*  591 */     ensureOpen();
/*      */     LdapRequest localLdapRequest;
/*  595 */     if ((localLdapRequest = this.conn.findRequest(paramLdapResult.msgId)) == null) {
/*  596 */       return null;
/*      */     }
/*      */ 
/*  599 */     return getSearchReply(localLdapRequest, paramInt, paramLdapResult, paramHashtable);
/*      */   }
/*      */ 
/*      */   private LdapResult getSearchReply(LdapRequest paramLdapRequest, int paramInt, LdapResult paramLdapResult, Hashtable paramHashtable)
/*      */     throws IOException, NamingException
/*      */   {
/*  606 */     if (paramInt == 0) {
/*  607 */       paramInt = 2147483647;
/*      */     }
/*  609 */     if (paramLdapResult.entries != null)
/*  610 */       paramLdapResult.entries.setSize(0);
/*      */     else {
/*  612 */       paramLdapResult.entries = new Vector(paramInt == 2147483647 ? 32 : paramInt);
/*      */     }
/*      */ 
/*  616 */     if (paramLdapResult.referrals != null) {
/*  617 */       paramLdapResult.referrals.setSize(0);
/*      */     }
/*      */ 
/*  630 */     for (int k = 0; k < paramInt; ) {
/*  631 */       BerDecoder localBerDecoder = this.conn.readReply(paramLdapRequest);
/*      */ 
/*  636 */       localBerDecoder.parseSeq(null);
/*  637 */       localBerDecoder.parseInt();
/*  638 */       int i = localBerDecoder.parseSeq(null);
/*      */ 
/*  640 */       if (i == 100)
/*      */       {
/*  643 */         BasicAttributes localBasicAttributes = new BasicAttributes(true);
/*  644 */         String str = localBerDecoder.parseString(this.isLdapv3);
/*  645 */         LdapEntry localLdapEntry = new LdapEntry(str, localBasicAttributes);
/*  646 */         int[] arrayOfInt = new int[1];
/*      */ 
/*  648 */         localBerDecoder.parseSeq(arrayOfInt);
/*  649 */         int j = localBerDecoder.getParsePosition() + arrayOfInt[0];
/*  650 */         while ((localBerDecoder.getParsePosition() < j) && (localBerDecoder.bytesLeft() > 0))
/*      */         {
/*  652 */           Attribute localAttribute = parseAttribute(localBerDecoder, paramHashtable);
/*  653 */           localBasicAttributes.put(localAttribute);
/*      */         }
/*  655 */         localLdapEntry.respCtls = (this.isLdapv3 ? parseControls(localBerDecoder) : null);
/*      */ 
/*  657 */         paramLdapResult.entries.addElement(localLdapEntry);
/*  658 */         k++;
/*      */       }
/*  660 */       else if ((i == 115) && (this.isLdapv3))
/*      */       {
/*  663 */         Vector localVector = new Vector(4);
/*      */ 
/*  667 */         if (localBerDecoder.peekByte() == 48)
/*      */         {
/*  669 */           localBerDecoder.parseSeq(null);
/*      */         }
/*      */ 
/*  672 */         while ((localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 4))
/*      */         {
/*  675 */           localVector.addElement(localBerDecoder.parseString(this.isLdapv3));
/*      */         }
/*      */ 
/*  678 */         if (paramLdapResult.referrals == null) {
/*  679 */           paramLdapResult.referrals = new Vector(4);
/*      */         }
/*  681 */         paramLdapResult.referrals.addElement(localVector);
/*  682 */         paramLdapResult.resControls = (this.isLdapv3 ? parseControls(localBerDecoder) : null);
/*      */       }
/*  686 */       else if (i == 120)
/*      */       {
/*  688 */         parseExtResponse(localBerDecoder, paramLdapResult);
/*      */       }
/*  690 */       else if (i == 101)
/*      */       {
/*  692 */         parseResult(localBerDecoder, paramLdapResult, this.isLdapv3);
/*  693 */         paramLdapResult.resControls = (this.isLdapv3 ? parseControls(localBerDecoder) : null);
/*      */ 
/*  695 */         this.conn.removeRequest(paramLdapRequest);
/*  696 */         return paramLdapResult;
/*      */       }
/*      */     }
/*      */ 
/*  700 */     return paramLdapResult;
/*      */   }
/*      */ 
/*      */   private Attribute parseAttribute(BerDecoder paramBerDecoder, Hashtable paramHashtable)
/*      */     throws IOException
/*      */   {
/*  706 */     int[] arrayOfInt = new int[1];
/*  707 */     int i = paramBerDecoder.parseSeq(null);
/*  708 */     String str = paramBerDecoder.parseString(this.isLdapv3);
/*  709 */     boolean bool = isBinaryValued(str, paramHashtable);
/*  710 */     LdapAttribute localLdapAttribute = new LdapAttribute(str);
/*      */ 
/*  712 */     if ((i = paramBerDecoder.parseSeq(arrayOfInt)) == 49) {
/*  713 */       int j = arrayOfInt[0];
/*      */       while (true) if ((paramBerDecoder.bytesLeft() > 0) && (j > 0)) {
/*      */           try {
/*  716 */             j -= parseAttributeValue(paramBerDecoder, localLdapAttribute, bool);
/*      */           } catch (IOException localIOException) {
/*  718 */             paramBerDecoder.seek(j);
/*      */           }
/*      */         }
/*      */     }
/*      */     else
/*      */     {
/*  724 */       paramBerDecoder.seek(arrayOfInt[0]);
/*      */     }
/*  726 */     return localLdapAttribute;
/*      */   }
/*      */ 
/*      */   private int parseAttributeValue(BerDecoder paramBerDecoder, Attribute paramAttribute, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  735 */     int[] arrayOfInt = new int[1];
/*      */ 
/*  737 */     if (paramBoolean)
/*  738 */       paramAttribute.add(paramBerDecoder.parseOctetString(paramBerDecoder.peekByte(), arrayOfInt));
/*      */     else {
/*  740 */       paramAttribute.add(paramBerDecoder.parseStringWithTag(4, this.isLdapv3, arrayOfInt));
/*      */     }
/*  742 */     return arrayOfInt[0];
/*      */   }
/*      */ 
/*      */   private boolean isBinaryValued(String paramString, Hashtable paramHashtable) {
/*  746 */     String str = paramString.toLowerCase();
/*      */ 
/*  748 */     return (str.indexOf(";binary") != -1) || (defaultBinaryAttrs.containsKey(str)) || ((paramHashtable != null) && (paramHashtable.containsKey(str)));
/*      */   }
/*      */ 
/*      */   static void parseResult(BerDecoder paramBerDecoder, LdapResult paramLdapResult, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/*  757 */     paramLdapResult.status = paramBerDecoder.parseEnumeration();
/*  758 */     paramLdapResult.matchedDN = paramBerDecoder.parseString(paramBoolean);
/*  759 */     paramLdapResult.errorMessage = paramBerDecoder.parseString(paramBoolean);
/*      */ 
/*  762 */     if ((paramBoolean) && (paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 163))
/*      */     {
/*  766 */       Vector localVector = new Vector(4);
/*  767 */       int[] arrayOfInt = new int[1];
/*      */ 
/*  769 */       paramBerDecoder.parseSeq(arrayOfInt);
/*  770 */       int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
/*  771 */       while ((paramBerDecoder.getParsePosition() < i) && (paramBerDecoder.bytesLeft() > 0))
/*      */       {
/*  774 */         localVector.addElement(paramBerDecoder.parseString(paramBoolean));
/*      */       }
/*      */ 
/*  777 */       if (paramLdapResult.referrals == null) {
/*  778 */         paramLdapResult.referrals = new Vector(4);
/*      */       }
/*  780 */       paramLdapResult.referrals.addElement(localVector);
/*      */     }
/*      */   }
/*      */ 
/*      */   static Vector parseControls(BerDecoder paramBerDecoder)
/*      */     throws IOException
/*      */   {
/*  788 */     if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 160)) {
/*  789 */       Vector localVector = new Vector(4);
/*      */ 
/*  791 */       boolean bool = false;
/*  792 */       byte[] arrayOfByte = null;
/*  793 */       int[] arrayOfInt = new int[1];
/*      */ 
/*  795 */       paramBerDecoder.parseSeq(arrayOfInt);
/*  796 */       int i = paramBerDecoder.getParsePosition() + arrayOfInt[0];
/*  797 */       while ((paramBerDecoder.getParsePosition() < i) && (paramBerDecoder.bytesLeft() > 0))
/*      */       {
/*  800 */         paramBerDecoder.parseSeq(null);
/*  801 */         String str = paramBerDecoder.parseString(true);
/*      */ 
/*  803 */         if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 1))
/*      */         {
/*  805 */           bool = paramBerDecoder.parseBoolean();
/*      */         }
/*  807 */         if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 4))
/*      */         {
/*  809 */           arrayOfByte = paramBerDecoder.parseOctetString(4, null);
/*      */         }
/*      */ 
/*  812 */         if (str != null) {
/*  813 */           localVector.addElement(new BasicControl(str, bool, arrayOfByte));
/*      */         }
/*      */       }
/*      */ 
/*  817 */       return localVector;
/*      */     }
/*  819 */     return null;
/*      */   }
/*      */ 
/*      */   private void parseExtResponse(BerDecoder paramBerDecoder, LdapResult paramLdapResult)
/*      */     throws IOException
/*      */   {
/*  826 */     parseResult(paramBerDecoder, paramLdapResult, this.isLdapv3);
/*      */ 
/*  828 */     if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 138))
/*      */     {
/*  830 */       paramLdapResult.extensionId = paramBerDecoder.parseStringWithTag(138, this.isLdapv3, null);
/*      */     }
/*      */ 
/*  833 */     if ((paramBerDecoder.bytesLeft() > 0) && (paramBerDecoder.peekByte() == 139))
/*      */     {
/*  835 */       paramLdapResult.extensionValue = paramBerDecoder.parseOctetString(139, null);
/*      */     }
/*      */ 
/*  839 */     paramLdapResult.resControls = parseControls(paramBerDecoder);
/*      */   }
/*      */ 
/*      */   static void encodeControls(BerEncoder paramBerEncoder, Control[] paramArrayOfControl)
/*      */     throws IOException
/*      */   {
/*  848 */     if ((paramArrayOfControl == null) || (paramArrayOfControl.length == 0)) {
/*  849 */       return;
/*      */     }
/*      */ 
/*  854 */     paramBerEncoder.beginSeq(160);
/*      */ 
/*  856 */     for (int i = 0; i < paramArrayOfControl.length; i++) {
/*  857 */       paramBerEncoder.beginSeq(48);
/*  858 */       paramBerEncoder.encodeString(paramArrayOfControl[i].getID(), true);
/*  859 */       if (paramArrayOfControl[i].isCritical())
/*  860 */         paramBerEncoder.encodeBoolean(true);
/*      */       byte[] arrayOfByte;
/*  862 */       if ((arrayOfByte = paramArrayOfControl[i].getEncodedValue()) != null) {
/*  863 */         paramBerEncoder.encodeOctetString(arrayOfByte, 4);
/*      */       }
/*  865 */       paramBerEncoder.endSeq();
/*      */     }
/*  867 */     paramBerEncoder.endSeq();
/*      */   }
/*      */ 
/*      */   private LdapResult processReply(LdapRequest paramLdapRequest, LdapResult paramLdapResult, int paramInt)
/*      */     throws IOException, NamingException
/*      */   {
/*  877 */     BerDecoder localBerDecoder = this.conn.readReply(paramLdapRequest);
/*      */ 
/*  879 */     localBerDecoder.parseSeq(null);
/*  880 */     localBerDecoder.parseInt();
/*  881 */     if (localBerDecoder.parseByte() != paramInt) {
/*  882 */       return paramLdapResult;
/*      */     }
/*      */ 
/*  885 */     localBerDecoder.parseLength();
/*  886 */     parseResult(localBerDecoder, paramLdapResult, this.isLdapv3);
/*  887 */     paramLdapResult.resControls = (this.isLdapv3 ? parseControls(localBerDecoder) : null);
/*      */ 
/*  889 */     this.conn.removeRequest(paramLdapRequest);
/*      */ 
/*  891 */     return paramLdapResult;
/*      */   }
/*      */ 
/*      */   LdapResult modify(String paramString, int[] paramArrayOfInt, Attribute[] paramArrayOfAttribute, Control[] paramArrayOfControl)
/*      */     throws IOException, NamingException
/*      */   {
/*  915 */     ensureOpen();
/*      */ 
/*  917 */     LdapResult localLdapResult = new LdapResult();
/*  918 */     localLdapResult.status = 1;
/*      */ 
/*  920 */     if ((paramString == null) || (paramArrayOfInt.length != paramArrayOfAttribute.length)) {
/*  921 */       return localLdapResult;
/*      */     }
/*  923 */     BerEncoder localBerEncoder = new BerEncoder();
/*  924 */     int i = this.conn.getMsgId();
/*      */ 
/*  926 */     localBerEncoder.beginSeq(48);
/*  927 */     localBerEncoder.encodeInt(i);
/*  928 */     localBerEncoder.beginSeq(102);
/*  929 */     localBerEncoder.encodeString(paramString, this.isLdapv3);
/*  930 */     localBerEncoder.beginSeq(48);
/*  931 */     for (int j = 0; j < paramArrayOfInt.length; j++) {
/*  932 */       localBerEncoder.beginSeq(48);
/*  933 */       localBerEncoder.encodeInt(paramArrayOfInt[j], 10);
/*      */ 
/*  936 */       if ((paramArrayOfInt[j] == 0) && (hasNoValue(paramArrayOfAttribute[j]))) {
/*  937 */         throw new InvalidAttributeValueException("'" + paramArrayOfAttribute[j].getID() + "' has no values.");
/*      */       }
/*      */ 
/*  940 */       encodeAttribute(localBerEncoder, paramArrayOfAttribute[j]);
/*      */ 
/*  942 */       localBerEncoder.endSeq();
/*      */     }
/*  944 */     localBerEncoder.endSeq();
/*  945 */     localBerEncoder.endSeq();
/*  946 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/*  947 */     localBerEncoder.endSeq();
/*      */ 
/*  949 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i);
/*      */ 
/*  951 */     return processReply(localLdapRequest, localLdapResult, 103);
/*      */   }
/*      */ 
/*      */   private void encodeAttribute(BerEncoder paramBerEncoder, Attribute paramAttribute)
/*      */     throws IOException, NamingException
/*      */   {
/*  957 */     paramBerEncoder.beginSeq(48);
/*  958 */     paramBerEncoder.encodeString(paramAttribute.getID(), this.isLdapv3);
/*  959 */     paramBerEncoder.beginSeq(49);
/*  960 */     NamingEnumeration localNamingEnumeration = paramAttribute.getAll();
/*      */ 
/*  962 */     while (localNamingEnumeration.hasMore()) {
/*  963 */       Object localObject = localNamingEnumeration.next();
/*  964 */       if ((localObject instanceof String))
/*  965 */         paramBerEncoder.encodeString((String)localObject, this.isLdapv3);
/*  966 */       else if ((localObject instanceof byte[]))
/*  967 */         paramBerEncoder.encodeOctetString((byte[])localObject, 4);
/*  968 */       else if (localObject != null)
/*      */       {
/*  971 */         throw new InvalidAttributeValueException("Malformed '" + paramAttribute.getID() + "' attribute value");
/*      */       }
/*      */     }
/*      */ 
/*  975 */     paramBerEncoder.endSeq();
/*  976 */     paramBerEncoder.endSeq();
/*      */   }
/*      */ 
/*      */   private static boolean hasNoValue(Attribute paramAttribute) throws NamingException {
/*  980 */     return (paramAttribute.size() == 0) || ((paramAttribute.size() == 1) && (paramAttribute.get() == null));
/*      */   }
/*      */ 
/*      */   LdapResult add(LdapEntry paramLdapEntry, Control[] paramArrayOfControl)
/*      */     throws IOException, NamingException
/*      */   {
/*  993 */     ensureOpen();
/*      */ 
/*  995 */     LdapResult localLdapResult = new LdapResult();
/*  996 */     localLdapResult.status = 1;
/*      */ 
/*  998 */     if ((paramLdapEntry == null) || (paramLdapEntry.DN == null)) {
/*  999 */       return localLdapResult;
/*      */     }
/* 1001 */     BerEncoder localBerEncoder = new BerEncoder();
/* 1002 */     int i = this.conn.getMsgId();
/*      */ 
/* 1005 */     localBerEncoder.beginSeq(48);
/* 1006 */     localBerEncoder.encodeInt(i);
/* 1007 */     localBerEncoder.beginSeq(104);
/* 1008 */     localBerEncoder.encodeString(paramLdapEntry.DN, this.isLdapv3);
/* 1009 */     localBerEncoder.beginSeq(48);
/* 1010 */     NamingEnumeration localNamingEnumeration = paramLdapEntry.attributes.getAll();
/* 1011 */     while (localNamingEnumeration.hasMore()) {
/* 1012 */       Attribute localAttribute = (Attribute)localNamingEnumeration.next();
/*      */ 
/* 1015 */       if (hasNoValue(localAttribute)) {
/* 1016 */         throw new InvalidAttributeValueException("'" + localAttribute.getID() + "' has no values.");
/*      */       }
/*      */ 
/* 1019 */       encodeAttribute(localBerEncoder, localAttribute);
/*      */     }
/*      */ 
/* 1022 */     localBerEncoder.endSeq();
/* 1023 */     localBerEncoder.endSeq();
/* 1024 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/* 1025 */     localBerEncoder.endSeq();
/*      */ 
/* 1027 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i);
/* 1028 */     return processReply(localLdapRequest, localLdapResult, 105);
/*      */   }
/*      */ 
/*      */   LdapResult delete(String paramString, Control[] paramArrayOfControl)
/*      */     throws IOException, NamingException
/*      */   {
/* 1041 */     ensureOpen();
/*      */ 
/* 1043 */     LdapResult localLdapResult = new LdapResult();
/* 1044 */     localLdapResult.status = 1;
/*      */ 
/* 1046 */     if (paramString == null) {
/* 1047 */       return localLdapResult;
/*      */     }
/* 1049 */     BerEncoder localBerEncoder = new BerEncoder();
/* 1050 */     int i = this.conn.getMsgId();
/*      */ 
/* 1052 */     localBerEncoder.beginSeq(48);
/* 1053 */     localBerEncoder.encodeInt(i);
/* 1054 */     localBerEncoder.encodeString(paramString, 74, this.isLdapv3);
/* 1055 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/* 1056 */     localBerEncoder.endSeq();
/*      */ 
/* 1058 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i);
/*      */ 
/* 1060 */     return processReply(localLdapRequest, localLdapResult, 107);
/*      */   }
/*      */ 
/*      */   LdapResult moddn(String paramString1, String paramString2, boolean paramBoolean, String paramString3, Control[] paramArrayOfControl)
/*      */     throws IOException, NamingException
/*      */   {
/* 1080 */     ensureOpen();
/*      */ 
/* 1082 */     int i = (paramString3 != null) && (paramString3.length() > 0) ? 1 : 0;
/*      */ 
/* 1085 */     LdapResult localLdapResult = new LdapResult();
/* 1086 */     localLdapResult.status = 1;
/*      */ 
/* 1088 */     if ((paramString1 == null) || (paramString2 == null)) {
/* 1089 */       return localLdapResult;
/*      */     }
/* 1091 */     BerEncoder localBerEncoder = new BerEncoder();
/* 1092 */     int j = this.conn.getMsgId();
/*      */ 
/* 1094 */     localBerEncoder.beginSeq(48);
/* 1095 */     localBerEncoder.encodeInt(j);
/* 1096 */     localBerEncoder.beginSeq(108);
/* 1097 */     localBerEncoder.encodeString(paramString1, this.isLdapv3);
/* 1098 */     localBerEncoder.encodeString(paramString2, this.isLdapv3);
/* 1099 */     localBerEncoder.encodeBoolean(paramBoolean);
/* 1100 */     if ((this.isLdapv3) && (i != 0))
/*      */     {
/* 1102 */       localBerEncoder.encodeString(paramString3, 128, this.isLdapv3);
/*      */     }
/* 1104 */     localBerEncoder.endSeq();
/* 1105 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/* 1106 */     localBerEncoder.endSeq();
/*      */ 
/* 1109 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, j);
/*      */ 
/* 1111 */     return processReply(localLdapRequest, localLdapResult, 109);
/*      */   }
/*      */ 
/*      */   LdapResult compare(String paramString1, String paramString2, String paramString3, Control[] paramArrayOfControl)
/*      */     throws IOException, NamingException
/*      */   {
/* 1124 */     ensureOpen();
/*      */ 
/* 1126 */     LdapResult localLdapResult = new LdapResult();
/* 1127 */     localLdapResult.status = 1;
/*      */ 
/* 1129 */     if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null)) {
/* 1130 */       return localLdapResult;
/*      */     }
/* 1132 */     BerEncoder localBerEncoder = new BerEncoder();
/* 1133 */     int i = this.conn.getMsgId();
/*      */ 
/* 1135 */     localBerEncoder.beginSeq(48);
/* 1136 */     localBerEncoder.encodeInt(i);
/* 1137 */     localBerEncoder.beginSeq(110);
/* 1138 */     localBerEncoder.encodeString(paramString1, this.isLdapv3);
/* 1139 */     localBerEncoder.beginSeq(48);
/* 1140 */     localBerEncoder.encodeString(paramString2, this.isLdapv3);
/*      */ 
/* 1143 */     byte[] arrayOfByte = this.isLdapv3 ? paramString3.getBytes("UTF8") : paramString3.getBytes("8859_1");
/*      */ 
/* 1145 */     localBerEncoder.encodeOctetString(Filter.unescapeFilterValue(arrayOfByte, 0, arrayOfByte.length), 4);
/*      */ 
/* 1149 */     localBerEncoder.endSeq();
/* 1150 */     localBerEncoder.endSeq();
/* 1151 */     if (this.isLdapv3) encodeControls(localBerEncoder, paramArrayOfControl);
/* 1152 */     localBerEncoder.endSeq();
/*      */ 
/* 1154 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i);
/*      */ 
/* 1156 */     return processReply(localLdapRequest, localLdapResult, 111);
/*      */   }
/*      */ 
/*      */   LdapResult extendedOp(String paramString, byte[] paramArrayOfByte, Control[] paramArrayOfControl, boolean paramBoolean)
/*      */     throws IOException, NamingException
/*      */   {
/* 1168 */     ensureOpen();
/*      */ 
/* 1170 */     LdapResult localLdapResult = new LdapResult();
/* 1171 */     localLdapResult.status = 1;
/*      */ 
/* 1173 */     if (paramString == null) {
/* 1174 */       return localLdapResult;
/*      */     }
/* 1176 */     BerEncoder localBerEncoder = new BerEncoder();
/* 1177 */     int i = this.conn.getMsgId();
/*      */ 
/* 1179 */     localBerEncoder.beginSeq(48);
/* 1180 */     localBerEncoder.encodeInt(i);
/* 1181 */     localBerEncoder.beginSeq(119);
/* 1182 */     localBerEncoder.encodeString(paramString, 128, this.isLdapv3);
/*      */ 
/* 1184 */     if (paramArrayOfByte != null) {
/* 1185 */       localBerEncoder.encodeOctetString(paramArrayOfByte, 129);
/*      */     }
/*      */ 
/* 1188 */     localBerEncoder.endSeq();
/* 1189 */     encodeControls(localBerEncoder, paramArrayOfControl);
/* 1190 */     localBerEncoder.endSeq();
/*      */ 
/* 1192 */     LdapRequest localLdapRequest = this.conn.writeRequest(localBerEncoder, i, paramBoolean);
/*      */ 
/* 1194 */     BerDecoder localBerDecoder = this.conn.readReply(localLdapRequest);
/*      */ 
/* 1196 */     localBerDecoder.parseSeq(null);
/* 1197 */     localBerDecoder.parseInt();
/* 1198 */     if (localBerDecoder.parseByte() != 120) {
/* 1199 */       return localLdapResult;
/*      */     }
/*      */ 
/* 1202 */     localBerDecoder.parseLength();
/* 1203 */     parseExtResponse(localBerDecoder, localLdapResult);
/* 1204 */     this.conn.removeRequest(localLdapRequest);
/*      */ 
/* 1206 */     return localLdapResult;
/*      */   }
/*      */ 
/*      */   static String getErrorMessage(int paramInt, String paramString)
/*      */   {
/* 1430 */     String str = "[LDAP: error code " + paramInt;
/*      */ 
/* 1432 */     if ((paramString != null) && (paramString.length() != 0))
/*      */     {
/* 1435 */       str = str + " - " + paramString + "]";
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/* 1441 */         if (ldap_error_message[paramInt] != null)
/* 1442 */           str = str + " - " + ldap_error_message[paramInt] + "]";
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
/*      */       {
/* 1446 */         str = str + "]";
/*      */       }
/*      */     }
/* 1449 */     return str;
/*      */   }
/*      */ 
/*      */   void addUnsolicited(LdapCtx paramLdapCtx)
/*      */   {
/* 1482 */     this.unsolicited.addElement(paramLdapCtx);
/*      */   }
/*      */ 
/*      */   void removeUnsolicited(LdapCtx paramLdapCtx)
/*      */   {
/* 1489 */     synchronized (this.unsolicited) {
/* 1490 */       if (this.unsolicited.size() == 0) {
/* 1491 */         return;
/*      */       }
/* 1493 */       this.unsolicited.removeElement(paramLdapCtx);
/*      */     }
/*      */   }
/*      */ 
/*      */   void processUnsolicited(BerDecoder paramBerDecoder)
/*      */   {
/* 1503 */     synchronized (this.unsolicited)
/*      */     {
/*      */       try {
/* 1506 */         LdapResult localLdapResult = new LdapResult();
/*      */ 
/* 1508 */         paramBerDecoder.parseSeq(null);
/* 1509 */         paramBerDecoder.parseInt();
/* 1510 */         if (paramBerDecoder.parseByte() != 120) {
/* 1511 */           throw new IOException("Unsolicited Notification must be an Extended Response");
/*      */         }
/*      */ 
/* 1514 */         paramBerDecoder.parseLength();
/* 1515 */         parseExtResponse(paramBerDecoder, localLdapResult);
/*      */ 
/* 1517 */         if ("1.3.6.1.4.1.1466.20036".equals(localLdapResult.extensionId))
/*      */         {
/* 1519 */           forceClose(this.pooled);
/*      */         }
/*      */ 
/* 1522 */         if (this.unsolicited.size() > 0)
/*      */         {
/* 1526 */           localObject1 = new UnsolicitedResponseImpl(localLdapResult.extensionId, localLdapResult.extensionValue, localLdapResult.referrals, localLdapResult.status, localLdapResult.errorMessage, localLdapResult.matchedDN, localLdapResult.resControls != null ? ((LdapCtx)this.unsolicited.elementAt(0)).convertControls(localLdapResult.resControls) : null);
/*      */ 
/* 1538 */           notifyUnsolicited(localObject1);
/*      */ 
/* 1542 */           if ("1.3.6.1.4.1.1466.20036".equals(localLdapResult.extensionId))
/* 1543 */             notifyUnsolicited(new CommunicationException("Connection closed"));
/*      */         }
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/* 1548 */         if (this.unsolicited.size() == 0) {
/* 1549 */           return;
/*      */         }
/* 1551 */         Object localObject1 = new CommunicationException("Problem parsing unsolicited notification");
/*      */ 
/* 1553 */         ((NamingException)localObject1).setRootCause(localIOException);
/*      */ 
/* 1555 */         notifyUnsolicited(localObject1);
/*      */       }
/*      */       catch (NamingException localNamingException) {
/* 1558 */         notifyUnsolicited(localNamingException);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyUnsolicited(Object paramObject)
/*      */   {
/* 1565 */     for (int i = 0; i < this.unsolicited.size(); i++) {
/* 1566 */       ((LdapCtx)this.unsolicited.elementAt(i)).fireUnsolicited(paramObject);
/*      */     }
/* 1568 */     if ((paramObject instanceof NamingException))
/* 1569 */       this.unsolicited.setSize(0);
/*      */   }
/*      */ 
/*      */   private void ensureOpen() throws IOException
/*      */   {
/* 1574 */     if ((this.conn == null) || (!this.conn.useable)) {
/* 1575 */       if ((this.conn != null) && (this.conn.closureReason != null)) {
/* 1576 */         throw this.conn.closureReason;
/*      */       }
/* 1578 */       throw new IOException("connection closed");
/*      */     }
/*      */   }
/*      */ 
/*      */   static LdapClient getInstance(boolean paramBoolean, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, int paramInt4, String paramString3, Control[] paramArrayOfControl, String paramString4, String paramString5, Object paramObject, Hashtable paramHashtable)
/*      */     throws NamingException
/*      */   {
/* 1589 */     if ((paramBoolean) && 
/* 1590 */       (LdapPoolManager.isPoolingAllowed(paramString2, paramOutputStream, paramString3, paramString4, paramHashtable)))
/*      */     {
/* 1592 */       LdapClient localLdapClient = LdapPoolManager.getLdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, paramInt4, paramString3, paramArrayOfControl, paramString4, paramString5, paramObject, paramHashtable);
/*      */ 
/* 1596 */       localLdapClient.referenceCount = 1;
/* 1597 */       return localLdapClient;
/*      */     }
/*      */ 
/* 1600 */     return new LdapClient(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream, null);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   86 */     defaultBinaryAttrs.put("userpassword", Boolean.TRUE);
/*   87 */     defaultBinaryAttrs.put("javaserializeddata", Boolean.TRUE);
/*      */ 
/*   89 */     defaultBinaryAttrs.put("javaserializedobject", Boolean.TRUE);
/*      */ 
/*   91 */     defaultBinaryAttrs.put("jpegphoto", Boolean.TRUE);
/*      */ 
/*   93 */     defaultBinaryAttrs.put("audio", Boolean.TRUE);
/*   94 */     defaultBinaryAttrs.put("thumbnailphoto", Boolean.TRUE);
/*      */ 
/*   96 */     defaultBinaryAttrs.put("thumbnaillogo", Boolean.TRUE);
/*      */ 
/*   98 */     defaultBinaryAttrs.put("usercertificate", Boolean.TRUE);
/*   99 */     defaultBinaryAttrs.put("cacertificate", Boolean.TRUE);
/*  100 */     defaultBinaryAttrs.put("certificaterevocationlist", Boolean.TRUE);
/*      */ 
/*  102 */     defaultBinaryAttrs.put("authorityrevocationlist", Boolean.TRUE);
/*  103 */     defaultBinaryAttrs.put("crosscertificatepair", Boolean.TRUE);
/*  104 */     defaultBinaryAttrs.put("photo", Boolean.TRUE);
/*  105 */     defaultBinaryAttrs.put("personalsignature", Boolean.TRUE);
/*      */ 
/*  107 */     defaultBinaryAttrs.put("x500uniqueidentifier", Boolean.TRUE);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.jndi.ldap.LdapClient
 * JD-Core Version:    0.6.2
 */