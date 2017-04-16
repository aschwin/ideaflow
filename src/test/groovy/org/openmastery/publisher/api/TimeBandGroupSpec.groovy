package org.openmastery.publisher.api

import org.openmastery.publisher.core.timeline.TimeBandGroupModel
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import java.time.LocalDateTime

public class TimeBandGroupSpec extends Specification implements TimeBandTestSupport {

	private MockTimeService timeService = new MockTimeService()

	private LocalDateTime hourZero = timeService.now()
	private LocalDateTime hourOne = timeService.plusHour().now()
	private LocalDateTime hourTwo = timeService.plusHour().now()
	private LocalDateTime hourThree = timeService.plusHour().now()
	private LocalDateTime hourFour = timeService.plusHour().now()
	private LocalDateTime hourFive = timeService.plusHour().now()
	private LocalDateTime hourSix = timeService.plusHour().now()

	def "splitAndReturn should return null if position is on or outside timeband range AND exclusive direction"() {
		given:
		TimeBandGroupModel band = createGroup(hourOne, hourTwo)

		expect:
		assert band.splitAndReturnLeftSide(hourZero) == null
		assert band.splitAndReturnLeftSide(hourOne) == null

		and:
		assert band.splitAndReturnRightSide(hourTwo) == null
		assert band.splitAndReturnRightSide(hourThree) == null
	}

	def "splitAndReturn should return self WHEN position is on or outside timeband range AND inclusive direction"() {
		given:
		TimeBandGroupModel band = createGroup(hourOne, hourTwo)

		expect:
		assert band.splitAndReturnLeftSide(hourTwo).is(band)
		assert band.splitAndReturnLeftSide(hourThree).is(band)

		and:
		assert band.splitAndReturnRightSide(hourZero).is(band)
		assert band.splitAndReturnRightSide(hourOne).is(band)
	}

	def "spiltAndReturn should split timeband WHEN position is within timeband range"() {
		given:
		TimeBandGroupModel band = createGroup(hourOne, hourThree)

		when:
		TimeBandGroupModel leftSide = band.splitAndReturnLeftSide(hourTwo)

		then:
		assertStartAndEnd(leftSide, hourOne, hourTwo)

		when:
		TimeBandGroupModel rightSide = band.splitAndReturnRightSide(hourTwo)

		then:
		assertStartAndEnd(rightSide, hourTwo, hourThree)
	}

	def "splitAndReturn should split linked bands"() {
		given:
		TimeBandGroupModel outerBand = createGroup(
				createBand(hourTwo, hourThree),
				createBand(hourFour, hourFive),
				createBand(hourFive, hourSix)
		)
		LocalDateTime hourFourOneHalf = hourFour.plusMinutes(30)

		when:
		TimeBandGroupModel leftSide = outerBand.splitAndReturnLeftSide(hourFourOneHalf)

		then:
		assertStartAndEnd(leftSide, hourTwo, hourFourOneHalf)
		assertStartAndEnd(leftSide.linkedTimeBands[0], hourTwo, hourThree)
		assertStartAndEnd(leftSide.linkedTimeBands[1], hourFour, hourFourOneHalf)

		when:
		TimeBandGroupModel rightSide = outerBand.splitAndReturnRightSide(hourFour.plusMinutes(30))

		then:
		assertStartAndEnd(rightSide, hourFourOneHalf, hourSix)
		assertStartAndEnd(rightSide.linkedTimeBands[0], hourFourOneHalf, hourFive)
		assertStartAndEnd(rightSide.linkedTimeBands[1], hourFive, hourSix)
	}

}