package foldingInputs.booleanLiterals;

public class should_NotFoldAnything_when_ThereAreNoBooleanLiterals {
    public int name(final int y) {
        final int x = 3 + y;
        final char c = 'c';
        final String s = new String("Hello");
        final boolean t = (Name.class == Name.class);
        return x;
      }
}
