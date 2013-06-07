/*      */ package com.sun.org.apache.xerces.internal.impl.xs.traversers;
/*      */ 
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
/*      */ import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
/*      */ import com.sun.org.apache.xerces.internal.impl.xs.util.XIntPool;
/*      */ import com.sun.org.apache.xerces.internal.util.DOMUtil;
/*      */ import com.sun.org.apache.xerces.internal.util.SecurityManager;
/*      */ import com.sun.org.apache.xerces.internal.util.SymbolTable;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLChar;
/*      */ import com.sun.org.apache.xerces.internal.util.XMLSymbols;
/*      */ import com.sun.org.apache.xerces.internal.xni.QName;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import org.w3c.dom.Attr;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ 
/*      */ public class XSAttributeChecker
/*      */ {
/*      */   private static final String ELEMENT_N = "element_n";
/*      */   private static final String ELEMENT_R = "element_r";
/*      */   private static final String ATTRIBUTE_N = "attribute_n";
/*      */   private static final String ATTRIBUTE_R = "attribute_r";
/*   85 */   private static int ATTIDX_COUNT = 0;
/*   86 */   public static final int ATTIDX_ABSTRACT = ATTIDX_COUNT++;
/*   87 */   public static final int ATTIDX_AFORMDEFAULT = ATTIDX_COUNT++;
/*   88 */   public static final int ATTIDX_BASE = ATTIDX_COUNT++;
/*   89 */   public static final int ATTIDX_BLOCK = ATTIDX_COUNT++;
/*   90 */   public static final int ATTIDX_BLOCKDEFAULT = ATTIDX_COUNT++;
/*   91 */   public static final int ATTIDX_DEFAULT = ATTIDX_COUNT++;
/*   92 */   public static final int ATTIDX_EFORMDEFAULT = ATTIDX_COUNT++;
/*   93 */   public static final int ATTIDX_FINAL = ATTIDX_COUNT++;
/*   94 */   public static final int ATTIDX_FINALDEFAULT = ATTIDX_COUNT++;
/*   95 */   public static final int ATTIDX_FIXED = ATTIDX_COUNT++;
/*   96 */   public static final int ATTIDX_FORM = ATTIDX_COUNT++;
/*   97 */   public static final int ATTIDX_ID = ATTIDX_COUNT++;
/*   98 */   public static final int ATTIDX_ITEMTYPE = ATTIDX_COUNT++;
/*   99 */   public static final int ATTIDX_MAXOCCURS = ATTIDX_COUNT++;
/*  100 */   public static final int ATTIDX_MEMBERTYPES = ATTIDX_COUNT++;
/*  101 */   public static final int ATTIDX_MINOCCURS = ATTIDX_COUNT++;
/*  102 */   public static final int ATTIDX_MIXED = ATTIDX_COUNT++;
/*  103 */   public static final int ATTIDX_NAME = ATTIDX_COUNT++;
/*  104 */   public static final int ATTIDX_NAMESPACE = ATTIDX_COUNT++;
/*  105 */   public static final int ATTIDX_NAMESPACE_LIST = ATTIDX_COUNT++;
/*  106 */   public static final int ATTIDX_NILLABLE = ATTIDX_COUNT++;
/*  107 */   public static final int ATTIDX_NONSCHEMA = ATTIDX_COUNT++;
/*  108 */   public static final int ATTIDX_PROCESSCONTENTS = ATTIDX_COUNT++;
/*  109 */   public static final int ATTIDX_PUBLIC = ATTIDX_COUNT++;
/*  110 */   public static final int ATTIDX_REF = ATTIDX_COUNT++;
/*  111 */   public static final int ATTIDX_REFER = ATTIDX_COUNT++;
/*  112 */   public static final int ATTIDX_SCHEMALOCATION = ATTIDX_COUNT++;
/*  113 */   public static final int ATTIDX_SOURCE = ATTIDX_COUNT++;
/*  114 */   public static final int ATTIDX_SUBSGROUP = ATTIDX_COUNT++;
/*  115 */   public static final int ATTIDX_SYSTEM = ATTIDX_COUNT++;
/*  116 */   public static final int ATTIDX_TARGETNAMESPACE = ATTIDX_COUNT++;
/*  117 */   public static final int ATTIDX_TYPE = ATTIDX_COUNT++;
/*  118 */   public static final int ATTIDX_USE = ATTIDX_COUNT++;
/*  119 */   public static final int ATTIDX_VALUE = ATTIDX_COUNT++;
/*  120 */   public static final int ATTIDX_ENUMNSDECLS = ATTIDX_COUNT++;
/*  121 */   public static final int ATTIDX_VERSION = ATTIDX_COUNT++;
/*  122 */   public static final int ATTIDX_XML_LANG = ATTIDX_COUNT++;
/*  123 */   public static final int ATTIDX_XPATH = ATTIDX_COUNT++;
/*  124 */   public static final int ATTIDX_FROMDEFAULT = ATTIDX_COUNT++;
/*      */ 
/*  126 */   public static final int ATTIDX_ISRETURNED = ATTIDX_COUNT++;
/*      */ 
/*  128 */   private static final XIntPool fXIntPool = new XIntPool();
/*      */ 
/*  130 */   private static final XInt INT_QUALIFIED = fXIntPool.getXInt(1);
/*  131 */   private static final XInt INT_UNQUALIFIED = fXIntPool.getXInt(0);
/*  132 */   private static final XInt INT_EMPTY_SET = fXIntPool.getXInt(0);
/*  133 */   private static final XInt INT_ANY_STRICT = fXIntPool.getXInt(1);
/*  134 */   private static final XInt INT_ANY_LAX = fXIntPool.getXInt(3);
/*  135 */   private static final XInt INT_ANY_SKIP = fXIntPool.getXInt(2);
/*  136 */   private static final XInt INT_ANY_ANY = fXIntPool.getXInt(1);
/*  137 */   private static final XInt INT_ANY_LIST = fXIntPool.getXInt(3);
/*  138 */   private static final XInt INT_ANY_NOT = fXIntPool.getXInt(2);
/*  139 */   private static final XInt INT_USE_OPTIONAL = fXIntPool.getXInt(0);
/*  140 */   private static final XInt INT_USE_REQUIRED = fXIntPool.getXInt(1);
/*  141 */   private static final XInt INT_USE_PROHIBITED = fXIntPool.getXInt(2);
/*  142 */   private static final XInt INT_WS_PRESERVE = fXIntPool.getXInt(0);
/*  143 */   private static final XInt INT_WS_REPLACE = fXIntPool.getXInt(1);
/*  144 */   private static final XInt INT_WS_COLLAPSE = fXIntPool.getXInt(2);
/*  145 */   private static final XInt INT_UNBOUNDED = fXIntPool.getXInt(-1);
/*      */ 
/*  149 */   private static final Map fEleAttrsMapG = new HashMap(29);
/*      */ 
/*  151 */   private static final Map fEleAttrsMapL = new HashMap(79);
/*      */   protected static final int DT_ANYURI = 0;
/*      */   protected static final int DT_ID = 1;
/*      */   protected static final int DT_QNAME = 2;
/*      */   protected static final int DT_STRING = 3;
/*      */   protected static final int DT_TOKEN = 4;
/*      */   protected static final int DT_NCNAME = 5;
/*      */   protected static final int DT_XPATH = 6;
/*      */   protected static final int DT_XPATH1 = 7;
/*      */   protected static final int DT_LANGUAGE = 8;
/*      */   protected static final int DT_COUNT = 9;
/*  170 */   private static final XSSimpleType[] fExtraDVs = new XSSimpleType[9];
/*      */   protected static final int DT_BLOCK = -1;
/*      */   protected static final int DT_BLOCK1 = -2;
/*      */   protected static final int DT_FINAL = -3;
/*      */   protected static final int DT_FINAL1 = -4;
/*      */   protected static final int DT_FINAL2 = -5;
/*      */   protected static final int DT_FORM = -6;
/*      */   protected static final int DT_MAXOCCURS = -7;
/*      */   protected static final int DT_MAXOCCURS1 = -8;
/*      */   protected static final int DT_MEMBERTYPES = -9;
/*      */   protected static final int DT_MINOCCURS1 = -10;
/*      */   protected static final int DT_NAMESPACE = -11;
/*      */   protected static final int DT_PROCESSCONTENTS = -12;
/*      */   protected static final int DT_USE = -13;
/*      */   protected static final int DT_WHITESPACE = -14;
/*      */   protected static final int DT_BOOLEAN = -15;
/*      */   protected static final int DT_NONNEGINT = -16;
/*      */   protected static final int DT_POSINT = -17;
/*  924 */   protected XSDHandler fSchemaHandler = null;
/*      */ 
/*  927 */   protected SymbolTable fSymbolTable = null;
/*      */ 
/*  930 */   protected Map fNonSchemaAttrs = new HashMap();
/*      */ 
/*  933 */   protected Vector fNamespaceList = new Vector();
/*      */ 
/*  936 */   protected boolean[] fSeen = new boolean[ATTIDX_COUNT];
/*  937 */   private static boolean[] fSeenTemp = new boolean[ATTIDX_COUNT];
/*      */   static final int INIT_POOL_SIZE = 10;
/*      */   static final int INC_POOL_SIZE = 10;
/* 1677 */   Object[][] fArrayPool = new Object[10][ATTIDX_COUNT];
/*      */ 
/* 1680 */   private static Object[] fTempArray = new Object[ATTIDX_COUNT];
/*      */ 
/* 1682 */   int fPoolPos = 0;
/*      */ 
/*      */   public XSAttributeChecker(XSDHandler schemaHandler)
/*      */   {
/*  941 */     this.fSchemaHandler = schemaHandler;
/*      */   }
/*      */ 
/*      */   public void reset(SymbolTable symbolTable) {
/*  945 */     this.fSymbolTable = symbolTable;
/*  946 */     this.fNonSchemaAttrs.clear();
/*      */   }
/*      */ 
/*      */   public Object[] checkAttributes(Element element, boolean isGlobal, XSDocumentInfo schemaDoc)
/*      */   {
/*  961 */     return checkAttributes(element, isGlobal, schemaDoc, false);
/*      */   }
/*      */ 
/*      */   public Object[] checkAttributes(Element element, boolean isGlobal, XSDocumentInfo schemaDoc, boolean enumAsQName)
/*      */   {
/*  980 */     if (element == null) {
/*  981 */       return null;
/*      */     }
/*      */ 
/*  984 */     Attr[] attrs = DOMUtil.getAttrs(element);
/*      */ 
/*  987 */     resolveNamespace(element, attrs, schemaDoc.fNamespaceSupport);
/*      */ 
/*  989 */     String uri = DOMUtil.getNamespaceURI(element);
/*  990 */     String elName = DOMUtil.getLocalName(element);
/*      */ 
/*  992 */     if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(uri)) {
/*  993 */       reportSchemaError("s4s-elt-schema-ns", new Object[] { elName }, element);
/*      */     }
/*      */ 
/*  996 */     Map eleAttrsMap = fEleAttrsMapG;
/*  997 */     String lookupName = elName;
/*      */ 
/* 1003 */     if (!isGlobal) {
/* 1004 */       eleAttrsMap = fEleAttrsMapL;
/* 1005 */       if (elName.equals(SchemaSymbols.ELT_ELEMENT)) {
/* 1006 */         if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null)
/* 1007 */           lookupName = "element_r";
/*      */         else
/* 1009 */           lookupName = "element_n";
/* 1010 */       } else if (elName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
/* 1011 */         if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null)
/* 1012 */           lookupName = "attribute_r";
/*      */         else {
/* 1014 */           lookupName = "attribute_n";
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1019 */     Container attrList = (Container)eleAttrsMap.get(lookupName);
/* 1020 */     if (attrList == null)
/*      */     {
/* 1024 */       reportSchemaError("s4s-elt-invalid", new Object[] { elName }, element);
/* 1025 */       return null;
/*      */     }
/*      */ 
/* 1029 */     Object[] attrValues = getAvailableArray();
/*      */ 
/* 1031 */     long fromDefault = 0L;
/*      */ 
/* 1034 */     System.arraycopy(fSeenTemp, 0, this.fSeen, 0, ATTIDX_COUNT);
/*      */ 
/* 1037 */     int length = attrs.length;
/* 1038 */     Attr sattr = null;
/* 1039 */     for (int i = 0; i < length; i++) {
/* 1040 */       sattr = attrs[i];
/*      */ 
/* 1043 */       String attrName = sattr.getName();
/* 1044 */       String attrURI = DOMUtil.getNamespaceURI(sattr);
/* 1045 */       String attrVal = DOMUtil.getValue(sattr);
/*      */ 
/* 1047 */       if (attrName.startsWith("xml")) {
/* 1048 */         String attrPrefix = DOMUtil.getPrefix(sattr);
/*      */ 
/* 1050 */         if (("xmlns".equals(attrPrefix)) || ("xmlns".equals(attrName)))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 1056 */         if ((SchemaSymbols.ATT_XML_LANG.equals(attrName)) && ((SchemaSymbols.ELT_SCHEMA.equals(elName)) || (SchemaSymbols.ELT_DOCUMENTATION.equals(elName))))
/*      */         {
/* 1059 */           attrURI = null;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1065 */       if ((attrURI != null) && (attrURI.length() != 0))
/*      */       {
/* 1068 */         if (attrURI.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
/* 1069 */           reportSchemaError("s4s-att-not-allowed", new Object[] { elName, attrName }, element);
/*      */         }
/*      */         else {
/* 1072 */           if (attrValues[ATTIDX_NONSCHEMA] == null)
/*      */           {
/* 1074 */             attrValues[ATTIDX_NONSCHEMA] = new Vector(4, 2);
/*      */           }
/* 1076 */           ((Vector)attrValues[ATTIDX_NONSCHEMA]).addElement(attrName);
/* 1077 */           ((Vector)attrValues[ATTIDX_NONSCHEMA]).addElement(attrVal);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1101 */         OneAttr oneAttr = attrList.get(attrName);
/* 1102 */         if (oneAttr == null) {
/* 1103 */           reportSchemaError("s4s-att-not-allowed", new Object[] { elName, attrName }, element);
/*      */         }
/*      */         else
/*      */         {
/* 1110 */           this.fSeen[oneAttr.valueIndex] = true;
/*      */           try
/*      */           {
/* 1117 */             if (oneAttr.dvIndex >= 0) {
/* 1118 */               if ((oneAttr.dvIndex != 3) && (oneAttr.dvIndex != 6) && (oneAttr.dvIndex != 7))
/*      */               {
/* 1121 */                 XSSimpleType dv = fExtraDVs[oneAttr.dvIndex];
/* 1122 */                 Object avalue = dv.validate(attrVal, schemaDoc.fValidationContext, null);
/*      */ 
/* 1124 */                 if (oneAttr.dvIndex == 2) {
/* 1125 */                   QName qname = (QName)avalue;
/* 1126 */                   if ((qname.prefix == XMLSymbols.EMPTY_STRING) && (qname.uri == null) && (schemaDoc.fIsChameleonSchema))
/* 1127 */                     qname.uri = schemaDoc.fTargetNamespace;
/*      */                 }
/* 1129 */                 attrValues[oneAttr.valueIndex] = avalue;
/*      */               } else {
/* 1131 */                 attrValues[oneAttr.valueIndex] = attrVal;
/*      */               }
/*      */             }
/*      */             else
/* 1135 */               attrValues[oneAttr.valueIndex] = validate(attrValues, attrName, attrVal, oneAttr.dvIndex, schemaDoc);
/*      */           }
/*      */           catch (InvalidDatatypeValueException ide) {
/* 1138 */             reportSchemaError("s4s-att-invalid-value", new Object[] { elName, attrName, ide.getMessage() }, element);
/*      */ 
/* 1141 */             if (oneAttr.dfltValue != null)
/*      */             {
/* 1143 */               attrValues[oneAttr.valueIndex] = oneAttr.dfltValue;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1148 */           if ((elName.equals(SchemaSymbols.ELT_ENUMERATION)) && (enumAsQName)) {
/* 1149 */             attrValues[ATTIDX_ENUMNSDECLS] = new SchemaNamespaceSupport(schemaDoc.fNamespaceSupport);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1154 */     OneAttr[] reqAttrs = attrList.values;
/* 1155 */     for (int i = 0; i < reqAttrs.length; i++) {
/* 1156 */       OneAttr oneAttr = reqAttrs[i];
/*      */ 
/* 1160 */       if ((oneAttr.dfltValue != null) && (this.fSeen[oneAttr.valueIndex] == 0))
/*      */       {
/* 1162 */         attrValues[oneAttr.valueIndex] = oneAttr.dfltValue;
/* 1163 */         fromDefault |= 1 << oneAttr.valueIndex;
/*      */       }
/*      */     }
/*      */ 
/* 1167 */     attrValues[ATTIDX_FROMDEFAULT] = new Long(fromDefault);
/*      */ 
/* 1172 */     if (attrValues[ATTIDX_MAXOCCURS] != null) {
/* 1173 */       int min = ((XInt)attrValues[ATTIDX_MINOCCURS]).intValue();
/* 1174 */       int max = ((XInt)attrValues[ATTIDX_MAXOCCURS]).intValue();
/* 1175 */       if (max != -1)
/*      */       {
/* 1178 */         if (this.fSchemaHandler.fSecureProcessing != null) {
/* 1179 */           String localName = element.getLocalName();
/*      */ 
/* 1188 */           boolean optimize = ((localName.equals("element")) || (localName.equals("any"))) && (element.getNextSibling() == null) && (element.getPreviousSibling() == null) && (element.getParentNode().getLocalName().equals("sequence"));
/*      */ 
/* 1194 */           if (!optimize)
/*      */           {
/* 1197 */             int maxOccurNodeLimit = this.fSchemaHandler.fSecureProcessing.getMaxOccurNodeLimit();
/* 1198 */             if (max > maxOccurNodeLimit) {
/* 1199 */               reportSchemaFatalError("maxOccurLimit", new Object[] { new Integer(maxOccurNodeLimit) }, element);
/*      */ 
/* 1202 */               attrValues[ATTIDX_MAXOCCURS] = fXIntPool.getXInt(maxOccurNodeLimit);
/*      */ 
/* 1204 */               max = maxOccurNodeLimit;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1209 */         if (min > max) {
/* 1210 */           reportSchemaError("p-props-correct.2.1", new Object[] { elName, attrValues[ATTIDX_MINOCCURS], attrValues[ATTIDX_MAXOCCURS] }, element);
/*      */ 
/* 1213 */           attrValues[ATTIDX_MINOCCURS] = attrValues[ATTIDX_MAXOCCURS];
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1218 */     return attrValues;
/*      */   }
/*      */ 
/*      */   private Object validate(Object[] attrValues, String attr, String ivalue, int dvIndex, XSDocumentInfo schemaDoc) throws InvalidDatatypeValueException
/*      */   {
/* 1223 */     if (ivalue == null) {
/* 1224 */       return null;
/*      */     }
/*      */ 
/* 1231 */     String value = XMLChar.trim(ivalue);
/* 1232 */     Object retValue = null;
/*      */     int choice;
/* 1236 */     switch (dvIndex) {
/*      */     case -15:
/* 1238 */       if ((value.equals("false")) || (value.equals("0")))
/*      */       {
/* 1240 */         retValue = Boolean.FALSE;
/* 1241 */       } else if ((value.equals("true")) || (value.equals("1")))
/*      */       {
/* 1243 */         retValue = Boolean.TRUE;
/*      */       }
/* 1245 */       else throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { value, "boolean" });
/*      */       break;
/*      */     case -16:
/*      */       try
/*      */       {
/* 1250 */         if ((value.length() > 0) && (value.charAt(0) == '+'))
/* 1251 */           value = value.substring(1);
/* 1252 */         retValue = fXIntPool.getXInt(Integer.parseInt(value));
/*      */       } catch (NumberFormatException e) {
/* 1254 */         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { value, "nonNegativeInteger" });
/*      */       }
/* 1256 */       if (((XInt)retValue).intValue() < 0)
/* 1257 */         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { value, "nonNegativeInteger" });
/*      */       break;
/*      */     case -17:
/*      */       try {
/* 1261 */         if ((value.length() > 0) && (value.charAt(0) == '+'))
/* 1262 */           value = value.substring(1);
/* 1263 */         retValue = fXIntPool.getXInt(Integer.parseInt(value));
/*      */       } catch (NumberFormatException e) {
/* 1265 */         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { value, "positiveInteger" });
/*      */       }
/* 1267 */       if (((XInt)retValue).intValue() <= 0) {
/* 1268 */         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { value, "positiveInteger" });
/*      */       }
/*      */       break;
/*      */     case -1:
/* 1272 */       choice = 0;
/* 1273 */       if (value.equals("#all")) {
/* 1274 */         choice = 7;
/*      */       }
/*      */       else
/*      */       {
/* 1278 */         StringTokenizer t = new StringTokenizer(value, " \n\t\r");
/* 1279 */         while (t.hasMoreTokens()) {
/* 1280 */           String token = t.nextToken();
/*      */ 
/* 1282 */           if (token.equals("extension")) {
/* 1283 */             choice |= 1;
/*      */           }
/* 1285 */           else if (token.equals("restriction")) {
/* 1286 */             choice |= 2;
/*      */           }
/* 1288 */           else if (token.equals("substitution")) {
/* 1289 */             choice |= 4;
/*      */           }
/*      */           else {
/* 1292 */             throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "(#all | List of (extension | restriction | substitution))" });
/*      */           }
/*      */         }
/*      */       }
/* 1296 */       retValue = fXIntPool.getXInt(choice);
/* 1297 */       break;
/*      */     case -3:
/*      */     case -2:
/* 1302 */       choice = 0;
/* 1303 */       if (value.equals("#all"))
/*      */       {
/* 1315 */         choice = 31;
/*      */       }
/*      */       else
/*      */       {
/* 1320 */         StringTokenizer t = new StringTokenizer(value, " \n\t\r");
/* 1321 */         while (t.hasMoreTokens()) {
/* 1322 */           String token = t.nextToken();
/*      */ 
/* 1324 */           if (token.equals("extension")) {
/* 1325 */             choice |= 1;
/*      */           }
/* 1327 */           else if (token.equals("restriction")) {
/* 1328 */             choice |= 2;
/*      */           }
/*      */           else {
/* 1331 */             throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "(#all | List of (extension | restriction))" });
/*      */           }
/*      */         }
/*      */       }
/* 1335 */       retValue = fXIntPool.getXInt(choice);
/* 1336 */       break;
/*      */     case -4:
/* 1339 */       choice = 0;
/* 1340 */       if (value.equals("#all"))
/*      */       {
/* 1347 */         choice = 31;
/*      */       }
/*      */       else
/*      */       {
/* 1352 */         StringTokenizer t = new StringTokenizer(value, " \n\t\r");
/* 1353 */         while (t.hasMoreTokens()) {
/* 1354 */           String token = t.nextToken();
/*      */ 
/* 1356 */           if (token.equals("list")) {
/* 1357 */             choice |= 16;
/*      */           }
/* 1359 */           else if (token.equals("union")) {
/* 1360 */             choice |= 8;
/*      */           }
/* 1362 */           else if (token.equals("restriction")) {
/* 1363 */             choice |= 2;
/*      */           }
/*      */           else {
/* 1366 */             throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "(#all | List of (list | union | restriction))" });
/*      */           }
/*      */         }
/*      */       }
/* 1370 */       retValue = fXIntPool.getXInt(choice);
/* 1371 */       break;
/*      */     case -5:
/* 1374 */       choice = 0;
/* 1375 */       if (value.equals("#all"))
/*      */       {
/* 1382 */         choice = 31;
/*      */       }
/*      */       else
/*      */       {
/* 1387 */         StringTokenizer t = new StringTokenizer(value, " \n\t\r");
/* 1388 */         while (t.hasMoreTokens()) {
/* 1389 */           String token = t.nextToken();
/*      */ 
/* 1391 */           if (token.equals("extension")) {
/* 1392 */             choice |= 1;
/*      */           }
/* 1394 */           else if (token.equals("restriction")) {
/* 1395 */             choice |= 2;
/*      */           }
/* 1397 */           else if (token.equals("list")) {
/* 1398 */             choice |= 16;
/*      */           }
/* 1400 */           else if (token.equals("union")) {
/* 1401 */             choice |= 8;
/*      */           }
/*      */           else {
/* 1404 */             throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "(#all | List of (extension | restriction | list | union))" });
/*      */           }
/*      */         }
/*      */       }
/* 1408 */       retValue = fXIntPool.getXInt(choice);
/* 1409 */       break;
/*      */     case -6:
/* 1412 */       if (value.equals("qualified"))
/* 1413 */         retValue = INT_QUALIFIED;
/* 1414 */       else if (value.equals("unqualified"))
/* 1415 */         retValue = INT_UNQUALIFIED;
/*      */       else {
/* 1417 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(qualified | unqualified)" });
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -7:
/* 1422 */       if (value.equals("unbounded"))
/* 1423 */         retValue = INT_UNBOUNDED;
/*      */       else {
/*      */         try {
/* 1426 */           retValue = validate(attrValues, attr, value, -16, schemaDoc);
/*      */         } catch (NumberFormatException e) {
/* 1428 */           throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "(nonNegativeInteger | unbounded)" });
/*      */         }
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -8:
/* 1434 */       if (value.equals("1"))
/* 1435 */         retValue = fXIntPool.getXInt(1);
/*      */       else {
/* 1437 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(1)" });
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -9:
/* 1442 */       Vector memberType = new Vector();
/*      */       try {
/* 1444 */         StringTokenizer t = new StringTokenizer(value, " \n\t\r");
/* 1445 */         while (t.hasMoreTokens()) {
/* 1446 */           String token = t.nextToken();
/* 1447 */           QName qname = (QName)fExtraDVs[2].validate(token, schemaDoc.fValidationContext, null);
/*      */ 
/* 1449 */           if ((qname.prefix == XMLSymbols.EMPTY_STRING) && (qname.uri == null) && (schemaDoc.fIsChameleonSchema))
/* 1450 */             qname.uri = schemaDoc.fTargetNamespace;
/* 1451 */           memberType.addElement(qname);
/*      */         }
/* 1453 */         retValue = memberType;
/*      */       }
/*      */       catch (InvalidDatatypeValueException ide) {
/* 1456 */         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.2", new Object[] { value, "(List of QName)" });
/*      */       }
/*      */ 
/*      */     case -10:
/* 1461 */       if (value.equals("0"))
/* 1462 */         retValue = fXIntPool.getXInt(0);
/* 1463 */       else if (value.equals("1"))
/* 1464 */         retValue = fXIntPool.getXInt(1);
/*      */       else {
/* 1466 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(0 | 1)" });
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -11:
/* 1471 */       if (value.equals("##any"))
/*      */       {
/* 1473 */         retValue = INT_ANY_ANY;
/* 1474 */       } else if (value.equals("##other"))
/*      */       {
/* 1476 */         retValue = INT_ANY_NOT;
/* 1477 */         String[] list = new String[2];
/* 1478 */         list[0] = schemaDoc.fTargetNamespace;
/* 1479 */         list[1] = null;
/* 1480 */         attrValues[ATTIDX_NAMESPACE_LIST] = list;
/*      */       }
/*      */       else {
/* 1483 */         retValue = INT_ANY_LIST;
/*      */ 
/* 1485 */         this.fNamespaceList.removeAllElements();
/*      */ 
/* 1488 */         StringTokenizer tokens = new StringTokenizer(value, " \n\t\r");
/*      */         try
/*      */         {
/* 1492 */           while (tokens.hasMoreTokens()) {
/* 1493 */             String token = tokens.nextToken();
/*      */             String tempNamespace;
/*      */             String tempNamespace;
/* 1494 */             if (token.equals("##local")) {
/* 1495 */               tempNamespace = null;
/*      */             }
/*      */             else
/*      */             {
/*      */               String tempNamespace;
/* 1496 */               if (token.equals("##targetNamespace")) {
/* 1497 */                 tempNamespace = schemaDoc.fTargetNamespace;
/*      */               }
/*      */               else
/*      */               {
/* 1501 */                 fExtraDVs[0].validate(token, schemaDoc.fValidationContext, null);
/* 1502 */                 tempNamespace = this.fSymbolTable.addSymbol(token);
/*      */               }
/*      */             }
/*      */ 
/* 1506 */             if (!this.fNamespaceList.contains(tempNamespace))
/* 1507 */               this.fNamespaceList.addElement(tempNamespace);
/*      */           }
/*      */         }
/*      */         catch (InvalidDatatypeValueException ide) {
/* 1511 */           throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { value, "((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )" });
/*      */         }
/*      */ 
/* 1515 */         int num = this.fNamespaceList.size();
/* 1516 */         String[] list = new String[num];
/* 1517 */         this.fNamespaceList.copyInto(list);
/* 1518 */         attrValues[ATTIDX_NAMESPACE_LIST] = list;
/*      */       }
/* 1520 */       break;
/*      */     case -12:
/* 1523 */       if (value.equals("strict"))
/* 1524 */         retValue = INT_ANY_STRICT;
/* 1525 */       else if (value.equals("lax"))
/* 1526 */         retValue = INT_ANY_LAX;
/* 1527 */       else if (value.equals("skip"))
/* 1528 */         retValue = INT_ANY_SKIP;
/*      */       else {
/* 1530 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(lax | skip | strict)" });
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -13:
/* 1535 */       if (value.equals("optional"))
/* 1536 */         retValue = INT_USE_OPTIONAL;
/* 1537 */       else if (value.equals("required"))
/* 1538 */         retValue = INT_USE_REQUIRED;
/* 1539 */       else if (value.equals("prohibited"))
/* 1540 */         retValue = INT_USE_PROHIBITED;
/*      */       else {
/* 1542 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(optional | prohibited | required)" });
/*      */       }
/*      */ 
/*      */       break;
/*      */     case -14:
/* 1547 */       if (value.equals("preserve"))
/* 1548 */         retValue = INT_WS_PRESERVE;
/* 1549 */       else if (value.equals("replace"))
/* 1550 */         retValue = INT_WS_REPLACE;
/* 1551 */       else if (value.equals("collapse"))
/* 1552 */         retValue = INT_WS_COLLAPSE;
/*      */       else {
/* 1554 */         throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { value, "(preserve | replace | collapse)" });
/*      */       }
/*      */       break;
/*      */     }
/*      */ 
/* 1559 */     return retValue;
/*      */   }
/*      */ 
/*      */   void reportSchemaFatalError(String key, Object[] args, Element ele) {
/* 1563 */     this.fSchemaHandler.reportSchemaFatalError(key, args, ele);
/*      */   }
/*      */ 
/*      */   void reportSchemaError(String key, Object[] args, Element ele) {
/* 1567 */     this.fSchemaHandler.reportSchemaError(key, args, ele);
/*      */   }
/*      */ 
/*      */   public void checkNonSchemaAttributes(XSGrammarBucket grammarBucket)
/*      */   {
/* 1576 */     Iterator entries = this.fNonSchemaAttrs.entrySet().iterator();
/*      */ 
/* 1578 */     while (entries.hasNext()) {
/* 1579 */       Map.Entry entry = (Map.Entry)entries.next();
/*      */ 
/* 1581 */       String attrRName = (String)entry.getKey();
/* 1582 */       String attrURI = attrRName.substring(0, attrRName.indexOf(','));
/* 1583 */       String attrLocal = attrRName.substring(attrRName.indexOf(',') + 1);
/*      */ 
/* 1585 */       SchemaGrammar sGrammar = grammarBucket.getGrammar(attrURI);
/* 1586 */       if (sGrammar != null)
/*      */       {
/* 1590 */         XSAttributeDecl attrDecl = sGrammar.getGlobalAttributeDecl(attrLocal);
/* 1591 */         if (attrDecl != null)
/*      */         {
/* 1594 */           XSSimpleType dv = (XSSimpleType)attrDecl.getTypeDefinition();
/* 1595 */           if (dv != null)
/*      */           {
/* 1600 */             Vector values = (Vector)entry.getValue();
/*      */ 
/* 1602 */             String attrName = (String)values.elementAt(0);
/*      */ 
/* 1604 */             int count = values.size();
/* 1605 */             for (int i = 1; i < count; i += 2) {
/* 1606 */               String elName = (String)values.elementAt(i);
/*      */               try
/*      */               {
/* 1611 */                 dv.validate((String)values.elementAt(i + 1), null, null);
/*      */               } catch (InvalidDatatypeValueException ide) {
/* 1613 */                 reportSchemaError("s4s-att-invalid-value", new Object[] { elName, attrName, ide.getMessage() }, null);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String normalize(String content, short ws) {
/* 1623 */     int len = content == null ? 0 : content.length();
/* 1624 */     if ((len == 0) || (ws == 0)) {
/* 1625 */       return content;
/*      */     }
/* 1627 */     StringBuffer sb = new StringBuffer();
/* 1628 */     if (ws == 1)
/*      */     {
/* 1631 */       for (int i = 0; i < len; i++) {
/* 1632 */         char ch = content.charAt(i);
/* 1633 */         if ((ch != '\t') && (ch != '\n') && (ch != '\r'))
/* 1634 */           sb.append(ch);
/*      */         else
/* 1636 */           sb.append(' ');
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1641 */       boolean isLeading = true;
/*      */ 
/* 1643 */       for (int i = 0; i < len; i++) {
/* 1644 */         char ch = content.charAt(i);
/*      */ 
/* 1646 */         if ((ch != '\t') && (ch != '\n') && (ch != '\r') && (ch != ' ')) {
/* 1647 */           sb.append(ch);
/* 1648 */           isLeading = false;
/*      */         }
/*      */         else
/*      */         {
/* 1652 */           for (; i < len - 1; i++) {
/* 1653 */             ch = content.charAt(i + 1);
/* 1654 */             if ((ch != '\t') && (ch != '\n') && (ch != '\r') && (ch != ' ')) {
/*      */               break;
/*      */             }
/*      */           }
/* 1658 */           if ((i < len - 1) && (!isLeading)) {
/* 1659 */             sb.append(' ');
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1664 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   protected Object[] getAvailableArray()
/*      */   {
/* 1687 */     if (this.fArrayPool.length == this.fPoolPos)
/*      */     {
/* 1689 */       this.fArrayPool = new Object[this.fPoolPos + 10][];
/*      */ 
/* 1691 */       for (int i = this.fPoolPos; i < this.fArrayPool.length; i++) {
/* 1692 */         this.fArrayPool[i] = new Object[ATTIDX_COUNT];
/*      */       }
/*      */     }
/* 1695 */     Object[] retArray = this.fArrayPool[this.fPoolPos];
/*      */ 
/* 1698 */     this.fArrayPool[(this.fPoolPos++)] = null;
/*      */ 
/* 1702 */     System.arraycopy(fTempArray, 0, retArray, 0, ATTIDX_COUNT - 1);
/* 1703 */     retArray[ATTIDX_ISRETURNED] = Boolean.FALSE;
/*      */ 
/* 1705 */     return retArray;
/*      */   }
/*      */ 
/*      */   public void returnAttrArray(Object[] attrArray, XSDocumentInfo schemaDoc)
/*      */   {
/* 1711 */     if (schemaDoc != null) {
/* 1712 */       schemaDoc.fNamespaceSupport.popContext();
/*      */     }
/*      */ 
/* 1717 */     if ((this.fPoolPos == 0) || (attrArray == null) || (attrArray.length != ATTIDX_COUNT) || (((Boolean)attrArray[ATTIDX_ISRETURNED]).booleanValue()))
/*      */     {
/* 1721 */       return;
/*      */     }
/*      */ 
/* 1725 */     attrArray[ATTIDX_ISRETURNED] = Boolean.TRUE;
/*      */ 
/* 1727 */     if (attrArray[ATTIDX_NONSCHEMA] != null) {
/* 1728 */       ((Vector)attrArray[ATTIDX_NONSCHEMA]).clear();
/*      */     }
/* 1730 */     this.fArrayPool[(--this.fPoolPos)] = attrArray;
/*      */   }
/*      */ 
/*      */   public void resolveNamespace(Element element, Attr[] attrs, SchemaNamespaceSupport nsSupport)
/*      */   {
/* 1736 */     nsSupport.pushContext();
/*      */ 
/* 1739 */     int length = attrs.length;
/* 1740 */     Attr sattr = null;
/*      */ 
/* 1742 */     for (int i = 0; i < length; i++) {
/* 1743 */       sattr = attrs[i];
/* 1744 */       String rawname = DOMUtil.getName(sattr);
/* 1745 */       String prefix = null;
/* 1746 */       if (rawname.equals(XMLSymbols.PREFIX_XMLNS))
/* 1747 */         prefix = XMLSymbols.EMPTY_STRING;
/* 1748 */       else if (rawname.startsWith("xmlns:"))
/* 1749 */         prefix = this.fSymbolTable.addSymbol(DOMUtil.getLocalName(sattr));
/* 1750 */       if (prefix != null) {
/* 1751 */         String uri = this.fSymbolTable.addSymbol(DOMUtil.getValue(sattr));
/* 1752 */         nsSupport.declarePrefix(prefix, uri.length() != 0 ? uri : null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  173 */     SchemaGrammar grammar = SchemaGrammar.SG_SchemaNS;
/*      */ 
/*  175 */     fExtraDVs[0] = ((XSSimpleType)grammar.getGlobalTypeDecl("anyURI"));
/*      */ 
/*  177 */     fExtraDVs[1] = ((XSSimpleType)grammar.getGlobalTypeDecl("ID"));
/*      */ 
/*  179 */     fExtraDVs[2] = ((XSSimpleType)grammar.getGlobalTypeDecl("QName"));
/*      */ 
/*  181 */     fExtraDVs[3] = ((XSSimpleType)grammar.getGlobalTypeDecl("string"));
/*      */ 
/*  183 */     fExtraDVs[4] = ((XSSimpleType)grammar.getGlobalTypeDecl("token"));
/*      */ 
/*  185 */     fExtraDVs[5] = ((XSSimpleType)grammar.getGlobalTypeDecl("NCName"));
/*      */ 
/*  187 */     fExtraDVs[6] = fExtraDVs[3];
/*      */ 
/*  189 */     fExtraDVs[6] = fExtraDVs[3];
/*      */ 
/*  191 */     fExtraDVs[8] = ((XSSimpleType)grammar.getGlobalTypeDecl("language"));
/*      */ 
/*  214 */     int attCount = 0;
/*  215 */     int ATT_ABSTRACT_D = attCount++;
/*  216 */     int ATT_ATTRIBUTE_FD_D = attCount++;
/*  217 */     int ATT_BASE_R = attCount++;
/*  218 */     int ATT_BASE_N = attCount++;
/*  219 */     int ATT_BLOCK_N = attCount++;
/*  220 */     int ATT_BLOCK1_N = attCount++;
/*  221 */     int ATT_BLOCK_D_D = attCount++;
/*  222 */     int ATT_DEFAULT_N = attCount++;
/*  223 */     int ATT_ELEMENT_FD_D = attCount++;
/*  224 */     int ATT_FINAL_N = attCount++;
/*  225 */     int ATT_FINAL1_N = attCount++;
/*  226 */     int ATT_FINAL_D_D = attCount++;
/*  227 */     int ATT_FIXED_N = attCount++;
/*  228 */     int ATT_FIXED_D = attCount++;
/*  229 */     int ATT_FORM_N = attCount++;
/*  230 */     int ATT_ID_N = attCount++;
/*  231 */     int ATT_ITEMTYPE_N = attCount++;
/*  232 */     int ATT_MAXOCCURS_D = attCount++;
/*  233 */     int ATT_MAXOCCURS1_D = attCount++;
/*  234 */     int ATT_MEMBER_T_N = attCount++;
/*  235 */     int ATT_MINOCCURS_D = attCount++;
/*  236 */     int ATT_MINOCCURS1_D = attCount++;
/*  237 */     int ATT_MIXED_D = attCount++;
/*  238 */     int ATT_MIXED_N = attCount++;
/*  239 */     int ATT_NAME_R = attCount++;
/*  240 */     int ATT_NAMESPACE_D = attCount++;
/*  241 */     int ATT_NAMESPACE_N = attCount++;
/*  242 */     int ATT_NILLABLE_D = attCount++;
/*  243 */     int ATT_PROCESS_C_D = attCount++;
/*  244 */     int ATT_PUBLIC_R = attCount++;
/*  245 */     int ATT_REF_R = attCount++;
/*  246 */     int ATT_REFER_R = attCount++;
/*  247 */     int ATT_SCHEMA_L_R = attCount++;
/*  248 */     int ATT_SCHEMA_L_N = attCount++;
/*  249 */     int ATT_SOURCE_N = attCount++;
/*  250 */     int ATT_SUBSTITUTION_G_N = attCount++;
/*  251 */     int ATT_SYSTEM_N = attCount++;
/*  252 */     int ATT_TARGET_N_N = attCount++;
/*  253 */     int ATT_TYPE_N = attCount++;
/*  254 */     int ATT_USE_D = attCount++;
/*  255 */     int ATT_VALUE_NNI_N = attCount++;
/*  256 */     int ATT_VALUE_PI_N = attCount++;
/*  257 */     int ATT_VALUE_STR_N = attCount++;
/*  258 */     int ATT_VALUE_WS_N = attCount++;
/*  259 */     int ATT_VERSION_N = attCount++;
/*  260 */     int ATT_XML_LANG = attCount++;
/*  261 */     int ATT_XPATH_R = attCount++;
/*  262 */     int ATT_XPATH1_R = attCount++;
/*      */ 
/*  265 */     OneAttr[] allAttrs = new OneAttr[attCount];
/*  266 */     allAttrs[ATT_ABSTRACT_D] = new OneAttr(SchemaSymbols.ATT_ABSTRACT, -15, ATTIDX_ABSTRACT, Boolean.FALSE);
/*      */ 
/*  270 */     allAttrs[ATT_ATTRIBUTE_FD_D] = new OneAttr(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, -6, ATTIDX_AFORMDEFAULT, INT_UNQUALIFIED);
/*      */ 
/*  274 */     allAttrs[ATT_BASE_R] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
/*      */ 
/*  278 */     allAttrs[ATT_BASE_N] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
/*      */ 
/*  282 */     allAttrs[ATT_BLOCK_N] = new OneAttr(SchemaSymbols.ATT_BLOCK, -1, ATTIDX_BLOCK, null);
/*      */ 
/*  286 */     allAttrs[ATT_BLOCK1_N] = new OneAttr(SchemaSymbols.ATT_BLOCK, -2, ATTIDX_BLOCK, null);
/*      */ 
/*  290 */     allAttrs[ATT_BLOCK_D_D] = new OneAttr(SchemaSymbols.ATT_BLOCKDEFAULT, -1, ATTIDX_BLOCKDEFAULT, INT_EMPTY_SET);
/*      */ 
/*  294 */     allAttrs[ATT_DEFAULT_N] = new OneAttr(SchemaSymbols.ATT_DEFAULT, 3, ATTIDX_DEFAULT, null);
/*      */ 
/*  298 */     allAttrs[ATT_ELEMENT_FD_D] = new OneAttr(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, -6, ATTIDX_EFORMDEFAULT, INT_UNQUALIFIED);
/*      */ 
/*  302 */     allAttrs[ATT_FINAL_N] = new OneAttr(SchemaSymbols.ATT_FINAL, -3, ATTIDX_FINAL, null);
/*      */ 
/*  306 */     allAttrs[ATT_FINAL1_N] = new OneAttr(SchemaSymbols.ATT_FINAL, -4, ATTIDX_FINAL, null);
/*      */ 
/*  310 */     allAttrs[ATT_FINAL_D_D] = new OneAttr(SchemaSymbols.ATT_FINALDEFAULT, -5, ATTIDX_FINALDEFAULT, INT_EMPTY_SET);
/*      */ 
/*  314 */     allAttrs[ATT_FIXED_N] = new OneAttr(SchemaSymbols.ATT_FIXED, 3, ATTIDX_FIXED, null);
/*      */ 
/*  318 */     allAttrs[ATT_FIXED_D] = new OneAttr(SchemaSymbols.ATT_FIXED, -15, ATTIDX_FIXED, Boolean.FALSE);
/*      */ 
/*  322 */     allAttrs[ATT_FORM_N] = new OneAttr(SchemaSymbols.ATT_FORM, -6, ATTIDX_FORM, null);
/*      */ 
/*  326 */     allAttrs[ATT_ID_N] = new OneAttr(SchemaSymbols.ATT_ID, 1, ATTIDX_ID, null);
/*      */ 
/*  330 */     allAttrs[ATT_ITEMTYPE_N] = new OneAttr(SchemaSymbols.ATT_ITEMTYPE, 2, ATTIDX_ITEMTYPE, null);
/*      */ 
/*  334 */     allAttrs[ATT_MAXOCCURS_D] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -7, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
/*      */ 
/*  338 */     allAttrs[ATT_MAXOCCURS1_D] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -8, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
/*      */ 
/*  342 */     allAttrs[ATT_MEMBER_T_N] = new OneAttr(SchemaSymbols.ATT_MEMBERTYPES, -9, ATTIDX_MEMBERTYPES, null);
/*      */ 
/*  346 */     allAttrs[ATT_MINOCCURS_D] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -16, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
/*      */ 
/*  350 */     allAttrs[ATT_MINOCCURS1_D] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -10, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
/*      */ 
/*  354 */     allAttrs[ATT_MIXED_D] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, Boolean.FALSE);
/*      */ 
/*  358 */     allAttrs[ATT_MIXED_N] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, null);
/*      */ 
/*  362 */     allAttrs[ATT_NAME_R] = new OneAttr(SchemaSymbols.ATT_NAME, 5, ATTIDX_NAME, null);
/*      */ 
/*  366 */     allAttrs[ATT_NAMESPACE_D] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, ATTIDX_NAMESPACE, INT_ANY_ANY);
/*      */ 
/*  370 */     allAttrs[ATT_NAMESPACE_N] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, 0, ATTIDX_NAMESPACE, null);
/*      */ 
/*  374 */     allAttrs[ATT_NILLABLE_D] = new OneAttr(SchemaSymbols.ATT_NILLABLE, -15, ATTIDX_NILLABLE, Boolean.FALSE);
/*      */ 
/*  378 */     allAttrs[ATT_PROCESS_C_D] = new OneAttr(SchemaSymbols.ATT_PROCESSCONTENTS, -12, ATTIDX_PROCESSCONTENTS, INT_ANY_STRICT);
/*      */ 
/*  382 */     allAttrs[ATT_PUBLIC_R] = new OneAttr(SchemaSymbols.ATT_PUBLIC, 4, ATTIDX_PUBLIC, null);
/*      */ 
/*  386 */     allAttrs[ATT_REF_R] = new OneAttr(SchemaSymbols.ATT_REF, 2, ATTIDX_REF, null);
/*      */ 
/*  390 */     allAttrs[ATT_REFER_R] = new OneAttr(SchemaSymbols.ATT_REFER, 2, ATTIDX_REFER, null);
/*      */ 
/*  394 */     allAttrs[ATT_SCHEMA_L_R] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
/*      */ 
/*  398 */     allAttrs[ATT_SCHEMA_L_N] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
/*      */ 
/*  402 */     allAttrs[ATT_SOURCE_N] = new OneAttr(SchemaSymbols.ATT_SOURCE, 0, ATTIDX_SOURCE, null);
/*      */ 
/*  406 */     allAttrs[ATT_SUBSTITUTION_G_N] = new OneAttr(SchemaSymbols.ATT_SUBSTITUTIONGROUP, 2, ATTIDX_SUBSGROUP, null);
/*      */ 
/*  410 */     allAttrs[ATT_SYSTEM_N] = new OneAttr(SchemaSymbols.ATT_SYSTEM, 0, ATTIDX_SYSTEM, null);
/*      */ 
/*  414 */     allAttrs[ATT_TARGET_N_N] = new OneAttr(SchemaSymbols.ATT_TARGETNAMESPACE, 0, ATTIDX_TARGETNAMESPACE, null);
/*      */ 
/*  418 */     allAttrs[ATT_TYPE_N] = new OneAttr(SchemaSymbols.ATT_TYPE, 2, ATTIDX_TYPE, null);
/*      */ 
/*  422 */     allAttrs[ATT_USE_D] = new OneAttr(SchemaSymbols.ATT_USE, -13, ATTIDX_USE, INT_USE_OPTIONAL);
/*      */ 
/*  426 */     allAttrs[ATT_VALUE_NNI_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -16, ATTIDX_VALUE, null);
/*      */ 
/*  430 */     allAttrs[ATT_VALUE_PI_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -17, ATTIDX_VALUE, null);
/*      */ 
/*  434 */     allAttrs[ATT_VALUE_STR_N] = new OneAttr(SchemaSymbols.ATT_VALUE, 3, ATTIDX_VALUE, null);
/*      */ 
/*  438 */     allAttrs[ATT_VALUE_WS_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -14, ATTIDX_VALUE, null);
/*      */ 
/*  442 */     allAttrs[ATT_VERSION_N] = new OneAttr(SchemaSymbols.ATT_VERSION, 4, ATTIDX_VERSION, null);
/*      */ 
/*  446 */     allAttrs[ATT_XML_LANG] = new OneAttr(SchemaSymbols.ATT_XML_LANG, 8, ATTIDX_XML_LANG, null);
/*      */ 
/*  450 */     allAttrs[ATT_XPATH_R] = new OneAttr(SchemaSymbols.ATT_XPATH, 6, ATTIDX_XPATH, null);
/*      */ 
/*  454 */     allAttrs[ATT_XPATH1_R] = new OneAttr(SchemaSymbols.ATT_XPATH, 7, ATTIDX_XPATH, null);
/*      */ 
/*  463 */     Container attrList = Container.getContainer(5);
/*      */ 
/*  465 */     attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
/*      */ 
/*  467 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
/*      */ 
/*  469 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  471 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  473 */     attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
/*  474 */     fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTE, attrList);
/*      */ 
/*  477 */     attrList = Container.getContainer(7);
/*      */ 
/*  479 */     attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
/*      */ 
/*  481 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
/*      */ 
/*  483 */     attrList.put(SchemaSymbols.ATT_FORM, allAttrs[ATT_FORM_N]);
/*      */ 
/*  485 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  487 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  489 */     attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
/*      */ 
/*  491 */     attrList.put(SchemaSymbols.ATT_USE, allAttrs[ATT_USE_D]);
/*  492 */     fEleAttrsMapL.put("attribute_n", attrList);
/*      */ 
/*  495 */     attrList = Container.getContainer(5);
/*      */ 
/*  497 */     attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
/*      */ 
/*  499 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
/*      */ 
/*  501 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  503 */     attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
/*      */ 
/*  505 */     attrList.put(SchemaSymbols.ATT_USE, allAttrs[ATT_USE_D]);
/*  506 */     fEleAttrsMapL.put("attribute_r", attrList);
/*      */ 
/*  509 */     attrList = Container.getContainer(10);
/*      */ 
/*  511 */     attrList.put(SchemaSymbols.ATT_ABSTRACT, allAttrs[ATT_ABSTRACT_D]);
/*      */ 
/*  513 */     attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK_N]);
/*      */ 
/*  515 */     attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
/*      */ 
/*  517 */     attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL_N]);
/*      */ 
/*  519 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
/*      */ 
/*  521 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  523 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  525 */     attrList.put(SchemaSymbols.ATT_NILLABLE, allAttrs[ATT_NILLABLE_D]);
/*      */ 
/*  527 */     attrList.put(SchemaSymbols.ATT_SUBSTITUTIONGROUP, allAttrs[ATT_SUBSTITUTION_G_N]);
/*      */ 
/*  529 */     attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
/*  530 */     fEleAttrsMapG.put(SchemaSymbols.ELT_ELEMENT, attrList);
/*      */ 
/*  533 */     attrList = Container.getContainer(10);
/*      */ 
/*  535 */     attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK_N]);
/*      */ 
/*  537 */     attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
/*      */ 
/*  539 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
/*      */ 
/*  541 */     attrList.put(SchemaSymbols.ATT_FORM, allAttrs[ATT_FORM_N]);
/*      */ 
/*  543 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  545 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
/*      */ 
/*  547 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
/*      */ 
/*  549 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  551 */     attrList.put(SchemaSymbols.ATT_NILLABLE, allAttrs[ATT_NILLABLE_D]);
/*      */ 
/*  553 */     attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
/*  554 */     fEleAttrsMapL.put("element_n", attrList);
/*      */ 
/*  557 */     attrList = Container.getContainer(4);
/*      */ 
/*  559 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  561 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
/*      */ 
/*  563 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
/*      */ 
/*  565 */     attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
/*  566 */     fEleAttrsMapL.put("element_r", attrList);
/*      */ 
/*  569 */     attrList = Container.getContainer(6);
/*      */ 
/*  571 */     attrList.put(SchemaSymbols.ATT_ABSTRACT, allAttrs[ATT_ABSTRACT_D]);
/*      */ 
/*  573 */     attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK1_N]);
/*      */ 
/*  575 */     attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL_N]);
/*      */ 
/*  577 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  579 */     attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_D]);
/*      */ 
/*  581 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*  582 */     fEleAttrsMapG.put(SchemaSymbols.ELT_COMPLEXTYPE, attrList);
/*      */ 
/*  585 */     attrList = Container.getContainer(4);
/*      */ 
/*  587 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  589 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  591 */     attrList.put(SchemaSymbols.ATT_PUBLIC, allAttrs[ATT_PUBLIC_R]);
/*      */ 
/*  593 */     attrList.put(SchemaSymbols.ATT_SYSTEM, allAttrs[ATT_SYSTEM_N]);
/*  594 */     fEleAttrsMapG.put(SchemaSymbols.ELT_NOTATION, attrList);
/*      */ 
/*  598 */     attrList = Container.getContainer(2);
/*      */ 
/*  600 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  602 */     attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_D]);
/*  603 */     fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXTYPE, attrList);
/*      */ 
/*  606 */     attrList = Container.getContainer(1);
/*      */ 
/*  608 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*  609 */     fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLECONTENT, attrList);
/*      */ 
/*  612 */     attrList = Container.getContainer(2);
/*      */ 
/*  614 */     attrList.put(SchemaSymbols.ATT_BASE, allAttrs[ATT_BASE_N]);
/*      */ 
/*  616 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*  617 */     fEleAttrsMapL.put(SchemaSymbols.ELT_RESTRICTION, attrList);
/*      */ 
/*  620 */     attrList = Container.getContainer(2);
/*      */ 
/*  622 */     attrList.put(SchemaSymbols.ATT_BASE, allAttrs[ATT_BASE_R]);
/*      */ 
/*  624 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*  625 */     fEleAttrsMapL.put(SchemaSymbols.ELT_EXTENSION, attrList);
/*      */ 
/*  628 */     attrList = Container.getContainer(2);
/*      */ 
/*  630 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  632 */     attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
/*  633 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, attrList);
/*      */ 
/*  636 */     attrList = Container.getContainer(3);
/*      */ 
/*  638 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  640 */     attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_D]);
/*      */ 
/*  642 */     attrList.put(SchemaSymbols.ATT_PROCESSCONTENTS, allAttrs[ATT_PROCESS_C_D]);
/*  643 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, attrList);
/*      */ 
/*  646 */     attrList = Container.getContainer(2);
/*      */ 
/*  648 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  650 */     attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_N]);
/*  651 */     fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, attrList);
/*      */ 
/*  654 */     attrList = Container.getContainer(2);
/*      */ 
/*  656 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  658 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*  659 */     fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, attrList);
/*      */ 
/*  662 */     attrList = Container.getContainer(2);
/*      */ 
/*  664 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  666 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*  667 */     fEleAttrsMapG.put(SchemaSymbols.ELT_GROUP, attrList);
/*      */ 
/*  670 */     attrList = Container.getContainer(4);
/*      */ 
/*  672 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  674 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
/*      */ 
/*  676 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
/*      */ 
/*  678 */     attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
/*  679 */     fEleAttrsMapL.put(SchemaSymbols.ELT_GROUP, attrList);
/*      */ 
/*  682 */     attrList = Container.getContainer(3);
/*      */ 
/*  684 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  686 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS1_D]);
/*      */ 
/*  688 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS1_D]);
/*  689 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ALL, attrList);
/*      */ 
/*  692 */     attrList = Container.getContainer(3);
/*      */ 
/*  694 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  696 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
/*      */ 
/*  698 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
/*  699 */     fEleAttrsMapL.put(SchemaSymbols.ELT_CHOICE, attrList);
/*      */ 
/*  701 */     fEleAttrsMapL.put(SchemaSymbols.ELT_SEQUENCE, attrList);
/*      */ 
/*  704 */     attrList = Container.getContainer(5);
/*      */ 
/*  706 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  708 */     attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
/*      */ 
/*  710 */     attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
/*      */ 
/*  712 */     attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_D]);
/*      */ 
/*  714 */     attrList.put(SchemaSymbols.ATT_PROCESSCONTENTS, allAttrs[ATT_PROCESS_C_D]);
/*  715 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ANY, attrList);
/*      */ 
/*  718 */     attrList = Container.getContainer(2);
/*      */ 
/*  720 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  722 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*  723 */     fEleAttrsMapL.put(SchemaSymbols.ELT_UNIQUE, attrList);
/*      */ 
/*  725 */     fEleAttrsMapL.put(SchemaSymbols.ELT_KEY, attrList);
/*      */ 
/*  728 */     attrList = Container.getContainer(3);
/*      */ 
/*  730 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  732 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*      */ 
/*  734 */     attrList.put(SchemaSymbols.ATT_REFER, allAttrs[ATT_REFER_R]);
/*  735 */     fEleAttrsMapL.put(SchemaSymbols.ELT_KEYREF, attrList);
/*      */ 
/*  738 */     attrList = Container.getContainer(2);
/*      */ 
/*  740 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  742 */     attrList.put(SchemaSymbols.ATT_XPATH, allAttrs[ATT_XPATH_R]);
/*  743 */     fEleAttrsMapL.put(SchemaSymbols.ELT_SELECTOR, attrList);
/*      */ 
/*  746 */     attrList = Container.getContainer(2);
/*      */ 
/*  748 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  750 */     attrList.put(SchemaSymbols.ATT_XPATH, allAttrs[ATT_XPATH1_R]);
/*  751 */     fEleAttrsMapL.put(SchemaSymbols.ELT_FIELD, attrList);
/*      */ 
/*  754 */     attrList = Container.getContainer(1);
/*      */ 
/*  756 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*  757 */     fEleAttrsMapG.put(SchemaSymbols.ELT_ANNOTATION, attrList);
/*      */ 
/*  759 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ANNOTATION, attrList);
/*      */ 
/*  762 */     attrList = Container.getContainer(1);
/*      */ 
/*  764 */     attrList.put(SchemaSymbols.ATT_SOURCE, allAttrs[ATT_SOURCE_N]);
/*  765 */     fEleAttrsMapG.put(SchemaSymbols.ELT_APPINFO, attrList);
/*  766 */     fEleAttrsMapL.put(SchemaSymbols.ELT_APPINFO, attrList);
/*      */ 
/*  769 */     attrList = Container.getContainer(2);
/*      */ 
/*  771 */     attrList.put(SchemaSymbols.ATT_SOURCE, allAttrs[ATT_SOURCE_N]);
/*      */ 
/*  773 */     attrList.put(SchemaSymbols.ATT_XML_LANG, allAttrs[ATT_XML_LANG]);
/*  774 */     fEleAttrsMapG.put(SchemaSymbols.ELT_DOCUMENTATION, attrList);
/*  775 */     fEleAttrsMapL.put(SchemaSymbols.ELT_DOCUMENTATION, attrList);
/*      */ 
/*  778 */     attrList = Container.getContainer(3);
/*      */ 
/*  780 */     attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL1_N]);
/*      */ 
/*  782 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  784 */     attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
/*  785 */     fEleAttrsMapG.put(SchemaSymbols.ELT_SIMPLETYPE, attrList);
/*      */ 
/*  788 */     attrList = Container.getContainer(2);
/*      */ 
/*  790 */     attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL1_N]);
/*      */ 
/*  792 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*  793 */     fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLETYPE, attrList);
/*      */ 
/*  799 */     attrList = Container.getContainer(2);
/*      */ 
/*  801 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  803 */     attrList.put(SchemaSymbols.ATT_ITEMTYPE, allAttrs[ATT_ITEMTYPE_N]);
/*  804 */     fEleAttrsMapL.put(SchemaSymbols.ELT_LIST, attrList);
/*      */ 
/*  807 */     attrList = Container.getContainer(2);
/*      */ 
/*  809 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  811 */     attrList.put(SchemaSymbols.ATT_MEMBERTYPES, allAttrs[ATT_MEMBER_T_N]);
/*  812 */     fEleAttrsMapL.put(SchemaSymbols.ELT_UNION, attrList);
/*      */ 
/*  815 */     attrList = Container.getContainer(8);
/*      */ 
/*  817 */     attrList.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, allAttrs[ATT_ATTRIBUTE_FD_D]);
/*      */ 
/*  819 */     attrList.put(SchemaSymbols.ATT_BLOCKDEFAULT, allAttrs[ATT_BLOCK_D_D]);
/*      */ 
/*  821 */     attrList.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, allAttrs[ATT_ELEMENT_FD_D]);
/*      */ 
/*  823 */     attrList.put(SchemaSymbols.ATT_FINALDEFAULT, allAttrs[ATT_FINAL_D_D]);
/*      */ 
/*  825 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  827 */     attrList.put(SchemaSymbols.ATT_TARGETNAMESPACE, allAttrs[ATT_TARGET_N_N]);
/*      */ 
/*  829 */     attrList.put(SchemaSymbols.ATT_VERSION, allAttrs[ATT_VERSION_N]);
/*      */ 
/*  831 */     attrList.put(SchemaSymbols.ATT_XML_LANG, allAttrs[ATT_XML_LANG]);
/*  832 */     fEleAttrsMapG.put(SchemaSymbols.ELT_SCHEMA, attrList);
/*      */ 
/*  835 */     attrList = Container.getContainer(2);
/*      */ 
/*  837 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  839 */     attrList.put(SchemaSymbols.ATT_SCHEMALOCATION, allAttrs[ATT_SCHEMA_L_R]);
/*  840 */     fEleAttrsMapG.put(SchemaSymbols.ELT_INCLUDE, attrList);
/*      */ 
/*  842 */     fEleAttrsMapG.put(SchemaSymbols.ELT_REDEFINE, attrList);
/*      */ 
/*  845 */     attrList = Container.getContainer(3);
/*      */ 
/*  847 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  849 */     attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_N]);
/*      */ 
/*  851 */     attrList.put(SchemaSymbols.ATT_SCHEMALOCATION, allAttrs[ATT_SCHEMA_L_N]);
/*  852 */     fEleAttrsMapG.put(SchemaSymbols.ELT_IMPORT, attrList);
/*      */ 
/*  855 */     attrList = Container.getContainer(3);
/*      */ 
/*  857 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  859 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_NNI_N]);
/*      */ 
/*  861 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
/*  862 */     fEleAttrsMapL.put(SchemaSymbols.ELT_LENGTH, attrList);
/*      */ 
/*  864 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MINLENGTH, attrList);
/*      */ 
/*  866 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MAXLENGTH, attrList);
/*      */ 
/*  868 */     fEleAttrsMapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, attrList);
/*      */ 
/*  871 */     attrList = Container.getContainer(3);
/*      */ 
/*  873 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  875 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_PI_N]);
/*      */ 
/*  877 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
/*  878 */     fEleAttrsMapL.put(SchemaSymbols.ELT_TOTALDIGITS, attrList);
/*      */ 
/*  881 */     attrList = Container.getContainer(2);
/*      */ 
/*  883 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  885 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
/*  886 */     fEleAttrsMapL.put(SchemaSymbols.ELT_PATTERN, attrList);
/*      */ 
/*  889 */     attrList = Container.getContainer(2);
/*      */ 
/*  891 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  893 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
/*  894 */     fEleAttrsMapL.put(SchemaSymbols.ELT_ENUMERATION, attrList);
/*      */ 
/*  897 */     attrList = Container.getContainer(3);
/*      */ 
/*  899 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  901 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_WS_N]);
/*      */ 
/*  903 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
/*  904 */     fEleAttrsMapL.put(SchemaSymbols.ELT_WHITESPACE, attrList);
/*      */ 
/*  907 */     attrList = Container.getContainer(3);
/*      */ 
/*  909 */     attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
/*      */ 
/*  911 */     attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
/*      */ 
/*  913 */     attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
/*  914 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, attrList);
/*      */ 
/*  916 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, attrList);
/*      */ 
/*  918 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MININCLUSIVE, attrList);
/*      */ 
/*  920 */     fEleAttrsMapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, attrList);
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.traversers.XSAttributeChecker
 * JD-Core Version:    0.6.2
 */