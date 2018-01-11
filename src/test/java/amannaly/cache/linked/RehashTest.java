/*
 *      Copyright (C) 2014 Robert Stupp, Koeln, Germany, robert-stupp.de
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package amannaly.cache.linked;

import amannaly.ByteArraySerializer;
import com.google.common.primitives.Longs;
import amannaly.cache.OHCache;
import amannaly.cache.OHCacheBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RehashTest
{
    @AfterMethod(alwaysRun = true)
    public void deinit()
    {
        Uns.clearUnsDebugForTest();
    }

    @Test
    public void testRehash() throws IOException
    {
        try (OHCache<byte[], byte[]> cache = OHCacheBuilder.<byte[], byte[]>newBuilder()
                                                            .keySerializer(new ByteArraySerializer())
                                                            .valueSerializer(new ByteArraySerializer())
                                                            .hashTableSize(64)
                                                            .segmentCount(4)
                                                            .capacity(512 * 1024 * 1024)
                                                            .fixedValueSize(8)
                                                            .build())
        {
            for (int i = 0; i < 100000; i++)
                cache.put(Longs.toByteArray(i), Longs.toByteArray(i));

            assertTrue(cache.stats().getRehashCount() > 0);

            for (int i = 0; i < 100000; i++)
            {
                byte[] v = cache.get(Longs.toByteArray(i));
                assertEquals(Longs.fromByteArray(v), i);
            }
        }
    }
}
