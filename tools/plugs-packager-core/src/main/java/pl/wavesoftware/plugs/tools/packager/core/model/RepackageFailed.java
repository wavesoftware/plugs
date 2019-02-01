/*
 * Copyright (c) 2019 Wave Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.wavesoftware.plugs.tools.packager.core.model;

import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Thrown if repackage operation has failed
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@SuppressWarnings("squid:S1162")
public final class RepackageFailed extends Exception {
  private static final long serialVersionUID = 20190208231616L;

  /**
   * Default constructor
   *
   * @param humanReadable a massage that is safe to be presented
   */
  public RepackageFailed(String humanReadable) {
    super(humanReadable);
  }

  /**
   * Default constructor
   *
   * @param humanReadable a massage that is safe to be presented
   * @param cause         a cause of exception
   */
  public RepackageFailed(String humanReadable, Throwable cause) {
    super(humanReadable, cause);
  }

  public static OrElse<Void> check(boolean condition) {
    return politeErrorSupplier -> {
      if (!condition) {
        throw new RepackageFailed(politeErrorSupplier.get());
      }
      return null;
    };
  }

  public static OrElse<Void> tring(IoPossibleBlock block) {
    return humanReadableSupplier -> {
      try {
        block.execute();
      } catch (IOException ex) {
        throw new RepackageFailed(humanReadableSupplier.get(), ex);
      }
      return null;
    };
  }

  public static <T> OrElse<T> tring(IoPossibleSupplier<T> supplier) {
    return humanReadableSupplier -> {
      try {
        return supplier.get();
      } catch (IOException ex) {
        throw new RepackageFailed(humanReadableSupplier.get(), ex);
      }
    };
  }

  public interface OrElse<T> {
    T or(Supplier<String> politeErrorSupplier) throws RepackageFailed;
    default T or(String politeError) throws RepackageFailed {
      return or(() -> politeError);
    }
    default T or(String politeErrorTemplate, Object... args) throws RepackageFailed {
      return or(
        () -> MessageFormatter
          .arrayFormat(politeErrorTemplate, args)
          .getMessage()
      );
    }
  }
}
