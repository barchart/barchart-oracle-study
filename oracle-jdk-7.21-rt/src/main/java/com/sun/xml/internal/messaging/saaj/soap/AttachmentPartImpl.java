/*     */ package com.sun.xml.internal.messaging.saaj.soap;
/*     */ 
/*     */ import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
/*     */ import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.InternetHeaders;
/*     */ import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
/*     */ import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
/*     */ import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
/*     */ import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
/*     */ import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
/*     */ import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.activation.CommandMap;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.activation.MailcapCommandMap;
/*     */ import javax.xml.soap.AttachmentPart;
/*     */ import javax.xml.soap.MimeHeader;
/*     */ import javax.xml.soap.MimeHeaders;
/*     */ import javax.xml.soap.SOAPException;
/*     */ 
/*     */ public class AttachmentPartImpl extends AttachmentPart
/*     */ {
/*  61 */   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
/*     */   private final MimeHeaders headers;
/* 121 */   private MimeBodyPart rawContent = null;
/* 122 */   private DataHandler dataHandler = null;
/*     */ 
/* 125 */   private MIMEPart mimePart = null;
/*     */ 
/*     */   public AttachmentPartImpl() {
/* 128 */     this.headers = new MimeHeaders();
/*     */   }
/*     */ 
/*     */   public AttachmentPartImpl(MIMEPart part) {
/* 132 */     this.headers = new MimeHeaders();
/* 133 */     this.mimePart = part;
/* 134 */     List hdrs = part.getAllHeaders();
/* 135 */     for (com.sun.xml.internal.org.jvnet.mimepull.Header hd : hdrs)
/* 136 */       this.headers.addHeader(hd.getName(), hd.getValue());
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */     throws SOAPException
/*     */   {
/* 142 */     if (this.mimePart != null) {
/*     */       try {
/* 144 */         return this.mimePart.read().available();
/*     */       } catch (IOException e) {
/* 146 */         return -1;
/*     */       }
/*     */     }
/* 149 */     if ((this.rawContent == null) && (this.dataHandler == null)) {
/* 150 */       return 0;
/*     */     }
/* 152 */     if (this.rawContent != null) {
/*     */       try {
/* 154 */         return this.rawContent.getSize();
/*     */       } catch (Exception ex) {
/* 156 */         log.log(Level.SEVERE, "SAAJ0573.soap.attachment.getrawbytes.ioexception", new String[] { ex.getLocalizedMessage() });
/*     */ 
/* 160 */         throw new SOAPExceptionImpl("Raw InputStream Error: " + ex);
/*     */       }
/*     */     }
/* 163 */     ByteOutputStream bout = new ByteOutputStream();
/*     */     try {
/* 165 */       this.dataHandler.writeTo(bout);
/*     */     } catch (IOException ex) {
/* 167 */       log.log(Level.SEVERE, "SAAJ0501.soap.data.handler.err", new String[] { ex.getLocalizedMessage() });
/*     */ 
/* 171 */       throw new SOAPExceptionImpl("Data handler error: " + ex);
/*     */     }
/* 173 */     return bout.size();
/*     */   }
/*     */ 
/*     */   public void clearContent()
/*     */   {
/* 178 */     if (this.mimePart != null) {
/* 179 */       this.mimePart.close();
/* 180 */       this.mimePart = null;
/*     */     }
/* 182 */     this.dataHandler = null;
/* 183 */     this.rawContent = null;
/*     */   }
/*     */ 
/*     */   public Object getContent() throws SOAPException {
/*     */     try {
/* 188 */       if (this.mimePart != null)
/*     */       {
/* 190 */         return this.mimePart.read();
/*     */       }
/* 192 */       if (this.dataHandler != null)
/* 193 */         return getDataHandler().getContent();
/* 194 */       if (this.rawContent != null) {
/* 195 */         return this.rawContent.getContent();
/*     */       }
/* 197 */       log.severe("SAAJ0572.soap.no.content.for.attachment");
/* 198 */       throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
/*     */     }
/*     */     catch (Exception ex) {
/* 201 */       log.log(Level.SEVERE, "SAAJ0575.soap.attachment.getcontent.exception", ex);
/* 202 */       throw new SOAPExceptionImpl(ex.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setContent(Object object, String contentType) throws IllegalArgumentException
/*     */   {
/* 208 */     if (this.mimePart != null) {
/* 209 */       this.mimePart.close();
/* 210 */       this.mimePart = null;
/*     */     }
/* 212 */     DataHandler dh = new DataHandler(object, contentType);
/*     */ 
/* 214 */     setDataHandler(dh);
/*     */   }
/*     */ 
/*     */   public DataHandler getDataHandler() throws SOAPException
/*     */   {
/* 219 */     if (this.mimePart != null)
/*     */     {
/* 221 */       return new DataHandler(new DataSource()
/*     */       {
/*     */         public InputStream getInputStream() throws IOException {
/* 224 */           return AttachmentPartImpl.this.mimePart.read();
/*     */         }
/*     */ 
/*     */         public OutputStream getOutputStream() throws IOException {
/* 228 */           throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
/*     */         }
/*     */ 
/*     */         public String getContentType() {
/* 232 */           return AttachmentPartImpl.this.mimePart.getContentType();
/*     */         }
/*     */ 
/*     */         public String getName() {
/* 236 */           return "MIMEPart Wrapper DataSource";
/*     */         }
/*     */       });
/*     */     }
/* 240 */     if (this.dataHandler == null) {
/* 241 */       if (this.rawContent != null) {
/* 242 */         return new DataHandler(new MimePartDataSource(this.rawContent));
/*     */       }
/* 244 */       log.severe("SAAJ0502.soap.no.handler.for.attachment");
/* 245 */       throw new SOAPExceptionImpl("No data handler associated with this attachment");
/*     */     }
/* 247 */     return this.dataHandler;
/*     */   }
/*     */ 
/*     */   public void setDataHandler(DataHandler dataHandler) throws IllegalArgumentException
/*     */   {
/* 252 */     if (this.mimePart != null) {
/* 253 */       this.mimePart.close();
/* 254 */       this.mimePart = null;
/*     */     }
/* 256 */     if (dataHandler == null) {
/* 257 */       log.severe("SAAJ0503.soap.no.null.to.dataHandler");
/* 258 */       throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
/*     */     }
/* 260 */     this.dataHandler = dataHandler;
/* 261 */     this.rawContent = null;
/*     */ 
/* 263 */     log.log(Level.FINE, "SAAJ0580.soap.set.Content-Type", new String[] { dataHandler.getContentType() });
/*     */ 
/* 267 */     setMimeHeader("Content-Type", dataHandler.getContentType());
/*     */   }
/*     */ 
/*     */   public void removeAllMimeHeaders() {
/* 271 */     this.headers.removeAllHeaders();
/*     */   }
/*     */ 
/*     */   public void removeMimeHeader(String header) {
/* 275 */     this.headers.removeHeader(header);
/*     */   }
/*     */ 
/*     */   public String[] getMimeHeader(String name) {
/* 279 */     return this.headers.getHeader(name);
/*     */   }
/*     */ 
/*     */   public void setMimeHeader(String name, String value) {
/* 283 */     this.headers.setHeader(name, value);
/*     */   }
/*     */ 
/*     */   public void addMimeHeader(String name, String value) {
/* 287 */     this.headers.addHeader(name, value);
/*     */   }
/*     */ 
/*     */   public Iterator getAllMimeHeaders() {
/* 291 */     return this.headers.getAllHeaders();
/*     */   }
/*     */ 
/*     */   public Iterator getMatchingMimeHeaders(String[] names) {
/* 295 */     return this.headers.getMatchingHeaders(names);
/*     */   }
/*     */ 
/*     */   public Iterator getNonMatchingMimeHeaders(String[] names) {
/* 299 */     return this.headers.getNonMatchingHeaders(names);
/*     */   }
/*     */ 
/*     */   boolean hasAllHeaders(MimeHeaders hdrs) {
/* 303 */     if (hdrs != null) {
/* 304 */       Iterator i = hdrs.getAllHeaders();
/* 305 */       while (i.hasNext()) {
/* 306 */         MimeHeader hdr = (MimeHeader)i.next();
/* 307 */         String[] values = this.headers.getHeader(hdr.getName());
/* 308 */         boolean found = false;
/*     */ 
/* 310 */         if (values != null) {
/* 311 */           for (int j = 0; j < values.length; j++) {
/* 312 */             if (hdr.getValue().equalsIgnoreCase(values[j])) {
/* 313 */               found = true;
/* 314 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 318 */         if (!found) {
/* 319 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 323 */     return true;
/*     */   }
/*     */ 
/*     */   MimeBodyPart getMimePart() throws SOAPException {
/*     */     try {
/* 328 */       if (this.mimePart != null) {
/* 329 */         return new MimeBodyPart(this.mimePart);
/*     */       }
/* 331 */       if (this.rawContent != null) {
/* 332 */         copyMimeHeaders(this.headers, this.rawContent);
/* 333 */         return this.rawContent;
/*     */       }
/*     */ 
/* 336 */       MimeBodyPart envelope = new MimeBodyPart();
/*     */ 
/* 338 */       envelope.setDataHandler(this.dataHandler);
/* 339 */       copyMimeHeaders(this.headers, envelope);
/*     */ 
/* 341 */       return envelope;
/*     */     } catch (Exception ex) {
/* 343 */       log.severe("SAAJ0504.soap.cannot.externalize.attachment");
/* 344 */       throw new SOAPExceptionImpl("Unable to externalize attachment", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void copyMimeHeaders(MimeHeaders headers, MimeBodyPart mbp)
/*     */     throws SOAPException
/*     */   {
/* 351 */     Iterator i = headers.getAllHeaders();
/*     */ 
/* 353 */     while (i.hasNext())
/*     */       try {
/* 355 */         MimeHeader mh = (MimeHeader)i.next();
/*     */ 
/* 357 */         mbp.setHeader(mh.getName(), mh.getValue());
/*     */       } catch (Exception ex) {
/* 359 */         log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
/* 360 */         throw new SOAPExceptionImpl("Unable to copy MIME header", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void copyMimeHeaders(MimeBodyPart mbp, AttachmentPartImpl ap) throws SOAPException
/*     */   {
/*     */     try {
/* 367 */       List hdr = mbp.getAllHeaders();
/* 368 */       int sz = hdr.size();
/* 369 */       for (int i = 0; i < sz; i++) {
/* 370 */         com.sun.xml.internal.messaging.saaj.packaging.mime.Header h = (com.sun.xml.internal.messaging.saaj.packaging.mime.Header)hdr.get(i);
/* 371 */         if (!h.getName().equalsIgnoreCase("Content-Type"))
/*     */         {
/* 373 */           ap.addMimeHeader(h.getName(), h.getValue());
/*     */         }
/*     */       }
/*     */     } catch (Exception ex) { log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
/* 377 */       throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBase64Content(InputStream content, String contentType)
/*     */     throws SOAPException
/*     */   {
/* 386 */     if (this.mimePart != null) {
/* 387 */       this.mimePart.close();
/* 388 */       this.mimePart = null;
/*     */     }
/* 390 */     this.dataHandler = null;
/* 391 */     InputStream decoded = null;
/*     */     try {
/* 393 */       decoded = MimeUtility.decode(content, "base64");
/* 394 */       InternetHeaders hdrs = new InternetHeaders();
/* 395 */       hdrs.setHeader("Content-Type", contentType);
/*     */ 
/* 399 */       ByteOutputStream bos = new ByteOutputStream();
/* 400 */       bos.write(decoded);
/* 401 */       this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
/* 402 */       setMimeHeader("Content-Type", contentType);
/*     */     } catch (Exception e) {
/* 404 */       log.log(Level.SEVERE, "SAAJ0578.soap.attachment.setbase64content.exception", e);
/* 405 */       throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */     } finally {
/*     */       try {
/* 408 */         decoded.close();
/*     */       } catch (IOException ex) {
/* 410 */         throw new SOAPException(ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getBase64Content()
/*     */     throws SOAPException
/*     */   {
/*     */     InputStream stream;
/*     */     InputStream stream;
/* 417 */     if (this.mimePart != null) {
/* 418 */       stream = this.mimePart.read();
/* 419 */     } else if (this.rawContent != null) {
/*     */       try {
/* 421 */         stream = this.rawContent.getInputStream();
/*     */       } catch (Exception e) {
/* 423 */         log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e);
/* 424 */         throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */       }
/* 426 */     } else if (this.dataHandler != null) {
/*     */       try {
/* 428 */         stream = this.dataHandler.getInputStream();
/*     */       } catch (IOException e) {
/* 430 */         log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
/* 431 */         throw new SOAPExceptionImpl("DataHandler error" + e);
/*     */       }
/*     */     } else {
/* 434 */       log.severe("SAAJ0572.soap.no.content.for.attachment");
/* 435 */       throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
/*     */     }
/*     */ 
/* 442 */     int size = 1024;
/*     */ 
/* 444 */     if (stream != null) {
/*     */       try {
/* 446 */         ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
/*     */ 
/* 449 */         OutputStream ret = MimeUtility.encode(bos, "base64");
/* 450 */         byte[] buf = new byte[size];
/*     */         int len;
/* 451 */         while ((len = stream.read(buf, 0, size)) != -1) {
/* 452 */           ret.write(buf, 0, len);
/*     */         }
/* 454 */         ret.flush();
/* 455 */         buf = bos.toByteArray();
/* 456 */         return new ByteArrayInputStream(buf);
/*     */       }
/*     */       catch (Exception e) {
/* 459 */         log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e);
/* 460 */         throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */       } finally {
/*     */         try {
/* 463 */           stream.close();
/*     */         }
/*     */         catch (IOException ex)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 470 */     log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
/* 471 */     throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
/*     */   }
/*     */ 
/*     */   public void setRawContent(InputStream content, String contentType)
/*     */     throws SOAPException
/*     */   {
/* 477 */     if (this.mimePart != null) {
/* 478 */       this.mimePart.close();
/* 479 */       this.mimePart = null;
/*     */     }
/* 481 */     this.dataHandler = null;
/*     */     try {
/* 483 */       InternetHeaders hdrs = new InternetHeaders();
/* 484 */       hdrs.setHeader("Content-Type", contentType);
/*     */ 
/* 488 */       ByteOutputStream bos = new ByteOutputStream();
/* 489 */       bos.write(content);
/* 490 */       this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
/* 491 */       setMimeHeader("Content-Type", contentType);
/*     */     } catch (Exception e) {
/* 493 */       log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
/* 494 */       throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */     } finally {
/*     */       try {
/* 497 */         content.close();
/*     */       } catch (IOException ex) {
/* 499 */         throw new SOAPException(ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRawContentBytes(byte[] content, int off, int len, String contentType)
/*     */     throws SOAPException
/*     */   {
/* 525 */     if (this.mimePart != null) {
/* 526 */       this.mimePart.close();
/* 527 */       this.mimePart = null;
/*     */     }
/* 529 */     if (content == null) {
/* 530 */       throw new SOAPExceptionImpl("Null content passed to setRawContentBytes");
/*     */     }
/* 532 */     this.dataHandler = null;
/*     */     try {
/* 534 */       InternetHeaders hdrs = new InternetHeaders();
/* 535 */       hdrs.setHeader("Content-Type", contentType);
/* 536 */       this.rawContent = new MimeBodyPart(hdrs, content, off, len);
/* 537 */       setMimeHeader("Content-Type", contentType);
/*     */     } catch (Exception e) {
/* 539 */       log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
/*     */ 
/* 541 */       throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getRawContent() throws SOAPException {
/* 546 */     if (this.mimePart != null) {
/* 547 */       return this.mimePart.read();
/*     */     }
/* 549 */     if (this.rawContent != null)
/*     */       try {
/* 551 */         return this.rawContent.getInputStream();
/*     */       } catch (Exception e) {
/* 553 */         log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
/* 554 */         throw new SOAPExceptionImpl(e.getLocalizedMessage());
/*     */       }
/* 556 */     if (this.dataHandler != null) {
/*     */       try {
/* 558 */         return this.dataHandler.getInputStream();
/*     */       } catch (IOException e) {
/* 560 */         log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
/* 561 */         throw new SOAPExceptionImpl("DataHandler error" + e);
/*     */       }
/*     */     }
/* 564 */     log.severe("SAAJ0572.soap.no.content.for.attachment");
/* 565 */     throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
/*     */   }
/*     */ 
/*     */   public byte[] getRawContentBytes()
/*     */     throws SOAPException
/*     */   {
/*     */     InputStream ret;
/* 571 */     if (this.mimePart != null) {
/*     */       try {
/* 573 */         ret = this.mimePart.read();
/* 574 */         return ASCIIUtility.getBytes(ret);
/*     */       } catch (IOException ex) {
/* 576 */         log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", ex);
/* 577 */         throw new SOAPExceptionImpl(ex);
/*     */       }
/*     */     }
/* 580 */     if (this.rawContent != null)
/*     */       try {
/* 582 */         ret = this.rawContent.getInputStream();
/* 583 */         return ASCIIUtility.getBytes(ret);
/*     */       } catch (Exception e) {
/* 585 */         log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
/* 586 */         throw new SOAPExceptionImpl(e);
/*     */       }
/* 588 */     if (this.dataHandler != null) {
/*     */       try {
/* 590 */         ret = this.dataHandler.getInputStream();
/* 591 */         return ASCIIUtility.getBytes(ret);
/*     */       } catch (IOException e) {
/* 593 */         log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
/* 594 */         throw new SOAPExceptionImpl("DataHandler error" + e);
/*     */       }
/*     */     }
/* 597 */     log.severe("SAAJ0572.soap.no.content.for.attachment");
/* 598 */     throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 604 */     return this == o;
/*     */   }
/*     */ 
/*     */   public MimeHeaders getMimeHeaders() {
/* 608 */     return this.headers;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  67 */       CommandMap map = CommandMap.getDefaultCommandMap();
/*  68 */       if ((map instanceof MailcapCommandMap)) {
/*  69 */         MailcapCommandMap mailMap = (MailcapCommandMap)map;
/*  70 */         String hndlrStr = ";;x-java-content-handler=";
/*  71 */         mailMap.addMailcap("text/xml" + hndlrStr + "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
/*     */ 
/*  75 */         mailMap.addMailcap("application/xml" + hndlrStr + "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
/*     */ 
/*  79 */         mailMap.addMailcap("application/fastinfoset" + hndlrStr + "com.sun.xml.internal.messaging.saaj.soap.FastInfosetDataContentHandler");
/*     */ 
/*  96 */         mailMap.addMailcap("image/*" + hndlrStr + "com.sun.xml.internal.messaging.saaj.soap.ImageDataContentHandler");
/*     */ 
/* 100 */         mailMap.addMailcap("text/plain" + hndlrStr + "com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler");
/*     */       }
/*     */       else
/*     */       {
/* 105 */         throw new SOAPExceptionImpl("Default CommandMap is not a MailcapCommandMap");
/*     */       }
/*     */     } catch (Throwable t) {
/* 108 */       log.log(Level.SEVERE, "SAAJ0508.soap.cannot.register.handlers", t);
/*     */ 
/* 112 */       if ((t instanceof RuntimeException)) {
/* 113 */         throw ((RuntimeException)t);
/*     */       }
/* 115 */       throw new RuntimeException(t.getLocalizedMessage());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl
 * JD-Core Version:    0.6.2
 */