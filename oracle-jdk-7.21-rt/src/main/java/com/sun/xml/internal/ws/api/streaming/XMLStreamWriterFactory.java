/*     */ package com.sun.xml.internal.ws.api.streaming;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.ws.streaming.XMLReaderException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamWriter;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.ws.WebServiceException;
/*     */ 
/*     */ public abstract class XMLStreamWriterFactory
/*     */ {
/*  55 */   private static final Logger LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
/*     */ 
/*     */   @NotNull
/*     */   private static volatile XMLStreamWriterFactory theInstance;
/*     */ 
/*     */   public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream);
/*     */ 
/*     */   public abstract XMLStreamWriter doCreate(OutputStream paramOutputStream, String paramString);
/*     */ 
/*     */   public abstract void doRecycle(XMLStreamWriter paramXMLStreamWriter);
/*     */ 
/*     */   public static void recycle(XMLStreamWriter r)
/*     */   {
/* 136 */     get().doRecycle(r);
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public static XMLStreamWriterFactory get()
/*     */   {
/* 155 */     return theInstance;
/*     */   }
/*     */ 
/*     */   public static void set(@NotNull XMLStreamWriterFactory f)
/*     */   {
/* 166 */     if (f == null) throw new IllegalArgumentException();
/* 167 */     theInstance = f;
/*     */   }
/*     */ 
/*     */   public static XMLStreamWriter create(OutputStream out)
/*     */   {
/* 174 */     return get().doCreate(out);
/*     */   }
/*     */ 
/*     */   public static XMLStreamWriter create(OutputStream out, String encoding) {
/* 178 */     return get().doCreate(out, encoding);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static XMLStreamWriter createXMLStreamWriter(OutputStream out)
/*     */   {
/* 186 */     return create(out);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding)
/*     */   {
/* 194 */     return create(out, encoding);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding, boolean declare)
/*     */   {
/* 202 */     return create(out, encoding);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  64 */     XMLOutputFactory xof = null;
/*  65 */     if (Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".woodstox"))
/*     */       try {
/*  67 */         xof = (XMLOutputFactory)Class.forName("com.ctc.wstx.stax.WstxOutputFactory").newInstance();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*  72 */     if (xof == null) {
/*  73 */       xof = XMLOutputFactory.newInstance();
/*     */     }
/*     */ 
/*  76 */     XMLStreamWriterFactory f = null;
/*     */ 
/*  80 */     if (!Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".noPool"))
/*  81 */       f = Zephyr.newInstance(xof);
/*  82 */     if (f == null)
/*     */     {
/*  84 */       if (xof.getClass().getName().equals("com.ctc.wstx.stax.WstxOutputFactory"))
/*  85 */         f = new NoLock(xof);
/*     */     }
/*  87 */     if (f == null) {
/*  88 */       f = new Default(xof);
/*     */     }
/*  90 */     theInstance = f;
/*  91 */     LOGGER.fine("XMLStreamWriterFactory instance is = " + theInstance);
/*     */   }
/*     */ 
/*     */   public static final class Default extends XMLStreamWriterFactory
/*     */   {
/*     */     private final XMLOutputFactory xof;
/*     */ 
/*     */     public Default(XMLOutputFactory xof)
/*     */     {
/* 217 */       this.xof = xof;
/*     */     }
/*     */ 
/*     */     public XMLStreamWriter doCreate(OutputStream out) {
/* 221 */       return doCreate(out, "UTF-8");
/*     */     }
/*     */ 
/*     */     public synchronized XMLStreamWriter doCreate(OutputStream out, String encoding)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 51	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$Default:xof	Ljavax/xml/stream/XMLOutputFactory;
/*     */       //   4: aload_1
/*     */       //   5: aload_2
/*     */       //   6: invokevirtual 55	javax/xml/stream/XMLOutputFactory:createXMLStreamWriter	(Ljava/io/OutputStream;Ljava/lang/String;)Ljavax/xml/stream/XMLStreamWriter;
/*     */       //   9: areturn
/*     */       //   10: astore_3
/*     */       //   11: new 32	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   14: dup
/*     */       //   15: ldc 2
/*     */       //   17: iconst_1
/*     */       //   18: anewarray 33	java/lang/Object
/*     */       //   21: dup
/*     */       //   22: iconst_0
/*     */       //   23: aload_3
/*     */       //   24: aastore
/*     */       //   25: invokespecial 54	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   28: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	9	10	javax/xml/stream/XMLStreamException
/*     */     }
/*     */ 
/*     */     public void doRecycle(XMLStreamWriter r)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class NoLock extends XMLStreamWriterFactory
/*     */   {
/*     */     private final XMLOutputFactory xof;
/*     */ 
/*     */     public NoLock(XMLOutputFactory xof)
/*     */     {
/* 333 */       this.xof = xof;
/*     */     }
/*     */ 
/*     */     public XMLStreamWriter doCreate(OutputStream out) {
/* 337 */       return doCreate(out, "UTF-8");
/*     */     }
/*     */ 
/*     */     public XMLStreamWriter doCreate(OutputStream out, String encoding)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 50	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$NoLock:xof	Ljavax/xml/stream/XMLOutputFactory;
/*     */       //   4: aload_1
/*     */       //   5: aload_2
/*     */       //   6: invokevirtual 54	javax/xml/stream/XMLOutputFactory:createXMLStreamWriter	(Ljava/io/OutputStream;Ljava/lang/String;)Ljavax/xml/stream/XMLStreamWriter;
/*     */       //   9: areturn
/*     */       //   10: astore_3
/*     */       //   11: new 31	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   14: dup
/*     */       //   15: ldc 2
/*     */       //   17: iconst_1
/*     */       //   18: anewarray 32	java/lang/Object
/*     */       //   21: dup
/*     */       //   22: iconst_0
/*     */       //   23: aload_3
/*     */       //   24: aastore
/*     */       //   25: invokespecial 53	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   28: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   0	9	10	javax/xml/stream/XMLStreamException
/*     */     }
/*     */ 
/*     */     public void doRecycle(XMLStreamWriter r)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface RecycleAware
/*     */   {
/*     */     public abstract void onRecycled();
/*     */   }
/*     */ 
/*     */   public static final class Zephyr extends XMLStreamWriterFactory
/*     */   {
/*     */     private final XMLOutputFactory xof;
/* 245 */     private final ThreadLocal<XMLStreamWriter> pool = new ThreadLocal();
/*     */     private final Method resetMethod;
/*     */     private final Method setOutputMethod;
/*     */     private final Class zephyrClass;
/*     */ 
/*     */     public static XMLStreamWriterFactory newInstance(XMLOutputFactory xof)
/*     */     {
/*     */       try
/*     */       {
/* 252 */         Class clazz = xof.createXMLStreamWriter(new StringWriter()).getClass();
/*     */ 
/* 254 */         if (!clazz.getName().startsWith("com.sun.xml.internal.stream.")) {
/* 255 */           return null;
/*     */         }
/* 257 */         return new Zephyr(xof, clazz);
/*     */       } catch (XMLStreamException e) {
/* 259 */         return null; } catch (NoSuchMethodException e) {
/*     */       }
/* 261 */       return null;
/*     */     }
/*     */ 
/*     */     private Zephyr(XMLOutputFactory xof, Class clazz) throws NoSuchMethodException
/*     */     {
/* 266 */       this.xof = xof;
/*     */ 
/* 268 */       this.zephyrClass = clazz;
/* 269 */       this.setOutputMethod = clazz.getMethod("setOutput", new Class[] { StreamResult.class, String.class });
/* 270 */       this.resetMethod = clazz.getMethod("reset", new Class[0]);
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     private XMLStreamWriter fetch()
/*     */     {
/* 277 */       XMLStreamWriter sr = (XMLStreamWriter)this.pool.get();
/* 278 */       if (sr == null) return null;
/* 279 */       this.pool.set(null);
/* 280 */       return sr;
/*     */     }
/*     */ 
/*     */     public XMLStreamWriter doCreate(OutputStream out) {
/* 284 */       return doCreate(out, "UTF-8"); } 
/*     */     public XMLStreamWriter doCreate(OutputStream out, String encoding) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: invokespecial 152	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$Zephyr:fetch	()Ljavax/xml/stream/XMLStreamWriter;
/*     */       //   4: astore_3
/*     */       //   5: aload_3
/*     */       //   6: ifnull +88 -> 94
/*     */       //   9: aload_0
/*     */       //   10: getfield 148	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$Zephyr:resetMethod	Ljava/lang/reflect/Method;
/*     */       //   13: aload_3
/*     */       //   14: iconst_0
/*     */       //   15: anewarray 89	java/lang/Object
/*     */       //   18: invokevirtual 165	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
/*     */       //   21: pop
/*     */       //   22: aload_0
/*     */       //   23: getfield 149	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$Zephyr:setOutputMethod	Ljava/lang/reflect/Method;
/*     */       //   26: aload_3
/*     */       //   27: iconst_2
/*     */       //   28: anewarray 89	java/lang/Object
/*     */       //   31: dup
/*     */       //   32: iconst_0
/*     */       //   33: new 97	javax/xml/transform/stream/StreamResult
/*     */       //   36: dup
/*     */       //   37: aload_1
/*     */       //   38: invokespecial 168	javax/xml/transform/stream/StreamResult:<init>	(Ljava/io/OutputStream;)V
/*     */       //   41: aastore
/*     */       //   42: dup
/*     */       //   43: iconst_1
/*     */       //   44: aload_2
/*     */       //   45: aastore
/*     */       //   46: invokevirtual 165	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
/*     */       //   49: pop
/*     */       //   50: aload_3
/*     */       //   51: areturn
/*     */       //   52: astore 4
/*     */       //   54: new 83	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   57: dup
/*     */       //   58: ldc 5
/*     */       //   60: iconst_1
/*     */       //   61: anewarray 89	java/lang/Object
/*     */       //   64: dup
/*     */       //   65: iconst_0
/*     */       //   66: aload 4
/*     */       //   68: aastore
/*     */       //   69: invokespecial 155	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   72: athrow
/*     */       //   73: astore 4
/*     */       //   75: new 83	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   78: dup
/*     */       //   79: ldc 5
/*     */       //   81: iconst_1
/*     */       //   82: anewarray 89	java/lang/Object
/*     */       //   85: dup
/*     */       //   86: iconst_0
/*     */       //   87: aload 4
/*     */       //   89: aastore
/*     */       //   90: invokespecial 155	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   93: athrow
/*     */       //   94: aload_0
/*     */       //   95: getfield 150	com/sun/xml/internal/ws/api/streaming/XMLStreamWriterFactory$Zephyr:xof	Ljavax/xml/stream/XMLOutputFactory;
/*     */       //   98: aload_1
/*     */       //   99: aload_2
/*     */       //   100: invokevirtual 167	javax/xml/stream/XMLOutputFactory:createXMLStreamWriter	(Ljava/io/OutputStream;Ljava/lang/String;)Ljavax/xml/stream/XMLStreamWriter;
/*     */       //   103: areturn
/*     */       //   104: astore 4
/*     */       //   106: new 83	com/sun/xml/internal/ws/streaming/XMLReaderException
/*     */       //   109: dup
/*     */       //   110: ldc 5
/*     */       //   112: iconst_1
/*     */       //   113: anewarray 89	java/lang/Object
/*     */       //   116: dup
/*     */       //   117: iconst_0
/*     */       //   118: aload 4
/*     */       //   120: aastore
/*     */       //   121: invokespecial 155	com/sun/xml/internal/ws/streaming/XMLReaderException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
/*     */       //   124: athrow
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   9	51	52	java/lang/IllegalAccessException
/*     */       //   9	51	73	java/lang/reflect/InvocationTargetException
/*     */       //   94	103	104	javax/xml/stream/XMLStreamException } 
/* 311 */     public void doRecycle(XMLStreamWriter r) { if (this.zephyrClass.isInstance(r))
/*     */       {
/*     */         try {
/* 314 */           r.close();
/*     */         } catch (XMLStreamException e) {
/* 316 */           throw new WebServiceException(e);
/*     */         }
/* 318 */         this.pool.set(r);
/*     */       }
/* 320 */       if ((r instanceof XMLStreamWriterFactory.RecycleAware))
/* 321 */         ((XMLStreamWriterFactory.RecycleAware)r).onRecycled();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory
 * JD-Core Version:    0.6.2
 */