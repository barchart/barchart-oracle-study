/*     */ package sun.reflect.generics.reflectiveObjects;
/*     */ 
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import sun.reflect.generics.factory.GenericsFactory;
/*     */ import sun.reflect.generics.tree.FieldTypeSignature;
/*     */ import sun.reflect.generics.visitor.Reifier;
/*     */ 
/*     */ public class TypeVariableImpl<D extends GenericDeclaration> extends LazyReflectiveObjectGenerator
/*     */   implements TypeVariable<D>
/*     */ {
/*     */   D genericDeclaration;
/*     */   private String name;
/*     */   private Type[] bounds;
/*     */   private FieldTypeSignature[] boundASTs;
/*     */ 
/*     */   private TypeVariableImpl(D paramD, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory)
/*     */   {
/*  57 */     super(paramGenericsFactory);
/*  58 */     this.genericDeclaration = paramD;
/*  59 */     this.name = paramString;
/*  60 */     this.boundASTs = paramArrayOfFieldTypeSignature;
/*     */   }
/*     */ 
/*     */   private FieldTypeSignature[] getBoundASTs()
/*     */   {
/*  70 */     assert (this.bounds == null);
/*  71 */     return this.boundASTs;
/*     */   }
/*     */ 
/*     */   public static <T extends GenericDeclaration> TypeVariableImpl<T> make(T paramT, String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature, GenericsFactory paramGenericsFactory)
/*     */   {
/*  90 */     return new TypeVariableImpl(paramT, paramString, paramArrayOfFieldTypeSignature, paramGenericsFactory);
/*     */   }
/*     */ 
/*     */   public Type[] getBounds()
/*     */   {
/* 117 */     if (this.bounds == null) {
/* 118 */       FieldTypeSignature[] arrayOfFieldTypeSignature = getBoundASTs();
/*     */ 
/* 121 */       Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
/*     */ 
/* 123 */       for (int i = 0; i < arrayOfFieldTypeSignature.length; i++) {
/* 124 */         Reifier localReifier = getReifier();
/* 125 */         arrayOfFieldTypeSignature[i].accept(localReifier);
/* 126 */         arrayOfType[i] = localReifier.getResult();
/*     */       }
/*     */ 
/* 129 */       this.bounds = arrayOfType;
/*     */     }
/*     */ 
/* 132 */     return (Type[])this.bounds.clone();
/*     */   }
/*     */ 
/*     */   public D getGenericDeclaration()
/*     */   {
/* 144 */     return this.genericDeclaration;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 153 */     return this.name;
/*     */   }
/* 155 */   public String toString() { return getName(); }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 159 */     if ((paramObject instanceof TypeVariable)) {
/* 160 */       TypeVariable localTypeVariable = (TypeVariable)paramObject;
/*     */ 
/* 162 */       GenericDeclaration localGenericDeclaration = localTypeVariable.getGenericDeclaration();
/* 163 */       String str = localTypeVariable.getName();
/*     */ 
/* 165 */       return (this.genericDeclaration == null ? localGenericDeclaration == null : this.genericDeclaration.equals(localGenericDeclaration)) && (this.name == null ? str == null : this.name.equals(str));
/*     */     }
/*     */ 
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 179 */     return this.genericDeclaration.hashCode() ^ this.name.hashCode();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.reflectiveObjects.TypeVariableImpl
 * JD-Core Version:    0.6.2
 */