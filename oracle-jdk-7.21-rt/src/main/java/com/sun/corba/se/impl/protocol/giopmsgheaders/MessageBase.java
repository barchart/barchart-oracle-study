/*     */ package com.sun.corba.se.impl.protocol.giopmsgheaders;
/*     */ 
/*     */ import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
/*     */ import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
/*     */ import com.sun.corba.se.impl.logging.ORBUtilSystemException;
/*     */ import com.sun.corba.se.impl.orbutil.ORBClassLoader;
/*     */ import com.sun.corba.se.impl.orbutil.ORBUtility;
/*     */ import com.sun.corba.se.impl.protocol.AddressingDispositionException;
/*     */ import com.sun.corba.se.spi.ior.IOR;
/*     */ import com.sun.corba.se.spi.ior.ObjectKey;
/*     */ import com.sun.corba.se.spi.ior.ObjectKeyFactory;
/*     */ import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
/*     */ import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
/*     */ import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
/*     */ import com.sun.corba.se.spi.orb.ORB;
/*     */ import com.sun.corba.se.spi.orb.ORBData;
/*     */ import com.sun.corba.se.spi.servicecontext.ServiceContexts;
/*     */ import com.sun.corba.se.spi.transport.CorbaConnection;
/*     */ import com.sun.corba.se.spi.transport.ReadTimeouts;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ import org.omg.CORBA.CompletionStatus;
/*     */ import org.omg.CORBA.Principal;
/*     */ import org.omg.CORBA.SystemException;
/*     */ import org.omg.IOP.TaggedProfile;
/*     */ 
/*     */ public abstract class MessageBase
/*     */   implements Message
/*     */ {
/*     */   public byte[] giopHeader;
/*     */   private ByteBuffer byteBuffer;
/*     */   private int threadPoolToUse;
/*  84 */   byte encodingVersion = 0;
/*     */ 
/*  86 */   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
/*     */ 
/*     */   public static String typeToString(int paramInt)
/*     */   {
/*  93 */     return typeToString((byte)paramInt);
/*     */   }
/*     */ 
/*     */   public static String typeToString(byte paramByte)
/*     */   {
/*  98 */     String str = paramByte + "/";
/*  99 */     switch (paramByte) { case 0:
/* 100 */       str = str + "GIOPRequest"; break;
/*     */     case 1:
/* 101 */       str = str + "GIOPReply"; break;
/*     */     case 2:
/* 102 */       str = str + "GIOPCancelRequest"; break;
/*     */     case 3:
/* 103 */       str = str + "GIOPLocateRequest"; break;
/*     */     case 4:
/* 104 */       str = str + "GIOPLocateReply"; break;
/*     */     case 5:
/* 105 */       str = str + "GIOPCloseConnection"; break;
/*     */     case 6:
/* 106 */       str = str + "GIOPMessageError"; break;
/*     */     case 7:
/* 107 */       str = str + "GIOPFragment"; break;
/*     */     default:
/* 108 */       str = str + "Unknown";
/*     */     }
/* 110 */     return str;
/*     */   }
/*     */ 
/*     */   public static MessageBase readGIOPMessage(ORB paramORB, CorbaConnection paramCorbaConnection)
/*     */   {
/* 115 */     MessageBase localMessageBase = readGIOPHeader(paramORB, paramCorbaConnection);
/* 116 */     localMessageBase = (MessageBase)readGIOPBody(paramORB, paramCorbaConnection, localMessageBase);
/* 117 */     return localMessageBase;
/*     */   }
/*     */ 
/*     */   public static MessageBase readGIOPHeader(ORB paramORB, CorbaConnection paramCorbaConnection)
/*     */   {
/* 122 */     Object localObject1 = null;
/* 123 */     ReadTimeouts localReadTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
/*     */ 
/* 126 */     ByteBuffer localByteBuffer1 = null;
/*     */     try
/*     */     {
/* 129 */       localByteBuffer1 = paramCorbaConnection.read(12, 0, 12, localReadTimeouts.get_max_giop_header_time_to_wait());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 133 */       throw wrapper.ioexceptionWhenReadingConnection(localIOException);
/*     */     }
/*     */ 
/* 136 */     if (paramORB.giopDebugFlag)
/*     */     {
/* 141 */       dprint(".readGIOPHeader: " + typeToString(localByteBuffer1.get(7)));
/* 142 */       dprint(".readGIOPHeader: GIOP header is: ");
/* 143 */       ByteBuffer localByteBuffer2 = localByteBuffer1.asReadOnlyBuffer();
/* 144 */       localByteBuffer2.position(0).limit(12);
/* 145 */       ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(paramORB, localByteBuffer2);
/* 146 */       localByteBufferWithInfo.buflen = 12;
/* 147 */       CDRInputStream_1_0.printBuffer(localByteBufferWithInfo);
/*     */     }
/*     */ 
/* 164 */     int i = localByteBuffer1.get(0) << 24 & 0xFF000000;
/* 165 */     int j = localByteBuffer1.get(1) << 16 & 0xFF0000;
/* 166 */     int k = localByteBuffer1.get(2) << 8 & 0xFF00;
/* 167 */     int m = localByteBuffer1.get(3) << 0 & 0xFF;
/* 168 */     int n = i | j | k | m;
/*     */ 
/* 170 */     if (n != 1195986768)
/*     */     {
/* 173 */       throw wrapper.giopMagicError(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */ 
/* 179 */     byte b = 0;
/* 180 */     if ((localByteBuffer1.get(4) == 13) && (localByteBuffer1.get(5) <= 1) && (localByteBuffer1.get(5) > 0) && (paramORB.getORBData().isJavaSerializationEnabled()))
/*     */     {
/* 186 */       b = localByteBuffer1.get(5);
/* 187 */       localByteBuffer1.put(4, (byte)1);
/* 188 */       localByteBuffer1.put(5, (byte)2);
/*     */     }
/*     */ 
/* 191 */     GIOPVersion localGIOPVersion = paramORB.getORBData().getGIOPVersion();
/*     */ 
/* 193 */     if (paramORB.giopDebugFlag) {
/* 194 */       dprint(".readGIOPHeader: Message GIOP version: " + localByteBuffer1.get(4) + '.' + localByteBuffer1.get(5));
/*     */ 
/* 196 */       dprint(".readGIOPHeader: ORB Max GIOP Version: " + localGIOPVersion);
/*     */     }
/*     */ 
/* 200 */     if ((localByteBuffer1.get(4) > localGIOPVersion.getMajor()) || ((localByteBuffer1.get(4) == localGIOPVersion.getMajor()) && (localByteBuffer1.get(5) > localGIOPVersion.getMinor())))
/*     */     {
/* 213 */       if (localByteBuffer1.get(7) != 6) {
/* 214 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */     }
/*     */ 
/* 218 */     AreFragmentsAllowed(localByteBuffer1.get(4), localByteBuffer1.get(5), localByteBuffer1.get(6), localByteBuffer1.get(7));
/*     */ 
/* 222 */     switch (localByteBuffer1.get(7))
/*     */     {
/*     */     case 0:
/* 225 */       if (paramORB.giopDebugFlag) {
/* 226 */         dprint(".readGIOPHeader: creating RequestMessage");
/*     */       }
/*     */ 
/* 229 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 230 */         localObject1 = new RequestMessage_1_0(paramORB);
/* 231 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 232 */         localObject1 = new RequestMessage_1_1(paramORB);
/* 233 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 234 */         localObject1 = new RequestMessage_1_2(paramORB);
/*     */       else {
/* 236 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 3:
/* 242 */       if (paramORB.giopDebugFlag) {
/* 243 */         dprint(".readGIOPHeader: creating LocateRequestMessage");
/*     */       }
/*     */ 
/* 246 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 247 */         localObject1 = new LocateRequestMessage_1_0(paramORB);
/* 248 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 249 */         localObject1 = new LocateRequestMessage_1_1(paramORB);
/* 250 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 251 */         localObject1 = new LocateRequestMessage_1_2(paramORB);
/*     */       else {
/* 253 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 2:
/* 259 */       if (paramORB.giopDebugFlag) {
/* 260 */         dprint(".readGIOPHeader: creating CancelRequestMessage");
/*     */       }
/*     */ 
/* 263 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 264 */         localObject1 = new CancelRequestMessage_1_0();
/* 265 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 266 */         localObject1 = new CancelRequestMessage_1_1();
/* 267 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 268 */         localObject1 = new CancelRequestMessage_1_2();
/*     */       else {
/* 270 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 1:
/* 276 */       if (paramORB.giopDebugFlag) {
/* 277 */         dprint(".readGIOPHeader: creating ReplyMessage");
/*     */       }
/*     */ 
/* 280 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 281 */         localObject1 = new ReplyMessage_1_0(paramORB);
/* 282 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 283 */         localObject1 = new ReplyMessage_1_1(paramORB);
/* 284 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 285 */         localObject1 = new ReplyMessage_1_2(paramORB);
/*     */       else {
/* 287 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 4:
/* 293 */       if (paramORB.giopDebugFlag) {
/* 294 */         dprint(".readGIOPHeader: creating LocateReplyMessage");
/*     */       }
/*     */ 
/* 297 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 298 */         localObject1 = new LocateReplyMessage_1_0(paramORB);
/* 299 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 300 */         localObject1 = new LocateReplyMessage_1_1(paramORB);
/* 301 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 302 */         localObject1 = new LocateReplyMessage_1_2(paramORB);
/*     */       else {
/* 304 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 5:
/*     */     case 6:
/* 311 */       if (paramORB.giopDebugFlag) {
/* 312 */         dprint(".readGIOPHeader: creating Message for CloseConnection or MessageError");
/*     */       }
/*     */ 
/* 319 */       if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0))
/* 320 */         localObject1 = new Message_1_0();
/* 321 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 322 */         localObject1 = new Message_1_1();
/* 323 */       else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 324 */         localObject1 = new Message_1_1();
/*     */       else {
/* 326 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 7:
/* 332 */       if (paramORB.giopDebugFlag) {
/* 333 */         dprint(".readGIOPHeader: creating FragmentMessage");
/*     */       }
/*     */ 
/* 336 */       if ((localByteBuffer1.get(4) != 1) || (localByteBuffer1.get(5) != 0))
/*     */       {
/* 338 */         if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 1))
/* 339 */           localObject1 = new FragmentMessage_1_1();
/* 340 */         else if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 2))
/* 341 */           localObject1 = new FragmentMessage_1_2();
/*     */         else {
/* 343 */           throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */         }
/*     */       }
/*     */ 
/*     */       break;
/*     */     default:
/* 349 */       if (paramORB.giopDebugFlag) {
/* 350 */         dprint(".readGIOPHeader: UNKNOWN MESSAGE TYPE: " + localByteBuffer1.get(7));
/*     */       }
/*     */ 
/* 354 */       throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */     Object localObject2;
/* 362 */     if ((localByteBuffer1.get(4) == 1) && (localByteBuffer1.get(5) == 0)) {
/* 363 */       localObject2 = (Message_1_0)localObject1;
/* 364 */       ((Message_1_0)localObject2).magic = n;
/* 365 */       ((Message_1_0)localObject2).GIOP_version = new GIOPVersion(localByteBuffer1.get(4), localByteBuffer1.get(5));
/* 366 */       ((Message_1_0)localObject2).byte_order = (localByteBuffer1.get(6) == 1);
/*     */ 
/* 369 */       ((MessageBase)localObject1).threadPoolToUse = 0;
/* 370 */       ((Message_1_0)localObject2).message_type = localByteBuffer1.get(7);
/* 371 */       ((Message_1_0)localObject2).message_size = (readSize(localByteBuffer1.get(8), localByteBuffer1.get(9), localByteBuffer1.get(10), localByteBuffer1.get(11), ((Message_1_0)localObject2).isLittleEndian()) + 12);
/*     */     }
/*     */     else
/*     */     {
/* 375 */       localObject2 = (Message_1_1)localObject1;
/* 376 */       ((Message_1_1)localObject2).magic = n;
/* 377 */       ((Message_1_1)localObject2).GIOP_version = new GIOPVersion(localByteBuffer1.get(4), localByteBuffer1.get(5));
/* 378 */       ((Message_1_1)localObject2).flags = ((byte)(localByteBuffer1.get(6) & 0x3));
/*     */ 
/* 388 */       ((MessageBase)localObject1).threadPoolToUse = (localByteBuffer1.get(6) >>> 2 & 0x3F);
/* 389 */       ((Message_1_1)localObject2).message_type = localByteBuffer1.get(7);
/* 390 */       ((Message_1_1)localObject2).message_size = (readSize(localByteBuffer1.get(8), localByteBuffer1.get(9), localByteBuffer1.get(10), localByteBuffer1.get(11), ((Message_1_1)localObject2).isLittleEndian()) + 12);
/*     */     }
/*     */ 
/* 396 */     if (paramORB.giopDebugFlag)
/*     */     {
/* 401 */       dprint(".readGIOPHeader: header construction complete.");
/*     */ 
/* 404 */       localObject2 = localByteBuffer1.asReadOnlyBuffer();
/* 405 */       byte[] arrayOfByte = new byte[12];
/* 406 */       ((ByteBuffer)localObject2).position(0).limit(12);
/* 407 */       ((ByteBuffer)localObject2).get(arrayOfByte, 0, arrayOfByte.length);
/*     */ 
/* 409 */       ((MessageBase)localObject1).giopHeader = arrayOfByte;
/*     */     }
/*     */ 
/* 412 */     ((MessageBase)localObject1).setByteBuffer(localByteBuffer1);
/* 413 */     ((MessageBase)localObject1).setEncodingVersion(b);
/*     */ 
/* 415 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public static Message readGIOPBody(ORB paramORB, CorbaConnection paramCorbaConnection, Message paramMessage)
/*     */   {
/* 422 */     ReadTimeouts localReadTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
/*     */ 
/* 424 */     ByteBuffer localByteBuffer1 = paramMessage.getByteBuffer();
/*     */ 
/* 426 */     localByteBuffer1.position(12);
/* 427 */     int i = paramMessage.getSize() - 12;
/*     */     try
/*     */     {
/* 430 */       localByteBuffer1 = paramCorbaConnection.read(localByteBuffer1, 12, i, localReadTimeouts.get_max_time_to_wait());
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/* 434 */       throw wrapper.ioexceptionWhenReadingConnection(localIOException);
/*     */     }
/*     */ 
/* 437 */     paramMessage.setByteBuffer(localByteBuffer1);
/*     */ 
/* 439 */     if (paramORB.giopDebugFlag) {
/* 440 */       dprint(".readGIOPBody: received message:");
/* 441 */       ByteBuffer localByteBuffer2 = localByteBuffer1.asReadOnlyBuffer();
/* 442 */       localByteBuffer2.position(0).limit(paramMessage.getSize());
/* 443 */       ByteBufferWithInfo localByteBufferWithInfo = new ByteBufferWithInfo(paramORB, localByteBuffer2);
/* 444 */       CDRInputStream_1_0.printBuffer(localByteBufferWithInfo);
/*     */     }
/*     */ 
/* 447 */     return paramMessage;
/*     */   }
/*     */ 
/*     */   private static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, byte[] paramArrayOfByte, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal)
/*     */   {
/* 455 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
/* 456 */       return new RequestMessage_1_0(paramORB, paramServiceContexts, paramInt, paramBoolean, paramArrayOfByte, paramString, paramPrincipal);
/*     */     }
/*     */ 
/* 459 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
/* 460 */       return new RequestMessage_1_1(paramORB, paramServiceContexts, paramInt, paramBoolean, new byte[] { 0, 0, 0 }, paramArrayOfByte, paramString, paramPrincipal);
/*     */     }
/*     */ 
/* 463 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2))
/*     */     {
/* 467 */       byte b = 3;
/* 468 */       if (paramBoolean)
/* 469 */         b = 3;
/*     */       else {
/* 471 */         b = 0;
/*     */       }
/*     */ 
/* 489 */       TargetAddress localTargetAddress = new TargetAddress();
/* 490 */       localTargetAddress.object_key(paramArrayOfByte);
/* 491 */       RequestMessage_1_2 localRequestMessage_1_2 = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, localTargetAddress, paramString, paramServiceContexts);
/*     */ 
/* 495 */       localRequestMessage_1_2.setEncodingVersion(paramByte);
/* 496 */       return localRequestMessage_1_2;
/*     */     }
/* 498 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, IOR paramIOR, short paramShort, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal)
/*     */   {
/* 509 */     Object localObject1 = null;
/* 510 */     IIOPProfile localIIOPProfile = paramIOR.getProfile();
/*     */     Object localObject2;
/*     */     byte b;
/*     */     Object localObject3;
/* 512 */     if (paramShort == 0)
/*     */     {
/* 514 */       localIIOPProfile = paramIOR.getProfile();
/* 515 */       ObjectKey localObjectKey = localIIOPProfile.getObjectKey();
/* 516 */       localObject2 = localObjectKey.getBytes(paramORB);
/* 517 */       localObject1 = createRequest(paramORB, paramGIOPVersion, paramByte, paramInt, paramBoolean, (byte[])localObject2, paramString, paramServiceContexts, paramPrincipal);
/*     */     }
/*     */     else
/*     */     {
/* 524 */       if (!paramGIOPVersion.equals(GIOPVersion.V1_2))
/*     */       {
/* 527 */         throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */       }
/*     */ 
/* 534 */       b = 3;
/* 535 */       if (paramBoolean)
/* 536 */         b = 3;
/*     */       else {
/* 538 */         b = 0;
/*     */       }
/*     */ 
/* 541 */       localObject2 = new TargetAddress();
/* 542 */       if (paramShort == 1) {
/* 543 */         localIIOPProfile = paramIOR.getProfile();
/* 544 */         ((TargetAddress)localObject2).profile(localIIOPProfile.getIOPProfile());
/* 545 */       } else if (paramShort == 2) {
/* 546 */         localObject3 = new IORAddressingInfo(0, paramIOR.getIOPIOR());
/*     */ 
/* 549 */         ((TargetAddress)localObject2).ior((IORAddressingInfo)localObject3);
/*     */       }
/*     */       else {
/* 552 */         throw wrapper.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO);
/*     */       }
/*     */ 
/* 556 */       localObject1 = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, (TargetAddress)localObject2, paramString, paramServiceContexts);
/*     */ 
/* 560 */       ((RequestMessage)localObject1).setEncodingVersion(paramByte);
/*     */     }
/*     */ 
/* 563 */     if (paramGIOPVersion.supportsIORIIOPProfileComponents())
/*     */     {
/* 565 */       b = 0;
/* 566 */       localObject2 = (IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate();
/*     */ 
/* 568 */       localObject3 = ((IIOPProfileTemplate)localObject2).iteratorById(1398099457);
/*     */       int i;
/* 570 */       if (((Iterator)localObject3).hasNext()) {
/* 571 */         i = ((RequestPartitioningComponent)((Iterator)localObject3).next()).getRequestPartitioningId();
/*     */       }
/*     */ 
/* 575 */       if ((i < 0) || (i > 63))
/*     */       {
/* 577 */         throw wrapper.invalidRequestPartitioningId(new Integer(i), new Integer(0), new Integer(63));
/*     */       }
/*     */ 
/* 581 */       ((RequestMessage)localObject1).setThreadPoolToUse(i);
/*     */     }
/*     */ 
/* 584 */     return localObject1;
/*     */   }
/*     */ 
/*     */   public static ReplyMessage createReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, ServiceContexts paramServiceContexts, IOR paramIOR)
/*     */   {
/* 591 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
/* 592 */       return new ReplyMessage_1_0(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR);
/*     */     }
/* 594 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
/* 595 */       return new ReplyMessage_1_1(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR);
/*     */     }
/* 597 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 598 */       ReplyMessage_1_2 localReplyMessage_1_2 = new ReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramServiceContexts, paramIOR);
/*     */ 
/* 601 */       localReplyMessage_1_2.setEncodingVersion(paramByte);
/* 602 */       return localReplyMessage_1_2;
/*     */     }
/* 604 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static LocateRequestMessage createLocateRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, byte[] paramArrayOfByte)
/*     */   {
/* 613 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0))
/* 614 */       return new LocateRequestMessage_1_0(paramORB, paramInt, paramArrayOfByte);
/* 615 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1))
/* 616 */       return new LocateRequestMessage_1_1(paramORB, paramInt, paramArrayOfByte);
/* 617 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 618 */       TargetAddress localTargetAddress = new TargetAddress();
/* 619 */       localTargetAddress.object_key(paramArrayOfByte);
/* 620 */       LocateRequestMessage_1_2 localLocateRequestMessage_1_2 = new LocateRequestMessage_1_2(paramORB, paramInt, localTargetAddress);
/*     */ 
/* 622 */       localLocateRequestMessage_1_2.setEncodingVersion(paramByte);
/* 623 */       return localLocateRequestMessage_1_2;
/*     */     }
/* 625 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static LocateReplyMessage createLocateReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, IOR paramIOR)
/*     */   {
/* 634 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
/* 635 */       return new LocateReplyMessage_1_0(paramORB, paramInt1, paramInt2, paramIOR);
/*     */     }
/* 637 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
/* 638 */       return new LocateReplyMessage_1_1(paramORB, paramInt1, paramInt2, paramIOR);
/*     */     }
/* 640 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 641 */       LocateReplyMessage_1_2 localLocateReplyMessage_1_2 = new LocateReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramIOR);
/*     */ 
/* 644 */       localLocateReplyMessage_1_2.setEncodingVersion(paramByte);
/* 645 */       return localLocateReplyMessage_1_2;
/*     */     }
/* 647 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static CancelRequestMessage createCancelRequest(GIOPVersion paramGIOPVersion, int paramInt)
/*     */   {
/* 655 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0))
/* 656 */       return new CancelRequestMessage_1_0(paramInt);
/* 657 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1))
/* 658 */       return new CancelRequestMessage_1_1(paramInt);
/* 659 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 660 */       return new CancelRequestMessage_1_2(paramInt);
/*     */     }
/* 662 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static Message createCloseConnection(GIOPVersion paramGIOPVersion)
/*     */   {
/* 668 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
/* 669 */       return new Message_1_0(1195986768, false, (byte)5, 0);
/*     */     }
/* 671 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
/* 672 */       return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)5, 0);
/*     */     }
/*     */ 
/* 675 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 676 */       return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)5, 0);
/*     */     }
/*     */ 
/* 680 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static Message createMessageError(GIOPVersion paramGIOPVersion)
/*     */   {
/* 686 */     if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
/* 687 */       return new Message_1_0(1195986768, false, (byte)6, 0);
/*     */     }
/* 689 */     if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
/* 690 */       return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)6, 0);
/*     */     }
/*     */ 
/* 693 */     if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
/* 694 */       return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)6, 0);
/*     */     }
/*     */ 
/* 698 */     throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static FragmentMessage createFragmentMessage(GIOPVersion paramGIOPVersion)
/*     */   {
/* 708 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getRequestId(Message paramMessage) {
/* 712 */     switch (paramMessage.getType()) {
/*     */     case 0:
/* 714 */       return ((RequestMessage)paramMessage).getRequestId();
/*     */     case 1:
/* 716 */       return ((ReplyMessage)paramMessage).getRequestId();
/*     */     case 3:
/* 718 */       return ((LocateRequestMessage)paramMessage).getRequestId();
/*     */     case 4:
/* 720 */       return ((LocateReplyMessage)paramMessage).getRequestId();
/*     */     case 2:
/* 722 */       return ((CancelRequestMessage)paramMessage).getRequestId();
/*     */     case 7:
/* 724 */       return ((FragmentMessage)paramMessage).getRequestId();
/*     */     case 5:
/*     */     case 6:
/* 727 */     }throw wrapper.illegalGiopMsgType(CompletionStatus.COMPLETED_MAYBE);
/*     */   }
/*     */ 
/*     */   public static void setFlag(ByteBuffer paramByteBuffer, int paramInt)
/*     */   {
/* 735 */     int i = paramByteBuffer.get(6);
/* 736 */     i = (byte)(i | paramInt);
/* 737 */     paramByteBuffer.put(6, i);
/*     */   }
/*     */ 
/*     */   public static void clearFlag(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 744 */     paramArrayOfByte[6] = ((byte)(paramArrayOfByte[6] & (0xFF ^ paramInt)));
/*     */   }
/*     */ 
/*     */   private static void AreFragmentsAllowed(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
/*     */   {
/* 750 */     if ((paramByte1 == 1) && (paramByte2 == 0) && 
/* 751 */       (paramByte4 == 7)) {
/* 752 */       throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
/*     */     }
/*     */ 
/* 757 */     if ((paramByte3 & 0x2) == 2)
/* 758 */       switch (paramByte4) {
/*     */       case 2:
/*     */       case 5:
/*     */       case 6:
/* 762 */         throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
/*     */       case 3:
/*     */       case 4:
/* 766 */         if ((paramByte1 == 1) && (paramByte2 == 1))
/* 767 */           throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
/*     */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   static ObjectKey extractObjectKey(byte[] paramArrayOfByte, ORB paramORB)
/*     */   {
/*     */     try
/*     */     {
/* 783 */       if (paramArrayOfByte != null) {
/* 784 */         ObjectKey localObjectKey = paramORB.getObjectKeyFactory().create(paramArrayOfByte);
/*     */ 
/* 786 */         if (localObjectKey != null) {
/* 787 */           return localObjectKey;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 796 */     throw wrapper.invalidObjectKey();
/*     */   }
/*     */ 
/*     */   static ObjectKey extractObjectKey(TargetAddress paramTargetAddress, ORB paramORB)
/*     */   {
/* 806 */     int i = paramORB.getORBData().getGIOPTargetAddressPreference();
/* 807 */     int j = paramTargetAddress.discriminator();
/*     */ 
/* 809 */     switch (i) {
/*     */     case 0:
/* 811 */       if (j != 0) {
/* 812 */         throw new AddressingDispositionException((short)0);
/*     */       }
/*     */       break;
/*     */     case 1:
/* 816 */       if (j != 1) {
/* 817 */         throw new AddressingDispositionException((short)1);
/*     */       }
/*     */       break;
/*     */     case 2:
/* 821 */       if (j != 2) {
/* 822 */         throw new AddressingDispositionException((short)2);
/*     */       }
/*     */       break;
/*     */     case 3:
/* 826 */       break;
/*     */     default:
/* 828 */       throw wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
/*     */     }
/*     */     try
/*     */     {
/*     */       Object localObject1;
/*     */       TaggedProfile localTaggedProfile;
/*     */       Object localObject2;
/* 832 */       switch (j) {
/*     */       case 0:
/* 834 */         byte[] arrayOfByte = paramTargetAddress.object_key();
/* 835 */         if (arrayOfByte != null) {
/* 836 */           localObject1 = paramORB.getObjectKeyFactory().create(arrayOfByte);
/*     */ 
/* 838 */           if (localObject1 != null)
/* 839 */             return localObject1;
/*     */         }
/* 841 */         break;
/*     */       case 1:
/* 844 */         localObject1 = null;
/* 845 */         localTaggedProfile = paramTargetAddress.profile();
/* 846 */         if (localTaggedProfile != null) {
/* 847 */           localObject1 = IIOPFactories.makeIIOPProfile(paramORB, localTaggedProfile);
/* 848 */           localObject2 = ((IIOPProfile)localObject1).getObjectKey();
/* 849 */           if (localObject2 != null)
/* 850 */             return localObject2;
/*     */         }
/* 852 */         break;
/*     */       case 2:
/* 855 */         localObject2 = paramTargetAddress.ior();
/* 856 */         if (localObject2 != null) {
/* 857 */           localTaggedProfile = localObject2.ior.profiles[localObject2.selected_profile_index];
/* 858 */           localObject1 = IIOPFactories.makeIIOPProfile(paramORB, localTaggedProfile);
/* 859 */           ObjectKey localObjectKey = ((IIOPProfile)localObject1).getObjectKey();
/* 860 */           if (localObjectKey != null) {
/* 861 */             return localObjectKey;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */     }
/*     */ 
/* 876 */     throw wrapper.invalidObjectKey();
/*     */   }
/*     */ 
/*     */   private static int readSize(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, boolean paramBoolean)
/*     */   {
/*     */     int i;
/*     */     int j;
/*     */     int k;
/*     */     int m;
/* 884 */     if (!paramBoolean) {
/* 885 */       i = paramByte1 << 24 & 0xFF000000;
/* 886 */       j = paramByte2 << 16 & 0xFF0000;
/* 887 */       k = paramByte3 << 8 & 0xFF00;
/* 888 */       m = paramByte4 << 0 & 0xFF;
/*     */     } else {
/* 890 */       i = paramByte4 << 24 & 0xFF000000;
/* 891 */       j = paramByte3 << 16 & 0xFF0000;
/* 892 */       k = paramByte2 << 8 & 0xFF00;
/* 893 */       m = paramByte1 << 0 & 0xFF;
/*     */     }
/*     */ 
/* 896 */     return i | j | k | m;
/*     */   }
/*     */ 
/*     */   static void nullCheck(Object paramObject) {
/* 900 */     if (paramObject == null)
/* 901 */       throw wrapper.nullNotAllowed();
/*     */   }
/*     */ 
/*     */   static SystemException getSystemException(String paramString1, int paramInt, CompletionStatus paramCompletionStatus, String paramString2, ORBUtilSystemException paramORBUtilSystemException)
/*     */   {
/* 909 */     SystemException localSystemException = null;
/*     */     try
/*     */     {
/* 912 */       Class localClass = ORBClassLoader.loadClass(paramString1);
/* 913 */       if (paramString2 == null) {
/* 914 */         localSystemException = (SystemException)localClass.newInstance();
/*     */       } else {
/* 916 */         Class[] arrayOfClass = { String.class };
/* 917 */         Constructor localConstructor = localClass.getConstructor(arrayOfClass);
/* 918 */         Object[] arrayOfObject = { paramString2 };
/* 919 */         localSystemException = (SystemException)localConstructor.newInstance(arrayOfObject);
/*     */       }
/*     */     } catch (Exception localException) {
/* 922 */       throw paramORBUtilSystemException.badSystemExceptionInReply(CompletionStatus.COMPLETED_MAYBE, localException);
/*     */     }
/*     */ 
/* 926 */     localSystemException.minor = paramInt;
/* 927 */     localSystemException.completed = paramCompletionStatus;
/*     */ 
/* 929 */     return localSystemException;
/*     */   }
/*     */ 
/*     */   public void callback(MessageHandler paramMessageHandler)
/*     */     throws IOException
/*     */   {
/* 935 */     paramMessageHandler.handleInput(this);
/*     */   }
/*     */ 
/*     */   public ByteBuffer getByteBuffer()
/*     */   {
/* 940 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */   public void setByteBuffer(ByteBuffer paramByteBuffer)
/*     */   {
/* 945 */     this.byteBuffer = paramByteBuffer;
/*     */   }
/*     */ 
/*     */   public int getThreadPoolToUse()
/*     */   {
/* 950 */     return this.threadPoolToUse;
/*     */   }
/*     */ 
/*     */   public byte getEncodingVersion() {
/* 954 */     return this.encodingVersion;
/*     */   }
/*     */ 
/*     */   public void setEncodingVersion(byte paramByte) {
/* 958 */     this.encodingVersion = paramByte;
/*     */   }
/*     */ 
/*     */   private static void dprint(String paramString)
/*     */   {
/* 963 */     ORBUtility.dprint("MessageBase", paramString);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase
 * JD-Core Version:    0.6.2
 */