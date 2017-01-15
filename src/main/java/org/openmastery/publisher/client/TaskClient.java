package org.openmastery.publisher.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import com.bancvue.rest.client.crud.GenericTypeFactory;
import org.openmastery.publisher.api.PagedResult;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;

import javax.ws.rs.core.GenericType;
import java.lang.reflect.Field;
import java.util.List;

public class TaskClient extends IdeaFlowClient<Object, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH + ResourcePaths.TASK_PATH, Object.class);
	}

	public Task createTask(String taskName, String description, String project) {
		NewTask task = NewTask.builder()
				.name(taskName)
				.description(description)
				.project(project)
				.build();
		return (Task)crudClientRequest.entity(Task.class).createWithPost(task);
	}

	public Task findTaskWithName(String taskName) {
		return (Task)crudClientRequest.path(ResourcePaths.TASK_NAME_PATH)
				.path(taskName).entity(Task.class).find();
	}


	public PagedResult<Task> findRecentTasks(Integer pageNumber, Integer perPage) {
		CrudClientRequest request = getUntypedCrudClientRequest()
                .queryParam("page_number", pageNumber)
                .queryParam("per_page", perPage);


		return (PagedResult<Task>) withPagedResultType(request).find();
	}

	public PagedResult<Task> findRecentTasksMatchingTags(List<String> tags, Integer pageNumber, Integer perPage) {
		CrudClientRequest request = getUntypedCrudClientRequest()
				.queryParam("page_number", pageNumber)
				.queryParam("per_page", perPage);

		for (String tag : tags) {
			request = request.queryParam("tag", tag);
		}

		return (PagedResult<Task>) withPagedResultType(request).find();
	}


	private static final GenericTypeFactory GENERIC_TYPE_FACTORY = GenericTypeFactory.getInstance();

	private CrudClientRequest withPagedResultType(CrudClientRequest request) {
		GenericType<PagedResult<Task>> entityType = GENERIC_TYPE_FACTORY.createGenericType(PagedResult.class, Task.class);
		try {
			Field entityField = request.getClass().getDeclaredField("entity");
			entityField.setAccessible(true);
			entityField.set(request, entityType);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return request;
	}
}
