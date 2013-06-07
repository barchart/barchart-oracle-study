/*     */ package javax.naming.spi;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attributes;
/*     */ 
/*     */ public abstract interface DirStateFactory extends StateFactory
/*     */ {
/*     */   public abstract Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
/*     */     throws NamingException;
/*     */ 
/*     */   public static class Result
/*     */   {
/*     */     private Object obj;
/*     */     private Attributes attrs;
/*     */ 
/*     */     public Result(Object paramObject, Attributes paramAttributes)
/*     */     {
/* 171 */       this.obj = paramObject;
/* 172 */       this.attrs = paramAttributes;
/*     */     }
/*     */ 
/*     */     public Object getObject()
/*     */     {
/* 179 */       return this.obj;
/*     */     }
/*     */ 
/*     */     public Attributes getAttributes()
/*     */     {
/* 185 */       return this.attrs;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.naming.spi.DirStateFactory
 * JD-Core Version:    0.6.2
 */