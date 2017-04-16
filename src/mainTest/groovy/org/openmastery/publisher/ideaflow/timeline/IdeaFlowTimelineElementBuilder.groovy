package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.api.SharedTags
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.time.MockTimeService
import org.openmastery.time.TimeConverter

import java.time.Duration
import java.time.LocalDateTime

class IdeaFlowTimelineElementBuilder {

	private MockTimeService timeService
	private long eventId = 0
	private long idleTimeBandId = 0
	List<Event> eventList = []
	List<ModificationActivity> modificationActivityList = []
	List<IdleTimeBandModel> idleTimeBands = []
	List<ExecutionEvent> executionEventList = []

	Duration idleDuration = Duration.ofSeconds(0)
	LocalDateTime deactivationTime
	LocalDateTime startTime

	IdeaFlowTimelineElementBuilder() {
		this(new MockTimeService())
	}

	IdeaFlowTimelineElementBuilder(MockTimeService timeService) {
		this.timeService = timeService
		startTime = timeService.now()
	}

	IdeaFlowTimelineElementBuilder advanceDays(int days) {
		timeService.plusDays(days)
		this
	}

	IdeaFlowTimelineElementBuilder advanceHours(int hours) {
		timeService.plusHours(hours)
		this
	}

	IdeaFlowTimelineElementBuilder advanceMinutes(int minutes) {
		timeService.plusMinutes(minutes)
		this
	}

	IdeaFlowTimelineElementBuilder idleDays(int days) {
		idleDuration = idleDuration.plus(Duration.ofDays(days))
		idleTimeBands << IdleTimeBandModel.builder()
				.id(idleTimeBandId++)
				.start(timeService.now())
				.end(timeService.plusDays(days).now())
				.build()
		this
	}

	IdeaFlowTimelineElementBuilder idleHours(int hours) {
		idleDuration = idleDuration.plus(Duration.ofHours(hours))
		idleTimeBands << IdleTimeBandModel.builder()
				.id(idleTimeBandId++)
				.start(timeService.now())
				.end(timeService.plusHours(hours).now())
				.build()
		this
	}

	IdeaFlowTimelineElementBuilder idleMinutes(int minutes) {
		idleDuration = idleDuration.plus(Duration.ofMinutes(minutes))
		idleTimeBands << IdleTimeBandModel.builder()
				.id(idleTimeBandId++)
				.start(timeService.now())
				.end(timeService.plusMinutes(minutes).now())
				.build()
		this
	}

	IdeaFlowTimelineElementBuilder readCodeAndAdvance(int minutes) {
		for (int i = 0; i < minutes; i++) {
			ModificationActivity modificationActivity = new ModificationActivity()
			modificationActivity.position = timeService.now().plusMinutes(i)
			modificationActivity.durationInSeconds = 60
			modificationActivity.modificationCount = 0
			modificationActivityList << modificationActivity
		}
		advanceMinutes(minutes)
	}

	IdeaFlowTimelineElementBuilder modifyCodeAndAdvance(int minutes) {
		for (int i = 0; i < minutes; i++) {
			ModificationActivity modificationActivity = new ModificationActivity()
			modificationActivity.position = timeService.now().plusMinutes(i)
			modificationActivity.durationInSeconds = 60
			modificationActivity.modificationCount = 50
			modificationActivityList << modificationActivity
		}
		advanceMinutes(minutes)
	}

	IdeaFlowTimelineElementBuilder executeCode() {
		ExecutionEvent event = ExecutionEvent.builder()
				.failed(false)
				.debug(false)
				.executionTaskType("JUnit")
				.processName("MyTestClass")
				.build()
		event.setStart(timeService.now())
		event.relativePositionInSeconds = computeRelativePositionInSeconds()
		event.setDurationInSeconds(1)

		executionEventList << event
		return this
	}

	private void addEvent(EventType eventType) {
		addEvent(eventType, null)
	}

	private void addEvent(EventType eventType, String comment) {
		Event event = new Event()
		event.id = eventId++
		event.position = timeService.now()
		event.type = eventType
		event.description = comment
		event.relativePositionInSeconds = computeRelativePositionInSeconds()
		eventList << event
	}

	private Long computeRelativePositionInSeconds() {
		Duration.between(startTime, timeService.now()).minus(idleDuration).seconds
	}

	IdeaFlowTimelineElementBuilder activate() {
		if (deactivationTime != null) {
			idleDuration.plus(Duration.between(deactivationTime, timeService.now()))
		}
		addEvent(EventType.ACTIVATE)
		this
	}

	IdeaFlowTimelineElementBuilder deactivate() {
		deactivationTime = timeService.now()
		addEvent(EventType.DEACTIVATE)
		this
	}

	IdeaFlowTimelineElementBuilder wtf() {
		addEvent(EventType.WTF)
		this
	}

	IdeaFlowTimelineElementBuilder distraction() {
		addEvent(EventType.DISTRACTION)
		this
	}

	IdeaFlowTimelineElementBuilder wtf(String comment) {
		addEvent(EventType.WTF, comment)
		this
	}

	IdeaFlowTimelineElementBuilder awesome() {
		addEvent(EventType.AWESOME)
		this
	}

	IdeaFlowTimelineElementBuilder troubleshootingJourneyComplete() {
		addEvent(EventType.AWESOME, "done ${SharedTags.RESOLVE_TROUBLESHOOTING_JOURNEY}")
		this
	}

	IdeaFlowTimelineElementBuilder subtask() {
		addEvent(EventType.SUBTASK)
		this
	}

}
