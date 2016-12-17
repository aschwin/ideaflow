/*
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.core.ideaflow.timeline

import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.PositionableComparator
import org.openmastery.publisher.core.activity.*
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.publisher.core.timeline.IdleTimeProcessor
import org.openmastery.publisher.core.timeline.RelativeTimeProcessor

class IdeaFlowTimelineBuilder {

	private Task task

	private List<EventEntity> events
	private List<ModificationActivityEntity> modificationActivities
	private List<ExecutionActivityEntity> executionActivities
	private List<IdleActivityEntity> idleActivities

	IdeaFlowTimelineBuilder task(Task task) {
		this.task = task
		this
	}

	IdeaFlowTimelineBuilder idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleActivities = idleActivities
		this
	}

	IdeaFlowTimelineBuilder events(List<EventEntity> events) {
		this.events = events
		this
	}

	IdeaFlowTimelineBuilder modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = modificationActivities
		this
	}

	IdeaFlowTimeline build() {
		//if bands, collapse idle time within band, if idle time outside of band its chopped
		//translate execution activities to events
		//relative time, implement positionable

		//when no modification activity above threshold, create learning bands
		//when WTF, WTF, AWESOME combo, create conflict band that spans this time


		List<IdeaFlowBandModel> progressBands = generateProgressBands()
		List<ActivityModel> allActivities = []
		allActivities.addAll(toModificationModelList(modificationActivities))
		allActivities.addAll(toExecutionModelList(executionActivities))

		BandTimelineSegment segment = BandTimelineSegment.builder()
			.events(toEventModelList(events))
			.ideaFlowBands(progressBands)
			.activities(allActivities)
			.timeBandGroups([])
			.build()

		if (idleActivities) {
			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
			idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		}

		computeRelativeTime(segment.getAllContentsFlattenedAsPositionableList())

		return convertToIdeaFlowTimeline(segment)
	}

	private IdeaFlowTimeline convertToIdeaFlowTimeline(BandTimelineSegment segment) {

	}

	private void computeRelativeTime(List<Positionable> positionables) {
		RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()
		relativeTimeProcessor.computeRelativeTime(positionables)
	}

	List<IdeaFlowBandModel> generateProgressBands() {
		List<EventModel> sortedTaskActivationEvents = createSortedTaskActivationEventList()
		List<IdeaFlowBandModel> progressBands = []
		IdeaFlowBandModel activeProgressBand = null

		sortedTaskActivationEvents.each { EventModel taskEvent ->
			if (activeProgressBand == null && taskEvent.type == EventType.ACTIVATE) {
				activeProgressBand = IdeaFlowBandModel.builder()
					.type(IdeaFlowStateType.PROGRESS)
					.taskId(taskEvent.id)
					.start(taskEvent.position)
					.nestedBands([])
					.idleBands([])
					.build()
			} else if (activeProgressBand != null && taskEvent.type == EventType.DEACTIVATE) {
					activeProgressBand.end = taskEvent.position
					progressBands.add(activeProgressBand)
					activeProgressBand = null
			} else {
				//eh... messed up state.  Multiple activates, multiple deactivates
				//should trigger a "repair" by looking at raw data and correcting events
			}
		}
		return progressBands
	}

	List<EventModel> createSortedTaskActivationEventList() {
		List<EventEntity> taskActivationEvents = events.findAll { EventEntity event ->
			event.type == EventType.ACTIVATE || event.type == EventType.DEACTIVATE
		}
		List<EventModel> positionableTaskActivationEvents = toEventModelList(taskActivationEvents)
		Collections.sort(positionableTaskActivationEvents, PositionableComparator.INSTANCE);
		return positionableTaskActivationEvents
	}




	private List<EventModel> toEventModelList(List<EventEntity> eventEntityList) {
		if (eventEntityList == null) {
			return []
		}

		eventEntityList.collect { EventEntity eventEntity ->
			new EventModel(eventEntity)
		}
	}

	private List<ModificationActivityModel> toModificationModelList(List<ModificationActivityEntity> modificationActivityEntityList) {
		if (modificationActivityEntityList == null) {
			return []
		}

		modificationActivityEntityList.collect { ModificationActivityEntity activityEntity ->
			new ModificationActivityModel(activityEntity)
		}
	}

	private List<ExecutionActivityModel> toExecutionModelList(List<ExecutionActivityEntity> executionActivityEntityList) {
		if (executionActivityEntityList == null) {
			return []
		}

		executionActivityEntityList.collect { ExecutionActivityEntity activityEntity ->
			new ExecutionActivityModel(activityEntity)
		}
	}

}
