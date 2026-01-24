package com.carrotguy69.cxyz.models.config.channel.utils;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;

public class FunctionalChannel {

    private final BaseChannel baseChannel;
    private final ChannelFunction channelFunction;

    public FunctionalChannel(BaseChannel channel, ChannelFunction fxn) {
        this.baseChannel = channel;
        this.channelFunction = fxn;
    }

    public BaseChannel getBaseChannel() {
        return this.baseChannel;
    }

    public ChannelFunction getChannelFunction() {
        return this.channelFunction;
    }

    public String toString() {
        return "FunctionalChannel{" +
                "BaseChannel(name)=" + this.baseChannel.getName() + ", " +
                "ChannelFunction=" + this.channelFunction.name() +
                "}";
    }

}
