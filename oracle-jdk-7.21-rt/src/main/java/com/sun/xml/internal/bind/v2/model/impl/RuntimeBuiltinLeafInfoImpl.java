/*      */ package com.sun.xml.internal.bind.v2.model.impl;
/*      */ 
/*      */ import com.sun.istack.internal.ByteArrayDataSource;
/*      */ import com.sun.xml.internal.bind.DatatypeConverterImpl;
/*      */ import com.sun.xml.internal.bind.WhiteSpaceProcessor;
/*      */ import com.sun.xml.internal.bind.api.AccessorException;
/*      */ import com.sun.xml.internal.bind.v2.TODO;
/*      */ import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
/*      */ import com.sun.xml.internal.bind.v2.runtime.Name;
/*      */ import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;
/*      */ import com.sun.xml.internal.bind.v2.runtime.Transducer;
/*      */ import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
/*      */ import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
/*      */ import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
/*      */ import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
/*      */ import com.sun.xml.internal.bind.v2.util.DataSourceSource;
/*      */ import java.awt.Component;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.MediaTracker;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Type;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.UUID;
/*      */ import javax.activation.DataHandler;
/*      */ import javax.activation.DataSource;
/*      */ import javax.activation.MimeType;
/*      */ import javax.activation.MimeTypeParseException;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ import javax.xml.bind.MarshalException;
/*      */ import javax.xml.bind.helpers.ValidationEventImpl;
/*      */ import javax.xml.datatype.DatatypeConfigurationException;
/*      */ import javax.xml.datatype.DatatypeConstants;
/*      */ import javax.xml.datatype.DatatypeFactory;
/*      */ import javax.xml.datatype.Duration;
/*      */ import javax.xml.datatype.XMLGregorianCalendar;
/*      */ import javax.xml.namespace.QName;
/*      */ import javax.xml.stream.XMLStreamException;
/*      */ import javax.xml.transform.Source;
/*      */ import javax.xml.transform.Transformer;
/*      */ import javax.xml.transform.TransformerException;
/*      */ import javax.xml.transform.stream.StreamResult;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public abstract class RuntimeBuiltinLeafInfoImpl<T> extends BuiltinLeafInfoImpl<Type, Class>
/*      */   implements RuntimeBuiltinLeafInfo, Transducer<T>
/*      */ {
/*  176 */   public static final Map<Type, RuntimeBuiltinLeafInfoImpl<?>> LEAVES = new HashMap();
/*      */   public static final RuntimeBuiltinLeafInfoImpl<String> STRING;
/*      */   private static final String DATE = "date";
/*      */   public static final List<RuntimeBuiltinLeafInfoImpl<?>> builtinBeanInfos;
/*      */   public static final String MAP_ANYURI_TO_URI = "mapAnyUriToUri";
/*      */   private static final DatatypeFactory datatypeFactory;
/*      */   private static final Map<QName, String> xmlGregorianCalendarFormatString;
/*      */   private static final Map<QName, Integer> xmlGregorianCalendarFieldRef;
/*      */ 
/*      */   private RuntimeBuiltinLeafInfoImpl(Class type, QName[] typeNames)
/*      */   {
/*  106 */     super(type, typeNames);
/*  107 */     LEAVES.put(type, this);
/*      */   }
/*      */ 
/*      */   public final Class getClazz() {
/*  111 */     return (Class)getType();
/*      */   }
/*      */ 
/*      */   public final Transducer getTransducer()
/*      */   {
/*  116 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean useNamespace() {
/*  120 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isDefault() {
/*  124 */     return true;
/*      */   }
/*      */ 
/*      */   public void declareNamespace(T o, XMLSerializer w) throws AccessorException {
/*      */   }
/*      */ 
/*      */   public QName getTypeName(T instance) {
/*  131 */     return null;
/*      */   }
/*      */ 
/*      */   private static QName createXS(String typeName)
/*      */   {
/*  179 */     return new QName("http://www.w3.org/2001/XMLSchema", typeName);
/*      */   }
/*      */ 
/*      */   private static byte[] decodeBase64(CharSequence text)
/*      */   {
/*  871 */     if ((text instanceof Base64Data)) {
/*  872 */       Base64Data base64Data = (Base64Data)text;
/*  873 */       return base64Data.getExact();
/*      */     }
/*  875 */     return DatatypeConverterImpl._parseBase64Binary(text.toString());
/*      */   }
/*      */ 
/*      */   private static DatatypeFactory init()
/*      */   {
/*      */     try
/*      */     {
/*  888 */       return DatatypeFactory.newInstance();
/*      */     } catch (DatatypeConfigurationException e) {
/*  890 */       throw new Error(Messages.FAILED_TO_INITIALE_DATATYPE_FACTORY.format(new Object[0]), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void checkXmlGregorianCalendarFieldRef(QName type, XMLGregorianCalendar cal) throws MarshalException
/*      */   {
/*  896 */     StringBuilder buf = new StringBuilder();
/*  897 */     int bitField = ((Integer)xmlGregorianCalendarFieldRef.get(type)).intValue();
/*  898 */     int l = 1;
/*  899 */     int pos = 0;
/*  900 */     while (bitField != 0) {
/*  901 */       int bit = bitField & 0x1;
/*  902 */       bitField >>>= 4;
/*  903 */       pos++;
/*      */ 
/*  905 */       if (bit == 1) {
/*  906 */         switch (pos) {
/*      */         case 1:
/*  908 */           if (cal.getSecond() == -2147483648)
/*  909 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_SEC); break;
/*      */         case 2:
/*  913 */           if (cal.getMinute() == -2147483648)
/*  914 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_MIN); break;
/*      */         case 3:
/*  918 */           if (cal.getHour() == -2147483648)
/*  919 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_HR); break;
/*      */         case 4:
/*  923 */           if (cal.getDay() == -2147483648)
/*  924 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_DAY); break;
/*      */         case 5:
/*  928 */           if (cal.getMonth() == -2147483648)
/*  929 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_MONTH); break;
/*      */         case 6:
/*  933 */           if (cal.getYear() == -2147483648) {
/*  934 */             buf.append("  ").append(Messages.XMLGREGORIANCALENDAR_YEAR);
/*      */           }
/*      */           break;
/*      */         case 7:
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  942 */     if (buf.length() > 0)
/*  943 */       throw new MarshalException(Messages.XMLGREGORIANCALENDAR_INVALID.format(new Object[] { type.getLocalPart() }) + buf.toString());
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  199 */     QName[] qnames = { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), System.getProperty("mapAnyUriToUri") == null ? new QName[] { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("anyURI"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), createXS("ENTITY") } : createXS("ENTITY") };
/*      */ 
/*  222 */     STRING = new StringImplImpl(String.class, qnames);
/*      */ 
/*  224 */     ArrayList secondaryList = new ArrayList();
/*      */ 
/*  242 */     secondaryList.add(new StringImpl(Character.class, new QName[] { createXS("unsignedShort") })
/*      */     {
/*      */       public Character parse(CharSequence text)
/*      */       {
/*  246 */         return Character.valueOf((char)DatatypeConverterImpl._parseInt(text));
/*      */       }
/*      */       public String print(Character v) {
/*  249 */         return Integer.toString(v.charValue());
/*      */       }
/*      */     });
/*  252 */     secondaryList.add(new StringImpl(Calendar.class, new QName[] { DatatypeConstants.DATETIME })
/*      */     {
/*      */       public Calendar parse(CharSequence text) {
/*  255 */         return DatatypeConverterImpl._parseDateTime(text.toString());
/*      */       }
/*      */       public String print(Calendar v) {
/*  258 */         return DatatypeConverterImpl._printDateTime(v);
/*      */       }
/*      */     });
/*  261 */     secondaryList.add(new StringImpl(GregorianCalendar.class, new QName[] { DatatypeConstants.DATETIME })
/*      */     {
/*      */       public GregorianCalendar parse(CharSequence text) {
/*  264 */         return DatatypeConverterImpl._parseDateTime(text.toString());
/*      */       }
/*      */       public String print(GregorianCalendar v) {
/*  267 */         return DatatypeConverterImpl._printDateTime(v);
/*      */       }
/*      */     });
/*  270 */     secondaryList.add(new StringImpl(Date.class, new QName[] { DatatypeConstants.DATETIME })
/*      */     {
/*      */       public Date parse(CharSequence text) {
/*  273 */         return DatatypeConverterImpl._parseDateTime(text.toString()).getTime();
/*      */       }
/*      */       public String print(Date v) {
/*  276 */         XMLSerializer xs = XMLSerializer.getInstance();
/*  277 */         QName type = xs.getSchemaType();
/*  278 */         GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
/*  279 */         cal.setTime(v);
/*  280 */         if ((type != null) && ("http://www.w3.org/2001/XMLSchema".equals(type.getNamespaceURI())) && ("date".equals(type.getLocalPart())))
/*      */         {
/*  282 */           return DatatypeConverterImpl._printDate(cal);
/*      */         }
/*  284 */         return DatatypeConverterImpl._printDateTime(cal);
/*      */       }
/*      */     });
/*  288 */     secondaryList.add(new StringImpl(File.class, new QName[] { createXS("string") })
/*      */     {
/*      */       public File parse(CharSequence text) {
/*  291 */         return new File(WhiteSpaceProcessor.trim(text).toString());
/*      */       }
/*      */       public String print(File v) {
/*  294 */         return v.getPath();
/*      */       }
/*      */     });
/*  297 */     secondaryList.add(new StringImpl(URL.class, new QName[] { createXS("anyURI") })
/*      */     {
/*      */       public URL parse(CharSequence text) throws SAXException {
/*  300 */         TODO.checkSpec("JSR222 Issue #42");
/*      */         try {
/*  302 */           return new URL(WhiteSpaceProcessor.trim(text).toString());
/*      */         } catch (MalformedURLException e) {
/*  304 */           UnmarshallingContext.getInstance().handleError(e);
/*  305 */         }return null;
/*      */       }
/*      */ 
/*      */       public String print(URL v) {
/*  309 */         return v.toExternalForm();
/*      */       }
/*      */     });
/*  312 */     if (System.getProperty("mapAnyUriToUri") == null) {
/*  313 */       secondaryList.add(new StringImpl(URI.class, new QName[] { createXS("string") })
/*      */       {
/*      */         public URI parse(CharSequence text) throws SAXException {
/*      */           try {
/*  317 */             return new URI(text.toString());
/*      */           } catch (URISyntaxException e) {
/*  319 */             UnmarshallingContext.getInstance().handleError(e);
/*  320 */           }return null;
/*      */         }
/*      */ 
/*      */         public String print(URI v)
/*      */         {
/*  325 */           return v.toString();
/*      */         }
/*      */       });
/*      */     }
/*  329 */     secondaryList.add(new StringImpl(Class.class, new QName[] { createXS("string") })
/*      */     {
/*      */       public Class parse(CharSequence text) throws SAXException {
/*  332 */         TODO.checkSpec("JSR222 Issue #42");
/*      */         try {
/*  334 */           String name = WhiteSpaceProcessor.trim(text).toString();
/*  335 */           ClassLoader cl = UnmarshallingContext.getInstance().classLoader;
/*  336 */           if (cl == null) {
/*  337 */             cl = Thread.currentThread().getContextClassLoader();
/*      */           }
/*  339 */           if (cl != null) {
/*  340 */             return cl.loadClass(name);
/*      */           }
/*  342 */           return Class.forName(name);
/*      */         } catch (ClassNotFoundException e) {
/*  344 */           UnmarshallingContext.getInstance().handleError(e);
/*  345 */         }return null;
/*      */       }
/*      */ 
/*      */       public String print(Class v) {
/*  349 */         return v.getName();
/*      */       }
/*      */     });
/*  357 */     secondaryList.add(new PcdataImpl(Image.class, new QName[] { createXS("base64Binary") })
/*      */     {
/*      */       public Image parse(CharSequence text)
/*      */         throws SAXException
/*      */       {
/*      */         try
/*      */         {
/*      */           InputStream is;
/*      */           InputStream is;
/*  362 */           if ((text instanceof Base64Data))
/*  363 */             is = ((Base64Data)text).getInputStream();
/*      */           else {
/*  365 */             is = new ByteArrayInputStream(RuntimeBuiltinLeafInfoImpl.decodeBase64(text));
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/*  372 */             return ImageIO.read(is);
/*      */           } finally {
/*  374 */             is.close();
/*      */           }
/*      */         } catch (IOException e) {
/*  377 */           UnmarshallingContext.getInstance().handleError(e);
/*  378 */         }return null;
/*      */       }
/*      */ 
/*      */       private BufferedImage convertToBufferedImage(Image image) throws IOException
/*      */       {
/*  383 */         if ((image instanceof BufferedImage)) {
/*  384 */           return (BufferedImage)image;
/*      */         }
/*      */ 
/*  387 */         MediaTracker tracker = new MediaTracker(new Component()
/*      */         {
/*      */         });
/*  388 */         tracker.addImage(image, 0);
/*      */         try {
/*  390 */           tracker.waitForAll();
/*      */         } catch (InterruptedException e) {
/*  392 */           throw new IOException(e.getMessage());
/*      */         }
/*  394 */         BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
/*      */ 
/*  399 */         Graphics g = bufImage.createGraphics();
/*  400 */         g.drawImage(image, 0, 0, null);
/*  401 */         return bufImage;
/*      */       }
/*      */ 
/*      */       public Base64Data print(Image v)
/*      */       {
/*  406 */         ByteArrayOutputStreamEx imageData = new ByteArrayOutputStreamEx();
/*  407 */         XMLSerializer xs = XMLSerializer.getInstance();
/*      */ 
/*  409 */         String mimeType = xs.getXMIMEContentType();
/*  410 */         if ((mimeType == null) || (mimeType.startsWith("image/*")))
/*      */         {
/*  416 */           mimeType = "image/png";
/*      */         }
/*      */         try {
/*  419 */           Iterator itr = ImageIO.getImageWritersByMIMEType(mimeType);
/*  420 */           if (itr.hasNext()) {
/*  421 */             ImageWriter w = (ImageWriter)itr.next();
/*  422 */             ImageOutputStream os = ImageIO.createImageOutputStream(imageData);
/*  423 */             w.setOutput(os);
/*  424 */             w.write(convertToBufferedImage(v));
/*  425 */             os.close();
/*  426 */             w.dispose();
/*      */           }
/*      */           else {
/*  429 */             xs.handleEvent(new ValidationEventImpl(1, Messages.NO_IMAGE_WRITER.format(new Object[] { mimeType }), xs.getCurrentLocation(null)));
/*      */ 
/*  434 */             throw new RuntimeException("no encoder for MIME type " + mimeType);
/*      */           }
/*      */         } catch (IOException e) {
/*  437 */           xs.handleError(e);
/*      */ 
/*  439 */           throw new RuntimeException(e);
/*      */         }
/*  441 */         Base64Data bd = new Base64Data();
/*  442 */         imageData.set(bd, mimeType);
/*  443 */         return bd;
/*      */       }
/*      */     });
/*  446 */     secondaryList.add(new PcdataImpl(DataHandler.class, new QName[] { createXS("base64Binary") })
/*      */     {
/*      */       public DataHandler parse(CharSequence text) {
/*  449 */         if ((text instanceof Base64Data)) {
/*  450 */           return ((Base64Data)text).getDataHandler();
/*      */         }
/*  452 */         return new DataHandler(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
/*      */       }
/*      */ 
/*      */       public Base64Data print(DataHandler v)
/*      */       {
/*  457 */         Base64Data bd = new Base64Data();
/*  458 */         bd.set(v);
/*  459 */         return bd;
/*      */       }
/*      */     });
/*  462 */     secondaryList.add(new PcdataImpl(Source.class, new QName[] { createXS("base64Binary") })
/*      */     {
/*      */       public Source parse(CharSequence text) throws SAXException {
/*      */         try {
/*  466 */           if ((text instanceof Base64Data)) {
/*  467 */             return new DataSourceSource(((Base64Data)text).getDataHandler());
/*      */           }
/*  469 */           return new DataSourceSource(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
/*      */         }
/*      */         catch (MimeTypeParseException e) {
/*  472 */           UnmarshallingContext.getInstance().handleError(e);
/*  473 */         }return null;
/*      */       }
/*      */ 
/*      */       public Base64Data print(Source v)
/*      */       {
/*  478 */         XMLSerializer xs = XMLSerializer.getInstance();
/*  479 */         Base64Data bd = new Base64Data();
/*      */ 
/*  481 */         String contentType = xs.getXMIMEContentType();
/*  482 */         MimeType mt = null;
/*  483 */         if (contentType != null) {
/*      */           try {
/*  485 */             mt = new MimeType(contentType);
/*      */           } catch (MimeTypeParseException e) {
/*  487 */             xs.handleError(e);
/*      */           }
/*      */         }
/*      */ 
/*  491 */         if ((v instanceof DataSourceSource))
/*      */         {
/*  494 */           DataSource ds = ((DataSourceSource)v).getDataSource();
/*      */ 
/*  496 */           String dsct = ds.getContentType();
/*  497 */           if ((dsct != null) && ((contentType == null) || (contentType.equals(dsct)))) {
/*  498 */             bd.set(new DataHandler(ds));
/*  499 */             return bd;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  506 */         String charset = null;
/*  507 */         if (mt != null)
/*  508 */           charset = mt.getParameter("charset");
/*  509 */         if (charset == null)
/*  510 */           charset = "UTF-8";
/*      */         try
/*      */         {
/*  513 */           ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx();
/*  514 */           Transformer tr = xs.getIdentityTransformer();
/*  515 */           String defaultEncoding = tr.getOutputProperty("encoding");
/*  516 */           tr.setOutputProperty("encoding", charset);
/*  517 */           tr.transform(v, new StreamResult(new OutputStreamWriter(baos, charset)));
/*  518 */           tr.setOutputProperty("encoding", defaultEncoding);
/*  519 */           baos.set(bd, "application/xml; charset=" + charset);
/*  520 */           return bd;
/*      */         }
/*      */         catch (TransformerException e) {
/*  523 */           xs.handleError(e);
/*      */         } catch (UnsupportedEncodingException e) {
/*  525 */           xs.handleError(e);
/*      */         }
/*      */ 
/*  529 */         bd.set(new byte[0], "application/xml");
/*  530 */         return bd;
/*      */       }
/*      */     });
/*  533 */     secondaryList.add(new StringImpl(XMLGregorianCalendar.class, new QName[] { createXS("anySimpleType"), DatatypeConstants.DATE, DatatypeConstants.DATETIME, DatatypeConstants.TIME, DatatypeConstants.GMONTH, DatatypeConstants.GDAY, DatatypeConstants.GYEAR, DatatypeConstants.GYEARMONTH, DatatypeConstants.GMONTHDAY })
/*      */     {
/*      */       public String print(XMLGregorianCalendar cal)
/*      */       {
/*  546 */         XMLSerializer xs = XMLSerializer.getInstance();
/*      */ 
/*  548 */         QName type = xs.getSchemaType();
/*  549 */         if (type != null) {
/*      */           try {
/*  551 */             RuntimeBuiltinLeafInfoImpl.checkXmlGregorianCalendarFieldRef(type, cal);
/*  552 */             String format = (String)RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString.get(type);
/*  553 */             if (format != null)
/*  554 */               return format(format, cal);
/*      */           }
/*      */           catch (MarshalException e)
/*      */           {
/*  558 */             xs.handleEvent(new ValidationEventImpl(0, e.getMessage(), xs.getCurrentLocation(null)));
/*      */ 
/*  560 */             return "";
/*      */           }
/*      */         }
/*  563 */         return cal.toXMLFormat();
/*      */       }
/*      */ 
/*      */       public XMLGregorianCalendar parse(CharSequence lexical) throws SAXException {
/*      */         try {
/*  568 */           return RuntimeBuiltinLeafInfoImpl.datatypeFactory.newXMLGregorianCalendar(lexical.toString().trim());
/*      */         } catch (Exception e) {
/*  570 */           UnmarshallingContext.getInstance().handleError(e);
/*  571 */         }return null;
/*      */       }
/*      */ 
/*      */       private String format(String format, XMLGregorianCalendar value)
/*      */       {
/*  577 */         StringBuilder buf = new StringBuilder();
/*  578 */         int fidx = 0; int flen = format.length();
/*      */ 
/*  580 */         while (fidx < flen) {
/*  581 */           char fch = format.charAt(fidx++);
/*  582 */           if (fch != '%') {
/*  583 */             buf.append(fch);
/*      */           }
/*      */           else
/*      */           {
/*  587 */             switch (format.charAt(fidx++)) {
/*      */             case 'Y':
/*  589 */               printNumber(buf, value.getEonAndYear(), 4);
/*  590 */               break;
/*      */             case 'M':
/*  592 */               printNumber(buf, value.getMonth(), 2);
/*  593 */               break;
/*      */             case 'D':
/*  595 */               printNumber(buf, value.getDay(), 2);
/*  596 */               break;
/*      */             case 'h':
/*  598 */               printNumber(buf, value.getHour(), 2);
/*  599 */               break;
/*      */             case 'm':
/*  601 */               printNumber(buf, value.getMinute(), 2);
/*  602 */               break;
/*      */             case 's':
/*  604 */               printNumber(buf, value.getSecond(), 2);
/*  605 */               if (value.getFractionalSecond() != null) {
/*  606 */                 String frac = value.getFractionalSecond().toPlainString();
/*      */ 
/*  608 */                 buf.append(frac.substring(1, frac.length()));
/*  609 */               }break;
/*      */             case 'z':
/*  612 */               int offset = value.getTimezone();
/*  613 */               if (offset == 0) {
/*  614 */                 buf.append('Z');
/*  615 */               } else if (offset != -2147483648) {
/*  616 */                 if (offset < 0) {
/*  617 */                   buf.append('-');
/*  618 */                   offset *= -1;
/*      */                 } else {
/*  620 */                   buf.append('+');
/*      */                 }
/*  622 */                 printNumber(buf, offset / 60, 2);
/*  623 */                 buf.append(':');
/*  624 */                 printNumber(buf, offset % 60, 2); } break;
/*      */             default:
/*  628 */               throw new InternalError();
/*      */             }
/*      */           }
/*      */         }
/*  632 */         return buf.toString();
/*      */       }
/*      */       private void printNumber(StringBuilder out, BigInteger number, int nDigits) {
/*  635 */         String s = number.toString();
/*  636 */         for (int i = s.length(); i < nDigits; i++)
/*  637 */           out.append('0');
/*  638 */         out.append(s);
/*      */       }
/*      */       private void printNumber(StringBuilder out, int number, int nDigits) {
/*  641 */         String s = String.valueOf(number);
/*  642 */         for (int i = s.length(); i < nDigits; i++)
/*  643 */           out.append('0');
/*  644 */         out.append(s);
/*      */       }
/*      */ 
/*      */       public QName getTypeName(XMLGregorianCalendar cal) {
/*  648 */         return cal.getXMLSchemaType();
/*      */       }
/*      */     });
/*  652 */     ArrayList primaryList = new ArrayList();
/*      */ 
/*  657 */     primaryList.add(STRING);
/*  658 */     primaryList.add(new StringImpl(Boolean.class, new QName[] { createXS("boolean") })
/*      */     {
/*      */       public Boolean parse(CharSequence text)
/*      */       {
/*  662 */         return DatatypeConverterImpl._parseBoolean(text);
/*      */       }
/*      */ 
/*      */       public String print(Boolean v) {
/*  666 */         return v.toString();
/*      */       }
/*      */     });
/*  669 */     primaryList.add(new PcdataImpl([B.class, new QName[] { createXS("base64Binary"), createXS("hexBinary") })
/*      */     {
/*      */       public byte[] parse(CharSequence text)
/*      */       {
/*  674 */         return RuntimeBuiltinLeafInfoImpl.decodeBase64(text);
/*      */       }
/*      */ 
/*      */       public Base64Data print(byte[] v) {
/*  678 */         XMLSerializer w = XMLSerializer.getInstance();
/*  679 */         Base64Data bd = new Base64Data();
/*  680 */         String mimeType = w.getXMIMEContentType();
/*  681 */         bd.set(v, mimeType);
/*  682 */         return bd;
/*      */       }
/*      */     });
/*  685 */     primaryList.add(new StringImpl(Byte.class, new QName[] { createXS("byte") })
/*      */     {
/*      */       public Byte parse(CharSequence text)
/*      */       {
/*  689 */         return Byte.valueOf(DatatypeConverterImpl._parseByte(text));
/*      */       }
/*      */ 
/*      */       public String print(Byte v) {
/*  693 */         return DatatypeConverterImpl._printByte(v.byteValue());
/*      */       }
/*      */     });
/*  696 */     primaryList.add(new StringImpl(Short.class, new QName[] { createXS("short"), createXS("unsignedByte") })
/*      */     {
/*      */       public Short parse(CharSequence text)
/*      */       {
/*  701 */         return Short.valueOf(DatatypeConverterImpl._parseShort(text));
/*      */       }
/*      */ 
/*      */       public String print(Short v) {
/*  705 */         return DatatypeConverterImpl._printShort(v.shortValue());
/*      */       }
/*      */     });
/*  708 */     primaryList.add(new StringImpl(Integer.class, new QName[] { createXS("int"), createXS("unsignedShort") })
/*      */     {
/*      */       public Integer parse(CharSequence text)
/*      */       {
/*  713 */         return Integer.valueOf(DatatypeConverterImpl._parseInt(text));
/*      */       }
/*      */ 
/*      */       public String print(Integer v) {
/*  717 */         return DatatypeConverterImpl._printInt(v.intValue());
/*      */       }
/*      */     });
/*  720 */     primaryList.add(new StringImpl(Long.class, new QName[] { createXS("long"), createXS("unsignedInt") })
/*      */     {
/*      */       public Long parse(CharSequence text)
/*      */       {
/*  726 */         return Long.valueOf(DatatypeConverterImpl._parseLong(text));
/*      */       }
/*      */ 
/*      */       public String print(Long v) {
/*  730 */         return DatatypeConverterImpl._printLong(v.longValue());
/*      */       }
/*      */     });
/*  733 */     primaryList.add(new StringImpl(Float.class, new QName[] { createXS("float") })
/*      */     {
/*      */       public Float parse(CharSequence text)
/*      */       {
/*  738 */         return Float.valueOf(DatatypeConverterImpl._parseFloat(text.toString()));
/*      */       }
/*      */ 
/*      */       public String print(Float v) {
/*  742 */         return DatatypeConverterImpl._printFloat(v.floatValue());
/*      */       }
/*      */     });
/*  745 */     primaryList.add(new StringImpl(Double.class, new QName[] { createXS("double") })
/*      */     {
/*      */       public Double parse(CharSequence text)
/*      */       {
/*  750 */         return Double.valueOf(DatatypeConverterImpl._parseDouble(text));
/*      */       }
/*      */ 
/*      */       public String print(Double v) {
/*  754 */         return DatatypeConverterImpl._printDouble(v.doubleValue());
/*      */       }
/*      */     });
/*  757 */     primaryList.add(new StringImpl(BigInteger.class, new QName[] { createXS("integer"), createXS("positiveInteger"), createXS("negativeInteger"), createXS("nonPositiveInteger"), createXS("nonNegativeInteger"), createXS("unsignedLong") })
/*      */     {
/*      */       public BigInteger parse(CharSequence text)
/*      */       {
/*  767 */         return DatatypeConverterImpl._parseInteger(text);
/*      */       }
/*      */ 
/*      */       public String print(BigInteger v) {
/*  771 */         return DatatypeConverterImpl._printInteger(v);
/*      */       }
/*      */     });
/*  774 */     primaryList.add(new StringImpl(BigDecimal.class, new QName[] { createXS("decimal") })
/*      */     {
/*      */       public BigDecimal parse(CharSequence text)
/*      */       {
/*  779 */         return DatatypeConverterImpl._parseDecimal(text.toString());
/*      */       }
/*      */ 
/*      */       public String print(BigDecimal v) {
/*  783 */         return DatatypeConverterImpl._printDecimal(v);
/*      */       }
/*      */     });
/*  786 */     primaryList.add(new StringImpl(QName.class, new QName[] { createXS("QName") })
/*      */     {
/*      */       public QName parse(CharSequence text) throws SAXException
/*      */       {
/*      */         try
/*      */         {
/*  792 */           return DatatypeConverterImpl._parseQName(text.toString(), UnmarshallingContext.getInstance());
/*      */         } catch (IllegalArgumentException e) {
/*  794 */           UnmarshallingContext.getInstance().handleError(e);
/*  795 */         }return null;
/*      */       }
/*      */ 
/*      */       public String print(QName v)
/*      */       {
/*  800 */         return DatatypeConverterImpl._printQName(v, XMLSerializer.getInstance().getNamespaceContext());
/*      */       }
/*      */ 
/*      */       public boolean useNamespace()
/*      */       {
/*  805 */         return true;
/*      */       }
/*      */ 
/*      */       public void declareNamespace(QName v, XMLSerializer w)
/*      */       {
/*  810 */         w.getNamespaceContext().declareNamespace(v.getNamespaceURI(), v.getPrefix(), false);
/*      */       }
/*      */     });
/*  813 */     if (System.getProperty("mapAnyUriToUri") != null) {
/*  814 */       primaryList.add(new StringImpl(URI.class, new QName[] { createXS("anyURI") })
/*      */       {
/*      */         public URI parse(CharSequence text) throws SAXException {
/*      */           try {
/*  818 */             return new URI(text.toString());
/*      */           } catch (URISyntaxException e) {
/*  820 */             UnmarshallingContext.getInstance().handleError(e);
/*  821 */           }return null;
/*      */         }
/*      */ 
/*      */         public String print(URI v)
/*      */         {
/*  826 */           return v.toString();
/*      */         }
/*      */       });
/*      */     }
/*  830 */     primaryList.add(new StringImpl(Duration.class, new QName[] { createXS("duration") })
/*      */     {
/*      */       public String print(Duration duration) {
/*  833 */         return duration.toString();
/*      */       }
/*      */ 
/*      */       public Duration parse(CharSequence lexical) {
/*  837 */         TODO.checkSpec("JSR222 Issue #42");
/*  838 */         return RuntimeBuiltinLeafInfoImpl.datatypeFactory.newDuration(lexical.toString());
/*      */       }
/*      */     });
/*  841 */     primaryList.add(new StringImpl(Void.class, new QName[0])
/*      */     {
/*      */       public String print(Void value)
/*      */       {
/*  847 */         return "";
/*      */       }
/*      */ 
/*      */       public Void parse(CharSequence lexical) {
/*  851 */         return null;
/*      */       }
/*      */     });
/*  855 */     List l = new ArrayList(secondaryList.size() + primaryList.size() + 1);
/*  856 */     l.addAll(secondaryList);
/*      */     try
/*      */     {
/*  860 */       l.add(new UUIDImpl());
/*      */     }
/*      */     catch (LinkageError e)
/*      */     {
/*      */     }
/*  865 */     l.addAll(primaryList);
/*      */ 
/*  867 */     builtinBeanInfos = Collections.unmodifiableList(l);
/*      */ 
/*  884 */     datatypeFactory = init();
/*      */ 
/*  952 */     xmlGregorianCalendarFormatString = new HashMap();
/*      */ 
/*  955 */     Map m = xmlGregorianCalendarFormatString;
/*      */ 
/*  957 */     m.put(DatatypeConstants.DATETIME, "%Y-%M-%DT%h:%m:%s%z");
/*  958 */     m.put(DatatypeConstants.DATE, "%Y-%M-%D%z");
/*  959 */     m.put(DatatypeConstants.TIME, "%h:%m:%s%z");
/*  960 */     m.put(DatatypeConstants.GMONTH, "--%M--%z");
/*  961 */     m.put(DatatypeConstants.GDAY, "---%D%z");
/*  962 */     m.put(DatatypeConstants.GYEAR, "%Y%z");
/*  963 */     m.put(DatatypeConstants.GYEARMONTH, "%Y-%M%z");
/*  964 */     m.put(DatatypeConstants.GMONTHDAY, "--%M-%D%z");
/*      */ 
/*  977 */     xmlGregorianCalendarFieldRef = new HashMap();
/*      */ 
/*  980 */     Map f = xmlGregorianCalendarFieldRef;
/*  981 */     f.put(DatatypeConstants.DATETIME, Integer.valueOf(17895697));
/*  982 */     f.put(DatatypeConstants.DATE, Integer.valueOf(17895424));
/*  983 */     f.put(DatatypeConstants.TIME, Integer.valueOf(16777489));
/*  984 */     f.put(DatatypeConstants.GDAY, Integer.valueOf(16781312));
/*  985 */     f.put(DatatypeConstants.GMONTH, Integer.valueOf(16842752));
/*  986 */     f.put(DatatypeConstants.GYEAR, Integer.valueOf(17825792));
/*  987 */     f.put(DatatypeConstants.GYEARMONTH, Integer.valueOf(17891328));
/*  988 */     f.put(DatatypeConstants.GMONTHDAY, Integer.valueOf(16846848));
/*      */   }
/*      */ 
/*      */   private static abstract class PcdataImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
/*      */   {
/*      */     protected PcdataImpl(Class type, QName[] typeNames)
/*      */     {
/*  158 */       super(typeNames, null);
/*      */     }
/*      */ 
/*      */     public abstract Pcdata print(T paramT) throws AccessorException;
/*      */ 
/*      */     public final void writeText(XMLSerializer w, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
/*  164 */       w.text(print(o), fieldName);
/*      */     }
/*      */ 
/*      */     public final void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
/*  168 */       w.leafElement(tagName, print(o), fieldName);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class StringImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
/*      */   {
/*      */     protected StringImpl(Class type, QName[] typeNames)
/*      */     {
/*  139 */       super(typeNames, null);
/*      */     }
/*      */ 
/*      */     public abstract String print(T paramT) throws AccessorException;
/*      */ 
/*      */     public void writeText(XMLSerializer w, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
/*  145 */       w.text(print(o), fieldName);
/*      */     }
/*      */ 
/*      */     public void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
/*  149 */       w.leafElement(tagName, print(o), fieldName);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class StringImplImpl extends RuntimeBuiltinLeafInfoImpl.StringImpl<String>
/*      */   {
/*      */     public StringImplImpl(Class type, QName[] typeNames)
/*      */     {
/* 1019 */       super(typeNames);
/*      */     }
/*      */ 
/*      */     public String parse(CharSequence text) {
/* 1023 */       return text.toString();
/*      */     }
/*      */ 
/*      */     public String print(String s) {
/* 1027 */       return s;
/*      */     }
/*      */ 
/*      */     public final void writeText(XMLSerializer w, String o, String fieldName) throws IOException, SAXException, XMLStreamException
/*      */     {
/* 1032 */       w.text(o, fieldName);
/*      */     }
/*      */ 
/*      */     public final void writeLeafElement(XMLSerializer w, Name tagName, String o, String fieldName) throws IOException, SAXException, XMLStreamException
/*      */     {
/* 1037 */       w.leafElement(tagName, o, fieldName);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UUIDImpl extends RuntimeBuiltinLeafInfoImpl.StringImpl<UUID>
/*      */   {
/*      */     public UUIDImpl()
/*      */     {
/*  998 */       super(new QName[] { RuntimeBuiltinLeafInfoImpl.createXS("string") });
/*      */     }
/*      */ 
/*      */     public UUID parse(CharSequence text) throws SAXException {
/* 1002 */       TODO.checkSpec("JSR222 Issue #42");
/*      */       try {
/* 1004 */         return UUID.fromString(WhiteSpaceProcessor.trim(text).toString());
/*      */       } catch (IllegalArgumentException e) {
/* 1006 */         UnmarshallingContext.getInstance().handleError(e);
/* 1007 */       }return null;
/*      */     }
/*      */ 
/*      */     public String print(UUID v)
/*      */     {
/* 1012 */       return v.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl
 * JD-Core Version:    0.6.2
 */