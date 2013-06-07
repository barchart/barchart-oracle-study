/*    */ package sun.text;
/*    */ 
/*    */ import sun.text.normalizer.NormalizerBase;
/*    */ import sun.text.normalizer.NormalizerBase.Mode;
/*    */ 
/*    */ public class CollatorUtilities
/*    */ {
/* 59 */   static NormalizerBase.Mode[] legacyModeMap = { NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD };
/*    */ 
/*    */   public static int toLegacyMode(NormalizerBase.Mode paramMode)
/*    */   {
/* 35 */     int i = legacyModeMap.length;
/* 36 */     while (i > 0) {
/* 37 */       i--;
/* 38 */       if (legacyModeMap[i] == paramMode) {
/* 39 */         break;
/*    */       }
/*    */     }
/* 42 */     return i;
/*    */   }
/*    */ 
/*    */   public static NormalizerBase.Mode toNormalizerMode(int paramInt)
/*    */   {
/*    */     NormalizerBase.Mode localMode;
/*    */     try {
/* 49 */       localMode = legacyModeMap[paramInt];
/*    */     }
/*    */     catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 52 */       localMode = NormalizerBase.NONE;
/*    */     }
/* 54 */     return localMode;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.text.CollatorUtilities
 * JD-Core Version:    0.6.2
 */