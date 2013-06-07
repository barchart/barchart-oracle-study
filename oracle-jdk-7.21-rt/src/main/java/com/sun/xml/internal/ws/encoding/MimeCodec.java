/*     */ package com.sun.xml.internal.ws.encoding;
/*     */ 
/*     */ import com.sun.xml.internal.ws.api.SOAPVersion;
/*     */ import com.sun.xml.internal.ws.api.WSBinding;
/*     */ import com.sun.xml.internal.ws.api.message.Attachment;
/*     */ import com.sun.xml.internal.ws.api.message.AttachmentSet;
/*     */ import com.sun.xml.internal.ws.api.message.Message;
/*     */ import com.sun.xml.internal.ws.api.message.Packet;
/*     */ import com.sun.xml.internal.ws.api.pipe.Codec;
/*     */ import com.sun.xml.internal.ws.api.pipe.ContentType;
/*     */ import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.UUID;
/*     */ import javax.activation.CommandMap;
/*     */ import javax.activation.MailcapCommandMap;
/*     */ 
/*     */ abstract class MimeCodec
/*     */   implements Codec
/*     */ {
/*     */   public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
/*     */   private String boundary;
/*     */   private String messageContentType;
/*     */   private boolean hasAttachments;
/*     */   protected Codec rootCodec;
/*     */   protected final SOAPVersion version;
/*     */   protected final WSBinding binding;
/*     */ 
/*     */   protected MimeCodec(SOAPVersion version, WSBinding binding)
/*     */   {
/* 102 */     this.version = version;
/* 103 */     this.binding = binding;
/*     */   }
/*     */ 
/*     */   public String getMimeType() {
/* 107 */     return "multipart/related";
/*     */   }
/*     */ 
/*     */   public ContentType encode(Packet packet, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 113 */     Message msg = packet.getMessage();
/* 114 */     if (msg == null) {
/* 115 */       return null;
/*     */     }
/*     */ 
/* 118 */     if (this.hasAttachments) {
/* 119 */       writeln("--" + this.boundary, out);
/* 120 */       ContentType ct = this.rootCodec.getStaticContentType(packet);
/* 121 */       String ctStr = ct != null ? ct.getContentType() : this.rootCodec.getMimeType();
/* 122 */       writeln("Content-Type: " + ctStr, out);
/* 123 */       writeln(out);
/*     */     }
/* 125 */     ContentType primaryCt = this.rootCodec.encode(packet, out);
/*     */ 
/* 127 */     if (this.hasAttachments) {
/* 128 */       writeln(out);
/*     */ 
/* 130 */       for (Attachment att : msg.getAttachments()) {
/* 131 */         writeln("--" + this.boundary, out);
/*     */ 
/* 134 */         String cid = att.getContentId();
/* 135 */         if ((cid != null) && (cid.length() > 0) && (cid.charAt(0) != '<'))
/* 136 */           cid = '<' + cid + '>';
/* 137 */         writeln("Content-Id:" + cid, out);
/* 138 */         writeln("Content-Type: " + att.getContentType(), out);
/* 139 */         writeln("Content-Transfer-Encoding: binary", out);
/* 140 */         writeln(out);
/* 141 */         att.writeTo(out);
/* 142 */         writeln(out);
/*     */       }
/* 144 */       writeAsAscii("--" + this.boundary, out);
/* 145 */       writeAsAscii("--", out);
/*     */     }
/*     */ 
/* 148 */     return this.hasAttachments ? new ContentTypeImpl(this.messageContentType, packet.soapAction, null) : primaryCt;
/*     */   }
/*     */ 
/*     */   public ContentType getStaticContentType(Packet packet) {
/* 152 */     Message msg = packet.getMessage();
/* 153 */     this.hasAttachments = (!msg.getAttachments().isEmpty());
/*     */ 
/* 155 */     if (this.hasAttachments) {
/* 156 */       this.boundary = ("uuid:" + UUID.randomUUID().toString());
/* 157 */       String boundaryParameter = "boundary=\"" + this.boundary + "\"";
/*     */ 
/* 159 */       this.messageContentType = ("multipart/related; type=\"" + this.rootCodec.getMimeType() + "\"; " + boundaryParameter);
/*     */ 
/* 162 */       return new ContentTypeImpl(this.messageContentType, packet.soapAction, null);
/*     */     }
/* 164 */     return this.rootCodec.getStaticContentType(packet);
/*     */   }
/*     */ 
/*     */   protected MimeCodec(MimeCodec that)
/*     */   {
/* 172 */     this.version = that.version;
/* 173 */     this.binding = that.binding;
/*     */   }
/*     */ 
/*     */   public void decode(InputStream in, String contentType, Packet packet) throws IOException {
/* 177 */     MimeMultipartParser parser = new MimeMultipartParser(in, contentType, (StreamingAttachmentFeature)this.binding.getFeature(StreamingAttachmentFeature.class));
/* 178 */     decode(parser, packet);
/*     */   }
/*     */ 
/*     */   public void decode(ReadableByteChannel in, String contentType, Packet packet) {
/* 182 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   protected abstract void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract MimeCodec copy();
/*     */ 
/*     */   public static void writeln(String s, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 194 */     writeAsAscii(s, out);
/* 195 */     writeln(out);
/*     */   }
/*     */ 
/*     */   public static void writeAsAscii(String s, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 202 */     int len = s.length();
/* 203 */     for (int i = 0; i < len; i++)
/* 204 */       out.write((byte)s.charAt(i));
/*     */   }
/*     */ 
/*     */   public static void writeln(OutputStream out) throws IOException {
/* 208 */     out.write(13);
/* 209 */     out.write(10);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  68 */       CommandMap map = CommandMap.getDefaultCommandMap();
/*  69 */       if ((map instanceof MailcapCommandMap)) {
/*  70 */         MailcapCommandMap mailMap = (MailcapCommandMap)map;
/*  71 */         String hndlrStr = ";;x-java-content-handler=";
/*     */ 
/*  74 */         mailMap.addMailcap("text/xml" + hndlrStr + XmlDataContentHandler.class.getName());
/*     */ 
/*  76 */         mailMap.addMailcap("application/xml" + hndlrStr + XmlDataContentHandler.class.getName());
/*     */ 
/*  78 */         if (map.createDataContentHandler("image/*") == null) {
/*  79 */           mailMap.addMailcap("image/*" + hndlrStr + ImageDataContentHandler.class.getName());
/*     */         }
/*     */ 
/*  82 */         if (map.createDataContentHandler("text/plain") == null)
/*  83 */           mailMap.addMailcap("text/plain" + hndlrStr + StringDataContentHandler.class.getName());
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.encoding.MimeCodec
 * JD-Core Version:    0.6.2
 */