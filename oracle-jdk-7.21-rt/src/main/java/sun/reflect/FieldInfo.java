/*    */ package sun.reflect;
/*    */ 
/*    */ import java.lang.reflect.Modifier;
/*    */ 
/*    */ public class FieldInfo
/*    */ {
/*    */   private String name;
/*    */   private String signature;
/*    */   private int modifiers;
/*    */   private int slot;
/*    */ 
/*    */   public String name()
/*    */   {
/* 50 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String signature()
/*    */   {
/* 56 */     return this.signature;
/*    */   }
/*    */ 
/*    */   public int modifiers() {
/* 60 */     return this.modifiers;
/*    */   }
/*    */ 
/*    */   public int slot() {
/* 64 */     return this.slot;
/*    */   }
/*    */ 
/*    */   public boolean isPublic()
/*    */   {
/* 69 */     return Modifier.isPublic(modifiers());
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.FieldInfo
 * JD-Core Version:    0.6.2
 */