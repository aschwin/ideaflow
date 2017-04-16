package org.openmastery.storyweb.core

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.storyweb.api.FaqSummary
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class StoryWebServiceSpec extends Specification {

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private StoryWebService storyWebService

	private Long taskId

	def setup() {
		taskId = persistenceService.saveTask(aRandom.taskEntity().build()).id
	}

	def "findFaqsBySearchCriteria SHOULD join event and faq details"() {
		given:
		EventEntity event = aRandom.eventEntity().taskId(taskId).build()
		FaqAnnotationEntity annotation = aRandom.faqAnnotationEntity().taskId(taskId).comment("for #this and #that").build()

		EventEntity savedEvent = persistenceService.saveEvent(event)
		annotation.eventId = savedEvent.id
		persistenceService.saveAnnotation(annotation)

		when:

		List<FaqSummary> faqs = storyWebService.findAllFaqMatchingTags(["this", "othertag"]);
		then:
		assert faqs.size() == 1
		assert faqs.get(0).taskId == event.taskId
		assert faqs.get(0).eventId == savedEvent.id
		assert faqs.get(0).position == event.position
		assert faqs.get(0).faqComment == annotation.comment
		assert faqs.get(0).eventComment == event.comment
		assert faqs.get(0).tags == ["#this", "#that"].toSet()
	}

}
