/*    */ package sun.misc;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public abstract class ClassFileTransformer
/*    */ {
/* 49 */   private static ArrayList<ClassFileTransformer> transformerList = new ArrayList();
/*    */ 
/* 51 */   private static ClassFileTransformer[] transformers = new ClassFileTransformer[0];
/*    */ 
/*    */   public static void add(ClassFileTransformer paramClassFileTransformer)
/*    */   {
/* 61 */     synchronized (transformerList)
/*    */     {
/* 63 */       transformerList.add(paramClassFileTransformer);
/* 64 */       transformers = (ClassFileTransformer[])transformerList.toArray(new ClassFileTransformer[0]);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static ClassFileTransformer[] getTransformers()
/*    */   {
/* 79 */     return transformers;
/*    */   }
/*    */ 
/*    */   public abstract byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*    */     throws ClassFormatError;
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.misc.ClassFileTransformer
 * JD-Core Version:    0.6.2
 */