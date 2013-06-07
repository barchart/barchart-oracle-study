/*     */ package javax.management.remote;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import javax.management.Notification;
/*     */ 
/*     */ public class TargetedNotification
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 7676132089779300926L;
/*     */   private final Notification notif;
/*     */   private final Integer id;
/*     */ 
/*     */   public TargetedNotification(Notification paramNotification, Integer paramInteger)
/*     */   {
/*  78 */     if (paramNotification == null) throw new IllegalArgumentException("Invalid notification: null");
/*     */ 
/*  80 */     if (paramInteger == null) throw new IllegalArgumentException("Invalid listener ID: null");
/*     */ 
/*  82 */     this.notif = paramNotification;
/*  83 */     this.id = paramInteger;
/*     */   }
/*     */ 
/*     */   public Notification getNotification()
/*     */   {
/*  92 */     return this.notif;
/*     */   }
/*     */ 
/*     */   public Integer getListenerID()
/*     */   {
/* 102 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 111 */     return "{" + this.notif + ", " + this.id + "}";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.TargetedNotification
 * JD-Core Version:    0.6.2
 */