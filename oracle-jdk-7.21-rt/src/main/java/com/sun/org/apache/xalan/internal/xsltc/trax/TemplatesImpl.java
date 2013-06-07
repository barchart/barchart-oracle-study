/*     */ package com.sun.org.apache.xalan.internal.xsltc.trax;
/*     */ 
/*     */ import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
/*     */ import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.DOM;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.Translet;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
/*     */ import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Properties;
/*     */ import javax.xml.transform.Templates;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.URIResolver;
/*     */ 
/*     */ public final class TemplatesImpl
/*     */   implements Templates, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 673094361519270707L;
/*     */   public static final String DESERIALIZE_TRANSLET = "jdk.xml.enableTemplatesImplDeserialization";
/*  63 */   private static String ABSTRACT_TRANSLET = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
/*     */ 
/*  69 */   private String _name = null;
/*     */ 
/*  75 */   private byte[][] _bytecodes = (byte[][])null;
/*     */ 
/*  81 */   private Class[] _class = null;
/*     */ 
/*  87 */   private int _transletIndex = -1;
/*     */ 
/*  92 */   private Hashtable _auxClasses = null;
/*     */   private Properties _outputProperties;
/*     */   private int _indentNumber;
/* 108 */   private transient URIResolver _uriResolver = null;
/*     */ 
/* 117 */   private transient ThreadLocal _sdom = new ThreadLocal();
/*     */ 
/* 123 */   private transient TransformerFactoryImpl _tfactory = null;
/*     */   private boolean _useServicesMechanism;
/*     */ 
/*     */   protected TemplatesImpl(byte[][] bytecodes, String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory)
/*     */   {
/* 150 */     this._bytecodes = bytecodes;
/* 151 */     init(transletName, outputProperties, indentNumber, tfactory);
/*     */   }
/*     */ 
/*     */   protected TemplatesImpl(Class[] transletClasses, String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory)
/*     */   {
/* 161 */     this._class = transletClasses;
/* 162 */     this._transletIndex = 0;
/* 163 */     init(transletName, outputProperties, indentNumber, tfactory);
/*     */   }
/*     */ 
/*     */   private void init(String transletName, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory)
/*     */   {
/* 169 */     this._name = transletName;
/* 170 */     this._outputProperties = outputProperties;
/* 171 */     this._indentNumber = indentNumber;
/* 172 */     this._tfactory = tfactory;
/* 173 */     this._useServicesMechanism = tfactory.useServicesMechnism();
/*     */   }
/*     */ 
/*     */   public TemplatesImpl()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream is)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 192 */     SecurityManager security = System.getSecurityManager();
/* 193 */     if (security != null) {
/* 194 */       String temp = SecuritySupport.getSystemProperty("jdk.xml.enableTemplatesImplDeserialization");
/* 195 */       if ((temp == null) || ((temp.length() != 0) && (!temp.equalsIgnoreCase("true")))) {
/* 196 */         ErrorMsg err = new ErrorMsg("DESERIALIZE_TEMPLATES_ERR");
/* 197 */         throw new UnsupportedOperationException(err.toString());
/*     */       }
/*     */     }
/*     */ 
/* 201 */     is.defaultReadObject();
/* 202 */     if (is.readBoolean()) {
/* 203 */       this._uriResolver = ((URIResolver)is.readObject());
/*     */     }
/*     */ 
/* 206 */     this._tfactory = new TransformerFactoryImpl();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream os)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 217 */     os.defaultWriteObject();
/* 218 */     if ((this._uriResolver instanceof Serializable)) {
/* 219 */       os.writeBoolean(true);
/* 220 */       os.writeObject((Serializable)this._uriResolver);
/*     */     }
/*     */     else {
/* 223 */       os.writeBoolean(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean useServicesMechnism()
/*     */   {
/* 231 */     return this._useServicesMechanism;
/*     */   }
/*     */ 
/*     */   public synchronized void setURIResolver(URIResolver resolver)
/*     */   {
/* 238 */     this._uriResolver = resolver;
/*     */   }
/*     */ 
/*     */   private synchronized void setTransletBytecodes(byte[][] bytecodes)
/*     */   {
/* 251 */     this._bytecodes = bytecodes;
/*     */   }
/*     */ 
/*     */   private synchronized byte[][] getTransletBytecodes()
/*     */   {
/* 263 */     return this._bytecodes;
/*     */   }
/*     */ 
/*     */   private synchronized Class[] getTransletClasses()
/*     */   {
/*     */     try
/*     */     {
/* 276 */       if (this._class == null) defineTransletClasses();
/*     */     }
/*     */     catch (TransformerConfigurationException e)
/*     */     {
/*     */     }
/* 281 */     return this._class;
/*     */   }
/*     */ 
/*     */   public synchronized int getTransletIndex()
/*     */   {
/*     */     try
/*     */     {
/* 289 */       if (this._class == null) defineTransletClasses();
/*     */     }
/*     */     catch (TransformerConfigurationException e)
/*     */     {
/*     */     }
/* 294 */     return this._transletIndex;
/*     */   }
/*     */ 
/*     */   protected synchronized void setTransletName(String name)
/*     */   {
/* 301 */     this._name = name;
/*     */   }
/*     */ 
/*     */   protected synchronized String getTransletName()
/*     */   {
/* 308 */     return this._name;
/*     */   }
/*     */ 
/*     */   private void defineTransletClasses()
/*     */     throws TransformerConfigurationException
/*     */   {
/* 318 */     if (this._bytecodes == null) {
/* 319 */       ErrorMsg err = new ErrorMsg("NO_TRANSLET_CLASS_ERR");
/* 320 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */ 
/* 323 */     TransletClassLoader loader = (TransletClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run() {
/* 326 */         return new TemplatesImpl.TransletClassLoader(ObjectFactory.findClassLoader());
/*     */       }
/*     */     });
/*     */     try
/*     */     {
/* 331 */       int classCount = this._bytecodes.length;
/* 332 */       this._class = new Class[classCount];
/*     */ 
/* 334 */       if (classCount > 1) {
/* 335 */         this._auxClasses = new Hashtable();
/*     */       }
/*     */ 
/* 338 */       for (int i = 0; i < classCount; i++) {
/* 339 */         this._class[i] = loader.defineClass(this._bytecodes[i]);
/* 340 */         Class superClass = this._class[i].getSuperclass();
/*     */ 
/* 343 */         if (superClass.getName().equals(ABSTRACT_TRANSLET)) {
/* 344 */           this._transletIndex = i;
/*     */         }
/*     */         else {
/* 347 */           this._auxClasses.put(this._class[i].getName(), this._class[i]);
/*     */         }
/*     */       }
/*     */ 
/* 351 */       if (this._transletIndex < 0) {
/* 352 */         ErrorMsg err = new ErrorMsg("NO_MAIN_TRANSLET_ERR", this._name);
/* 353 */         throw new TransformerConfigurationException(err.toString());
/*     */       }
/*     */     }
/*     */     catch (ClassFormatError e) {
/* 357 */       ErrorMsg err = new ErrorMsg("TRANSLET_CLASS_ERR", this._name);
/* 358 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */     catch (LinkageError e) {
/* 361 */       ErrorMsg err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
/* 362 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private Translet getTransletInstance()
/*     */     throws TransformerConfigurationException
/*     */   {
/*     */     try
/*     */     {
/* 374 */       if (this._name == null) return null;
/*     */ 
/* 376 */       if (this._class == null) defineTransletClasses();
/*     */ 
/* 380 */       AbstractTranslet translet = (AbstractTranslet)this._class[this._transletIndex].newInstance();
/* 381 */       translet.postInitialization();
/* 382 */       translet.setTemplates(this);
/* 383 */       translet.setServicesMechnism(this._useServicesMechanism);
/* 384 */       if (this._auxClasses != null) {
/* 385 */         translet.setAuxiliaryClasses(this._auxClasses);
/*     */       }
/*     */ 
/* 388 */       return translet;
/*     */     }
/*     */     catch (InstantiationException e) {
/* 391 */       ErrorMsg err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
/* 392 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 395 */       ErrorMsg err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
/* 396 */       throw new TransformerConfigurationException(err.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized Transformer newTransformer()
/*     */     throws TransformerConfigurationException
/*     */   {
/* 410 */     TransformerImpl transformer = new TransformerImpl(getTransletInstance(), this._outputProperties, this._indentNumber, this._tfactory);
/*     */ 
/* 413 */     if (this._uriResolver != null) {
/* 414 */       transformer.setURIResolver(this._uriResolver);
/*     */     }
/*     */ 
/* 417 */     if (this._tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
/* 418 */       transformer.setSecureProcessing(true);
/*     */     }
/* 420 */     return transformer;
/*     */   }
/*     */ 
/*     */   public synchronized Properties getOutputProperties()
/*     */   {
/*     */     try
/*     */     {
/* 431 */       return newTransformer().getOutputProperties();
/*     */     } catch (TransformerConfigurationException e) {
/*     */     }
/* 434 */     return null;
/*     */   }
/*     */ 
/*     */   public DOM getStylesheetDOM()
/*     */   {
/* 442 */     return (DOM)this._sdom.get();
/*     */   }
/*     */ 
/*     */   public void setStylesheetDOM(DOM sdom)
/*     */   {
/* 449 */     this._sdom.set(sdom);
/*     */   }
/*     */ 
/*     */   static final class TransletClassLoader extends ClassLoader
/*     */   {
/*     */     TransletClassLoader(ClassLoader parent)
/*     */     {
/* 129 */       super();
/*     */     }
/*     */ 
/*     */     Class defineClass(byte[] b)
/*     */     {
/* 136 */       return defineClass(null, b, 0, b.length);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl
 * JD-Core Version:    0.6.2
 */