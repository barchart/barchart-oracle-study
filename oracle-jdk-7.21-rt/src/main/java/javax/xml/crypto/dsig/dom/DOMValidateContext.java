/*     */ package javax.xml.crypto.dsig.dom;
/*     */ 
/*     */ import java.security.Key;
/*     */ import javax.xml.crypto.KeySelector;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.XMLValidateContext;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class DOMValidateContext extends DOMCryptoContext
/*     */   implements XMLValidateContext
/*     */ {
/*     */   private Node node;
/*     */ 
/*     */   public DOMValidateContext(KeySelector paramKeySelector, Node paramNode)
/*     */   {
/*  74 */     if (paramKeySelector == null) {
/*  75 */       throw new NullPointerException("key selector is null");
/*     */     }
/*  77 */     if (paramNode == null) {
/*  78 */       throw new NullPointerException("node is null");
/*     */     }
/*  80 */     setKeySelector(paramKeySelector);
/*  81 */     this.node = paramNode;
/*     */   }
/*     */ 
/*     */   public DOMValidateContext(Key paramKey, Node paramNode)
/*     */   {
/*  97 */     if (paramKey == null) {
/*  98 */       throw new NullPointerException("validatingKey is null");
/*     */     }
/* 100 */     if (paramNode == null) {
/* 101 */       throw new NullPointerException("node is null");
/*     */     }
/* 103 */     setKeySelector(KeySelector.singletonKeySelector(paramKey));
/* 104 */     this.node = paramNode;
/*     */   }
/*     */ 
/*     */   public void setNode(Node paramNode)
/*     */   {
/* 115 */     if (paramNode == null) {
/* 116 */       throw new NullPointerException();
/*     */     }
/* 118 */     this.node = paramNode;
/*     */   }
/*     */ 
/*     */   public Node getNode()
/*     */   {
/* 128 */     return this.node;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.xml.crypto.dsig.dom.DOMValidateContext
 * JD-Core Version:    0.6.2
 */