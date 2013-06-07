/*     */ package javax.print.attribute;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class HashPrintServiceAttributeSet extends HashAttributeSet
/*     */   implements PrintServiceAttributeSet, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6642904616179203070L;
/*     */ 
/*     */   public HashPrintServiceAttributeSet()
/*     */   {
/*  48 */     super(PrintServiceAttribute.class);
/*     */   }
/*     */ 
/*     */   public HashPrintServiceAttributeSet(PrintServiceAttribute paramPrintServiceAttribute)
/*     */   {
/*  62 */     super(paramPrintServiceAttribute, PrintServiceAttribute.class);
/*     */   }
/*     */ 
/*     */   public HashPrintServiceAttributeSet(PrintServiceAttribute[] paramArrayOfPrintServiceAttribute)
/*     */   {
/*  81 */     super(paramArrayOfPrintServiceAttribute, PrintServiceAttribute.class);
/*     */   }
/*     */ 
/*     */   public HashPrintServiceAttributeSet(PrintServiceAttributeSet paramPrintServiceAttributeSet)
/*     */   {
/* 100 */     super(paramPrintServiceAttributeSet, PrintServiceAttribute.class);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.attribute.HashPrintServiceAttributeSet
 * JD-Core Version:    0.6.2
 */