/*     */ package com.sun.xml.internal.ws.policy.sourcemodel.attach;
/*     */ 
/*     */ import com.sun.xml.internal.ws.policy.Policy;
/*     */ import com.sun.xml.internal.ws.policy.PolicyException;
/*     */ import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
/*     */ import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
/*     */ import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
/*     */ import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelUnmarshaller;
/*     */ import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
/*     */ import java.io.Reader;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.events.Characters;
/*     */ import javax.xml.stream.events.EndElement;
/*     */ import javax.xml.stream.events.StartElement;
/*     */ import javax.xml.stream.events.XMLEvent;
/*     */ 
/*     */ public class ExternalAttachmentsUnmarshaller
/*     */ {
/*  61 */   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ExternalAttachmentsUnmarshaller.class);
/*     */   public static final URI BINDING_ID;
/*     */   public static final URI BINDING_OPERATION_ID;
/*     */   public static final URI BINDING_OPERATION_INPUT_ID;
/*     */   public static final URI BINDING_OPERATION_OUTPUT_ID;
/*     */   public static final URI BINDING_OPERATION_FAULT_ID;
/*  81 */   private static final QName POLICY_ATTACHMENT = new QName("http://www.w3.org/ns/ws-policy", "PolicyAttachment");
/*  82 */   private static final QName APPLIES_TO = new QName("http://www.w3.org/ns/ws-policy", "AppliesTo");
/*  83 */   private static final QName POLICY = new QName("http://www.w3.org/ns/ws-policy", "Policy");
/*  84 */   private static final QName URI = new QName("http://www.w3.org/ns/ws-policy", "URI");
/*  85 */   private static final QName POLICIES = new QName("http://java.sun.com/xml/ns/metro/management", "Policies");
/*  86 */   private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
/*  87 */   private static final PolicyModelUnmarshaller POLICY_UNMARSHALLER = PolicyModelUnmarshaller.getXmlUnmarshaller();
/*     */ 
/*  89 */   private final Map<URI, Policy> map = new HashMap();
/*  90 */   private URI currentUri = null;
/*  91 */   private Policy currentPolicy = null;
/*     */ 
/*     */   public static Map<URI, Policy> unmarshal(Reader source) throws PolicyException {
/*  94 */     LOGGER.entering(new Object[] { source });
/*     */     try {
/*  96 */       XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(source);
/*  97 */       ExternalAttachmentsUnmarshaller instance = new ExternalAttachmentsUnmarshaller();
/*  98 */       Map map = instance.unmarshal(reader, null);
/*  99 */       LOGGER.exiting(map);
/* 100 */       return Collections.unmodifiableMap(map);
/*     */     } catch (XMLStreamException ex) {
/* 102 */       throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0086_FAILED_CREATE_READER(source)), ex));
/*     */     }
/*     */   }
/*     */ 
/*     */   private Map<URI, Policy> unmarshal(XMLEventReader reader, StartElement parentElement) throws PolicyException {
/* 107 */     XMLEvent event = null;
/* 108 */     while (reader.hasNext()) {
/*     */       try {
/* 110 */         event = reader.peek();
/* 111 */         switch (event.getEventType()) {
/*     */         case 5:
/*     */         case 7:
/* 114 */           reader.nextEvent();
/* 115 */           break;
/*     */         case 4:
/* 118 */           processCharacters(event.asCharacters(), parentElement, this.map);
/* 119 */           reader.nextEvent();
/* 120 */           break;
/*     */         case 2:
/* 123 */           processEndTag(event.asEndElement(), parentElement);
/* 124 */           reader.nextEvent();
/* 125 */           return this.map;
/*     */         case 1:
/* 128 */           StartElement element = event.asStartElement();
/* 129 */           processStartTag(element, parentElement, reader, this.map);
/* 130 */           break;
/*     */         case 8:
/* 133 */           return this.map;
/*     */         case 3:
/*     */         case 6:
/*     */         default:
/* 136 */           throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0087_UNKNOWN_EVENT(event))));
/*     */         }
/*     */       } catch (XMLStreamException e) {
/* 139 */         Location location = event == null ? null : event.getLocation();
/* 140 */         throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(location)), e));
/*     */       }
/*     */     }
/* 143 */     return this.map;
/*     */   }
/*     */ 
/*     */   private void processStartTag(StartElement element, StartElement parent, XMLEventReader reader, Map<URI, Policy> map) throws PolicyException
/*     */   {
/*     */     try
/*     */     {
/* 150 */       QName name = element.getName();
/* 151 */       if (parent == null) {
/* 152 */         if (!name.equals(POLICIES))
/* 153 */           throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<Policies>", name, element.getLocation()))));
/*     */       }
/*     */       else {
/* 156 */         QName parentName = parent.getName();
/* 157 */         if (parentName.equals(POLICIES)) {
/* 158 */           if (!name.equals(POLICY_ATTACHMENT))
/* 159 */             throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<PolicyAttachment>", name, element.getLocation()))));
/*     */         }
/* 161 */         else if (parentName.equals(POLICY_ATTACHMENT)) {
/* 162 */           if (name.equals(POLICY)) {
/* 163 */             readPolicy(reader);
/* 164 */             return;
/* 165 */           }if (!name.equals(APPLIES_TO))
/* 166 */             throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<AppliesTo> or <Policy>", name, element.getLocation()))));
/*     */         }
/* 168 */         else if (parentName.equals(APPLIES_TO)) {
/* 169 */           if (!name.equals(URI))
/* 170 */             throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0089_EXPECTED_ELEMENT("<URI>", name, element.getLocation()))));
/*     */         }
/*     */         else {
/* 173 */           throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0090_UNEXPECTED_ELEMENT(name, element.getLocation()))));
/*     */         }
/*     */       }
/* 176 */       reader.nextEvent();
/* 177 */       unmarshal(reader, element);
/*     */     } catch (XMLStreamException e) {
/* 179 */       throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0088_FAILED_PARSE(element.getLocation()), e)));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readPolicy(XMLEventReader reader) throws PolicyException {
/* 184 */     PolicySourceModel policyModel = POLICY_UNMARSHALLER.unmarshalModel(reader);
/* 185 */     PolicyModelTranslator translator = PolicyModelTranslator.getTranslator();
/* 186 */     Policy policy = translator.translate(policyModel);
/* 187 */     if (this.currentUri != null) {
/* 188 */       this.map.put(this.currentUri, policy);
/* 189 */       this.currentUri = null;
/* 190 */       this.currentPolicy = null;
/*     */     }
/*     */     else {
/* 193 */       this.currentPolicy = policy;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processEndTag(EndElement element, StartElement startElement) throws PolicyException {
/* 198 */     checkEndTagName(startElement.getName(), element);
/*     */   }
/*     */ 
/*     */   private void checkEndTagName(QName expectedName, EndElement element) throws PolicyException {
/* 202 */     QName actualName = element.getName();
/* 203 */     if (!expectedName.equals(actualName))
/* 204 */       throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0091_END_ELEMENT_NO_MATCH(expectedName, element, element.getLocation()))));
/*     */   }
/*     */ 
/*     */   private void processCharacters(Characters chars, StartElement currentElement, Map<URI, Policy> map)
/*     */     throws PolicyException
/*     */   {
/* 211 */     if (chars.isWhiteSpace()) {
/* 212 */       return;
/*     */     }
/*     */ 
/* 215 */     String data = chars.getData();
/* 216 */     if ((currentElement != null) && (URI.equals(currentElement.getName()))) {
/* 217 */       processUri(chars, map);
/* 218 */       return;
/*     */     }
/* 220 */     throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0092_CHARACTER_DATA_UNEXPECTED(currentElement, data, chars.getLocation()))));
/*     */   }
/*     */ 
/*     */   private void processUri(Characters chars, Map<URI, Policy> map)
/*     */     throws PolicyException
/*     */   {
/* 227 */     String data = chars.getData().trim();
/*     */     try {
/* 229 */       URI uri = new URI(data);
/* 230 */       if (this.currentPolicy != null) {
/* 231 */         map.put(uri, this.currentPolicy);
/* 232 */         this.currentUri = null;
/* 233 */         this.currentPolicy = null;
/*     */       } else {
/* 235 */         this.currentUri = uri;
/*     */       }
/*     */     } catch (URISyntaxException e) {
/* 238 */       throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0093_INVALID_URI(data, chars.getLocation())), e));
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  71 */       BINDING_ID = new URI("urn:uuid:c9bef600-0d7a-11de-abc1-0002a5d5c51b");
/*  72 */       BINDING_OPERATION_ID = new URI("urn:uuid:62e66b60-0d7b-11de-a1a2-0002a5d5c51b");
/*  73 */       BINDING_OPERATION_INPUT_ID = new URI("urn:uuid:730d8d20-0d7b-11de-84e9-0002a5d5c51b");
/*  74 */       BINDING_OPERATION_OUTPUT_ID = new URI("urn:uuid:85b0f980-0d7b-11de-8e9d-0002a5d5c51b");
/*  75 */       BINDING_OPERATION_FAULT_ID = new URI("urn:uuid:917cb060-0d7b-11de-9e80-0002a5d5c51b");
/*     */     } catch (URISyntaxException e) {
/*  77 */       throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0094_INVALID_URN()), e));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.policy.sourcemodel.attach.ExternalAttachmentsUnmarshaller
 * JD-Core Version:    0.6.2
 */