/*     */ package org.jcp.xml.dsig.internal.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.security.Init;
/*     */ import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
/*     */ import com.sun.org.apache.xml.internal.security.utils.Base64;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.xml.crypto.KeySelector;
/*     */ import javax.xml.crypto.KeySelector.Purpose;
/*     */ import javax.xml.crypto.KeySelectorException;
/*     */ import javax.xml.crypto.KeySelectorResult;
/*     */ import javax.xml.crypto.MarshalException;
/*     */ import javax.xml.crypto.XMLCryptoContext;
/*     */ import javax.xml.crypto.XMLStructure;
/*     */ import javax.xml.crypto.dom.DOMCryptoContext;
/*     */ import javax.xml.crypto.dsig.Manifest;
/*     */ import javax.xml.crypto.dsig.Reference;
/*     */ import javax.xml.crypto.dsig.SignatureMethod;
/*     */ import javax.xml.crypto.dsig.SignedInfo;
/*     */ import javax.xml.crypto.dsig.Transform;
/*     */ import javax.xml.crypto.dsig.XMLObject;
/*     */ import javax.xml.crypto.dsig.XMLSignContext;
/*     */ import javax.xml.crypto.dsig.XMLSignature;
/*     */ import javax.xml.crypto.dsig.XMLSignature.SignatureValue;
/*     */ import javax.xml.crypto.dsig.XMLSignatureException;
/*     */ import javax.xml.crypto.dsig.XMLValidateContext;
/*     */ import javax.xml.crypto.dsig.dom.DOMSignContext;
/*     */ import javax.xml.crypto.dsig.dom.DOMValidateContext;
/*     */ import javax.xml.crypto.dsig.keyinfo.KeyInfo;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public final class DOMXMLSignature extends DOMStructure
/*     */   implements XMLSignature
/*     */ {
/*  69 */   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
/*     */   private String id;
/*     */   private XMLSignature.SignatureValue sv;
/*     */   private KeyInfo ki;
/*     */   private List objects;
/*     */   private SignedInfo si;
/*  75 */   private Document ownerDoc = null;
/*  76 */   private Element localSigElem = null;
/*  77 */   private Element sigElem = null;
/*     */   private boolean validationStatus;
/*  79 */   private boolean validated = false;
/*     */   private KeySelectorResult ksr;
/*     */   private HashMap signatureIdMap;
/*     */ 
/*     */   public DOMXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo, List paramList, String paramString1, String paramString2)
/*     */   {
/* 103 */     if (paramSignedInfo == null) {
/* 104 */       throw new NullPointerException("signedInfo cannot be null");
/*     */     }
/* 106 */     this.si = paramSignedInfo;
/* 107 */     this.id = paramString1;
/* 108 */     this.sv = new DOMSignatureValue(paramString2);
/* 109 */     if (paramList == null) {
/* 110 */       this.objects = Collections.EMPTY_LIST;
/*     */     } else {
/* 112 */       ArrayList localArrayList = new ArrayList(paramList);
/* 113 */       int i = 0; for (int j = localArrayList.size(); i < j; i++) {
/* 114 */         if (!(localArrayList.get(i) instanceof XMLObject)) {
/* 115 */           throw new ClassCastException("objs[" + i + "] is not an XMLObject");
/*     */         }
/*     */       }
/*     */ 
/* 119 */       this.objects = Collections.unmodifiableList(localArrayList);
/*     */     }
/* 121 */     this.ki = paramKeyInfo;
/*     */   }
/*     */ 
/*     */   public DOMXMLSignature(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
/*     */     throws MarshalException
/*     */   {
/* 132 */     this.localSigElem = paramElement;
/* 133 */     this.ownerDoc = this.localSigElem.getOwnerDocument();
/*     */ 
/* 136 */     this.id = DOMUtils.getAttributeValue(this.localSigElem, "Id");
/*     */ 
/* 139 */     Element localElement1 = DOMUtils.getFirstChildElement(this.localSigElem);
/* 140 */     this.si = new DOMSignedInfo(localElement1, paramXMLCryptoContext, paramProvider);
/*     */ 
/* 143 */     Element localElement2 = DOMUtils.getNextSiblingElement(localElement1);
/* 144 */     this.sv = new DOMSignatureValue(localElement2);
/*     */ 
/* 147 */     Element localElement3 = DOMUtils.getNextSiblingElement(localElement2);
/* 148 */     if ((localElement3 != null) && (localElement3.getLocalName().equals("KeyInfo"))) {
/* 149 */       this.ki = new DOMKeyInfo(localElement3, paramXMLCryptoContext, paramProvider);
/* 150 */       localElement3 = DOMUtils.getNextSiblingElement(localElement3);
/*     */     }
/*     */ 
/* 154 */     if (localElement3 == null) {
/* 155 */       this.objects = Collections.EMPTY_LIST;
/*     */     } else {
/* 157 */       ArrayList localArrayList = new ArrayList();
/* 158 */       while (localElement3 != null) {
/* 159 */         localArrayList.add(new DOMXMLObject(localElement3, paramXMLCryptoContext, paramProvider));
/*     */ 
/* 161 */         localElement3 = DOMUtils.getNextSiblingElement(localElement3);
/*     */       }
/* 163 */       this.objects = Collections.unmodifiableList(localArrayList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId() {
/* 168 */     return this.id;
/*     */   }
/*     */ 
/*     */   public KeyInfo getKeyInfo() {
/* 172 */     return this.ki;
/*     */   }
/*     */ 
/*     */   public SignedInfo getSignedInfo() {
/* 176 */     return this.si;
/*     */   }
/*     */ 
/*     */   public List getObjects() {
/* 180 */     return this.objects;
/*     */   }
/*     */ 
/*     */   public XMLSignature.SignatureValue getSignatureValue() {
/* 184 */     return this.sv;
/*     */   }
/*     */ 
/*     */   public KeySelectorResult getKeySelectorResult() {
/* 188 */     return this.ksr;
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 193 */     marshal(paramNode, null, paramString, paramDOMCryptoContext);
/*     */   }
/*     */ 
/*     */   public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException
/*     */   {
/* 198 */     this.ownerDoc = DOMUtils.getOwnerDocument(paramNode1);
/*     */ 
/* 200 */     this.sigElem = DOMUtils.createElement(this.ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 204 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 205 */       this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */     else {
/* 208 */       this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString, "http://www.w3.org/2000/09/xmldsig#");
/*     */     }
/*     */ 
/* 214 */     ((DOMSignedInfo)this.si).marshal(this.sigElem, paramString, paramDOMCryptoContext);
/*     */ 
/* 217 */     ((DOMSignatureValue)this.sv).marshal(this.sigElem, paramString, paramDOMCryptoContext);
/*     */ 
/* 220 */     if (this.ki != null) {
/* 221 */       ((DOMKeyInfo)this.ki).marshal(this.sigElem, null, paramString, paramDOMCryptoContext);
/*     */     }
/*     */ 
/* 225 */     int i = 0; for (int j = this.objects.size(); i < j; i++) {
/* 226 */       ((DOMXMLObject)this.objects.get(i)).marshal(this.sigElem, paramString, paramDOMCryptoContext);
/*     */     }
/*     */ 
/* 230 */     DOMUtils.setAttributeID(this.sigElem, "Id", this.id);
/*     */ 
/* 232 */     paramNode1.insertBefore(this.sigElem, paramNode2);
/*     */   }
/*     */ 
/*     */   public boolean validate(XMLValidateContext paramXMLValidateContext)
/*     */     throws XMLSignatureException
/*     */   {
/* 238 */     if (paramXMLValidateContext == null) {
/* 239 */       throw new NullPointerException("validateContext is null");
/*     */     }
/*     */ 
/* 242 */     if (!(paramXMLValidateContext instanceof DOMValidateContext)) {
/* 243 */       throw new ClassCastException("validateContext must be of type DOMValidateContext");
/*     */     }
/*     */ 
/* 247 */     if (this.validated) {
/* 248 */       return this.validationStatus;
/*     */     }
/*     */ 
/* 252 */     boolean bool1 = this.sv.validate(paramXMLValidateContext);
/* 253 */     if (!bool1) {
/* 254 */       this.validationStatus = false;
/* 255 */       this.validated = true;
/* 256 */       return this.validationStatus;
/*     */     }
/*     */ 
/* 260 */     List localList1 = this.si.getReferences();
/* 261 */     boolean bool2 = true;
/* 262 */     int i = 0; for (int j = localList1.size(); (bool2) && (i < j); i++) {
/* 263 */       Reference localReference1 = (Reference)localList1.get(i);
/* 264 */       boolean bool3 = localReference1.validate(paramXMLValidateContext);
/* 265 */       if (log.isLoggable(Level.FINE)) {
/* 266 */         log.log(Level.FINE, "Reference[" + localReference1.getURI() + "] is valid: " + bool3);
/*     */       }
/*     */ 
/* 269 */       bool2 &= bool3;
/*     */     }
/* 271 */     if (!bool2) {
/* 272 */       if (log.isLoggable(Level.FINE)) {
/* 273 */         log.log(Level.FINE, "Couldn't validate the References");
/*     */       }
/* 275 */       this.validationStatus = false;
/* 276 */       this.validated = true;
/* 277 */       return this.validationStatus;
/*     */     }
/*     */ 
/* 281 */     i = 1;
/* 282 */     if (Boolean.TRUE.equals(paramXMLValidateContext.getProperty("org.jcp.xml.dsig.validateManifests")))
/*     */     {
/* 285 */       j = 0; for (int k = this.objects.size(); (i != 0) && (j < k); j++) {
/* 286 */         XMLObject localXMLObject = (XMLObject)this.objects.get(j);
/* 287 */         List localList2 = localXMLObject.getContent();
/* 288 */         int m = localList2.size();
/* 289 */         for (int n = 0; (i != 0) && (n < m); n++) {
/* 290 */           XMLStructure localXMLStructure = (XMLStructure)localList2.get(n);
/* 291 */           if ((localXMLStructure instanceof Manifest)) {
/* 292 */             if (log.isLoggable(Level.FINE)) {
/* 293 */               log.log(Level.FINE, "validating manifest");
/*     */             }
/* 295 */             Manifest localManifest = (Manifest)localXMLStructure;
/* 296 */             List localList3 = localManifest.getReferences();
/* 297 */             int i1 = localList3.size();
/* 298 */             for (int i2 = 0; (i != 0) && (i2 < i1); i2++) {
/* 299 */               Reference localReference2 = (Reference)localList3.get(i2);
/* 300 */               int i3 = localReference2.validate(paramXMLValidateContext);
/* 301 */               if (log.isLoggable(Level.FINE)) {
/* 302 */                 log.log(Level.FINE, "Manifest ref[" + localReference2.getURI() + "] is valid: " + i3);
/*     */               }
/*     */ 
/* 305 */               i &= i3;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 312 */     this.validationStatus = i;
/* 313 */     this.validated = true;
/* 314 */     return this.validationStatus;
/*     */   }
/*     */ 
/*     */   public void sign(XMLSignContext paramXMLSignContext) throws MarshalException, XMLSignatureException
/*     */   {
/* 319 */     if (paramXMLSignContext == null) {
/* 320 */       throw new NullPointerException("signContext cannot be null");
/*     */     }
/* 322 */     DOMSignContext localDOMSignContext = (DOMSignContext)paramXMLSignContext;
/* 323 */     if (localDOMSignContext != null) {
/* 324 */       marshal(localDOMSignContext.getParent(), localDOMSignContext.getNextSibling(), DOMUtils.getSignaturePrefix(localDOMSignContext), localDOMSignContext);
/*     */     }
/*     */ 
/* 329 */     ArrayList localArrayList = new ArrayList();
/*     */ 
/* 333 */     this.signatureIdMap = new HashMap();
/* 334 */     this.signatureIdMap.put(this.id, this);
/* 335 */     this.signatureIdMap.put(this.si.getId(), this.si);
/* 336 */     List localList1 = this.si.getReferences();
/* 337 */     int i = 0;
/*     */     Object localObject;
/* 337 */     for (int j = localList1.size(); i < j; i++) {
/* 338 */       localObject = (Reference)localList1.get(i);
/* 339 */       this.signatureIdMap.put(((Reference)localObject).getId(), localObject);
/*     */     }
/* 341 */     i = 0; for (j = this.objects.size(); i < j; i++) {
/* 342 */       localObject = (XMLObject)this.objects.get(i);
/* 343 */       this.signatureIdMap.put(((XMLObject)localObject).getId(), localObject);
/* 344 */       List localList2 = ((XMLObject)localObject).getContent();
/* 345 */       int k = 0; for (int m = localList2.size(); k < m; k++) {
/* 346 */         XMLStructure localXMLStructure = (XMLStructure)localList2.get(k);
/* 347 */         if ((localXMLStructure instanceof Manifest)) {
/* 348 */           Manifest localManifest = (Manifest)localXMLStructure;
/* 349 */           this.signatureIdMap.put(localManifest.getId(), localManifest);
/* 350 */           List localList3 = localManifest.getReferences();
/* 351 */           int n = 0; for (int i1 = localList3.size(); n < i1; n++) {
/* 352 */             Reference localReference = (Reference)localList3.get(n);
/* 353 */             localArrayList.add(localReference);
/* 354 */             this.signatureIdMap.put(localReference.getId(), localReference);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 361 */     localArrayList.addAll(this.si.getReferences());
/*     */ 
/* 364 */     i = 0; for (j = localArrayList.size(); i < j; i++) {
/* 365 */       localObject = (DOMReference)localArrayList.get(i);
/* 366 */       digestReference((DOMReference)localObject, paramXMLSignContext);
/*     */     }
/*     */ 
/* 370 */     i = 0; for (j = localArrayList.size(); i < j; i++) {
/* 371 */       localObject = (DOMReference)localArrayList.get(i);
/* 372 */       if (!((DOMReference)localObject).isDigested())
/*     */       {
/* 375 */         ((DOMReference)localObject).digest(paramXMLSignContext);
/*     */       }
/*     */     }
/* 378 */     Key localKey = null;
/* 379 */     KeySelectorResult localKeySelectorResult = null;
/*     */     try {
/* 381 */       localKeySelectorResult = paramXMLSignContext.getKeySelector().select(this.ki, KeySelector.Purpose.SIGN, this.si.getSignatureMethod(), paramXMLSignContext);
/*     */ 
/* 384 */       localKey = localKeySelectorResult.getKey();
/* 385 */       if (localKey == null)
/* 386 */         throw new XMLSignatureException("the keySelector did not find a signing key");
/*     */     }
/*     */     catch (KeySelectorException localKeySelectorException)
/*     */     {
/* 390 */       throw new XMLSignatureException("cannot find signing key", localKeySelectorException);
/*     */     }
/*     */ 
/* 394 */     byte[] arrayOfByte = null;
/*     */     try {
/* 396 */       arrayOfByte = ((DOMSignatureMethod)this.si.getSignatureMethod()).sign(localKey, (DOMSignedInfo)this.si, paramXMLSignContext);
/*     */     }
/*     */     catch (InvalidKeyException localInvalidKeyException) {
/* 399 */       throw new XMLSignatureException(localInvalidKeyException);
/*     */     }
/*     */ 
/* 402 */     if (log.isLoggable(Level.FINE)) {
/* 403 */       log.log(Level.FINE, "SignatureValue = " + arrayOfByte);
/*     */     }
/* 405 */     ((DOMSignatureValue)this.sv).setValue(arrayOfByte);
/*     */ 
/* 407 */     this.localSigElem = this.sigElem;
/* 408 */     this.ksr = localKeySelectorResult;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 412 */     if (this == paramObject) {
/* 413 */       return true;
/*     */     }
/*     */ 
/* 416 */     if (!(paramObject instanceof XMLSignature)) {
/* 417 */       return false;
/*     */     }
/* 419 */     XMLSignature localXMLSignature = (XMLSignature)paramObject;
/*     */ 
/* 421 */     boolean bool1 = this.id == null ? false : localXMLSignature.getId() == null ? true : this.id.equals(localXMLSignature.getId());
/*     */ 
/* 423 */     boolean bool2 = this.ki == null ? false : localXMLSignature.getKeyInfo() == null ? true : this.ki.equals(localXMLSignature.getKeyInfo());
/*     */ 
/* 427 */     return (bool1) && (bool2) && (this.sv.equals(localXMLSignature.getSignatureValue())) && (this.si.equals(localXMLSignature.getSignedInfo())) && (this.objects.equals(localXMLSignature.getObjects()));
/*     */   }
/*     */ 
/*     */   private void digestReference(DOMReference paramDOMReference, XMLSignContext paramXMLSignContext)
/*     */     throws XMLSignatureException
/*     */   {
/* 435 */     if (paramDOMReference.isDigested()) {
/* 436 */       return;
/*     */     }
/*     */ 
/* 439 */     String str1 = paramDOMReference.getURI();
/* 440 */     if (Utils.sameDocumentURI(str1)) {
/* 441 */       String str2 = Utils.parseIdFromSameDocumentURI(str1);
/*     */       Object localObject;
/* 442 */       if ((str2 != null) && (this.signatureIdMap.containsKey(str2))) {
/* 443 */         localObject = this.signatureIdMap.get(str2);
/* 444 */         if ((localObject instanceof DOMReference)) {
/* 445 */           digestReference((DOMReference)localObject, paramXMLSignContext);
/* 446 */         } else if ((localObject instanceof Manifest)) {
/* 447 */           Manifest localManifest = (Manifest)localObject;
/* 448 */           List localList = localManifest.getReferences();
/* 449 */           int k = 0; for (int m = localList.size(); k < m; k++) {
/* 450 */             digestReference((DOMReference)localList.get(k), paramXMLSignContext);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 458 */       if (str1.length() == 0) {
/* 459 */         localObject = paramDOMReference.getTransforms();
/* 460 */         int i = 0; for (int j = ((List)localObject).size(); i < j; i++) {
/* 461 */           Transform localTransform = (Transform)((List)localObject).get(i);
/* 462 */           String str3 = localTransform.getAlgorithm();
/* 463 */           if ((str3.equals("http://www.w3.org/TR/1999/REC-xpath-19991116")) || (str3.equals("http://www.w3.org/2002/06/xmldsig-filter2")))
/*     */           {
/* 465 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 470 */     paramDOMReference.digest(paramXMLSignContext);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  84 */     Init.init();
/*     */   }
/*     */ 
/*     */   public class DOMSignatureValue extends DOMStructure
/*     */     implements XMLSignature.SignatureValue
/*     */   {
/*     */     private String id;
/*     */     private byte[] value;
/*     */     private String valueBase64;
/*     */     private Element sigValueElem;
/* 480 */     private boolean validated = false;
/*     */     private boolean validationStatus;
/*     */ 
/*     */     DOMSignatureValue(String arg2)
/*     */     {
/*     */       Object localObject;
/* 484 */       this.id = localObject;
/*     */     }
/*     */ 
/*     */     DOMSignatureValue(Element arg2) throws MarshalException {
/*     */       Element localElement;
/*     */       try {
/* 490 */         this.value = Base64.decode(localElement);
/*     */       } catch (Base64DecodingException localBase64DecodingException) {
/* 492 */         throw new MarshalException(localBase64DecodingException);
/*     */       }
/*     */ 
/* 495 */       this.id = DOMUtils.getAttributeValue(localElement, "Id");
/* 496 */       this.sigValueElem = localElement;
/*     */     }
/*     */ 
/*     */     public String getId() {
/* 500 */       return this.id;
/*     */     }
/*     */ 
/*     */     public byte[] getValue() {
/* 504 */       return this.value == null ? null : (byte[])this.value.clone();
/*     */     }
/*     */ 
/*     */     public boolean validate(XMLValidateContext paramXMLValidateContext)
/*     */       throws XMLSignatureException
/*     */     {
/* 510 */       if (paramXMLValidateContext == null) {
/* 511 */         throw new NullPointerException("context cannot be null");
/*     */       }
/*     */ 
/* 514 */       if (this.validated) {
/* 515 */         return this.validationStatus;
/* 519 */       }
/*     */ SignatureMethod localSignatureMethod = DOMXMLSignature.this.si.getSignatureMethod();
/* 520 */       Key localKey = null;
/*     */       KeySelectorResult localKeySelectorResult;
/*     */       try {
/* 523 */         localKeySelectorResult = paramXMLValidateContext.getKeySelector().select(DOMXMLSignature.this.ki, KeySelector.Purpose.VERIFY, localSignatureMethod, paramXMLValidateContext);
/*     */ 
/* 525 */         localKey = localKeySelectorResult.getKey();
/* 526 */         if (localKey == null)
/* 527 */           throw new XMLSignatureException("the keyselector did not find a validation key");
/*     */       }
/*     */       catch (KeySelectorException localKeySelectorException)
/*     */       {
/* 531 */         throw new XMLSignatureException("cannot find validation key", localKeySelectorException);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 537 */         this.validationStatus = ((DOMSignatureMethod)localSignatureMethod).verify(localKey, (DOMSignedInfo)DOMXMLSignature.this.si, this.value, paramXMLValidateContext);
/*     */       }
/*     */       catch (Exception localException) {
/* 540 */         throw new XMLSignatureException(localException);
/*     */       }
/*     */ 
/* 543 */       this.validated = true;
/* 544 */       DOMXMLSignature.this.ksr = localKeySelectorResult;
/* 545 */       return this.validationStatus;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object paramObject) {
/* 549 */       if (this == paramObject) {
/* 550 */         return true;
/*     */       }
/*     */ 
/* 553 */       if (!(paramObject instanceof XMLSignature.SignatureValue)) {
/* 554 */         return false;
/*     */       }
/* 556 */       XMLSignature.SignatureValue localSignatureValue = (XMLSignature.SignatureValue)paramObject;
/*     */ 
/* 558 */       boolean bool = this.id == null ? false : localSignatureValue.getId() == null ? true : this.id.equals(localSignatureValue.getId());
/*     */ 
/* 562 */       return bool;
/*     */     }
/*     */ 
/*     */     public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
/*     */       throws MarshalException
/*     */     {
/* 569 */       this.sigValueElem = DOMUtils.createElement(DOMXMLSignature.this.ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
/*     */ 
/* 571 */       if (this.valueBase64 != null) {
/* 572 */         this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
/*     */       }
/*     */ 
/* 576 */       DOMUtils.setAttributeID(this.sigValueElem, "Id", this.id);
/* 577 */       paramNode.appendChild(this.sigValueElem);
/*     */     }
/*     */ 
/*     */     void setValue(byte[] paramArrayOfByte) {
/* 581 */       this.value = paramArrayOfByte;
/* 582 */       this.valueBase64 = Base64.encode(paramArrayOfByte);
/* 583 */       this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.jcp.xml.dsig.internal.dom.DOMXMLSignature
 * JD-Core Version:    0.6.2
 */