/*     */ package com.sun.corba.se.impl.protocol.giopmsgheaders;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.CDRInputStream;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.IORFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import java.io.IOException;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.CORBA.portable.OutputStream;
/*     */ 
/*     */ public final class LocateReplyMessage_1_2 extends Message_1_2
/*     */   implements LocateReplyMessage
/*     */ {
/*  60 */   private ORB orb = null;
/*  61 */   private ORBUtilSystemException wrapper = null;
/*  62 */   private int reply_status = 0;
/*  63 */   private IOR ior = null;
/*  64 */   private String exClassName = null;
/*  65 */   private int minorCode = 0;
/*  66 */   private CompletionStatus completionStatus = null;
/*  67 */   private short addrDisposition = 0;
/*     */ 
/*     */   LocateReplyMessage_1_2(ORB paramORB)
/*     */   {
/*  72 */     this.orb = paramORB;
/*  73 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*     */   }
/*     */ 
/*     */   LocateReplyMessage_1_2(ORB paramORB, int paramInt1, int paramInt2, IOR paramIOR)
/*     */   {
/*  79 */     super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)4, 0);
/*     */ 
/*  81 */     this.orb = paramORB;
/*  82 */     this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
/*     */ 
/*  84 */     this.request_id = paramInt1;
/*  85 */     this.reply_status = paramInt2;
/*  86 */     this.ior = paramIOR;
/*     */   }
/*     */ 
/*     */   public int getRequestId()
/*     */   {
/*  92 */     return this.request_id;
/*     */   }
/*     */ 
/*     */   public int getReplyStatus() {
/*  96 */     return this.reply_status;
/*     */   }
/*     */ 
/*     */   public short getAddrDisposition() {
/* 100 */     return this.addrDisposition;
/*     */   }
/*     */ 
/*     */   public SystemException getSystemException(String paramString) {
/* 104 */     return MessageBase.getSystemException(this.exClassName, this.minorCode, this.completionStatus, paramString, this.wrapper);
/*     */   }
/*     */ 
/*     */   public IOR getIOR()
/*     */   {
/* 109 */     return this.ior;
/*     */   }
/*     */ 
/*     */   public void read(org.omg.CORBA.portable.InputStream paramInputStream)
/*     */   {
/* 115 */     super.read(paramInputStream);
/* 116 */     this.request_id = paramInputStream.read_ulong();
/* 117 */     this.reply_status = paramInputStream.read_long();
/* 118 */     isValidReplyStatus(this.reply_status);
/*     */     Object localObject;
/* 126 */     if (this.reply_status == 4)
/*     */     {
/* 128 */       localObject = paramInputStream.read_string();
/* 129 */       this.exClassName = ORBUtility.classNameOf((String)localObject);
/* 130 */       this.minorCode = paramInputStream.read_long();
/* 131 */       int i = paramInputStream.read_long();
/*     */ 
/* 133 */       switch (i) {
/*     */       case 0:
/* 135 */         this.completionStatus = CompletionStatus.COMPLETED_YES;
/* 136 */         break;
/*     */       case 1:
/* 138 */         this.completionStatus = CompletionStatus.COMPLETED_NO;
/* 139 */         break;
/*     */       case 2:
/* 141 */         this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
/* 142 */         break;
/*     */       default:
/* 144 */         throw this.wrapper.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
/*     */       }
/*     */     }
/* 147 */     else if ((this.reply_status == 2) || (this.reply_status == 3))
/*     */     {
/* 149 */       localObject = (CDRInputStream)paramInputStream;
/* 150 */       this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)localObject);
/* 151 */     } else if (this.reply_status == 5)
/*     */     {
/* 155 */       this.addrDisposition = AddressingDispositionHelper.read(paramInputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(OutputStream paramOutputStream)
/*     */   {
/* 163 */     super.write(paramOutputStream);
/* 164 */     paramOutputStream.write_ulong(this.request_id);
/* 165 */     paramOutputStream.write_long(this.reply_status);
/*     */   }
/*     */ 
/*     */   public static void isValidReplyStatus(int paramInt)
/*     */   {
/* 175 */     switch (paramInt) {
/*     */     case 0:
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 182 */       break;
/*     */     default:
/* 184 */       ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("rpc.protocol");
/*     */ 
/* 186 */       throw localORBUtilSystemException.illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void callback(MessageHandler paramMessageHandler)
/*     */     throws IOException
/*     */   {
/* 193 */     paramMessageHandler.handleInput(this);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2
 * JD-Core Version:    0.6.2
 */