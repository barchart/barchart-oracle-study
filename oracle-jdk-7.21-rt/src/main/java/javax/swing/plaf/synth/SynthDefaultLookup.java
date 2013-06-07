/*    */ package javax.swing.plaf.synth;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.plaf.ComponentUI;
/*    */ import sun.swing.DefaultLookup;
/*    */ 
/*    */ class SynthDefaultLookup extends DefaultLookup
/*    */ {
/*    */   public Object getDefault(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString)
/*    */   {
/* 38 */     if (!(paramComponentUI instanceof SynthUI)) {
/* 39 */       localObject1 = super.getDefault(paramJComponent, paramComponentUI, paramString);
/* 40 */       return localObject1;
/*    */     }
/* 42 */     Object localObject1 = ((SynthUI)paramComponentUI).getContext(paramJComponent);
/* 43 */     Object localObject2 = ((SynthContext)localObject1).getStyle().get((SynthContext)localObject1, paramString);
/* 44 */     ((SynthContext)localObject1).dispose();
/* 45 */     return localObject2;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.synth.SynthDefaultLookup
 * JD-Core Version:    0.6.2
 */