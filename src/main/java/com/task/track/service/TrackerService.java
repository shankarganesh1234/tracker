package com.task.track.service;

import com.apple.foundationdb.record.RecordCursor;
import com.apple.foundationdb.record.RecordMetaData;
import com.apple.foundationdb.record.RecordMetaDataBuilder;
import com.apple.foundationdb.record.metadata.Key;
import com.apple.foundationdb.record.provider.foundationdb.FDBQueriedRecord;
import com.apple.foundationdb.record.provider.foundationdb.FDBRecordContext;
import com.apple.foundationdb.record.provider.foundationdb.FDBRecordStore;
import com.apple.foundationdb.record.provider.foundationdb.FDBStoredRecord;
import com.apple.foundationdb.record.query.RecordQuery;
import com.apple.foundationdb.record.query.expressions.Query;
import com.apple.foundationdb.tuple.Tuple;
import com.google.protobuf.Message;
import com.task.TaskProto;
import com.task.track.configuration.FDBConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.task.track.constants.Constants.ID_FIELD;
import static com.task.track.constants.Constants.RECORD_TYPE;

@Service
public class TrackerService {

    @Autowired
    private FDBConfiguration fdbConfiguration;

    private Function<FDBRecordContext, FDBRecordStore> recordStoreProvider;

    @PostConstruct
    public void init() {

        RecordMetaDataBuilder metaDataBuilder = RecordMetaData.newBuilder()
                .setRecords(TaskProto.getDescriptor());

        metaDataBuilder.getRecordType(RECORD_TYPE)
                .setPrimaryKey(Key.Expressions.field(ID_FIELD));

        recordStoreProvider = context -> FDBRecordStore.newBuilder()
                .setMetaDataProvider(metaDataBuilder.build())
                .setContext(context)
                .setKeySpacePath(fdbConfiguration.getPath())
                .createOrOpen();
    }

    public TaskProto.Task getTaskById(String id) {

        FDBStoredRecord<Message> storedRecord = fdbConfiguration.getDb().run(context -> {
            FDBRecordStore recordStore = recordStoreProvider.apply(context);
            return recordStore.loadRecord(Tuple.from(id));
        });

        if(storedRecord != null) {
           TaskProto.Task record = TaskProto.Task.newBuilder().mergeFrom(storedRecord.getRecord()).build();
           return record;
        }
        return null;
    }

    public List<TaskProto.Task> getTasksByKey(String key) {
        RecordQuery query = RecordQuery.newBuilder()
                .setRecordType(RECORD_TYPE)
                .setFilter(Query.field("key").equalsValue(key))
                .build();

        List<TaskProto.Task> tasks = fdbConfiguration.getDb().run(context -> {
            FDBRecordStore recordStore = recordStoreProvider.apply(context);
            RecordCursor<FDBQueriedRecord<Message>> cursor = recordStore.executeQuery(query);

            return cursor
                    .filter(Objects::nonNull)
                    .map(queriedRecord -> TaskProto.Task.newBuilder()
                            .mergeFrom(queriedRecord.getRecord()).build())
                    .asList().join();
        });
       return tasks;
    }

    public TaskProto.Task addTask(TaskProto.Task task) {
        fdbConfiguration.getDb().run(context -> {
            FDBRecordStore recordStore = recordStoreProvider.apply(context);
            recordStore.saveRecord(task);
            return null;
        });
        return task;
    }

    public TaskProto.Task updateTask(TaskProto.Task task) {

        FDBStoredRecord<Message> storedRecord = fdbConfiguration.getDb().run(context -> {
            FDBRecordStore recordStore = recordStoreProvider.apply(context);
            return recordStore.loadRecord(Tuple.from(task.getId()));
        });

        if(storedRecord != null && storedRecord.getRecord() != null) {
            TaskProto.Task record = TaskProto.Task.newBuilder()
                    .mergeFrom(storedRecord.getRecord())
                    .setDateTime(task.getDateTime())
                    .setName(task.getName())
                    .setTag(task.getTag())
                    .setStatus(task.getStatus())
                    .build();

            addTask(record);
        }
        return task;
    }

    public Boolean deleteTask(String id) {
        Boolean deleteResult = fdbConfiguration.getDb().run(context -> {
            FDBRecordStore recordStore = recordStoreProvider.apply(context);
            return recordStore.deleteRecord(Tuple.from(id));
        });
        return deleteResult;
    }
}
