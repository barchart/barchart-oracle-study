/*     */ package javax.sql.rowset.serial;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Vector;
/*     */ import javax.sql.rowset.RowSetWarning;
/*     */ 
/*     */ public class SerialJavaObject
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private final Object obj;
/*     */   private transient Field[] fields;
/*     */   static final long serialVersionUID = -1465795139032831023L;
/*     */   Vector chain;
/*     */ 
/*     */   public SerialJavaObject(Object paramObject)
/*     */     throws SerialException
/*     */   {
/*  74 */     Class localClass = paramObject.getClass();
/*     */ 
/*  77 */     if (!(paramObject instanceof Cloneable)) {
/*  78 */       setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
/*     */     }
/*     */ 
/*  86 */     int i = 0;
/*  87 */     this.fields = localClass.getFields();
/*     */ 
/*  89 */     for (int j = 0; j < this.fields.length; j++) {
/*  90 */       if (this.fields[j].getModifiers() == 8) {
/*  91 */         i = 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     if (i != 0) {
/*  97 */       throw new SerialException("Located static fields in object instance. Cannot serialize");
/*     */     }
/*     */ 
/* 101 */     this.obj = paramObject;
/*     */   }
/*     */ 
/*     */   public Object getObject()
/*     */     throws SerialException
/*     */   {
/* 113 */     return this.obj;
/*     */   }
/*     */ 
/*     */   public Field[] getFields()
/*     */     throws SerialException
/*     */   {
/* 125 */     if (this.fields != null) {
/* 126 */       Class localClass = this.obj.getClass();
/* 127 */       return localClass.getFields();
/*     */     }
/* 129 */     throw new SerialException("SerialJavaObject does not contain a serialized object instance");
/*     */   }
/*     */ 
/*     */   private void setWarning(RowSetWarning paramRowSetWarning)
/*     */   {
/* 151 */     if (this.chain == null) {
/* 152 */       this.chain = new Vector();
/*     */     }
/* 154 */     this.chain.add(paramRowSetWarning);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.sql.rowset.serial.SerialJavaObject
 * JD-Core Version:    0.6.2
 */