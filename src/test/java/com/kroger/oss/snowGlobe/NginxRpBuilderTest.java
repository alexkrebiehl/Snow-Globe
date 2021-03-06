/*
 * Snow-Globe
 *
 * Copyright 2017 The Kroger Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kroger.oss.snowGlobe;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.contains;

public class NginxRpBuilderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void shouldBuildComposeInformation() {
        NginxRpBuilder nginxRpBuilder = new NginxRpBuilder("snow-globe.yml", null);
        Map<String, Object> composeMap = nginxRpBuilder.buildComposeMap();
        assertThat(composeMap, Matchers.hasKey(nginxRpBuilder.buildRpContainerId()));
        Map<String, Object> argsMap = (Map<String, Object>) composeMap.get(nginxRpBuilder.buildRpContainerId());
        assertThat(argsMap, hasKey("container_name"));
        assertThat(argsMap, hasKey("volumes"));
        assertThat(argsMap, hasKey("ports"));
        assertThat(argsMap, hasKey("command"));
    }

    @Test
    public void shouldFilterVolumeMountsThatDontExist() {
        List<String> volumeMounts = new ArrayList<>();
        volumeMounts.add("src/integration/resources/nginx.conf:/etc/nginx/nginx.conf");
        volumeMounts.add("totally/doesnt/exist:/etc/nginx/not/there/");
        volumeMounts.add("totally/doesnt/exist/with/wildcard:/etc/nginx/not/there/");

        List<String> mounts = new NginxRpBuilder("snow-globe.yml", null)
                .calculateVolumeMounts(volumeMounts);

        assertThat(mounts, hasItem("src/integration/resources/nginx.conf:/etc/nginx/nginx.conf"));
        assertThat(mounts, not(hasItem("totally/doesnt/exist:/etc/nginx/not/there/")));
        assertThat(mounts, not(hasItem("totally/doesnt/exist/with/wildcard:/etc/nginx/not/there/")));
    }


    @Test
    public void shouldFilterVolumeMountsThatDontExistWithWildcard() {
        List<String> volumeMounts = new ArrayList<>();
        volumeMounts.add("src/integration/resources/nginx.conf:/etc/nginx/nginx.conf");
        volumeMounts.add("totally/doesnt/exist/with/wildcard/*:/etc/nginx/not/there/");

        List<String> mounts = new NginxRpBuilder("snow-globe.yml", null)
                .calculateVolumeMounts(volumeMounts);

        assertThat(mounts, hasItem("src/integration/resources/nginx.conf:/etc/nginx/nginx.conf"));
        assertThat(mounts, not(hasItems(contains("totally/doesnt/exist/with/wildcard"))));
    }
}