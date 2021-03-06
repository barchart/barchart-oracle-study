/*     */ package org.ietf.jgss;
/*     */ 
/*     */ import java.security.Provider;
/*     */ import sun.security.jgss.GSSManagerImpl;
/*     */ 
/*     */ public abstract class GSSManager
/*     */ {
/*     */   public static GSSManager getInstance()
/*     */   {
/* 147 */     return new GSSManagerImpl();
/*     */   }
/*     */ 
/*     */   public abstract Oid[] getMechs();
/*     */ 
/*     */   public abstract Oid[] getNamesForMech(Oid paramOid)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract Oid[] getMechsForName(Oid paramOid);
/*     */ 
/*     */   public abstract GSSName createName(String paramString, Oid paramOid)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSName createName(byte[] paramArrayOfByte, Oid paramOid)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSName createName(String paramString, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSName createName(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSCredential createCredential(int paramInt)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSContext createContext(GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSContext createContext(GSSCredential paramGSSCredential)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract GSSContext createContext(byte[] paramArrayOfByte)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract void addProviderAtFront(Provider paramProvider, Oid paramOid)
/*     */     throws GSSException;
/*     */ 
/*     */   public abstract void addProviderAtEnd(Provider paramProvider, Oid paramOid)
/*     */     throws GSSException;
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.ietf.jgss.GSSManager
 * JD-Core Version:    0.6.2
 */