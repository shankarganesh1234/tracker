package com.task.track.graphql;

import com.task.TaskProto;
import com.task.track.service.TrackerService;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GraphQLDataFetchers {

    @Autowired
    private TrackerService trackerService;

    public DataFetcher addTask() {
        return dataFetchingEnvironment -> {
            String key = dataFetchingEnvironment.getArgument("key");
            String name = dataFetchingEnvironment.getArgument("name");
            String dateTime = dataFetchingEnvironment.getArgument("dateTime");
            String tag = dataFetchingEnvironment.getArgument("tag");

            TaskProto.Task task = TaskProto.Task.newBuilder().setStatus(TaskProto.Status.CREATED.name())
                    .setTag(tag)
                    .setDateTime(Long.valueOf(dateTime))
                    .setName(name)
                    .setKey(key)
                    .setId(UUID.randomUUID().toString())
                    .build();

            return trackerService.addTask(task);
        };
    }

    public DataFetcher getTaskById() {
        return dataFetchingEnvironment -> {
            String taskId = dataFetchingEnvironment.getArgument("id");
            return trackerService.getTaskById(taskId);
        };
    }

    public DataFetcher getTasksByKey() {
        return dataFetchingEnvironment -> {
            String key = dataFetchingEnvironment.getArgument("key");
            return trackerService.getTasksByKey(key);
        };
    }

    public DataFetcher updateTask() {
        return dataFetchingEnvironment -> {
            String key = dataFetchingEnvironment.getArgument("key");
            String name = dataFetchingEnvironment.getArgument("name");
            String dateTime = dataFetchingEnvironment.getArgument("dateTime");
            String tag = dataFetchingEnvironment.getArgument("tag");
            String id = dataFetchingEnvironment.getArgument("id");
            String status = dataFetchingEnvironment.getArgument("status");

            TaskProto.Task task = TaskProto.Task.newBuilder()
                    .setTag(tag)
                    .setDateTime(Long.valueOf(dateTime))
                    .setName(name)
                    .setKey(key)
                    .setId(id)
                    .setStatus(status)
                    .build();

            return trackerService.updateTask(task);
        };
    }

    public DataFetcher deleteTask() {
        return dataFetchingEnvironment -> {
            String id = dataFetchingEnvironment.getArgument("id");
            trackerService.deleteTask(id);
            TaskProto.Task task = TaskProto.Task.newBuilder()
                    .setStatus(TaskProto.Status.DELETED.name())
                    .setId("-1")
                    .setName("deleted")
                    .setTag("deleted")
                    .setDateTime(0L)
                    .setKey("deleted")
                    .build();
            return task;
        };
    }
}
