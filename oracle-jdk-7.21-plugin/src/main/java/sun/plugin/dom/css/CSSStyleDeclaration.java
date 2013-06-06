package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.css.CSS2Properties;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSStyleDeclaration
  implements org.w3c.dom.css.CSSStyleDeclaration, CSS2Properties
{
  protected DOMObject obj;
  private Document document;

  public CSSStyleDeclaration(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
  }

  public String getCssText()
  {
    return getPropertyValue("cssText");
  }

  public void setCssText(String paramString)
    throws DOMException
  {
    setProperty("cssText", paramString, null);
  }

  public String getPropertyValue(String paramString)
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, paramString);
  }

  public CSSValue getPropertyCSSValue(String paramString)
  {
    return DOMObjectFactory.createCSSValue(this.obj.call("getPropertyValue", new Object[] { paramString }), this.document);
  }

  public String removeProperty(String paramString)
    throws DOMException
  {
    return DOMObjectHelper.callStringMethod(this.obj, "removeProperty", new Object[] { paramString });
  }

  public String getPropertyPriority(String paramString)
  {
    return DOMObjectHelper.callStringMethod(this.obj, "getPropertyPriority", new Object[] { paramString });
  }

  public void setProperty(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, paramString1, paramString2);
  }

  public int getLength()
  {
    return ((Number)this.obj.getMember("length")).intValue();
  }

  public String item(int paramInt)
  {
    return DOMObjectHelper.callStringMethod(this.obj, "item", new Object[] { new Integer(paramInt) });
  }

  public CSSRule getParentRule()
  {
    return DOMObjectFactory.createCSSRule((DOMObject)this.obj.getMember("parentRule"), this.document);
  }

  public String getAzimuth()
  {
    return getPropertyValue("azimuth");
  }

  public void setAzimuth(String paramString)
    throws DOMException
  {
    setProperty("azimuth", paramString, null);
  }

  public String getBackground()
  {
    return getPropertyValue("background");
  }

  public void setBackground(String paramString)
    throws DOMException
  {
    setProperty("background", paramString, null);
  }

  public String getBackgroundAttachment()
  {
    return getPropertyValue("backgroundAttachment");
  }

  public void setBackgroundAttachment(String paramString)
    throws DOMException
  {
    setProperty("backgroundAttachment", paramString, null);
  }

  public String getBackgroundColor()
  {
    return getPropertyValue("backgroundColor");
  }

  public void setBackgroundColor(String paramString)
    throws DOMException
  {
    setProperty("backgroundColor", paramString, null);
  }

  public String getBackgroundImage()
  {
    return getPropertyValue("backgroundImage");
  }

  public void setBackgroundImage(String paramString)
    throws DOMException
  {
    setProperty("backgroundImage", paramString, null);
  }

  public String getBackgroundPosition()
  {
    return getPropertyValue("backgroundPosition");
  }

  public void setBackgroundPosition(String paramString)
    throws DOMException
  {
    setProperty("backgroundPosition", paramString, null);
  }

  public String getBackgroundRepeat()
  {
    return getPropertyValue("backgroundRepeat");
  }

  public void setBackgroundRepeat(String paramString)
    throws DOMException
  {
    setProperty("backgroundRepeat", paramString, null);
  }

  public String getBorder()
  {
    return getPropertyValue("border");
  }

  public void setBorder(String paramString)
    throws DOMException
  {
    setProperty("border", paramString, null);
  }

  public String getBorderCollapse()
  {
    return getPropertyValue("borderCollapse");
  }

  public void setBorderCollapse(String paramString)
    throws DOMException
  {
    setProperty("borderCollapse", paramString, null);
  }

  public String getBorderColor()
  {
    return getPropertyValue("borderColor");
  }

  public void setBorderColor(String paramString)
    throws DOMException
  {
    setProperty("borderColor", paramString, null);
  }

  public String getBorderSpacing()
  {
    return getPropertyValue("borderSpacing");
  }

  public void setBorderSpacing(String paramString)
    throws DOMException
  {
    setProperty("borderSpacing", paramString, null);
  }

  public String getBorderStyle()
  {
    return getPropertyValue("borderStyle");
  }

  public void setBorderStyle(String paramString)
    throws DOMException
  {
    setProperty("borderStyle", paramString, null);
  }

  public String getBorderTop()
  {
    return getPropertyValue("borderTop");
  }

  public void setBorderTop(String paramString)
    throws DOMException
  {
    setProperty("borderTop", paramString, null);
  }

  public String getBorderRight()
  {
    return getPropertyValue("borderRight");
  }

  public void setBorderRight(String paramString)
    throws DOMException
  {
    setProperty("borderRight", paramString, null);
  }

  public String getBorderBottom()
  {
    return getPropertyValue("borderBottom");
  }

  public void setBorderBottom(String paramString)
    throws DOMException
  {
    setProperty("borderBottom", paramString, null);
  }

  public String getBorderLeft()
  {
    return getPropertyValue("borderLeft");
  }

  public void setBorderLeft(String paramString)
    throws DOMException
  {
    setProperty("borderLeft", paramString, null);
  }

  public String getBorderTopColor()
  {
    return getPropertyValue("borderTopColor");
  }

  public void setBorderTopColor(String paramString)
    throws DOMException
  {
    setProperty("borderTopColor", paramString, null);
  }

  public String getBorderRightColor()
  {
    return getPropertyValue("borderRightColor");
  }

  public void setBorderRightColor(String paramString)
    throws DOMException
  {
    setProperty("borderRightColor", paramString, null);
  }

  public String getBorderBottomColor()
  {
    return getPropertyValue("borderBottomColor");
  }

  public void setBorderBottomColor(String paramString)
    throws DOMException
  {
    setProperty("borderBottomColor", paramString, null);
  }

  public String getBorderLeftColor()
  {
    return getPropertyValue("borderLeftColor");
  }

  public void setBorderLeftColor(String paramString)
    throws DOMException
  {
    setProperty("borderLeftColor", paramString, null);
  }

  public String getBorderTopStyle()
  {
    return getPropertyValue("borderTopStyle");
  }

  public void setBorderTopStyle(String paramString)
    throws DOMException
  {
    setProperty("borderTopStyle", paramString, null);
  }

  public String getBorderRightStyle()
  {
    return getPropertyValue("borderRightStyle");
  }

  public void setBorderRightStyle(String paramString)
    throws DOMException
  {
    setProperty("borderRightStyle", paramString, null);
  }

  public String getBorderBottomStyle()
  {
    return getPropertyValue("borderBottomStyle");
  }

  public void setBorderBottomStyle(String paramString)
    throws DOMException
  {
    setProperty("borderBottomStyle", paramString, null);
  }

  public String getBorderLeftStyle()
  {
    return getPropertyValue("borderLeftStyle");
  }

  public void setBorderLeftStyle(String paramString)
    throws DOMException
  {
    setProperty("borderLeftStyle", paramString, null);
  }

  public String getBorderTopWidth()
  {
    return getPropertyValue("borderTopWidth");
  }

  public void setBorderTopWidth(String paramString)
    throws DOMException
  {
    setProperty("borderTopWidth", paramString, null);
  }

  public String getBorderRightWidth()
  {
    return getPropertyValue("borderRightWidth");
  }

  public void setBorderRightWidth(String paramString)
    throws DOMException
  {
    setProperty("borderRightWidth", paramString, null);
  }

  public String getBorderBottomWidth()
  {
    return getPropertyValue("borderBottomWidth");
  }

  public void setBorderBottomWidth(String paramString)
    throws DOMException
  {
    setProperty("borderBottomWidth", paramString, null);
  }

  public String getBorderLeftWidth()
  {
    return getPropertyValue("borderLeftWidth");
  }

  public void setBorderLeftWidth(String paramString)
    throws DOMException
  {
    setProperty("borderLeftWidth", paramString, null);
  }

  public String getBorderWidth()
  {
    return getPropertyValue("borderWidth");
  }

  public void setBorderWidth(String paramString)
    throws DOMException
  {
    setProperty("borderWidth", paramString, null);
  }

  public String getBottom()
  {
    return getPropertyValue("bottom");
  }

  public void setBottom(String paramString)
    throws DOMException
  {
    setProperty("bottom", paramString, null);
  }

  public String getCaptionSide()
  {
    return getPropertyValue("captionSide");
  }

  public void setCaptionSide(String paramString)
    throws DOMException
  {
    setProperty("captionSide", paramString, null);
  }

  public String getClear()
  {
    return getPropertyValue("clear");
  }

  public void setClear(String paramString)
    throws DOMException
  {
    setProperty("clear", paramString, null);
  }

  public String getClip()
  {
    return getPropertyValue("clip");
  }

  public void setClip(String paramString)
    throws DOMException
  {
    setProperty("clip", paramString, null);
  }

  public String getColor()
  {
    return getPropertyValue("color");
  }

  public void setColor(String paramString)
    throws DOMException
  {
    setProperty("color", paramString, null);
  }

  public String getContent()
  {
    return getPropertyValue("content");
  }

  public void setContent(String paramString)
    throws DOMException
  {
    setProperty("content", paramString, null);
  }

  public String getCounterIncrement()
  {
    return getPropertyValue("counterIncrement");
  }

  public void setCounterIncrement(String paramString)
    throws DOMException
  {
    setProperty("counterIncrement", paramString, null);
  }

  public String getCounterReset()
  {
    return getPropertyValue("counterReset");
  }

  public void setCounterReset(String paramString)
    throws DOMException
  {
    setProperty("counterReset", paramString, null);
  }

  public String getCue()
  {
    return getPropertyValue("cue");
  }

  public void setCue(String paramString)
    throws DOMException
  {
    setProperty("cue", paramString, null);
  }

  public String getCueAfter()
  {
    return getPropertyValue("cueAfter");
  }

  public void setCueAfter(String paramString)
    throws DOMException
  {
    setProperty("cueAfter", paramString, null);
  }

  public String getCueBefore()
  {
    return getPropertyValue("cueBefore");
  }

  public void setCueBefore(String paramString)
    throws DOMException
  {
    setProperty("cueBefore", paramString, null);
  }

  public String getCursor()
  {
    return getPropertyValue("cursor");
  }

  public void setCursor(String paramString)
    throws DOMException
  {
    setProperty("cursor", paramString, null);
  }

  public String getDirection()
  {
    return getPropertyValue("direction");
  }

  public void setDirection(String paramString)
    throws DOMException
  {
    setProperty("direction", paramString, null);
  }

  public String getDisplay()
  {
    return getPropertyValue("display");
  }

  public void setDisplay(String paramString)
    throws DOMException
  {
    setProperty("display", paramString, null);
  }

  public String getElevation()
  {
    return getPropertyValue("elevation");
  }

  public void setElevation(String paramString)
    throws DOMException
  {
    setProperty("elevation", paramString, null);
  }

  public String getEmptyCells()
  {
    return getPropertyValue("emptyCells");
  }

  public void setEmptyCells(String paramString)
    throws DOMException
  {
    setProperty("emptyCells", paramString, null);
  }

  public String getCssFloat()
  {
    return getPropertyValue("cssFloat");
  }

  public void setCssFloat(String paramString)
    throws DOMException
  {
    setProperty("cssFloat", paramString, null);
  }

  public String getFont()
  {
    return getPropertyValue("font");
  }

  public void setFont(String paramString)
    throws DOMException
  {
    setProperty("font", paramString, null);
  }

  public String getFontFamily()
  {
    return getPropertyValue("fontFamily");
  }

  public void setFontFamily(String paramString)
    throws DOMException
  {
    setProperty("fontFamily", paramString, null);
  }

  public String getFontSize()
  {
    return getPropertyValue("fontSize");
  }

  public void setFontSize(String paramString)
    throws DOMException
  {
    setProperty("fontSize", paramString, null);
  }

  public String getFontSizeAdjust()
  {
    return getPropertyValue("fontSizeAdjust");
  }

  public void setFontSizeAdjust(String paramString)
    throws DOMException
  {
    setProperty("fontSizeAdjust", paramString, null);
  }

  public String getFontStretch()
  {
    return getPropertyValue("fontStretch");
  }

  public void setFontStretch(String paramString)
    throws DOMException
  {
    setProperty("fontStretch", paramString, null);
  }

  public String getFontStyle()
  {
    return getPropertyValue("fontStyle");
  }

  public void setFontStyle(String paramString)
    throws DOMException
  {
    setProperty("fontStyle", paramString, null);
  }

  public String getFontVariant()
  {
    return getPropertyValue("fontVariant");
  }

  public void setFontVariant(String paramString)
    throws DOMException
  {
    setProperty("fontVariant", paramString, null);
  }

  public String getFontWeight()
  {
    return getPropertyValue("fontWeight");
  }

  public void setFontWeight(String paramString)
    throws DOMException
  {
    setProperty("fontWeight", paramString, null);
  }

  public String getHeight()
  {
    return getPropertyValue("height");
  }

  public void setHeight(String paramString)
    throws DOMException
  {
    setProperty("height", paramString, null);
  }

  public String getLeft()
  {
    return getPropertyValue("left");
  }

  public void setLeft(String paramString)
    throws DOMException
  {
    setProperty("left", paramString, null);
  }

  public String getLetterSpacing()
  {
    return getPropertyValue("letterSpacing");
  }

  public void setLetterSpacing(String paramString)
    throws DOMException
  {
    setProperty("letterSpacing", paramString, null);
  }

  public String getLineHeight()
  {
    return getPropertyValue("lineHeight");
  }

  public void setLineHeight(String paramString)
    throws DOMException
  {
    setProperty("lineHeight", paramString, null);
  }

  public String getListStyle()
  {
    return getPropertyValue("listStyle");
  }

  public void setListStyle(String paramString)
    throws DOMException
  {
    setProperty("listStyle", paramString, null);
  }

  public String getListStyleImage()
  {
    return getPropertyValue("listStyleImage");
  }

  public void setListStyleImage(String paramString)
    throws DOMException
  {
    setProperty("listStyleImage", paramString, null);
  }

  public String getListStylePosition()
  {
    return getPropertyValue("listStylePosition");
  }

  public void setListStylePosition(String paramString)
    throws DOMException
  {
    setProperty("listStylePosition", paramString, null);
  }

  public String getListStyleType()
  {
    return getPropertyValue("listStyleType");
  }

  public void setListStyleType(String paramString)
    throws DOMException
  {
    setProperty("listStyleType", paramString, null);
  }

  public String getMargin()
  {
    return getPropertyValue("margin");
  }

  public void setMargin(String paramString)
    throws DOMException
  {
    setProperty("margin", paramString, null);
  }

  public String getMarginTop()
  {
    return getPropertyValue("marginTop");
  }

  public void setMarginTop(String paramString)
    throws DOMException
  {
    setProperty("marginTop", paramString, null);
  }

  public String getMarginRight()
  {
    return getPropertyValue("marginRight");
  }

  public void setMarginRight(String paramString)
    throws DOMException
  {
    setProperty("marginRight", paramString, null);
  }

  public String getMarginBottom()
  {
    return getPropertyValue("marginBottom");
  }

  public void setMarginBottom(String paramString)
    throws DOMException
  {
    setProperty("marginBottom", paramString, null);
  }

  public String getMarginLeft()
  {
    return getPropertyValue("marginLeft");
  }

  public void setMarginLeft(String paramString)
    throws DOMException
  {
    setProperty("marginLeft", paramString, null);
  }

  public String getMarkerOffset()
  {
    return getPropertyValue("markerOffset");
  }

  public void setMarkerOffset(String paramString)
    throws DOMException
  {
    setProperty("markerOffset", paramString, null);
  }

  public String getMarks()
  {
    return getPropertyValue("marks");
  }

  public void setMarks(String paramString)
    throws DOMException
  {
    setProperty("marks", paramString, null);
  }

  public String getMaxHeight()
  {
    return getPropertyValue("maxHeight");
  }

  public void setMaxHeight(String paramString)
    throws DOMException
  {
    setProperty("maxHeight", paramString, null);
  }

  public String getMaxWidth()
  {
    return getPropertyValue("maxWidth");
  }

  public void setMaxWidth(String paramString)
    throws DOMException
  {
    setProperty("maxWidth", paramString, null);
  }

  public String getMinHeight()
  {
    return getPropertyValue("minHeight");
  }

  public void setMinHeight(String paramString)
    throws DOMException
  {
    setProperty("minHeight", paramString, null);
  }

  public String getMinWidth()
  {
    return getPropertyValue("minWidth");
  }

  public void setMinWidth(String paramString)
    throws DOMException
  {
    setProperty("minWidth", paramString, null);
  }

  public String getOrphans()
  {
    return getPropertyValue("orphans");
  }

  public void setOrphans(String paramString)
    throws DOMException
  {
    setProperty("orphans", paramString, null);
  }

  public String getOutline()
  {
    return getPropertyValue("outline");
  }

  public void setOutline(String paramString)
    throws DOMException
  {
    setProperty("outline", paramString, null);
  }

  public String getOutlineColor()
  {
    return getPropertyValue("outlineColor");
  }

  public void setOutlineColor(String paramString)
    throws DOMException
  {
    setProperty("outlineColor", paramString, null);
  }

  public String getOutlineStyle()
  {
    return getPropertyValue("outlineStyle");
  }

  public void setOutlineStyle(String paramString)
    throws DOMException
  {
    setProperty("outlineStyle", paramString, null);
  }

  public String getOutlineWidth()
  {
    return getPropertyValue("outlineWidth");
  }

  public void setOutlineWidth(String paramString)
    throws DOMException
  {
    setProperty("outlineWidth", paramString, null);
  }

  public String getOverflow()
  {
    return getPropertyValue("overflow");
  }

  public void setOverflow(String paramString)
    throws DOMException
  {
    setProperty("overflow", paramString, null);
  }

  public String getPadding()
  {
    return getPropertyValue("padding");
  }

  public void setPadding(String paramString)
    throws DOMException
  {
    setProperty("padding", paramString, null);
  }

  public String getPaddingTop()
  {
    return getPropertyValue("paddingTop");
  }

  public void setPaddingTop(String paramString)
    throws DOMException
  {
    setProperty("paddingTop", paramString, null);
  }

  public String getPaddingRight()
  {
    return getPropertyValue("paddingRight");
  }

  public void setPaddingRight(String paramString)
    throws DOMException
  {
    setProperty("paddingRight", paramString, null);
  }

  public String getPaddingBottom()
  {
    return getPropertyValue("paddingBottom");
  }

  public void setPaddingBottom(String paramString)
    throws DOMException
  {
    setProperty("paddingBottom", paramString, null);
  }

  public String getPaddingLeft()
  {
    return getPropertyValue("paddingLeft");
  }

  public void setPaddingLeft(String paramString)
    throws DOMException
  {
    setProperty("paddingLeft", paramString, null);
  }

  public String getPage()
  {
    return getPropertyValue("page");
  }

  public void setPage(String paramString)
    throws DOMException
  {
    setProperty("page", paramString, null);
  }

  public String getPageBreakAfter()
  {
    return getPropertyValue("pageBreakAfter");
  }

  public void setPageBreakAfter(String paramString)
    throws DOMException
  {
    setProperty("pageBreakAfter", paramString, null);
  }

  public String getPageBreakBefore()
  {
    return getPropertyValue("pageBreakBefore");
  }

  public void setPageBreakBefore(String paramString)
    throws DOMException
  {
    setProperty("pageBreakBefore", paramString, null);
  }

  public String getPageBreakInside()
  {
    return getPropertyValue("pageBreakInside");
  }

  public void setPageBreakInside(String paramString)
    throws DOMException
  {
    setProperty("pageBreakInside", paramString, null);
  }

  public String getPause()
  {
    return getPropertyValue("pause");
  }

  public void setPause(String paramString)
    throws DOMException
  {
    setProperty("pause", paramString, null);
  }

  public String getPauseAfter()
  {
    return getPropertyValue("pauseAfter");
  }

  public void setPauseAfter(String paramString)
    throws DOMException
  {
    setProperty("pauseAfter", paramString, null);
  }

  public String getPauseBefore()
  {
    return getPropertyValue("pauseBefore");
  }

  public void setPauseBefore(String paramString)
    throws DOMException
  {
    setProperty("pauseBefore", paramString, null);
  }

  public String getPitch()
  {
    return getPropertyValue("pitch");
  }

  public void setPitch(String paramString)
    throws DOMException
  {
    setProperty("pitch", paramString, null);
  }

  public String getPitchRange()
  {
    return getPropertyValue("pitchRange");
  }

  public void setPitchRange(String paramString)
    throws DOMException
  {
    setProperty("pitchRange", paramString, null);
  }

  public String getPlayDuring()
  {
    return getPropertyValue("playDuring");
  }

  public void setPlayDuring(String paramString)
    throws DOMException
  {
    setProperty("playDuring", paramString, null);
  }

  public String getPosition()
  {
    return getPropertyValue("position");
  }

  public void setPosition(String paramString)
    throws DOMException
  {
    setProperty("position", paramString, null);
  }

  public String getQuotes()
  {
    return getPropertyValue("quotes");
  }

  public void setQuotes(String paramString)
    throws DOMException
  {
    setProperty("quotes", paramString, null);
  }

  public String getRichness()
  {
    return getPropertyValue("richness");
  }

  public void setRichness(String paramString)
    throws DOMException
  {
    setProperty("richness", paramString, null);
  }

  public String getRight()
  {
    return getPropertyValue("right");
  }

  public void setRight(String paramString)
    throws DOMException
  {
    setProperty("right", paramString, null);
  }

  public String getSize()
  {
    return getPropertyValue("size");
  }

  public void setSize(String paramString)
    throws DOMException
  {
    setProperty("size", paramString, null);
  }

  public String getSpeak()
  {
    return getPropertyValue("speak");
  }

  public void setSpeak(String paramString)
    throws DOMException
  {
    setProperty("speak", paramString, null);
  }

  public String getSpeakHeader()
  {
    return getPropertyValue("speakHeader");
  }

  public void setSpeakHeader(String paramString)
    throws DOMException
  {
    setProperty("speakHeader", paramString, null);
  }

  public String getSpeakNumeral()
  {
    return getPropertyValue("speakNumeral");
  }

  public void setSpeakNumeral(String paramString)
    throws DOMException
  {
    setProperty("speakNumeral", paramString, null);
  }

  public String getSpeakPunctuation()
  {
    return getPropertyValue("speakPunctuation");
  }

  public void setSpeakPunctuation(String paramString)
    throws DOMException
  {
    setProperty("speakPunctuation", paramString, null);
  }

  public String getSpeechRate()
  {
    return getPropertyValue("speechRate");
  }

  public void setSpeechRate(String paramString)
    throws DOMException
  {
    setProperty("speechRate", paramString, null);
  }

  public String getStress()
  {
    return getPropertyValue("stress");
  }

  public void setStress(String paramString)
    throws DOMException
  {
    setProperty("stress", paramString, null);
  }

  public String getTableLayout()
  {
    return getPropertyValue("tableLayout");
  }

  public void setTableLayout(String paramString)
    throws DOMException
  {
    setProperty("tableLayout", paramString, null);
  }

  public String getTextAlign()
  {
    return getPropertyValue("textAlign");
  }

  public void setTextAlign(String paramString)
    throws DOMException
  {
    setProperty("textAlign", paramString, null);
  }

  public String getTextDecoration()
  {
    return getPropertyValue("textDecoration");
  }

  public void setTextDecoration(String paramString)
    throws DOMException
  {
    setProperty("textDecoration", paramString, null);
  }

  public String getTextIndent()
  {
    return getPropertyValue("textIndent");
  }

  public void setTextIndent(String paramString)
    throws DOMException
  {
    setProperty("textIndent", paramString, null);
  }

  public String getTextShadow()
  {
    return getPropertyValue("textShadow");
  }

  public void setTextShadow(String paramString)
    throws DOMException
  {
    setProperty("textShadow", paramString, null);
  }

  public String getTextTransform()
  {
    return getPropertyValue("textTransform");
  }

  public void setTextTransform(String paramString)
    throws DOMException
  {
    setProperty("textTransform", paramString, null);
  }

  public String getTop()
  {
    return getPropertyValue("top");
  }

  public void setTop(String paramString)
    throws DOMException
  {
    setProperty("top", paramString, null);
  }

  public String getUnicodeBidi()
  {
    return getPropertyValue("unicodeBidi");
  }

  public void setUnicodeBidi(String paramString)
    throws DOMException
  {
    setProperty("unicodeBidi", paramString, null);
  }

  public String getVerticalAlign()
  {
    return getPropertyValue("verticalAlign");
  }

  public void setVerticalAlign(String paramString)
    throws DOMException
  {
    setProperty("verticalAlign", paramString, null);
  }

  public String getVisibility()
  {
    return getPropertyValue("visibility");
  }

  public void setVisibility(String paramString)
    throws DOMException
  {
    setProperty("visibility", paramString, null);
  }

  public String getVoiceFamily()
  {
    return getPropertyValue("voiceFamily");
  }

  public void setVoiceFamily(String paramString)
    throws DOMException
  {
    setProperty("voiceFamily", paramString, null);
  }

  public String getVolume()
  {
    return getPropertyValue("volumn");
  }

  public void setVolume(String paramString)
    throws DOMException
  {
    setProperty("volumn", paramString, null);
  }

  public String getWhiteSpace()
  {
    return getPropertyValue("whiteSpace");
  }

  public void setWhiteSpace(String paramString)
    throws DOMException
  {
    setProperty("whiteSpace", paramString, null);
  }

  public String getWidows()
  {
    return getPropertyValue("widows");
  }

  public void setWidows(String paramString)
    throws DOMException
  {
    setProperty("widows", paramString, null);
  }

  public String getWidth()
  {
    return getPropertyValue("width");
  }

  public void setWidth(String paramString)
    throws DOMException
  {
    setProperty("width", paramString, null);
  }

  public String getWordSpacing()
  {
    return getPropertyValue("wordSpacing");
  }

  public void setWordSpacing(String paramString)
    throws DOMException
  {
    setProperty("wordSpacing", paramString, null);
  }

  public String getZIndex()
  {
    return getPropertyValue("zIndex");
  }

  public void setZIndex(String paramString)
    throws DOMException
  {
    setProperty("zIndex", paramString, null);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSStyleDeclaration
 * JD-Core Version:    0.6.2
 */