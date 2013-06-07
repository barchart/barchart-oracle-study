/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.Association;
/*     */ import com.sun.nio.sctp.AssociationChangeNotification;
/*     */ import com.sun.nio.sctp.AssociationChangeNotification.AssocChangeEvent;
/*     */ 
/*     */ public class SctpAssocChange extends AssociationChangeNotification
/*     */   implements SctpNotification
/*     */ {
/*     */   private static final int SCTP_COMM_UP = 1;
/*     */   private static final int SCTP_COMM_LOST = 2;
/*     */   private static final int SCTP_RESTART = 3;
/*     */   private static final int SCTP_SHUTDOWN = 4;
/*     */   private static final int SCTP_CANT_START = 5;
/*     */   private Association association;
/*     */   private int assocId;
/*     */   private AssociationChangeNotification.AssocChangeEvent event;
/*     */   private int maxOutStreams;
/*     */   private int maxInStreams;
/*     */ 
/*     */   private SctpAssocChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/*  57 */     switch (paramInt2) {
/*     */     case 1:
/*  59 */       this.event = AssociationChangeNotification.AssocChangeEvent.COMM_UP;
/*  60 */       break;
/*     */     case 2:
/*  62 */       this.event = AssociationChangeNotification.AssocChangeEvent.COMM_LOST;
/*  63 */       break;
/*     */     case 3:
/*  65 */       this.event = AssociationChangeNotification.AssocChangeEvent.RESTART;
/*  66 */       break;
/*     */     case 4:
/*  68 */       this.event = AssociationChangeNotification.AssocChangeEvent.SHUTDOWN;
/*  69 */       break;
/*     */     case 5:
/*  71 */       this.event = AssociationChangeNotification.AssocChangeEvent.CANT_START;
/*  72 */       break;
/*     */     default:
/*  74 */       throw new AssertionError("Unknown Association Change Event type: " + paramInt2);
/*     */     }
/*     */ 
/*  78 */     this.assocId = paramInt1;
/*  79 */     this.maxOutStreams = paramInt3;
/*  80 */     this.maxInStreams = paramInt4;
/*     */   }
/*     */ 
/*     */   public int assocId()
/*     */   {
/*  85 */     return this.assocId;
/*     */   }
/*     */ 
/*     */   public void setAssociation(Association paramAssociation)
/*     */   {
/*  90 */     this.association = paramAssociation;
/*     */   }
/*     */ 
/*     */   public Association association()
/*     */   {
/*  95 */     assert (this.association != null);
/*  96 */     return this.association;
/*     */   }
/*     */ 
/*     */   public AssociationChangeNotification.AssocChangeEvent event()
/*     */   {
/* 101 */     return this.event;
/*     */   }
/*     */ 
/*     */   int maxOutStreams() {
/* 105 */     return this.maxOutStreams;
/*     */   }
/*     */ 
/*     */   int maxInStreams() {
/* 109 */     return this.maxInStreams;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 114 */     StringBuilder localStringBuilder = new StringBuilder();
/* 115 */     localStringBuilder.append(super.toString()).append(" [");
/* 116 */     localStringBuilder.append("Association:").append(this.association);
/* 117 */     localStringBuilder.append(", Event: ").append(this.event).append("]");
/* 118 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpAssocChange
 * JD-Core Version:    0.6.2
 */