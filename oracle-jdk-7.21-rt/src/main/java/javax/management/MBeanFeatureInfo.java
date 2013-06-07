/*     */ package javax.management;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.io.StreamCorruptedException;
/*     */ 
/*     */ public class MBeanFeatureInfo
/*     */   implements Serializable, DescriptorRead
/*     */ {
/*     */   static final long serialVersionUID = 3952882688968447265L;
/*     */   protected String name;
/*     */   protected String description;
/*     */   private transient Descriptor descriptor;
/*     */ 
/*     */   public MBeanFeatureInfo(String paramString1, String paramString2)
/*     */   {
/*  83 */     this(paramString1, paramString2, null);
/*     */   }
/*     */ 
/*     */   public MBeanFeatureInfo(String paramString1, String paramString2, Descriptor paramDescriptor)
/*     */   {
/*  98 */     this.name = paramString1;
/*  99 */     this.description = paramString2;
/* 100 */     this.descriptor = paramDescriptor;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 109 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 118 */     return this.description;
/*     */   }
/*     */ 
/*     */   public Descriptor getDescriptor()
/*     */   {
/* 130 */     return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 145 */     if (paramObject == this)
/* 146 */       return true;
/* 147 */     if (!(paramObject instanceof MBeanFeatureInfo))
/* 148 */       return false;
/* 149 */     MBeanFeatureInfo localMBeanFeatureInfo = (MBeanFeatureInfo)paramObject;
/* 150 */     return (localMBeanFeatureInfo.getName().equals(getName())) && (localMBeanFeatureInfo.getDescription().equals(getDescription())) && (localMBeanFeatureInfo.getDescriptor().equals(getDescriptor()));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 156 */     return getName().hashCode() ^ getDescription().hashCode() ^ getDescriptor().hashCode();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*     */     throws IOException
/*     */   {
/* 186 */     paramObjectOutputStream.defaultWriteObject();
/*     */ 
/* 188 */     if ((this.descriptor != null) && (this.descriptor.getClass() == ImmutableDescriptor.class))
/*     */     {
/* 191 */       paramObjectOutputStream.write(1);
/*     */ 
/* 193 */       String[] arrayOfString = this.descriptor.getFieldNames();
/*     */ 
/* 195 */       paramObjectOutputStream.writeObject(arrayOfString);
/* 196 */       paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
/*     */     } else {
/* 198 */       paramObjectOutputStream.write(0);
/*     */ 
/* 200 */       paramObjectOutputStream.writeObject(this.descriptor);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream paramObjectInputStream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 236 */     paramObjectInputStream.defaultReadObject();
/*     */ 
/* 238 */     switch (paramObjectInputStream.read()) {
/*     */     case 1:
/* 240 */       String[] arrayOfString = (String[])paramObjectInputStream.readObject();
/*     */ 
/* 242 */       if (arrayOfString.length == 0) {
/* 243 */         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */       } else {
/* 245 */         Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
/* 246 */         this.descriptor = new ImmutableDescriptor(arrayOfString, arrayOfObject);
/*     */       }
/*     */ 
/* 249 */       break;
/*     */     case 0:
/* 251 */       this.descriptor = ((Descriptor)paramObjectInputStream.readObject());
/*     */ 
/* 253 */       if (this.descriptor == null)
/* 254 */         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; break;
/*     */     case -1:
/* 259 */       this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
/*     */ 
/* 261 */       break;
/*     */     default:
/* 263 */       throw new StreamCorruptedException("Got unexpected byte.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.management.MBeanFeatureInfo
 * JD-Core Version:    0.6.2
 */