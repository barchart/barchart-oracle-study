/*    */ package sun.print;
/*    */ 
/*    */ import javax.print.DocFlavor;
/*    */ import javax.print.FlavorException;
/*    */ import javax.print.PrintException;
/*    */ 
/*    */ class PrintJobFlavorException extends PrintException
/*    */   implements FlavorException
/*    */ {
/*    */   private DocFlavor flavor;
/*    */ 
/*    */   PrintJobFlavorException(String paramString, DocFlavor paramDocFlavor)
/*    */   {
/* 39 */     super(paramString);
/* 40 */     this.flavor = paramDocFlavor;
/*    */   }
/*    */ 
/*    */   public DocFlavor[] getUnsupportedFlavors() {
/* 44 */     DocFlavor[] arrayOfDocFlavor = { this.flavor };
/* 45 */     return arrayOfDocFlavor;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.print.PrintJobFlavorException
 * JD-Core Version:    0.6.2
 */