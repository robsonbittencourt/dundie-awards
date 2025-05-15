package com.ninjaone.dundie_awards.application.dundie.split;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class DundieDeliverySplitRollbackFinished extends ApplicationEvent {

    private final List<Long> chunksIds;

    public DundieDeliverySplitRollbackFinished(Object source, List<Long> chunksIds) {
        super(source);
        this.chunksIds = chunksIds;
    }

    public List<Long> getChunksIds() {
        return chunksIds;
    }
}
