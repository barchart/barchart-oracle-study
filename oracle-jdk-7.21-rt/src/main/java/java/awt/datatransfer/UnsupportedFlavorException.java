/*    */ package java.awt.datatransfer;
/*    */ 
/*    */ public class UnsupportedFlavorException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 5383814944251665601L;
/*    */ 
/*    */   public UnsupportedFlavorException(DataFlavor paramDataFlavor)
/*    */   {
/* 48 */     super(paramDataFlavor != null ? paramDataFlavor.getHumanPresentableName() : null);
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.awt.datatransfer.UnsupportedFlavorException
 * JD-Core Version:    0.6.2
 */