/*     */ package com.sun.xml.internal.ws.binding;
/*     */ 
/*     */ import com.sun.istack.internal.NotNull;
/*     */ import com.sun.istack.internal.Nullable;
/*     */ import com.sun.xml.internal.ws.api.BindingID;
/*     */ import com.sun.xml.internal.ws.api.SOAPVersion;
/*     */ import com.sun.xml.internal.ws.api.WSBinding;
/*     */ import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
/*     */ import com.sun.xml.internal.ws.api.pipe.Codec;
/*     */ import com.sun.xml.internal.ws.client.HandlerConfiguration;
/*     */ import com.sun.xml.internal.ws.developer.BindingTypeFeature;
/*     */ import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.ws.Service.Mode;
/*     */ import javax.xml.ws.WebServiceFeature;
/*     */ import javax.xml.ws.handler.Handler;
/*     */ import javax.xml.ws.soap.AddressingFeature;
/*     */ 
/*     */ public abstract class BindingImpl
/*     */   implements WSBinding
/*     */ {
/*     */   protected HandlerConfiguration handlerConfig;
/*     */   private final BindingID bindingId;
/*  67 */   protected final WebServiceFeatureList features = new WebServiceFeatureList();
/*     */ 
/*  69 */   protected Service.Mode serviceMode = Service.Mode.PAYLOAD;
/*     */ 
/*     */   protected BindingImpl(BindingID bindingId) {
/*  72 */     this.bindingId = bindingId;
/*  73 */     this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public List<Handler> getHandlerChain()
/*     */   {
/*  79 */     return this.handlerConfig.getHandlerChain();
/*     */   }
/*     */ 
/*     */   public HandlerConfiguration getHandlerConfig() {
/*  83 */     return this.handlerConfig;
/*     */   }
/*     */ 
/*     */   public void setMode(@NotNull Service.Mode mode)
/*     */   {
/*  88 */     this.serviceMode = Service.Mode.MESSAGE;
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public BindingID getBindingId()
/*     */   {
/*  94 */     return this.bindingId;
/*     */   }
/*     */ 
/*     */   public final SOAPVersion getSOAPVersion() {
/*  98 */     return this.bindingId.getSOAPVersion();
/*     */   }
/*     */ 
/*     */   public AddressingVersion getAddressingVersion()
/*     */   {
/*     */     AddressingVersion addressingVersion;
/*     */     AddressingVersion addressingVersion;
/* 103 */     if (this.features.isEnabled(AddressingFeature.class)) {
/* 104 */       addressingVersion = AddressingVersion.W3C;
/*     */     }
/*     */     else
/*     */     {
/*     */       AddressingVersion addressingVersion;
/* 105 */       if (this.features.isEnabled(MemberSubmissionAddressingFeature.class))
/* 106 */         addressingVersion = AddressingVersion.MEMBER;
/*     */       else
/* 108 */         addressingVersion = null; 
/*     */     }
/* 109 */     return addressingVersion;
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public final Codec createCodec()
/*     */   {
/* 115 */     return this.bindingId.createEncoder(this);
/*     */   }
/*     */ 
/*     */   public static BindingImpl create(@NotNull BindingID bindingId) {
/* 119 */     if (bindingId.equals(BindingID.XML_HTTP)) {
/* 120 */       return new HTTPBindingImpl();
/*     */     }
/* 122 */     return new SOAPBindingImpl(bindingId);
/*     */   }
/*     */ 
/*     */   public static BindingImpl create(@NotNull BindingID bindingId, WebServiceFeature[] features)
/*     */   {
/* 127 */     for (WebServiceFeature feature : features) {
/* 128 */       if ((feature instanceof BindingTypeFeature)) {
/* 129 */         BindingTypeFeature f = (BindingTypeFeature)feature;
/* 130 */         bindingId = BindingID.parse(f.getBindingId());
/*     */       }
/*     */     }
/* 133 */     if (bindingId.equals(BindingID.XML_HTTP)) {
/* 134 */       return new HTTPBindingImpl();
/*     */     }
/* 136 */     return new SOAPBindingImpl(bindingId, features);
/*     */   }
/*     */ 
/*     */   public static WSBinding getDefaultBinding() {
/* 140 */     return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
/*     */   }
/*     */ 
/*     */   public String getBindingID() {
/* 144 */     return this.bindingId.toString();
/*     */   }
/*     */   @Nullable
/*     */   public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType) {
/* 148 */     return this.features.get(featureType);
/*     */   }
/*     */ 
/*     */   public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
/* 152 */     return this.features.isEnabled(feature);
/*     */   }
/*     */ 
/*     */   @NotNull
/*     */   public WebServiceFeatureList getFeatures() {
/* 157 */     return this.features;
/*     */   }
/*     */ 
/*     */   public void setFeatures(WebServiceFeature[] newFeatures)
/*     */   {
/* 162 */     if (newFeatures != null)
/* 163 */       for (WebServiceFeature f : newFeatures)
/* 164 */         this.features.add(f);
/*     */   }
/*     */ 
/*     */   public void addFeature(@NotNull WebServiceFeature newFeature)
/*     */   {
/* 170 */     this.features.add(newFeature);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.binding.BindingImpl
 * JD-Core Version:    0.6.2
 */