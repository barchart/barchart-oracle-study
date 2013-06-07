/*    */ package sun.print;
/*    */ 
/*    */ import java.awt.Frame;
/*    */ import javax.print.attribute.PrintRequestAttribute;
/*    */ 
/*    */ public final class DialogOwner
/*    */   implements PrintRequestAttribute
/*    */ {
/*    */   private Frame dlgOwner;
/*    */ 
/*    */   public DialogOwner(Frame paramFrame)
/*    */   {
/* 52 */     this.dlgOwner = paramFrame;
/*    */   }
/*    */ 
/*    */   public Frame getOwner()
/*    */   {
/* 60 */     return this.dlgOwner;
/*    */   }
/*    */ 
/*    */   public final Class getCategory()
/*    */   {
/* 75 */     return DialogOwner.class;
/*    */   }
/*    */ 
/*    */   public final String getName()
/*    */   {
/* 89 */     return "dialog-owner";
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.DialogOwner
 * JD-Core Version:    0.6.2
 */