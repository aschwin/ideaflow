package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.core.activity.EditorActivityEntity;
import org.ideaflow.publisher.core.activity.EditorActivityRepository;
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity;
import org.ideaflow.publisher.core.activity.IdleTimeBandRepository;
import org.ideaflow.publisher.core.event.EventEntity;
import org.ideaflow.publisher.core.event.EventRepository;
import org.ideaflow.publisher.core.task.TaskEntity;
import org.ideaflow.publisher.core.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdeaFlowRelationalPersistenceService implements IdeaFlowPersistenceService {

	private Map<Long, IdeaFlowStateEntity> activeStateMap = new HashMap<>();
	private Map<Long, IdeaFlowStateEntity> containingStateMap = new HashMap<>();

	@Autowired
	private IdeaFlowStateRepository ideaFlowStateRepository;
	@Autowired
	private IdleTimeBandRepository idleTimeBandRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EditorActivityRepository editorActivityRepository;
	@Autowired
	private TaskRepository taskRepository;

	@Override
	public IdeaFlowStateEntity getActiveState(long taskId) {
		return activeStateMap.get(taskId);
	}

	@Override
	public IdeaFlowStateEntity getContainingState(long taskId) {
		return containingStateMap.get(taskId);
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		return ideaFlowStateRepository.findByTaskId(taskId);
	}

	@Override
	public List<IdleTimeBandEntity> getIdleTimeBandList(long taskId) {
		return idleTimeBandRepository.findByTaskId(taskId);
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		return eventRepository.findByTaskId(taskId);
	}

	@Override
	public List<EditorActivityEntity> getEditorActivityList(long taskId) {
		return editorActivityRepository.findByTaskId(taskId);
	}

	@Override
	public LocalDateTime getMostRecentActivityEnd(long taskId) {
//		editorActivityRepository
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
		activeStateMap.put(activeState.getTaskId(), activeState);
		if (containingState != null) {
			containingStateMap.put(containingState.getTaskId(), containingState);
		} else {
			containingStateMap.remove(activeState.getTaskId());
		}
	}

	@Override
	@Transactional
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		ideaFlowStateRepository.save(stateToSave);
		saveActiveState(activeState);
	}

	@Override
	public IdleTimeBandEntity saveIdleActivity(IdleTimeBandEntity idleActivity) {
		return idleTimeBandRepository.save(idleActivity);
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		return eventRepository.save(event);
	}

	@Override
	public EditorActivityEntity saveEditorActivity(EditorActivityEntity activity) {
		return editorActivityRepository.save(activity);
	}

	@Override
	public TaskEntity saveTask(TaskEntity task) {
		return taskRepository.save(task);
	}

	@Override
	public TaskEntity findTaskWithName(String taskName) {
		return taskRepository.findByName(taskName);
	}

}
