/*      */ package sun.org.mozilla.classfile.internal;
/*      */ 
/*      */ final class FieldOrMethodRef
/*      */ {
/*      */   private String className;
/*      */   private String name;
/*      */   private String type;
/* 4783 */   private int hashCode = -1;
/*      */ 
/*      */   FieldOrMethodRef(String paramString1, String paramString2, String paramString3)
/*      */   {
/* 4738 */     this.className = paramString1;
/* 4739 */     this.name = paramString2;
/* 4740 */     this.type = paramString3;
/*      */   }
/*      */ 
/*      */   public String getClassName()
/*      */   {
/* 4745 */     return this.className;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/* 4750 */     return this.name;
/*      */   }
/*      */ 
/*      */   public String getType()
/*      */   {
/* 4755 */     return this.type;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/* 4761 */     if (!(paramObject instanceof FieldOrMethodRef)) return false;
/* 4762 */     FieldOrMethodRef localFieldOrMethodRef = (FieldOrMethodRef)paramObject;
/* 4763 */     return (this.className.equals(localFieldOrMethodRef.className)) && (this.name.equals(localFieldOrMethodRef.name)) && (this.type.equals(localFieldOrMethodRef.type));
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 4771 */     if (this.hashCode == -1) {
/* 4772 */       int i = this.className.hashCode();
/* 4773 */       int j = this.name.hashCode();
/* 4774 */       int k = this.type.hashCode();
/* 4775 */       this.hashCode = (i ^ j ^ k);
/*      */     }
/* 4777 */     return this.hashCode;
/*      */   }
/*      */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.org.mozilla.classfile.internal.FieldOrMethodRef
 * JD-Core Version:    0.6.2
 */