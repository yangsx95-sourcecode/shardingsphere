/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.spi.type.ordered.cache;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ordered services cache.
 * 缓存类，缓存所有的SPI实例，并且给他们提供类型type的支持
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderedServicesCache {

    /**
     * 软引用 SoftReference，当Java程序内存不足，即OOM即将发生时，堆内存中虚引用指向的对象都会被gc二次回收，以防发生OOM异常。通常用于修饰缓存
     * 虚引用 WeakReference，当发生GC时，就会被回收
     *
     * 缓存对象是一个Map
     *   key是SPI接口Key(Class + types)，Class 是SPI接口的Class，而types是对类型的支持，一个实例可以有多个type
     *   value是要缓存的SPI实例
     *
     * 其中Map对象是线程安全的，初始容量为128，加载因子为1  （该值是一个百分比，0-1之间的浮点数，代表当前容量到到了这个百分比，进行扩容）
     */
    private static volatile SoftReference<Map<Key, Map<?, ?>>> cache = new SoftReference<>(new ConcurrentHashMap<>(128, 1F));
    
    /**
     * Find cached services.
     * 
     * @param spiClass SPI class
     * @param types types
     * @return cached services
     */
    public static Optional<Map<?, ?>> findCachedServices(final Class<?> spiClass, final Collection<?> types) {
        return Optional.ofNullable(cache.get()).map(optional -> optional.get(new Key(spiClass, types)));
    }
    
    /**
     * Cache services.
     * 
     * @param spiClass SPI class
     * @param types types
     * @param services services to be cached
     */
    public static void cacheServices(final Class<?> spiClass, final Collection<?> types, final Map<?, ?> services) {
        Map<Key, Map<?, ?>> cache = OrderedServicesCache.cache.get();
        if (null == cache) {
            synchronized (OrderedServicesCache.class) {
                cache = OrderedServicesCache.cache.get();
                if (null == cache) {
                    cache = new ConcurrentHashMap<>(128, 1F);
                    OrderedServicesCache.cache = new SoftReference<>(cache);
                }
            }
        }
        cache.put(new Key(spiClass, types), services);
    }
    
    /**
     * Clear cache.
     */
    public static void clearCache() {
        Map<Key, Map<?, ?>> cache = OrderedServicesCache.cache.get();
        if (null != cache) {
            cache.clear();
        }
    }
    
    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static final class Key {
        
        private final Class<?> clazz;
        
        private final Collection<?> types;
    }
}
