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

import io.vavr.Lazy;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.helpers.MarkerIgnoringBase;

import java.util.function.Supplier;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenLogger extends MarkerIgnoringBase {
  private static final long serialVersionUID = 20190207213304L;
  private final Lazy<Log> log;

  MavenLogger(Supplier<Log> logSupplier) {
    this.log = Lazy.of(logSupplier);
    name = Log.class.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return isDebugEnabled();
  }

  @Override
  public void trace(String msg) {
    debug(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    debug(format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    debug(format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    debug(format, arguments);
  }

  @Override
  public void trace(String msg, Throwable throwable) {
    debug(msg, throwable);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.get().isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    if (isDebugEnabled()) {
      log.get().debug(msg);
    }
  }

  @Override
  public void debug(String format, Object arg) {
    if (isDebugEnabled()) {
      log.get().debug(format(format, arg).getMessage());
    }
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    if (isDebugEnabled()) {
      log.get().debug(format(format, arg1, arg2).getMessage());
    }
  }

  @Override
  public void debug(String format, Object... arguments) {
    if (isDebugEnabled()) {
      log.get().debug(arrayFormat(format, arguments).getMessage());
    }
  }

  @Override
  public void debug(String msg, Throwable throwable) {
    if (isDebugEnabled()) {
      log.get().debug(msg, throwable);
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return log.get().isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    if (isInfoEnabled()) {
      log.get().info(msg);
    }
  }

  @Override
  public void info(String format, Object arg) {
    if (isInfoEnabled()) {
      log.get().info(format(format, arg).getMessage());
    }
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    if (isInfoEnabled()) {
      log.get().info(format(format, arg1, arg2).getMessage());
    }
  }

  @Override
  public void info(String format, Object... arguments) {
    if (isInfoEnabled()) {
      log.get().info(arrayFormat(format, arguments).getMessage());
    }
  }

  @Override
  public void info(String msg, Throwable throwable) {
    if (isInfoEnabled()) {
      log.get().info(msg, throwable);
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return log.get().isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    if (isWarnEnabled()) {
      log.get().warn(msg);
    }
  }

  @Override
  public void warn(String format, Object arg) {
    if (isWarnEnabled()) {
      log.get().warn(format(format, arg).getMessage());
    }
  }

  @Override
  public void warn(String format, Object... arguments) {
    if (isWarnEnabled()) {
      log.get().warn(arrayFormat(format, arguments).getMessage());
    }
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    if (isWarnEnabled()) {
      log.get().warn(format(format, arg1, arg2).getMessage());
    }
  }

  @Override
  public void warn(String msg, Throwable throwable) {
    if (isWarnEnabled()) {
      log.get().warn(msg, throwable);
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return log.get().isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    if (isErrorEnabled()) {
      log.get().error(msg);
    }
  }

  @Override
  public void error(String format, Object arg) {
    if (isErrorEnabled()) {
      log.get().error(format(format, arg).getMessage());
    }
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    if (isErrorEnabled()) {
      log.get().error(format(format, arg1, arg2).getMessage());
    }
  }

  @Override
  public void error(String format, Object... arguments) {
    if (isErrorEnabled()) {
      log.get().error(arrayFormat(format, arguments).getMessage());
    }
  }

  @Override
  public void error(String msg, Throwable throwable) {
    if (isErrorEnabled()) {
      log.get().error(msg, throwable);
    }
  }
}
