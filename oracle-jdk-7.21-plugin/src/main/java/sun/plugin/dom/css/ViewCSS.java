package sun.plugin.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.views.DocumentView;
import sun.plugin.dom.views.AbstractView;

public class ViewCSS extends AbstractView
  implements org.w3c.dom.css.ViewCSS
{
  public ViewCSS(DocumentView paramDocumentView)
  {
    super(paramDocumentView);
  }

  public CSSStyleDeclaration getComputedStyle(Element paramElement, String paramString)
  {
    DocumentCSS localDocumentCSS = (DocumentCSS)getDocument();
    return localDocumentCSS.getOverrideStyle(paramElement, paramString);
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.ViewCSS
 * JD-Core Version:    0.6.2
 */