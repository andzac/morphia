/*
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.morphia;

import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import dev.morphia.internal.SessionConfigurable;

/**
 * Options related to insertion of documents into MongoDB.  The setter methods return {@code this} so that a chaining style can be used.
 *
 * @since 1.3
 */
public class InsertOneOptions implements SessionConfigurable<InsertOneOptions> {
    private com.mongodb.client.model.InsertOneOptions options = new com.mongodb.client.model.InsertOneOptions();
    private WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;
    private ClientSession clientSession;

    /**
     * Creates a new options wrapper
     */
    public InsertOneOptions() {
    }

    /**
     * @param that the options to copy
     * @morphia.internal
     * @since 2.0
     */
    public InsertOneOptions(final InsertOneOptions that) {
        this.options = that.options;
        this.writeConcern = that.writeConcern;
        this.clientSession = that.clientSession;
    }

    @Override
    public InsertOneOptions clientSession(final ClientSession clientSession) {
        this.clientSession = clientSession;
        return this;
    }

    @Override
    public ClientSession clientSession() {
        return clientSession;
    }

    /**
     * Set the write concern to use for the insert.
     *
     * @param writeConcern the write concern
     * @return this
     */
    public InsertOneOptions writeConcern(final WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
        return this;
    }

    /**
     * The write concern to use for the insertion.  By default the write concern configured for the MongoCollection instance will be used.
     *
     * @return the write concern, or null if the default will be used.
     */
    public WriteConcern writeConcern() {
        return writeConcern;
    }

    /**
     * The write concern to use for the insertion.  By default the write concern configured for the MongoCollection instance will be used.
     *
     * @return the write concern, or null if the default will be used.
     * @deprecated use {@link #writeConcern()} instead
     */
    @Deprecated(since = "2.0", forRemoval = true)
    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    /**
     * Gets the the bypass document level validation flag
     *
     * @return the bypass document level validation flag
     */
    public Boolean getBypassDocumentValidation() {
        return options.getBypassDocumentValidation();
    }

    /**
     * Sets whether to bypass document validation.
     *
     * @param bypassDocumentValidation whether to bypass document validation, or null if unspecified
     * @return this
     * @mongodb.server.release 3.2
     */
    public InsertOneOptions bypassDocumentValidation(final Boolean bypassDocumentValidation) {
        options.bypassDocumentValidation(bypassDocumentValidation);
        return this;
    }

    /**
     * @return the driver version of the options
     */
    public com.mongodb.client.model.InsertOneOptions getOptions() {
        return options;
    }

    /**
     * Applies the configured WriteConcern to the collection
     *
     * @param collection the collection to update
     * @param <T>        the collection type
     * @return the updated collection
     */
    public <T> MongoCollection<T> apply(final MongoCollection<T> collection) {
        return writeConcern != null ? collection.withWriteConcern(writeConcern) : collection;
    }
}
