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
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.FormattingTuple;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenLogger extends AbstractLogger {
  private static final long serialVersionUID = 20190207213304L;
  private final Lazy<Log> log;

  MavenLogger(Supplier<Log> logSupplier, Class<?> caller) {
    this.log = Lazy.of(logSupplier);
    name = caller.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return isTraceEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return log.get().isDebugEnabled();
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return log.get().isInfoEnabled();
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isInfoEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return log.get().isWarnEnabled();
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isWarnEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return log.get().isErrorEnabled();
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isErrorEnabled();
  }

  @Override
  protected String getFullyQualifiedCallerName() {
    return name;
  }

  @Override
  protected void handleNormalizedLoggingCall(
    Level level,
    @Nullable Marker marker,
    String messagePattern,
    Object[] arguments,
    @Nullable Throwable throwable
  ) {
    CharSequence msg = message(marker, messagePattern, arguments);
    BiConsumer<CharSequence, Throwable> lfn2 = null;
    Consumer<CharSequence> lfn = null;
    switch (level) {
      case TRACE:
      case DEBUG:
        lfn = log.get()::debug;
        lfn2 = log.get()::debug;
        break;
      case INFO:
        lfn = log.get()::info;
        lfn2 = log.get()::info;
        break;
      case WARN:
        lfn = log.get()::warn;
        lfn2 = log.get()::warn;
        break;
      case ERROR:
        lfn = log.get()::error;
        lfn2 = log.get()::error;
        break;
      default:
        throw new IllegalStateException("Unknown level: " + level);
    }
    if (throwable != null) {
      lfn2.accept(msg, throwable);
    } else {
      lfn.accept(msg);
    }
  }

  private static CharSequence message(
    @Nullable Marker marker,
    String messagePattern,
    Object[] arguments
  ) {
    FormattingTuple ft = arrayFormat(messagePattern, arguments);
    if (marker != null) {
      return marker + " " + ft.getMessage();
    }
    return ft.getMessage();
  }
}
