/*     */ package com.sun.xml.internal.bind.api;
/*     */ 
/*     */ import com.sun.xml.internal.bind.v2.model.nav.Navigator;
/*     */ import com.sun.xml.internal.bind.v2.model.nav.ReflectionNavigator;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collection;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ public final class TypeReference
/*     */ {
/*     */   public final QName tagName;
/*     */   public final Type type;
/*     */   public final Annotation[] annotations;
/*     */ 
/*     */   public TypeReference(QName tagName, Type type, Annotation[] annotations)
/*     */   {
/*  69 */     if ((tagName == null) || (type == null) || (annotations == null)) {
/*  70 */       String nullArgs = "";
/*     */ 
/*  72 */       if (tagName == null) nullArgs = "tagName";
/*  73 */       if (type == null) nullArgs = nullArgs + (nullArgs.length() > 0 ? ", type" : "type");
/*  74 */       if (annotations == null) nullArgs = nullArgs + (nullArgs.length() > 0 ? ", annotations" : "annotations");
/*     */ 
/*  76 */       Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { nullArgs });
/*     */ 
/*  78 */       throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(new Object[] { nullArgs }));
/*     */     }
/*     */ 
/*  81 */     this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
/*  82 */     this.type = type;
/*  83 */     this.annotations = annotations;
/*     */   }
/*     */ 
/*     */   public <A extends Annotation> A get(Class<A> annotationType)
/*     */   {
/*  91 */     for (Annotation a : this.annotations) {
/*  92 */       if (a.annotationType() == annotationType)
/*  93 */         return (Annotation)annotationType.cast(a);
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeReference toItemType()
/*     */   {
/* 107 */     Type base = Navigator.REFLECTION.getBaseClass(this.type, Collection.class);
/* 108 */     if (base == null) {
/* 109 */       return this;
/*     */     }
/* 111 */     return new TypeReference(this.tagName, Navigator.REFLECTION.getTypeArgument(base, 0), new Annotation[0]);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.bind.api.TypeReference
 * JD-Core Version:    0.6.2
 */