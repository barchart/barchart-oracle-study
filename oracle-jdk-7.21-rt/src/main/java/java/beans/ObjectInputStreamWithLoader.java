/*     */ package java.beans;
/*     */ 
/*     */ import com.sun.beans.finder.ClassFinder;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.io.StreamCorruptedException;
/*     */ 
/*     */ class ObjectInputStreamWithLoader extends ObjectInputStream
/*     */ {
/*     */   private ClassLoader loader;
/*     */ 
/*     */   public ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
/*     */     throws IOException, StreamCorruptedException
/*     */   {
/* 482 */     super(paramInputStream);
/* 483 */     if (paramClassLoader == null) {
/* 484 */       throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
/*     */     }
/* 486 */     this.loader = paramClassLoader;
/*     */   }
/*     */ 
/*     */   protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 495 */     String str = paramObjectStreamClass.getName();
/* 496 */     return ClassFinder.resolveClass(str, this.loader);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.beans.ObjectInputStreamWithLoader
 * JD-Core Version:    0.6.2
 */