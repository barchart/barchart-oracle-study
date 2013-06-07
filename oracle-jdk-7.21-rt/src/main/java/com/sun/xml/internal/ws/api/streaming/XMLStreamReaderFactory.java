/*     */ package com.sun.xml.internal.ws.api.streaming;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.ws.streaming.XMLReaderException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public abstract class XMLStreamReaderFactory
/*     */ {
/*  59 */   private static final Logger LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
/*     */ 
/*     */   @NotNull
/*     */   private static volatile XMLStreamReaderFactory theInstance;
/*     */ 
/*     */   private static XMLInputFactory getXMLInputFactory()
/*     */   {
/*  89 */     XMLInputFactory xif = null;
/*  90 */     if (getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox").booleanValue())
/*     */       try {
/*  92 */         xif = (XMLInputFactory)Class.forName("com.ctc.wstx.stax.WstxInputFactory").newInstance();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*  97 */     if (xif == null) {
/*  98 */       xif = XMLInputFactory.newInstance();
/*     */     }
/* 100 */     xif.setProperty("javax.xml.stream.isNamespaceAware", Boolean.valueOf(true));
/* 101 */     xif.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
/* 102 */     return xif;
/*     */   }
/*     */ 
/*     */   public static void set(XMLStreamReaderFactory f)
/*     */   {
/* 111 */     if (f == null) throw new IllegalArgumentException();
/* 112 */     theInstance = f;
/*     */   }
/*     */ 
/*     */   public static XMLStreamReaderFactory get() {
/* 116 */     return theInstance; } 
/*     */   public static XMLStreamReader create(InputSource source, boolean rejectDTDs) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 234	org/xml/sax/InputSource:getCharacterStream	()Ljava/io/Reader;
/*     */     //   4: ifnull +19 -> 23
/*     */     //   7: invokestatic 199	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:get	()Lcom/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory;
/*     */     //   10: aload_0
/*     */     //   11: invokevirtual 235	org/xml/sax/InputSource:getSystemId	()Ljava/lang/String;
/*     */     //   14: aload_0
/*     */     //   15: invokevirtual 234	org/xml/sax/InputSource:getCharacterStream	()Ljava/io/Reader;
/*     */     //   18: iload_1
/*     */     //   19: invokevirtual 205	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:doCreate	(Ljava/lang/String;Ljava/io/Reader;Z)Ljavax/xml/stream/XMLStreamReader;
/*     */     //   22: areturn
/*     */     //   23: aload_0
/*     */     //   24: invokevirtual 233	org/xml/sax/InputSource:getByteStream	()Ljava/io/InputStream;
/*     */     //   27: ifnull +19 -> 46
/*     */     //   30: invokestatic 199	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:get	()Lcom/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory;
/*     */     //   33: aload_0
/*     */     //   34: invokevirtual 235	org/xml/sax/InputSource:getSystemId	()Ljava/lang/String;
/*     */     //   37: aload_0
/*     */     //   38: invokevirtual 233	org/xml/sax/InputSource:getByteStream	()Ljava/io/InputStream;
/*     */     //   41: iload_1
/*     */     //   42: invokevirtual 204	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:doCreate	(Ljava/lang/String;Ljava/io/InputStream;Z)Ljavax/xml/stream/XMLStreamReader;
/*     */     //   45: areturn
/*     */     //   46: invokestatic 199	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:get	()Lcom/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory;
/*     */     //   49: aload_0
/*     */     //   50: invokevirtual 235	org/xml/sax/InputSource:getSystemId	()Ljava/lang/String;
/*     */     //   53: new 120	java/net/URL
/*     */     //   56: dup
/*     */     //   57: aload_0
/*     */     //   58: invokevirtual 235	org/xml/sax/InputSource:getSystemId	()Ljava/lang/String;
/*     */     //   61: invokespecial 227	java/net/URL:<init>	(Ljava/lang/String;)V
/*     */     //   64: invokevirtual 226	java/net/URL:openStream	()Ljava/io/InputStream;
/*     */     //   67: iload_1
/*     */     //   68: invokevirtual 204	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory:doCreate	(Ljava/lang/String;Ljava/io/InputStream;Z)Ljavax/xml/stream/XMLStreamReader;
/*     */     //   71: areturn
/*     */     //   72: astore_2
/*     */     //   73: new 108	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */     //   76: dup
/*     */     //   77: ldc 7
/*     */     //   79: iconst_1
/*     */     //   80: anewarray 117	java/lang/Object
/*     */     //   83: dup
/*     */     //   84: iconst_0
/*     */     //   85: aload_2
/*     */     //   86: aastore
/*     */     //   87: invokespecial 211	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */     //   90: athrow
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	22	72	java/io/IOException
/*     */     //   23	45	72	java/io/IOException
/*     */     //   46	71	72	java/io/IOException } 
/* 139 */   public static XMLStreamReader create(@Nullable String systemId, InputStream in, boolean rejectDTDs) { return get().doCreate(systemId, in, rejectDTDs); }
/*     */ 
/*     */   public static XMLStreamReader create(@Nullable String systemId, InputStream in, @Nullable String encoding, boolean rejectDTDs)
/*     */   {
/* 143 */     return encoding == null ? create(systemId, in, rejectDTDs) : get().doCreate(systemId, in, encoding, rejectDTDs);
/*     */   }
/*     */ 
/*     */   public static XMLStreamReader create(@Nullable String systemId, Reader reader, boolean rejectDTDs)
/*     */   {
/* 149 */     return get().doCreate(systemId, reader, rejectDTDs);
/*     */   }
/*     */ 
/*     */   public static void recycle(XMLStreamReader r)
/*     */   {
/* 176 */     get().doRecycle(r);
/* 177 */     if ((r instanceof RecycleAware))
/* 178 */       ((RecycleAware)r).onRecycled();
/*     */   }
/*     */ 
/*     */   public abstract XMLStreamReader doCreate(String paramString, InputStream paramInputStream, boolean paramBoolean);
/*     */ 
/*     */   private XMLStreamReader doCreate(String systemId, InputStream in, @NotNull String encoding, boolean rejectDTDs)
/*     */   {
/*     */     Reader reader;
/*     */     try
/*     */     {
/* 189 */       reader = new InputStreamReader(in, encoding);
/*     */     } catch (UnsupportedEncodingException ue) {
/* 191 */       throw new XMLReaderException("stax.cantCreate", new Object[] { ue });
/*     */     }
/* 193 */     return doCreate(systemId, reader, rejectDTDs);
/*     */   }
/*     */ 
/*     */   public abstract XMLStreamReader doCreate(String paramString, Reader paramReader, boolean paramBoolean);
/*     */ 
/*     */   public abstract void doRecycle(XMLStreamReader paramXMLStreamReader);
/*     */ 
/*     */   private static Boolean getProperty(String prop)
/*     */   {
/* 431 */     return (Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Boolean run() {
/* 434 */         String value = System.getProperty(this.val$prop);
/* 435 */         return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  67 */     XMLInputFactory xif = getXMLInputFactory();
/*  68 */     XMLStreamReaderFactory f = null;
/*     */ 
/*  72 */     if (!getProperty(XMLStreamReaderFactory.class.getName() + ".noPool").booleanValue()) {
/*  73 */       f = Zephyr.newInstance(xif);
/*     */     }
/*  75 */     if (f == null)
/*     */     {
/*  77 */       if (xif.getClass().getName().equals("com.ctc.wstx.stax.WstxInputFactory")) {
/*  78 */         f = new Woodstox(xif);
/*     */       }
/*     */     }
/*  81 */     if (f == null) {
/*  82 */       f = new Default();
/*     */     }
/*  84 */     theInstance = f;
/*  85 */     LOGGER.fine("XMLStreamReaderFactory instance is = " + theInstance);
/*     */   }
/*     */ 
/*     */   public static final class Default extends XMLStreamReaderFactory
/*     */   {
/* 348 */     private final ThreadLocal<XMLInputFactory> xif = new ThreadLocal()
/*     */     {
/*     */       public XMLInputFactory initialValue() {
/* 351 */         return XMLStreamReaderFactory.access$000();
/*     */       }
/* 348 */     };
/*     */ 
/*     */     public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 64	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Default:xif	Ljava/lang/ThreadLocal;
/*     */       //   4: invokevirtual 68	java/lang/ThreadLocal:get	()Ljava/lang/Object;
/*     */       //   7: checkcast 40	javax/xml/stream/XMLInputFactory
/*     */       //   10: aload_1
/*     */       //   11: aload_2
/*     */       //   12: invokevirtual 69	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/InputStream;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   15: areturn
/*     */       //   16: astore 4
/*     */       //   18: new 37	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   21: dup
/*     */       //   22: ldc 1
/*     */       //   24: iconst_1
/*     */       //   25: anewarray 38	java/lang/Object
/*     */       //   28: dup
/*     */       //   29: iconst_0
/*     */       //   30: aload 4
/*     */       //   32: aastore
/*     */       //   33: invokespecial 67	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   36: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	15	16	javax/xml/stream/XMLStreamException } 
/*     */     public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 64	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Default:xif	Ljava/lang/ThreadLocal;
/*     */       //   4: invokevirtual 68	java/lang/ThreadLocal:get	()Ljava/lang/Object;
/*     */       //   7: checkcast 40	javax/xml/stream/XMLInputFactory
/*     */       //   10: aload_1
/*     */       //   11: aload_2
/*     */       //   12: invokevirtual 70	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/Reader;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   15: areturn
/*     */       //   16: astore 4
/*     */       //   18: new 37	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   21: dup
/*     */       //   22: ldc 1
/*     */       //   24: iconst_1
/*     */       //   25: anewarray 38	java/lang/Object
/*     */       //   28: dup
/*     */       //   29: iconst_0
/*     */       //   30: aload 4
/*     */       //   32: aastore
/*     */       //   33: invokespecial 67	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   36: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	15	16	javax/xml/stream/XMLStreamException } 
/*     */     public void doRecycle(XMLStreamReader r) {  }  } 
/*     */   public static class NoLock extends XMLStreamReaderFactory { private final XMLInputFactory xif;
/*     */ 
/* 387 */     public NoLock(XMLInputFactory xif) { this.xif = xif; } 
/*     */     public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 53	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$NoLock:xif	Ljavax/xml/stream/XMLInputFactory;
/*     */       //   4: aload_1
/*     */       //   5: aload_2
/*     */       //   6: invokevirtual 56	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/InputStream;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   9: areturn
/*     */       //   10: astore 4
/*     */       //   12: new 31	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   15: dup
/*     */       //   16: ldc 1
/*     */       //   18: iconst_1
/*     */       //   19: anewarray 32	java/lang/Object
/*     */       //   22: dup
/*     */       //   23: iconst_0
/*     */       //   24: aload 4
/*     */       //   26: aastore
/*     */       //   27: invokespecial 55	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   30: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	9	10	javax/xml/stream/XMLStreamException } 
/*     */     public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 53	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$NoLock:xif	Ljavax/xml/stream/XMLInputFactory;
/*     */       //   4: aload_1
/*     */       //   5: aload_2
/*     */       //   6: invokevirtual 57	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/Reader;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   9: areturn
/*     */       //   10: astore 4
/*     */       //   12: new 31	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   15: dup
/*     */       //   16: ldc 1
/*     */       //   18: iconst_1
/*     */       //   19: anewarray 32	java/lang/Object
/*     */       //   22: dup
/*     */       //   23: iconst_0
/*     */       //   24: aload 4
/*     */       //   26: aastore
/*     */       //   27: invokespecial 55	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   30: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	9	10	javax/xml/stream/XMLStreamException } 
/*     */     public void doRecycle(XMLStreamReader r) {  }  } 
/*     */   public static abstract interface RecycleAware { public abstract void onRecycled(); } 
/* 417 */   public static final class Woodstox extends XMLStreamReaderFactory.NoLock { public Woodstox(XMLInputFactory xif) { super();
/* 418 */       xif.setProperty("org.codehaus.stax2.internNsUris", Boolean.valueOf(true)); }
/*     */ 
/*     */     public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs)
/*     */     {
/* 422 */       return super.doCreate(systemId, in, rejectDTDs);
/*     */     }
/*     */ 
/*     */     public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
/* 426 */       return super.doCreate(systemId, in, rejectDTDs);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Zephyr extends XMLStreamReaderFactory
/*     */   {
/*     */     private final XMLInputFactory xif;
/* 218 */     private final ThreadLocal<XMLStreamReader> pool = new ThreadLocal();
/*     */     private final Method setInputSourceMethod;
/*     */     private final Method resetMethod;
/*     */     private final Class zephyrClass;
/*     */ 
/*     */     @Nullable
/*     */     public static XMLStreamReaderFactory newInstance(XMLInputFactory xif)
/*     */     {
/*     */       try
/*     */       {
/* 243 */         Class clazz = xif.createXMLStreamReader(new StringReader("<foo/>")).getClass();
/*     */ 
/* 246 */         if (!clazz.getName().startsWith("com.sun.xml.internal.stream."))
/* 247 */           return null;
/* 248 */         return new Zephyr(xif, clazz);
/*     */       } catch (NoSuchMethodException e) {
/* 250 */         return null; } catch (XMLStreamException e) {
/*     */       }
/* 252 */       return null;
/*     */     }
/*     */ 
/*     */     public Zephyr(XMLInputFactory xif, Class clazz) throws NoSuchMethodException
/*     */     {
/* 257 */       this.zephyrClass = clazz;
/* 258 */       this.setInputSourceMethod = clazz.getMethod("setInputSource", new Class[] { InputSource.class });
/* 259 */       this.resetMethod = clazz.getMethod("reset", new Class[0]);
/*     */       try
/*     */       {
/* 264 */         xif.setProperty("reuse-instance", Boolean.valueOf(false));
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/*     */       }
/* 268 */       this.xif = xif;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     private XMLStreamReader fetch()
/*     */     {
/* 275 */       XMLStreamReader sr = (XMLStreamReader)this.pool.get();
/* 276 */       if (sr == null) return null;
/* 277 */       this.pool.set(null);
/* 278 */       return sr;
/*     */     }
/*     */ 
/*     */     public void doRecycle(XMLStreamReader r) {
/* 282 */       if (this.zephyrClass.isInstance(r))
/* 283 */         this.pool.set(r);  } 
/*     */     public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: invokespecial 180	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:fetch	()Ljavax/xml/stream/XMLStreamReader;
/*     */       //   4: astore 4
/*     */       //   6: aload 4
/*     */       //   8: ifnonnull +13 -> 21
/*     */       //   11: aload_0
/*     */       //   12: getfield 178	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:xif	Ljavax/xml/stream/XMLInputFactory;
/*     */       //   15: aload_1
/*     */       //   16: aload_2
/*     */       //   17: invokevirtual 198	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/InputStream;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   20: areturn
/*     */       //   21: new 112	org/xml/sax/InputSource
/*     */       //   24: dup
/*     */       //   25: aload_1
/*     */       //   26: invokespecial 202	org/xml/sax/InputSource:<init>	(Ljava/lang/String;)V
/*     */       //   29: astore 5
/*     */       //   31: aload 5
/*     */       //   33: aload_2
/*     */       //   34: invokevirtual 200	org/xml/sax/InputSource:setByteStream	(Ljava/io/InputStream;)V
/*     */       //   37: aload_0
/*     */       //   38: aload 4
/*     */       //   40: aload 5
/*     */       //   42: invokespecial 182	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:reuse	(Ljavax/xml/stream/XMLStreamReader;Lorg/xml/sax/InputSource;)V
/*     */       //   45: aload 4
/*     */       //   47: areturn
/*     */       //   48: astore 4
/*     */       //   50: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   53: dup
/*     */       //   54: ldc 6
/*     */       //   56: iconst_1
/*     */       //   57: anewarray 103	java/lang/Object
/*     */       //   60: dup
/*     */       //   61: iconst_0
/*     */       //   62: aload 4
/*     */       //   64: aastore
/*     */       //   65: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   68: athrow
/*     */       //   69: astore 4
/*     */       //   71: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   74: dup
/*     */       //   75: ldc 6
/*     */       //   77: iconst_1
/*     */       //   78: anewarray 103	java/lang/Object
/*     */       //   81: dup
/*     */       //   82: iconst_0
/*     */       //   83: aload 4
/*     */       //   85: aastore
/*     */       //   86: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   89: athrow
/*     */       //   90: astore 4
/*     */       //   92: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   95: dup
/*     */       //   96: ldc 6
/*     */       //   98: iconst_1
/*     */       //   99: anewarray 103	java/lang/Object
/*     */       //   102: dup
/*     */       //   103: iconst_0
/*     */       //   104: aload 4
/*     */       //   106: aastore
/*     */       //   107: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   110: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	20	48	java/lang/IllegalAccessException
/*     */       //   21	47	48	java/lang/IllegalAccessException
/*     */       //   0	20	69	java/lang/reflect/InvocationTargetException
/*     */       //   21	47	69	java/lang/reflect/InvocationTargetException
/*     */       //   0	20	90	javax/xml/stream/XMLStreamException
/*     */       //   21	47	90	javax/xml/stream/XMLStreamException } 
/*     */     public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: invokespecial 180	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:fetch	()Ljavax/xml/stream/XMLStreamReader;
/*     */       //   4: astore 4
/*     */       //   6: aload 4
/*     */       //   8: ifnonnull +13 -> 21
/*     */       //   11: aload_0
/*     */       //   12: getfield 178	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:xif	Ljavax/xml/stream/XMLInputFactory;
/*     */       //   15: aload_1
/*     */       //   16: aload_2
/*     */       //   17: invokevirtual 199	javax/xml/stream/XMLInputFactory:createXMLStreamReader	(Ljava/lang/String;Ljava/io/Reader;)Ljavax/xml/stream/XMLStreamReader;
/*     */       //   20: areturn
/*     */       //   21: new 112	org/xml/sax/InputSource
/*     */       //   24: dup
/*     */       //   25: aload_1
/*     */       //   26: invokespecial 202	org/xml/sax/InputSource:<init>	(Ljava/lang/String;)V
/*     */       //   29: astore 5
/*     */       //   31: aload 5
/*     */       //   33: aload_2
/*     */       //   34: invokevirtual 201	org/xml/sax/InputSource:setCharacterStream	(Ljava/io/Reader;)V
/*     */       //   37: aload_0
/*     */       //   38: aload 4
/*     */       //   40: aload 5
/*     */       //   42: invokespecial 182	com/sun/xml/internal/ws/api/streaming/XMLStreamReaderFactory$Zephyr:reuse	(Ljavax/xml/stream/XMLStreamReader;Lorg/xml/sax/InputSource;)V
/*     */       //   45: aload 4
/*     */       //   47: areturn
/*     */       //   48: astore 4
/*     */       //   50: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   53: dup
/*     */       //   54: ldc 6
/*     */       //   56: iconst_1
/*     */       //   57: anewarray 103	java/lang/Object
/*     */       //   60: dup
/*     */       //   61: iconst_0
/*     */       //   62: aload 4
/*     */       //   64: aastore
/*     */       //   65: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   68: athrow
/*     */       //   69: astore 4
/*     */       //   71: aload 4
/*     */       //   73: invokevirtual 194	java/lang/reflect/InvocationTargetException:getCause	()Ljava/lang/Throwable;
/*     */       //   76: astore 5
/*     */       //   78: aload 5
/*     */       //   80: ifnonnull +7 -> 87
/*     */       //   83: aload 4
/*     */       //   85: astore 5
/*     */       //   87: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   90: dup
/*     */       //   91: ldc 6
/*     */       //   93: iconst_1
/*     */       //   94: anewarray 103	java/lang/Object
/*     */       //   97: dup
/*     */       //   98: iconst_0
/*     */       //   99: aload 5
/*     */       //   101: aastore
/*     */       //   102: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   105: athrow
/*     */       //   106: astore 4
/*     */       //   108: new 94	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   111: dup
/*     */       //   112: ldc 6
/*     */       //   114: iconst_1
/*     */       //   115: anewarray 103	java/lang/Object
/*     */       //   118: dup
/*     */       //   119: iconst_0
/*     */       //   120: aload 4
/*     */       //   122: aastore
/*     */       //   123: invokespecial 183	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   126: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	20	48	java/lang/IllegalAccessException
/*     */       //   21	47	48	java/lang/IllegalAccessException
/*     */       //   0	20	69	java/lang/reflect/InvocationTargetException
/*     */       //   21	47	69	java/lang/reflect/InvocationTargetException
/*     */       //   0	20	106	javax/xml/stream/XMLStreamException
/*     */       //   21	47	106	javax/xml/stream/XMLStreamException } 
/* 331 */     private void reuse(XMLStreamReader xsr, InputSource in) throws IllegalAccessException, InvocationTargetException { this.resetMethod.invoke(xsr, new Object[0]);
/* 332 */       this.setInputSourceMethod.invoke(xsr, new Object[] { in });
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory
 * JD-Core Version:    0.6.2
 */