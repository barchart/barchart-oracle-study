/*     */ package java.text;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ 
/*     */ class CalendarBuilder
/*     */ {
/*     */   private static final int UNSET = 0;
/*     */   private static final int COMPUTED = 1;
/*     */   private static final int MINIMUM_USER_STAMP = 2;
/*     */   private static final int MAX_FIELD = 18;
/*     */   public static final int WEEK_YEAR = 17;
/*     */   public static final int ISO_DAY_OF_WEEK = 1000;
/*     */   private final int[] field;
/*     */   private int nextStamp;
/*     */   private int maxFieldIndex;
/*     */ 
/*     */   CalendarBuilder()
/*     */   {
/*  63 */     this.field = new int[36];
/*  64 */     this.nextStamp = 2;
/*  65 */     this.maxFieldIndex = -1;
/*     */   }
/*     */ 
/*     */   CalendarBuilder set(int paramInt1, int paramInt2) {
/*  69 */     if (paramInt1 == 1000) {
/*  70 */       paramInt1 = 7;
/*  71 */       paramInt2 = toCalendarDayOfWeek(paramInt2);
/*     */     }
/*  73 */     this.field[paramInt1] = (this.nextStamp++);
/*  74 */     this.field[(18 + paramInt1)] = paramInt2;
/*  75 */     if ((paramInt1 > this.maxFieldIndex) && (paramInt1 < 17)) {
/*  76 */       this.maxFieldIndex = paramInt1;
/*     */     }
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */   CalendarBuilder addYear(int paramInt) {
/*  82 */     this.field[19] += paramInt;
/*  83 */     this.field[35] += paramInt;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   boolean isSet(int paramInt) {
/*  88 */     if (paramInt == 1000) {
/*  89 */       paramInt = 7;
/*     */     }
/*  91 */     return this.field[paramInt] > 0;
/*     */   }
/*     */ 
/*     */   Calendar establish(Calendar paramCalendar) {
/*  95 */     int i = (isSet(17)) && (this.field[17] > this.field[1]) ? 1 : 0;
/*     */ 
/*  97 */     if ((i != 0) && (!paramCalendar.isWeekDateSupported()))
/*     */     {
/*  99 */       if (!isSet(1)) {
/* 100 */         set(1, this.field[35]);
/*     */       }
/* 102 */       i = 0;
/*     */     }
/*     */ 
/* 105 */     paramCalendar.clear();
/*     */     int k;
/* 108 */     for (int j = 2; j < this.nextStamp; j++) {
/* 109 */       for (k = 0; k <= this.maxFieldIndex; k++) {
/* 110 */         if (this.field[k] == j) {
/* 111 */           paramCalendar.set(k, this.field[(18 + k)]);
/* 112 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 117 */     if (i != 0) {
/* 118 */       j = isSet(3) ? this.field[21] : 1;
/* 119 */       k = isSet(7) ? this.field[25] : paramCalendar.getFirstDayOfWeek();
/*     */ 
/* 121 */       if ((!isValidDayOfWeek(k)) && (paramCalendar.isLenient())) {
/* 122 */         if (k >= 8) {
/* 123 */           k--;
/* 124 */           j += k / 7;
/* 125 */           k = k % 7 + 1;
/*     */         } else {
/* 127 */           while (k <= 0) {
/* 128 */             k += 7;
/* 129 */             j--;
/*     */           }
/*     */         }
/* 132 */         k = toCalendarDayOfWeek(k);
/*     */       }
/* 134 */       paramCalendar.setWeekDate(this.field[35], j, k);
/*     */     }
/* 136 */     return paramCalendar;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 140 */     StringBuilder localStringBuilder = new StringBuilder();
/* 141 */     localStringBuilder.append("CalendarBuilder:[");
/* 142 */     for (int i = 0; i < this.field.length; i++) {
/* 143 */       if (isSet(i)) {
/* 144 */         localStringBuilder.append(i).append('=').append(this.field[(18 + i)]).append(',');
/*     */       }
/*     */     }
/* 147 */     i = localStringBuilder.length() - 1;
/* 148 */     if (localStringBuilder.charAt(i) == ',') {
/* 149 */       localStringBuilder.setLength(i);
/*     */     }
/* 151 */     localStringBuilder.append(']');
/* 152 */     return localStringBuilder.toString();
/*     */   }
/*     */ 
/*     */   static int toISODayOfWeek(int paramInt) {
/* 156 */     return paramInt == 1 ? 7 : paramInt - 1;
/*     */   }
/*     */ 
/*     */   static int toCalendarDayOfWeek(int paramInt) {
/* 160 */     if (!isValidDayOfWeek(paramInt))
/*     */     {
/* 162 */       return paramInt;
/*     */     }
/* 164 */     return paramInt == 7 ? 1 : paramInt + 1;
/*     */   }
/*     */ 
/*     */   static boolean isValidDayOfWeek(int paramInt) {
/* 168 */     return (paramInt > 0) && (paramInt <= 7);
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     java.text.CalendarBuilder
 * JD-Core Version:    0.6.2
 */