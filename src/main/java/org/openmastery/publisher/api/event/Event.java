package org.openmastery.publisher.api.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openmastery.publisher.api.AbstractPositionable;
import org.openmastery.time.TimeConverter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class Event extends AbstractPositionable {

	private String fullPath;
	private String comment;
	private EventType type;

	@JsonIgnore
	private Long id;


}
