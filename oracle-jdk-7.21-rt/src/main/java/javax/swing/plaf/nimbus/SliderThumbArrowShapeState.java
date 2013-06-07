/*    */ package javax.swing.plaf.nimbus;
/*    */ 
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ class SliderThumbArrowShapeState extends State
/*    */ {
/*    */   SliderThumbArrowShapeState()
/*    */   {
/* 33 */     super("ArrowShape");
/*    */   }
/*    */ 
/*    */   protected boolean isInState(JComponent paramJComponent) {
/* 37 */     return paramJComponent.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
/*    */   }
/*    */ }

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     javax.swing.plaf.nimbus.SliderThumbArrowShapeState
 * JD-Core Version:    0.6.2
 */