package pl.wavesoftware.plugs.testing.ansi;

/**
 * An ANSI encodable element.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface AnsiElement {

  /**
   * @return the ANSI escape code
   */
  @Override
  String toString();

}
