/*    */ package sun.security.provider.certpath;
/*    */ 
/*    */ import java.security.cert.X509CRLSelector;
/*    */ import java.security.cert.X509CertSelector;
/*    */ import java.util.Date;
/*    */ import java.util.Set;
/*    */ import sun.security.x509.GeneralNameInterface;
/*    */ 
/*    */ public abstract class CertPathHelper
/*    */ {
/*    */   protected static CertPathHelper instance;
/*    */ 
/*    */   protected abstract void implSetPathToNames(X509CertSelector paramX509CertSelector, Set<GeneralNameInterface> paramSet);
/*    */ 
/*    */   protected abstract void implSetDateAndTime(X509CRLSelector paramX509CRLSelector, Date paramDate, long paramLong);
/*    */ 
/*    */   static void setPathToNames(X509CertSelector paramX509CertSelector, Set<GeneralNameInterface> paramSet)
/*    */   {
/* 64 */     instance.implSetPathToNames(paramX509CertSelector, paramSet);
/*    */   }
/*    */ 
/*    */   static void setDateAndTime(X509CRLSelector paramX509CRLSelector, Date paramDate, long paramLong) {
/* 68 */     instance.implSetDateAndTime(paramX509CRLSelector, paramDate, paramLong);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.security.provider.certpath.CertPathHelper
 * JD-Core Version:    0.6.2
 */