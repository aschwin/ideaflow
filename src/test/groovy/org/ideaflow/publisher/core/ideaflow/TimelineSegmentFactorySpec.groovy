package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimelineSegment
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TimelineSegmentFactorySpec extends Specification {

	TimelineSegmentFactory factory = new TimelineSegmentFactory()
	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private TimelineSegment generatePrimaryTimeline() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()
		factory.createTimelineSegment(stateList)
	}

	def "SHOULD calculate duration for all TimeBands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 2)
		testSupport.endBandAndAdvanceHours(REWORK, 1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, REWORK, Duration.ofHours(2))
		validator.assertTimeBand(segment.ideaFlowBands, 2, PROGRESS, Duration.ofHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(4)
	}

	def "WHEN IdeaFlowStates are nested SHOULD create nested TimeBands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 2)
		testSupport.endBandAndAdvanceHours(CONFLICT, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 3)
		testSupport.endBandAndAdvanceHours(CONFLICT, 1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()


		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, REWORK, Duration.ofHours(8))
		List<IdeaFlowBand> nestedBands = segment.ideaFlowBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, CONFLICT, Duration.ofHours(2))
		validator.assertNestedTimeBand(nestedBands, 1, CONFLICT, Duration.ofHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(9)
	}

	def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
		given:
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.startBandAndAdvanceHours(LEARNING, 3)
		testSupport.startBandAndAdvanceHours(REWORK, 2)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		List groupedIdeaFlowBands = segment.ideaFlowBandGroups[0].linkedIdeaFlowBands
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 1, LEARNING, Duration.ofHours(3))
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 2, REWORK, Duration.ofHours(2))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(7)
	}

	def "WHEN IdeaFlowStates are linked AND first state has nested conflicts SHOULD create TimeBandGroup including all bands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 2)
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.endBandAndAdvanceHours(CONFLICT, 3)
		testSupport.startBandAndAdvanceHours(LEARNING, 4)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		List linkedIdeaFlowBands = segment.ideaFlowBandGroups[0].linkedIdeaFlowBands
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 0, REWORK, Duration.ofHours(6))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 1, LEARNING, Duration.ofHours(4))
		List nestedIdeaFlowBands = segment.ideaFlowBandGroups[0].linkedIdeaFlowBands[0].nestedBands
		validator.assertNestedTimeBand(nestedIdeaFlowBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(11)
	}

	def "WHEN conflict is unnested SHOULD be considered linked AND previous duration should be reduced by the band overlap"() {
		given:

		//conflict <- learning <-rework <- unnested conflict (rework ends after conflict start) <- learning
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(REWORK, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 3) //nested
		testSupport.endBandAndAdvanceHours(REWORK, 4) //unnest the conflict so it's linkable
		testSupport.startBandAndAdvanceHours(LEARNING, 5)
		testSupport.endBandAndAdvanceHours(LEARNING, 2) //finish the group

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		List linkedIdeaFlowBands = segment.ideaFlowBandGroups[0].linkedIdeaFlowBands
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 1, LEARNING, Duration.ofHours(2))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 2, REWORK, Duration.ofHours(1))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 3, CONFLICT, Duration.ofHours(7))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 4, LEARNING, Duration.ofHours(5))
		assert segment.duration == Duration.ofHours(19)
	}

}