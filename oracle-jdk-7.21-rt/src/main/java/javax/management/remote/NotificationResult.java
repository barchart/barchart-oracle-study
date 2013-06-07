/*     */ package javax.management.remote;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class NotificationResult
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1191800228721395279L;
/*     */   private final long earliestSequenceNumber;
/*     */   private final long nextSequenceNumber;
/*     */   private final TargetedNotification[] targetedNotifications;
/*     */ 
/*     */   public NotificationResult(long paramLong1, long paramLong2, TargetedNotification[] paramArrayOfTargetedNotification)
/*     */   {
/*  79 */     if (paramArrayOfTargetedNotification == null)
/*     */     {
/*  81 */       throw new IllegalArgumentException("Notifications null");
/*     */     }
/*     */ 
/*  84 */     if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
/*  85 */       throw new IllegalArgumentException("Bad sequence numbers");
/*     */     }
/*     */ 
/*  90 */     this.earliestSequenceNumber = paramLong1;
/*  91 */     this.nextSequenceNumber = paramLong2;
/*  92 */     this.targetedNotifications = paramArrayOfTargetedNotification;
/*     */   }
/*     */ 
/*     */   public long getEarliestSequenceNumber()
/*     */   {
/* 103 */     return this.earliestSequenceNumber;
/*     */   }
/*     */ 
/*     */   public long getNextSequenceNumber()
/*     */   {
/* 114 */     return this.nextSequenceNumber;
/*     */   }
/*     */ 
/*     */   public TargetedNotification[] getTargetedNotifications()
/*     */   {
/* 125 */     return this.targetedNotifications;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 136 */     return "NotificationResult: earliest=" + getEarliestSequenceNumber() + "; next=" + getNextSequenceNumber() + "; nnotifs=" + getTargetedNotifications().length;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.remote.NotificationResult
 * JD-Core Version:    0.6.2
 */