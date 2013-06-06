package sun.plugin2.message;

import com.sun.deploy.util.SystemUtils;
import java.io.IOException;
import java.util.Map;

public class KeyEventMessage extends EventMessage
{
  public static final int ID = 83;
  private int type;
  private int modifierFlags;
  private String characters;
  private String charactersIgnoringModifiers;
  private boolean isARepeat;
  private int keyCode;
  private boolean needsKeyTyped;

  public KeyEventMessage(Conversation paramConversation)
  {
    super(83, paramConversation);
  }

  public KeyEventMessage(Conversation paramConversation, int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, boolean paramBoolean1, int paramInt4, boolean paramBoolean2)
  {
    super(83, paramConversation, paramInt1);
    this.type = paramInt2;
    this.modifierFlags = paramInt3;
    this.characters = paramString1;
    this.charactersIgnoringModifiers = paramString2;
    this.isARepeat = paramBoolean1;
    this.keyCode = paramInt4;
    this.needsKeyTyped = paramBoolean2;
  }

  public int getType()
  {
    return this.type;
  }

  public int getModifierFlags()
  {
    return this.modifierFlags;
  }

  public String getCharacters()
  {
    return this.characters;
  }

  public String getCharactersIgnoringModifiers()
  {
    return this.charactersIgnoringModifiers;
  }

  public boolean isARepeat()
  {
    return this.isARepeat;
  }

  public int getKeyCode()
  {
    return this.keyCode;
  }

  public boolean needsKeyTyped()
  {
    return this.needsKeyTyped;
  }

  public void writeFields(Serializer paramSerializer)
    throws IOException
  {
    super.writeFields(paramSerializer);
    paramSerializer.writeInt(this.type);
    paramSerializer.writeInt(this.modifierFlags);
    paramSerializer.writeUTF(this.characters == null ? "" : this.characters);
    paramSerializer.writeUTF(this.charactersIgnoringModifiers == null ? "" : this.charactersIgnoringModifiers);
    paramSerializer.writeBoolean(this.isARepeat);
    paramSerializer.writeInt(this.keyCode);
    paramSerializer.writeBoolean(this.needsKeyTyped);
  }

  public void readFields(Serializer paramSerializer)
    throws IOException
  {
    super.readFields(paramSerializer);
    this.type = paramSerializer.readInt();
    this.modifierFlags = paramSerializer.readInt();
    this.characters = paramSerializer.readUTF();
    this.charactersIgnoringModifiers = paramSerializer.readUTF();
    this.isARepeat = paramSerializer.readBoolean();
    this.keyCode = paramSerializer.readInt();
    this.needsKeyTyped = paramSerializer.readBoolean();
  }

  public void flattenInto(Map paramMap)
  {
    paramMap.put("type", SystemUtils.integerValueOf(this.type));
    paramMap.put("modifierFlags", SystemUtils.integerValueOf(this.modifierFlags));
    paramMap.put("characters", this.characters == null ? "" : this.characters);
    paramMap.put("charactersIgnoringModifiers", this.charactersIgnoringModifiers == null ? "" : this.charactersIgnoringModifiers);
    paramMap.put("isARepeat", Boolean.valueOf(this.isARepeat));
    paramMap.put("keyCode", SystemUtils.integerValueOf(this.keyCode));
    paramMap.put("needsKeyTyped", Boolean.valueOf(this.needsKeyTyped));
  }
}

/* Location:           /home/user1/Temp/jvm/plugin.jar
 * Qualified Name:     sun.plugin2.message.KeyEventMessage
 * JD-Core Version:    0.6.2
 */