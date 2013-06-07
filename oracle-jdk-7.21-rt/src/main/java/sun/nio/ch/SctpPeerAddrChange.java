/*     */ package sun.nio.ch;
/*     */ 
/*     */ import com.sun.nio.sctp.Association;
/*     */ import com.sun.nio.sctp.PeerAddressChangeNotification;
/*     */ import com.sun.nio.sctp.PeerAddressChangeNotification.AddressChangeEvent;
/*     */ import java.net.SocketAddress;
/*     */ 
/*     */ public class SctpPeerAddrChange extends PeerAddressChangeNotification
/*     */   implements SctpNotification
/*     */ {
/*     */   private static final int SCTP_ADDR_AVAILABLE = 1;
/*     */   private static final int SCTP_ADDR_UNREACHABLE = 2;
/*     */   private static final int SCTP_ADDR_REMOVED = 3;
/*     */   private static final int SCTP_ADDR_ADDED = 4;
/*     */   private static final int SCTP_ADDR_MADE_PRIM = 5;
/*     */   private static final int SCTP_ADDR_CONFIRMED = 6;
/*     */   private Association association;
/*     */   private int assocId;
/*     */   private SocketAddress address;
/*     */   private PeerAddressChangeNotification.AddressChangeEvent event;
/*     */ 
/*     */   private SctpPeerAddrChange(int paramInt1, SocketAddress paramSocketAddress, int paramInt2)
/*     */   {
/*  55 */     switch (paramInt2) {
/*     */     case 1:
/*  57 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_AVAILABLE;
/*  58 */       break;
/*     */     case 2:
/*  60 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_UNREACHABLE;
/*  61 */       break;
/*     */     case 3:
/*  63 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_REMOVED;
/*  64 */       break;
/*     */     case 4:
/*  66 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_ADDED;
/*  67 */       break;
/*     */     case 5:
/*  69 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_MADE_PRIMARY;
/*  70 */       break;
/*     */     case 6:
/*  72 */       this.event = PeerAddressChangeNotification.AddressChangeEvent.ADDR_CONFIRMED;
/*  73 */       break;
/*     */     default:
/*  75 */       throw new AssertionError("Unknown event type");
/*     */     }
/*  77 */     this.assocId = paramInt1;
/*  78 */     this.address = paramSocketAddress;
/*     */   }
/*     */ 
/*     */   public int assocId()
/*     */   {
/*  83 */     return this.assocId;
/*     */   }
/*     */ 
/*     */   public void setAssociation(Association paramAssociation)
/*     */   {
/*  88 */     this.association = paramAssociation;
/*     */   }
/*     */ 
/*     */   public SocketAddress address()
/*     */   {
/*  93 */     assert (this.address != null);
/*  94 */     return this.address;
/*     */   }
/*     */ 
/*     */   public Association association()
/*     */   {
/*  99 */     assert (this.association != null);
/* 100 */     return this.association;
/*     */   }
/*     */ 
/*     */   public PeerAddressChangeNotification.AddressChangeEvent event()
/*     */   {
/* 105 */     assert (this.event != null);
/* 106 */     return this.event;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 111 */     StringBuilder localStringBuilder = new StringBuilder();
/* 112 */     localStringBuilder.append(super.toString()).append(" [");
/* 113 */     localStringBuilder.append("Address: ").append(this.address);
/* 114 */     localStringBuilder.append(", Association:").append(this.association);
/* 115 */     localStringBuilder.append(", Event: ").append(this.event).append("]");
/* 116 */     return localStringBuilder.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.nio.ch.SctpPeerAddrChange
 * JD-Core Version:    0.6.2
 */