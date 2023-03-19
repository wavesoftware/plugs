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

package pl.wavesoftware.plugs.tools.maven.plugin.io;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * Creates a SLF4J compatible logger based on Maven logger.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface LoggerFactory {
  /**
   * Creates a logger from Maven log.
   *
   * @param log a maven log supplier
   * @param caller a caller class
   * @return a SLF4J compatible logger
   */
  Logger create(Supplier<Log> log, Class<?> caller);
}
