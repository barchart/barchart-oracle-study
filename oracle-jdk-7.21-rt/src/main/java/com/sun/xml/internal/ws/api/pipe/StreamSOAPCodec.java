package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.stream.XMLStreamReader;

public abstract interface StreamSOAPCodec extends Codec
{
  @NotNull
  public abstract Message decode(@NotNull XMLStreamReader paramXMLStreamReader);

  @NotNull
  public abstract Message decode(@NotNull XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec
 * JD-Core Version:    0.6.2
 */