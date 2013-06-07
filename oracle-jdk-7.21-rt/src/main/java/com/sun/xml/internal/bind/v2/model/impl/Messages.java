/*     */ package com.sun.xml.internal.bind.v2.model.impl;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */  enum Messages
/*     */ {
/*  36 */   ID_MUST_BE_STRING, 
/*     */ 
/*  38 */   MUTUALLY_EXCLUSIVE_ANNOTATIONS, 
/*  39 */   DUPLICATE_ANNOTATIONS, 
/*  40 */   NO_DEFAULT_CONSTRUCTOR, 
/*  41 */   CANT_HANDLE_INTERFACE, 
/*  42 */   CANT_HANDLE_INNER_CLASS, 
/*  43 */   ANNOTATION_ON_WRONG_METHOD, 
/*  44 */   GETTER_SETTER_INCOMPATIBLE_TYPE, 
/*  45 */   DUPLICATE_ENTRY_IN_PROP_ORDER, 
/*  46 */   DUPLICATE_PROPERTIES, 
/*     */ 
/*  48 */   XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD, 
/*  49 */   SCOPE_IS_NOT_COMPLEXTYPE, 
/*  50 */   CONFLICTING_XML_ELEMENT_MAPPING, 
/*     */ 
/*  52 */   REFERENCE_TO_NON_ELEMENT, 
/*     */ 
/*  54 */   NON_EXISTENT_ELEMENT_MAPPING, 
/*     */ 
/*  56 */   TWO_ATTRIBUTE_WILDCARDS, 
/*  57 */   SUPER_CLASS_HAS_WILDCARD, 
/*  58 */   INVALID_ATTRIBUTE_WILDCARD_TYPE, 
/*  59 */   PROPERTY_MISSING_FROM_ORDER, 
/*  60 */   PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY, 
/*     */ 
/*  62 */   INVALID_XML_ENUM_VALUE, 
/*  63 */   FAILED_TO_INITIALE_DATATYPE_FACTORY, 
/*  64 */   NO_IMAGE_WRITER, 
/*     */ 
/*  66 */   ILLEGAL_MIME_TYPE, 
/*  67 */   ILLEGAL_ANNOTATION, 
/*     */ 
/*  69 */   MULTIPLE_VALUE_PROPERTY, 
/*  70 */   ELEMENT_AND_VALUE_PROPERTY, 
/*  71 */   CONFLICTING_XML_TYPE_MAPPING, 
/*  72 */   XMLVALUE_IN_DERIVED_TYPE, 
/*  73 */   SIMPLE_TYPE_IS_REQUIRED, 
/*  74 */   PROPERTY_COLLISION, 
/*  75 */   INVALID_IDREF, 
/*  76 */   INVALID_XML_ELEMENT_REF, 
/*  77 */   NO_XML_ELEMENT_DECL, 
/*  78 */   XML_ELEMENT_WRAPPER_ON_NON_COLLECTION, 
/*     */ 
/*  80 */   ANNOTATION_NOT_ALLOWED, 
/*  81 */   XMLLIST_NEEDS_SIMPLETYPE, 
/*  82 */   XMLLIST_ON_SINGLE_PROPERTY, 
/*  83 */   NO_FACTORY_METHOD, 
/*  84 */   FACTORY_CLASS_NEEDS_FACTORY_METHOD, 
/*     */ 
/*  86 */   INCOMPATIBLE_API_VERSION, 
/*  87 */   INCOMPATIBLE_API_VERSION_MUSTANG, 
/*  88 */   RUNNING_WITH_1_0_RUNTIME, 
/*     */ 
/*  90 */   MISSING_JAXB_PROPERTIES, 
/*  91 */   TRANSIENT_FIELD_NOT_BINDABLE, 
/*  92 */   THERE_MUST_BE_VALUE_IN_XMLVALUE, 
/*  93 */   UNMATCHABLE_ADAPTER, 
/*  94 */   ANONYMOUS_ARRAY_ITEM, 
/*     */ 
/*  96 */   ACCESSORFACTORY_INSTANTIATION_EXCEPTION, 
/*  97 */   ACCESSORFACTORY_ACCESS_EXCEPTION, 
/*  98 */   CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR, 
/*  99 */   CUSTOM_ACCESSORFACTORY_FIELD_ERROR, 
/* 100 */   XMLGREGORIANCALENDAR_INVALID, 
/* 101 */   XMLGREGORIANCALENDAR_SEC, 
/* 102 */   XMLGREGORIANCALENDAR_MIN, 
/* 103 */   XMLGREGORIANCALENDAR_HR, 
/* 104 */   XMLGREGORIANCALENDAR_DAY, 
/* 105 */   XMLGREGORIANCALENDAR_MONTH, 
/* 106 */   XMLGREGORIANCALENDAR_YEAR, 
/* 107 */   XMLGREGORIANCALENDAR_TIMEZONE;
/*     */ 
/* 110 */   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());
/*     */ 
/*     */   public String toString()
/*     */   {
/* 114 */     return format(new Object[0]);
/*     */   }
/*     */ 
/*     */   public String format(Object[] args) {
/* 118 */     return MessageFormat.format(rb.getString(name()), args);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.v2.model.impl.Messages
 * JD-Core Version:    0.6.2
 */