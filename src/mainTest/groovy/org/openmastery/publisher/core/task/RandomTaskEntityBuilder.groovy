package org.openmastery.publisher.core.task

import static org.openmastery.publisher.ARandom.aRandom

class RandomTaskEntityBuilder extends TaskEntity.TaskEntityBuilder {

	RandomTaskEntityBuilder() {
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.name(aRandom.text(10))
				.description(aRandom.optionalText(50))
	}

}
