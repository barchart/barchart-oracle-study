/*     */ package com.sun.servicetag;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.validation.Schema;
/*     */ import javax.xml.validation.SchemaFactory;
/*     */ import javax.xml.validation.Validator;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ class RegistrationDocument
/*     */ {
/*     */   private static final String REGISTRATION_DATA_SCHEMA = "/com/sun/servicetag/resources/product_registration.xsd";
/*     */   private static final String REGISTRATION_DATA_VERSION = "1.0";
/*     */   private static final String SERVICE_TAG_VERSION = "1.0";
/*     */   static final String ST_NODE_REGISTRATION_DATA = "registration_data";
/*     */   static final String ST_ATTR_REGISTRATION_VERSION = "version";
/*     */   static final String ST_NODE_ENVIRONMENT = "environment";
/*     */   static final String ST_NODE_HOSTNAME = "hostname";
/*     */   static final String ST_NODE_HOST_ID = "hostId";
/*     */   static final String ST_NODE_OS_NAME = "osName";
/*     */   static final String ST_NODE_OS_VERSION = "osVersion";
/*     */   static final String ST_NODE_OS_ARCH = "osArchitecture";
/*     */   static final String ST_NODE_SYSTEM_MODEL = "systemModel";
/*     */   static final String ST_NODE_SYSTEM_MANUFACTURER = "systemManufacturer";
/*     */   static final String ST_NODE_CPU_MANUFACTURER = "cpuManufacturer";
/*     */   static final String ST_NODE_SERIAL_NUMBER = "serialNumber";
/*     */   static final String ST_NODE_REGISTRY = "registry";
/*     */   static final String ST_ATTR_REGISTRY_URN = "urn";
/*     */   static final String ST_ATTR_REGISTRY_VERSION = "version";
/*     */   static final String ST_NODE_SERVICE_TAG = "service_tag";
/*     */   static final String ST_NODE_INSTANCE_URN = "instance_urn";
/*     */   static final String ST_NODE_PRODUCT_NAME = "product_name";
/*     */   static final String ST_NODE_PRODUCT_VERSION = "product_version";
/*     */   static final String ST_NODE_PRODUCT_URN = "product_urn";
/*     */   static final String ST_NODE_PRODUCT_PARENT_URN = "product_parent_urn";
/*     */   static final String ST_NODE_PRODUCT_PARENT = "product_parent";
/*     */   static final String ST_NODE_PRODUCT_DEFINED_INST_ID = "product_defined_inst_id";
/*     */   static final String ST_NODE_PRODUCT_VENDOR = "product_vendor";
/*     */   static final String ST_NODE_PLATFORM_ARCH = "platform_arch";
/*     */   static final String ST_NODE_TIMESTAMP = "timestamp";
/*     */   static final String ST_NODE_CONTAINER = "container";
/*     */   static final String ST_NODE_SOURCE = "source";
/*     */   static final String ST_NODE_INSTALLER_UID = "installer_uid";
/*     */ 
/*     */   static RegistrationData load(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  99 */     Document localDocument = initializeDocument(paramInputStream);
/*     */ 
/* 102 */     Element localElement1 = getRegistrationDataRoot(localDocument);
/* 103 */     Element localElement2 = getSingletonElementFromRoot(localElement1, "registry");
/*     */ 
/* 105 */     String str = localElement2.getAttribute("urn");
/*     */ 
/* 109 */     RegistrationData localRegistrationData = new RegistrationData(str);
/* 110 */     addServiceTags(localElement2, localRegistrationData);
/*     */ 
/* 112 */     Element localElement3 = getSingletonElementFromRoot(localElement1, "environment");
/* 113 */     buildEnvironmentMap(localElement3, localRegistrationData);
/* 114 */     return localRegistrationData;
/*     */   }
/*     */ 
/*     */   static void store(OutputStream paramOutputStream, RegistrationData paramRegistrationData)
/*     */     throws IOException
/*     */   {
/* 120 */     Document localDocument = initializeDocument();
/*     */ 
/* 124 */     addEnvironmentNodes(localDocument, paramRegistrationData.getEnvironmentMap());
/*     */ 
/* 126 */     addServiceTagRegistry(localDocument, paramRegistrationData.getRegistrationURN(), paramRegistrationData.getServiceTags());
/*     */ 
/* 129 */     transform(localDocument, paramOutputStream);
/*     */   }
/*     */ 
/*     */   private static Document initializeDocument(InputStream paramInputStream) throws IOException {
/* 134 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */     Object localObject;
/*     */     try {
/* 137 */       SchemaFactory localSchemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
/* 138 */       localObject = RegistrationDocument.class.getResource("/com/sun/servicetag/resources/product_registration.xsd");
/* 139 */       Schema localSchema = localSchemaFactory.newSchema((URL)localObject);
/* 140 */       Validator localValidator = localSchema.newValidator();
/*     */ 
/* 142 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 143 */       Document localDocument = localDocumentBuilder.parse(new InputSource(paramInputStream));
/* 144 */       localValidator.validate(new DOMSource(localDocument));
/* 145 */       return localDocument;
/*     */     } catch (SAXException localSAXException) {
/* 147 */       localObject = new IllegalArgumentException("Error generated in parsing");
/* 148 */       ((IllegalArgumentException)localObject).initCause(localSAXException);
/* 149 */       throw ((Throwable)localObject);
/*     */     }
/*     */     catch (ParserConfigurationException localParserConfigurationException)
/*     */     {
/* 153 */       localObject = new InternalError("Error in creating the new document");
/* 154 */       ((InternalError)localObject).initCause(localParserConfigurationException);
/* 155 */     }throw ((Throwable)localObject);
/*     */   }
/*     */ 
/*     */   private static Document initializeDocument() throws IOException {
/* 161 */     DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */     Object localObject;
/*     */     try {
/* 163 */       DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
/* 164 */       localObject = localDocumentBuilder.newDocument();
/*     */ 
/* 167 */       Element localElement = ((Document)localObject).createElement("registration_data");
/* 168 */       ((Document)localObject).appendChild(localElement);
/* 169 */       localElement.setAttribute("version", "1.0");
/*     */ 
/* 171 */       return localObject;
/*     */     }
/*     */     catch (ParserConfigurationException localParserConfigurationException)
/*     */     {
/* 175 */       localObject = new InternalError("Error in creating the new document");
/* 176 */       ((InternalError)localObject).initCause(localParserConfigurationException);
/* 177 */     }throw ((Throwable)localObject);
/*     */   }
/*     */ 
/*     */   private static void transform(Document paramDocument, OutputStream paramOutputStream)
/*     */   {
/*     */     try
/*     */     {
/* 185 */       TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
/* 186 */       localTransformerFactory.setAttribute("indent-number", new Integer(3));
/*     */ 
/* 188 */       localObject = localTransformerFactory.newTransformer();
/*     */ 
/* 190 */       ((Transformer)localObject).setOutputProperty("indent", "yes");
/* 191 */       ((Transformer)localObject).setOutputProperty("method", "xml");
/* 192 */       ((Transformer)localObject).setOutputProperty("encoding", "UTF-8");
/* 193 */       ((Transformer)localObject).setOutputProperty("standalone", "yes");
/* 194 */       ((Transformer)localObject).transform(new DOMSource(paramDocument), new StreamResult(new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF-8"))));
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/* 198 */       localObject = new InternalError("Error generated during transformation");
/* 199 */       ((InternalError)localObject).initCause(localUnsupportedEncodingException);
/* 200 */       throw ((Throwable)localObject);
/*     */     }
/*     */     catch (TransformerConfigurationException localTransformerConfigurationException)
/*     */     {
/* 204 */       localObject = new InternalError("Error in creating the new document");
/* 205 */       ((InternalError)localObject).initCause(localTransformerConfigurationException);
/* 206 */       throw ((Throwable)localObject);
/*     */     }
/*     */     catch (TransformerException localTransformerException) {
/* 209 */       Object localObject = new InternalError("Error generated during transformation");
/* 210 */       ((InternalError)localObject).initCause(localTransformerException);
/* 211 */       throw ((Throwable)localObject);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void addServiceTagRegistry(Document paramDocument, String paramString, Set<ServiceTag> paramSet)
/*     */   {
/* 219 */     Element localElement1 = paramDocument.createElement("registry");
/* 220 */     localElement1.setAttribute("urn", paramString);
/* 221 */     localElement1.setAttribute("version", "1.0");
/*     */ 
/* 223 */     Element localElement2 = getRegistrationDataRoot(paramDocument);
/* 224 */     localElement2.appendChild(localElement1);
/*     */ 
/* 227 */     for (ServiceTag localServiceTag : paramSet)
/* 228 */       addServiceTagElement(paramDocument, localElement1, localServiceTag);
/*     */   }
/*     */ 
/*     */   private static void addServiceTagElement(Document paramDocument, Element paramElement, ServiceTag paramServiceTag)
/*     */   {
/* 235 */     Element localElement = paramDocument.createElement("service_tag");
/* 236 */     paramElement.appendChild(localElement);
/* 237 */     addChildElement(paramDocument, localElement, "instance_urn", paramServiceTag.getInstanceURN());
/*     */ 
/* 239 */     addChildElement(paramDocument, localElement, "product_name", paramServiceTag.getProductName());
/*     */ 
/* 241 */     addChildElement(paramDocument, localElement, "product_version", paramServiceTag.getProductVersion());
/*     */ 
/* 243 */     addChildElement(paramDocument, localElement, "product_urn", paramServiceTag.getProductURN());
/*     */ 
/* 245 */     addChildElement(paramDocument, localElement, "product_parent_urn", paramServiceTag.getProductParentURN());
/*     */ 
/* 247 */     addChildElement(paramDocument, localElement, "product_parent", paramServiceTag.getProductParent());
/*     */ 
/* 249 */     addChildElement(paramDocument, localElement, "product_defined_inst_id", paramServiceTag.getProductDefinedInstanceID());
/*     */ 
/* 252 */     addChildElement(paramDocument, localElement, "product_vendor", paramServiceTag.getProductVendor());
/*     */ 
/* 254 */     addChildElement(paramDocument, localElement, "platform_arch", paramServiceTag.getPlatformArch());
/*     */ 
/* 256 */     addChildElement(paramDocument, localElement, "timestamp", Util.formatTimestamp(paramServiceTag.getTimestamp()));
/*     */ 
/* 258 */     addChildElement(paramDocument, localElement, "container", paramServiceTag.getContainer());
/*     */ 
/* 260 */     addChildElement(paramDocument, localElement, "source", paramServiceTag.getSource());
/*     */ 
/* 262 */     addChildElement(paramDocument, localElement, "installer_uid", String.valueOf(paramServiceTag.getInstallerUID()));
/*     */   }
/*     */ 
/*     */   private static void addChildElement(Document paramDocument, Element paramElement, String paramString1, String paramString2)
/*     */   {
/* 269 */     Element localElement = paramDocument.createElement(paramString1);
/* 270 */     localElement.appendChild(paramDocument.createTextNode(paramString2));
/* 271 */     paramElement.appendChild(localElement);
/*     */   }
/*     */ 
/*     */   private static void addServiceTags(Element paramElement, RegistrationData paramRegistrationData)
/*     */   {
/* 277 */     NodeList localNodeList = paramElement.getElementsByTagName("service_tag");
/* 278 */     int i = localNodeList == null ? 0 : localNodeList.getLength();
/* 279 */     for (int j = 0; j < i; j++) {
/* 280 */       Element localElement = (Element)localNodeList.item(j);
/* 281 */       ServiceTag localServiceTag = getServiceTag(localElement);
/* 282 */       paramRegistrationData.addServiceTag(localServiceTag);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void buildEnvironmentMap(Element paramElement, RegistrationData paramRegistrationData)
/*     */   {
/* 289 */     paramRegistrationData.setEnvironment("hostname", getTextValue(paramElement, "hostname"));
/* 290 */     paramRegistrationData.setEnvironment("hostId", getTextValue(paramElement, "hostId"));
/* 291 */     paramRegistrationData.setEnvironment("osName", getTextValue(paramElement, "osName"));
/* 292 */     paramRegistrationData.setEnvironment("osVersion", getTextValue(paramElement, "osVersion"));
/* 293 */     paramRegistrationData.setEnvironment("osArchitecture", getTextValue(paramElement, "osArchitecture"));
/* 294 */     paramRegistrationData.setEnvironment("systemModel", getTextValue(paramElement, "systemModel"));
/* 295 */     paramRegistrationData.setEnvironment("systemManufacturer", getTextValue(paramElement, "systemManufacturer"));
/* 296 */     paramRegistrationData.setEnvironment("cpuManufacturer", getTextValue(paramElement, "cpuManufacturer"));
/* 297 */     paramRegistrationData.setEnvironment("serialNumber", getTextValue(paramElement, "serialNumber"));
/*     */   }
/*     */ 
/*     */   private static void addEnvironmentNodes(Document paramDocument, Map<String, String> paramMap)
/*     */   {
/* 303 */     Element localElement1 = getRegistrationDataRoot(paramDocument);
/* 304 */     Element localElement2 = paramDocument.createElement("environment");
/* 305 */     localElement1.appendChild(localElement2);
/* 306 */     Set localSet = paramMap.entrySet();
/* 307 */     for (Map.Entry localEntry : localSet)
/* 308 */       addChildElement(paramDocument, localElement2, (String)localEntry.getKey(), (String)localEntry.getValue());
/*     */   }
/*     */ 
/*     */   private static Element getRegistrationDataRoot(Document paramDocument)
/*     */   {
/* 313 */     Element localElement = paramDocument.getDocumentElement();
/* 314 */     if (!localElement.getNodeName().equals("registration_data")) {
/* 315 */       throw new IllegalArgumentException("Not a registration_data node \"" + localElement.getNodeName() + "\"");
/*     */     }
/*     */ 
/* 319 */     return localElement;
/*     */   }
/*     */ 
/*     */   private static Element getSingletonElementFromRoot(Element paramElement, String paramString) {
/* 323 */     NodeList localNodeList = paramElement.getElementsByTagName(paramString);
/* 324 */     int i = localNodeList == null ? 0 : localNodeList.getLength();
/* 325 */     if (i != 1) {
/* 326 */       throw new IllegalArgumentException("Invalid number of " + paramString + " nodes = " + i);
/*     */     }
/*     */ 
/* 329 */     Element localElement = (Element)localNodeList.item(0);
/* 330 */     if (!localElement.getNodeName().equals(paramString)) {
/* 331 */       throw new IllegalArgumentException("Not a  " + paramString + " node \"" + localElement.getNodeName() + "\"");
/*     */     }
/*     */ 
/* 334 */     return localElement;
/*     */   }
/*     */ 
/*     */   private static ServiceTag getServiceTag(Element paramElement)
/*     */   {
/* 339 */     return new ServiceTag(getTextValue(paramElement, "instance_urn"), getTextValue(paramElement, "product_name"), getTextValue(paramElement, "product_version"), getTextValue(paramElement, "product_urn"), getTextValue(paramElement, "product_parent"), getTextValue(paramElement, "product_parent_urn"), getTextValue(paramElement, "product_defined_inst_id"), getTextValue(paramElement, "product_vendor"), getTextValue(paramElement, "platform_arch"), getTextValue(paramElement, "container"), getTextValue(paramElement, "source"), Util.getIntValue(getTextValue(paramElement, "installer_uid")), Util.parseTimestamp(getTextValue(paramElement, "timestamp")));
/*     */   }
/*     */ 
/*     */   private static String getTextValue(Element paramElement, String paramString)
/*     */   {
/* 357 */     String str = "";
/* 358 */     NodeList localNodeList = paramElement.getElementsByTagName(paramString);
/* 359 */     if ((localNodeList != null) && (localNodeList.getLength() > 0)) {
/* 360 */       Element localElement = (Element)localNodeList.item(0);
/* 361 */       Node localNode = localElement.getFirstChild();
/* 362 */       if (localNode != null) {
/* 363 */         str = localNode.getNodeValue();
/*     */       }
/*     */     }
/* 366 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.servicetag.RegistrationDocument
 * JD-Core Version:    0.6.2
 */