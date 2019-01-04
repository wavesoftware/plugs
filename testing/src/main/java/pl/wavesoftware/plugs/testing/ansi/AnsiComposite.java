package pl.wavesoftware.plugs.testing.ansi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class AnsiComposite implements AnsiElement {
  private final List<AnsiElement> elements;

  private AnsiComposite(AnsiElement... elements) {
    this.elements = Arrays.asList(elements);
  }

  public static AnsiComposite of(AnsiElement... elements) {
    return new AnsiComposite(elements);
  }

  @Override
  public String toString() {
    return elements.stream()
      .map(AnsiElement::toString)
      .collect(Collectors.joining(";"));
  }
}
