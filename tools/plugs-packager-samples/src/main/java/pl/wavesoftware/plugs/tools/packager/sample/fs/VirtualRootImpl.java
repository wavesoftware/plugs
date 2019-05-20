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

package pl.wavesoftware.plugs.tools.packager.sample.fs;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.springframework.beans.factory.DisposableBean;
import pl.wavesoftware.sampler.spring.Sample;

import java.nio.file.FileSystem;
import java.nio.file.Path;

@Sample
final class VirtualRootImpl implements VirtualRoot, DisposableBean {
  private final FileSystem fs = Jimfs.newFileSystem(Configuration.unix());

  @Override
  public Path create() {
    return fs.getPath("/");
  }

  @Override
  public void destroy() throws Exception {
    fs.close();
  }
}
