/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.security.AccessController;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.crypto.Data;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.NodeSetData;
/*     */ import javax.xml.crypto.OctetStreamData;
/*     */ import javax.xml.crypto.URIDereferencer;
/*     */ import javax.xml.crypto.URIReferenceException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dom.DOMURIReference;
/*     */ import javax.xml.crypto.dsig.DigestMethod;
/*     */ import javax.xml.crypto.dsig.Reference;
/*     */ import javax.xml.crypto.dsig.Transform;
/*     */ import javax.xml.crypto.dsig.TransformException;
/*     */ import javax.xml.crypto.dsig.TransformService;
/*     */ import javax.xml.crypto.dsig.XMLSignContext;
/*     */ import javax.xml.crypto.dsig.XMLSignatureException;
/*     */ import javax.xml.crypto.dsig.XMLValidateContext;
/*     */ import org.jcp.xml.dsig.internal.DigesterOutputStream;
/*     */ import org.w3c.dom.Attr;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class DOMReference extends DOMStructure
/*     */   implements Reference, DOMURIReference
/*     */ {
/*  75 */   private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Boolean run() {
/*  78 */       return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
/*     */     } } )).booleanValue();
/*     */ 
/*  83 */   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
/*     */   private final DigestMethod digestMethod;
/*     */   private final String id;
/*     */   private final List transforms;
/*     */   private List allTransforms;
/*     */   private final Data appliedTransformData;
/*     */   private Attr here;
/*     */   private final String uri;
/*     */   private final String type;
/*     */   private byte[] digestValue;
/*     */   private byte[] calcDigestValue;
/*     */   private Element refElem;
/*  96 */   private boolean digested = false;
/*  97 */   private boolean validated = false;
/*     */   private boolean validationStatus;
/*     */   private Data derefData;
/*     */   private InputStream dis;
/*     */   private MessageDigest md;
/*     */   private Provider provider;
/*     */ 
/*     */   public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List paramList, String paramString3, Provider paramProvider) {
/* 121 */     this(paramString1, paramString2, paramDigestMethod, null, null, paramList, paramString3, null, paramProvider);
/*     */   }
/*     */ 
/*     */   public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List paramList1, Data paramData, List paramList2, String paramString3, Provider paramProvider)
/*     */   {
/* 127 */     this(paramString1, paramString2, paramDigestMethod, paramList1, paramData, paramList2, paramString3, null, paramProvider);
/*     */   }
/*     */ 
/*     */   public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List paramList1, Data paramData, List paramList2, String paramString3, byte[] paramArrayOfByte, Provider paramProvider)
/*     */   {
/* 134 */     if (paramDigestMethod == null) {
/* 135 */       throw new NullPointerException("DigestMethod must be non-null");
/*     */     }
/* 137 */     this.allTransforms = new ArrayList();
/*     */     ArrayList localArrayList;
/*     */     int i;
/*     */     int j;
/* 138 */     if (paramList1 != null) {
/* 139 */       localArrayList = new ArrayList(paramList1);
/* 140 */       i = 0; for (j = localArrayList.size(); i < j; i++) {
/* 141 */         if (!(localArrayList.get(i) instanceof Transform)) {
/* 142 */           throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
/*     */         }
/*     */       }
/*     */ 
/* 146 */       this.allTransforms = localArrayList;
/*     */     }
/* 148 */     if (paramList2 == null) {
/* 149 */       this.transforms = Collections.EMPTY_LIST;
/*     */     } else {
/* 151 */       localArrayList = new ArrayList(paramList2);
/* 152 */       i = 0; for (j = localArrayList.size(); i < j; i++) {
/* 153 */         if (!(localArrayList.get(i) instanceof Transform)) {
/* 154 */           throw new ClassCastException("transforms[" + i + "] is not a valid type");
/*     */         }
/*     */       }
/*     */ 
/* 158 */       this.transforms = localArrayList;
/* 159 */       this.allTransforms.addAll(localArrayList);
/*     */     }
/* 161 */     this.digestMethod = paramDigestMethod;
/* 162 */     this.uri = paramString1;
/* 163 */     if ((paramString1 != null) && (!paramString1.equals(""))) {
/*     */       try {
/* 165 */         new URI(paramString1);
/*     */       } catch (URISyntaxException localURISyntaxException) {
/* 167 */         throw new IllegalArgumentException(localURISyntaxException.getMessage());
/*     */       }
/*     */     }
/* 170 */     this.type = paramString2;
/* 171 */     this.id = paramString3;
/* 172 */     if (paramArrayOfByte != null) {
/* 173 */       this.digestValue = ((byte[])paramArrayOfByte.clone());
/* 174 */       this.digested = true;
/*     */     }
/* 176 */     this.appliedTransformData = paramData;
/* 177 */     this.provider = paramProvider;
/*     */   }
/*     */ 
/*     */   public DOMReference(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/* 188 */     Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
/* 189 */     ArrayList localArrayList = new ArrayList(5);
/* 190 */     if (localElement1.getLocalName().equals("Transforms")) {
/* 191 */       localElement2 = DOMUtils.getFirstChildElement(localElement1);
/* 192 */       while (localElement2 != null) {
/* 193 */         localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
/*     */ 
/* 195 */         localElement2 = DOMUtils.getNextSiblingElement(localElement2);
/*     */       }
/* 197 */       localElement1 = DOMUtils.getNextSiblingElement(localElement1);
/*     */     }
/*     */ 
/* 201 */     Element localElement2 = localElement1;
/* 202 */     this.digestMethod = DOMDigestMethod.unmarshal(localElement2);
/*     */     try
/*     */     {
/* 206 */       Element localElement3 = DOMUtils.getNextSiblingElement(localElement2);
/* 207 */       this.digestValue = Base64.decode(localElement3);
/*     */     } catch (Base64DecodingException localBase64DecodingException) {
/* 209 */       throw new MarshalException(localBase64DecodingException);
/*     */     }
/*     */ 
/* 213 */     this.uri = DOMUtils.getAttributeValue(paramElement, "URI");
/* 214 */     this.id = DOMUtils.getAttributeValue(paramElement, "Id");
/*     */ 
/* 216 */     this.type = DOMUtils.getAttributeValue(paramElement, "Type");
/* 217 */     this.here = paramElement.getAttributeNodeNS(null, "URI");
/* 218 */     this.refElem = paramElement;
/* 219 */     this.transforms = localArrayList;
/* 220 */     this.allTransforms = localArrayList;
/* 221 */     this.appliedTransformData = null;
/* 222 */     this.provider = paramProvider;
/*     */   }
/*     */ 
/*     */   public DigestMethod getDigestMethod() {
/* 226 */     return this.digestMethod;
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 230 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getURI() {
/* 234 */     return this.uri;
/*     */   }
/*     */ 
/*     */   public String getType() {
/* 238 */     return this.type;
/*     */   }
/*     */ 
/*     */   public List getTransforms() {
/* 242 */     return Collections.unmodifiableList(this.allTransforms);
/*     */   }
/*     */ 
/*     */   public byte[] getDigestValue() {
/* 246 */     return this.digestValue == null ? null : (byte[])this.digestValue.clone();
/*     */   }
/*     */ 
/*     */   public byte[] getCalculatedDigestValue() {
/* 250 */     return this.calcDigestValue == null ? null : (byte[])this.calcDigestValue.clone();
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
/*     */     throws MarshalException
/*     */   {
/* 256 */     if (log.isLoggable(Level.FINE)) {
/* 257 */       log.log(Level.FINE, "Marshalling Reference");
/*     */     }
/* 259 */     Document localDocument = DOMUtils.getOwnerDocument(paramNode);
/*     */ 
/* 261 */     this.refElem = DOMUtils.createElement(localDocument, "Reference", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 265 */     DOMUtils.setAttributeID(this.refElem, "Id", this.id);
/* 266 */     DOMUtils.setAttribute(this.refElem, "URI", this.uri);
/* 267 */     DOMUtils.setAttribute(this.refElem, "Type", this.type);
/*     */ 
/* 270 */     if (!this.allTransforms.isEmpty()) {
/* 271 */       localElement = DOMUtils.createElement(localDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 273 */       this.refElem.appendChild(localElement);
/* 274 */       int i = 0; for (int j = this.allTransforms.size(); i < j; i++) {
/* 275 */         DOMStructure localDOMStructure = (DOMStructure)this.allTransforms.get(i);
/*     */ 
/* 277 */         localDOMStructure.marshal(localElement, paramString, paramDOMCryptoContext);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 282 */     ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, paramString, paramDOMCryptoContext);
/*     */ 
/* 285 */     if (log.isLoggable(Level.FINE)) {
/* 286 */       log.log(Level.FINE, "Adding digestValueElem");
/*     */     }
/* 288 */     Element localElement = DOMUtils.createElement(localDocument, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 290 */     if (this.digestValue != null) {
/* 291 */       localElement.appendChild(localDocument.createTextNode(Base64.encode(this.digestValue)));
/*     */     }
/*     */ 
/* 294 */     this.refElem.appendChild(localElement);
/*     */ 
/* 296 */     paramNode.appendChild(this.refElem);
/* 297 */     this.here = this.refElem.getAttributeNodeNS(null, "URI");
/*     */   }
/*     */ 
/*     */   public void digest(XMLSignContext paramXMLSignContext) throws XMLSignatureException
/*     */   {
/* 302 */     Data localData = null;
/* 303 */     if (this.appliedTransformData == null)
/* 304 */       localData = dereference(paramXMLSignContext);
/*     */     else {
/* 306 */       localData = this.appliedTransformData;
/*     */     }
/* 308 */     this.digestValue = transform(localData, paramXMLSignContext);
/*     */ 
/* 311 */     String str = Base64.encode(this.digestValue);
/* 312 */     if (log.isLoggable(Level.FINE)) {
/* 313 */       log.log(Level.FINE, "Reference object uri = " + this.uri);
/*     */     }
/* 315 */     Element localElement = DOMUtils.getLastChildElement(this.refElem);
/* 316 */     if (localElement == null) {
/* 317 */       throw new XMLSignatureException("DigestValue element expected");
/*     */     }
/* 319 */     DOMUtils.removeAllChildren(localElement);
/* 320 */     localElement.appendChild(this.refElem.getOwnerDocument().createTextNode(str));
/*     */ 
/* 323 */     this.digested = true;
/* 324 */     if (log.isLoggable(Level.FINE))
/* 325 */       log.log(Level.FINE, "Reference digesting completed");
/*     */   }
/*     */ 
/*     */   public boolean validate(XMLValidateContext paramXMLValidateContext)
/*     */     throws XMLSignatureException
/*     */   {
/* 331 */     if (paramXMLValidateContext == null) {
/* 332 */       throw new NullPointerException("validateContext cannot be null");
/*     */     }
/* 334 */     if (this.validated) {
/* 335 */       return this.validationStatus;
/*     */     }
/* 337 */     Data localData = dereference(paramXMLValidateContext);
/* 338 */     this.calcDigestValue = transform(localData, paramXMLValidateContext);
/*     */ 
/* 340 */     if (log.isLoggable(Level.FINE)) {
/* 341 */       log.log(Level.FINE, "Expected digest: " + Base64.encode(this.digestValue));
/*     */ 
/* 343 */       log.log(Level.FINE, "Actual digest: " + Base64.encode(this.calcDigestValue));
/*     */     }
/*     */ 
/* 347 */     this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
/* 348 */     this.validated = true;
/* 349 */     return this.validationStatus;
/*     */   }
/*     */ 
/*     */   public Data getDereferencedData() {
/* 353 */     return this.derefData;
/*     */   }
/*     */ 
/*     */   public InputStream getDigestInputStream() {
/* 357 */     return this.dis;
/*     */   }
/*     */ 
/*     */   private Data dereference(XMLCryptoContext paramXMLCryptoContext) throws XMLSignatureException
/*     */   {
/* 362 */     Data localData = null;
/*     */ 
/* 365 */     URIDereferencer localURIDereferencer = paramXMLCryptoContext.getURIDereferencer();
/* 366 */     if (localURIDereferencer == null)
/* 367 */       localURIDereferencer = DOMURIDereferencer.INSTANCE;
/*     */     try
/*     */     {
/* 370 */       localData = localURIDereferencer.dereference(this, paramXMLCryptoContext);
/* 371 */       if (log.isLoggable(Level.FINE)) {
/* 372 */         log.log(Level.FINE, "URIDereferencer class name: " + localURIDereferencer.getClass().getName());
/*     */ 
/* 374 */         log.log(Level.FINE, "Data class name: " + localData.getClass().getName());
/*     */       }
/*     */     }
/*     */     catch (URIReferenceException localURIReferenceException) {
/* 378 */       throw new XMLSignatureException(localURIReferenceException);
/*     */     }
/*     */ 
/* 381 */     return localData;
/*     */   }
/*     */ 
/*     */   private byte[] transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
/*     */     throws XMLSignatureException
/*     */   {
/* 387 */     if (this.md == null) {
/*     */       try {
/* 389 */         this.md = MessageDigest.getInstance(((DOMDigestMethod)this.digestMethod).getMessageDigestAlgorithm());
/*     */       }
/*     */       catch (NoSuchAlgorithmException localNoSuchAlgorithmException1) {
/* 392 */         throw new XMLSignatureException(localNoSuchAlgorithmException1);
/*     */       }
/*     */     }
/* 395 */     this.md.reset();
/*     */ 
/* 397 */     Boolean localBoolean = (Boolean)paramXMLCryptoContext.getProperty("javax.xml.crypto.dsig.cacheReference");
/*     */     DigesterOutputStream localDigesterOutputStream;
/* 399 */     if ((localBoolean != null) && (localBoolean.booleanValue() == true)) {
/* 400 */       this.derefData = copyDerefData(paramData);
/* 401 */       localDigesterOutputStream = new DigesterOutputStream(this.md, true);
/*     */     } else {
/* 403 */       localDigesterOutputStream = new DigesterOutputStream(this.md);
/*     */     }
/* 405 */     UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localDigesterOutputStream);
/* 406 */     Data localData = paramData;
/* 407 */     int i = 0;
/*     */     Object localObject1;
/* 407 */     for (int j = this.transforms.size(); i < j; i++) {
/* 408 */       localObject1 = (DOMTransform)this.transforms.get(i);
/*     */       try {
/* 410 */         if (i < j - 1)
/* 411 */           localData = ((DOMTransform)localObject1).transform(localData, paramXMLCryptoContext);
/*     */         else
/* 413 */           localData = ((DOMTransform)localObject1).transform(localData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
/*     */       }
/*     */       catch (TransformException localTransformException) {
/* 416 */         throw new XMLSignatureException(localTransformException);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 421 */       if (localData != null)
/*     */       {
/* 425 */         boolean bool = useC14N11;
/* 426 */         localObject1 = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
/*     */         Object localObject2;
/* 427 */         if ((paramXMLCryptoContext instanceof XMLSignContext))
/* 428 */           if (!bool) {
/* 429 */             localObject2 = (Boolean)paramXMLCryptoContext.getProperty("com.sun.org.apache.xml.internal.security.useC14N11");
/*     */ 
/* 431 */             bool = (localObject2 != null) && (((Boolean)localObject2).booleanValue() == true);
/* 432 */             if (bool)
/* 433 */               localObject1 = "http://www.w3.org/2006/12/xml-c14n11";
/*     */           }
/*     */           else {
/* 436 */             localObject1 = "http://www.w3.org/2006/12/xml-c14n11";
/*     */           }
/*     */         XMLSignatureInput localXMLSignatureInput;
/* 439 */         if ((localData instanceof ApacheData)) {
/* 440 */           localXMLSignatureInput = ((ApacheData)localData).getXMLSignatureInput();
/* 441 */         } else if ((localData instanceof OctetStreamData)) {
/* 442 */           localXMLSignatureInput = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
/*     */         }
/* 444 */         else if ((localData instanceof NodeSetData)) {
/* 445 */           localObject2 = null;
/*     */           try {
/* 447 */             localObject2 = TransformService.getInstance((String)localObject1, "DOM");
/*     */           } catch (NoSuchAlgorithmException localNoSuchAlgorithmException2) {
/* 449 */             localObject2 = TransformService.getInstance((String)localObject1, "DOM", this.provider);
/*     */           }
/*     */ 
/* 452 */           localData = ((TransformService)localObject2).transform(localData, paramXMLCryptoContext);
/* 453 */           localXMLSignatureInput = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
/*     */         }
/*     */         else {
/* 456 */           throw new XMLSignatureException("unrecognized Data type");
/*     */         }
/* 458 */         if (((paramXMLCryptoContext instanceof XMLSignContext)) && (bool) && (!localXMLSignatureInput.isOctetStream()) && (!localXMLSignatureInput.isOutputStreamSet()))
/*     */         {
/* 460 */           localObject2 = new DOMTransform(TransformService.getInstance((String)localObject1, "DOM"));
/*     */ 
/* 462 */           Element localElement = null;
/* 463 */           String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
/* 464 */           if (this.allTransforms.isEmpty()) {
/* 465 */             localElement = DOMUtils.createElement(this.refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", str);
/*     */ 
/* 468 */             this.refElem.insertBefore(localElement, DOMUtils.getFirstChildElement(this.refElem));
/*     */           }
/*     */           else {
/* 471 */             localElement = DOMUtils.getFirstChildElement(this.refElem);
/*     */           }
/* 473 */           ((DOMTransform)localObject2).marshal(localElement, str, (DOMCryptoContext)paramXMLCryptoContext);
/* 474 */           this.allTransforms.add(localObject2);
/* 475 */           localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream, true);
/*     */         } else {
/* 477 */           localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream);
/*     */         }
/*     */       }
/* 480 */       localUnsyncBufferedOutputStream.flush();
/* 481 */       if ((localBoolean != null) && (localBoolean.booleanValue() == true)) {
/* 482 */         this.dis = localDigesterOutputStream.getInputStream();
/*     */       }
/* 484 */       return localDigesterOutputStream.getDigestValue();
/*     */     } catch (Exception localException) {
/* 486 */       throw new XMLSignatureException(localException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Node getHere() {
/* 491 */     return this.here;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 495 */     if (this == paramObject) {
/* 496 */       return true;
/*     */     }
/*     */ 
/* 499 */     if (!(paramObject instanceof DOMURIReference)) {
/* 500 */       return false;
/*     */     }
/* 502 */     Reference localReference = (DOMURIReference)paramObject;
/*     */ 
/* 504 */     boolean bool1 = this.id == null ? false : localReference.getId() == null ? true : this.id.equals(localReference.getId());
/*     */ 
/* 506 */     boolean bool2 = this.uri == null ? false : localReference.getURI() == null ? true : this.uri.equals(localReference.getURI());
/*     */ 
/* 508 */     boolean bool3 = this.type == null ? false : localReference.getType() == null ? true : this.type.equals(localReference.getType());
/*     */ 
/* 510 */     boolean bool4 = Arrays.equals(this.digestValue, localReference.getDigestValue());
/*     */ 
/* 513 */     return (this.digestMethod.equals(localReference.getDigestMethod())) && (bool1) && (bool2) && (bool3) && (this.allTransforms.equals(localReference.getTransforms()));
/*     */   }
/*     */ 
/*     */   boolean isDigested()
/*     */   {
/* 518 */     return this.digested;
/*     */   }
/*     */ 
/*     */   private static Data copyDerefData(Data paramData) {
/* 522 */     if ((paramData instanceof ApacheData))
/*     */     {
/* 524 */       ApacheData localApacheData = (ApacheData)paramData;
/* 525 */       XMLSignatureInput localXMLSignatureInput = localApacheData.getXMLSignatureInput();
/* 526 */       if (localXMLSignatureInput.isNodeSet())
/*     */         try {
/* 528 */           Set localSet = localXMLSignatureInput.getNodeSet();
/* 529 */           return new NodeSetData() {
/* 530 */             public Iterator iterator() { return this.val$s.iterator(); }
/*     */           };
/*     */         }
/*     */         catch (Exception localException) {
/* 534 */           log.log(Level.WARNING, "cannot cache dereferenced data: " + localException);
/*     */ 
/* 536 */           return null;
/*     */         }
/* 538 */       if (localXMLSignatureInput.isElement()) {
/* 539 */         return new DOMSubTreeData(localXMLSignatureInput.getSubNode(), localXMLSignatureInput.isExcludeComments());
/*     */       }
/* 541 */       if ((localXMLSignatureInput.isOctetStream()) || (localXMLSignatureInput.isByteArray())) {
/*     */         try {
/* 543 */           return new OctetStreamData(localXMLSignatureInput.getOctetStream(), localXMLSignatureInput.getSourceURI(), localXMLSignatureInput.getMIMEType());
/*     */         }
/*     */         catch (IOException localIOException)
/*     */         {
/* 547 */           log.log(Level.WARNING, "cannot cache dereferenced data: " + localIOException);
/*     */ 
/* 549 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 553 */     return paramData;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMReference
 * JD-Core Version:    0.6.2
 */