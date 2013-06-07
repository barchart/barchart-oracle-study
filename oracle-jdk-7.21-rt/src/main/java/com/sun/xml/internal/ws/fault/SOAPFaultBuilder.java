/*     */ package com.sun.xml.internal.ws.fault;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.bind.api.Bridge;
/*     */ import com.sun.xml.internal.bind.api.JAXBRIContext;
/*     */ import com.sun.xml.internal.bind.api.TypeReference;
/*     */ import com.sun.xml.internal.ws.api.SOAPVersion;
/*     */ import com.sun.xml.internal.ws.api.message.Message;
/*     */ import com.sun.xml.internal.ws.api.model.CheckedException;
/*     */ import com.sun.xml.internal.ws.api.model.ExceptionType;
/*     */ import com.sun.xml.internal.ws.encoding.soap.SerializationException;
/*     */ import com.sun.xml.internal.ws.message.FaultMessage;
/*     */ import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
/*     */ import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
/*     */ import com.sun.xml.internal.ws.util.DOMUtil;
/*     */ import com.sun.xml.internal.ws.util.StringUtils;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.bind.JAXBContext;
/*     */ import javax.xml.bind.JAXBException;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.soap.Detail;
/*     */ import javax.xml.soap.DetailEntry;
/*     */ import javax.xml.soap.SOAPFault;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.ws.ProtocolException;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ import javax.xml.ws.soap.SOAPFaultException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public abstract class SOAPFaultBuilder
/*     */ {
/*     */   private static final JAXBRIContext JAXB_CONTEXT;
/* 533 */   private static final Logger logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
/*     */   public static boolean captureStackTrace;
/* 540 */   static final String CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";
/*     */ 
/*     */   abstract DetailType getDetail();
/*     */ 
/*     */   abstract void setDetail(DetailType paramDetailType);
/*     */ 
/*     */   @Nullable
/*     */   public QName getFirstDetailEntryName()
/*     */   {
/*  84 */     DetailType dt = getDetail();
/*  85 */     if (dt != null) {
/*  86 */       Node entry = dt.getDetail(0);
/*  87 */       if (entry != null) {
/*  88 */         return new QName(entry.getNamespaceURI(), entry.getLocalName());
/*     */       }
/*     */     }
/*  91 */     return null;
/*     */   }
/*     */ 
/*     */   abstract String getFaultString();
/*     */ 
/*     */   public Throwable createException(Map<QName, CheckedExceptionImpl> exceptions)
/*     */     throws JAXBException
/*     */   {
/* 103 */     DetailType dt = getDetail();
/* 104 */     Node detail = null;
/* 105 */     if (dt != null) detail = dt.getDetail(0);
/*     */ 
/* 108 */     if ((detail == null) || (exceptions == null))
/*     */     {
/* 111 */       return attachServerException(getProtocolException());
/*     */     }
/*     */ 
/* 115 */     QName detailName = new QName(detail.getNamespaceURI(), detail.getLocalName());
/* 116 */     CheckedExceptionImpl ce = (CheckedExceptionImpl)exceptions.get(detailName);
/* 117 */     if (ce == null)
/*     */     {
/* 119 */       return attachServerException(getProtocolException());
/*     */     }
/*     */ 
/* 123 */     if (ce.getExceptionType().equals(ExceptionType.UserDefined)) {
/* 124 */       return attachServerException(createUserDefinedException(ce));
/*     */     }
/*     */ 
/* 127 */     Class exceptionClass = ce.getExceptionClass();
/*     */     try {
/* 129 */       Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class, (Class)ce.getDetailType().type });
/* 130 */       Exception exception = (Exception)constructor.newInstance(new Object[] { getFaultString(), getJAXBObject(detail, ce) });
/* 131 */       return attachServerException(exception);
/*     */     } catch (Exception e) {
/* 133 */       throw new WebServiceException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public static Message createSOAPFaultMessage(@NotNull SOAPVersion soapVersion, @NotNull ProtocolException ex, @Nullable QName faultcode)
/*     */   {
/* 147 */     Object detail = getFaultDetail(null, ex);
/* 148 */     if (soapVersion == SOAPVersion.SOAP_12)
/* 149 */       return createSOAP12Fault(soapVersion, ex, detail, null, faultcode);
/* 150 */     return createSOAP11Fault(soapVersion, ex, detail, null, faultcode);
/*     */   }
/*     */ 
/*     */   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex)
/*     */   {
/* 173 */     return createSOAPFaultMessage(soapVersion, ceModel, ex, null);
/*     */   }
/*     */ 
/*     */   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, CheckedExceptionImpl ceModel, Throwable ex, QName faultCode)
/*     */   {
/* 182 */     Object detail = getFaultDetail(ceModel, ex);
/* 183 */     if (soapVersion == SOAPVersion.SOAP_12)
/* 184 */       return createSOAP12Fault(soapVersion, ex, detail, ceModel, faultCode);
/* 185 */     return createSOAP11Fault(soapVersion, ex, detail, ceModel, faultCode);
/*     */   }
/*     */ 
/*     */   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode)
/*     */   {
/* 212 */     if (faultCode == null)
/* 213 */       faultCode = getDefaultFaultCode(soapVersion);
/* 214 */     return createSOAPFaultMessage(soapVersion, faultString, faultCode, null);
/*     */   }
/*     */ 
/*     */   public static Message createSOAPFaultMessage(SOAPVersion soapVersion, SOAPFault fault) {
/* 218 */     switch (1.$SwitchMap$com$sun$xml$internal$ws$api$SOAPVersion[soapVersion.ordinal()]) {
/*     */     case 1:
/* 220 */       return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(fault), soapVersion);
/*     */     case 2:
/* 222 */       return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(fault), soapVersion);
/*     */     }
/* 224 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private static Message createSOAPFaultMessage(SOAPVersion soapVersion, String faultString, QName faultCode, Element detail)
/*     */   {
/* 229 */     switch (1.$SwitchMap$com$sun$xml$internal$ws$api$SOAPVersion[soapVersion.ordinal()]) {
/*     */     case 1:
/* 231 */       return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(faultCode, faultString, null, detail), soapVersion);
/*     */     case 2:
/* 233 */       return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(faultCode, faultString, detail), soapVersion);
/*     */     }
/* 235 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   final void captureStackTrace(@Nullable Throwable t)
/*     */   {
/* 244 */     if (t == null) return;
/* 245 */     if (!captureStackTrace) return;
/*     */     try
/*     */     {
/* 248 */       Document d = DOMUtil.createDom();
/* 249 */       ExceptionBean.marshal(t, d);
/*     */ 
/* 251 */       DetailType detail = getDetail();
/* 252 */       if (detail == null) {
/* 253 */         setDetail(detail = new DetailType());
/*     */       }
/* 255 */       detail.getDetails().add(d.getDocumentElement());
/*     */     }
/*     */     catch (JAXBException e) {
/* 258 */       logger.log(Level.WARNING, "Unable to capture the stack trace into XML", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T extends Throwable> T attachServerException(T t)
/*     */   {
/* 266 */     DetailType detail = getDetail();
/* 267 */     if (detail == null) return t;
/*     */ 
/* 269 */     for (Element n : detail.getDetails()) {
/* 270 */       if (ExceptionBean.isStackTraceXml(n)) {
/*     */         try {
/* 272 */           t.initCause(ExceptionBean.unmarshal(n));
/*     */         }
/*     */         catch (JAXBException e) {
/* 275 */           logger.log(Level.WARNING, "Unable to read the capture stack trace in the fault", e);
/*     */         }
/* 277 */         return t;
/*     */       }
/*     */     }
/*     */ 
/* 281 */     return t;
/*     */   }
/*     */ 
/*     */   protected abstract Throwable getProtocolException();
/*     */ 
/*     */   private Object getJAXBObject(Node jaxbBean, CheckedException ce) throws JAXBException {
/* 287 */     Bridge bridge = ce.getBridge();
/* 288 */     return bridge.unmarshal(jaxbBean);
/*     */   }
/*     */ 
/*     */   private Exception createUserDefinedException(CheckedExceptionImpl ce) {
/* 292 */     Class exceptionClass = ce.getExceptionClass();
/* 293 */     Class detailBean = ce.getDetailBean();
/*     */     try {
/* 295 */       Node detailNode = (Node)getDetail().getDetails().get(0);
/* 296 */       Object jaxbDetail = getJAXBObject(detailNode, ce);
/*     */       try
/*     */       {
/* 299 */         exConstructor = exceptionClass.getConstructor(new Class[] { String.class, detailBean });
/* 300 */         return (Exception)exConstructor.newInstance(new Object[] { getFaultString(), jaxbDetail });
/*     */       } catch (NoSuchMethodException e) {
/* 302 */         Constructor exConstructor = exceptionClass.getConstructor(new Class[] { String.class });
/* 303 */         return (Exception)exConstructor.newInstance(new Object[] { getFaultString() });
/*     */       }
/*     */     } catch (Exception e) {
/* 306 */       throw new WebServiceException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getWriteMethod(Field f) {
/* 311 */     return "set" + StringUtils.capitalize(f.getName());
/*     */   }
/*     */ 
/*     */   private static Object getFaultDetail(CheckedExceptionImpl ce, Throwable exception) {
/* 315 */     if (ce == null)
/* 316 */       return null;
/* 317 */     if (ce.getExceptionType().equals(ExceptionType.UserDefined))
/* 318 */       return createDetailFromUserDefinedException(ce, exception);
/*     */     try
/*     */     {
/* 321 */       Method m = exception.getClass().getMethod("getFaultInfo", new Class[0]);
/* 322 */       return m.invoke(exception, new Object[0]);
/*     */     } catch (Exception e) {
/* 324 */       throw new SerializationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Object createDetailFromUserDefinedException(CheckedExceptionImpl ce, Object exception) {
/* 329 */     Class detailBean = ce.getDetailBean();
/* 330 */     Field[] fields = detailBean.getDeclaredFields();
/*     */     try {
/* 332 */       Object detail = detailBean.newInstance();
/* 333 */       for (Field f : fields) {
/* 334 */         Method em = exception.getClass().getMethod(getReadMethod(f), new Class[0]);
/*     */         try {
/* 336 */           Method sm = detailBean.getMethod(getWriteMethod(f), new Class[] { em.getReturnType() });
/* 337 */           sm.invoke(detail, new Object[] { em.invoke(exception, new Object[0]) });
/*     */         }
/*     */         catch (NoSuchMethodException ne) {
/* 340 */           Field sf = detailBean.getField(f.getName());
/* 341 */           sf.set(detail, em.invoke(exception, new Object[0]));
/*     */         }
/*     */       }
/* 344 */       return detail;
/*     */     } catch (Exception e) {
/* 346 */       throw new SerializationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getReadMethod(Field f) {
/* 351 */     if (f.getType().isAssignableFrom(Boolean.TYPE))
/* 352 */       return "is" + StringUtils.capitalize(f.getName());
/* 353 */     return "get" + StringUtils.capitalize(f.getName());
/*     */   }
/*     */ 
/*     */   private static Message createSOAP11Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
/* 357 */     SOAPFaultException soapFaultException = null;
/* 358 */     String faultString = null;
/* 359 */     String faultActor = null;
/* 360 */     Throwable cause = e.getCause();
/* 361 */     if ((e instanceof SOAPFaultException))
/* 362 */       soapFaultException = (SOAPFaultException)e;
/* 363 */     else if ((cause != null) && ((cause instanceof SOAPFaultException))) {
/* 364 */       soapFaultException = (SOAPFaultException)e.getCause();
/*     */     }
/* 366 */     if (soapFaultException != null) {
/* 367 */       QName soapFaultCode = soapFaultException.getFault().getFaultCodeAsQName();
/* 368 */       if (soapFaultCode != null) {
/* 369 */         faultCode = soapFaultCode;
/*     */       }
/* 371 */       faultString = soapFaultException.getFault().getFaultString();
/* 372 */       faultActor = soapFaultException.getFault().getFaultActor();
/*     */     }
/*     */ 
/* 375 */     if (faultCode == null) {
/* 376 */       faultCode = getDefaultFaultCode(soapVersion);
/*     */     }
/*     */ 
/* 379 */     if (faultString == null) {
/* 380 */       faultString = e.getMessage();
/* 381 */       if (faultString == null) {
/* 382 */         faultString = e.toString();
/*     */       }
/*     */     }
/* 385 */     Element detailNode = null;
/* 386 */     QName firstEntry = null;
/* 387 */     if ((detail == null) && (soapFaultException != null)) {
/* 388 */       detailNode = soapFaultException.getFault().getDetail();
/* 389 */       firstEntry = getFirstDetailEntryName((Detail)detailNode);
/* 390 */     } else if (ce != null) {
/*     */       try {
/* 392 */         DOMResult dr = new DOMResult();
/* 393 */         ce.getBridge().marshal(detail, dr);
/* 394 */         detailNode = (Element)dr.getNode().getFirstChild();
/* 395 */         firstEntry = getFirstDetailEntryName(detailNode);
/*     */       }
/*     */       catch (JAXBException e1) {
/* 398 */         faultString = e.getMessage();
/* 399 */         faultCode = getDefaultFaultCode(soapVersion);
/*     */       }
/*     */     }
/* 402 */     SOAP11Fault soap11Fault = new SOAP11Fault(faultCode, faultString, faultActor, detailNode);
/*     */ 
/* 405 */     if (ce == null) {
/* 406 */       soap11Fault.captureStackTrace(e);
/*     */     }
/* 408 */     Message msg = JAXBMessage.create(JAXB_CONTEXT, soap11Fault, soapVersion);
/* 409 */     return new FaultMessage(msg, firstEntry);
/*     */   }
/*     */   @Nullable
/*     */   private static QName getFirstDetailEntryName(@Nullable Detail detail) {
/* 413 */     if (detail != null) {
/* 414 */       Iterator it = detail.getDetailEntries();
/* 415 */       if (it.hasNext()) {
/* 416 */         DetailEntry entry = (DetailEntry)it.next();
/* 417 */         return getFirstDetailEntryName(entry);
/*     */       }
/*     */     }
/* 420 */     return null;
/*     */   }
/*     */   @NotNull
/*     */   private static QName getFirstDetailEntryName(@NotNull Element entry) {
/* 424 */     return new QName(entry.getNamespaceURI(), entry.getLocalName());
/*     */   }
/*     */ 
/*     */   private static Message createSOAP12Fault(SOAPVersion soapVersion, Throwable e, Object detail, CheckedExceptionImpl ce, QName faultCode) {
/* 428 */     SOAPFaultException soapFaultException = null;
/* 429 */     CodeType code = null;
/* 430 */     String faultString = null;
/* 431 */     String faultRole = null;
/* 432 */     String faultNode = null;
/* 433 */     Throwable cause = e.getCause();
/* 434 */     if ((e instanceof SOAPFaultException))
/* 435 */       soapFaultException = (SOAPFaultException)e;
/* 436 */     else if ((cause != null) && ((cause instanceof SOAPFaultException))) {
/* 437 */       soapFaultException = (SOAPFaultException)e.getCause();
/*     */     }
/* 439 */     if (soapFaultException != null) {
/* 440 */       SOAPFault fault = soapFaultException.getFault();
/* 441 */       QName soapFaultCode = fault.getFaultCodeAsQName();
/* 442 */       if (soapFaultCode != null) {
/* 443 */         faultCode = soapFaultCode;
/* 444 */         code = new CodeType(faultCode);
/* 445 */         Iterator iter = fault.getFaultSubcodes();
/* 446 */         boolean first = true;
/* 447 */         SubcodeType subcode = null;
/* 448 */         while (iter.hasNext()) {
/* 449 */           QName value = (QName)iter.next();
/* 450 */           if (first) {
/* 451 */             SubcodeType sct = new SubcodeType(value);
/* 452 */             code.setSubcode(sct);
/* 453 */             subcode = sct;
/* 454 */             first = false;
/*     */           }
/*     */           else {
/* 457 */             subcode = fillSubcodes(subcode, value);
/*     */           }
/*     */         }
/*     */       }
/* 460 */       faultString = soapFaultException.getFault().getFaultString();
/* 461 */       faultRole = soapFaultException.getFault().getFaultActor();
/* 462 */       faultNode = soapFaultException.getFault().getFaultNode();
/*     */     }
/*     */ 
/* 465 */     if (faultCode == null) {
/* 466 */       faultCode = getDefaultFaultCode(soapVersion);
/* 467 */       code = new CodeType(faultCode);
/* 468 */     } else if (code == null) {
/* 469 */       code = new CodeType(faultCode);
/*     */     }
/*     */ 
/* 472 */     if (faultString == null) {
/* 473 */       faultString = e.getMessage();
/* 474 */       if (faultString == null) {
/* 475 */         faultString = e.toString();
/*     */       }
/*     */     }
/*     */ 
/* 479 */     ReasonType reason = new ReasonType(faultString);
/* 480 */     Element detailNode = null;
/* 481 */     QName firstEntry = null;
/* 482 */     if ((detail == null) && (soapFaultException != null)) {
/* 483 */       detailNode = soapFaultException.getFault().getDetail();
/* 484 */       firstEntry = getFirstDetailEntryName((Detail)detailNode);
/* 485 */     } else if (detail != null) {
/*     */       try {
/* 487 */         DOMResult dr = new DOMResult();
/* 488 */         ce.getBridge().marshal(detail, dr);
/* 489 */         detailNode = (Element)dr.getNode().getFirstChild();
/* 490 */         firstEntry = getFirstDetailEntryName(detailNode);
/*     */       }
/*     */       catch (JAXBException e1) {
/* 493 */         faultString = e.getMessage();
/* 494 */         faultCode = getDefaultFaultCode(soapVersion);
/*     */       }
/*     */     }
/*     */ 
/* 498 */     SOAP12Fault soap12Fault = new SOAP12Fault(code, reason, faultNode, faultRole, detailNode);
/*     */ 
/* 501 */     if (ce == null) {
/* 502 */       soap12Fault.captureStackTrace(e);
/*     */     }
/* 504 */     Message msg = JAXBMessage.create(JAXB_CONTEXT, soap12Fault, soapVersion);
/* 505 */     return new FaultMessage(msg, firstEntry);
/*     */   }
/*     */ 
/*     */   private static SubcodeType fillSubcodes(SubcodeType parent, QName value) {
/* 509 */     SubcodeType newCode = new SubcodeType(value);
/* 510 */     parent.setSubcode(newCode);
/* 511 */     return newCode;
/*     */   }
/*     */ 
/*     */   private static QName getDefaultFaultCode(SOAPVersion soapVersion) {
/* 515 */     return soapVersion.faultCodeServer;
/*     */   }
/*     */ 
/*     */   public static SOAPFaultBuilder create(Message msg)
/*     */     throws JAXBException
/*     */   {
/* 525 */     return (SOAPFaultBuilder)msg.readPayloadAsJAXB(JAXB_CONTEXT.createUnmarshaller());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 544 */       captureStackTrace = Boolean.getBoolean(CAPTURE_STACK_TRACE_PROPERTY);
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/*     */     }
/*     */     try {
/* 550 */       JAXB_CONTEXT = (JAXBRIContext)JAXBContext.newInstance(new Class[] { SOAP11Fault.class, SOAP12Fault.class });
/*     */     } catch (JAXBException e) {
/* 552 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.fault.SOAPFaultBuilder
 * JD-Core Version:    0.6.2
 */