/*     */ package sun.nio.ch;
/*     */ 
/*     */ public class SctpResultContainer
/*     */ {
/*     */   static final int NOTHING = 0;
/*     */   static final int MESSAGE = 1;
/*     */   static final int SEND_FAILED = 2;
/*     */   static final int ASSOCIATION_CHANGED = 3;
/*     */   static final int PEER_ADDRESS_CHANGED = 4;
/*     */   static final int SHUTDOWN = 5;
/*     */   private Object value;
/*     */   private int type;
/*     */ 
/*     */   int type()
/*     */   {
/*  44 */     return this.type;
/*     */   }
/*     */ 
/*     */   boolean hasSomething() {
/*  48 */     return type() != 0;
/*     */   }
/*     */ 
/*     */   boolean isNotification() {
/*  52 */     return (type() != 1) && (type() != 0);
/*     */   }
/*     */ 
/*     */   void clear() {
/*  56 */     this.type = 0;
/*  57 */     this.value = null;
/*     */   }
/*     */ 
/*     */   SctpNotification notification() {
/*  61 */     assert ((type() != 1) && (type() != 0));
/*     */ 
/*  63 */     return (SctpNotification)this.value;
/*     */   }
/*     */ 
/*     */   SctpMessageInfoImpl getMessageInfo() {
/*  67 */     assert (type() == 1);
/*     */ 
/*  69 */     if ((this.value instanceof SctpMessageInfoImpl)) {
/*  70 */       return (SctpMessageInfoImpl)this.value;
/*     */     }
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   SctpSendFailed getSendFailed() {
/*  76 */     assert (type() == 2);
/*     */ 
/*  78 */     if ((this.value instanceof SctpSendFailed)) {
/*  79 */       return (SctpSendFailed)this.value;
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   SctpAssocChange getAssociationChanged() {
/*  85 */     assert (type() == 3);
/*     */ 
/*  87 */     if ((this.value instanceof SctpAssocChange)) {
/*  88 */       return (SctpAssocChange)this.value;
/*     */     }
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   SctpPeerAddrChange getPeerAddressChanged() {
/*  94 */     assert (type() == 4);
/*     */ 
/*  96 */     if ((this.value instanceof SctpPeerAddrChange)) {
/*  97 */       return (SctpPeerAddrChange)this.value;
/*     */     }
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   SctpShutdown getShutdown() {
/* 103 */     assert (type() == 5);
/*     */ 
/* 105 */     if ((this.value instanceof SctpShutdown)) {
/* 106 */       return (SctpShutdown)this.value;
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 113 */     StringBuilder localStringBuilder = new StringBuilder();
/* 114 */     localStringBuilder.append("Type: ");
/* 115 */     switch (this.type) { case 0:
/* 116 */       localStringBuilder.append("NOTHING"); break;
/*     */     case 1:
/* 117 */       localStringBuilder.append("MESSAGE"); break;
/*     */     case 2:
/* 118 */       localStringBuilder.append("SEND FAILED"); break;
/*     */     case 3:
/* 119 */       localStringBuilder.append("ASSOCIATION CHANGE"); break;
/*     */     case 4:
/* 120 */       localStringBuilder.append("PEER ADDRESS CHANGE"); break;
/*     */     case 5:
/* 121 */       localStringBuilder.append("SHUTDOWN"); break;
/*     */     default:
/* 122 */       localStringBuilder.append("Unknown result type");
/*     */     }
/* 124 */     localStringBuilder.append(", Value: ");
/* 125 */     localStringBuilder.append(this.value == null ? "null" : this.value.toString());
/* 126 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpResultContainer
 * JD-Core Version:    0.6.2
 */