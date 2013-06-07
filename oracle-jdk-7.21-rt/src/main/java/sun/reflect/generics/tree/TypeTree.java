package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public abstract interface TypeTree extends Tree
{
  public abstract void accept(TypeTreeVisitor<?> paramTypeTreeVisitor);
}

/* Location:           /home/user1/Temp/jvm/rt.jar
 * Qualified Name:     sun.reflect.generics.tree.TypeTree
 * JD-Core Version:    0.6.2
 */