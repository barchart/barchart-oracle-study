/*     */ package java.sql;
/*     */ 
/*     */ class DriverInfo
/*     */ {
/*     */   final Driver driver;
/*     */ 
/*     */   DriverInfo(Driver paramDriver)
/*     */   {
/* 621 */     this.driver = paramDriver;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/* 625 */     return ((paramObject instanceof DriverInfo)) && (this.driver == ((DriverInfo)paramObject).driver);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 630 */     return this.driver.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 634 */     return "driver[className=" + this.driver + "]";
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.sql.DriverInfo
 * JD-Core Version:    0.6.2
 */