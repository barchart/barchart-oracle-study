package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;

public abstract interface CSSMediaRule extends CSSRule
{
  public abstract MediaList getMedia();

  public abstract CSSRuleList getCssRules();

  public abstract int insertRule(String paramString, int paramInt)
    throws DOMException;

  public abstract void deleteRule(int paramInt)
    throws DOMException;
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     org.w3c.dom.css.CSSMediaRule
 * JD-Core Version:    0.6.2
 */