/*     */ package sun.awt.X11;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ class XAtomList
/*     */ {
/*  34 */   Set<XAtom> atoms = new HashSet();
/*     */ 
/*     */   public XAtomList()
/*     */   {
/*     */   }
/*     */ 
/*     */   public XAtomList(long paramLong, int paramInt)
/*     */   {
/*  48 */     init(paramLong, paramInt);
/*     */   }
/*     */   private void init(long paramLong, int paramInt) {
/*  51 */     for (int i = 0; i < paramInt; i++)
/*  52 */       add(new XAtom(XToolkit.getDisplay(), XAtom.getAtom(paramLong + paramInt * XAtom.getAtomSize())));
/*     */   }
/*     */ 
/*     */   public XAtomList(XAtom[] paramArrayOfXAtom)
/*     */   {
/*  61 */     init(paramArrayOfXAtom);
/*     */   }
/*     */   private void init(XAtom[] paramArrayOfXAtom) {
/*  64 */     for (int i = 0; i < paramArrayOfXAtom.length; i++)
/*  65 */       add(paramArrayOfXAtom[i]);
/*     */   }
/*     */ 
/*     */   public XAtom[] getAtoms()
/*     */   {
/*  73 */     XAtom[] arrayOfXAtom = new XAtom[size()];
/*  74 */     Iterator localIterator = this.atoms.iterator();
/*  75 */     int i = 0;
/*  76 */     while (localIterator.hasNext()) {
/*  77 */       arrayOfXAtom[(i++)] = ((XAtom)localIterator.next());
/*     */     }
/*  79 */     return arrayOfXAtom;
/*     */   }
/*     */ 
/*     */   public long getAtomsData()
/*     */   {
/*  89 */     return XAtom.toData(getAtoms());
/*     */   }
/*     */ 
/*     */   public boolean contains(XAtom paramXAtom)
/*     */   {
/*  96 */     return this.atoms.contains(paramXAtom);
/*     */   }
/*     */ 
/*     */   public void add(XAtom paramXAtom)
/*     */   {
/* 103 */     this.atoms.add(paramXAtom);
/*     */   }
/*     */ 
/*     */   public void remove(XAtom paramXAtom)
/*     */   {
/* 110 */     this.atoms.remove(paramXAtom);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 118 */     return this.atoms.size();
/*     */   }
/*     */ 
/*     */   public XAtomList subset(int paramInt, Map<Integer, XAtom> paramMap)
/*     */   {
/* 126 */     XAtomList localXAtomList = new XAtomList();
/* 127 */     Iterator localIterator = paramMap.keySet().iterator();
/* 128 */     while (localIterator.hasNext()) {
/* 129 */       Integer localInteger = (Integer)localIterator.next();
/* 130 */       if ((paramInt & localInteger.intValue()) == localInteger.intValue()) {
/* 131 */         XAtom localXAtom = (XAtom)paramMap.get(localInteger);
/* 132 */         if (contains(localXAtom)) {
/* 133 */           localXAtomList.add(localXAtom);
/*     */         }
/*     */       }
/*     */     }
/* 137 */     return localXAtomList;
/*     */   }
/*     */ 
/*     */   public Iterator<XAtom> iterator()
/*     */   {
/* 144 */     return this.atoms.iterator();
/*     */   }
/*     */ 
/*     */   public void addAll(XAtomList paramXAtomList)
/*     */   {
/* 151 */     Iterator localIterator = paramXAtomList.iterator();
/* 152 */     while (localIterator.hasNext())
/* 153 */       add((XAtom)localIterator.next());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 158 */     StringBuffer localStringBuffer = new StringBuffer();
/* 159 */     localStringBuffer.append("[");
/* 160 */     Iterator localIterator = this.atoms.iterator();
/* 161 */     while (localIterator.hasNext()) {
/* 162 */       localStringBuffer.append(localIterator.next().toString());
/* 163 */       if (localIterator.hasNext()) {
/* 164 */         localStringBuffer.append(", ");
/*     */       }
/*     */     }
/* 167 */     localStringBuffer.append("]");
/* 168 */     return localStringBuffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.awt.X11.XAtomList
 * JD-Core Version:    0.6.2
 */