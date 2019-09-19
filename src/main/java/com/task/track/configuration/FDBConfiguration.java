package com.task.track.configuration;

import com.apple.foundationdb.FDB;
import com.apple.foundationdb.record.provider.foundationdb.FDBDatabase;
import com.apple.foundationdb.record.provider.foundationdb.FDBDatabaseFactory;
import com.apple.foundationdb.record.provider.foundationdb.keyspace.KeySpace;
import com.apple.foundationdb.record.provider.foundationdb.keyspace.KeySpaceDirectory;
import com.apple.foundationdb.record.provider.foundationdb.keyspace.KeySpacePath;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.task.track.constants.Constants.KEYSPACE;

@Configuration
public class FDBConfiguration {

    private FDBDatabase db;
    private KeySpace keySpace;
    private KeySpacePath path;

    @PostConstruct
    public void init() {
        db = FDBDatabaseFactory.instance().getDatabase("/Users/shankarganesh/Downloads/fdb.cluster");
        //db = FDBDatabaseFactory.instance().getDatabase();
        keySpace = new KeySpace(new KeySpaceDirectory(KEYSPACE, KeySpaceDirectory.KeyType.STRING, KEYSPACE));
        path = keySpace.path(KEYSPACE);
    }

    public FDBDatabase getDb() {
        return db;
    }

    public void setDb(FDBDatabase db) {
        this.db = db;
    }

    public KeySpacePath getPath() {
        return path;
    }

    public void setPath(KeySpacePath path) {
        this.path = path;
    }

    public KeySpace getKeySpace() {
        return keySpace;
    }

    public void setKeySpace(KeySpace keySpace) {
        this.keySpace = keySpace;
    }
}
