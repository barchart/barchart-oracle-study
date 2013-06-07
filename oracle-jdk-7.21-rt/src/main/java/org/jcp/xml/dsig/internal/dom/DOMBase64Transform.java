/*    */ package org.jcp.xml.dsig.internal.dom;
/*    */ 
/*    */ import java.security.InvalidAlgorithmParameterException;
/*    */ import javax.xml.crypto.dsig.spec.TransformParameterSpec;
/*    */ 
/*    */ public final class DOMBase64Transform extends ApacheTransform
/*    */ {
/*    */   public void init(TransformParameterSpec paramTransformParameterSpec)
/*    */     throws InvalidAlgorithmParameterException
/*    */   {
/* 44 */     if (paramTransformParameterSpec != null)
/* 45 */       throw new InvalidAlgorithmParameterException("params must be null");
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMBase64Transform
 * JD-Core Version:    0.6.2
 */