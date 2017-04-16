/*
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.mapper

import org.dozer.CustomConverter
import org.dozer.MappingException

import java.time.Duration



class LongDurationConverter implements CustomConverter {

	@Override
	Object convert(Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {
		if (source == null) {
			return null
		}

		if (destinationClass == Long.class && sourceClass == Duration.class) {
			return ((Duration) source).seconds
		} else if (destinationClass == Duration.class && sourceClass == Long.class) {
			return Duration.ofSeconds((Long) source)
		} else {
			throw new MappingException("Converter LongDurationConverter used incorrectly, destination=${destination}, source=${source}")
		}
	}

}
