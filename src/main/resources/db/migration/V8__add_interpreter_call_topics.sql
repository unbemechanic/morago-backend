CREATE TABLE interpreter_call_topics(
    interpreter_profile_id BIGINT NOT NULL,
    call_topic_id BIGINT NOT NULL,

    PRIMARY KEY (interpreter_profile_id, call_topic_id),

    CONSTRAINT fk_interpreter_profile_topics_interpreter
                                    FOREIGN KEY (interpreter_profile_id)
                                    REFERENCES interpreter_profile(id)
                                    ON DELETE CASCADE,
    CONSTRAINT fk_interpreter_call_topics_topic
                                    FOREIGN KEY (call_topic_id)
                                    REFERENCES call_topics(id)
                                    ON DELETE CASCADE
);