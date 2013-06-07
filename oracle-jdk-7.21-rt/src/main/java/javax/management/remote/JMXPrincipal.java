/*     */ package javax.management.remote;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.security.Principal;
/*     */ 
/*     */ public class JMXPrincipal
/*     */   implements Principal, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4184480100214577411L;
/*     */   private String name;
/*     */ 
/*     */   public JMXPrincipal(String paramString)
/*     */   {
/*  67 */     if (paramString == null) {
/*  68 */       throw new NullPointerException("illegal null input");
/*     */     }
/*  70 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  81 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  92 */     return "JMXPrincipal:  " + this.name;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 110 */     if (paramObject == null) {
/* 111 */       return false;
/*     */     }
/* 113 */     if (this == paramObject) {
/* 114 */       return true;
/*     */     }
/* 116 */     if (!(paramObject instanceof JMXPrincipal))
/* 117 */       return false;
/* 118 */     JMXPrincipal localJMXPrincipal = (JMXPrincipal)paramObject;
/*     */ 
/* 120 */     return getName().equals(localJMXPrincipal.getName());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 131 */     return this.name.hashCode();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.JMXPrincipal
 * JD-Core Version:    0.6.2
 */