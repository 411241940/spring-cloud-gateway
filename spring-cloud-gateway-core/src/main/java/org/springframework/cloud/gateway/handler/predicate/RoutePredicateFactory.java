/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.gateway.handler.predicate;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.support.Configurable;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.cloud.gateway.support.ShortcutConfigurable;
import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.toAsyncPredicate;

/**
 * @author Spencer Gibb
 */
@FunctionalInterface
public interface RoutePredicateFactory<C> extends ShortcutConfigurable, Configurable<C> {
	String PATTERN_KEY = "pattern";

	// useful for javadsl
	default Predicate<ServerWebExchange> apply(Consumer<C> consumer) {
		C config = newConfig();
		consumer.accept(config);
		beforeApply(config);
		return apply(config);
	}

	default AsyncPredicate<ServerWebExchange> applyAsync(Consumer<C> consumer) {
		C config = newConfig();
		consumer.accept(config);
		beforeApply(config);
		return applyAsync(config);
	}

	default Class<C> getConfigClass() {
		throw new UnsupportedOperationException("getConfigClass() not implemented");
	}

	// 创建一个泛型的 config 实例，由具体的实现类来完成
	@Override
	default C newConfig() {
		throw new UnsupportedOperationException("newConfig() not implemented");
	}

	default void beforeApply(C config) {}

	// 生产 Predicate
	Predicate<ServerWebExchange> apply(C config);

	// 生产 非阻塞 AsyncPredicate
	default AsyncPredicate<ServerWebExchange> applyAsync(C config) {
		return toAsyncPredicate(apply(config));
	}

	// 工厂的 name 为：类名去除掉后面的 RoutePredicateFactory (RoutePredicateFactory前面的单词)
	default String name() {
		return NameUtils.normalizeRoutePredicateName(getClass());
	}

}
