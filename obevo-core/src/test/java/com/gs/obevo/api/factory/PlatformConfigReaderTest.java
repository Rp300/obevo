/**
 * Copyright 2017 Goldman Sachs.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.gs.obevo.api.factory;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlatformConfigReaderTest {
    private final PlatformConfigReader reader = new PlatformConfigReader();

    @Test
    public void testValidDefault() {
        ImmutableHierarchicalConfiguration properties = reader.readPlatformProperties(Lists.immutable.of("PlatformConfigReader/validDefault"));

        assertEquals("val1", properties.getString("prop1"));
        assertEquals("val2", properties.getString("prop2"));
        assertEquals("val3", properties.getString("key3.prop3"));
        assertEquals("val4", properties.getString("key4.prop4"));
        assertEquals(4, properties.size());
    }

    @Test
    public void testValidWithOverride() {
        ImmutableHierarchicalConfiguration properties = reader.readPlatformProperties(Lists.immutable.of("PlatformConfigReader/validWithOverride"));

        assertEquals(4, properties.size());
        assertEquals("val1", properties.getString("prop1"));
        assertEquals("val2Override", properties.getString("prop2"));
        assertEquals("val3Override", properties.getString("key3.prop3"));
        assertEquals("val4", properties.getString("key4.prop4"));
    }

    @Test
    public void testSameFileWarning() {
        ImmutableHierarchicalConfiguration properties = reader.readPlatformProperties(Lists.immutable.of("PlatformConfigReader/sameFileWarning"));

        assertEquals(4, properties.size());
        assertEquals("val1", properties.getString("prop1"));
        assertEquals("val2diff", properties.getString("prop2"));
        assertEquals("val3diff", properties.getString("key3.prop3"));
        assertEquals("val4", properties.getString("key4.prop4"));
    }

    @Test(expected = IllegalStateException.class)
    public void testBadMissingDefault() {
        reader.readPlatformProperties(Lists.immutable.of("PlatformConfigReader/badMissingDefault"));
    }

    @Test(expected = IllegalStateException.class)
    public void testBadSameFileNameWithSamePriority() {
        reader.readPlatformProperties(Lists.immutable.of("PlatformConfigReader/badSameFileSamePriority"));
    }
}