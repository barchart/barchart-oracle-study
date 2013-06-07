/*    */ package javax.net.ssl;
/*    */ 
/*    */ import java.security.cert.CertPathParameters;
/*    */ 
/*    */ public class CertPathTrustManagerParameters
/*    */   implements ManagerFactoryParameters
/*    */ {
/*    */   private final CertPathParameters parameters;
/*    */ 
/*    */   public CertPathTrustManagerParameters(CertPathParameters paramCertPathParameters)
/*    */   {
/* 59 */     this.parameters = ((CertPathParameters)paramCertPathParameters.clone());
/*    */   }
/*    */ 
/*    */   public CertPathParameters getParameters()
/*    */   {
/* 68 */     return (CertPathParameters)this.parameters.clone();
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.net.ssl.CertPathTrustManagerParameters
 * JD-Core Version:    0.6.2
 */