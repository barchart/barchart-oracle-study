/*    */ package javax.print.attribute;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class HashDocAttributeSet extends HashAttributeSet
/*    */   implements DocAttributeSet, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -1128534486061432528L;
/*    */ 
/*    */   public HashDocAttributeSet()
/*    */   {
/* 49 */     super(DocAttribute.class);
/*    */   }
/*    */ 
/*    */   public HashDocAttributeSet(DocAttribute paramDocAttribute)
/*    */   {
/* 62 */     super(paramDocAttribute, DocAttribute.class);
/*    */   }
/*    */ 
/*    */   public HashDocAttributeSet(DocAttribute[] paramArrayOfDocAttribute)
/*    */   {
/* 82 */     super(paramArrayOfDocAttribute, DocAttribute.class);
/*    */   }
/*    */ 
/*    */   public HashDocAttributeSet(DocAttributeSet paramDocAttributeSet)
/*    */   {
/* 99 */     super(paramDocAttributeSet, DocAttribute.class);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.print.attribute.HashDocAttributeSet
 * JD-Core Version:    0.6.2
 */