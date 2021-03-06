package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.Interval;
import org.openmastery.publisher.api.Positionable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowBand implements Positionable, Interval {

	@JsonIgnore //will these ever be persistent?
	private long id;

	private String fullPath;

	@JsonIgnore //should we be populating taskId on everything or does this even matter?
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;
	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	private IdeaFlowStateType type;

	@JsonIgnore //deprecated
	private String startingComment;
	@JsonIgnore //deprecated
	private String endingComent;

	@JsonIgnore
	private List<IdeaFlowBand> nestedBands = new ArrayList<IdeaFlowBand>();

	@JsonIgnore //redundant field
	@Override
	public LocalDateTime getPosition() {
		return start;
	}

	@JsonIgnore //refactor into AbstractRelativeInterval
	public Long getRelativeStart() {
		return relativePositionInSeconds;
	}

	@JsonIgnore //refactor into AbstractRelativeInterval
	public Long getRelativeEnd() {
		return relativePositionInSeconds + durationInSeconds;
	}


	// simplify dozer mapping between this object and IdeaFlowBandModel

	@JsonIgnore
	public Duration getDuration() {
		return Duration.ofSeconds(durationInSeconds);
	}

	@JsonIgnore
	public void setDuration(Duration duration) {
		durationInSeconds = duration.getSeconds();
	}

}


