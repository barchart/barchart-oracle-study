/*     */ package com.sun.xml.internal.messaging.saaj.soap;
/*     */ 
/*     */ import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
/*     */ import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
/*     */ import com.sun.xml.internal.messaging.saaj.util.ParserPool;
/*     */ import com.sun.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
/*     */ import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.soap.SOAPException;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public class EnvelopeFactory
/*     */ {
/*  53 */   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
/*     */ 
/*  56 */   private static ParserPool parserPool = new ParserPool(5);
/*     */ 
/*     */   public static Envelope createEnvelope(Source src, SOAPPartImpl soapPart)
/*     */     throws SOAPException
/*     */   {
/*  63 */     SAXParser saxParser = null;
/*  64 */     if ((src instanceof StreamSource)) {
/*  65 */       if ((src instanceof JAXMStreamSource))
/*     */         try {
/*  67 */           if (!SOAPPartImpl.lazyContentLength)
/*  68 */             ((JAXMStreamSource)src).reset();
/*     */         }
/*     */         catch (IOException ioe) {
/*  71 */           log.severe("SAAJ0515.source.reset.exception");
/*  72 */           throw new SOAPExceptionImpl(ioe);
/*     */         }
/*     */       try
/*     */       {
/*  76 */         saxParser = parserPool.get();
/*     */       } catch (Exception e) {
/*  78 */         log.severe("SAAJ0601.util.newSAXParser.exception");
/*  79 */         throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", e);
/*     */       }
/*     */ 
/*  83 */       InputSource is = SAXSource.sourceToInputSource(src);
/*  84 */       if ((is.getEncoding() == null) && (soapPart.getSourceCharsetEncoding() != null))
/*  85 */         is.setEncoding(soapPart.getSourceCharsetEncoding());
/*     */       XMLReader rejectFilter;
/*     */       try
/*     */       {
/*  89 */         rejectFilter = new RejectDoctypeSaxFilter(saxParser);
/*     */       } catch (Exception ex) {
/*  91 */         log.severe("SAAJ0510.soap.cannot.create.envelope");
/*  92 */         throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex);
/*     */       }
/*     */ 
/*  96 */       src = new SAXSource(rejectFilter, is);
/*     */     }
/*     */     try
/*     */     {
/* 100 */       Transformer transformer = EfficientStreamingTransformer.newTransformer();
/*     */ 
/* 102 */       DOMResult result = new DOMResult(soapPart);
/* 103 */       transformer.transform(src, result);
/*     */ 
/* 105 */       Envelope env = (Envelope)soapPart.getEnvelope();
/* 106 */       return env;
/*     */     } catch (Exception ex) {
/* 108 */       if ((ex instanceof SOAPVersionMismatchException)) {
/* 109 */         throw ((SOAPVersionMismatchException)ex);
/*     */       }
/* 111 */       log.severe("SAAJ0511.soap.cannot.create.envelope");
/* 112 */       throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex);
/*     */     }
/*     */     finally
/*     */     {
/* 116 */       if (saxParser != null)
/* 117 */         parserPool.returnParser(saxParser);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.messaging.saaj.soap.EnvelopeFactory
 * JD-Core Version:    0.6.2
 */