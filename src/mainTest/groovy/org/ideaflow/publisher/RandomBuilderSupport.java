package org.ideaflow.publisher;

import org.ideaflow.publisher.core.activity.RandomEditorActivityEntityBuilder;
import org.ideaflow.publisher.core.activity.RandomIdleTimeBandEntityBuilder;
import org.ideaflow.publisher.core.event.RandomEventEntityBuilder;
import org.ideaflow.publisher.core.ideaflow.RandomIdeaFlowStateEntityBuilder;
import org.ideaflow.publisher.core.task.RandomTaskEntityBuilder;

public class RandomBuilderSupport {

	public RandomEditorActivityEntityBuilder editorActivityEntity() {
		return new RandomEditorActivityEntityBuilder();
	}

	public RandomIdleTimeBandEntityBuilder idleTimeBandEntity() {
		return new RandomIdleTimeBandEntityBuilder();
	}

	public RandomEventEntityBuilder eventEntity() {
		return new RandomEventEntityBuilder();
	}

	public RandomIdeaFlowStateEntityBuilder ideaFlowStateEntity() {
		return new RandomIdeaFlowStateEntityBuilder();
	}

	public RandomTaskEntityBuilder taskEntity() {
		return new RandomTaskEntityBuilder();
	}

}