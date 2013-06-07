/*    */ package sun.java2d.xr;
/*    */ 
/*    */ public class MutableInteger
/*    */ {
/*    */   private int value;
/*    */ 
/*    */   public MutableInteger(int paramInt)
/*    */   {
/* 38 */     setValue(paramInt);
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 42 */     return getValue();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object paramObject) {
/* 46 */     return ((paramObject instanceof MutableInteger)) && (((MutableInteger)paramObject).getValue() == getValue());
/*    */   }
/*    */ 
/*    */   public void setValue(int paramInt)
/*    */   {
/* 51 */     this.value = paramInt;
/*    */   }
/*    */ 
/*    */   public int getValue() {
/* 55 */     return this.value;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.java2d.xr.MutableInteger
 * JD-Core Version:    0.6.2
 */