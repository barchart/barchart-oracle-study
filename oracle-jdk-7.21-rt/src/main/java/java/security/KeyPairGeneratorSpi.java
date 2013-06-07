/*    */ package java.security;
/*    */ 
/*    */ import java.security.spec.AlgorithmParameterSpec;
/*    */ 
/*    */ public abstract class KeyPairGeneratorSpi
/*    */ {
/*    */   public abstract void initialize(int paramInt, SecureRandom paramSecureRandom);
/*    */ 
/*    */   public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
/*    */     throws InvalidAlgorithmParameterException
/*    */   {
/* 94 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public abstract KeyPair generateKeyPair();
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.security.KeyPairGeneratorSpi
 * JD-Core Version:    0.6.2
 */