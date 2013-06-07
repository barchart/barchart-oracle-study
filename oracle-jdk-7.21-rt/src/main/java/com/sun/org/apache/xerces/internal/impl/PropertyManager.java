/*     */ package com.sun.org.apache.xerces.internal.impl;
/*     */ 
/*     */ import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
/*     */ import java.util.HashMap;
/*     */ import javax.xml.stream.XMLResolver;
/*     */ 
/*     */ public class PropertyManager
/*     */ {
/*     */   public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
/*     */   public static final String STAX_ENTITIES = "javax.xml.stream.entities";
/*     */   private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
/*  54 */   HashMap supportedProps = new HashMap();
/*     */   public static final int CONTEXT_READER = 1;
/*     */   public static final int CONTEXT_WRITER = 2;
/*     */ 
/*     */   public PropertyManager(int context)
/*     */   {
/*  61 */     switch (context) {
/*     */     case 1:
/*  63 */       initConfigurableReaderProperties();
/*  64 */       break;
/*     */     case 2:
/*  67 */       initWriterProps();
/*     */     }
/*     */   }
/*     */ 
/*     */   public PropertyManager(PropertyManager propertyManager)
/*     */   {
/*  78 */     HashMap properties = propertyManager.getProperties();
/*  79 */     this.supportedProps.putAll(properties);
/*     */   }
/*     */ 
/*     */   private HashMap getProperties() {
/*  83 */     return this.supportedProps;
/*     */   }
/*     */ 
/*     */   private void initConfigurableReaderProperties()
/*     */   {
/*  95 */     this.supportedProps.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
/*  96 */     this.supportedProps.put("javax.xml.stream.isValidating", Boolean.FALSE);
/*  97 */     this.supportedProps.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
/*  98 */     this.supportedProps.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
/*  99 */     this.supportedProps.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
/* 100 */     this.supportedProps.put("javax.xml.stream.supportDTD", Boolean.TRUE);
/* 101 */     this.supportedProps.put("javax.xml.stream.reporter", null);
/* 102 */     this.supportedProps.put("javax.xml.stream.resolver", null);
/* 103 */     this.supportedProps.put("javax.xml.stream.allocator", null);
/* 104 */     this.supportedProps.put("javax.xml.stream.notations", null);
/*     */ 
/* 108 */     this.supportedProps.put("http://xml.org/sax/features/string-interning", new Boolean(true));
/*     */ 
/* 110 */     this.supportedProps.put("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
/*     */ 
/* 112 */     this.supportedProps.put("add-namespacedecl-as-attrbiute", Boolean.FALSE);
/* 113 */     this.supportedProps.put("http://java.sun.com/xml/stream/properties/reader-in-defined-state", new Boolean(true));
/* 114 */     this.supportedProps.put("reuse-instance", new Boolean(true));
/* 115 */     this.supportedProps.put("http://java.sun.com/xml/stream/properties/report-cdata-event", new Boolean(false));
/* 116 */     this.supportedProps.put("http://java.sun.com/xml/stream/properties/ignore-external-dtd", Boolean.FALSE);
/* 117 */     this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", new Boolean(false));
/* 118 */     this.supportedProps.put("http://apache.org/xml/features/warn-on-duplicate-entitydef", new Boolean(false));
/* 119 */     this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", new Boolean(false));
/*     */   }
/*     */ 
/*     */   private void initWriterProps() {
/* 123 */     this.supportedProps.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
/*     */ 
/* 125 */     this.supportedProps.put("escapeCharacters", Boolean.TRUE);
/* 126 */     this.supportedProps.put("reuse-instance", new Boolean(true));
/*     */   }
/*     */ 
/*     */   public boolean containsProperty(String property)
/*     */   {
/* 135 */     return this.supportedProps.containsKey(property);
/*     */   }
/*     */ 
/*     */   public Object getProperty(String property) {
/* 139 */     return this.supportedProps.get(property);
/*     */   }
/*     */ 
/*     */   public void setProperty(String property, Object value) {
/* 143 */     String equivalentProperty = null;
/* 144 */     if ((property == "javax.xml.stream.isNamespaceAware") || (property.equals("javax.xml.stream.isNamespaceAware"))) {
/* 145 */       equivalentProperty = "http://apache.org/xml/features/namespaces";
/*     */     }
/* 147 */     else if ((property == "javax.xml.stream.isValidating") || (property.equals("javax.xml.stream.isValidating"))) {
/* 148 */       if (((value instanceof Boolean)) && (((Boolean)value).booleanValue())) {
/* 149 */         throw new IllegalArgumentException("true value of isValidating not supported");
/*     */       }
/*     */     }
/* 152 */     else if ((property == "http://xml.org/sax/features/string-interning") || (property.equals("http://xml.org/sax/features/string-interning"))) {
/* 153 */       if (((value instanceof Boolean)) && (!((Boolean)value).booleanValue())) {
/* 154 */         throw new IllegalArgumentException("false value of http://xml.org/sax/features/string-interningfeature is not supported");
/*     */       }
/*     */     }
/* 157 */     else if ((property == "javax.xml.stream.resolver") || (property.equals("javax.xml.stream.resolver")))
/*     */     {
/* 159 */       this.supportedProps.put("http://apache.org/xml/properties/internal/stax-entity-resolver", new StaxEntityResolverWrapper((XMLResolver)value));
/*     */     }
/* 161 */     this.supportedProps.put(property, value);
/* 162 */     if (equivalentProperty != null)
/* 163 */       this.supportedProps.put(equivalentProperty, value);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 168 */     return this.supportedProps.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.PropertyManager
 * JD-Core Version:    0.6.2
 */