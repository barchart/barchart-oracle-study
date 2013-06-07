/*     */ package com.sun.security.sasl.gsskerb;
/*     */ 
/*     */ import com.sun.security.sasl.util.AbstractSaslImpl;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.security.sasl.SaslException;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.MessageProp;
/*     */ import org.ietf.jgss.Oid;
/*     */ 
/*     */ abstract class GssKrb5Base extends AbstractSaslImpl
/*     */ {
/*     */   private static final String KRB5_OID_STR = "1.2.840.113554.1.2.2";
/*     */   protected static Oid KRB5_OID;
/*  41 */   protected static final byte[] EMPTY = new byte[0];
/*     */ 
/*  49 */   protected GSSContext secCtx = null;
/*     */   protected MessageProp msgProp;
/*     */   protected static final int JGSS_QOP = 0;
/*     */ 
/*     */   protected GssKrb5Base(Map paramMap, String paramString)
/*     */     throws SaslException
/*     */   {
/*  54 */     super(paramMap, paramString);
/*     */   }
/*     */ 
/*     */   public String getMechanismName()
/*     */   {
/*  63 */     return "GSSAPI";
/*     */   }
/*     */ 
/*     */   public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException
/*     */   {
/*  68 */     if (!this.completed) {
/*  69 */       throw new IllegalStateException("GSSAPI authentication not completed");
/*     */     }
/*     */ 
/*  73 */     if (!this.integrity) {
/*  74 */       throw new IllegalStateException("No security layer negotiated");
/*     */     }
/*     */     try
/*     */     {
/*  78 */       byte[] arrayOfByte = this.secCtx.unwrap(paramArrayOfByte, paramInt1, paramInt2, this.msgProp);
/*  79 */       if (logger.isLoggable(Level.FINEST)) {
/*  80 */         traceOutput(this.myClassName, "KRB501:Unwrap", "incoming: ", paramArrayOfByte, paramInt1, paramInt2);
/*     */ 
/*  82 */         traceOutput(this.myClassName, "KRB502:Unwrap", "unwrapped: ", arrayOfByte, 0, arrayOfByte.length);
/*     */       }
/*     */ 
/*  85 */       return arrayOfByte;
/*     */     } catch (GSSException localGSSException) {
/*  87 */       throw new SaslException("Problems unwrapping SASL buffer", localGSSException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
/*  92 */     if (!this.completed) {
/*  93 */       throw new IllegalStateException("GSSAPI authentication not completed");
/*     */     }
/*     */ 
/*  97 */     if (!this.integrity) {
/*  98 */       throw new IllegalStateException("No security layer negotiated");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 103 */       byte[] arrayOfByte = this.secCtx.wrap(paramArrayOfByte, paramInt1, paramInt2, this.msgProp);
/* 104 */       if (logger.isLoggable(Level.FINEST)) {
/* 105 */         traceOutput(this.myClassName, "KRB503:Wrap", "outgoing: ", paramArrayOfByte, paramInt1, paramInt2);
/*     */ 
/* 107 */         traceOutput(this.myClassName, "KRB504:Wrap", "wrapped: ", arrayOfByte, 0, arrayOfByte.length);
/*     */       }
/*     */ 
/* 110 */       return arrayOfByte;
/*     */     }
/*     */     catch (GSSException localGSSException) {
/* 113 */       throw new SaslException("Problem performing GSS wrap", localGSSException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose() throws SaslException {
/* 118 */     if (this.secCtx != null) {
/*     */       try {
/* 120 */         this.secCtx.dispose();
/*     */       } catch (GSSException localGSSException) {
/* 122 */         throw new SaslException("Problem disposing GSS context", localGSSException);
/*     */       }
/* 124 */       this.secCtx = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable {
/* 129 */     dispose();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  45 */       KRB5_OID = new Oid("1.2.840.113554.1.2.2");
/*     */     }
/*     */     catch (GSSException localGSSException)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.security.sasl.gsskerb.GssKrb5Base
 * JD-Core Version:    0.6.2
 */