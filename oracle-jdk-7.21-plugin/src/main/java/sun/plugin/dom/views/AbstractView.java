package sun.plugin.dom.views;

import org.w3c.dom.views.DocumentView;

public class AbstractView
  implements org.w3c.dom.views.AbstractView
{
  private DocumentView view;

  public AbstractView(DocumentView paramDocumentView)
  {
    this.view = paramDocumentView;
  }

  public DocumentView getDocument()
  {
    return this.view;
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin.dom.views.AbstractView
 * JD-Core Version:    0.6.2
 */