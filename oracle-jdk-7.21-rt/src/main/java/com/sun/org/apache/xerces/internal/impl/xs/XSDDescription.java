/*     */ package com.sun.org.apache.xerces.internal.impl.xs;
/*     */ 
/*     */ import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
/*     */ import com.sun.org.apache.xerces.internal.xni.QName;
/*     */ import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
/*     */ import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;
/*     */ 
/*     */ public class XSDDescription extends XMLResourceIdentifierImpl
/*     */   implements XMLSchemaDescription
/*     */ {
/*     */   public static final short CONTEXT_INITIALIZE = -1;
/*     */   public static final short CONTEXT_INCLUDE = 0;
/*     */   public static final short CONTEXT_REDEFINE = 1;
/*     */   public static final short CONTEXT_IMPORT = 2;
/*     */   public static final short CONTEXT_PREPARSE = 3;
/*     */   public static final short CONTEXT_INSTANCE = 4;
/*     */   public static final short CONTEXT_ELEMENT = 5;
/*     */   public static final short CONTEXT_ATTRIBUTE = 6;
/*     */   public static final short CONTEXT_XSITYPE = 7;
/*     */   protected short fContextType;
/*     */   protected String[] fLocationHints;
/*     */   protected QName fTriggeringComponent;
/*     */   protected QName fEnclosedElementName;
/*     */   protected XMLAttributes fAttributes;
/*     */ 
/*     */   public String getGrammarType()
/*     */   {
/* 109 */     return "http://www.w3.org/2001/XMLSchema";
/*     */   }
/*     */ 
/*     */   public short getContextType()
/*     */   {
/* 119 */     return this.fContextType;
/*     */   }
/*     */ 
/*     */   public String getTargetNamespace()
/*     */   {
/* 130 */     return this.fNamespace;
/*     */   }
/*     */ 
/*     */   public String[] getLocationHints()
/*     */   {
/* 142 */     return this.fLocationHints;
/*     */   }
/*     */ 
/*     */   public QName getTriggeringComponent()
/*     */   {
/* 153 */     return this.fTriggeringComponent;
/*     */   }
/*     */ 
/*     */   public QName getEnclosingElementName()
/*     */   {
/* 163 */     return this.fEnclosedElementName;
/*     */   }
/*     */ 
/*     */   public XMLAttributes getAttributes()
/*     */   {
/* 173 */     return this.fAttributes;
/*     */   }
/*     */ 
/*     */   public boolean fromInstance() {
/* 177 */     return (this.fContextType == 6) || (this.fContextType == 5) || (this.fContextType == 4) || (this.fContextType == 7);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object descObj)
/*     */   {
/* 191 */     if (!(descObj instanceof XMLSchemaDescription)) return false;
/* 192 */     XMLSchemaDescription desc = (XMLSchemaDescription)descObj;
/* 193 */     if (this.fNamespace != null) {
/* 194 */       return this.fNamespace.equals(desc.getTargetNamespace());
/*     */     }
/* 196 */     return desc.getTargetNamespace() == null;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 205 */     return this.fNamespace == null ? 0 : this.fNamespace.hashCode();
/*     */   }
/*     */ 
/*     */   public void setContextType(short contextType) {
/* 209 */     this.fContextType = contextType;
/*     */   }
/*     */ 
/*     */   public void setTargetNamespace(String targetNamespace) {
/* 213 */     this.fNamespace = targetNamespace;
/*     */   }
/*     */ 
/*     */   public void setLocationHints(String[] locationHints) {
/* 217 */     int length = locationHints.length;
/* 218 */     this.fLocationHints = new String[length];
/* 219 */     System.arraycopy(locationHints, 0, this.fLocationHints, 0, length);
/*     */   }
/*     */ 
/*     */   public void setTriggeringComponent(QName triggeringComponent)
/*     */   {
/* 224 */     this.fTriggeringComponent = triggeringComponent;
/*     */   }
/*     */ 
/*     */   public void setEnclosingElementName(QName enclosedElementName) {
/* 228 */     this.fEnclosedElementName = enclosedElementName;
/*     */   }
/*     */ 
/*     */   public void setAttributes(XMLAttributes attributes) {
/* 232 */     this.fAttributes = attributes;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 239 */     super.clear();
/* 240 */     this.fContextType = -1;
/* 241 */     this.fLocationHints = null;
/* 242 */     this.fTriggeringComponent = null;
/* 243 */     this.fEnclosedElementName = null;
/* 244 */     this.fAttributes = null;
/*     */   }
/*     */ 
/*     */   public XSDDescription makeClone() {
/* 248 */     XSDDescription desc = new XSDDescription();
/* 249 */     desc.fAttributes = this.fAttributes;
/* 250 */     desc.fBaseSystemId = this.fBaseSystemId;
/* 251 */     desc.fContextType = this.fContextType;
/* 252 */     desc.fEnclosedElementName = this.fEnclosedElementName;
/* 253 */     desc.fExpandedSystemId = this.fExpandedSystemId;
/* 254 */     desc.fLiteralSystemId = this.fLiteralSystemId;
/* 255 */     desc.fLocationHints = this.fLocationHints;
/* 256 */     desc.fPublicId = this.fPublicId;
/* 257 */     desc.fNamespace = this.fNamespace;
/* 258 */     desc.fTriggeringComponent = this.fTriggeringComponent;
/* 259 */     return desc;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.org.apache.xerces.internal.impl.xs.XSDDescription
 * JD-Core Version:    0.6.2
 */