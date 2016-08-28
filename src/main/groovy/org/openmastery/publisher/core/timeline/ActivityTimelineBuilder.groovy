package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.timeline.ActivityNode
import org.openmastery.publisher.api.timeline.ActivityNodeType
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel

import static org.openmastery.time.TimeConverter.toJodaLocalDateTime

class ActivityTimelineBuilder {

	private List<ActivityNode> activityNodes = []

	public ActivityTimeline build() {
		Collections.sort(activityNodes)

		ActivityTimeline.builder()
				.activityNodes(activityNodes)
				.build()
	}

	public ActivityTimelineBuilder addTimelineSegment(BandTimelineSegment segment) {
		addIdeaFlowBandActivityNodes(segment.ideaFlowBands)
		addTimeBandGroupActivityNodes(segment.timeBandGroups)
		addEventActivityNodes(segment.events)
		this
	}

	private void addEventActivityNodes(List<EventModel> events) {
		for (EventModel event : events) {
			ActivityNode eventNode = createActivityNodeBuilder(event, ActivityNodeType.EVENT)
					.eventComment(event.comment)
					.build()
			activityNodes << eventNode
		}
	}

	private void addTimeBandGroupActivityNodes(List<TimeBandGroupModel> timeBandGroups) {
		for (TimeBandGroupModel timeBandGroup : timeBandGroups) {
			addIdeaFlowBandActivityNodes(timeBandGroup.linkedTimeBands)
		}
	}

	private void addIdeaFlowBandActivityNodes(List<IdeaFlowBandModel> ideaFlowBands) {
		for (IdeaFlowBandModel ideaFlowBand : ideaFlowBands) {
			addStartAndEndActivityBandNodes(ideaFlowBand)
		}
	}

	private void addStartAndEndActivityBandNodes(IdeaFlowBandModel ideaFlowBand) {
		if (ideaFlowBand.type != IdeaFlowStateType.PROGRESS) {
			ActivityNode startNode = createActivityNodeBuilder(ideaFlowBand, ActivityNodeType.BAND)
					.bandComment(ideaFlowBand.startingComment)
					.bandStateType(ideaFlowBand.type)
					.bandStart(true)
					.build()
			activityNodes << startNode

			ActivityNode endNode = ActivityNode.builder()
					.type(ActivityNodeType.BAND)
					.position(toJodaLocalDateTime(ideaFlowBand.end))
					.relativePositionInSeconds(ideaFlowBand.relativePositionInSeconds + ideaFlowBand.duration.seconds)
					.bandComment(ideaFlowBand.endingComent)
					.bandStateType(ideaFlowBand.type)
					.bandStart(false)
					.build()
			activityNodes << endNode
		}

		addIdleActivityNodes(ideaFlowBand.idleBands)
		addIdeaFlowBandActivityNodes(ideaFlowBand.nestedBands)
	}

	private void addIdleActivityNodes(List<IdleTimeBandModel> idleBands) {
		for (IdleTimeBandModel idleTimeBand : idleBands) {
			ActivityNode idleNode = createActivityNodeBuilder(idleTimeBand, ActivityNodeType.EXTERNAL)
					.externalDurationInSeconds(idleTimeBand.duration.seconds)
					.externalIdle(true)
					.build()
			activityNodes << idleNode
		}
	}

	private ActivityNode.ActivityNodeBuilder createActivityNodeBuilder(Positionable positionable, ActivityNodeType type) {
		ActivityNode.builder()
				.type(type)
				.position(toJodaLocalDateTime(positionable.position))
				.relativePositionInSeconds(positionable.relativePositionInSeconds)
	}

}