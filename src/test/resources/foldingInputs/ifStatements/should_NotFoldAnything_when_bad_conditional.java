package foldingInputs.ifStatements;

public class should_NotFoldAnything_when_bad_conditional {
    public int name(final int y) {
        final int x = 3;
        final boolean b = true;
        final Integer i = null;
        final char c = 'c';
        final String s = new String("Hello");
        if (x == 4){
            return x;
        }
        final boolean t = (Name.class == Name.class);
        return x;
      }
}
