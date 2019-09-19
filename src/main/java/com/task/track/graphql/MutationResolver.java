package com.task.track.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.task.TaskProto;
import com.task.track.service.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class MutationResolver implements GraphQLMutationResolver {

    @Autowired
    private TrackerService trackerService;

    public TaskProto.Task add(String key, String name, String dateTime, String tag) {
        TaskProto.Task task = TaskProto.Task.newBuilder().setId(UUID.randomUUID().toString()).setKey(key).setName(name).setDateTime(Long.valueOf(dateTime)).setTag(tag).setStatus(TaskProto.Status.CREATED.name()).build();
        return trackerService.addTask(task);
    }

}
