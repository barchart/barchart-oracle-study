/*     */ package com.sun.org.apache.xerces.internal.dom;
/*     */ 
/*     */ public class DeferredCDATASectionImpl extends CDATASectionImpl
/*     */   implements DeferredNode
/*     */ {
/*     */   static final long serialVersionUID = 1983580632355645726L;
/*     */   protected transient int fNodeIndex;
/*     */ 
/*     */   DeferredCDATASectionImpl(DeferredDocumentImpl ownerDocument, int nodeIndex)
/*     */   {
/*  76 */     super(ownerDocument, null);
/*     */ 
/*  78 */     this.fNodeIndex = nodeIndex;
/*  79 */     needsSyncData(true);
/*     */   }
/*     */ 
/*     */   public int getNodeIndex()
/*     */   {
/*  89 */     return this.fNodeIndex;
/*     */   }
/*     */ 
/*     */   protected void synchronizeData()
/*     */   {
/* 100 */     needsSyncData(false);
/*     */ 
/* 103 */     DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)ownerDocument();
/*     */ 
/* 105 */     this.data = ownerDocument.getNodeValueString(this.fNodeIndex);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.dom.DeferredCDATASectionImpl
 * JD-Core Version:    0.6.2
 */