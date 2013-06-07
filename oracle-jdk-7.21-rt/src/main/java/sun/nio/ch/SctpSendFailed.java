/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.Association;
/*     */ import com.sun.nio.sctp.SendFailedNotification;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ public class SctpSendFailed extends SendFailedNotification
/*     */   implements SctpNotification
/*     */ {
/*     */   private Association association;
/*     */   private int assocId;
/*     */   private SocketAddress address;
/*     */   private ByteBuffer buffer;
/*     */   private int errorCode;
/*     */   private int streamNumber;
/*     */ 
/*     */   private SctpSendFailed(int paramInt1, SocketAddress paramSocketAddress, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3)
/*     */   {
/*  53 */     this.assocId = paramInt1;
/*  54 */     this.errorCode = paramInt2;
/*  55 */     this.streamNumber = paramInt3;
/*  56 */     this.address = paramSocketAddress;
/*  57 */     this.buffer = paramByteBuffer;
/*     */   }
/*     */ 
/*     */   public int assocId()
/*     */   {
/*  62 */     return this.assocId;
/*     */   }
/*     */ 
/*     */   public void setAssociation(Association paramAssociation)
/*     */   {
/*  67 */     this.association = paramAssociation;
/*     */   }
/*     */ 
/*     */   public Association association()
/*     */   {
/*  73 */     return this.association;
/*     */   }
/*     */ 
/*     */   public SocketAddress address()
/*     */   {
/*  78 */     assert (this.address != null);
/*  79 */     return this.address;
/*     */   }
/*     */ 
/*     */   public ByteBuffer buffer()
/*     */   {
/*  84 */     assert (this.buffer != null);
/*  85 */     return this.buffer;
/*     */   }
/*     */ 
/*     */   public int errorCode()
/*     */   {
/*  90 */     return this.errorCode;
/*     */   }
/*     */ 
/*     */   public int streamNumber()
/*     */   {
/*  95 */     return this.streamNumber;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 100 */     StringBuilder localStringBuilder = new StringBuilder();
/* 101 */     localStringBuilder.append(super.toString()).append(" [");
/* 102 */     localStringBuilder.append("Association:").append(this.association);
/* 103 */     localStringBuilder.append(", Address: ").append(this.address);
/* 104 */     localStringBuilder.append(", buffer: ").append(this.buffer);
/* 105 */     localStringBuilder.append(", errorCode: ").append(this.errorCode);
/* 106 */     localStringBuilder.append(", streamNumber: ").append(this.streamNumber);
/* 107 */     localStringBuilder.append("]");
/* 108 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpSendFailed
 * JD-Core Version:    0.6.2
 */